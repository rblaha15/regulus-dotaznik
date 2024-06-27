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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import cz.regulus.dotaznik.BuildConfig
import cz.regulus.dotaznik.Uzivatel
import cz.regulus.dotaznik.destinations.FotkyScreenDestination
import cz.regulus.dotaznik.destinations.ResetScreenDestination
import cz.regulus.dotaznik.strings.strings
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.Normalizer

@Composable
@Destination
@RootNavGraph(start = true)
fun DotaznikScreen(
    navigator: DestinationsNavigator,
) {
    val viewModel = koinViewModel<DotaznikViewModel> {
        parametersOf({
            navigator.navigate(ResetScreenDestination)
        })
    }

    val stranky by viewModel.stranky.collectAsStateWithLifecycle()
    val firmy by viewModel.firmy.collectAsStateWithLifecycle()
    val prihlasen by viewModel.prihlasen.collectAsStateWithLifecycle()
    val odesilaniState by viewModel.odesilaniState.collectAsStateWithLifecycle()
    val poprve by viewModel.poprve.collectAsStateWithLifecycle()

    if (poprve && prihlasen != null) AlertDialog(
        onDismissRequest = {
            viewModel.podruhe()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.podruhe()
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

    Dotaznik(
        stranky = stranky,
        firmy = firmy,
        upravitStranky = viewModel::upravitStranky,
        odhlasit = viewModel::odhlasit,
        uzivatel = prihlasen,
        odesilaniState = odesilaniState,
        zmenitState = viewModel::zmenitState,
        jitNaFotky = {
            navigator.navigate(FotkyScreenDestination)
        },
        odstranitVse = viewModel::odstranitVse,
        debug = viewModel.debug
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Dotaznik(
    stranky: Stranky?,
    firmy: List<Firma>,
    upravitStranky: (Stranky) -> Unit,
    odhlasit: () -> Unit,
    uzivatel: Uzivatel?,
    odesilaniState: OdesilaniState,
    zmenitState: (positive: Boolean) -> Unit,
    jitNaFotky: () -> Unit,
    odstranitVse: () -> Unit,
    debug: Boolean,
) = with(stranky.orEmpty()) {
    val pagerState = rememberPagerState(pageCount = {
        (stranky.orEmpty()).vse.size
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
                    if (uzivatel == null) {
                        Text(strings.nejstePrihlaseni, Modifier.padding(all = 8.dp))
                    } else {
                        Text(
                            text = strings.menuVybranyJmenoPrijmeni(uzivatel.jmeno, uzivatel.prijmeni),
                            Modifier
                                .padding(horizontal = 8.dp)
                                .padding(top = 8.dp)
                        )
                        Text(
                            text = strings.menuVybranyEmail(uzivatel.email),
                            Modifier
                                .padding(horizontal = 8.dp)
                                .padding(top = 8.dp)
                        )
                        if (uzivatel.ico.isNotBlank()) Text(
                            text = strings.menuVybranyIco(uzivatel.ico),
                            Modifier
                                .padding(horizontal = 8.dp)
                                .padding(top = 8.dp)
                        )
                        Text(
                            text = strings.menuVybranyKod(uzivatel.cisloKo),
                            Modifier
                                .padding(horizontal = 8.dp)
                                .padding(vertical = 8.dp)
                        )

                        HorizontalDivider(Modifier.fillMaxWidth())

                        (stranky.orEmpty()).vse.forEachIndexed { i, stranka ->
                            with(stranky.orEmpty()) {
                                NavigationDrawerItem(
                                    label = {
                                        Text(stranka.nazev)
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
                                        Icon(stranka.icon, stranka.nazev)
                                    },
                                )
                            }
                        }

                        HorizontalDivider(Modifier.fillMaxWidth())

                        val posun = remember {
                            Animatable(0F)
                        }

                        NavigationDrawerItem(
                            label = {
                                Text(
                                    strings.odeslat,
                                    Modifier.padding(ButtonDefaults.ButtonWithIconContentPadding)
                                )
                            },
                            selected = false,
                            onClick = {
                                scope.launch {
                                    posun.animateTo(2500F, FloatTweenSpec(400, 0, FastOutLinearInEasing))
                                    drawerState.close()
                                    posun.snapTo(0F)
                                }
                                zmenitState(true)
                            },
                            Modifier.padding(all = 8.dp),
                            icon = {
                                Icon(Icons.AutoMirrored.Default.Send, null, Modifier.offset {
                                    IntOffset(x = posun.value.toDp().value.toInt(), y = 0)
                                })
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = MaterialTheme.colorScheme.primary,
                                unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        )

                        OutlinedButton(
                            onClick = {
                                odstranitVse()
                            },
                            Modifier.padding(all = 8.dp),
                        ) {
                            Text(strings.odstranitVse)
                        }

                        TextButton(
                            onClick = {
                                odhlasit()
                            },
                            Modifier.padding(all = 8.dp),
                        ) {
                            Text(strings.odhlasitSe)
                        }

                        Text(
                            "Verze: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                            Modifier.padding(8.dp),
                            fontSize = 12.sp
                        )
                        if (debug) Text(
                            "Toto je DEBUG verze aplikace. Žádný email nebude odeslán firmě Regulus, pouze Vám na email specifikovaný výše.",
                            Modifier.padding(8.dp),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        },
        drawerState = drawerState,
    ) {
        when (odesilaniState) {
            OdesilaniState.Nic -> Unit
            is OdesilaniState.OpravduOdeslat -> AlertDialog(
                onDismissRequest = {
                    zmenitState(false)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            zmenitState(true)
                        }
                    ) {
                        Text(strings.ano)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            zmenitState(false)
                        }
                    ) {
                        Text(strings.ne)
                    }
                },
                title = {
                    Text(text = strings.exportChceteOdeslat)
                },
                icon = {
                    Icon(Icons.AutoMirrored.Default.Send, null)
                },
                text = {
                    Text(text = strings.exportOpravduChceteOdeslatNa(odesilaniState.email))
                },
            )

            OdesilaniState.Odesilani -> AlertDialog(
                onDismissRequest = {},
                confirmButton = {},
                dismissButton = {},
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator()
                        Text(text = strings.exportOdesilani, Modifier.padding(8.dp))
                    }
                },
            )

            OdesilaniState.Uspech -> AlertDialog(
                onDismissRequest = {
                    zmenitState(true)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            zmenitState(true)
                        }
                    ) {
                        Text(strings.ok)
                    }
                },
                title = {
                    Text(text = strings.exportEmailUspesneOdeslan)
                },
                icon = {
                    Icon(Icons.Default.Check, null)
                },
            )

            OdesilaniState.OdstranitData -> AlertDialog(
                onDismissRequest = {
                    zmenitState(false)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            zmenitState(true)
                        }
                    ) {
                        Text(strings.ano)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            zmenitState(false)
                        }
                    ) {
                        Text(strings.ne)
                    }
                },
                title = {
                    Text(text = strings.odstranitVse)
                },
                icon = {
                    Icon(Icons.Default.DeleteForever, null)
                },
                text = {
                    Text(text = strings.exportOpravduOdstranitData)
                },
            )

            OdesilaniState.Error.Offline -> AlertDialog(
                onDismissRequest = {
                    zmenitState(false)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            zmenitState(false)
                        }
                    ) {
                        Text(strings.ok)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            zmenitState(true)
                        }
                    ) {
                        Text(strings.podrobnejsiInfo)
                    }
                },
                title = {
                    Text(text = strings.exportEmailNeodeslan(""))
                },
                icon = {
                    Icon(Icons.Default.WifiOff, null)
                },
                text = {
                    Text(text = strings.exportNejstePripojeni)
                },
            )

            is OdesilaniState.Error.Podrobne -> AlertDialog(
                onDismissRequest = {
                    zmenitState(false)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            zmenitState(false)
                        }
                    ) {
                        Text(strings.ok)
                    }
                },
                title = {
                    Text(text = strings.exportEmailNeodeslan(strings.totoJeChyba))
                },
                icon = {
                    Icon(Icons.Default.ErrorOutline, null)
                },
                text = {
                    Text(text = odesilaniState.error)
                },
            )
        }

        val aktualniStranka = remember(pagerState.currentPage, stranky) { stranky?.vse?.get(pagerState.currentPage) }
        if (aktualniStranka != null && stranky != null) Scaffold(
            Modifier
                .imePadding()
                .navigationBarsPadding(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(aktualniStranka.nazev)
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
                                if (drawerState.isOpen) strings.zavrit else strings.otevrit
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                jitNaFotky()
                            }
                        ) {
                            Icon(Icons.Default.PhotoLibrary, strings.fotkySpravaFotek)
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
                key = { stranky.vse[it].nazev }
            ) { i ->
                Scaffold(
                    floatingActionButton = {
                        val posun = remember {
                            Animatable(0F)
                        }
                        if (i == stranky.vse.lastIndex) {
                            FloatingActionButton(
                                onClick = {
                                    scope.launch {
                                        posun.animateTo(500F, FloatTweenSpec(800, 0, AnticipateOvershootInterpolator().toEasing()))
                                        pagerState.animateScrollToPage(0)
                                        posun.snapTo(0F)
                                    }
                                    zmenitState(true)
                                },
                                Modifier
                                    .offset {
                                        IntOffset(x = posun.value.toDp().value.toInt(), y = 0)
                                    }
                                    .padding(bottom = OutlinedTextFieldDefaults.MinHeight),
                            ) {
                                Icon(Icons.AutoMirrored.Default.Send, strings.odeslat)
                            }
                        }
                    },
                ) { _ ->
                    Column(
                        Modifier
                            .fillMaxSize()
                    ) {
                        val stranka by remember(stranky.vse, i) {
                            derivedStateOf {
                                stranky.vse[i]
                            }
                        }
                        Column(
                            Modifier
                                .weight(1F)
                                .verticalScroll(rememberScrollState())
                        ) {
                            stranka.veci.dropLast(1).forEachIndexed { i, veci ->
                                veci.forEach { vec ->
                                    Surface(
                                        Modifier.fillMaxWidth(),
                                        color = if (i % 2 == 0) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
                                    ) {
                                        Vec(
                                            stranky = stranky,
                                            firmy = firmy,
                                            vec = vec,
                                            upravitVec = { novaVec ->
                                                upravitStranky(stranky.kopirovatStranku(stranka.kopirovatVec(novaVec)))
                                            },
                                        )
                                    }
                                }
                            }
                        }

                        Vec(
                            stranky = stranky,
                            firmy = firmy,
                            vec = stranka.veci.last().last(),
                            upravitVec = { novaVec ->
                                upravitStranky(stranky.kopirovatStranku(stranka.kopirovatVec(novaVec)))
                            },
                        )
                    }
                }
            }
        }
        else LinearProgressIndicator()
    }
}

