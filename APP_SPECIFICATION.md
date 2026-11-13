# App Specification: HeroRoutine (Assistive Daily Exercise & Routine Companion)

---

## 1. Vision & Purpose
A dignified, 100% offline Android assistive application designed for individuals with motor and cognitive needs (e.g., Cerebral Palsy) who may not read or write, but thrive with visual recognition, familiar voice cues, and clear physical interactions.

It bridges the gap between daily independent exercise execution and parental tracking, ensuring progress is celebrated and easily monitored.

---

## 2. Modes & User Experience

### A. "Champion Mode" (Brother's Active Screen)
* **Design Tone:** Uplifting, dignified, high-contrast, distraction-free.
* **No Accidental Modification:** All editing, deleting, or reordering tools are hidden.
* **Switching Out:** A 3.5-second long press on a discreet corner icon or a two-step gesture is required to switch back to Guardian Mode, preventing accidental navigation.
* **Task Interaction Card:**
  * **Massive Reference Photo:** Clear visual cue of the exercise or task.
  * **Big "Listen" Button (Audio):** Large audio icon that plays familiar voice instructions recorded by family (e.g., *"Lift your right knee 10 times, let's do it together!"*).
  * **Target Details (Visual):** Large numbers showing target reps/duration.
  * **Giant "Done" Checkmark:** Generous touch area to accommodate motor tremors or imprecise touches.
  * **Reward Feedback:** Tapping "Done" triggers an immediate tactile haptic vibration, a cheerful chime, and a celebratory visual indicator (e.g., confetti burst/star).
* **Strict Rule:** No bulk checkmarks. Each task in a session must be tapped individually to encourage routine pacing.

### B. "Guardian Mode" (Parents / Caregivers Screen)
* **Session Management:**
  * Create, name, and reorder sessions (e.g., *Morning Physiotherapy*, *Post-Lunch Stretches*, *Evening Relaxation*).
* **Task Builder:**
  * **Task Types:**
    * **Exercise:** Tracks target repetitions (reps) or duration (seconds/minutes), supporting historical progression tracking.
    * **General Task:** For non-repetitive activities (e.g., posture alignment, drinking water, stretching, resting).
  * **Audio Recorder:** One-tap in-app microphone to record clear, warm voice guidance; option to play back and re-record anytime.
  * **Reference Photo:** Capture via camera or pick from gallery to set the visual cue.
  * **Alarm & Reminders:** Set local alarms for session start times or specific task notifications.
* **Progress & Analytics Hub:**
  * **Daily Activity Calendar:** View which tasks were completed on which days.
  * **Exercise Progression Charts:** Visual graph tracking increases in repetitions or duration over weeks/months (e.g., 10 reps in October -> 15 reps in November).
  * **Dated Progress Photo Gallery:** Snap and store dated photos directly under a task to compare posture, range of motion, and physical results over time.

---

## 3. Architecture & Data Model (100% Offline)

All data, photos, and audio files reside strictly on the device internal storage. No network connection, accounts, or cloud storage required.

- **Session**: `id`, `title`, `orderIndex`, `iconName`
- **Task**: `id`, `sessionId`, `title`, `taskType` (EXERCISE / GENERAL), `targetReps`, `targetDurationSeconds`, `audioFilePath`, `referencePhotoPath`, `orderIndex`
- **TaskLog**: `id`, `taskId`, `timestamp`, `completed`, `repsAchieved`, `durationAchieved`
- **ProgressPhoto**: `id`, `taskId`, `photoPath`, `capturedDate`, `notes`
- **AlarmSchedule**: `id`, `targetId`, `targetType` (SESSION / TASK), `hour`, `minute`, `daysOfWeek`, `isEnabled`

---

## 4. Technical Stack

* **OS / Platform:** Android (API 26+ / Android 8.0 through Android 15).
* **Language:** Kotlin.
* **UI Framework:** Jetpack Compose (Declarative UI, ideal for large dynamic cards, custom touch states, and smooth animations).
* **Local Database:** Room Database (SQLite) with Kotlin Coroutines & Flow.
* **Media Handling:**
  * Audio: Android `MediaRecorder` (saving AAC/M4A) and `MediaPlayer`.
  * Camera / Photos: Android Photo Picker & FileProvider (storing JPEG in `context.filesDir`).
* **Notifications & Alarms:** Android `AlarmManager` with `NotificationCompat`.
* **Charts:** Lightweight Canvas-based Jetpack Compose charts (zero external heavy dependencies).
* **Build Pipeline:** GitHub Actions workflow compiling `.apk` directly on commit, downloadable via GitHub Releases or Artifacts.

---

## 5. Screen Flow Overview

1. **App Launch** -> Auto-opens in **Champion Mode** (or last used mode).
2. **Champion Mode**:
   - Clean, giant cards for the current session.
   - Tap "Listen" to hear voice guidance.
   - Tap "Done" for haptic feedback + celebration + auto-logging.
   - Long-press top corner (3.5s) to open Guardian Mode.
3. **Guardian Mode**:
   - Manage Sessions & Tasks (add photos, record voice clips, set target reps).
   - View Progress Charts & calendar logs.
   - Progress Photos Gallery (compare dated visual progress).
   - Configure Reminders / Alarms.
   - Tap "Switch to Champion Mode" when ready for brother to exercise.
