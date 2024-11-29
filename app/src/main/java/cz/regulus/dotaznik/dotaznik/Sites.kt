package cz.regulus.dotaznik.dotaznik

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pool
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import cz.regulus.dotaznik.BuildConfig
import cz.regulus.dotaznik.Products
import cz.regulus.dotaznik.User
import cz.regulus.dotaznik.strings.strings
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.intellij.lang.annotations.Language

@Serializable
@SerialName("Sites")
data class Sites(
    val contacts: Contacts = Contacts(),
    val objectDetails: ObjectDetails = ObjectDetails(),
    val system: System = System(),
    val pool: Pool = Pool(),
    val additionalSources: AdditionalSources = AdditionalSources(),
    val accessories: Accessories = Accessories(),
    @Transient internal val products: Products = Products(),
) {
    val vse = listOf(
        contacts, objectDetails, system, pool, additionalSources, accessories
    )

    fun copySite(site: Site) = when (site) {
        is Pool -> copy(pool = site)
        is ObjectDetails -> copy(objectDetails = site)
        is AdditionalSources -> copy(additionalSources = site)
        is Contacts -> copy(contacts = site)
        is Accessories -> copy(accessories = site)
        is System -> copy(system = site)
    }
}

@Serializable
sealed interface Site {
    fun copyWidget(newWidget: Widget): Site

    fun getName(sites: Sites): String
    fun getIcon(sites: Sites): ImageVector
    fun getWidgets(sites: Sites): List<List<Widget>>
    fun showSite(sites: Sites) = true
}

@Serializable
@SerialName("Contacts")
data class Contacts(
    val demandOrigin: DemandOrigin = DemandOrigin(),
    val surname: Surname = Surname(),
    val name: Name = Name(),
    val street: Street = Street(),
    val city: City = City(),
    val zip: Zip = Zip(),
    val phone: Phone = Phone(),
    val email: Email = Email(),
    val assemblyCompany: AssemblyCompany = AssemblyCompany(),
    val note: Note = Note(),
) : Site {
    override fun copyWidget(newWidget: Widget) = when (newWidget) {
        is DemandOrigin -> copy(demandOrigin = newWidget)
        is Email -> copy(email = newWidget)
        is AssemblyCompany -> copy(assemblyCompany = newWidget)
        is Name -> copy(name = newWidget)
        is City -> copy(city = newWidget)
        is Note -> copy(note = newWidget)
        is Surname -> copy(surname = newWidget)
        is Zip -> copy(zip = newWidget)
        is Phone -> copy(phone = newWidget)
        is Street -> copy(street = newWidget)
        else -> this
    }

    @Serializable
    data class Surname(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.contacts.surname
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words
        )
    }

    @Serializable
    data class DemandOrigin(
        override val chosenIndex: Int? = null,
    ) : Chooser {
        override fun getOptions(sites: Sites) = getOrigins().keys.toList()
        override fun getLabel(sites: Sites) = strings.contacts.demandOrigin + "*"
        override fun getPlaceholder(sites: Sites) = strings.choose
        override fun changeChosenIndex(chosenIndex: Int?): HasChooser =
            copy(chosenIndex = chosenIndex)

        private fun getOrigins() = listOfNotNull(
            strings.contacts.originQuestionEmail to "_dotazEmail",
            strings.contacts.originQuestionExhibition to "_dotaznikVYS",
            strings.contacts.originQuestionInPerson to "_dotazOsobně",
            strings.contacts.originDistributionCompany to "_poptávkaDis",
            strings.contacts.originAssemlbleres to "_poptávkaMF",
            strings.contacts.originDesigner to "_poptávkaPROJ",
            if (BuildConfig.DEBUG) "Zkoušení funkčnosti aplikace, prosím, nepoužívejte tuto možnost v reálných poptávkách (DEBUG)" to "" else null,
        ).toMap()

        fun getCode(sites: Sites) = getOrigins()[getChosen(sites)]
    }

    @Serializable
    data class Name(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.contacts.name
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words
        )
    }

    @Serializable
    data class Street(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.contacts.street
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences
        )
    }

    @Serializable
    data class City(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.contacts.city
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words
        )
    }

    @Serializable
    data class Zip(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.contacts.zip
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    }

    @Serializable
    data class Phone(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.contacts.phone
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            keyboardType = KeyboardType.Phone
        )
    }

    @Serializable
    data class Email(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.contacts.email
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            keyboardType = KeyboardType.Email
        )
    }

    @Serializable
    data class AssemblyCompany(
        val crn: String = "",
    ) : Other {
        fun ico(ico: String) = copy(crn = ico)
    }

    @Serializable
    data class Note(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.note
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            imeAction = ImeAction.Done
        )
    }

    override fun getName(sites: Sites) = strings.contacts.contacts
    override fun getIcon(sites: Sites) = Icons.Default.Person
    override fun getWidgets(sites: Sites) = listOf(
        listOf(demandOrigin),
        listOf(surname, name, street, city, zip, phone, email),
        listOf(assemblyCompany),
        listOf(note),
    )
}