private fun Stranky?.orEmpty() = this ?: Stranky()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Vec(
    stranky: Stranky,
    firmy: List<Firma>,
    vec: Stranky.Stranka.Vec,
    upravitVec: (Stranky.Stranka.Vec) -> Unit,
) = with(stranky) {
    val zobrazit = remember(stranky) { with(stranky) { vec.zobrazit } }
    val focusManager = LocalFocusManager.current
    if (zobrazit) when (vec) {
        is Stranky.Stranka.Vec.Nadpis -> {
            Text(
                text = vec.text,
                Modifier.padding(all = 8.dp),
                style = MaterialTheme.typography.headlineSmall,
            )
        }

        is Stranky.Stranka.Vec.Pisatko -> {
            var text by remember { mutableStateOf(vec.textOrDefault) }
            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                    upravitVec(vec.text(it))
                },
                Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
                label = {
                    Text(vec.popis)
                },
                trailingIcon = {
                    Text(text = vec.suffix)
                },
                singleLine = true,
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Down)
                },
                keyboardOptions = vec.klavesnice,
            )
        }

        is Stranky.Stranka.Vec.PisatkoSJednotkama -> {
            var text by remember { mutableStateOf(vec.textOrDefault) }
            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                    upravitVec(vec.text(it))
                },
                Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
                label = {
                    Text(vec.popis)
                },
                trailingIcon = {
                    var expanded by remember { mutableStateOf(false) }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        },
                        Modifier,
                    ) {
                        vec.jednotky.forEachIndexed { i, it ->
                            DropdownMenuItem(
                                text = {
                                    Text(it)
                                },
                                onClick = {
                                    upravitVec(vec.vybraneJednotkyIndex(i))
                                    expanded = false
                                }
                            )
                        }
                    }
                    Surface(
                        onClick = {
                            expanded = true
                        },
                        Modifier.padding(all = 4.dp),
                        shape = CircleShape,
                        color = Color.Unspecified,
                    ) {
                        Row(
                            Modifier,
                        ) {
                            Text(vec.vybraneJednotkyOrDefault)
                            Icon(Icons.Default.ArrowDropDown, strings.vybratJednotky)
                        }
                    }
                },
                singleLine = true,
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Down)
                },
                keyboardOptions = vec.klavesnice,
            )
        }

        is Stranky.Stranka.Vec.Vybiratko -> {
            var expanded by rememberSaveable { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                Modifier
                    .fillMaxWidth(),
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(top = 0.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
                    readOnly = true,
                    value = vec.vybranoOrDefault,
                    onValueChange = {},
                    label = { Text(vec.popis) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    keyboardActions = KeyboardActions {
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                        focusManager.clearFocus()
                    },
                ) {
                    vec.moznosti.forEachIndexed { i, moznost ->
                        DropdownMenuItem(
                            text = { Text(moznost) },
                            onClick = {
                                upravitVec(vec.vybranoIndex(i))
                                expanded = false
                                focusManager.clearFocus()
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }

        is Stranky.Stranka.Vec.DvojVybiratko -> Row(
            Modifier.fillMaxWidth()
        ) {
            var expanded1 by rememberSaveable { mutableStateOf(false) }
            var expanded2 by rememberSaveable { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded1,
                onExpandedChange = { expanded1 = !expanded1 },
                Modifier
                    .weight(1F),
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(top = 0.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
                    readOnly = true,
                    value = vec.vybrano1OrDefault,
                    onValueChange = {},
                    label = { Text(vec.popis) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded1) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    keyboardActions = KeyboardActions {
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                )
                ExposedDropdownMenu(
                    expanded = expanded1,
                    onDismissRequest = {
                        expanded1 = false
                        focusManager.clearFocus()
                    },
                ) {
                    vec.moznosti1.forEachIndexed { i, moznost ->
                        DropdownMenuItem(
                            text = { Text(moznost) },
                            onClick = {
                                upravitVec(vec.vybrano1Index(i))
                                expanded1 = false
                                focusManager.clearFocus()
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
            if (vec.moznosti2.isNotEmpty()) ExposedDropdownMenuBox(
                expanded = expanded2,
                onExpandedChange = { expanded2 = !expanded2 },
                Modifier
                    .weight(1F),
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(top = 0.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
                    readOnly = true,
                    value = vec.vybrano2OrDefault,
                    onValueChange = {},
                    label = { Text(vec.popis) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded2) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    keyboardActions = KeyboardActions {
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                )
                ExposedDropdownMenu(
                    expanded = expanded2,
                    onDismissRequest = {
                        expanded2 = false
                        focusManager.clearFocus()
                    },
                ) {
                    vec.moznosti2.forEachIndexed { i, moznost ->
                        DropdownMenuItem(
                            text = { Text(moznost) },
                            onClick = {
                                upravitVec(vec.vybrano2Index(i))
                                expanded2 = false
                                focusManager.clearFocus()
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }

        is Stranky.Stranka.Vec.Zaskrtavatko -> {
            Surface(
                onClick = {
                    upravitVec(vec.zaskrtnuto(!vec.zaskrtnutoOrDefault))
                },
                Modifier.padding(top = 0.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
                shape = CircleShape,
                color = Color.Unspecified,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = vec.zaskrtnutoOrDefault,
                        onCheckedChange = {
                            upravitVec(vec.zaskrtnuto(!vec.zaskrtnutoOrDefault))
                        },
                    )
                    Text(text = vec.popis)
                }
            }
        }

        is Stranky.Stranka.Vec.ZaskrtavatkoSVybiratkem -> Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 0.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = vec.zaskrtnutoOrDefault,
                onCheckedChange = {
                    upravitVec(vec.zaskrtnuto(it))
                },
            )
            var expanded by rememberSaveable { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded && vec.zaskrtnutoOrDefault,
                onExpandedChange = {
                    expanded = !expanded
                },
                Modifier
                    .fillMaxWidth(),
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(top = 0.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
                    readOnly = true,
                    value = vec.vybranoOrDefault,
                    onValueChange = {},
                    label = { Text(vec.popis) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    keyboardActions = KeyboardActions {
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                        focusManager.clearFocus()
                    },
                ) {
                    vec.moznosti.forEachIndexed { i, moznost ->
                        DropdownMenuItem(
                            text = { Text(moznost) },
                            onClick = {
                                upravitVec(vec.vybranoIndex(i))
                                expanded = false
                                focusManager.clearFocus()
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }

        is Stranky.Stranka.Kontakty.MontazniFirma -> {

            var expanded by rememberSaveable { mutableStateOf(false) }
            val vybranaFirma by remember(vec) {
                derivedStateOf {
                    firmy.find { it.ico == vec.ico }
                }
            }
            var text by remember { mutableStateOf(TextFieldValue(vec.ico)) }
            val filtrovaneFirmy by remember(firmy, text) {
                derivedStateOf {
                    firmy.filter { firma ->
                        text.text.upravit().split(" ").all { slovoTextu ->
                            firma.jmeno.upravit().split(" ").any { slovoFirmy ->
                                slovoFirmy.startsWith(slovoTextu)
                            }
                        } || firma.ico.startsWith(text.text)
                    }
                }
            }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },
                Modifier
                    .fillMaxWidth(),
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .padding(top = 0.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
                    value = text,
                    onValueChange = {
                        text = it
                        expanded = it.text.toIntOrNull()?.toString()?.length != 8
                        if (it.text.toIntOrNull()?.toString()?.length == 8)
                            upravitVec(vec.ico(it.text))
                        else
                            upravitVec(vec.ico(""))
                    },
                    label = { Text(strings.kontaktyIco) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    supportingText = {
                        if (text.text.isBlank()) {
                            Text(text = "Můžete zadat název firmy pro vyhledávání")
                        } else if (vybranaFirma != null) {
                            Text(text = "Detekováno: ${vybranaFirma!!.jmeno}")
                        } else if (text.text.toIntOrNull()?.toString()?.length != 8) {
                            Text(text = "Pozor! Nejedná se o IČO, hodnota nebude uložena")
                        } else {
                            Text(text = "Validní IČO")
                        }
                    },
                    singleLine = true,
                    keyboardActions = KeyboardActions {
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                    ),
                    isError = text.text.toIntOrNull()?.toString()?.length != 8 && text.text.isNotBlank()
                )
                ExposedDropdownMenu(
                    expanded = expanded && filtrovaneFirmy.isNotEmpty(),
                    onDismissRequest = { expanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(strings.kontaktyVyberteMontazniFirmu) },
                        onClick = {},
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        enabled = false,
                    )
                    filtrovaneFirmy
                        .forEach { moznost ->
                            DropdownMenuItem(
                                text = { Text("${moznost.jmeno} - ${moznost.ico}") },
                                onClick = {
                                    text = TextFieldValue(
                                        text = moznost.ico,
                                        selection = TextRange(8)
                                    )
                                    upravitVec(vec.ico(moznost.ico))
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                }
            }
        }
    }
}

fun TimeInterpolator.toEasing() = Easing { x -> getInterpolation(x) }

private fun String.normalize() = Normalizer.normalize(this, Normalizer.Form.NFD)!!

private fun String.upravit() = uppercase().normalize().replace("\\p{Mn}+".toRegex(), "")