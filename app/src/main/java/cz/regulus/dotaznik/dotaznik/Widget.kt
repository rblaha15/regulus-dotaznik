package cz.regulus.dotaznik.dotaznik

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Widget {
    fun showWidget(sites: Sites) = true
}

interface HasLabel : Widget {
    fun getLabel(sites: Sites): String
}

interface HasTextField : Widget {
    fun getSuffix(sites: Sites) = ""
    fun getKeyboard(sites: Sites) = KeyboardOptions(imeAction = ImeAction.Next)
    fun getPlaceholder(sites: Sites): String = ""

    val text: String? get() = null
    fun changeText(text: String?): HasTextField
    fun getDefaultText(sites: Sites) = ""
}

interface HasUnits : Widget {
    fun getUnits(sites: Sites): List<String>

    val chosenUnitIndex: Int? get() = null
    fun changeChosenUnitIndex(chosenUnitIndex: Int?): HasUnits
    fun getDefaultUnitIndex(sites: Sites) = 0
}

interface HasChooser : Widget {
    fun getOptions(sites: Sites): List<String>
    fun getPlaceholder(sites: Sites): String = ""

    val chosenIndex: Int? get() = null
    fun changeChosenIndex(chosenIndex: Int?): HasChooser
    fun getDefaultIndex(sites: Sites) = 0
}

interface HasMultiChooser : Widget {
    fun getOptions(sites: Sites): List<String>
    fun getPlaceholder(sites: Sites): String = ""

    val chosenIndices: Set<Int> get() = emptySet()
    fun changeChosenIndices(chosenIndices: Set<Int>): HasMultiChooser
    fun getDefaultIndices(sites: Sites) = emptySet<Int>()
}

interface HasFollowUpChooser : Widget {
    fun getOptions2(sites: Sites): List<String>
    fun getPlaceholder2(sites: Sites): String = ""

    val chosenIndex2: Int? get() = null
    fun changeChosenIndex2(chosenIndex2: Int?): HasFollowUpChooser
    fun getDefaultIndex2(sites: Sites) = 0
}

interface HasCheckBox : Widget {
    val checked: Boolean? get() = null
    fun changeChecked(checked: Boolean?): HasCheckBox
    fun getDefaultChecked(sites: Sites) = false
}

interface HasDropdown : Widget {
    fun getItems(sites: Sites): List<String>
    fun getPlaceholder(sites: Sites): String = ""
}

interface HasAmount : Widget, HasDropdown {
    fun getMinimumAmount(sites: Sites): GetFunction1<Int, Int> = get { 0 }
    fun getMaximumAmount(sites: Sites): GetFunction1<Int, Int>
    fun getDefaultAmount(sites: Sites): GetFunction1<Int, Int> = get { 0 }

    val amounts: Map<Int, Int>? get() = null
    fun changeAmounts(numbers: Map<Int, Int>?): HasAmount
}

interface HasMaxSumOfNumbers : Widget, HasAmount {
    fun getMaxSum(sites: Sites): Int
    override fun getMaximumAmount(sites: Sites) = get {
        val left = getMaxSum(sites) - getCurrentSum(sites)
        (getAmount(sites)[it] + left).coerceAtLeast(getMinimumAmount(sites)[it])
    }
}

interface HasTitle : Widget {
    fun getTitle(sites: Sites): String
}

@Serializable @SerialName("TextField") sealed interface TextField : HasTextField, HasLabel
@Serializable @SerialName("TextFieldWithUnits") sealed interface TextFieldWithUnits : HasTextField, HasUnits, HasLabel
@Serializable @SerialName("Chooser") sealed interface Chooser : HasChooser, HasLabel
@Serializable @SerialName("MultiChooser") sealed interface MultiChooser : HasMultiChooser, HasLabel
@Serializable @SerialName("DoubleChooser") sealed interface DoubleChooser : HasChooser, HasFollowUpChooser, HasLabel
@Serializable @SerialName("CheckBox") sealed interface CheckBox : HasCheckBox, HasLabel
@Serializable @SerialName("CheckBoxWithChooser") sealed interface CheckBoxWithChooser : HasCheckBox, HasChooser, HasLabel
@Serializable @SerialName("DropdownWithAmount") sealed interface DropdownWithAmount : HasDropdown, HasAmount, HasLabel
@Serializable @SerialName("Other") sealed interface Other : Widget

fun interface GetFunction1<K, V> {
    operator fun get(index: K): V
}

fun interface GetFunction2<K1, K2, V> {
    operator fun get(i1: K1, i2: K2): V
}

@Style1
fun <T> get(function: (Int) -> T): GetFunction1<Int, T> = GetFunction1(function)

@DslMarker
annotation class Style1

@DslMarker
annotation class Style3

