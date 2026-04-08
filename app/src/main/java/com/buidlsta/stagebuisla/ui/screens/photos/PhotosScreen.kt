package com.buidlsta.stagebuisla.ui.screens.photos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.buidlsta.stagebuisla.data.db.entity.PhotoEntity
import com.buidlsta.stagebuisla.ui.components.*
import com.buidlsta.stagebuisla.ui.navigation.Screen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen(navController: NavController, projectId: Long, phaseId: Long = 0L) {
    val vm: PhotosViewModel = koinViewModel(parameters = { parametersOf(projectId) })
    val state by vm.state.collectAsState()
    var selectedPhaseId by remember { mutableStateOf(phaseId) }
    var deleteTarget by remember { mutableStateOf<PhotoEntity?>(null) }
    var fullscreenPhoto by remember { mutableStateOf<PhotoEntity?>(null) }

    LaunchedEffect(selectedPhaseId) { vm.setPhaseFilter(selectedPhaseId) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Photos",
                onBack = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            val targetPhaseId = if (selectedPhaseId != 0L) selectedPhaseId
            else state.phases.firstOrNull()?.id ?: 0L
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddPhoto.route(projectId, targetPhaseId)) }
            ) {
                Icon(Icons.Default.AddAPhoto, contentDescription = "Add Photo")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            if (state.phases.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedPhaseId == 0L,
                            onClick = { selectedPhaseId = 0L },
                            label = { Text("All") }
                        )
                    }
                    items(state.phases) { phase ->
                        FilterChip(
                            selected = selectedPhaseId == phase.id,
                            onClick = { selectedPhaseId = phase.id },
                            label = { Text(phase.name) }
                        )
                    }
                }
            }

            if (state.isLoading) {
                LoadingScreen()
                return@Column
            }

            if (state.photos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        icon = Icons.Default.PhotoLibrary,
                        title = "No photos yet",
                        subtitle = "Document your construction progress with photos"
                    )
                }
                return@Column
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    bottom = innerPadding.calculateBottomPadding() + 80.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.photos, key = { it.id }) { photo ->
                    PhotoCard(
                        photo = photo,
                        onView = { fullscreenPhoto = photo },
                        onDelete = { deleteTarget = photo }
                    )
                }
            }
        }
    }

    fullscreenPhoto?.let { photo ->
        FullscreenPhotoDialog(
            photo = photo,
            onDismiss = { fullscreenPhoto = null }
        )
    }

    deleteTarget?.let { photo ->
        DeleteConfirmDialog(
            itemName = "photo",
            onConfirm = {
                vm.deletePhoto(photo.id)
                deleteTarget = null
            },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
private fun PhotoCard(photo: PhotoEntity, onView: () -> Unit, onDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onView)
    ) {
        AsyncImage(
            model = photo.uri,
            contentDescription = photo.caption,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
        ) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        RoundedCornerShape(6.dp)
                    )
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        if (photo.caption.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                    .padding(4.dp)
            ) {
                Text(
                    text = photo.caption,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun FullscreenPhotoDialog(photo: PhotoEntity, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
        text = {
            Column {
                AsyncImage(
                    model = photo.uri,
                    contentDescription = photo.caption,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                )
                if (photo.caption.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(photo.caption, style = MaterialTheme.typography.bodyMedium)
                }
                Text(
                    text = formatDateTime(photo.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}
