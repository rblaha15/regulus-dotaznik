package cz.regulus.dotaznik.strings

import androidx.compose.ui.text.intl.Locale

typealias LanguageTag = String

interface Strings {
    val appName: String
    val contacts: ContactsStrings
    val fve: FVEStrings
    val objectDetail: ObjectDetailStrings
    val system: SystemStrings
    val pool: PoolStrings
    val additionalSources: AdditionalSourcesStrings
    val accessories: AccessoriesStrings
    val logIn: LogInStrings
    val photos: PhotosStrings
    val export: ExportStrings
    val note: String
    val open: String
    val close: String
    val ok: String
    val yes: String
    val no: String
    val cancel: String
    val units: UnitsStrings
        get() = object : UnitsStrings {
            override val kW = "kW"
            override val kWh = "kWh"
            override val m2 = "m²"
            override val q = "q"
            override val m3 = "m³"
            override val dm3 = "dm³"
            override val m = "m"
            override val degreeCelsius = "°C"
        }
    val noneFeminine: String
    val noneMasculine: String
    val noneNeuter: String
    val choose: String
    val otherNeuter: String
    val otherMasculine: String
    val currency: String
    val internetConnectionNeeded: String
    val back: String
    val chooseUnits: String
    val remove: String
    val add: String
}

interface ContactsStrings {
    val contacts: String
    val surname: String
    val name: String
    val street: String
    val city: String
    val zip: String
    val phone: String
    val email: String
    val chooseAssemblyCompanyHere: String
    val chooseCompany: String
    val chooseAssemblyCompany: String
    val searchCompany: String
    val crn: String
    val demandOrigin: String
    val originQuestionEmail: String
    val originQuestionExhibition: String
    val originQuestionInPerson: String
    val originDistributionCompany: String
    val originAssemlbleres: String
    val originDesigner: String
    val demandSubject: String
    val heatPump: String
    val fve: String
}

interface FVEStrings {
    val fve: String
    val currentSituation: String
    val currentHeating: String
    val currentHotWater: String
    val currentTanks: String
    val currentConsumption: String
    val breakerSize: String
    val tariff: String
    val breakerBoxLocation: String
    val requirements: String
    val requiredPower: String
    val locationBuidingType: String
    val lightningRod: String
    val familyHouseEtc: String
    val avaiableAreas: String
    val roofMaterial: String
    val roofAge: String
    val tile: String
    val tileType: String
    val metalSheetFolded: String
    val metalSheetTrapezoidal: String
    val foil: String
    val asphaltShingle: String
    val useOptimizers: String
    val size: (Int) -> String
    val orientation: (Int) -> String
    val slope: (Int) -> String
    val battery: String
    val batteryCapacity: String
    val water: String
    val network: String
    val networkPower: String
    val charging: String
}

interface ObjectDetailStrings {
    val objectDetail: String
    val heatLoss: String
    val heatNeedsForHeating: String
    val heatNeedsForHotWater: String
    val area: String
    val volume: String
    val costs: String
    val type: String
    val usage: String
    val type2: String
    val usage2: String
}

interface SystemStrings {
    val system: String
    val hPType: String
    val airToWater: String
    val groundToWater: String
    val hPModel: String
    val storeType: String
    val storeVolume: String
    val tankType: String
    val tankVolume: String
    val indoorUnitType: String
    val heatingSystem: String
    val heatingSystem1circuit: String
    val heatingSystem2circuits: String
    val heatingSystem3circuits: String
    val heatingSystemInvertor: String
    val hotWaterCirculation: String
    val poolHeating: String
}

interface PoolStrings {
    val pool: String
    val usagePeriod: String
    val periodYearlong: String
    val periodSeasonal: String
    val location: String
    val locationOutdoor: String
    val locationIndoor: String
    val waterType: String
    val freshType: String
    val saltType: String
    val shape: String
    val shapeRectangle: String
    val shapeOval: String
    val shapeCircle: String
    val length: String
    val width: String
    val radius: String
    val depth: String
    val coverage: String
    val covaregeSolid: String
    val coveragePolycarbonate: String
    val temperature: String
}

interface AdditionalSourcesStrings {
    val hotWater: String
    val heating: String
    val sources: String
    val heatingElement: String
    val electricBoiler: String
    val gasBoiler: String
    val fireplace: String
    val newNeuter: String
    val newMasculine: String
    val existing: String
    val toSocket: String
    val fromRegulation: String
}

interface AccessoriesStrings {
    val accessories: String
    val hose: String
    val wallSupportBracket: String
    val roomUnitsAndSensors: String
    val heatingCable: String
    val onWall: String
    val onIsolatedWall: String
}

interface LogInStrings {
    val logIn: String
    val internetNeeded: String
    val iAmEmploee: String
    val iAmNotEmploee: String
    val yourRepresentative: String
    val yourName: String
    val yourSurname: String
    val yourCrn: String
    val yourEmail: String
    val chooseYourself: String
    val selectedFullName: (String, String) -> String
    val selectedEmail: (String) -> String
    val selectedCode: (String) -> String
    val selectedCrn: (String) -> String
    val nameAndSurname: String
    val email: String
    val code: String
    val crn: String
    val addMoreInfo: String
    val representativeNeeded: String
    val emailNeeded: String
    val surnameNeeded: String
    val nameNeeded: String
    val youAreNotLoggedIn: String
    val logOut: String
}

interface PhotosStrings {
    val photosManager: String
    val takePhoto: String
    val choosePhoto: String
    val photo: String
    val add: String
    val maxPhotosReached: String
    val maxPhotosOvershoot: String
    val noPhotos: String
    val remove: String
}

interface ExportStrings {
    val sending: String
    val emailIsBeingSend: String
    val emailSuccessfullySent: String
    val doYouWantToSend: String
    val missingField: String
    val pleaseFillInField: (String) -> String
    val doYouReallyWantToSend: (String) -> String
    val emailNotSent: (String) -> String
    val doYouRellyWantToRemoveData: String
    val youAreOffline: String
    val errorReported: String
    val send: String
    val nameAndSurnameNeeded: String
    val removeAll: String
    val thisIsTheIssue: String
    val moreInfo: String
}

interface UnitsStrings {
    val kW: String
    val kWh: String
    val m2: String
    val q: String
    val m3: String
    val dm3: String
    val m: String
    val degreeCelsius: String
}

object Locales {
    const val SK = "sk"
    const val CS = "cs"
}

val StringMap: Map<LanguageTag, Strings> = mapOf(
    Locales.SK to SkStrings,
    Locales.CS to CsStrings,
)

val strings: Strings get() = strings()
fun strings(
    defaultLanguageTag: LanguageTag = Locales.CS,
    currentLanguageTag: LanguageTag = Locale.current.toLanguageTag(),
) = StringMap[currentLanguageTag]
    ?: StringMap[defaultLanguageTag]
    ?: throw IllegalArgumentException()