@Serializable
@SerialName("ObjectDetails")
data class ObjectDetails(
    val heatLost: HeatLost = HeatLost(),
    val heatNeedsForHeating: HeatNeedsForHeating = HeatNeedsForHeating(),
    val heatNeedsForHotWater: HeatNeedsForHotWater = HeatNeedsForHotWater(),
    val heatedArea: HeatedArea = HeatedArea(),
    val heatedVolume: HeatedVolume = HeatedVolume(),
    val heatingCosts: HeatingCosts = HeatingCosts(),
    val fuelType: FuelType = FuelType(),
    val fuelConsumption: FuelConsumption = FuelConsumption(),
    val fuelType2: FuelType2 = FuelType2(),
    val fuelConsumption2: FuelConsumption2 = FuelConsumption2(),
    val note: Note = Note(),
) : Site {
    override fun copyWidget(newWidget: Widget) = when (newWidget) {
        is FuelType -> copy(fuelType = newWidget)
        is FuelType2 -> copy(fuelType2 = newWidget)
        is HeatingCosts -> copy(heatingCosts = newWidget)
        is HeatNeedsForHotWater -> copy(heatNeedsForHotWater = newWidget)
        is HeatNeedsForHeating -> copy(heatNeedsForHeating = newWidget)
        is Note -> copy(note = newWidget)
        is HeatLost -> copy(heatLost = newWidget)
        is HeatedArea -> copy(heatedArea = newWidget)
        is HeatedVolume -> copy(heatedVolume = newWidget)
        is FuelConsumption -> copy(fuelConsumption = newWidget)
        is FuelConsumption2 -> copy(fuelConsumption2 = newWidget)
        else -> this
    }

    @Serializable
    data class HeatLost(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.objectDetail.heatLoss
        override fun getSuffix(sites: Sites) = strings.units.kW
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        )
    }

    @Serializable
    data class HeatNeedsForHeating(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.objectDetail.heatNeedsForHeating
        override fun getSuffix(sites: Sites) = strings.units.kWh
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    }

    @Serializable
    data class HeatNeedsForHotWater(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.objectDetail.heatNeedsForHotWater
        override fun getSuffix(sites: Sites) = strings.units.kWh
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    }

    @Serializable
    data class HeatedArea(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.objectDetail.area
        override fun getSuffix(sites: Sites) = strings.units.m2
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    }

    @Serializable
    data class HeatedVolume(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.objectDetail.volume
        override fun getSuffix(sites: Sites) = strings.units.m3
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    }

    @Serializable
    data class HeatingCosts(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.objectDetail.costs
        override fun getSuffix(sites: Sites) = strings.currency
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        )
    }

    @Serializable
    data class FuelType(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.objectDetail.type
    }

    @Serializable
    data class FuelConsumption(
        override val text: String? = null,
        override val chosenUnitIndex: Int? = null,
    ) : TextFieldWithUnits {
        override fun changeText(text: String?) = copy(text = text)
        override fun changeChosenUnitIndex(chosenUnitIndex: Int?) = copy(chosenUnitIndex = chosenUnitIndex)
        override fun getLabel(sites: Sites) = strings.objectDetail.usage
        override fun getUnits(sites: Sites) = listOf(strings.units.q, strings.units.m3, strings.units.kWh)
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            keyboardType = KeyboardType.Text
        )
    }

    @Serializable
    data class FuelType2(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.objectDetail.type2
    }

    @Serializable
    data class FuelConsumption2(
        override val text: String? = null,
        override val chosenUnitIndex: Int? = null,
    ) : TextFieldWithUnits {
        override fun changeChosenUnitIndex(chosenUnitIndex: Int?) = copy(chosenUnitIndex = chosenUnitIndex)
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.objectDetail.usage2
        override fun getUnits(sites: Sites) = listOf(strings.units.q, strings.units.m3, strings.units.kWh)
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            keyboardType = KeyboardType.Text
        )
    }

    @Serializable
    data class Note(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.note
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            imeAction = ImeAction.Done
        )
    }

    override fun getName(sites: Sites) = strings.objectDetail.objectDetail
    override fun getIcon(sites: Sites) = Icons.Default.Home
    override fun getWidgets(sites: Sites) = listOf(
        listOf(heatLost),
        listOf(heatNeedsForHeating, heatNeedsForHotWater),
        listOf(heatedArea, heatedVolume),
        listOf(heatingCosts, fuelType, fuelConsumption, fuelType2, fuelConsumption2),
        listOf(note),
    )
}

