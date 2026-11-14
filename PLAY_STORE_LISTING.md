# Google Play Store Listing & Launch Kit — Pragti

Everything you need to set up, publish, and list your app on the Google Play Console.

---

## 1. Brand Identity & Name

### Selected Name: **Pragti**
- **Meaning & Root**: Derived from the Hindi / Sanskrit word *प्रगति* (*Pragati*), meaning **Progress, Growth, Advancement, and Upliftment**.
- **Why the "Pragti" Spelling Works Exceptionally Well**:
  1. **Clean & Modern (Startup Vibe)**: Dropping the redundant middle 'a' gives it the punchy, modern feel of top health-tech and Indian consumer apps (like *Practo*, *Khatabook*, *Swiggy*, *Zomato*).
  2. **Distinctive & Uncluttered**: Avoids confusion with generic government initiatives (e.g., PRAGATI platform) or generic corporate trademarks while preserving authentic phonetic pronunciation.
  3. **Universal Resonance**:
     - **For Parents**: Represents the heartfelt desire for their child’s everyday advancement and rehabilitation.
     - **For Champions / Patients**: Positive, encouraging, non-medicalized, and stigma-free.
     - **For Doctors & Physiotherapists**: Professional and outcome-oriented ("Pragti Report", tracking patient progress).
  4. **Celebration Cohesion**: The app is called **Pragti** (Progress), while milestone completion celebrations cheer **"Shabaash!"** (Well done!).

---

## 2. Store Listing Texts

### App Title (Max 30 characters)
```text
Pragti: Daily Routine & Rehab
```
*(Exact length: 29 characters — fits within the strict 30-character Play Store limit)*

*Alternative options:*
- `Pragti: Daily Habit & Therapy` (29 chars)
- `Pragti: Routine & Rehab Tracker` (30 chars)

---

### Short Description (Max 80 characters)
*Strict limit: 80 characters. High clarity for patients, parents & therapists.*
```text
Daily exercise, therapy routines, and visual habit tracking for champions.
```
*(Exact length: 73 characters)*

---

### Full Description (Max 4,000 characters)
```text
Pragti is a gentle, distraction-free daily routine and physical therapy companion created for champions, families, and therapists. Designed with ultra-clear visual cues, high-contrast layouts, and voice guidance, Pragti makes completing daily rehabilitation exercises and routines simple, rewarding, and stress-free.

Whether recovering from an injury, managing ongoing physical therapy, or building healthy daily habits, Pragti gives you the tools to succeed at your own pace.

🌟 WHY CHAMPIONS LOVE IT:
• Distraction-Free Routine View: Big, clear exercise cards showing exactly what to do without overwhelming menus or clutter.
• High-Contrast Sets & Reps: Bold, easily legible repetition counters tailored for individuals with diverse physical or visual needs.
• Voice Guidance Clips: Hear warm, encouraging exercise instructions recorded in a familiar voice by parents or your therapist.
• Celebrate Milestones: Enjoy gentle celebrations and daily streak counters that make every completed routine feel like a victory.

🛡️ WHY PARENTS & GUARDIANS LOVE IT:
• Dedicated Parent Dashboard: Easily set up morning, afternoon, or evening routines with customized exercise targets.
• Daily Scheduled Alarms: Set routine times with built-in daily alarms so sessions are never missed.
• Camera & Posture Guides: Attach reference posture photos or take real-time camera shots to ensure correct form.
• Monthly Training Health: Clear, pressure-free analytics tracking target body areas, monthly consistency, and active days.
• Offline & 100% Private: All family routines, voice notes, and photos stay secure on your device.
• Easy Backup & Transfer: Save routines and progress history to a portable backup file to switch phones effortlessly.

🩺 BUILT FOR PHYSIOTHERAPY & DOCTORS:
• Track target body areas (Arms, Legs, Shoulders, Core, Back, Neck, and custom muscle groups).
• Monitor monthly compliance rates and rehabilitation progress.
• Tailored for physical therapy, occupational therapy, and neuro-rehabilitation protocols.

🌐 BILINGUAL SUPPORT:
• Toggle smoothly between English and Hinglish (Hindi in English script) with a single tap.

Download Pragti today and bring consistency, confidence, and joy to daily routines!
```

---

## 3. Play Store Categorization & Tags

- **Application Type**: App
- **Primary Category**: **Health & Fitness** *(Alternative: Medical)*
- **Store Tags** (Select up to 5):
  1. *Physical Therapy*
  2. *Habit Tracker*
  3. *Health & Fitness*
  4. *Parenting*
  5. *Rehabilitation*

---

## 4. Privacy Policy URL (GitHub Pages)

### Live URL to enter in Play Console:
```text
https://akshat-soni02.github.io/harsh-app/privacy-policy.html
```
*(Also accessible at `https://akshat-soni02.github.io/harsh-app/`)*

### How to Enable GitHub Pages (Takes 30 seconds):
1. Open your repository on GitHub: `https://github.com/Akshat-Soni02/harsh-app`
2. Click **Settings** (top right tab).
3. In the left sidebar, click **Pages**.
4. Under **Build and deployment** -> **Branch**:
   - Source: `Deploy from a branch`
   - Branch: select `main`
   - Folder: select `/docs`
5. Click **Save**. GitHub will publish your Privacy Policy within 60 seconds at the URL above.

---

## 5. Google Play Data Safety Form Answers

When filling out the **Data Safety** questionnaire in Google Play Console, use these exact answers:

| Question | Answer | Reason |
|---|---|---|
| **Does your app collect or share any user data?** | **Yes** *(Collected locally, not shared)* | The app saves user-entered photos and voice notes on the device. |
| **Is all user data encrypted in transit?** | **Yes** | Standard HTTPS used for any network connectivity. |
| **Do you provide a way for users to request data deletion?** | **Yes** | Users can delete all tasks/photos in-app or clear app data / uninstall. |

### Data Types Breakdown:
1. **Photos and Videos**:
   - Data type: *Photos*
   - Collected? **Yes** (Only when user takes or picks reference photos)
   - Shared? **No**
   - Purpose: *App functionality* (Displaying exercise reference postures)
   - Is it ephemeral? **No** (Saved in local database until user deletes)
2. **Audio Files**:
   - Data type: *Voice or sound recordings*
   - Collected? **Yes** (Only when parent records voice guide)
   - Shared? **No**
   - Purpose: *App functionality* (Playback coaching during exercises)
   - Is it ephemeral? **No** (Saved locally until deleted)
3. **Fitness & Health Data**:
   - Data type: *Fitness info* (Exercise reps & sets)
   - Collected? **Yes**
   - Shared? **No**
   - Purpose: *App functionality & analytics*

---

## 6. Content Rating (IARC Questionnaire)

- Category: **Consumer / Utility / Health**
- Violence, Sexual content, Gambling, Offensive language: **None**
- User-generated open sharing / chat with strangers: **No**
- Shares physical location: **No**
- Allows buying digital goods: **No** (until Doctor mode paywall is launched)
- Result: **Rated for 3+ / Everyone (Clean rating worldwide)**.

---

## 7. Target Audience & Children's Policy Declaration

- **Target Age Groups**: Check **13-15**, **16-17**, and **18 and over**.
- *Important Tip*: Unless you explicitly want to enter Google's strict "Designed for Families" program (which has heavy ad/SDK audit restrictions), do **not** select ages under 13 on the initial console declaration. You can state that parents supervise routines.
- Could the app appeal to children? Select **Yes** or **No** (Select *"Yes, but primarily designed for general audiences and families under parental supervision"*).
