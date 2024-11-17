package cz.regulus.dotaznik.dotaznik

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cz.regulus.dotaznik.dotaznik.Sites.Site.Widget.Companion.changeNumber
import cz.regulus.dotaznik.dotaznik.Sites.Site.Widget.Companion.getAmount
import cz.regulus.dotaznik.dotaznik.Sites.Site.Widget.Companion.getChecked
import cz.regulus.dotaznik.dotaznik.Sites.Site.Widget.Companion.getChosen
import cz.regulus.dotaznik.dotaznik.Sites.Site.Widget.Companion.getChosen2
import cz.regulus.dotaznik.dotaznik.Sites.Site.Widget.Companion.getChosenUnit
import cz.regulus.dotaznik.dotaznik.Sites.Site.Widget.Companion.getText
import cz.regulus.dotaznik.dotaznik.Sites.Site.Widget.Companion.getValuesWithAmounts
import cz.regulus.dotaznik.strings.strings

@Composable
fun Widget(
    sites: Sites,
    companies: List<Company>,
    widget: Sites.Site.Widget,
    editWidget: (Sites.Site.Widget) -> Unit,
) {
    val show = widget.showWidget(sites)
    if (show) when (widget) {
        is Sites.Site.Widget.HasTitle -> Title(widget, sites)
        is Sites.Site.Widget.TextField -> TextField(widget, sites, editWidget)
        is Sites.Site.Widget.TextFieldWithUnits -> TextFieldWithUnits(widget, sites, editWidget)
        is Sites.Site.Widget.Chooser -> Chooser(widget, editWidget, sites)
        is Sites.Site.Widget.DoubleChooser -> DoubleChooser(widget, editWidget, sites)
        is Sites.Site.Widget.CheckBox -> Checkbox(widget, sites, editWidget)
        is Sites.Site.Widget.CheckBoxWithChooser -> CheckboxWithChooser(widget, sites, editWidget)
        is Sites.Site.Widget.DropdownWithAmount -> DropdownWithAmount(widget, sites, editWidget)
        is Sites.Site.Contacts.AssemblyCompany -> AssemblyCompany(widget, companies, editWidget)
        else -> {}
    }
}

@Composable
private fun Title(
    widget: Sites.Site.Widget.HasTitle,
    sites: Sites,
) = Text(
    text = widget.getTitle(sites),
    Modifier.padding(all = 8.dp),
    style = MaterialTheme.typography.headlineSmall,
)

@Composable
private fun TextField(
    widget: Sites.Site.Widget.TextField,
    sites: Sites,
    editWidget: (Sites.Site.Widget) -> Unit,
) {
    var text by remember { mutableStateOf(widget.getText(sites)) }
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            editWidget(widget.changeText(it))
        },
        Modifier
            .fillMaxWidth()
            .padding(top = 0.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
        label = {
            Text(widget.getLabel(sites))
        },
        placeholder = { Text(widget.getPlaceholder(sites)) },
        trailingIcon = {
            Text(text = widget.getSuffix(sites))
        },
        singleLine = true,
        keyboardActions = KeyboardActions {
            focusManager.moveFocus(FocusDirection.Down)
        },
        keyboardOptions = widget.getKeyboard(sites),
    )
}

