package cz.regulus.dotaznik.strings

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale

typealias LanguageTag = String

interface Strings {
    val appName: String
    val kontakty: String
    val detailObjektu: String
    val system: String
    val bazen: String
    val zdrojeTv: String
    val zdrojeTop: String
    val zdroje: String
    val poznamka: String
    val otevrit: String
    val zavrit: String
    val ok: String
    val ano: String
    val ne: String
    val zrusit: String
    val odeslat: String
    val odstranit: String
    val kW get() = "kW"
    val kWh get() = "kWh"
    val m2 get() = "m²"
    val q get() = "q"
    val m3 get() = "m³"
    val dm3 get() = "dm³"
    val m get() = "m"
    val stupenC get() = "°C"
    val zadna: String
    val zadny: String
    val zadne: String
    val vyberte: String
    val jine: String
    val jiny: String
    val vzduchVoda: String
    val zemeVoda: String
    val kontaktyPrijmeni: String
    val kontaktyJmeno: String
    val kontaktyUlice: String
    val kontaktyMesto: String
    val kontaktyPsc: String
    val kontaktyTelefon: String
    val kontaktyEmail: String
    val kontaktyVyberteMontazniFirmu: String
    val kontaktyVybratFirmu: String
    val kontaktyVyberteFirmu: String
    val kontaktyVyhledatFirmu: String
    val kontaktyIco: String
    val detailObjektuZtrata: String
    val detailObjektuPotrebaVytapeni: String
    val detailObjektuPotrebaTv: String
    val detailObjektuPlocha: String
    val detailObjektuObjem: String
    val detailObjektuNaklady: String
    val detailObjektuDruh: String
    val detailObjektuSpotreba: String
    val detailObjektuDruh2: String
    val detailObjektuSpotreba2: String
    val systemTcTyp: String
    val systemTcModel: String
    val systemNadrzTyp: String
    val systemNadrzObjem: String
    val systemZasobnikTyp: String
    val sytemZasobnikObjem: String
    val systemTypJednotky: String
    val systemOtopnySystem: String
    val systemOs1okruh: String
    val systemOs2okruhy: String
    val systemOs3okruhy: String
    val systemOsInvertor: String
    val systemCirkulace: String
    val systemOhrevBazenu: String
    val bazenDobaUzivani: String
    val bazenDobaCelorocni: String
    val bazenDobaSezonni: String
    val bazenUmisteni: String
    val bazenUmisteniVenkovni: String
    val bazenUmisteniVnitrni: String
    val bazenDruhVody: String
    val bazenDruhSladka: String
    val bazenDruhSlana: String
    val bazenTvar: String
    val bazenTvarObdelnikovy: String
    val bazenTvarOvalny: String
    val bazenTvarKruhovy: String
    val bazenDelka: String
    val bazenSirka: String
    val bazenRadius: String
    val bazenHloubka: String
    val bazenZakryti: String
    val bazenZakrytiPevna: String
    val bazenZakrytiPolykarbonat: String
    val bazenTeplota: String
    val zdrojeTopneTeleso: String
    val zdrojeElektrokotel: String
    val zdrojePlynKotel: String
    val zdrojeKrb: String
    val zdrojeNove: String
    val zdrojeNovy: String
    val zdrojeStavajici: String
    val zdrojeTvDoZasuvky: String
    val zdrojeTvZRegulace: String
    val jePotrebaZadatJmenoAPrijmeni: String
    val jePotrebaZadatZastupce: String
    val jePotrebaZadatEmail: String
    val jePotrebaZadatPrijmeni: String
    val jePotrebaZadatJmeno: String
    val prihlaseni: String
    val prihlaseniPotrebaInternet: String
    val prihlaseniJsemZamestanec: String
    val prihlaseniNejsemZamestanec: String
    val prihlaseniVasZastupce: String
    val prihlaseniVaseJmeno: String
    val prihlaseniVasePrijmeni: String
    val prihlaseniVaseIco: String
    val prihlaseniVasEmail: String
    val prihlaseniVyberSe: String
    val prihlaseniVybranyJmenoPrijmeni: (String, String) -> String
    val prihlaseniVybranyEmail: (String) -> String
    val prihlaseniVybranyKod: (String) -> String
    val menuVybranyJmenoPrijmeni: (String, String) -> String
    val menuVybranyEmail: (String) -> String
    val menuVybranyIco: (String) -> String
    val menuVybranyKod: (String) -> String
    val prihlaseniJmenoPrijmeni: String
    val prihlaseniEmail: String
    val prihlaseniKod: String
    val prihlaseniIco: String
    val exportOdesilani: String
    val exportEmailSeOdesila: String
    val exportEmailUspesneOdeslan: String
    val exportChceteOdeslat: String
    val exportOpravduChceteOdeslatNa: (String) -> String
    val exportEmailNeodeslan: (String) -> String
    val exportOpravduOdstranitData: String
    val fotkySpravaFotek: String
    val fotkyVyfotit: String
    val fotkyVybratZGalerie: String
    val fotkyFotka: String
    val fotkyPridat: String
    val fotkyMaximalneFotek: String
    val fotkyMaximalneFotekPresazeno: String
    val odstranitVse: String
    val totoJeChyba: String
    val podrobnejsiInfo: String
    val exportNejstePripojeni: String
    val prihlaseniDoplnitInfo: String
    val aktualizovat: String
    val vyberteFirmu: String
    val naStenu: String
    val naIzolovanouStenu: String
    val prislusenstvi: String
    val prislusenstviHadice: String
    val prislusenstviDrzakProTc: String
    val prislusenstviPokojovaJednotka: String
    val prislusenstviTopnyKabel: String
    val prislusenstviPokojoveCidlo: String
    val mena: String
    val nejstePrihlaseni: String
    val odhlasitSe: String
    val fotkyZadneFotky: String
    val potrebaInternet: String
    val zpet: String
    val vybratJednotky: String
}

object Locales {
    const val SK = "sk"
    const val CS = "cs"
}

val StringMap: Map<LanguageTag, Strings> = mapOf(
    Locales.SK to SkStrings,
    Locales.CS to CsStrings,
)

fun interface StringsProvider {
    fun provideStrings(
        defaultLanguageTag: LanguageTag,
        currentLanguageTag: LanguageTag,
    ): Strings
}

context(StringsProvider) val strings: Strings get() = strings()
fun StringsProvider.strings(
    defaultLanguageTag: LanguageTag = Locales.CS,
    currentLanguageTag: LanguageTag = Locale.current.toLanguageTag(),
) = provideStrings(defaultLanguageTag, currentLanguageTag)

data object GenericStringsProvider : StringsProvider {
    override fun provideStrings(defaultLanguageTag: LanguageTag, currentLanguageTag: LanguageTag) =
        StringMap[currentLanguageTag]
            ?: StringMap[defaultLanguageTag]
            ?: throw IllegalArgumentException()
}

val strings: Strings @Composable get() = GenericStringsProvider.run { strings }