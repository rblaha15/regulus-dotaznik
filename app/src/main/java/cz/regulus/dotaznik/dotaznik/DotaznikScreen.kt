package cz.regulus.dotaznik.dotaznik

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import cz.regulus.dotaznik.Stranky
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
@Destination
@RootNavGraph(start = true)
fun DotaznikScreen() {
    val viewModel = koinViewModel<DotaznikViewModel>()

    val stranky by viewModel.stranky.collectAsStateWithLifecycle()

    Dotaznik(
        stranky = stranky,
        upravitStranky = viewModel::upravitStranky,
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Dotaznik(
    stranky: Stranky,
    upravitStranky: (Stranky) -> Unit,
) {
    val pagerState = rememberPagerState()
    val drawerState = rememberDrawerState(DrawerValue.Open)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                stranky.vse.forEachIndexed { i, stranka ->
                    NavigationDrawerItem(
                        label = {
                            Text(stranka.nazev.asString())
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
                            Icon(stranka.icon, stranka.nazev.asString())
                        },
                    )
                }
            }
        },
        drawerState = drawerState,
    ) {
        val aktualniStranka = remember(pagerState.currentPage) { stranky.vse[pagerState.currentPage] }
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(aktualniStranka.nazev.asString())
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(Icons.Default.Menu, "Otevřít menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            HorizontalPager(
                pageCount = stranky.vse.size,
                Modifier.padding(paddingValues)
                    .imePadding(),
                state = pagerState
            ) { i ->
                Stranka(
                    stranka = stranky.vse[i],
                    upravitStranku = { novaStranka ->
                        upravitStranky(stranky.kopirovatStranku(novaStranka))
                    }
                )
            }
        }
    }
}

@Composable
fun Stranka(
    stranka: Stranky.Stranka,
    upravitStranku: (Stranky.Stranka) -> Unit,
) = Column(
    Modifier
        .fillMaxSize()
) {
    LazyColumn(
        Modifier
            .weight(1F),
    ) {
        stranka.veci.dropLast(1).forEachIndexed { i, veci ->
            items(veci) {vec ->
                Surface(
                    Modifier.fillMaxWidth(),
                    color = if (i % 2 == 0) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    Vec(
                        stranka = stranka,
                        vec = vec,
                        upravitVec = { novaVec ->
                            upravitStranku(stranka.kopirovatVec(novaVec))
                        },
                    )
                }
            }
        }
    }
    Vec(
        stranka = stranka,
        vec = stranka.veci.last().last(),
        upravitVec = { novaVec ->
            upravitStranku(stranka.kopirovatVec(novaVec))
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Vec(
    stranka: Stranky.Stranka,
    vec: Stranky.Stranka.Vec,
    upravitVec: (Stranky.Stranka.Vec) -> Unit,
) {
    val zobrazit = remember(stranka) { vec.zobrazit(stranka) }
    val focusManager = LocalFocusManager.current
    if (zobrazit) when (vec) {
        is Stranky.Stranka.Vec.Nadpis -> {
            Text(
                text = vec.text.asString(),
                Modifier.padding(all = 8.dp),
                style = MaterialTheme.typography.headlineSmall,
            )
        }

        is Stranky.Stranka.Vec.Pisatko -> {
            var text by remember { mutableStateOf(vec.text) }
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
                    Text(vec.popis.asString())
                },
                trailingIcon = {
                    Text(text = vec.suffix.asString())
                },
                singleLine = true,
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Down)
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                ),
            )
        }

        is Stranky.Stranka.Vec.PisatkoSJednotkama -> {
            var text by remember { mutableStateOf(vec.text) }
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
                    Text(vec.popis.asString())
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
                        vec.jednotky.forEach {
                            DropdownMenuItem(
                                text = {
                                    Text(it.asString())
                                },
                                onClick = {
                                    upravitVec(vec.vybraneJednotky(it))
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
                            Text(vec.vybraneJednotky.asString())
                            Icon(Icons.Default.ArrowDropDown, "Vybrat jednotky")
                        }
                    }
                },
                singleLine = true,
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Down)
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                ),
            )
        }

        is Stranky.Stranka.Vec.Vybiratko -> {
            val seznam = remember(stranka) { vec.moznosti(stranka) }
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
                    value = vec.vybrano.asString(),
                    onValueChange = {},
                    label = { Text(vec.popis.asString()) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    seznam.forEach { moznost ->
                        DropdownMenuItem(
                            text = { Text(moznost.asString()) },
                            onClick = {
                                upravitVec(vec.vybrano(moznost))
                                expanded = false
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
            val seznam1 = remember(stranka) { vec.moznosti1(stranka) }
            val seznam2 = remember(stranka) { vec.moznosti2(stranka) }
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
                    value = vec.vybrano1.asString(),
                    onValueChange = {},
                    label = { Text(vec.popis.asString()) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded1) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = expanded1,
                    onDismissRequest = { expanded1 = false },
                ) {
                    seznam1.forEach { moznost ->
                        DropdownMenuItem(
                            text = { Text(moznost.asString()) },
                            onClick = {
                                upravitVec(vec.vybrano1(moznost))
                                expanded1 = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
            if (seznam2.isNotEmpty()) ExposedDropdownMenuBox(
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
                    value = vec.vybrano2.asString(),
                    onValueChange = {},
                    label = { Text(vec.popis.asString()) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded2) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = expanded2,
                    onDismissRequest = { expanded2 = false },
                ) {
                    seznam2.forEach { moznost ->
                        DropdownMenuItem(
                            text = { Text(moznost.asString()) },
                            onClick = {
                                upravitVec(vec.vybrano2(moznost))
                                expanded2 = false
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
                    upravitVec(vec.zaskrtnuto(!vec.zaskrtnuto))
                },
                Modifier.padding(top = 0.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
                shape = CircleShape,
                color = Color.Unspecified,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = vec.zaskrtnuto,
                        onCheckedChange = {
                            upravitVec(vec.zaskrtnuto(!vec.zaskrtnuto))
                        },
                    )
                    Text(text = vec.popis.asString())
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
                checked = vec.zaskrtnuto,
                onCheckedChange = {
                    upravitVec(vec.zaskrtnuto(it))
                },
            )
            val seznam = remember(stranka) { vec.moznosti(stranka) }
            var expanded by rememberSaveable { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded && vec.zaskrtnuto,
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
                    value = if (!vec.zaskrtnuto) "" else vec.vybrano.asString(),
                    onValueChange = {},
                    label = { Text(vec.popis.asString()) },
                    trailingIcon = { if (vec.zaskrtnuto) ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded && vec.zaskrtnuto) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    enabled = vec.zaskrtnuto,
                )
                ExposedDropdownMenu(
                    expanded = expanded && vec.zaskrtnuto,
                    onDismissRequest = { expanded = false },
                ) {
                    seznam.forEach { moznost ->
                        DropdownMenuItem(
                            text = { Text(moznost.asString()) },
                            onClick = {
                                upravitVec(vec.vybrano(moznost))
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }

        Stranky.Stranka.Vec.Jine.MontazniFirma -> {

        }
    }
}