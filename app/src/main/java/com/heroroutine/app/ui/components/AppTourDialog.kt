package com.heroroutine.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.heroroutine.app.ui.theme.MintGreen
import com.heroroutine.app.ui.theme.MintGreenDark
import com.heroroutine.app.ui.theme.MintGreenSoft
import com.heroroutine.app.ui.theme.SkyBlue
import com.heroroutine.app.ui.theme.SkyBlueSoft
import com.heroroutine.app.ui.theme.TextDark
import com.heroroutine.app.ui.theme.TextMuted
import com.heroroutine.app.ui.theme.TextSecondary
import com.heroroutine.app.ui.theme.WarmCardBorder
import com.heroroutine.app.ui.theme.WarmOrange
import com.heroroutine.app.ui.theme.WarmOrangeSoft
import com.heroroutine.app.ui.theme.WarmWhite

data class TourSlide(
    val titleEn: String,
    val titleHi: String,
    val descriptionEn: String,
    val descriptionHi: String,
    val highlightsEn: List<String>,
    val highlightsHi: List<String>,
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color
)

@Composable
fun AppTourDialog(
    isHinglish: Boolean,
    onDismiss: () -> Unit
) {
    val slides = remember {
        listOf(
            TourSlide(
                titleEn = "Welcome to Pragti",
                titleHi = "Pragti Mein Swagat Hai",
                descriptionEn = "A gentle, distraction-free routine and therapy companion created for your champion brother.",
                descriptionHi = "Yeh app aapke champion bhai ki daily exercise aur physical therapy routine ko aasaan banane ke liye banaya gaya hai.",
                highlightsEn = listOf(
                    "Ultra-clean, high-contrast visual design",
                    "Distraction-free cards with zero clutter",
                    "Positive reinforcement and celebration"
                ),
                highlightsHi = listOf(
                    "Saaf-suthra design jise bhai asani se samajh sakein",
                    "Bina kisi uljhan ke seedhe task cards",
                    "Shabaashi aur celebratory utsaah"
                ),
                icon = Icons.Default.Favorite,
                iconBg = WarmOrangeSoft,
                iconTint = WarmOrange
            ),
            TourSlide(
                titleEn = "Champion Mode (For Brother)",
                titleHi = "Champion Mode (Bhai Ke Liye)",
                descriptionEn = "Designed specifically so your brother can comfortably follow his exercises independently.",
                descriptionHi = "Bhai ke liye bilkul simple screen jahan wo aasaani se apni kasrat dekh kar poora kar sakein.",
                highlightsEn = listOf(
                    "Large, bold numbers for target sets & reps",
                    "One-tap audio button to hear family voice guidance",
                    "Green 'Done' button with confetti celebration",
                    "5-second undo safeguard if marked by mistake"
                ),
                highlightsHi = listOf(
                    "Reps aur sets ka bada number jo dur se dikhe",
                    "Family ki aawaz sunne ke liye aasan audio button",
                    "Hara 'Done' button aur khushi ka celebration",
                    "Galti se tap hone par 5 second ka Undo option"
                ),
                icon = Icons.Default.FitnessCenter,
                iconBg = MintGreenSoft,
                iconTint = MintGreenDark
            ),
            TourSlide(
                titleEn = "Parent Mode (For Parents)",
                titleHi = "Parent Mode (Mata-Pita Ke Liye)",
                descriptionEn = "Access anytime from the gear icon in Champion mode to manage exercises and view progress.",
                descriptionHi = "Champion screen par upar gear icon tap karke Parent Mode kholein aur sabhi routines sambhalein.",
                highlightsEn = listOf(
                    "Organize routines by time (Morning, Evening, etc.)",
                    "Add new exercises with targeted sets, reps & body areas",
                    "Reorder tasks or edit routines easily anytime"
                ),
                highlightsHi = listOf(
                    "Subah, dopahar ya shaam ke routine alag-alag banayein",
                    "Reps, sets aur body part ke saath nayi kasrat jodein",
                    "Kisi bhi samay kasratein edit karein ya aage-peeche karein"
                ),
                icon = Icons.Default.Shield,
                iconBg = SkyBlueSoft,
                iconTint = SkyBlue
            ),
            TourSlide(
                titleEn = "Record Voice & Snap Photos",
                titleHi = "Apni Aawaz & Photos Jodein",
                descriptionEn = "Hearing a familiar, loving family voice gives immense confidence and calmness.",
                descriptionHi = "Ghar ke sadasya ki aawaz sunkar bhai ko hausla milta hai aur wo aaram se kasrat karte hain.",
                highlightsEn = listOf(
                    "Record clear audio cues directly with the built-in mic",
                    "Snap reference photos directly using camera or gallery",
                    "Visual cues help your brother recognize each posture"
                ),
                highlightsHi = listOf(
                    "Built-in mic se aasaani se apni aawaz record karein",
                    "Camera se turant photo kheenche ya gallery se chunein",
                    "Photo dekhkar bhai samajh jate hain ki kya karna hai"
                ),
                icon = Icons.Default.CameraAlt,
                iconBg = WarmOrangeSoft,
                iconTint = WarmOrange
            ),
            TourSlide(
                titleEn = "Session Alarms & Reminders",
                titleHi = "Session Alarm & Reminders",
                descriptionEn = "Never miss a scheduled routine with automated daily session alarms.",
                descriptionHi = "Har routine ke samay par phone par alarm bajega taaki koi bhi session chhoot na jaye.",
                highlightsEn = listOf(
                    "Set preferred session times (e.g. 08:30 AM)",
                    "Toggle the Alarm switch ON for daily reminders",
                    "Rings an audible reminder when it's exercise time"
                ),
                highlightsHi = listOf(
                    "Session ka sahi samay set karein (jaise 08:30 AM)",
                    "Daily Alarm ka switch ON karein",
                    "Samay aane par phone bajakar yaad dilayega"
                ),
                icon = Icons.Default.Alarm,
                iconBg = MintGreenSoft,
                iconTint = MintGreenDark
            ),
            TourSlide(
                titleEn = "Progress, Health & Safe Backup",
                titleHi = "Pragati, Health & Safe Backup",
                descriptionEn = "Track long-term mobility improvements and keep all routine data safely preserved.",
                descriptionHi = "Long-term sudhar ka chart dekhein aur backup lekar sabhi data ko surakshit rakhein.",
                highlightsEn = listOf(
                    "Inspect Weekly, Monthly, and Yearly repetition progression",
                    "Monthly activity calendar to look back at any past month",
                    "Stress-free monthly body area health percentages",
                    "One-tap 'Backup Routines' exports everything to a zip file",
                    "Update the app safely without ever uninstalling"
                ),
                highlightsHi = listOf(
                    "Hafte, mahine aur saal ka repetition progression chart",
                    "Puraane mahino ki consistency dekhne ke liye calendar",
                    "Bina kisi tension ke body part ka monthly completion rate",
                    "'Backup Routines' se audio, photo aur data zip mein save karein",
                    "Naya update aane par seedhe install karein bina delete kiye"
                ),
                icon = Icons.Default.AutoGraph,
                iconBg = SkyBlueSoft,
                iconTint = SkyBlue
            )
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    val currentSlide = slides[currentIndex]
    val isLast = currentIndex == slides.size - 1

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(28.dp)),
            color = WarmWhite,
            border = BorderStroke(1.dp, WarmCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Indicator Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHinglish) "Kadam ${currentIndex + 1} of ${slides.size}" else "Step ${currentIndex + 1} of ${slides.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )

                    // Dots Indicator
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        slides.indices.forEach { index ->
                            Box(
                                modifier = Modifier
                                    .size(if (index == currentIndex) 18.dp else 8.dp, 8.dp)
                                    .clip(CircleShape)
                                    .background(if (index == currentIndex) WarmOrange else Color(0xFFE2E8F0))
                            )
                        }
                    }

                    TextButton(onClick = onDismiss) {
                        Text(
                            text = if (isHinglish) "Chhodein" else "Skip",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Feature Icon Card
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = currentSlide.iconBg,
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = currentSlide.icon,
                            contentDescription = null,
                            tint = currentSlide.iconTint,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = if (isHinglish) currentSlide.titleHi else currentSlide.titleEn,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = TextDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                Text(
                    text = if (isHinglish) currentSlide.descriptionHi else currentSlide.descriptionEn,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Highlights Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF7F2)),
                    border = BorderStroke(1.dp, WarmCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val highlights = if (isHinglish) currentSlide.highlightsHi else currentSlide.highlightsEn
                        highlights.forEach { item ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text(
                                    text = "•",
                                    color = WarmOrange,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = item,
                                    fontSize = 13.sp,
                                    color = TextDark,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Navigation Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentIndex > 0) {
                        TextButton(
                            onClick = { currentIndex-- },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isHinglish) "Peeche" else "Back",
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(60.dp))
                    }

                    Button(
                        onClick = {
                            if (isLast) onDismiss() else currentIndex++
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WarmOrange),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text(
                            text = if (isLast) {
                                if (isHinglish) "Shuru Karein 🚀" else "Get Started 🚀"
                            } else {
                                if (isHinglish) "Aage Badhein →" else "Next →"
                            },
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
