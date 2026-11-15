package com.heroroutine.app.ui.theme

object AppStrings {

    fun parentDashboard(isHinglish: Boolean) =
        if (isHinglish) "Parents Dashboard" else "Parent Dashboard"

    fun parentSubtitle(isHinglish: Boolean) =
        if (isHinglish) "Kasrat aur Routine Set Karein" else "Manage Routines & Exercises"

    fun switchToChampion(isHinglish: Boolean) =
        if (isHinglish) "Champion Mode" else "Champion Mode"

    fun tabRoutines(isHinglish: Boolean) =
        if (isHinglish) "Routines" else "Routines"

    fun tabAnalytics(isHinglish: Boolean) =
        if (isHinglish) "Pragati aur Report" else "Progress & Reports"

    fun tabPhotos(isHinglish: Boolean) =
        if (isHinglish) "Pragati Photos" else "Progress Photos"

    fun monthlyTrainingHealth(isHinglish: Boolean) =
        if (isHinglish) "Mahine ki kasrat ka hisaab" else "Monthly training health"

    fun emptyBodyParts(isHinglish: Boolean) =
        if (isHinglish) "Abhi tak koi body part tag nahi kiya gaya. Kasrat edit karte waqt body part chunein."
        else "No body parts tracked yet. Tag body parts when editing exercises."

    fun backupHeader(isHinglish: Boolean) =
        if (isHinglish) "Data Surakshit Karein (Backup)"
        else "Data Backup"

    fun backupDescription(isHinglish: Boolean) =
        if (isHinglish) "Isko roz karne ki zaroorat nahi hai — aapka saara data is phone mein apne aap save rehta hai. Iska istemal sirf tab karein jab naya phone lena ho ya mahine mein ek baar safety ke liye file save karni ho. Naya phone lene par 'Routines Wapas Layein' dabakar sab turant wapas aa jayega."
        else "You do not need to do this daily — all exercises and progress are always saved automatically on this device. Use this only when changing phones or occasionally for extra peace of mind. On a new phone, tap 'Restore Routines' to bring everything back."

    fun backupButton(isHinglish: Boolean) =
        if (isHinglish) "Backup Banayein" else "Backup Routines"

    fun restoreButton(isHinglish: Boolean) =
        if (isHinglish) "Routines Wapas Layein" else "Restore Routines"

    fun parentModeModalTitle(isHinglish: Boolean) =
        if (isHinglish) "Parent Mode" else "Parent Mode"

    fun parentModeModalDesc(isHinglish: Boolean) =
        if (isHinglish) "Yeh mode mata-pita ke liye hai. Yahan aap nayi kasrat jod sakte hain, aawaz record kar sakte hain aur chart dekh sakte hain."
        else "This mode is for parents to set up sessions, record voice instructions, and inspect progress."

    fun enterParentMode(isHinglish: Boolean) =
        if (isHinglish) "Parent Mode Kholein" else "Enter Parent Mode"

    fun appTourTitle(isHinglish: Boolean) =
        if (isHinglish) "App Guide / Tour" else "App Tour"

    fun languageLabel(isHinglish: Boolean) =
        if (isHinglish) "Bhasha: Hinglish" else "Language: English"
}