@Composable
private fun TextFieldWithUnits(
    widget: Sites.Site.Widget.TextFieldWithUnits,
    sites: Sites,
    editWidget: (Sites.Site.Widget) -> Unit,
) {
    var text by remember { mutableStateOf(widget.getText(sites)) }
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            editWidget(widget.changeText(it))
        },
        Modifier
            .fillMaxWidth()
            .padding(top = 0.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
        label = {
            Text(widget.getLabel(sites))
        },
        placeholder = { Text(widget.getPlaceholder(sites)) },
        trailingIcon = {
            var expanded by remember { mutableStateOf(false) }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                Modifier,
            ) {
                widget.getUnits(sites).forEachIndexed { i, it ->
                    DropdownMenuItem(
                        text = {
                            Text(it)
                        },
                        onClick = {
                            editWidget(widget.changeChosenUnitIndex(i))
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
                    Text(widget.getChosenUnit(sites))
                    Icon(Icons.Default.ArrowDropDown, strings.chooseUnits)
                }
            }
        },
        singleLine = true,
        keyboardActions = KeyboardActions {
            focusManager.moveFocus(FocusDirection.Down)
        },
        keyboardOptions = widget.getKeyboard(sites),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Chooser(
    widget: Sites.Site.Widget.Chooser,
    editWidget: (Sites.Site.Widget) -> Unit,
    sites: Sites,
) = CoreChooser(
    value = widget.getChosen(sites),
    options = widget.getOptions(sites),
    onClick = { i, _ ->
        editWidget(widget.changeChosenIndex(i))
    },
    Modifier
        .fillMaxWidth()
        .padding(top = 0.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
    label = widget.getLabel(sites),
    placeholder = widget.getPlaceholder(sites),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DoubleChooser(
    widget: Sites.Site.Widget.DoubleChooser,
    editWidget: (Sites.Site.Widget) -> Unit,
    sites: Sites,
) = Row(
    Modifier
        .fillMaxWidth()
        .padding(top = 0.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
) {
    CoreChooser(
        value = widget.getChosen(sites),
        options = widget.getOptions(sites),
        onClick = { i, _ ->
            editWidget(widget.changeChosenIndex(i))
        },
        Modifier
            .weight(1F),
        label = widget.getLabel(sites),
        placeholder = widget.getPlaceholder(sites),
    )
    if (widget.getOptions2(sites).isNotEmpty()) {
        CoreChooser(
            value = widget.getChosen2(sites),
            options = widget.getOptions2(sites),
            onClick = { i, _ ->
                editWidget(widget.changeChosenIndex2(i))
            },
            Modifier
                .weight(1F)
                .padding(start = 8.dp),
            placeholder = widget.getPlaceholder2(sites),
        )
    }
}

@Composable
private fun Checkbox(
    widget: Sites.Site.Widget.CheckBox,
    sites: Sites,
    editWidget: (Sites.Site.Widget) -> Unit,
) = Row(
    Modifier
        .fillMaxWidth()
        .padding(top = 0.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
) {
    var checked by remember { mutableStateOf(widget.getChecked(sites)) }
    Checkbox(
        checked = widget.getChecked(sites),
        onCheckedChange = {
            checked = it
            editWidget(widget.changeChecked(it))
        },
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .toggleable(
                    value = checked,
                    onValueChange = {
                        checked = it
                        editWidget(widget.changeChecked(it))
                    },
                )
                .padding(TextFieldDefaults.contentPaddingWithoutLabel()),
        ) {
            Text(widget.getLabel(sites))
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CheckboxWithChooser(
    widget: Sites.Site.Widget.CheckBoxWithChooser,
    sites: Sites,
    editWidget: (Sites.Site.Widget) -> Unit,
) = Row(
    Modifier
        .fillMaxWidth()
        .padding(top = 0.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
) {
    val focusManager = LocalFocusManager.current
    var checked by remember { mutableStateOf(widget.getChecked(sites)) }
    Checkbox(
        checked = checked,
        onCheckedChange = {
            checked = it
            editWidget(widget.changeChecked(it))
        },
    )
    var expanded by rememberSaveable { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (!checked) {
                checked = true
                editWidget(widget.changeChecked(true))
            }
            else expanded = !expanded
        },
        Modifier
            .fillMaxWidth(),
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
                .padding(top = 0.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
            readOnly = true,
            value = if (checked) widget.getChosen(sites) else "",
            onValueChange = {},
            label = { Text(widget.getLabel(sites)) },
            placeholder = { Text(widget.getPlaceholder(sites)) },
            trailingIcon = {
                if (checked)
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                disabledLabelColor = LocalContentColor.current,
                disabledBorderColor = Color.Transparent,
            ),
            enabled = checked,
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
            widget.getOptions(sites).forEachIndexed { i, moznost ->
                DropdownMenuItem(
                    text = { Text(moznost) },
                    onClick = {
                        editWidget(widget.changeChosenIndex(i))
                        expanded = false
                        focusManager.clearFocus()
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AssemblyCompany(
    widget: Sites.Site.Contacts.AssemblyCompany,
    companies: List<Company>,
    editWidget: (Sites.Site.Widget) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val chosenCompany by remember(widget) {
        derivedStateOf {
            companies.find { it.crn == widget.crn }
        }
    }
    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(widget.crn)) }
    val filteredCompanies by remember(companies, text) {
        derivedStateOf {
            companies.filter { company ->
                text.text.forComparing().split(" ").all { wordOfText ->
                    company.name.forComparing().split(" ").any { wordOfCompany ->
                        wordOfCompany.startsWith(wordOfText)
                    }
                } || company.crn.startsWith(text.text)
            }
        }
    }
    val focusManager = LocalFocusManager.current
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
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .padding(top = 0.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
            value = text,
            onValueChange = {
                text = it
                expanded = it.text.toIntOrNull()?.toString()?.length != 8
                if (it.text.toIntOrNull()?.toString()?.length == 8)
                    editWidget(widget.ico(it.text))
                else
                    editWidget(widget.ico(""))
            },
            label = { Text(strings.contacts.crn) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            supportingText = {
                if (text.text.isBlank()) {
                    Text(text = "Můžete zadat název firmy pro vyhledávání")
                } else if (chosenCompany != null) {
                    Text(text = "Detekováno: ${chosenCompany!!.name}")
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
            isError = text.text.toIntOrNull()?.toString()?.length != 8 && text.text.isNotBlank(),
        )
        ExposedDropdownMenu(
            expanded = expanded && filteredCompanies.isNotEmpty(),
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(strings.contacts.chooseAssemblyCompanyHere) },
                onClick = {},
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                enabled = false,
            )
            filteredCompanies
                .forEach { option ->
                    DropdownMenuItem(
                        text = { Text("${option.name} - ${option.crn}") },
                        onClick = {
                            text = TextFieldValue(
                                text = option.crn,
                                selection = TextRange(8)
                            )
                            editWidget(widget.ico(option.crn))
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DropdownWithAmount(
    widget: Sites.Site.Widget.DropdownWithAmount,
    sites: Sites,
    editWidget: (Sites.Site.Widget) -> Unit,
) = Row(
    Modifier.fillMaxWidth()
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        Modifier
            .weight(1F),
    ) {
        val focusManager = LocalFocusManager.current
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
                .padding(top = 0.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
            readOnly = true,
            value = widget.getValuesWithAmounts(sites),
            placeholder = { Text(widget.getPlaceholder(sites)) },
            onValueChange = {},
            label = { Text(widget.getLabel(sites)) },
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
            Modifier.exposedDropdownSize(),
        ) {
            widget.getItems(sites).forEachIndexed { i, item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {},
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    trailingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            IconButton(
                                onClick = {
                                    editWidget(widget.changeNumber(sites, i, widget.getAmount(sites)[i] - 1))
                                },
                                enabled = widget.getMinimumAmount(sites)[i] < widget.getAmount(sites)[i],
                            ) {
                                Icon(Icons.Default.Remove, strings.remove)
                            }
                            Text(widget.getAmount(sites)[i].toString())
                            IconButton(
                                onClick = {
                                    editWidget(widget.changeNumber(sites, i, widget.getAmount(sites)[i] + 1))
                                },
                                enabled = widget.getAmount(sites)[i] < widget.getMaximumAmount(sites)[i],
                            ) {
                                Icon(Icons.Default.Add, strings.add)
                            }
                        }
                    },
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun CoreChooser(
    value: String,
    options: List<String>,
    onClick: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholder: String = "",
    colors: TextFieldColors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
) {
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            value = value,
            onValueChange = {},
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = colors,
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
            options.forEachIndexed { i, option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onClick(i, option)
                        expanded = false
                        focusManager.clearFocus()
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    leadingIcon = {
                        if (option == value) Icon(Icons.Default.Check, null)
                    }
                )
            }
        }
    }
}