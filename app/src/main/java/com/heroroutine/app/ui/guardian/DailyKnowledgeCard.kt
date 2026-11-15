package com.heroroutine.app.ui.guardian

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heroroutine.app.ui.theme.TextDark
import com.heroroutine.app.ui.theme.TextMuted
import com.heroroutine.app.ui.theme.WarmOrange
import java.time.LocalDate

data class DailyKnowledgeItem(
    val category: String,
    val line: String,
    val note: String
)

val DAILY_KNOWLEDGE_POOL = listOf(
    DailyKnowledgeItem(
        category = "Neuroplasticity",
        line = "Every repetition physically builds new neural pathways in the brain. Progress happens cell by cell.",
        note = "Motor recovery principle"
    ),
    DailyKnowledgeItem(
        category = "Caregiver Strength",
        line = "Your patience and daily presence are the greatest therapy. Caregivers are the quiet architects of healing.",
        note = "Daily encouragement"
    ),
    DailyKnowledgeItem(
        category = "Mobility Science",
        line = "Gentle, consistent movement prevents joint contractures better than occasional intense exertion.",
        note = "Physiotherapy insight"
    ),
    DailyKnowledgeItem(
        category = "Disability Empowerment",
        line = "Adapting the world to the individual, rather than forcing the individual to fit, is true empowerment.",
        note = "Inclusion perspective"
    ),
    DailyKnowledgeItem(
        category = "Muscle Memory",
        line = "Muscles don't forget; nervous systems remodel with every set done with positive encouragement.",
        note = "Rehabilitation science"
    ),
    DailyKnowledgeItem(
        category = "Small Wins",
        line = "A 1% gain repeated 100 days turns into an entirely new capability. Trust small daily efforts.",
        note = "Consistency rule"
    ),
    DailyKnowledgeItem(
        category = "Brain Health",
        line = "Praise and joy release dopamine, which directly accelerates motor learning during exercises.",
        note = "Neuroscience finding"
    ),
    DailyKnowledgeItem(
        category = "Joint Nutrition",
        line = "Cartilage has no blood vessels; movement is the only pump that circulates joint nutrition.",
        note = "Orthopedic biology"
    ),
    DailyKnowledgeItem(
        category = "Dignity & Autonomy",
        line = "Every exercise completed independently, even partially, reinforces self-agency and dignity.",
        note = "Occupational therapy"
    ),
    DailyKnowledgeItem(
        category = "Rest & Recovery",
        line = "Muscles strengthen during restful sleep, not during the workout. Rest is active healing.",
        note = "Recovery wisdom"
    ),
    DailyKnowledgeItem(
        category = "Bilateral Integration",
        line = "Moving both sides of the body together helps the brain reconnect cross-hemisphere coordination.",
        note = "Rehab science"
    ),
    DailyKnowledgeItem(
        category = "Breath & Motion",
        line = "Exhaling on effort relaxes muscular spasms and lowers joint resistance during stretching.",
        note = "Therapeutic breathing"
    ),
    DailyKnowledgeItem(
        category = "Parent Resilience",
        line = "Take deep breaths today. Even doing half the routine is a complete victory for both of you.",
        note = "Mindful caregiving"
    ),
    DailyKnowledgeItem(
        category = "Sensory Feedback",
        line = "Hearing your familiar voice on instructions calms the nervous system and improves motor execution.",
        note = "Auditory cues"
    ),
    DailyKnowledgeItem(
        category = "Flexibility",
        line = "Holding a gentle stretch for 30 calm seconds signals muscle spindles to lengthen safely.",
        note = "Stretching biology"
    ),
    DailyKnowledgeItem(
        category = "Celebration Effect",
        line = "Celebrating the end of a session anchors a positive habit loop in the brain's reward center.",
        note = "Behavioral psychology"
    ),
    DailyKnowledgeItem(
        category = "Postural Alignment",
        line = "Core stability gives the limbs freedom. Small trunk adjustments multiply arm and leg ease.",
        note = "Biomechanical foundation"
    ),
    DailyKnowledgeItem(
        category = "Patience",
        line = "Nerve regeneration moves at roughly 1 millimeter per day. Healing is continuous, even when invisible.",
        note = "Neurological science"
    ),
    DailyKnowledgeItem(
        category = "Strength in Routine",
        line = "Familiar timing and sequence reduce anxiety and allow muscles to anticipate movement patterns.",
        note = "Habit formation"
    ),
    DailyKnowledgeItem(
        category = "Balance Training",
        line = "Small foot and ankle wobbles are not mistakes; they are the brain actively recalibrating equilibrium.",
        note = "Proprioception"
    ),
    DailyKnowledgeItem(
        category = "Voice of Love",
        line = "Warmth in your tone reduces cortisol, allowing tighter muscle groups to naturally relax.",
        note = "Vagal tone"
    ),
    DailyKnowledgeItem(
        category = "Grip & Coordination",
        line = "Opening fingers against resistance stimulates the extensor reflex and aids hand release.",
        note = "Hand rehabilitation"
    ),
    DailyKnowledgeItem(
        category = "Hydration",
        line = "Fascia and tendons are primarily water. Hydration prevents stiffness before daily sessions.",
        note = "Fascial fitness"
    ),
    DailyKnowledgeItem(
        category = "Empowerment",
        line = "Focus on what can be done today. Yesterday's fatigue does not define today's capacity.",
        note = "Therapy mindset"
    ),
    DailyKnowledgeItem(
        category = "Motor Planning",
        line = "Looking at reference pictures activates mirror neurons before physical movement begins.",
        note = "Visual priming"
    ),
    DailyKnowledgeItem(
        category = "Rhythm & Music",
        line = "Rhythmic counting engages the basal ganglia, making repetitive movements smoother and easier.",
        note = "Rhythmic auditory stimulation"
    ),
    DailyKnowledgeItem(
        category = "Spasticity Management",
        line = "Slow, sustained movement avoids triggering the stretch reflex, keeping limbs supple.",
        note = "Tone reduction"
    ),
    DailyKnowledgeItem(
        category = "Caregiver Wellness",
        line = "Your well-being is the foundation of your champion's progress. Take 10 quiet minutes for yourself.",
        note = "Self-care priority"
    ),
    DailyKnowledgeItem(
        category = "Range of Motion",
        line = "Active-assisted movement maintains joint capsule integrity without causing tissue micro-tears.",
        note = "Physical therapy"
    ),
    DailyKnowledgeItem(
        category = "Celebrating Milestones",
        line = "Celebrate the effort, not just the outcome. Showing up to the mat is 90% of recovery.",
        note = "Growth mindset"
    ),
    DailyKnowledgeItem(
        category = "Circulation",
        line = "Gentle leg elevation and ankle pumps reduce fluid pooling and ease joint mobility.",
        note = "Vascular health"
    ),
    DailyKnowledgeItem(
        category = "Fine Motor Control",
        line = "Small pinch and release drills stimulate large areas of the sensory-motor cortex.",
        note = "Cortical representation"
    ),
    DailyKnowledgeItem(
        category = "Unsung Heroes",
        line = "Every parent guiding these routines is performing world-class rehabilitation every single day.",
        note = "Caregiver tribute"
    ),
    DailyKnowledgeItem(
        category = "Endurance",
        line = "Dividing a long routine into two short daily sessions builds endurance without muscular exhaustion.",
        note = "Pacing strategy"
    ),
    DailyKnowledgeItem(
        category = "Shoulder Safety",
        line = "Always support the arm at the elbow and wrist when moving the shoulder above horizontal.",
        note = "Subluxation protection"
    ),
    DailyKnowledgeItem(
        category = "Mind-Body Link",
        line = "Mental visualization of an exercise activates almost the same neural circuits as doing it physically.",
        note = "Motor imagery"
    ),
    DailyKnowledgeItem(
        category = "Consistency Over Perfection",
        line = "Five minutes every day beats one hour once a week. Consistency trains the nervous system.",
        note = "Therapy golden rule"
    ),
    DailyKnowledgeItem(
        category = "Spine Mobility",
        line = "Gentle seated twists nourish spinal discs and relieve lower back pressure from sitting.",
        note = "Spinal ergonomics"
    ),
    DailyKnowledgeItem(
        category = "Warmth & Comfort",
        line = "Applying gentle warmth to tight muscles before stretching improves tissue extensibility.",
        note = "Thermal therapy"
    ),
    DailyKnowledgeItem(
        category = "Daily Gratitude",
        line = "Each breath, each movement, and each shared smile is real progress on this shared journey.",
        note = "Heartfelt affirmation"
    )
)

@Composable
fun DailyKnowledgeCard(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    val dayEpoch = LocalDate.now().toEpochDay()
    val item = remember(dayEpoch) {
        val index = (dayEpoch % DAILY_KNOWLEDGE_POOL.size).toInt().let { if (it < 0) it + DAILY_KNOWLEDGE_POOL.size else it }
        DAILY_KNOWLEDGE_POOL[index]
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(WarmOrange.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Daily Insight",
                            tint = WarmOrange,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Daily Insight • ${item.category}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WarmOrange
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Hide Daily Insight",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.line,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "— ${item.note}",
                fontSize = 11.sp,
                color = TextMuted,
                fontWeight = FontWeight.Normal
            )
        }
    }
}