@Serializable
@SerialName("System")
data class System(
    val hPType: HPType = HPType(),
    val hPModel: HPModel = HPModel(),
    val indoorUnitType: IndoorUnitType = IndoorUnitType(),
    val thermalStoreType: ThermalStoreType = ThermalStoreType(),
    val thermalStoreVolume: ThermalStoreVolume = ThermalStoreVolume(),
    val waterTankType: WaterTankType = WaterTankType(),
    val waterTankVolume: WaterTankVolume = WaterTankVolume(),
    val heatingSystem: HeatingSystem = HeatingSystem(),
    val hotWaterCirculation: HotWaterCirculation = HotWaterCirculation(),
    val note: Note = Note(),
) : Site {
    override fun copyWidget(newWidget: Widget) = when (newWidget) {
        is ThermalStoreType -> copy(thermalStoreType = newWidget)
        is ThermalStoreVolume -> copy(thermalStoreVolume = newWidget)
        is WaterTankVolume -> copy(waterTankVolume = newWidget)
        is Note -> copy(note = newWidget)
        is HPModel -> copy(hPModel = newWidget)
        is HeatingSystem -> copy(heatingSystem = newWidget)
        is HPType -> copy(hPType = newWidget)
        is IndoorUnitType -> copy(indoorUnitType = newWidget)
        is WaterTankType -> copy(waterTankType = newWidget)
        is HotWaterCirculation -> copy(hotWaterCirculation = newWidget)
        else -> this
    }

    @Serializable
    data class HPType(
        override val chosenIndex: Int? = null,
    ) : Chooser {
        override fun changeChosenIndex(chosenIndex: Int?) = copy(chosenIndex = chosenIndex)
        override fun getLabel(sites: Sites) = strings.system.hPType
        override fun getOptions(sites: Sites) = listOf(strings.system.airToWater, strings.system.groundToWater)
    }

    @Serializable
    data class HPModel(
        override val chosenIndex: Int? = null,
    ) : Chooser {
        override fun changeChosenIndex(chosenIndex: Int?) = copy(chosenIndex = chosenIndex)
        override fun getLabel(sites: Sites) = strings.system.hPModel
        override fun getOptions(sites: Sites) = listOf(
            strings.choose,
        ) + when (sites.system.hPType.chosenIndex ?: sites.system.hPType.getDefaultIndex(sites)) {
            0 -> sites.products.heatPumpsAirToWater
            1 -> sites.products.heatPumpsGroundToWater
            else -> throw IllegalArgumentException()
        }
    }

    @Serializable
    data class IndoorUnitType(
        override val chosenIndex: Int? = null,
    ) : Chooser {
        override fun changeChosenIndex(chosenIndex: Int?) = copy(chosenIndex = chosenIndex)
        override fun getLabel(sites: Sites) = strings.system.indoorUnitType
        override fun getOptions(sites: Sites) = listOf(
            strings.noneFeminine,
        ) + sites.products.indoorUnits
    }

    @Serializable
    data class ThermalStoreType(
        override val chosenIndex: Int? = null,
        override val chosenIndex2: Int? = null,
    ) : DoubleChooser {
        override fun getLabel(sites: Sites) = strings.system.storeType
        override fun getOptions(sites: Sites) = listOf(
            strings.noneFeminine,
        ) + sites.products.thermalStores.keys

        override fun getOptions2(sites: Sites): List<String> {
            val chosen = sites.system.thermalStoreType.getChosen(sites)
            return sites.products.thermalStores[chosen] ?: listOf()
        }

        override fun changeChosenIndex(chosenIndex: Int?): HasChooser = copy(chosenIndex = chosenIndex)
        override fun changeChosenIndex2(chosenIndex2: Int?): HasFollowUpChooser = copy(chosenIndex2 = chosenIndex2)
    }

    @Serializable
    data class ThermalStoreVolume(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.system.storeVolume
        override fun getSuffix(sites: Sites) = strings.units.dm3
        override fun showWidget(sites: Sites) = sites.system.thermalStoreType.getChosen(sites) != strings.noneFeminine

        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    }

    @Serializable
    data class WaterTankType(
        override val chosenIndex: Int? = null,
    ) : Chooser {
        override fun changeChosenIndex(chosenIndex: Int?) = copy(chosenIndex = chosenIndex)
        override fun getLabel(sites: Sites) = strings.system.tankType
        override fun getOptions(sites: Sites) = listOf(
            strings.noneMasculine,
        ) + sites.products.waterTanks
    }

    @Serializable
    data class WaterTankVolume(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.system.tankVolume
        override fun getSuffix(sites: Sites) = strings.units.dm3
        override fun showWidget(sites: Sites) = sites.system.waterTankType.getChosen(sites) != strings.noneMasculine
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    }

    @Serializable
    data class HeatingSystem(
        override val chosenIndex: Int? = null,
    ) : Chooser {
        override fun changeChosenIndex(chosenIndex: Int?) = copy(chosenIndex = chosenIndex)
        override fun getLabel(sites: Sites) = strings.system.heatingSystem
        override fun getOptions(sites: Sites) = listOf(
            strings.system.heatingSystem1circuit,
            strings.system.heatingSystem2circuits,
            strings.system.heatingSystem3circuits,
            strings.system.heatingSystemInvertor,
            strings.otherMasculine,
        )
    }

    @Serializable
    data class HotWaterCirculation(
        override val checked: Boolean? = null,
    ) : CheckBox {
        override fun changeChecked(checked: Boolean?) = copy(checked = checked)
        override fun getLabel(sites: Sites) = strings.system.hotWaterCirculation
    }

    @Serializable
    data class Note(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.note
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            imeAction = ImeAction.Done
        )
    }

    override fun getName(sites: Sites) = strings.system.system
    override fun getIcon(sites: Sites) = Icons.Default.Category
    override fun getWidgets(sites: Sites) = listOf(
        listOf(hPType, hPModel),
        listOf(indoorUnitType),
        listOf(thermalStoreType, thermalStoreVolume),
        listOf(waterTankType, waterTankVolume),
        listOf(heatingSystem),
        listOf(hotWaterCirculation),
        listOf(note),
    )
}

