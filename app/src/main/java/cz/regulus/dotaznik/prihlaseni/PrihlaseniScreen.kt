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
import cz.regulus.dotaznik.User
import cz.regulus.dotaznik.strings.strings
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Destination
@Composable
fun LogInScreen(
    navigator: DestinationsNavigator,
) {
    val viewModel = koinViewModel<PrihlaseniViewModel> {
        parametersOf(navigator::navigateUp)
    }

    val representatives by viewModel.representatives.collectAsStateWithLifecycle()
    val employees by viewModel.employees.collectAsStateWithLifecycle()
    val newUser by viewModel.newUser.collectAsStateWithLifecycle()
    val isEmployee by viewModel.isEmployee.collectAsStateWithLifecycle()

    LogIn(
        representatives = representatives,
        employees = employees,
        isEmployee = isEmployee,
        changeBeingEmployee = viewModel::changeBeingEmployee,
        newUser = newUser,
        editUser = viewModel::editNewUser,
        confirm = viewModel::confirm,
        cancel = viewModel::cancel,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogIn(
    representatives: List<Employee>,
    employees: List<Employee>,
    isEmployee: Boolean,
    changeBeingEmployee: (Boolean) -> Unit,
    newUser: User?,
    editUser: ((User?) -> User?) -> Unit,
    confirm: (chyba: (String) -> Unit) -> Unit,
    cancel: () -> Unit,
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
                        cancel()
                    }
                ) {
                    Text(text = strings.cancel)
                }
                Spacer(Modifier.weight(1F))
                Button(
                    onClick = {
                        confirm {
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
        if (employees.isEmpty()) LinearProgressIndicator(
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
                    changeBeingEmployee(true)
                },
                shape = CircleShape,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isEmployee,
                        onClick = {
                            changeBeingEmployee(true)
                        },
                    )
                    Text(text = strings.logIn.iAmEmploee)
                }
            }
            Surface(
                onClick = {
                    changeBeingEmployee(false)
                },
                shape = CircleShape,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = !isEmployee,
                        onClick = {
                            changeBeingEmployee(false)
                        },
                    )
                    Text(text = strings.logIn.iAmNotEmploee)
                }
            }
            if (isEmployee) {
                val list = remember {
                    listOf("") + employees.map { it.wholeName }
                }
                var expanded by rememberSaveable { mutableStateOf(false) }
                var chosen by rememberSaveable { mutableStateOf(list.first()) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    Modifier.padding(top = 8.dp),
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor(),
                        readOnly = true,
                        value = chosen,
                        onValueChange = {},
                        label = { Text(strings.logIn.chooseYourself) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        list.drop(1).forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    editUser { user ->
                                        employees.find { it.wholeName == option }?.createUser(true) ?: user
                                    }
                                    chosen = option
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
                if (newUser != null) {
                    Text(
                        text = strings.logIn.selectedFullName(newUser.name, newUser.surname),
                        Modifier.padding(top = 8.dp)
                    )
                    Text(text = strings.logIn.selectedCode(newUser.koNumber))
                    Text(text = strings.logIn.selectedEmail(newUser.email))
                }
            } else {
                val list = remember {
                    listOf("") + representatives.map { it.wholeName }
                }
                var expanded by rememberSaveable { mutableStateOf(false) }
                var chosen by rememberSaveable { mutableStateOf(list.first()) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    Modifier.padding(top = 8.dp),
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor(),
                        readOnly = true,
                        value = chosen,
                        onValueChange = {},
                        label = { Text(strings.logIn.yourRepresentative) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        list.drop(1).forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    editUser { user ->
                                        employees.find { it.wholeName == option }?.createUser(false) ?: user
                                    }
                                    chosen = option
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
                if (newUser != null) {
                    Text(text = strings.logIn.addMoreInfo, Modifier.padding(top = 8.dp))
                    val focusManager = LocalFocusManager.current
                    OutlinedTextField(
                        value = newUser.name,
                        onValueChange = {
                            editUser { user ->
                                user?.copy(name = it)
                            }
                        },
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        label = {
                            Text(text = strings.logIn.yourName)
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
                        value = newUser.surname,
                        onValueChange = {
                            editUser { edit ->
                                edit?.copy(surname = it)
                            }
                        },
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        label = {
                            Text(text = strings.logIn.yourSurname)
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
                        value = newUser.email,
                        onValueChange = {
                            editUser { user ->
                                user?.copy(email = it)
                            }
                        },
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        label = {
                            Text(text = strings.logIn.yourEmail)
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
                        value = newUser.crn,
                        onValueChange = {
                            if (it.isBlank()) return@OutlinedTextField
                            it.toIntOrNull() ?: return@OutlinedTextField
                            editUser { user ->
                                user?.copy(crn = it.substring(0, it.length.coerceAtMost(8)))
                            }
                        },
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        label = {
                            Text(text = strings.logIn.yourCrn)
                        },
                        keyboardActions = KeyboardActions {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            confirm {
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