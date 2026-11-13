package com.heroroutine.app.ui.guardian

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.heroroutine.app.data.model.ProgressPhotoEntity
import com.heroroutine.app.media.MediaFileManager
import com.heroroutine.app.ui.theme.HeroGreen
import com.heroroutine.app.viewmodel.MainViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressPhotosView(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allTasks by viewModel.allTasks.collectAsState()
    val allPhotos by viewModel.allPhotos.collectAsState()

    var selectedTaskIdFilter by remember { mutableStateOf<Long?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredPhotos = if (selectedTaskIdFilter == null) {
        allPhotos
    } else {
        allPhotos.filter { it.taskId == selectedTaskIdFilter }
    }

    Scaffold(
        floatingActionButton = {
            if (allTasks.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = HeroGreen,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Add Progress Photo")
                }
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Progress Photo Timeline",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C1B1F)
            )
            Text(
                text = "Capture dated photos to compare posture & movement over time",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Filter chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    val isAll = selectedTaskIdFilter == null
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isAll) HeroGreen else Color.White,
                        shadowElevation = if (isAll) 3.dp else 1.dp,
                        modifier = Modifier.clickable { selectedTaskIdFilter = null }
                    ) {
                        Text(
                            text = "All Photos",
                            fontWeight = FontWeight.Bold,
                            color = if (isAll) Color.White else Color.Black,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }

                items(allTasks) { task ->
                    val isSelected = selectedTaskIdFilter == task.id
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) HeroGreen else Color.White,
                        shadowElevation = if (isSelected) 3.dp else 1.dp,
                        modifier = Modifier.clickable { selectedTaskIdFilter = task.id }
                    ) {
                        Text(
                            text = task.title,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color.Black,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredPhotos.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📸", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No progress photos yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap the camera button below to log your brother's visual progress!",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 72.dp)
                ) {
                    items(filteredPhotos, key = { it.id }) { photo ->
                        val task = allTasks.find { it.id == photo.taskId }
                        val formattedDate = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault())
                            .format(Date(photo.timestamp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(3.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = task?.title ?: "Exercise",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = formattedDate,
                                            fontSize = 13.sp,
                                            color = HeroGreen,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    IconButton(onClick = { viewModel.deleteProgressPhoto(photo) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Photo",
                                            tint = Color.Gray
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1.5f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFFF0F0F0))
                                ) {
                                    AsyncImage(
                                        model = File(photo.photoPath),
                                        contentDescription = "Progress Photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                if (!photo.notes.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Notes: ${photo.notes}",
                                        fontSize = 14.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddProgressPhotoDialog(
            tasks = allTasks,
            onDismiss = { showAddDialog = false },
            onSave = { taskId, photoPath, notes ->
                viewModel.addProgressPhoto(taskId, photoPath, notes)
                showAddDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProgressPhotoDialog(
    tasks: List<com.heroroutine.app.data.model.TaskEntity>,
    onDismiss: () -> Unit,
    onSave: (taskId: Long, photoPath: String, notes: String?) -> Unit
) {
    val context = LocalContext.current
    var selectedTaskId by remember { mutableStateOf(tasks.firstOrNull()?.id ?: 0L) }
    var photoPath by remember { mutableStateOf<String?>(null) }
    var notes by remember { mutableStateOf("") }
    var expandedDropdown by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val destFile = MediaFileManager.createImageFile(context, "PROGRESS")
            if (MediaFileManager.copyUriToFile(context, it, destFile)) {
                photoPath = destFile.absolutePath
            }
        }
    }

    val selectedTaskName = tasks.find { it.id == selectedTaskId }?.title ?: "Select Task"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Progress Photo", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Task Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedTaskName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Associated Task / Exercise") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        tasks.forEach { task ->
                            DropdownMenuItem(
                                text = { Text(task.title) },
                                onClick = {
                                    selectedTaskId = task.id
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                // Choose image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF3F4F6))
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (photoPath != null && File(photoPath!!).exists()) {
                        AsyncImage(
                            model = File(photoPath!!),
                            contentDescription = "Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = "Pick Photo", tint = HeroGreen)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Tap to select photo", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (e.g. Back was much straighter today)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (photoPath != null) {
                        onSave(selectedTaskId, photoPath!!, notes.trim())
                    }
                },
                enabled = photoPath != null,
                colors = ButtonDefaults.buttonColors(containerColor = HeroGreen)
            ) {
                Text("Save Photo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