@Serializable
@SerialName("Pool")
data class Pool(
    val wantsPool: WantsPool = WantsPool(),
    val usagePeriod: UsagePeriod = UsagePeriod(),
    val placement: Placement = Placement(),
    val waterType: WaterType = WaterType(),
    val shape: Shape = Shape(),
    val length: Length = Length(),
    val width: Width = Width(),
    val radius: Radius = Radius(),
    val depth: Depth = Depth(),
    val coverage: Coverage = Coverage(),
    val desiredTemperature: DesiredTemperature = DesiredTemperature(),
    val note: Note = Note(),
) : Site {
    override fun copyWidget(newWidget: Widget) = when (newWidget) {
        is Length -> copy(length = newWidget)
        is Depth -> copy(depth = newWidget)
        is DesiredTemperature -> copy(desiredTemperature = newWidget)
        is Note -> copy(note = newWidget)
        is Radius -> copy(radius = newWidget)
        is Width -> copy(width = newWidget)
        is UsagePeriod -> copy(usagePeriod = newWidget)
        is WaterType -> copy(waterType = newWidget)
        is Shape -> copy(shape = newWidget)
        is Placement -> copy(placement = newWidget)
        is Coverage -> copy(coverage = newWidget)
        is WantsPool -> copy(wantsPool = newWidget)
        else -> this
    }

    @Serializable
    data class WantsPool(
        override val checked: Boolean? = null,
    ) : CheckBox {
        override fun changeChecked(checked: Boolean?) = copy(checked = checked)
        override fun getLabel(sites: Sites) = strings.system.poolHeating
    }

    @Serializable
    data class UsagePeriod(
        override val chosenIndex: Int? = null,
    ) : Chooser {
        override fun changeChosenIndex(chosenIndex: Int?) = copy(chosenIndex = chosenIndex)
        override fun getLabel(sites: Sites) = strings.pool.usagePeriod
        override fun getOptions(sites: Sites) = listOf(
            strings.pool.periodYearlong,
            strings.pool.periodSeasonal,
        )

        override fun showWidget(sites: Sites) = sites.pool.wantsPool.getChecked(sites)
    }

    @Serializable
    data class Placement(
        override val chosenIndex: Int? = null,
    ) : Chooser {
        override fun changeChosenIndex(chosenIndex: Int?) = copy(chosenIndex = chosenIndex)
        override fun getLabel(sites: Sites) = strings.pool.location
        override fun getOptions(sites: Sites) = listOf(
            strings.pool.locationOutdoor,
            strings.pool.locationIndoor,
        )

        override fun showWidget(sites: Sites) = sites.pool.wantsPool.getChecked(sites)
    }

    @Serializable
    data class WaterType(
        override val chosenIndex: Int? = null,
    ) : Chooser {
        override fun changeChosenIndex(chosenIndex: Int?) = copy(chosenIndex = chosenIndex)
        override fun getLabel(sites: Sites) = strings.pool.waterType
        override fun getOptions(sites: Sites) = listOf(
            strings.pool.freshType,
            strings.pool.saltType,
        )

        override fun showWidget(sites: Sites) = sites.pool.wantsPool.getChecked(sites)
    }

    @Serializable
    data class Shape(
        override val chosenIndex: Int? = null,
    ) : Chooser {
        override fun changeChosenIndex(chosenIndex: Int?) = copy(chosenIndex = chosenIndex)
        override fun getLabel(sites: Sites) = strings.pool.shape
        override fun getOptions(sites: Sites) = listOf(
            strings.pool.shapeRectangle,
            strings.pool.shapeOval,
            strings.pool.shapeCircle,
        )

        override fun showWidget(sites: Sites) = sites.pool.wantsPool.getChecked(sites)
    }

    @Serializable
    data class Length(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.pool.length
        override fun getSuffix(sites: Sites) = strings.units.m
        override fun showWidget(sites: Sites) =
            sites.pool.wantsPool.getChecked(sites) && sites.pool.shape.getChosen(sites) != strings.pool.shapeCircle

        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        )
    }

    @Serializable
    data class Width(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.pool.width
        override fun getSuffix(sites: Sites) = strings.units.m
        override fun showWidget(sites: Sites) =
            sites.pool.wantsPool.getChecked(sites) && sites.pool.shape.getChosen(sites) != strings.pool.shapeCircle

        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        )
    }

    @Serializable
    data class Radius(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.pool.radius
        override fun getSuffix(sites: Sites) = strings.units.m
        override fun showWidget(sites: Sites) =
            sites.pool.wantsPool.getChecked(sites) && sites.pool.shape.getChosen(sites) == strings.pool.shapeCircle

        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        )
    }

    @Serializable
    data class Depth(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.pool.depth
        override fun getSuffix(sites: Sites) = strings.units.m
        override fun showWidget(sites: Sites) = sites.pool.wantsPool.getChecked(sites)
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        )
    }

    @Serializable
    data class Coverage(
        override val chosenIndex: Int? = null,
    ) : Chooser {
        override fun changeChosenIndex(chosenIndex: Int?) = copy(chosenIndex = chosenIndex)
        override fun getLabel(sites: Sites) = strings.pool.coverage
        override fun getOptions(sites: Sites) = listOf(
            strings.noneNeuter,
            strings.pool.covaregeSolid,
            strings.pool.coveragePolycarbonate,
            strings.otherNeuter,
        )

        override fun showWidget(sites: Sites) = sites.pool.wantsPool.getChecked(sites)
    }

    @Serializable
    data class DesiredTemperature(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.pool.temperature
        override fun getSuffix(sites: Sites) = strings.units.degreeCelsius
        override fun showWidget(sites: Sites) = sites.pool.wantsPool.getChecked(sites)
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    }

    @Serializable
    data class Note(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.note
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            imeAction = ImeAction.Done
        )
    }

    override fun getName(sites: Sites) = strings.pool.pool
    override fun getIcon(sites: Sites) = Icons.Default.Pool
    override fun getWidgets(sites: Sites) = listOf(
        listOf(
            wantsPool,
            usagePeriod,
            placement,
            waterType,
        ),
        listOf(
            shape,
            length,
            width,
            radius,
            depth,
        ),
        listOf(
            coverage,
            desiredTemperature,
        ),
        listOf(note),
    )
}

