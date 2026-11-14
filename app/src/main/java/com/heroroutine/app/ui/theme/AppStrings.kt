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
        if (isHinglish) "Data Surakshit Karein (Backup) & Phone Badlein"
        else "Data Backup & Device Transfer"

    fun backupDescription(isHinglish: Boolean) =
        if (isHinglish) "Sabhi kasratein, aawaz ke sandesh (voice guides), reference photos aur puraani report ek zip file mein save karein. Naya phone lene par 'Restore Routines' dabakar sab turant wapas layein."
        else "Save all exercises, family voice guidance clips, reference photos, and complete chart history into a backup file. When switching to a new phone, tap 'Restore Routines' to restore everything."

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
        if (isHinglish) "Parent Mode mein jayein" else "Enter Parent Mode"

    fun appTourTitle(isHinglish: Boolean) =
        if (isHinglish) "App Guide / Tour" else "App Tour"

    fun languageLabel(isHinglish: Boolean) =
        if (isHinglish) "Bhasha: Hinglish" else "Language: English"
}
