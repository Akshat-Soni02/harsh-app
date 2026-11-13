package com.heroroutine.app.ui.guardian

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heroroutine.app.data.model.SessionEntity
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
    onSave: (title: String, emoji: String, targetTime: String?) -> Unit
) {
    var title by remember { mutableStateOf(initialSession?.title ?: "") }
    var selectedEmoji by remember { mutableStateOf(initialSession?.iconEmoji ?: "☀️") }
    var targetTime by remember { mutableStateOf(initialSession?.targetTime ?: "09:00 AM") }

    val emojis = listOf("☀️", "🌅", "💪", "🏃", "🧘", "⚡", "🌙", "🎯")

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
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Session Name") },
                    placeholder = { Text("e.g. Morning Routine, Evening Stretch") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WarmOrange,
                        unfocusedBorderColor = WarmCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

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

                OutlinedTextField(
                    value = targetTime,
                    onValueChange = { targetTime = it },
                    label = { Text("Scheduled Time") },
                    placeholder = { Text("e.g. 08:30 AM") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WarmOrange,
                        unfocusedBorderColor = WarmCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) onSave(title.trim(), selectedEmoji, targetTime.trim())
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
