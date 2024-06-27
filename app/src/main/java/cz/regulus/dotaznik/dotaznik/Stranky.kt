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
import cz.regulus.dotaznik.Produkty
import cz.regulus.dotaznik.Uzivatel
import cz.regulus.dotaznik.dotaznik.Stranky.Stranka.Vec.DvojVybiratko
import cz.regulus.dotaznik.dotaznik.Stranky.Stranka.Vec.Jine
import cz.regulus.dotaznik.dotaznik.Stranky.Stranka.Vec.Nadpis
import cz.regulus.dotaznik.dotaznik.Stranky.Stranka.Vec.Pisatko
import cz.regulus.dotaznik.dotaznik.Stranky.Stranka.Vec.PisatkoSJednotkama
import cz.regulus.dotaznik.dotaznik.Stranky.Stranka.Vec.Vybiratko
import cz.regulus.dotaznik.dotaznik.Stranky.Stranka.Vec.Zaskrtavatko
import cz.regulus.dotaznik.dotaznik.Stranky.Stranka.Vec.ZaskrtavatkoSVybiratkem
import cz.regulus.dotaznik.strings.GenericStringsProvider
import cz.regulus.dotaznik.strings.StringsProvider
import cz.regulus.dotaznik.strings.strings
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Stranky(
    val kontakty: Stranka.Kontakty = Stranka.Kontakty(),
    val detailObjektu: Stranka.DetailObjektu = Stranka.DetailObjektu(),
    val system: Stranka.System = Stranka.System(),
    val bazen: Stranka.Bazen = Stranka.Bazen(),
    val doplnkoveZdroje: Stranka.DoplnkoveZdroje = Stranka.DoplnkoveZdroje(),
    val prislusenstvi: Stranka.Prislusenstvi = Stranka.Prislusenstvi(),
    @Transient private val produkty: Produkty = Produkty(),
) : StringsProvider by GenericStringsProvider {

    val vse = listOf(
        kontakty, detailObjektu, system, bazen, doplnkoveZdroje, prislusenstvi
    )

    fun kopirovatStranku(stranka: Stranka) = when (stranka) {
        is Stranka.Bazen -> copy(bazen = stranka)
        is Stranka.DetailObjektu -> copy(detailObjektu = stranka)
        is Stranka.DoplnkoveZdroje -> copy(doplnkoveZdroje = stranka)
        is Stranka.Kontakty -> copy(kontakty = stranka)
        is Stranka.Prislusenstvi -> copy(prislusenstvi = stranka)
        is Stranka.System -> copy(system = stranka)
    }

    @Serializable
    sealed interface Stranka {
        fun kopirovatVec(novaVec: Vec): Stranka

        context(Stranky) val nazev: String
        context(Stranky) val icon: ImageVector
        context(Stranky) val veci: List<List<Vec>>

        @Serializable
        sealed interface Vec {
            context(Stranky) val zobrazit: Boolean

            @Serializable
            sealed interface Pisatko : Vec {
                context(Stranky) val popis: String
                context(Stranky) val suffix get() = ""
                context(Stranky) val klavesnice get() = KeyboardOptions(imeAction = ImeAction.Next)
                context(Stranky) override val zobrazit get() = true

                val text: String? get() = null
                context(Stranky) val textOrDefault get() = text ?: defaultText
                fun text(text: String?): Pisatko
                context(Stranky) val defaultText get() = ""
            }

            @Serializable
            sealed interface PisatkoSJednotkama : Vec {
                context(Stranky) val popis: String
                context(Stranky) val jednotky: List<String>
                context(Stranky) val klavesnice get() = KeyboardOptions(imeAction = ImeAction.Next)
                context(Stranky) override val zobrazit get() = true

                val text: String? get() = null
                context(Stranky) val textOrDefault get() = text ?: defaultText
                fun text(text: String?): PisatkoSJednotkama
                context(Stranky) val defaultText get() = ""

                val vybraneJednotkyIndex: Int? get() = null
                context(Stranky) val vybraneJednotky get() = vybraneJednotkyIndex?.let { jednotky[it] }
                context(Stranky) val vybraneJednotkyOrDefault get() = vybraneJednotky ?: defaultText
                fun vybraneJednotkyIndex(vybraneJednotkyIndex: Int?): PisatkoSJednotkama
                context(Stranky) val default get() = jednotky[defaultIndex]
                context(Stranky) val defaultIndex get() = 0
            }

            @Serializable
            sealed interface Vybiratko : Vec {
                context(Stranky) val popis: String
                context(Stranky) val moznosti: List<String>
                context(Stranky) override val zobrazit get() = true

                val vybranoIndex: Int? get() = null
                context(Stranky) val vybrano get() = vybranoIndex?.let { moznosti[it] }
                context(Stranky) val vybranoOrDefault get() = vybrano ?: default
                fun vybranoIndex(vybranoIndex: Int?): Vybiratko
                context(Stranky) val default get() = moznosti[defaultIndex]
                context(Stranky) val defaultIndex get() = 0
            }

            @Serializable
            sealed interface DvojVybiratko : Vec {
                context(Stranky) val popis: String
                context(Stranky) val moznosti1: List<String>
                context(Stranky) val moznosti2: List<String>
                context(Stranky) override val zobrazit get() = true

                val vybrano1Index: Int? get() = null
                context(Stranky) val vybrano1 get() = vybrano1Index?.let { moznosti1[it] }
                context(Stranky) val vybrano1OrDefault get() = vybrano1 ?: default1
                fun vybrano1Index(vybrano1Index: Int?): DvojVybiratko
                context(Stranky) val default1 get() = moznosti1[default1Index]
                context(Stranky) val default1Index get() = 0

                val vybrano2Index: Int? get() = null
                context(Stranky) val vybrano2 get() = vybrano2Index?.let { moznosti2[it] }
                context(Stranky) val vybrano2OrDefault get() = vybrano2 ?: default2
                fun vybrano2Index(vybrano2Index: Int?): DvojVybiratko
                context(Stranky) val default2 get() = moznosti2[default2Index]
                context(Stranky) val default2Index get() = 0
            }

            @Serializable
            sealed interface Zaskrtavatko : Vec {
                context(Stranky) val popis: String
                context(Stranky) override val zobrazit get() = true

                val zaskrtnuto: Boolean? get() = null
                context(Stranky) val zaskrtnutoOrDefault get() = zaskrtnuto ?: default
                fun zaskrtnuto(zaskrtnuto: Boolean?): Zaskrtavatko
                context(Stranky) val default get() = false
            }

            @Serializable
            sealed interface ZaskrtavatkoSVybiratkem : Vec {
                context(Stranky) val popis: String
                context(Stranky) val moznosti: List<String>
                context(Stranky) override val zobrazit get() = true

                val zaskrtnuto: Boolean? get() = null
                context(Stranky) val zaskrtnutoOrDefault get() = zaskrtnuto ?: defaultZaskrtnuto
                fun zaskrtnuto(zaskrtnuto: Boolean?): ZaskrtavatkoSVybiratkem
                context(Stranky) val defaultZaskrtnuto get() = false

                val vybranoIndex: Int? get() = null
                context(Stranky) val vybrano get() = vybranoIndex?.let { moznosti[it] }
                context(Stranky) val vybranoOrDefault get() = vybrano ?: default
                fun vybranoIndex(vybranoIndex: Int?): ZaskrtavatkoSVybiratkem
                context(Stranky) val default get() = moznosti.first()

                context(Stranky) val text get() = if (zaskrtnutoOrDefault) vybranoOrDefault else "Ne"
            }

            @Serializable
            sealed interface Jine : Vec {
                context(Stranky) override val zobrazit get() = true
            }

            @Serializable
            sealed interface Nadpis : Vec {
                context(Stranky) val text: String
                context(Stranky) override val zobrazit get() = true
            }
        }

        @Serializable
        data class Kontakty(
            val prijmeni: Prijmeni = Prijmeni(),
            val jmeno: Jmeno = Jmeno(),
            val ulice: Ulice = Ulice(),
            val mesto: Mesto = Mesto(),
            val psc: Psc = Psc(),
            val telefon: Telefon = Telefon(),
            val email: Email = Email(),
            val montazniFirma: MontazniFirma = MontazniFirma(),
            val poznamka: Poznamka = Poznamka(),
        ) : Stranka {
            override fun kopirovatVec(novaVec: Vec) = when (novaVec) {
                is Email -> copy(email = novaVec)
                is MontazniFirma -> copy(montazniFirma = novaVec)
                is Jmeno -> copy(jmeno = novaVec)
                is Mesto -> copy(mesto = novaVec)
                is Poznamka -> copy(poznamka = novaVec)
                is Prijmeni -> copy(prijmeni = novaVec)
                is Psc -> copy(psc = novaVec)
                is Telefon -> copy(telefon = novaVec)
                is Ulice -> copy(ulice = novaVec)
                else -> this
            }

            @Serializable
            data class Prijmeni(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.kontaktyPrijmeni
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    )
            }

            @Serializable
            data class Jmeno(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.kontaktyJmeno
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    )
            }

            @Serializable
            data class Ulice(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.kontaktyUlice
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    )
            }

            @Serializable
            data class Mesto(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.kontaktyMesto
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    )
            }

            @Serializable
            data class Psc(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.kontaktyPsc
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
            }

            @Serializable
            data class Telefon(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.kontaktyTelefon
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    )
            }

            @Serializable
            data class Email(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.kontaktyEmail
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    )
            }

            @Serializable
            data class MontazniFirma(
                val ico: String = "",
            ) : Jine {
                fun ico(ico: String) = copy(ico = ico)
            }

            @Serializable
            data class Poznamka(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.poznamka
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
            }

            context(Stranky) override val nazev get() = strings.kontakty
            context(Stranky) override val icon get() = Icons.Default.Person
            context(Stranky) override val veci
                get() = listOf(
                    listOf(prijmeni, jmeno, ulice, mesto, psc, telefon, email),
                    listOf(montazniFirma),
                    listOf(poznamka),
                )
        }

        @Serializable
        data class DetailObjektu(
            val tepelnaZtrata: TepelnaZtrata = TepelnaZtrata(),
            val potrebaTeplaNaVytapeni: PotrebaTeplaNaVytapeni = PotrebaTeplaNaVytapeni(),
            val potrebaTeplaNaTeplouVodu: PotrebaTeplaNaTeplouVodu = PotrebaTeplaNaTeplouVodu(),
            val vytapenaPlocha: VytapenaPlocha = VytapenaPlocha(),
            val vytapenyObjem: VytapenyObjem = VytapenyObjem(),
            val nakladyNaVytapeni: NakladyNaVytapeni = NakladyNaVytapeni(),
            val druhPaliva: DruhPaliva = DruhPaliva(),
            val spotrebaPaliva: SpotrebaPaliva = SpotrebaPaliva(),
            val druhPaliva2: DruhPaliva2 = DruhPaliva2(),
            val spotrebaPaliva2: SpotrebaPaliva2 = SpotrebaPaliva2(),
            val poznamka: Poznamka = Poznamka(),
        ) : Stranka {
            override fun kopirovatVec(novaVec: Vec) = when (novaVec) {
                is DruhPaliva -> copy(druhPaliva = novaVec)
                is DruhPaliva2 -> copy(druhPaliva2 = novaVec)
                is NakladyNaVytapeni -> copy(nakladyNaVytapeni = novaVec)
                is PotrebaTeplaNaTeplouVodu -> copy(potrebaTeplaNaTeplouVodu = novaVec)
                is PotrebaTeplaNaVytapeni -> copy(potrebaTeplaNaVytapeni = novaVec)
                is Poznamka -> copy(poznamka = novaVec)
                is TepelnaZtrata -> copy(tepelnaZtrata = novaVec)
                is VytapenaPlocha -> copy(vytapenaPlocha = novaVec)
                is VytapenyObjem -> copy(vytapenyObjem = novaVec)
                is SpotrebaPaliva -> copy(spotrebaPaliva = novaVec)
                is SpotrebaPaliva2 -> copy(spotrebaPaliva2 = novaVec)
                else -> this
            }

            @Serializable
            data class TepelnaZtrata(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.detailObjektuZtrata
                context(Stranky) override val suffix get() = strings.kW
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
            }

            @Serializable
            data class PotrebaTeplaNaVytapeni(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.detailObjektuPotrebaVytapeni
                context(Stranky) override val suffix get() = strings.kWh
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
            }

            @Serializable
            data class PotrebaTeplaNaTeplouVodu(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.detailObjektuPotrebaTv
                context(Stranky) override val suffix get() = strings.kWh
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
            }

            @Serializable
            data class VytapenaPlocha(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.detailObjektuPlocha
                context(Stranky) override val suffix get() = strings.m2
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
            }

            @Serializable
            data class VytapenyObjem(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.detailObjektuObjem
                context(Stranky) override val suffix get() = strings.m3
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
            }

            @Serializable
            data class NakladyNaVytapeni(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.detailObjektuNaklady
                context(Stranky) override val suffix get() = strings.mena
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
            }

            @Serializable
            data class DruhPaliva(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.detailObjektuDruh
            }

            @Serializable
            data class SpotrebaPaliva(
                override val text: String? = null,
                override val vybraneJednotkyIndex: Int? = null,
            ) : PisatkoSJednotkama {
                override fun text(text: String?) = copy(text = text)
                override fun vybraneJednotkyIndex(vybraneJednotkyIndex: Int?) = copy(vybraneJednotkyIndex = vybraneJednotkyIndex)
                context(Stranky) override val popis get() = strings.detailObjektuSpotreba
                context(Stranky) override val jednotky get() = listOf(strings.q, strings.m3, strings.kWh)
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
            }

            @Serializable
            data class DruhPaliva2(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.detailObjektuDruh2
            }

            @Serializable
            data class SpotrebaPaliva2(
                override val text: String? = null,
                override val vybraneJednotkyIndex: Int? = null,
            ) : PisatkoSJednotkama {
                override fun vybraneJednotkyIndex(vybraneJednotkyIndex: Int?) = copy(vybraneJednotkyIndex = vybraneJednotkyIndex)
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.detailObjektuSpotreba2
                context(Stranky) override val jednotky get() = listOf(strings.q, strings.m3, strings.kWh)
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
            }

            @Serializable
            data class Poznamka(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.poznamka
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
            }

            context(Stranky) override val nazev get() = strings.detailObjektu
            context(Stranky) override val icon get() = Icons.Default.Home
            context(Stranky) override val veci
                get() = listOf(
                    listOf(tepelnaZtrata),
                    listOf(potrebaTeplaNaVytapeni, potrebaTeplaNaTeplouVodu),
                    listOf(vytapenaPlocha, vytapenyObjem),
                    listOf(nakladyNaVytapeni, druhPaliva, spotrebaPaliva, druhPaliva2, spotrebaPaliva2),
                    listOf(poznamka),
                )
        }

        @Serializable
        data class System(
            val typTC: TypTC = TypTC(),
            val modelTC: ModelTC = ModelTC(),
            val typVnitrniJednotky: TypVnitrniJednotky = TypVnitrniJednotky(),
            val typNadrze: TypNadrze = TypNadrze(),
            val objemNadrze: ObjemNadrze = ObjemNadrze(),
            val typZasobniku: TypZasobniku = TypZasobniku(),
            val objemZasobniku: ObjemZasobniku = ObjemZasobniku(),
            val otopnySystem: OtopnySystem = OtopnySystem(),
            val cirkulaceTepleVody: CirkulaceTepleVody = CirkulaceTepleVody(),
            val poznamka: Poznamka = Poznamka(),
        ) : Stranka {
            override fun kopirovatVec(novaVec: Vec) = when (novaVec) {
                is TypNadrze -> copy(typNadrze = novaVec)
                is ObjemNadrze -> copy(objemNadrze = novaVec)
                is ObjemZasobniku -> copy(objemZasobniku = novaVec)
                is Poznamka -> copy(poznamka = novaVec)
                is ModelTC -> copy(modelTC = novaVec)
                is OtopnySystem -> copy(otopnySystem = novaVec)
                is TypTC -> copy(typTC = novaVec)
                is TypVnitrniJednotky -> copy(typVnitrniJednotky = novaVec)
                is TypZasobniku -> copy(typZasobniku = novaVec)
                is CirkulaceTepleVody -> copy(cirkulaceTepleVody = novaVec)
                else -> this
            }

            @Serializable
            data class TypTC(
                override val vybranoIndex: Int? = null,
            ) : Vybiratko {
                override fun vybranoIndex(vybranoIndex: Int?) = copy(vybranoIndex = vybranoIndex)
                context(Stranky) override val popis get() = strings.systemTcTyp
                context(Stranky) override val moznosti get() = listOf(strings.vzduchVoda, strings.zemeVoda)
            }

            @Serializable
            data class ModelTC(
                override val vybranoIndex: Int? = null,
            ) : Vybiratko {
                override fun vybranoIndex(vybranoIndex: Int?) = copy(vybranoIndex = vybranoIndex)
                context(Stranky) override val popis get() = strings.systemTcModel
                context(Stranky) override val moznosti
                    get() = listOf(
                        strings.vyberte,
                    ) + when (system.typTC.vybranoOrDefault) {
                        strings.vzduchVoda -> produkty.tcVzduchVoda

                        strings.zemeVoda -> produkty.tcZemeVoda

                        else -> throw IllegalArgumentException()
                    }
            }

            @Serializable
            data class TypVnitrniJednotky(
                override val vybranoIndex: Int? = null,
            ) : Vybiratko {
                override fun vybranoIndex(vybranoIndex: Int?) = copy(vybranoIndex = vybranoIndex)
                context(Stranky) override val popis get() = strings.systemTypJednotky
                context(Stranky) override val moznosti
                    get() = listOf(
                        strings.zadna,
                    ) + produkty.boxy
            }

            @Serializable
            data class TypNadrze(
                override val vybrano1Index: Int? = null,
                override val vybrano2Index: Int? = null,
            ) : DvojVybiratko {
                context(Stranky) override val popis get() = strings.systemNadrzTyp
                context(Stranky) override val moznosti1
                    get() = listOf(
                        strings.zadna,
                    ) + produkty.nadrze.keys

                context(Stranky) override val moznosti2
                    get() = if (system.typNadrze.vybrano1OrDefault == strings.zadna) emptyList()
                    else {
                        val moznost1 = system.typNadrze.vybrano1!!
                        produkty.nadrze[moznost1].orEmpty()
                    }

                override fun vybrano1Index(vybrano1Index: Int?) = copy(vybrano1Index = vybrano1Index)
                override fun vybrano2Index(vybrano2Index: Int?) = copy(vybrano2Index = vybrano2Index)
            }

            @Serializable
            data class ObjemNadrze(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.systemNadrzObjem
                context(Stranky) override val suffix get() = strings.dm3
                context(Stranky) override val zobrazit get() = system.typNadrze.vybrano1 != strings.zadna

                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
            }

            @Serializable
            data class TypZasobniku(
                override val vybranoIndex: Int? = null,
            ) : Vybiratko {
                override fun vybranoIndex(vybranoIndex: Int?) = copy(vybranoIndex = vybranoIndex)
                context(Stranky) override val popis get() = strings.systemZasobnikTyp
                context(Stranky) override val moznosti
                    get() = listOf(
                        strings.zadny,
                    ) + produkty.zasobniky
            }

            @Serializable
            data class ObjemZasobniku(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.sytemZasobnikObjem
                context(Stranky) override val suffix get() = strings.dm3
                context(Stranky) override val zobrazit get() = system.typZasobniku.vybrano != strings.zadny
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
            }

            @Serializable
            data class OtopnySystem(
                override val vybranoIndex: Int? = null,
            ) : Vybiratko {
                override fun vybranoIndex(vybranoIndex: Int?) = copy(vybranoIndex = vybranoIndex)
                context(Stranky) override val popis get() = strings.systemOtopnySystem
                context(Stranky) override val moznosti
                    get() = listOf(
                        strings.systemOs1okruh,
                        strings.systemOs2okruhy,
                        strings.systemOs3okruhy,
                        strings.systemOsInvertor,
                        strings.jiny,
                    )
            }

            @Serializable
            data class CirkulaceTepleVody(
                override val zaskrtnuto: Boolean? = null,
            ) : Zaskrtavatko {
                override fun zaskrtnuto(zaskrtnuto: Boolean?) = copy(zaskrtnuto = zaskrtnuto)
                context(Stranky) override val popis get() = strings.systemCirkulace
            }

            @Serializable
            data class Poznamka(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.poznamka
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
            }

            context(Stranky) override val nazev get() = strings.system
            context(Stranky) override val icon get() = Icons.Default.Category
            context(Stranky) override val veci
                get() = listOf(
                    listOf(typTC, modelTC),
                    listOf(typVnitrniJednotky),
                    listOf(typNadrze, objemNadrze),
                    listOf(typZasobniku, objemZasobniku),
                    listOf(otopnySystem),
                    listOf(cirkulaceTepleVody),
                    listOf(poznamka),
                )
        }

        @Serializable
        data class Bazen(
            val chciBazen: ChciBazen = ChciBazen(),
            val dobaVyuzivani: DobaVyuzivani = DobaVyuzivani(),
            val umisteni: Umisteni = Umisteni(),
            val druhVody: DruhVody = DruhVody(),
            val tvar: Tvar = Tvar(),
            val delka: Delka = Delka(),
            val sirka: Sirka = Sirka(),
            val prumer: Prumer = Prumer(),
            val hloubka: Hloubka = Hloubka(),
            val zakryti: Zakryti = Zakryti(),
            val pozadovanaTeplota: PozadovanaTeplota = PozadovanaTeplota(),
            val poznamka: Poznamka = Poznamka(),
        ) : Stranka {
            override fun kopirovatVec(novaVec: Vec) = when (novaVec) {
                is Delka -> copy(delka = novaVec)
                is Hloubka -> copy(hloubka = novaVec)
                is PozadovanaTeplota -> copy(pozadovanaTeplota = novaVec)
                is Poznamka -> copy(poznamka = novaVec)
                is Prumer -> copy(prumer = novaVec)
                is Sirka -> copy(sirka = novaVec)
                is DobaVyuzivani -> copy(dobaVyuzivani = novaVec)
                is DruhVody -> copy(druhVody = novaVec)
                is Tvar -> copy(tvar = novaVec)
                is Umisteni -> copy(umisteni = novaVec)
                is Zakryti -> copy(zakryti = novaVec)
                is ChciBazen -> copy(chciBazen = novaVec)
                else -> this
            }

            @Serializable
            data class ChciBazen(
                override val zaskrtnuto: Boolean? = null,
            ) : Zaskrtavatko {
                override fun zaskrtnuto(zaskrtnuto: Boolean?) = copy(zaskrtnuto = zaskrtnuto)
                context(Stranky) override val popis get() = strings.systemOhrevBazenu
            }

            @Serializable
            data class DobaVyuzivani(
                override val vybranoIndex: Int? = null,
            ) : Vybiratko {
                override fun vybranoIndex(vybranoIndex: Int?) = copy(vybranoIndex = vybranoIndex)
                context(Stranky) override val popis get() = strings.bazenDobaUzivani
                context(Stranky) override val moznosti
                    get() = listOf(
                        strings.bazenDobaCelorocni,
                        strings.bazenDobaSezonni,
                    )

                context(Stranky) override val zobrazit get() = bazen.chciBazen.zaskrtnutoOrDefault
            }

            @Serializable
            data class Umisteni(
                override val vybranoIndex: Int? = null,
            ) : Vybiratko {
                override fun vybranoIndex(vybranoIndex: Int?) = copy(vybranoIndex = vybranoIndex)
                context(Stranky) override val popis get() = strings.bazenUmisteni
                context(Stranky) override val moznosti
                    get() = listOf(
                        strings.bazenUmisteniVenkovni,
                        strings.bazenUmisteniVnitrni,
                    )

                context(Stranky) override val zobrazit get() = bazen.chciBazen.zaskrtnutoOrDefault
            }

            @Serializable
            data class DruhVody(
                override val vybranoIndex: Int? = null,
            ) : Vybiratko {
                override fun vybranoIndex(vybranoIndex: Int?) = copy(vybranoIndex = vybranoIndex)
                context(Stranky) override val popis get() = strings.bazenDruhVody
                context(Stranky) override val moznosti
                    get() = listOf(
                        strings.bazenDruhSladka,
                        strings.bazenDruhSlana,
                    )

                context(Stranky) override val zobrazit get() = bazen.chciBazen.zaskrtnutoOrDefault
            }

            @Serializable
            data class Tvar(
                override val vybranoIndex: Int? = null,
            ) : Vybiratko {
                override fun vybranoIndex(vybranoIndex: Int?) = copy(vybranoIndex = vybranoIndex)
                context(Stranky) override val popis get() = strings.bazenTvar
                context(Stranky) override val moznosti
                    get() = listOf(
                        strings.bazenTvarObdelnikovy,
                        strings.bazenTvarOvalny,
                        strings.bazenTvarKruhovy,
                    )

                context(Stranky) override val zobrazit get() = bazen.chciBazen.zaskrtnutoOrDefault
            }

            @Serializable
            data class Delka(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.bazenDelka
                context(Stranky) override val suffix get() = strings.m
                context(Stranky) override val zobrazit get() = bazen.chciBazen.zaskrtnutoOrDefault && bazen.tvar.vybrano != strings.bazenTvarKruhovy

                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
            }

            @Serializable
            data class Sirka(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.bazenSirka
                context(Stranky) override val suffix get() = strings.m
                context(Stranky) override val zobrazit get() = bazen.chciBazen.zaskrtnutoOrDefault && bazen.tvar.vybrano != strings.bazenTvarKruhovy

                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
            }

            @Serializable
            data class Prumer(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.bazenRadius
                context(Stranky) override val suffix get() = strings.m
                context(Stranky) override val zobrazit get() = bazen.chciBazen.zaskrtnutoOrDefault && bazen.tvar.vybrano == strings.bazenTvarKruhovy

                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
            }

            @Serializable
            data class Hloubka(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.bazenHloubka
                context(Stranky) override val suffix get() = strings.m
                context(Stranky) override val zobrazit get() = bazen.chciBazen.zaskrtnutoOrDefault
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
            }

            @Serializable
            data class Zakryti(
                override val vybranoIndex: Int? = null,
            ) : Vybiratko {
                override fun vybranoIndex(vybranoIndex: Int?) = copy(vybranoIndex = vybranoIndex)
                context(Stranky) override val popis get() = strings.bazenZakryti
                context(Stranky) override val moznosti
                    get() = listOf(
                        strings.zadne,
                        strings.bazenZakrytiPevna,
                        strings.bazenZakrytiPolykarbonat,
                        strings.jine,
                    )

                context(Stranky) override val zobrazit get() = bazen.chciBazen.zaskrtnutoOrDefault
            }

            @Serializable
            data class PozadovanaTeplota(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.bazenTeplota
                context(Stranky) override val suffix get() = strings.stupenC
                context(Stranky) override val zobrazit get() = bazen.chciBazen.zaskrtnutoOrDefault
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
            }

            @Serializable
            data class Poznamka(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.poznamka
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
            }

            context(Stranky) override val nazev get() = strings.bazen
            context(Stranky) override val icon get() = Icons.Default.Pool
            context(Stranky) override val veci
                get() = listOf(
                    listOf(
                        chciBazen,
                        dobaVyuzivani,
                        umisteni,
                        druhVody,
                    ),
                    listOf(
                        tvar,
                        delka,
                        sirka,
                        prumer,
                        hloubka,
                    ),
                    listOf(
                        zakryti,
                        pozadovanaTeplota,
                    ),
                    listOf(poznamka),
                )
        }

        @Serializable
        data class DoplnkoveZdroje(
            val topneTelesoVNadrziTopeni: TopneTelesoVNadrziTopeni = TopneTelesoVNadrziTopeni(),
            val elektrokotelTopeni: ElektrokotelTopeni = ElektrokotelTopeni(),
            val plynovyKotelTopeni: PlynovyKotelTopeni = PlynovyKotelTopeni(),
            val krbTopeni: KrbTopeni = KrbTopeni(),
            val jinyTopeni: JinyTopeni = JinyTopeni(),
            val topneTelesoVNadrziTeplaVoda: TopneTelesoVNadrziTeplaVoda = TopneTelesoVNadrziTeplaVoda(),
            val elektrokotelTeplaVoda: ElektrokotelTeplaVoda = ElektrokotelTeplaVoda(),
            val plynovyKotelTeplaVoda: PlynovyKotelTeplaVoda = PlynovyKotelTeplaVoda(),
            val krbTeplaVoda: KrbTeplaVoda = KrbTeplaVoda(),
            val jinyTeplaVoda: JinyTeplaVoda = JinyTeplaVoda(),
            val poznamka: Poznamka = Poznamka(),
        ) : Stranka {
            override fun kopirovatVec(novaVec: Vec) = when (novaVec) {
                is JinyTeplaVoda -> copy(jinyTeplaVoda = novaVec)
                is JinyTopeni -> copy(jinyTopeni = novaVec)
                is Poznamka -> copy(poznamka = novaVec)
                is ElektrokotelTeplaVoda -> copy(elektrokotelTeplaVoda = novaVec)
                is KrbTeplaVoda -> copy(krbTeplaVoda = novaVec)
                is PlynovyKotelTeplaVoda -> copy(plynovyKotelTeplaVoda = novaVec)
                is ElektrokotelTopeni -> copy(elektrokotelTopeni = novaVec)
                is KrbTopeni -> copy(krbTopeni = novaVec)
                is PlynovyKotelTopeni -> copy(plynovyKotelTopeni = novaVec)
                is TopneTelesoVNadrziTeplaVoda -> copy(topneTelesoVNadrziTeplaVoda = novaVec)
                is TopneTelesoVNadrziTopeni -> copy(topneTelesoVNadrziTopeni = novaVec)
                else -> this
            }

            @Serializable
            data object NadpisTopeni : Nadpis {
                context(Stranky) override val text get() = strings.zdrojeTop
            }

            @Serializable
            data class TopneTelesoVNadrziTopeni(
                override val zaskrtnuto: Boolean? = null,
                override val vybranoIndex: Int? = null,
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean?) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybranoIndex(vybranoIndex: Int?) = copy(vybranoIndex = vybranoIndex)
                context(Stranky) override val popis get() = strings.zdrojeTopneTeleso
                context(Stranky) override val moznosti get() = listOf(strings.zdrojeStavajici, strings.zdrojeNove)
            }

            @Serializable
            data class ElektrokotelTopeni(
                override val zaskrtnuto: Boolean? = null,
                override val vybranoIndex: Int? = null,
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean?) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybranoIndex(vybranoIndex: Int?) = copy(vybranoIndex = vybranoIndex)
                context(Stranky) override val popis get() = strings.zdrojeElektrokotel
                context(Stranky) override val moznosti get() = listOf(strings.zdrojeStavajici, strings.zdrojeNovy)
            }

            @Serializable
            data class PlynovyKotelTopeni(
                override val zaskrtnuto: Boolean? = null,
                override val vybranoIndex: Int? = null,
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean?) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybranoIndex(vybranoIndex: Int?) = copy(vybranoIndex = vybranoIndex)
                context(Stranky) override val popis get() = strings.zdrojePlynKotel
                context(Stranky) override val moznosti get() = listOf(strings.zdrojeStavajici, strings.zdrojeNovy)
            }

            @Serializable
            data class KrbTopeni(
                override val zaskrtnuto: Boolean? = null,
                override val vybranoIndex: Int? = null,
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean?) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybranoIndex(vybranoIndex: Int?) = copy(vybranoIndex = vybranoIndex)
                context(Stranky) override val popis get() = strings.zdrojeKrb
                context(Stranky) override val moznosti get() = listOf(strings.zdrojeStavajici, strings.zdrojeNovy)
            }

            @Serializable
            data class JinyTopeni(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.jiny
            }

            @Serializable
            data object NadpisTeplaVoda : Nadpis {
                context(Stranky) override val text get() = strings.zdrojeTv
            }

            @Serializable
            data class TopneTelesoVNadrziTeplaVoda(
                override val zaskrtnuto: Boolean? = null,
                override val vybranoIndex: Int? = null,
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean?) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybranoIndex(vybranoIndex: Int?) = copy(vybranoIndex = vybranoIndex)
                context(Stranky) override val popis get() = strings.zdrojeTopneTeleso
                context(Stranky) override val moznosti get() = listOf(strings.zdrojeTvDoZasuvky, strings.zdrojeTvZRegulace)
            }

            @Serializable
            data class ElektrokotelTeplaVoda(
                override val zaskrtnuto: Boolean? = null,
            ) : Zaskrtavatko {
                override fun zaskrtnuto(zaskrtnuto: Boolean?) = copy(zaskrtnuto = zaskrtnuto)
                context(Stranky) override val popis get() = strings.zdrojeElektrokotel
            }

            @Serializable
            data class PlynovyKotelTeplaVoda(
                override val zaskrtnuto: Boolean? = null,
            ) : Zaskrtavatko {
                override fun zaskrtnuto(zaskrtnuto: Boolean?) = copy(zaskrtnuto = zaskrtnuto)
                context(Stranky) override val popis get() = strings.zdrojePlynKotel
            }

            @Serializable
            data class KrbTeplaVoda(
                override val zaskrtnuto: Boolean? = null,
            ) : Zaskrtavatko {
                override fun zaskrtnuto(zaskrtnuto: Boolean?) = copy(zaskrtnuto = zaskrtnuto)
                context(Stranky) override val popis get() = strings.zdrojeKrb
            }

            @Serializable
            data class JinyTeplaVoda(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.jiny
            }

            @Serializable
            data class Poznamka(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.poznamka
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
            }

            context(Stranky) override val nazev get() = strings.zdroje
            context(Stranky) override val icon get() = Icons.Default.AcUnit
            context(Stranky) override val veci
                get() = listOf(
                    listOf(
                        NadpisTopeni,
                        topneTelesoVNadrziTopeni,
                        elektrokotelTopeni,
                        plynovyKotelTopeni,
                        krbTopeni,
                        jinyTopeni
                    ),
                    listOf(
                        NadpisTeplaVoda,
                        topneTelesoVNadrziTeplaVoda,
                        elektrokotelTeplaVoda,
                        plynovyKotelTeplaVoda,
                        krbTeplaVoda,
                        jinyTeplaVoda
                    ),
                    listOf(poznamka),
                )
        }

        @Serializable
        data class Prislusenstvi(
            val hadice: Hadice = Hadice(),
            val topnyKabel: TopnyKabel = TopnyKabel(),
            val drzakNaStenu: DrzakNaStenu = DrzakNaStenu(),
            val pokojovaJednotka: PokojovaJednotka = PokojovaJednotka(),
            val pokojoveCidlo: PokojoveCidlo = PokojoveCidlo(),
            val poznamka: Poznamka = Poznamka(),
        ) : Stranka {
            override fun kopirovatVec(novaVec: Vec) = when (novaVec) {
                is Poznamka -> copy(poznamka = novaVec)
                is DrzakNaStenu -> copy(drzakNaStenu = novaVec)
                is Hadice -> copy(hadice = novaVec)
                is PokojovaJednotka -> copy(pokojovaJednotka = novaVec)
                is PokojoveCidlo -> copy(pokojoveCidlo = novaVec)
                is TopnyKabel -> copy(topnyKabel = novaVec)
                else -> this
            }

            @Serializable
            data class Hadice(
                override val zaskrtnuto: Boolean? = null,
                override val vybranoIndex: Int? = null,
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean?) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybranoIndex(vybranoIndex: Int?) = copy(vybranoIndex = vybranoIndex)
                context(Stranky) override val popis get() = strings.prislusenstviHadice
                context(Stranky) override val moznosti get() = listOf("300 mm", "500 mm", "700 mm", "1000 mm")
                context(Stranky) override val default get() = "500 mm"
            }

            @Serializable
            data class TopnyKabel(
                override val zaskrtnuto: Boolean? = null,
                override val vybranoIndex: Int? = null,
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean?) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybranoIndex(vybranoIndex: Int?) = copy(vybranoIndex = vybranoIndex)
                context(Stranky) override val popis get() = strings.prislusenstviTopnyKabel
                context(Stranky) override val moznosti get() = listOf("3,5 m", "5 m")
            }

            @Serializable
            data class DrzakNaStenu(
                override val zaskrtnuto: Boolean? = null,
                override val vybranoIndex: Int? = null,
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean?) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybranoIndex(vybranoIndex: Int?) = copy(vybranoIndex = vybranoIndex)
                context(Stranky) override val popis get() = strings.prislusenstviDrzakProTc
                context(Stranky) override val moznosti get() = listOf(strings.naStenu, strings.naIzolovanouStenu)
            }

            @Serializable
            data class PokojovaJednotka(
                override val zaskrtnuto: Boolean? = null,
            ) : Zaskrtavatko {
                override fun zaskrtnuto(zaskrtnuto: Boolean?) = copy(zaskrtnuto = zaskrtnuto)
                context(Stranky) override val popis get() = strings.prislusenstviPokojovaJednotka + " RC 25"
            }

            @Serializable
            data class PokojoveCidlo(
                override val zaskrtnuto: Boolean? = null,
                override val vybranoIndex: Int? = null,
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean?) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybranoIndex(vybranoIndex: Int?) = copy(vybranoIndex = vybranoIndex)
                context(Stranky) override val popis get() = strings.prislusenstviPokojoveCidlo
                context(Stranky) override val moznosti get() = listOf("RS 10", "RSW 30 - WiFi")
            }

            @Serializable
            data class Poznamka(
                override val text: String? = null,
            ) : Pisatko {
                override fun text(text: String?) = copy(text = text)
                context(Stranky) override val popis get() = strings.poznamka
                context(Stranky) override val klavesnice
                    get() = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
            }

            context(Stranky) override val nazev get() = strings.prislusenstvi
            context(Stranky) override val icon get() = Icons.Default.AddShoppingCart
            context(Stranky) override val veci
                get() = listOf(
                    listOf(
                        hadice,
                        topnyKabel,
                        drzakNaStenu,
                        pokojovaJednotka,
                        pokojoveCidlo,
                    ),
                    listOf(poznamka),
                )
        }
    }
}

