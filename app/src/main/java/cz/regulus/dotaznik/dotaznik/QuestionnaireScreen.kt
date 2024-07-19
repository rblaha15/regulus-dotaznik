package cz.regulus.dotaznik.dotaznik

import android.animation.TimeInterpolator
import android.annotation.SuppressLint
import android.view.animation.AnticipateOvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import cz.regulus.dotaznik.BuildConfig
import cz.regulus.dotaznik.User
import cz.regulus.dotaznik.destinations.PhotosScreenDestination
import cz.regulus.dotaznik.destinations.ResetScreenDestination
import cz.regulus.dotaznik.strings.strings
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.Normalizer

@Composable
@Destination
@RootNavGraph(start = true)
fun QuestionnaireScreen(
    navigator: DestinationsNavigator,
) {
    val viewModel = koinViewModel<QuestionnaireViewModel> {
        parametersOf({
            navigator.navigate(ResetScreenDestination)
        })
    }

    val sites by viewModel.sites.collectAsStateWithLifecycle()
    val companies by viewModel.companies.collectAsStateWithLifecycle()
    val userOrNull by viewModel.userOrNull.collectAsStateWithLifecycle()
    val sendState by viewModel.sendState.collectAsStateWithLifecycle()
    val firstStart by viewModel.firstStart.collectAsStateWithLifecycle()

    if (firstStart && userOrNull != null) AlertDialog(
        onDismissRequest = {
            viewModel.started()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.started()
                }
            ) {
                Text(strings.ok)
            }
        },
        title = {
            Text("TIP!")
        },
        text = {
            Text("Mezi obrazovkami můžete přepínat posunutím do stran.")
        },
        icon = {
            Icon(Icons.Default.Lightbulb, null)
        },
    )

    Questionnaire(
        sites = sites,
        companies = companies,
        editSites = viewModel::editSites,
        logOut = viewModel::logOut,
        user = userOrNull,
        sendState = sendState,
        changeState = viewModel::changeState,
        navigateToPhotos = {
            navigator.navigate(PhotosScreenDestination)
        },
        removeAll = viewModel::askForRemoval,
        isDebug = viewModel.isDebug
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Questionnaire(
    sites: Sites?,
    companies: List<Company>,
    editSites: (Sites) -> Unit,
    logOut: () -> Unit,
    user: User?,
    sendState: SendState,
    changeState: (moveOn: Boolean) -> Unit,
    navigateToPhotos: () -> Unit,
    removeAll: () -> Unit,
    isDebug: Boolean,
) {
    val pagerState = rememberPagerState(pageCount = {
        (sites.orEmpty()).vse.size
    })
    val drawerState = rememberDrawerState(DrawerValue.Open)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    Modifier
                        .weight(1F)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (user == null) {
                        Text(strings.logIn.youAreNotLoggedIn, Modifier.padding(all = 8.dp))
                    } else {
                        Text(
                            text = strings.logIn.selectedFullName(user.name, user.surname),
                            Modifier
                                .padding(horizontal = 8.dp)
                                .padding(top = 8.dp)
                        )
                        Text(
                            text = strings.logIn.selectedEmail(user.email),
                            Modifier
                                .padding(horizontal = 8.dp)
                                .padding(top = 8.dp)
                        )
                        if (user.crn.isNotBlank()) Text(
                            text = strings.logIn.selectedCrn(user.crn),
                            Modifier
                                .padding(horizontal = 8.dp)
                                .padding(top = 8.dp)
                        )
                        Text(
                            text = strings.logIn.selectedCode(user.koNumber),
                            Modifier
                                .padding(horizontal = 8.dp)
                                .padding(vertical = 8.dp)
                        )

                        HorizontalDivider(Modifier.fillMaxWidth())

                        sites.orEmpty().vse.forEachIndexed { i, site ->
                            NavigationDrawerItem(
                                label = {
                                    Text(site.getName(sites.orEmpty()))
                                },
                                selected = pagerState.currentPage == i,
                                onClick = {
                                    scope.launch {
                                        drawerState.close()
                                        pagerState.animateScrollToPage(i)
                                    }
                                },
                                Modifier.padding(all = 8.dp),
                                icon = {
                                    Icon(site.getIcon(sites.orEmpty()), site.getName(sites.orEmpty()))
                                },
                            )
                        }

                        HorizontalDivider(Modifier.fillMaxWidth())

                        val translation = remember {
                            Animatable(0F)
                        }

                        NavigationDrawerItem(
                            label = {
                                Text(strings.export.send)
                            },
                            selected = false,
                            onClick = {
                                scope.launch {
                                    translation.animateTo(2500F, FloatTweenSpec(400, 0, FastOutLinearInEasing))
                                    drawerState.close()
                                    translation.snapTo(0F)
                                    changeState(true)
                                }
                            },
                            Modifier.padding(all = 8.dp),
                            icon = {
                                Icon(Icons.AutoMirrored.Default.Send, null, Modifier.offset {
                                    IntOffset(x = translation.value.toDp().value.toInt(), y = 0)
                                })
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = MaterialTheme.colorScheme.primary,
                                unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        )

                        TextButton(
                            onClick = {
                                removeAll()
                            },
                            Modifier.padding(all = 8.dp),
                            contentPadding = ButtonDefaults.TextButtonWithIconContentPadding,
                        ) {
                            Icon(Icons.Default.DeleteForever, null, Modifier.size(ButtonDefaults.IconSize))
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(strings.export.removeAll)
                        }

                        TextButton(
                            onClick = {
                                logOut()
                            },
                            Modifier.padding(horizontal = 8.dp),
                            contentPadding = ButtonDefaults.TextButtonWithIconContentPadding,
                        ) {
                            Icon(Icons.AutoMirrored.Default.Logout, null, Modifier.size(ButtonDefaults.IconSize))
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(strings.logIn.logOut)
                        }

                        Text(
                            "Verze: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                            Modifier.padding(8.dp),
                            fontSize = 12.sp
                        )
                        if (isDebug) Text(
                            "Toto je DEBUG verze aplikace. Žádný email nebude odeslán firmě Regulus, pouze Vám na email specifikovaný výše.",
                            Modifier.padding(horizontal = 8.dp),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        },
        drawerState = drawerState,
    ) {
        ShowSendDialogs(sendState, changeState)

        val currentSite = remember(pagerState.currentPage, sites) { sites?.vse?.get(pagerState.currentPage) }
        if (currentSite != null && sites != null) Scaffold(
            Modifier
                .imePadding()
                .navigationBarsPadding(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(currentSite.getName(sites))
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                if (drawerState.isOpen) strings.close else strings.open
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                navigateToPhotos()
                            }
                        ) {
                            Icon(Icons.Default.PhotoLibrary, strings.photos.photosManager)
                        }
                    }
                )
            },
        ) { paddingValues ->
            HorizontalPager(
                state = pagerState,
                Modifier
                    .padding(paddingValues),
                pageSpacing = 8.dp,
                key = { sites.vse[it].getName(sites) },
            ) { i ->
                Scaffold(
                    floatingActionButton = {
                        val posun = remember {
                            Animatable(0F)
                        }
                        if (i == sites.vse.lastIndex) {
                            FloatingActionButton(
                                onClick = {
                                    scope.launch {
                                        posun.animateTo(500F, FloatTweenSpec(800, 0, AnticipateOvershootInterpolator().toEasing()))
                                        pagerState.animateScrollToPage(0)
                                        posun.snapTo(0F)
                                        changeState(true)
                                    }
                                },
                                Modifier
                                    .offset {
                                        IntOffset(x = posun.value.toDp().value.toInt(), y = 0)
                                    }
                                    .padding(bottom = OutlinedTextFieldDefaults.MinHeight),
                            ) {
                                Icon(Icons.AutoMirrored.Default.Send, strings.export.send)
                            }
                        }
                    },
                ) { _ ->
                    Column(
                        Modifier
                            .fillMaxSize()
                    ) {
                        val site by remember(sites.vse, i) {
                            derivedStateOf {
                                sites.vse[i]
                            }
                        }
                        Column(
                            Modifier
                                .weight(1F)
                                .verticalScroll(rememberScrollState())
                        ) {
                            site.getWidgets(sites).dropLast(1).forEachIndexed { i, widgets ->
                                widgets.forEach { widget ->
                                    Surface(
                                        Modifier.fillMaxWidth(),
                                        color = if (i % 2 == 0) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
                                    ) {
                                        Widget(
                                            sites = sites,
                                            companies = companies,
                                            widget = widget,
                                            editWidget = { newWidget ->
                                                editSites(sites.copySite(site.copyWidget(newWidget)))
                                            },
                                        )
                                    }
                                }
                            }
                        }

                        Widget(
                            sites = sites,
                            companies = companies,
                            widget = site.getWidgets(sites).last().last(),
                            editWidget = { newWidget ->
                                editSites(sites.copySite(site.copyWidget(newWidget)))
                            },
                        )
                    }
                }
            }
        }
        else LinearProgressIndicator()
    }
}

