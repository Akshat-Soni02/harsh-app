package com.heroroutine.app.ui.guardian

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
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
import com.heroroutine.app.ui.theme.HeroGreen

@Composable
fun SessionEditorDialog(
    initialSession: SessionEntity? = null,
    onDismiss: () -> Unit,
    onSave: (title: String, emoji: String, targetTime: String?) -> Unit
) {
    var title by remember { mutableStateOf(initialSession?.title ?: "") }
    var selectedEmoji by remember { mutableStateOf(initialSession?.iconEmoji ?: "☀️") }
    var targetTime by remember { mutableStateOf(initialSession?.targetTime ?: "09:00") }

    val emojis = listOf("☀️", "🏃", "⚡", "🧘", "💪", "🌙", "🎯", "⭐")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialSession == null) "New Session" else "Edit Session",
                fontWeight = FontWeight.Bold
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
                    label = { Text("Session Name (e.g. Morning Routine)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Select Icon Emoji:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(emojis) { emoji ->
                        val isSelected = emoji == selectedEmoji
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) HeroGreen.copy(alpha = 0.2f) else Color(0xFFF0F0F0),
                            modifier = Modifier.clickable { selectedEmoji = emoji }
                        ) {
                            Text(
                                text = emoji,
                                fontSize = 24.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = targetTime,
                    onValueChange = { targetTime = it },
                    label = { Text("Target Time (e.g. 09:30 AM)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title.trim(), selectedEmoji, targetTime.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = HeroGreen)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
