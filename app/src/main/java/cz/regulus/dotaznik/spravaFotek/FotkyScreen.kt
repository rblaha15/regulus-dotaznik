package cz.regulus.dotaznik.spravaFotek

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import cz.regulus.dotaznik.R
import cz.regulus.dotaznik.composeString
import cz.regulus.dotaznik.toText
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

var callback1 = { _: Boolean -> }
var callback2 = { _: List<Uri> -> }

@Destination
@Composable
fun FotkyScreen(
    navigator: DestinationsNavigator,
) {
    val launcher1 =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            callback1(it)
        }

    val launcher2 =
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) {
            callback2(it)
        }

    val viewModel = koinViewModel<FotkyViewModel> {
        parametersOf(
            FotkyViewModel.Launchers(
                getTakePicture = {
                    callback1 = it
                    launcher1
                },
                getPickMultipleMedia = {
                    callback2 = it
                    launcher2
                },
            )
        )
    }

    val fotky by viewModel.fotky.collectAsStateWithLifecycle()

    Fotky(
        fotky = fotky,
        vyfotit = viewModel::vyfotit,
        vybrat = viewModel::vybrat,
        odebrat = viewModel::odebrat,
        navigateUp = navigator::navigateUp
    )
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Fotky(
    fotky: List<Pair<Int, ImageBitmap>>,
    vyfotit: ((Int) -> Unit) -> Unit,
    vybrat: ((Int) -> Unit) -> Unit,
    odebrat: (Int) -> Unit,
    navigateUp: () -> Unit,
) {
    val snackbarState = remember { SnackbarHostState() }
    val resources = LocalContext.current.resources
    val scope = rememberCoroutineScope()
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarState) {
                Snackbar(object : SnackbarData {
                    override val visuals = it.visuals

                    override fun dismiss() {
                        it.dismiss()
                    }

                    override fun performAction() {
                        cameraPermissionState.launchPermissionRequest()
                        it.performAction()
                    }
                })
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(R.string.fotky_sprava_fotek.toText().composeString())
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigateUp()
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, "Zpět")
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
            ) {
                val rotation by animateFloatAsState(if (expanded) 135F else 0F, label = "fab rotation")
                var wait by rememberSaveable { mutableStateOf(false) }

                LaunchedEffect(wait, cameraPermissionState.status.isGranted) {
                    if (wait) {
                        wait = false
                        if (cameraPermissionState.status.isGranted) {
                            expanded = false
                            vyfotit {
                                scope.launch {
                                    snackbarState.showSnackbar(resources.getString(it))
                                }
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
                                        if (cameraPermissionState.status.isGranted) {
                                            expanded = false
                                            vyfotit {
                                                scope.launch {
                                                    snackbarState.showSnackbar(resources.getString(it))
                                                }
                                            }
                                        } else if (cameraPermissionState.status.shouldShowRationale) {
                                            scope.launch {
                                                wait = true
                                                snackbarState.showSnackbar(
                                                    message = "Prosím, povolte přístup k fotoaparátu.",
                                                    actionLabel = "Povolit",
                                                    duration = SnackbarDuration.Indefinite,
                                                    withDismissAction = true,
                                                )
                                            }
                                        } else {
                                            scope.launch {
                                                wait = true
                                                snackbarState.showSnackbar(
                                                    message = "Bez fotoaparátu nemůže tato funkce fungovat. Prosím, povolte přístup k fotoaparátu.",
                                                    actionLabel = "Povolit",
                                                    duration = SnackbarDuration.Indefinite,
                                                    withDismissAction = true,
                                                )
                                            }
                                        }
                                    },
                                    shape = CircleShape
                                ) {
                                    Text(R.string.fotky_vyfotit.toText().composeString(), Modifier.padding(8.dp), style = MaterialTheme.typography.labelMedium)
                                }
                                SmallFloatingActionButton(
                                    onClick = {
                                        if (cameraPermissionState.status.isGranted) {
                                            expanded = false
                                            vyfotit {
                                                scope.launch {
                                                    snackbarState.showSnackbar(resources.getString(it))
                                                }
                                            }
                                        } else if (cameraPermissionState.status.shouldShowRationale) {
                                            scope.launch {
                                                wait = true
                                                snackbarState.showSnackbar(
                                                    message = "Prosím, povolte přístup k fotoaparátu.",
                                                    actionLabel = "Povolit",
                                                    duration = SnackbarDuration.Indefinite,
                                                    withDismissAction = true,
                                                )
                                            }
                                        } else {
                                            scope.launch {
                                                wait = true
                                                snackbarState.showSnackbar(
                                                    message = "Bez fotoaparátu nemůže tato funkce fungovat. Prosím, povolte přístup k fotoaparátu.",
                                                    actionLabel = "Povolit",
                                                    duration = SnackbarDuration.Indefinite,
                                                    withDismissAction = true,
                                                )
                                            }
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
                                        vybrat {
                                            scope.launch {
                                                snackbarState.showSnackbar(resources.getString(it))
                                            }
                                        }
                                    },
                                    shape = CircleShape
                                ) {
                                    Text(R.string.fotky_vybrat_z_galerie.toText().composeString(), Modifier.padding(8.dp), style = MaterialTheme.typography.labelMedium)
                                }
                                SmallFloatingActionButton(
                                    onClick = {
                                        expanded = false
                                        vybrat {
                                            scope.launch {
                                                snackbarState.showSnackbar(resources.getString(it))
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
                        R.string.fotky_fotka.toText().composeString(),
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
            items(fotky, key = { (id, _) -> id }) { (id, image) ->
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
                                odebrat(id)
                            },
                            Modifier.padding(8.dp)
                        ) {
                            Icon(Icons.Default.Delete, null, Modifier.padding(end = ButtonDefaults.IconSpacing))
                            Text("Odstranit")
                        }
                    }
                }
            }
            if (fotky.isEmpty()) item {
                Text(R.string.fotky_zadne_fotky.toText().composeString(), Modifier.padding(8.dp))
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