fun Stranky.createXml(
    uzivatel: Uzivatel,
) = """
    <?xml version="1.0" encoding="utf-8"?>
    <?xml-stylesheet type="text/xsl" href="dotaznik_app.xsl"?>
    
    <!-- Tento soubor byl vygenerován automaticky aplikací Regulus Dotazník; verze: 2.2 -->
    
    <xml>
        <system>
            <resi_tc>Ano</resi_tc>
            <cislo_ko>${uzivatel.cisloKo}</cislo_ko>
            <odesilatel>${uzivatel.email}</odesilatel>
            <odberatel_ico>${uzivatel.ico}</odberatel_ico>
        </system>
        <kontakt>
            <jmeno>${kontakty.jmeno.text}</jmeno>
            <prijmeni>${kontakty.prijmeni.text}</prijmeni>
            <telefon>${kontakty.telefon.text}</telefon>
            <email>${kontakty.email.text}</email>
            <ulice>${kontakty.ulice.text}</ulice>
            <psc>${
    if (kontakty.psc.textOrDefault.length != 5) ""
    else kontakty.psc.textOrDefault.substring(0, 3) + " " + kontakty.psc.textOrDefault.substring(3, 5)
}</psc>
            <mesto>${kontakty.mesto.text}</mesto>
            <partner_ico>${kontakty.montazniFirma.ico}</partner_ico>
        </kontakt>
        <detailobjektu>
            <os_popis>${system.otopnySystem.vybrano}</os_popis>
            <tepelna_ztrata>${detailObjektu.tepelnaZtrata.text}</tepelna_ztrata>
            <rocni_spotreba_vytapeni>${detailObjektu.potrebaTeplaNaVytapeni.text}</rocni_spotreba_vytapeni>
            <rocni_spotreba_tv>${detailObjektu.potrebaTeplaNaTeplouVodu.text}</rocni_spotreba_tv>
            <vytapena_plocha>${detailObjektu.vytapenaPlocha.text}</vytapena_plocha>
            <vytapeny_objem>${detailObjektu.vytapenyObjem.text}</vytapeny_objem>
            <spotreba_paliva_druh>${detailObjektu.druhPaliva.text}</spotreba_paliva_druh>
            <spotreba_paliva_mnozstvi>${detailObjektu.spotrebaPaliva.text}</spotreba_paliva_mnozstvi>
            <spotreba_paliva_jednotky>${detailObjektu.spotrebaPaliva.vybraneJednotky}</spotreba_paliva_jednotky>
            <spotreba_paliva_2_druh>${detailObjektu.druhPaliva2.text}</spotreba_paliva_2_druh>
            <spotreba_paliva_2_mnozstvi>${detailObjektu.spotrebaPaliva2.text}</spotreba_paliva_2_mnozstvi>
            <spotreba_paliva_2_jednotky>${detailObjektu.spotrebaPaliva2.vybraneJednotky}</spotreba_paliva_2_jednotky>
            <rocni_platba_vytapeni>${detailObjektu.nakladyNaVytapeni.text} ${strings.mena}</rocni_platba_vytapeni>
        </detailobjektu>
        <tc>
            <typ>${system.typTC.vybrano}</typ>
            <model>${system.modelTC.vybrano}</model>
            <nadrz>${system.typNadrze.vybrano1} ${system.typNadrze.vybrano2} ${system.objemNadrze.text}</nadrz>
            <vnitrni_jednotka>${system.typVnitrniJednotky.vybrano}</vnitrni_jednotka>
        </tc>
        <zdrojeTop>
            <topne_teleso>${doplnkoveZdroje.topneTelesoVNadrziTopeni.text}</topne_teleso>
            <elektrokotel>${doplnkoveZdroje.elektrokotelTopeni.text}</elektrokotel>
            <plyn_kotel>${doplnkoveZdroje.plynovyKotelTopeni.text}</plyn_kotel>
            <krb_KTP>${doplnkoveZdroje.krbTopeni.text}</krb_KTP>
            <jiny_zdroj>${doplnkoveZdroje.jinyTopeni.text}</jiny_zdroj>
        </zdrojeTop>
        <tv>
            <zasobnik>${system.typZasobniku.vybrano} ${system.objemZasobniku.text}</zasobnik>
            <cirkulace>${if (system.cirkulaceTepleVody.zaskrtnutoOrDefault) "Ano" else "Ne"}</cirkulace>
        </tv>
        <zdrojeTV>
            <topne_teleso>${doplnkoveZdroje.topneTelesoVNadrziTeplaVoda.text}</topne_teleso>
            <elektrokotel>${if (doplnkoveZdroje.elektrokotelTeplaVoda.zaskrtnutoOrDefault) "Ano" else "Ne"}</elektrokotel>
            <plyn_kotel>${if (doplnkoveZdroje.plynovyKotelTeplaVoda.zaskrtnutoOrDefault) "Ano" else "Ne"}</plyn_kotel>
            <krb_KTP>${if (doplnkoveZdroje.krbTeplaVoda.zaskrtnutoOrDefault) "Ano" else "Ne"}</krb_KTP>
            <jiny_zdroj>${doplnkoveZdroje.jinyTeplaVoda.text}</jiny_zdroj>
        </zdrojeTV>
        <bazen>
            <ohrev>${if (bazen.chciBazen.zaskrtnutoOrDefault) "Ano" else "Ne"}</ohrev>
            <doba_vyuzivani>${if (bazen.chciBazen.zaskrtnutoOrDefault) bazen.dobaVyuzivani.vybrano else ""}</doba_vyuzivani>
            <umisteni>${if (bazen.chciBazen.zaskrtnutoOrDefault) bazen.umisteni.vybrano else ""}</umisteni>
            <zakryti>${if (bazen.chciBazen.zaskrtnutoOrDefault) bazen.zakryti.vybrano else ""}</zakryti>
            <tvar>${if (bazen.chciBazen.zaskrtnutoOrDefault) bazen.tvar.vybrano else ""}</tvar>
            <sirka>${bazen.sirka.text}</sirka>
            <delka>${bazen.delka.text}</delka>
            <hloubka>${bazen.hloubka.text}</hloubka>
            <prumer>${bazen.prumer.text}</prumer>
            <teplota>${bazen.pozadovanaTeplota.text}</teplota>
            <voda>${if (bazen.chciBazen.zaskrtnutoOrDefault) bazen.druhVody.vybrano else ""}</voda>
        </bazen>
        <prislusenstvi>
            <hadice>${prislusenstvi.hadice.text}</hadice>
            <topny_kabel>${prislusenstvi.topnyKabel.text}</topny_kabel>
            <drzak_na_tc>${prislusenstvi.drzakNaStenu.text}</drzak_na_tc>
            <pokojova_jednotka>${if (prislusenstvi.pokojovaJednotka.zaskrtnutoOrDefault) "RC 25" else "Ne"}</pokojova_jednotka>
            <pokojove_cidlo>${prislusenstvi.pokojoveCidlo.text}</pokojove_cidlo>
        </prislusenstvi>
        <poznamka>
            <kontakty>${kontakty.poznamka.text}</kontakty>
            <detail_objektu>${detailObjektu.poznamka.text}</detail_objektu>
            <tv_tc_nadrz_a_os>${system.poznamka.text}</tv_tc_nadrz_a_os>
            <bazen>${bazen.poznamka.text}</bazen>
            <doplnkove_zdroje>${doplnkoveZdroje.poznamka.text}</doplnkove_zdroje>
            <prislusenstvi>${prislusenstvi.poznamka.text}</prislusenstvi>
        </poznamka>
    </xml>
""".trimIndent()