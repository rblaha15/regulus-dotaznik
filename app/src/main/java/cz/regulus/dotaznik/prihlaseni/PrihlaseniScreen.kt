package cz.regulus.dotaznik.prihlaseni

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import cz.regulus.dotaznik.Uzivatel
import cz.regulus.dotaznik.strings.strings
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Destination
@Composable
fun PrihlaseniSceen(
    navigator: DestinationsNavigator,
) {

    val viewModel = koinViewModel<PrihlaseniViewModel> {
        parametersOf(navigator::navigateUp)
    }

    val zastupci by viewModel.zastupci.collectAsStateWithLifecycle()
    val zamestanci by viewModel.zamestnanci.collectAsStateWithLifecycle()
    val novyClovek by viewModel.novyClovek.collectAsStateWithLifecycle()
    val zamestanec by viewModel.jeZamestnanec.collectAsStateWithLifecycle()

    Prihlaseni(
        zastupci = zastupci,
        zamestnanci = zamestanci,
        zamestanec = zamestanec,
        zmenitJsemZamestnanec = viewModel::zmenitZdaJeZamestnanec,
        novyClovek = novyClovek,
        upravitCloveka = viewModel::upravitNovehoCloveka,
        potvrdit = viewModel::potvrdit,
        zrusit = viewModel::zrusit,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Prihlaseni(
    zastupci: List<Zamestnanec>,
    zamestnanci: List<Zamestnanec>,
    zamestanec: Boolean,
    zmenitJsemZamestnanec: (Boolean) -> Unit,
    novyClovek: Uzivatel?,
    upravitCloveka: ((Uzivatel?) -> Uzivatel?) -> Unit,
    potvrdit: (chyba: (String) -> Unit) -> Unit,
    zrusit: () -> Unit,
) {
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(
        Modifier
            .imePadding()
            .navigationBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(strings.appName)
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarState)
        },
        bottomBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            ) {
                TextButton(
                    onClick = {
                        zrusit()
                    }
                ) {
                    Text(text = strings.zrusit)
                }
                Spacer(Modifier.weight(1F))
                Button(
                    onClick = {
                        potvrdit {
                            scope.launch {
                                snackbarState.showSnackbar(it)
                            }
                        }
                    }
                ) {
                    Text(text = strings.ok)
                }
            }
        }
    ) { paddingValues ->
        if (zamestnanci.isEmpty()) LinearProgressIndicator(
            Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        )
        else Column(
            Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                onClick = {
                    zmenitJsemZamestnanec(true)
                },
                shape = CircleShape,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = zamestanec,
                        onClick = {
                            zmenitJsemZamestnanec(true)
                        },
                    )
                    Text(text = strings.prihlaseniJsemZamestanec)
                }
            }
            Surface(
                onClick = {
                    zmenitJsemZamestnanec(false)
                },
                shape = CircleShape,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = !zamestanec,
                        onClick = {
                            zmenitJsemZamestnanec(false)
                        },
                    )
                    Text(text = strings.prihlaseniNejsemZamestanec)
                }
            }
            if (zamestanec) {
                val seznam = remember {
                    listOf("") + zamestnanci.map { it.celeJmeno }
                }
                var expanded by rememberSaveable { mutableStateOf(false) }
                var vybrano by rememberSaveable { mutableStateOf(seznam.first()) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    Modifier.padding(top = 8.dp),
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor(),
                        readOnly = true,
                        value = vybrano,
                        onValueChange = {},
                        label = { Text(strings.prihlaseniVyberSe) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        seznam.drop(1).forEach { moznost ->
                            DropdownMenuItem(
                                text = { Text(moznost) },
                                onClick = {
                                    upravitCloveka { clovek ->
                                        zamestnanci.find { it.celeJmeno == moznost }?.vytvoritUzivatele(true) ?: clovek
                                    }
                                    vybrano = moznost
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
                if (novyClovek != null) {
                    Text(
                        text = strings.prihlaseniVybranyJmenoPrijmeni(novyClovek.jmeno, novyClovek.prijmeni),
                        Modifier.padding(top = 8.dp)
                    )
                    Text(text = strings.prihlaseniVybranyKod(novyClovek.cisloKo))
                    Text(text = strings.prihlaseniVybranyEmail(novyClovek.email))
                }
            } else {
                val seznam = remember {
                    listOf("") + zastupci.map { it.celeJmeno }
                }
                var expanded by rememberSaveable { mutableStateOf(false) }
                var vybrano by rememberSaveable { mutableStateOf(seznam.first()) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    Modifier.padding(top = 8.dp),
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor(),
                        readOnly = true,
                        value = vybrano,
                        onValueChange = {},
                        label = { Text(strings.prihlaseniVasZastupce) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        seznam.drop(1).forEach { moznost ->
                            DropdownMenuItem(
                                text = { Text(moznost) },
                                onClick = {
                                    upravitCloveka { clovek ->
                                        zamestnanci.find { it.celeJmeno == moznost }?.vytvoritUzivatele(false) ?: clovek
                                    }
                                    vybrano = moznost
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
                if (novyClovek != null) {
                    Text(text = strings.prihlaseniDoplnitInfo, Modifier.padding(top = 8.dp))
                    val focusManager = LocalFocusManager.current
                    OutlinedTextField(
                        value = novyClovek.jmeno,
                        onValueChange = {
                            upravitCloveka { clovek ->
                                clovek?.copy(jmeno = it)
                            }
                        },
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        label = {
                            Text(text = strings.prihlaseniVaseJmeno)
                        },
                        keyboardActions = KeyboardActions {
                            focusManager.moveFocus(FocusDirection.Down)
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Words,
                        ),
                    )
                    OutlinedTextField(
                        value = novyClovek.prijmeni,
                        onValueChange = {
                            upravitCloveka { clovek ->
                                clovek?.copy(prijmeni = it)
                            }
                        },
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        label = {
                            Text(text = strings.prihlaseniVasePrijmeni)
                        },
                        keyboardActions = KeyboardActions {
                            focusManager.moveFocus(FocusDirection.Down)
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Words,
                        ),
                    )
                    OutlinedTextField(
                        value = novyClovek.email,
                        onValueChange = {
                            upravitCloveka { clovek ->
                                clovek?.copy(email = it)
                            }
                        },
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        label = {
                            Text(text = strings.prihlaseniVasEmail)
                        },
                        keyboardActions = KeyboardActions {
                            focusManager.moveFocus(FocusDirection.Down)
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Email,
                        ),
                    )
                    val keyboardController = LocalSoftwareKeyboardController.current
                    OutlinedTextField(
                        value = novyClovek.ico,
                        onValueChange = {
                            if (it.isBlank()) return@OutlinedTextField
                            it.toIntOrNull() ?: return@OutlinedTextField
                            upravitCloveka { clovek ->
                                clovek?.copy(ico = it.substring(0, it.length.coerceAtMost(8)))
                            }
                        },
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        label = {
                            Text(text = strings.prihlaseniVaseIco)
                        },
                        keyboardActions = KeyboardActions {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            potvrdit {
                                scope.launch {
                                    snackbarState.showSnackbar(it)
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Number,
                        ),
                    )
                }
            }
        }
    }
}