@Composable
private fun ShowSendDialogs(sendState: SendState, changeState: (moveOn: Boolean) -> Unit) = when (sendState) {
    SendState.Nothing -> Unit
    is SendState.ConfirmSend -> AlertDialog(
        onDismissRequest = {
            changeState(false)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    changeState(true)
                }
            ) {
                Text(strings.yes)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    changeState(false)
                }
            ) {
                Text(strings.no)
            }
        },
        title = {
            Text(text = strings.export.doYouWantToSend)
        },
        icon = {
            Icon(Icons.AutoMirrored.Default.Send, null)
        },
        text = {
            Text(text = strings.export.doYouReallyWantToSend(sendState.email))
        },
    )

    SendState.Sending -> AlertDialog(
        onDismissRequest = {},
        confirmButton = {},
        dismissButton = {},
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator()
                Text(text = strings.export.sending, Modifier.padding(8.dp))
            }
        },
    )

    is SendState.MissingField -> AlertDialog(
        onDismissRequest = {
            changeState(false)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    changeState(false)
                }
            ) {
                Text(strings.ok)
            }
        },
        title = {
            Text(text = strings.export.missingField)
        },
        icon = {
            Icon(Icons.Default.WarningAmber, null)
        },
        text = {
            Text(text = strings.export.pleaseFillInField(sendState.fieldLabel))
        },
    )

    SendState.Sending -> AlertDialog(
        onDismissRequest = {},
        confirmButton = {},
        dismissButton = {},
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator()
                Text(text = strings.export.sending, Modifier.padding(8.dp))
            }
        },
    )

    SendState.Success -> AlertDialog(
        onDismissRequest = {
            changeState(true)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    changeState(true)
                }
            ) {
                Text(strings.ok)
            }
        },
        title = {
            Text(text = strings.export.emailSuccessfullySent)
        },
        icon = {
            Icon(Icons.Default.Check, null)
        },
    )

    SendState.ConfirmDataRemoval -> AlertDialog(
        onDismissRequest = {
            changeState(false)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    changeState(true)
                }
            ) {
                Text(strings.yes)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    changeState(false)
                }
            ) {
                Text(strings.no)
            }
        },
        title = {
            Text(text = strings.export.removeAll)
        },
        icon = {
            Icon(Icons.Default.DeleteForever, null)
        },
        text = {
            Text(text = strings.export.doYouRellyWantToRemoveData)
        },
    )

    SendState.Error.Offline -> AlertDialog(
        onDismissRequest = {
            changeState(false)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    changeState(false)
                }
            ) {
                Text(strings.ok)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    changeState(true)
                }
            ) {
                Text(strings.export.moreInfo)
            }
        },
        title = {
            Text(text = strings.export.emailNotSent(""))
        },
        icon = {
            Icon(Icons.Default.WifiOff, null)
        },
        text = {
            Text(text = strings.export.youAreOffline)
        },
    )

    SendState.Error.Other -> AlertDialog(
        onDismissRequest = {
            changeState(false)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    changeState(false)
                }
            ) {
                Text(strings.ok)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    changeState(true)
                }
            ) {
                Text(strings.export.moreInfo)
            }
        },
        title = {
            Text(text = strings.export.emailNotSent(""))
        },
        icon = {
            Icon(Icons.Default.ErrorOutline, null)
        },
        text = {
            Text(text = strings.export.errorReported)
        },
    )

    is SendState.Error.Details -> AlertDialog(
        onDismissRequest = {
            changeState(false)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    changeState(false)
                }
            ) {
                Text(strings.ok)
            }
        },
        title = {
            Text(text = strings.export.emailNotSent(strings.export.thisIsTheIssue))
        },
        icon = {
            Icon(Icons.Default.ErrorOutline, null)
        },
        text = {
            Text(text = sendState.error, Modifier.verticalScroll(rememberScrollState()))
        },
    )
}

private fun Sites?.orEmpty() = this ?: Sites()

fun TimeInterpolator.toEasing() = Easing { x -> getInterpolation(x) }

private fun String.normalize() = Normalizer.normalize(this, Normalizer.Form.NFD)!!

fun String.forComparing() = uppercase().normalize().replace("\\p{Mn}+".toRegex(), "")