context(Sites) @Style3 fun Chooser.toXmlEntry() = getChosen(this@Sites)
context(Sites) @Style3 fun DoubleChooser.toXmlEntry() = "${getChosen(this@Sites)} ${getChosen2(this@Sites)}"
context(Sites) @Style3 fun TextField.toXmlEntry() = getText(this@Sites)
context(Sites) @Style3 fun TextFieldWithUnits.toXmlEntry() = getText(this@Sites)
context(Sites) @Style3 fun TextFieldWithUnits.toXmlEntry2() = getChosenUnit(this@Sites)
context(Sites) @Style3 fun CheckBox.toXmlEntry() = if (getChecked(this@Sites)) "Ano" else "Ne"
context(Sites) @Style3 fun CheckBoxWithChooser.toXmlEntry() = if (getChecked(this@Sites)) getChosen(this@Sites) else "Ne"
context(Sites) @Style3 fun DropdownWithAmount.toXmlEntry() = getValuesWithAmounts(this@Sites)
context(Sites) @Style3 fun Contacts.DemandOrigin.toXmlEntry() = getCode(this@Sites)
context(Sites) fun String.emptyUnlessChecked(widget: HasCheckBox) = if (widget.getChecked(this@Sites)) this else ""

fun HasTextField.getText(sites: Sites) = text ?: getDefaultText(sites)

fun HasUnits.getDefaultUnit(sites: Sites) = getUnits(sites)[getDefaultUnitIndex(sites)]
fun HasUnits.getChosenUnit(sites: Sites) = chosenUnitIndex?.let { getUnits(sites)[it] } ?: getDefaultUnit(sites)
fun HasUnits.getChosenUnitIndex(sites: Sites) = chosenUnitIndex ?: getDefaultUnitIndex(sites)

fun HasChooser.getShowPlaceholder(sites: Sites) = getPlaceholder(sites).isNotEmpty()
fun HasChooser.getDefault(sites: Sites) =
    if (getShowPlaceholder(sites)) "" else getOptions(sites)[getDefaultIndex(sites)]
fun HasChooser.getChosen(sites: Sites) = chosenIndex?.let { getOptions(sites).getOrNull(it) } ?: getDefault(sites)
fun HasChooser.getChosenIndex(sites: Sites) = chosenIndex ?: getDefaultIndex(sites)

fun HasMultiChooser.getShowPlaceholder(sites: Sites) = getPlaceholder(sites).isNotEmpty()
fun HasMultiChooser.getDefault(sites: Sites) =
    if (getShowPlaceholder(sites)) emptyList() else getOptions(sites).slice(getDefaultIndices(sites))

fun HasMultiChooser.getChosen(sites: Sites) = getOptions(sites).slice(chosenIndices)
fun HasMultiChooser.toggleIndex(index: Int) =
    if (index in chosenIndices) changeChosenIndices(chosenIndices - index) else changeChosenIndices(chosenIndices + index)

fun HasFollowUpChooser.getShowPlaceholder2(sites: Sites) = getPlaceholder2(sites).isNotEmpty()
fun HasFollowUpChooser.getDefault2(sites: Sites) =
    if (getShowPlaceholder2(sites)) "" else getOptions2(sites)[getDefaultIndex2(sites)]
fun HasFollowUpChooser.getChosen2(sites: Sites) = chosenIndex2?.let { getOptions2(sites).getOrNull(it) } ?: getDefault2(sites)
fun HasFollowUpChooser.getChosenIndex2(sites: Sites) = chosenIndex2 ?: getDefaultIndex2(sites)

fun HasCheckBox.getChecked(sites: Sites) = checked ?: getDefaultChecked(sites)

fun HasAmount.changeNumber(sites: Sites, index: Int, number: Int) = changeAmounts(
    getAmounts(sites) + (index to number.coerceIn(getMinimumAmount(sites)[index]..getMaximumAmount(sites)[index]))
)
fun HasAmount.getAmounts(sites: Sites) = getDefaultNumbers(sites) + (amounts ?: emptyMap())
fun HasAmount.getAmount(sites: Sites) = get { index ->
    getAmounts(sites)[index] ?: getDefaultAmount(sites)[index]
}
fun HasAmount.getDefaultNumbers(sites: Sites) = List(getItemCount(sites)) { index ->
    index to getDefaultAmount(sites)[index]
}.toMap()
fun HasAmount.getValuesWithAmounts(sites: Sites) = this
    .getAmounts(sites)
    .filterValues { it > 0 }
    .map { (index, amount) ->
        "${amount}x ${getItems(sites)[index]}"
    }
    .joinToString(", ")

fun HasMaxSumOfNumbers.getCurrentSum(sites: Sites): Int = getAmounts(sites).values.sum()

fun HasDropdown.getItemCount(sites: Sites) = getItems(sites).size