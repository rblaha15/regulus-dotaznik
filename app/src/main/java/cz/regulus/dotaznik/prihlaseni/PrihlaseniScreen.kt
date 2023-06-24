package cz.regulus.dotaznik.prihlaseni

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import cz.regulus.dotaznik.Clovek
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun Prihlaseni(
    zastupci: List<Clovek>,
    zamestnanci: List<Clovek>,
    zamestanec: Boolean,
    zmenitJsemZamestnanec: (Boolean) -> Unit,
    novyClovek: Clovek?,
    upravitCloveka: ((Clovek?) -> Clovek?) -> Unit,
    potvrdit: (chyba: (Int) -> Unit) -> Unit,
    zrusit: () -> Unit,
) {
    val snackbarState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Regulus dotazník")
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarState)
        },
    ) { paddingValues ->
        if (zamestnanci.isEmpty()) LinearProgressIndicator(
            Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        )
        else Column(
            Modifier
                .padding(paddingValues)
                .padding(all = 16.dp)
                .fillMaxSize()
                .imePadding(),
        ) {
            val resources = LocalContext.current.resources
            val scope = rememberCoroutineScope()
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1F)
                    .fillMaxWidth(),
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
                        Text(text = "Jsem zaměstnanec Regulusu")
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
                        Text(text = "Nejsem zaměstnanec Regulusu")
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
                            label = { Text("Vyberte se") },
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
                                            zamestnanci.find { it.celeJmeno == moznost } ?: return@upravitCloveka clovek
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
                        Text(text = "Jméno a příjmení: ${novyClovek.celeJmeno}", Modifier.padding(top = 8.dp))
                        Text(text = "Číslo KO: ${novyClovek.cislo}")
                        Text(text = "Email: ${novyClovek.email}")
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
                            label = { Text("Váš obchodní zástupce") },
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
                                            zamestnanci.find { it.celeJmeno == moznost }?.copy(jmeno = "", prijmeni = "", email = "")
                                                ?: clovek
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
                        Text(text = "Doplňte, prosím, ještě nějaké informace o Vás:", Modifier.padding(top = 8.dp))
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
                                Text(text = "Vaše jméno")
                            },
                            keyboardActions = KeyboardActions {
                                focusManager.moveFocus(FocusDirection.Down)
                            },
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Text,
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
                                Text(text = "Vaše příjmení")
                            },
                            keyboardActions = KeyboardActions {
                                focusManager.moveFocus(FocusDirection.Down)
                            },
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Text,
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
                                Text(text = "Váš email")
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
                                Text(text = "Vaše IČO (nepovinné)")
                            },
                            keyboardActions = KeyboardActions {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                                potvrdit {
                                    scope.launch {
                                        snackbarState.showSnackbar(resources.getString(it))
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
            Row(
                Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = {
                        zrusit()
                    }
                ) {
                    Text(text = "Zrušit")
                }
                Spacer(Modifier.weight(1F))
                Button(
                    onClick = {
                        potvrdit {
                            scope.launch {
                                snackbarState.showSnackbar(resources.getString(it))
                            }
                        }
                    }
                ) {
                    Text(text = "OK")
                }
            }
        }
    }
}