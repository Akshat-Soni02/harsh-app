# HeroRoutine 🌟
### Assistive Exercise & Daily Routine Companion for Champions & Caregivers

**HeroRoutine** is a 100% offline native Android application designed specifically for individuals with motor and cognitive conditions (such as Cerebral Palsy) who may not read or write, but thrive with visual recognition, familiar voice guidance, and tactile interactions.

---

## ✨ Features Built

### 1. "Champion Mode" (Brother's Routine Screen)
* **Giant Visual Cards:** Large reference photos of each exercise or task.
* **Familiar Voice Audio Guidance:** Big "Listen" button plays voice recordings created by family members (e.g., *"Lift your right arm 10 times, let's do it together!"*).
* **Massive Touch Targets:** 68dp+ high-contrast completion buttons designed for motor tremors or imprecise touches.
* **No Accidental Modifications:** All editing/deleting features are completely hidden.
* **3.5-Second Hold-to-Switch:** A dedicated button requiring an intentional 3.5-second hold with animated circular visual & haptic feedback prevents accidental switches to Guardian mode.
* **Celebration Animations:** Confetti particle bursts and trophy overlays celebrate completed exercises.
* **Forgiving Streak:** Streaks do **not** break if a day is tough—completing $\ge 60\%$ of daily tasks preserves the streak!

### 2. "Guardian Mode" (Parents / Caregivers Dashboard)
* **Session & Routine Builder:** Organize tasks into sessions (e.g., *Morning Physiotherapy*, *Post-Lunch Stretches*).
* **Task Customization:**
  * Define type: **Exercise** (with target repetition count) or **General Task** (e.g. hydration, posture).
  * In-app microphone recorder to record and preview family voice prompts.
  * Attach visual cue photos from gallery or camera.
* **Repetition Progression Charts:** Clean visual graphs showing increases in repetition targets over time (e.g., 10 reps $\rightarrow$ 15 reps).
* **Dated Progress Photo Timeline:** Snap and attach dated photos under specific exercises to visually track posture, gait, and physical results over weeks/months.
* **100% Local & Offline:** SQLite Room database, zero cloud requirements, 100% privacy for family photos and voice notes.

---

## 🚀 How to Build & Download the APK (Cloud Build)

Because you do not need Android Studio on your Mac, this project is pre-configured with a **GitHub Actions CI/CD pipeline** (`.github/workflows/build.yml`).

### Step 1: Initialize Git and Commit
In your terminal:
```bash
git init
git add .
git commit -m "Initial commit of HeroRoutine"
```

### Step 2: Push to a New GitHub Repository
Create a repository on [GitHub](https://github.com/new) (can be private or public), then run:
```bash
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO_NAME.git
git branch -M main
git push -u origin main
```

### Step 3: Download Your APK
1. Go to your GitHub repository in your browser.
2. Click on the **"Actions"** tab.
3. You will see the **"Build Android APK"** workflow running automatically.
4. After ~2-3 minutes, click on the completed run and download **`HeroRoutine-Debug-APK`**.
5. Transfer the `.apk` file to your brother's Android phone (or download it directly on the phone via browser) and tap to install!