@Serializable
@SerialName("AdditionalSources")
data class AdditionalSources(
    val heatingElementInStoreHeating: HeatingElementInStoreHeating = HeatingElementInStoreHeating(),
    val electricBoilerHeating: ElectricBoilerHeating = ElectricBoilerHeating(),
    val gasBoilerHeating: GasBoilerHeating = GasBoilerHeating(),
    val fireplaceHeating: FireplaceHeating = FireplaceHeating(),
    val otherHeating: OtherHeating = OtherHeating(),
    val heatingElementInStoreHotWater: HeatingElementInStoreHotWater = HeatingElementInStoreHotWater(),
    val electricBoilerHotWater: ElectricBoilerHotWater = ElectricBoilerHotWater(),
    val gasBoilerHotWater: GasBoilerHotWater = GasBoilerHotWater(),
    val fireplaceHotWater: FireplaceHotWater = FireplaceHotWater(),
    val otherHotWater: OtherHotWater = OtherHotWater(),
    val note: Note = Note(),
) : Site {
    override fun copyWidget(newWidget: Widget) = when (newWidget) {
        is OtherHotWater -> copy(otherHotWater = newWidget)
        is OtherHeating -> copy(otherHeating = newWidget)
        is Note -> copy(note = newWidget)
        is ElectricBoilerHotWater -> copy(electricBoilerHotWater = newWidget)
        is FireplaceHotWater -> copy(fireplaceHotWater = newWidget)
        is GasBoilerHotWater -> copy(gasBoilerHotWater = newWidget)
        is ElectricBoilerHeating -> copy(electricBoilerHeating = newWidget)
        is FireplaceHeating -> copy(fireplaceHeating = newWidget)
        is GasBoilerHeating -> copy(gasBoilerHeating = newWidget)
        is HeatingElementInStoreHotWater -> copy(heatingElementInStoreHotWater = newWidget)
        is HeatingElementInStoreHeating -> copy(heatingElementInStoreHeating = newWidget)
        else -> this
    }

    @Serializable
    data object TitleTopeni : HasTitle {
        override fun getTitle(sites: Sites) = strings.additionalSources.heating
    }

    @Serializable
    data class HeatingElementInStoreHeating(
        override val checked: Boolean? = null,
        override val chosenIndex: Int? = null,
    ) : CheckBoxWithChooser {
        override fun changeChecked(checked: Boolean?): HasCheckBox = copy(checked = checked)
        override fun changeChosenIndex(chosenIndex: Int?): HasChooser = copy(chosenIndex = chosenIndex)
        override fun getLabel(sites: Sites) = strings.additionalSources.heatingElement
        override fun getOptions(sites: Sites) = listOf(strings.additionalSources.existing, strings.additionalSources.newNeuter)
        override fun getDefaultIndex(sites: Sites) = 1
    }

    @Serializable
    data class ElectricBoilerHeating(
        override val checked: Boolean? = null,
        override val chosenIndex: Int? = null,
    ) : CheckBoxWithChooser {
        override fun changeChecked(checked: Boolean?): HasCheckBox = copy(checked = checked)
        override fun changeChosenIndex(chosenIndex: Int?): HasChooser = copy(chosenIndex = chosenIndex)
        override fun getLabel(sites: Sites) = strings.additionalSources.electricBoiler
        override fun getOptions(sites: Sites) = listOf(strings.additionalSources.existing, strings.additionalSources.newMasculine)
    }

    @Serializable
    data class GasBoilerHeating(
        override val checked: Boolean? = null,
        override val chosenIndex: Int? = null,
    ) : CheckBoxWithChooser {
        override fun changeChecked(checked: Boolean?): HasCheckBox = copy(checked = checked)
        override fun changeChosenIndex(chosenIndex: Int?): HasChooser = copy(chosenIndex = chosenIndex)
        override fun getLabel(sites: Sites) = strings.additionalSources.gasBoiler
        override fun getOptions(sites: Sites) = listOf(strings.additionalSources.existing, strings.additionalSources.newMasculine)
    }

    @Serializable
    data class FireplaceHeating(
        override val checked: Boolean? = null,
        override val chosenIndex: Int? = null,
    ) : CheckBoxWithChooser {
        override fun changeChecked(checked: Boolean?): HasCheckBox = copy(checked = checked)
        override fun changeChosenIndex(chosenIndex: Int?): HasChooser = copy(chosenIndex = chosenIndex)
        override fun getLabel(sites: Sites) = strings.additionalSources.fireplace
        override fun getOptions(sites: Sites) = listOf(strings.additionalSources.existing, strings.additionalSources.newMasculine)
    }

    @Serializable
    data class OtherHeating(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.otherMasculine
    }

    @Serializable
    data object TitleTeplaVoda : HasTitle {
        override fun getTitle(sites: Sites) = strings.additionalSources.hotWater
    }

    @Serializable
    data class HeatingElementInStoreHotWater(
        override val checked: Boolean? = null,
        override val chosenIndex: Int? = null,
    ) : CheckBoxWithChooser {
        override fun changeChecked(checked: Boolean?): HasCheckBox = copy(checked = checked)
        override fun changeChosenIndex(chosenIndex: Int?): HasChooser = copy(chosenIndex = chosenIndex)
        override fun getLabel(sites: Sites) = strings.additionalSources.heatingElement
        override fun getOptions(sites: Sites) = listOf(
            strings.additionalSources.toSocket,
            strings.additionalSources.fromRegulation
        )
    }

    @Serializable
    data class ElectricBoilerHotWater(
        override val checked: Boolean? = null,
    ) : CheckBox {
        override fun changeChecked(checked: Boolean?) = copy(checked = checked)
        override fun getLabel(sites: Sites) = strings.additionalSources.electricBoiler
    }

    @Serializable
    data class GasBoilerHotWater(
        override val checked: Boolean? = null,
    ) : CheckBox {
        override fun changeChecked(checked: Boolean?) = copy(checked = checked)
        override fun getLabel(sites: Sites) = strings.additionalSources.gasBoiler
    }

    @Serializable
    data class FireplaceHotWater(
        override val checked: Boolean? = null,
    ) : CheckBox {
        override fun changeChecked(checked: Boolean?) = copy(checked = checked)
        override fun getLabel(sites: Sites) = strings.additionalSources.fireplace
    }

    @Serializable
    data class OtherHotWater(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.otherMasculine
    }

    @Serializable
    data class Note(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.note
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            imeAction = ImeAction.Done
        )
    }

    override fun getName(sites: Sites) = strings.additionalSources.sources
    override fun getIcon(sites: Sites) = Icons.Default.AcUnit
    override fun getWidgets(sites: Sites) = listOf(
        listOf(
            TitleTopeni,
            heatingElementInStoreHeating,
            electricBoilerHeating,
            gasBoilerHeating,
            fireplaceHeating,
            otherHeating
        ),
        listOf(
            TitleTeplaVoda,
            heatingElementInStoreHotWater,
            electricBoilerHotWater,
            gasBoilerHotWater,
            fireplaceHotWater,
            otherHotWater
        ),
        listOf(note),
    )
}

