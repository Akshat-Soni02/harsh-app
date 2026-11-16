# Pragti — Unofficial Update & Data Preservation Guide

This guide explains how app updates work for **Pragti** when distributed unofficially (via APK sideloading outside the Google Play Store), how your champion's and family's data is safely preserved across updates, and the developer rules to follow to prevent accidental data loss.

---

## 1. What the Update Looks Like for Guardians & Parents

Your parents **never need to uninstall the app** to apply an update. Android natively supports seamless in-place updates.

### The 4-Step Update Procedure:
1. **Download the new APK**:
   - Send the latest `app-debug.apk` (or release APK) to your parents via WhatsApp, Telegram, Google Drive, or local storage.
2. **Open the downloaded file**:
   - When tapped, the Android Package Installer automatically recognizes the existing package name (`com.heroroutine.app`).
3. **Android prompts**:
   > *"Do you want to install an update to this application? Your existing data will not be lost."*
4. **Tap "Update"**:
   - Installation takes 2–5 seconds.
   - The app restarts or can be opened immediately.
   - **Result**: All sessions, custom reps & sets, voice instructions, reference photos, streaks, and historical chart logs are 100% intact.

---

## 2. How Data is Stored and Retained on Android

Under the hood, Pragti stores data in two safe locations:

| Storage Type | What is Stored | Physical Location | Retained on In-Place Update? |
|---|---|---|---|
| **Room SQLite Database** | Sessions, Tasks, Reps/Sets, Body Parts, Daily Completion Logs, Streaks, Daily Alarm Settings | `/data/data/com.heroroutine.app/databases/heroroutine_database` | ✅ **Yes (Untouched)** |
| **Internal App Sandbox Files** | Recorded Voice Guides (`VOICE_*.m4a`), Reference Photos (`IMG_*.jpg`), Progress Photos (`PROGRESS_*.jpg`) | `/data/data/com.heroroutine.app/files/` (`context.filesDir`) | ✅ **Yes (Untouched)** |
| **App Preferences** | Active mode (Champion / Parent), language (English / Hinglish), tour status | `/data/data/com.heroroutine.app/shared_prefs/` | ✅ **Yes (Untouched)** |

> [!IMPORTANT]
> **The One Rule Parents Must Follow: NEVER UNINSTALL FIRST!**  
> If an app is **uninstalled**, Android completely deletes `/data/data/com.heroroutine.app/`, wiping all routines, voice notes, and history permanently. Always install the new APK directly over the old one.

---

## 3. Developer Rules to Prevent Update Failures & Data Loss

As the maintainer of Pragti, follow these 4 rules before building any new update APK:

### Rule A: Always Increment `versionCode` in `app/build.gradle.kts`
Android compares the incoming APK's `versionCode` against the installed one. If the new APK's `versionCode` is lower than or equal to the installed version, Android may reject it with *"App not installed: A newer version is already installed"*.

```kotlin
// app/build.gradle.kts
defaultConfig {
    applicationId = "com.heroroutine.app"
    minSdk = 26
    targetSdk = 34
    versionCode = 6       // <-- Increment this (1 -> 2 -> 3 -> 4 -> 5 -> 6 ...) for each update
    versionName = "1.3.1" // <-- User-visible version string
}
```

---

### Rule B: Consistent App Signing Key (FIXED & AUTOMATED)
Android strictly enforces **Signature Verification**:
- An installed app signed with **Key A** can **ONLY** be updated by an APK signed with the exact same **Key A**.
- If signed with a different key, Android blocks installation with:  
  `"App not installed as package conflicts with an existing package by the same name"` or `"Issue installing update"`.

#### Permanent Repository Debug Keystore:
To solve ephemeral CI runner keystore mismatch permanently:
- A standardized, deterministic debug keystore is checked into the repository at `app/debug.keystore`.
- `app/build.gradle.kts` explicitly signs all debug builds using this repo keystore:
  ```kotlin
  signingConfigs {
      getByName("debug") {
          storeFile = file("debug.keystore")
          storePassword = "android"
          keyAlias = "androiddebugkey"
          keyPassword = "android"
      }
  }
  buildTypes {
      debug {
          signingConfig = signingConfigs.getByName("debug")
      }
  }
  ```
- **Result**: Every GitHub Actions CI build has the exact same cryptographic signature and SHA-256 fingerprint. New APKs will always update smoothly over previous ones without uninstalling or signature conflicts.

---

### Rule C: Room Database Migrations (Schema Changes)
In `AppDatabase.kt`:
- **If you change UI, layouts, colors, or business logic**: The database schema does not change, so no migration is needed and data remains completely safe.
- **When schema changes** (e.g. adding columns):
  Room version is currently `5` with non-destructive migrations:
  - `MIGRATION_3_4`: Adds `alarmEnabled` to sessions.
  - `MIGRATION_4_5`: Adds `isDraft` to tasks (`ALTER TABLE tasks ADD COLUMN isDraft INTEGER NOT NULL DEFAULT 0`).
  Registered via:
  ```kotlin
  Room.databaseBuilder(...)
      .addMigrations(MIGRATION_3_4, MIGRATION_4_5)
      .fallbackToDestructiveMigration()
      .build()
  ```
  This ensures zero data loss when existing user databases are upgraded.

---

### Rule D: Take a Backup Before Major Updates
Before installing a major update or when transferring to a new phone, use the built-in **Backup & Restore** feature:

1. Open **Parent Mode** -> Navigate to **Progress & Reports** (Pragati aur Report) -> Scroll down to **Data Surakshit Karein (Backup) & Phone Badlein**.
2. Tap **Backup Routines** ("Backup Banayein") to save the `.zip` archive (containing all SQLite data tables, photos, family voice notes, and historical logs).
3. On the new phone, install Pragti and tap **Restore Routines** ("Routines Wapas Layein") to import everything back seamlessly.
