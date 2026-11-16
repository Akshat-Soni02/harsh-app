package com.heroroutine.app.ui.guardian

import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heroroutine.app.data.model.SessionEntity
import com.heroroutine.app.ui.theme.MintGreen
import com.heroroutine.app.ui.theme.MintGreenSoft
import com.heroroutine.app.ui.theme.TextDark
import com.heroroutine.app.ui.theme.TextSecondary
import com.heroroutine.app.ui.theme.WarmCardBorder
import com.heroroutine.app.ui.theme.WarmOrange
import com.heroroutine.app.ui.theme.WarmOrangeBorder
import com.heroroutine.app.ui.theme.WarmOrangeSoft
import com.heroroutine.app.ui.theme.WarmWhite

@Composable
fun SessionEditorDialog(
    initialSession: SessionEntity? = null,
    onDismiss: () -> Unit,
    onSave: (title: String, emoji: String, targetTime: String?, alarmEnabled: Boolean) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(initialSession?.title ?: "") }
    var selectedEmoji by remember { mutableStateOf(initialSession?.iconEmoji ?: "☀️") }
    var targetTime by remember { mutableStateOf(initialSession?.targetTime ?: "09:00 AM") }
    var alarmEnabled by remember { mutableStateOf(initialSession?.alarmEnabled ?: false) }

    val emojis = listOf("☀️", "🌅", "💪", "🏃", "🧘", "⚡", "🌙", "🎯")

    fun showTimePicker() {
        val (currHour, currMin) = try {
            val trimmed = targetTime.trim().uppercase()
            var h = 9
            var m = 0
            if (trimmed.contains("AM") || trimmed.contains("PM")) {
                val isPm = trimmed.contains("PM")
                val clean = trimmed.replace("AM", "").replace("PM", "").trim()
                val parts = clean.split(":")
                h = parts[0].trim().toInt()
                m = if (parts.size > 1) parts[1].trim().toInt() else 0
                if (isPm && h < 12) h += 12
                if (!isPm && h == 12) h = 0
            } else if (trimmed.contains(":")) {
                val parts = trimmed.split(":")
                h = parts[0].trim().toInt()
                m = if (parts.size > 1) parts[1].trim().toInt() else 0
            }
            Pair(h, m)
        } catch (_: Exception) {
            Pair(9, 0)
        }

        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val isPm = hourOfDay >= 12
                val displayHour = when {
                    hourOfDay == 0 -> 12
                    hourOfDay > 12 -> hourOfDay - 12
                    else -> hourOfDay
                }
                val amPm = if (isPm) "PM" else "AM"
                targetTime = String.format("%02d:%02d %s", displayHour, minute, amPm)
            },
            currHour,
            currMin,
            false
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialSession == null) "New Routine Session" else "Edit Routine Session",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = TextDark
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Session Name",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = {
                            Text(
                                "e.g. Morning Routine, Evening Stretch",
                                fontSize = 13.sp,
                                color = Color(0xFFA0AEC0),
                                fontWeight = FontWeight.Normal
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WarmOrange,
                            unfocusedBorderColor = WarmCardBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text("Session Icon:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(emojis) { emoji ->
                        val isSelected = emoji == selectedEmoji
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) WarmOrangeSoft else Color(0xFFF1F5F9),
                            border = BorderStroke(1.5.dp, if (isSelected) WarmOrange else WarmCardBorder),
                            modifier = Modifier.clickable { selectedEmoji = emoji }
                        ) {
                            Text(emoji, fontSize = 24.sp, modifier = Modifier.padding(10.dp))
                        }
                    }
                }

                // Time picker field
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = targetTime,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Scheduled Time") },
                        placeholder = {
                            Text(
                                "Tap to select time (e.g. 08:30 AM)",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8),
                                fontWeight = FontWeight.Normal
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { showTimePicker() }) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = "Select Time",
                                    tint = WarmOrange
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WarmOrange,
                            unfocusedBorderColor = WarmCardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                showTimePicker()
                            }
                    )
                }

                // Alarm Toggle
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (alarmEnabled) MintGreenSoft else Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, if (alarmEnabled) MintGreen else WarmCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (alarmEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                contentDescription = null,
                                tint = if (alarmEnabled) WarmOrange else TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Daily Alarm Reminder",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Text(
                                    text = if (alarmEnabled) "Rings daily at scheduled time" else "No alarm scheduled",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = alarmEnabled,
                            onCheckedChange = { alarmEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = WarmOrange,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFCBD5E1)
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) onSave(title.trim(), selectedEmoji, targetTime.trim(), alarmEnabled)
                },
                colors = ButtonDefaults.buttonColors(containerColor = WarmOrange),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Save Session", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        },
        containerColor = WarmWhite,
        shape = RoundedCornerShape(26.dp)
    )
}