@Serializable
@SerialName("Accessories")
data class Accessories(
    val hose: Hose = Hose(),
    val heatingCable: HeatingCable = HeatingCable(),
    val wallSupportBracket: WallSupportBracket = WallSupportBracket(),
    val roomUnitsAndSensors: RoomUnitsAndSensors = RoomUnitsAndSensors(),
    val note: Note = Note(),
) : Site {
    override fun copyWidget(newWidget: Widget) = when (newWidget) {
        is Note -> copy(note = newWidget)
        is WallSupportBracket -> copy(wallSupportBracket = newWidget)
        is Hose -> copy(hose = newWidget)
        is RoomUnitsAndSensors -> copy(roomUnitsAndSensors = newWidget)
        is HeatingCable -> copy(heatingCable = newWidget)
        else -> this
    }

    @Serializable
    data class Hose(
        override val checked: Boolean? = null,
        override val chosenIndex: Int? = null,
    ) : CheckBoxWithChooser {
        override fun changeChecked(checked: Boolean?): HasCheckBox = copy(checked = checked)
        override fun changeChosenIndex(chosenIndex: Int?): HasChooser = copy(chosenIndex = chosenIndex)
        override fun getLabel(sites: Sites) = strings.accessories.hose
        override fun getOptions(sites: Sites) = listOf("300 mm", "500 mm", "700 mm", "1000 mm")
        override fun getDefaultIndex(sites: Sites) = 1
    }

    @Serializable
    data class HeatingCable(
        override val checked: Boolean? = null,
        override val chosenIndex: Int? = null,
    ) : CheckBoxWithChooser {
        override fun changeChecked(checked: Boolean?): HasCheckBox = copy(checked = checked)
        override fun changeChosenIndex(chosenIndex: Int?): HasChooser = copy(chosenIndex = chosenIndex)
        override fun getLabel(sites: Sites) = strings.accessories.heatingCable
        override fun getOptions(sites: Sites) = listOf("3,5 m", "5 m")
    }

    @Serializable
    data class WallSupportBracket(
        override val checked: Boolean? = null,
        override val chosenIndex: Int? = null,
    ) : CheckBoxWithChooser {
        override fun changeChecked(checked: Boolean?): HasCheckBox = copy(checked = checked)
        override fun changeChosenIndex(chosenIndex: Int?): HasChooser = copy(chosenIndex = chosenIndex)
        override fun getLabel(sites: Sites) = strings.accessories.wallSupportBracket
        override fun getOptions(sites: Sites) = listOf(strings.accessories.onWall, strings.accessories.onIsolatedWall)
    }

    @Serializable
    data class RoomUnitsAndSensors(
        override val amounts: Map<Int, Int>? = null,
    ) : DropdownWithAmount, HasMaxSumOfNumbers {
        override fun changeAmounts(numbers: Map<Int, Int>?): HasAmount = copy(amounts = numbers)
        override fun getPlaceholder(sites: Sites) = strings.noneNeuter
        override fun getLabel(sites: Sites) = strings.accessories.roomUnitsAndSensors
        override fun getItems(sites: Sites) = listOf("RC 25", "RDC", "RS 10", "RSW 30 - WiFi")
        override fun getMaxSum(sites: Sites) = when (sites.system.heatingSystem.getChosen(sites)) {
            strings.system.heatingSystem1circuit -> 1
            strings.system.heatingSystem2circuits -> 2
            strings.system.heatingSystem3circuits -> 3
            strings.system.heatingSystemInvertor -> 1
            else -> Int.MAX_VALUE
        }
    }

    @Serializable
    data class Note(
        override val text: String? = null,
    ) : TextField {
        override fun changeText(text: String?) = copy(text = text)
        override fun getLabel(sites: Sites) = strings.note
        override fun getKeyboard(sites: Sites) = KeyboardOptions(
            imeAction = ImeAction.Done
        )
    }

    override fun getName(sites: Sites) = strings.accessories.accessories
    override fun getIcon(sites: Sites) = Icons.Default.AddShoppingCart
    override fun getWidgets(sites: Sites) = listOf(
        listOf(
            hose,
            heatingCable,
            wallSupportBracket,
            roomUnitsAndSensors,
        ),
        listOf(note),
    )
}

