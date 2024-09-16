package cz.regulus.dotaznik.spravaFotek

import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import cz.regulus.dotaznik.strings.strings
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Destination
@Composable
fun PhotosScreen(
    navigator: DestinationsNavigator,
) {
    val launcher1 =
        rememberResultLauncher(ActivityResultContracts.TakePicture())

    val launcher2 =
        rememberResultLauncher(ActivityResultContracts.PickMultipleVisualMedia())

    val viewModel = koinViewModel<PhotosViewModel> {
        parametersOf(
            PhotosViewModel.Launchers(
                takePicture = launcher1,
                pickMultipleMedia = launcher2,
            )
        )
    }

    val photos by viewModel.photos.collectAsStateWithLifecycle()

    Fotky(
        photos = photos,
        takePicture = viewModel::takePicture,
        choose = viewModel::choosePhoto,
        remove = viewModel::removePhoto,
        navigateUp = navigator::navigateUp
    )
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Fotky(
    photos: List<Pair<Int, ImageBitmap>>,
    takePicture: ((String) -> Unit) -> Unit,
    choose: ((String) -> Unit) -> Unit,
    remove: (Int) -> Unit,
    navigateUp: () -> Unit,
) {
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(strings.photos.photosManager)
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigateUp()
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, strings.back)
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
            ) {
                val rotation by animateFloatAsState(if (expanded) 135F else 0F, label = "fab rotation")
                var isPermissionDialogOpen by rememberSaveable { mutableStateOf(false) }

                LaunchedEffect(isPermissionDialogOpen, cameraPermissionState.status) {
                    if (isPermissionDialogOpen && cameraPermissionState.status.isGranted) {
                        isPermissionDialogOpen = false
                        expanded = false
                        takePicture {
                            scope.launch {
                                snackbarState.showSnackbar(it)
                            }
                        }
                    }
                }

                LazyColumn(
                    horizontalAlignment = Alignment.End,
                ) {
                    item {
                        AnimatedVisibility(
                            visible = expanded,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Row(
                                Modifier.padding(bottom = 16.dp, end = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Surface(
                                    onClick = {
                                        isPermissionDialogOpen = false
                                        if (cameraPermissionState.status.isGranted) {
                                            expanded = false
                                            takePicture {
                                                scope.launch {
                                                    snackbarState.showSnackbar(it)
                                                }
                                            }
                                        } else {
                                            isPermissionDialogOpen = true
                                            cameraPermissionState.launchPermissionRequest()
                                        }
                                    },
                                    shape = CircleShape
                                ) {
                                    Text(strings.photos.takePhoto, Modifier.padding(8.dp), style = MaterialTheme.typography.labelMedium)
                                }
                                SmallFloatingActionButton(
                                    onClick = {
                                        isPermissionDialogOpen = false
                                        if (cameraPermissionState.status.isGranted) {
                                            expanded = false
                                            takePicture {
                                                scope.launch {
                                                    snackbarState.showSnackbar(it)
                                                }
                                            }
                                        } else {
                                            isPermissionDialogOpen = true
                                            cameraPermissionState.launchPermissionRequest()
                                        }
                                    },
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                ) {
                                    Icon(Icons.Default.CameraAlt, null)
                                }
                            }
                        }
                    }
                    item {
                        AnimatedVisibility(
                            visible = expanded,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Row(
                                Modifier.padding(bottom = 16.dp, end = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Surface(
                                    onClick = {
                                        expanded = false
                                        choose {
                                            scope.launch {
                                                snackbarState.showSnackbar(it)
                                            }
                                        }
                                    },
                                    shape = CircleShape
                                ) {
                                    Text(strings.photos.choosePhoto, Modifier.padding(8.dp), style = MaterialTheme.typography.labelMedium)
                                }
                                SmallFloatingActionButton(
                                    onClick = {
                                        expanded = false
                                        choose {
                                            scope.launch {
                                                snackbarState.showSnackbar(it)
                                            }
                                        }
                                    },
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, null)
                                }
                            }
                        }
                    }
                }
                FloatingActionButton(
                    onClick = {
                        expanded = !expanded
                    }
                ) {
                    Icon(
                        Icons.Default.Add,
                        strings.photos.photo,
                        Modifier.rotate(rotation)
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            items(photos, key = { (id, _) -> id }) { (id, image) ->
                ElevatedCard(
                    Modifier
                        .padding(8.dp)
                        .animateItemPlacement()
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(image, null, Modifier.weight(1F))
                        OutlinedButton(
                            onClick = {
                                remove(id)
                            },
                            Modifier.padding(8.dp)
                        ) {
                            Icon(Icons.Default.Delete, null, Modifier.padding(end = ButtonDefaults.IconSpacing))
                            Text(strings.photos.remove)
                        }
                    }
                }
            }
            if (photos.isEmpty()) item {
                Text(strings.photos.noPhotos, Modifier.padding(8.dp))
            }
        }
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Surface(
                onClick = {
                    expanded = false
                },
                Modifier.fillMaxSize(),
            ) {}
        }
    }
}