@Language("xml")
fun Sites.createXml(
    user: User,
) = """<?xml version="1.0" encoding="utf-8"?>
<?xml-stylesheet type="text/xsl" href="dotaznik_app.xsl"?>

<!-- 
Tento soubor byl vygenerován automaticky aplikací Regulus Dotazník
Verze dokumentu: 2.3 (Zaveden ve verzi aplikace 5.1.0)
Verze aplikace: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) (${BuildConfig.BUILD_TYPE})
-->

<!--
Změny ve verzi 2.3 oproti verzi 2.2:
- Přidán kód1 (/xml/system/kod1)
  - Možnosti jsou: _dotazEmail, _dotaznikVYS, _dotazOsobně, _poptávkaDis, _poptávkaMF, _poptávkaPROJ 
  - V DEBUG verzi aplikace lze zadat i možnost _debug, ta by se ale nikdy neměla objevit v opravdových poptávkách
- Pokojová čidla a jednotky sjednoceny do jednoho pole (/xml/prislusenstvi/pokojova_cidla_a_jednotky)
  - Nyní mohou obsahovat více možností, oddělených čárkou
  - Každá možnost má před sebou vždy specifikován počet položek pomocí písmene x 
  - Název žádné jednotky ani čidla neobsahuje čárku
  - Příklad: `<pokojove_cidlo>1x RS 10, 2x RDC</pokojove_cidlo>`
-->

<xml>
    <system>
        <kod1>${contacts.demandOrigin.toXmlEntry()}</kod1>
        <resi_tc>Ano</resi_tc>
        <cislo_ko>${user.koNumber}</cislo_ko>
        <odesilatel>${user.email}</odesilatel>
        <odberatel_ico>${user.crn}</odberatel_ico>
    </system>
    <kontakt>
        <jmeno>${contacts.name.toXmlEntry()}</jmeno>
        <prijmeni>${contacts.surname.toXmlEntry()}</prijmeni>
        <telefon>${contacts.phone.toXmlEntry()}</telefon>
        <email>${contacts.email.toXmlEntry()}</email>
        <ulice>${contacts.street.toXmlEntry()}</ulice>
        <psc>${
    if (contacts.zip.toXmlEntry().length != 5) null.orEmpty()
    else contacts.zip.toXmlEntry().substring(0, 3) + " " + contacts.zip.toXmlEntry().substring(3, 5)
}</psc>
        <mesto>${contacts.city.toXmlEntry()}</mesto>
        <partner_ico>${contacts.assemblyCompany.crn}</partner_ico>
    </kontakt>
    <detailobjektu>
        <os_popis>${system.heatingSystem.toXmlEntry()}</os_popis>
        <tepelna_ztrata>${objectDetails.heatLost.toXmlEntry()}</tepelna_ztrata>
        <rocni_spotreba_vytapeni>${objectDetails.heatNeedsForHeating.toXmlEntry()}</rocni_spotreba_vytapeni>
        <rocni_spotreba_tv>${objectDetails.heatNeedsForHotWater.toXmlEntry()}</rocni_spotreba_tv>
        <vytapena_plocha>${objectDetails.heatedArea.toXmlEntry()}</vytapena_plocha>
        <vytapeny_objem>${objectDetails.heatedVolume.toXmlEntry()}</vytapeny_objem>
        <spotreba_paliva_druh>${objectDetails.fuelType.toXmlEntry()}</spotreba_paliva_druh>
        <spotreba_paliva_mnozstvi>${objectDetails.fuelConsumption.toXmlEntry()}</spotreba_paliva_mnozstvi>
        <spotreba_paliva_jednotky>${objectDetails.fuelConsumption.toXmlEntry2()}</spotreba_paliva_jednotky>
        <spotreba_paliva_2_druh>${objectDetails.fuelType2.toXmlEntry()}</spotreba_paliva_2_druh>
        <spotreba_paliva_2_mnozstvi>${objectDetails.fuelConsumption2.toXmlEntry()}</spotreba_paliva_2_mnozstvi>
        <spotreba_paliva_2_jednotky>${objectDetails.fuelConsumption2.toXmlEntry2()}</spotreba_paliva_2_jednotky>
        <rocni_platba_vytapeni>${objectDetails.heatingCosts.toXmlEntry()} ${strings.currency}</rocni_platba_vytapeni>
    </detailobjektu>
    <tc>
        <typ>${system.hPType.toXmlEntry()}</typ>
        <model>${system.hPModel.toXmlEntry()}</model>
        <nadrz>${system.thermalStoreType.toXmlEntry()} ${system.thermalStoreVolume.toXmlEntry()}</nadrz>
        <vnitrni_jednotka>${system.indoorUnitType.toXmlEntry()}</vnitrni_jednotka>
    </tc>
    <zdrojeTop>
        <topne_teleso>${additionalSources.heatingElementInStoreHeating.toXmlEntry()}</topne_teleso>
        <elektrokotel>${additionalSources.electricBoilerHeating.toXmlEntry()}</elektrokotel>
        <plyn_kotel>${additionalSources.gasBoilerHeating.toXmlEntry()}</plyn_kotel>
        <krb_KTP>${additionalSources.fireplaceHeating.toXmlEntry()}</krb_KTP>
        <jiny_zdroj>${additionalSources.otherHeating.toXmlEntry()}</jiny_zdroj>
    </zdrojeTop>
    <tv>
        <zasobnik>${system.waterTankType.toXmlEntry()} ${system.waterTankVolume.toXmlEntry()}</zasobnik>
        <cirkulace>${system.hotWaterCirculation.toXmlEntry()}</cirkulace>
    </tv>
    <zdrojeTV>
        <topne_teleso>${additionalSources.heatingElementInStoreHotWater.toXmlEntry()}</topne_teleso>
        <elektrokotel>${additionalSources.electricBoilerHotWater.toXmlEntry()}</elektrokotel>
        <plyn_kotel>${additionalSources.gasBoilerHotWater.toXmlEntry()}</plyn_kotel>
        <krb_KTP>${additionalSources.fireplaceHotWater.toXmlEntry()}</krb_KTP>
        <jiny_zdroj>${additionalSources.otherHotWater.toXmlEntry()}</jiny_zdroj>
    </zdrojeTV>
    <bazen>
        <ohrev>${pool.wantsPool.toXmlEntry()}</ohrev>
        <doba_vyuzivani>${pool.usagePeriod.toXmlEntry().emptyUnlessChecked(pool.wantsPool)}</doba_vyuzivani>
        <umisteni>${pool.placement.toXmlEntry().emptyUnlessChecked(pool.wantsPool)}</umisteni>
        <zakryti>${pool.coverage.toXmlEntry().emptyUnlessChecked(pool.wantsPool)}</zakryti>
        <tvar>${pool.shape.toXmlEntry().emptyUnlessChecked(pool.wantsPool)}</tvar>
        <sirka>${pool.width.toXmlEntry()}</sirka>
        <delka>${pool.length.toXmlEntry()}</delka>
        <hloubka>${pool.depth.toXmlEntry()}</hloubka>
        <prumer>${pool.radius.toXmlEntry()}</prumer>
        <teplota>${pool.desiredTemperature.toXmlEntry()}</teplota>
        <voda>${pool.waterType.toXmlEntry().emptyUnlessChecked(pool.wantsPool)}</voda>
    </bazen>
    <prislusenstvi>
        <hadice>${accessories.hose.toXmlEntry()}</hadice>
        <topny_kabel>${accessories.heatingCable.toXmlEntry()}</topny_kabel>
        <drzak_na_tc>${accessories.wallSupportBracket.toXmlEntry()}</drzak_na_tc>
        <pokojova_cidla_a_jednotky>${accessories.roomUnitsAndSensors.toXmlEntry()}</pokojova_cidla_a_jednotky>
    </prislusenstvi>
    <poznamka>
        <kontakty>${contacts.note.toXmlEntry()}</kontakty>
        <detail_objektu>${objectDetails.note.toXmlEntry()}</detail_objektu>
        <tv_tc_nadrz_a_os>${system.note.toXmlEntry()}</tv_tc_nadrz_a_os>
        <bazen>${pool.note.toXmlEntry()}</bazen>
        <doplnkove_zdroje>${additionalSources.note.toXmlEntry()}</doplnkove_zdroje>
        <prislusenstvi>${accessories.note.toXmlEntry()}</prislusenstvi>
    </poznamka>
</xml>
"""