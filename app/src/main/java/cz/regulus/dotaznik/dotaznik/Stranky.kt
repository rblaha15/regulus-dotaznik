package cz.regulus.dotaznik.dotaznik

import android.content.res.Resources
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
import cz.regulus.dotaznik.R
import cz.regulus.dotaznik.Text
import cz.regulus.dotaznik.Uzivatel
import cz.regulus.dotaznik.dotaznik.Stranky.Stranka.Vec.DvojVybiratko
import cz.regulus.dotaznik.dotaznik.Stranky.Stranka.Vec.Jine
import cz.regulus.dotaznik.dotaznik.Stranky.Stranka.Vec.Nadpis
import cz.regulus.dotaznik.dotaznik.Stranky.Stranka.Vec.Pisatko
import cz.regulus.dotaznik.dotaznik.Stranky.Stranka.Vec.PisatkoSJednotkama
import cz.regulus.dotaznik.dotaznik.Stranky.Stranka.Vec.Vybiratko
import cz.regulus.dotaznik.dotaznik.Stranky.Stranka.Vec.Zaskrtavatko
import cz.regulus.dotaznik.dotaznik.Stranky.Stranka.Vec.ZaskrtavatkoSVybiratkem
import cz.regulus.dotaznik.plus
import cz.regulus.dotaznik.toText
import kotlinx.serialization.Serializable
import cz.regulus.dotaznik.R.string as Strings

@Serializable
data class Stranky(
    val kontakty: Stranka.Kontakty = Stranka.Kontakty(),
    val detailObjektu: Stranka.DetailObjektu = Stranka.DetailObjektu(),
    val system: Stranka.System = Stranka.System(),
    val bazen: Stranka.Bazen = Stranka.Bazen(),
    val doplnkoveZdroje: Stranka.DoplnkoveZdroje = Stranka.DoplnkoveZdroje(),
    val prislusenstvi: Stranka.Prislusenstvi = Stranka.Prislusenstvi(),
) {

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

        val nazev: Text
        val icon: ImageVector
        val veci: List<List<Vec>>

        @Serializable
        sealed interface Vec {
            val zobrazit: (Stranky) -> Boolean

            @Serializable
            sealed interface Pisatko : Vec {
                val popis: Text
                val suffix: Text get() = "".toText()
                val klavesnice: KeyboardOptions get() = KeyboardOptions(imeAction = ImeAction.Next)
                val text: String get() = ""
                override val zobrazit: (Stranky) -> Boolean get() = { true }

                fun text(text: String): Pisatko
            }

            @Serializable
            sealed interface PisatkoSJednotkama : Vec {
                val popis: Text
                val jednotky: List<Text>
                val klavesnice: KeyboardOptions get() = KeyboardOptions(imeAction = ImeAction.Next)
                val text: String get() = ""
                val vybraneJednotky: Text get() = jednotky.first()
                override val zobrazit: (Stranky) -> Boolean get() = { true }

                fun text(text: String): PisatkoSJednotkama
                fun vybraneJednotky(vybraneJednotky: Text): PisatkoSJednotkama
            }

            @Serializable
            sealed interface Vybiratko : Vec {
                val popis: Text
                val moznosti: (Stranky) -> List<Text>
                val vybrano: Text
                override val zobrazit: (Stranky) -> Boolean get() = { true }

                fun vybrano(vybrano: Text): Vybiratko
            }

            @Serializable
            sealed interface DvojVybiratko : Vec {
                val popis: Text
                val moznosti1: (Stranky) -> List<Text>
                val moznosti2: (Stranky) -> List<Text>
                val vybrano1: Text
                val vybrano2: Text
                override val zobrazit: (Stranky) -> Boolean get() = { true }

                fun vybrano1(vybrano1: Text): DvojVybiratko
                fun vybrano2(vybrano2: Text): DvojVybiratko
            }

            @Serializable
            sealed interface Zaskrtavatko : Vec {
                val popis: Text
                val zaskrtnuto: Boolean get() = false
                override val zobrazit: (Stranky) -> Boolean get() = { true }

                fun zaskrtnuto(zaskrtnuto: Boolean): Zaskrtavatko
            }

            @Serializable
            sealed interface ZaskrtavatkoSVybiratkem : Vec {
                val popis: Text
                val moznosti: (Stranky) -> List<Text>
                val vybrano: Text
                val zaskrtnuto: Boolean get() = false
                override val zobrazit: (Stranky) -> Boolean get() = { true }

                val text get() = if (zaskrtnuto) vybrano else "Ne".toText()

                fun zaskrtnuto(zaskrtnuto: Boolean): ZaskrtavatkoSVybiratkem
                fun vybrano(vybrano: Text): ZaskrtavatkoSVybiratkem
            }

            @Serializable
            sealed interface Jine : Vec {
                override val zobrazit: (Stranky) -> Boolean get() = { true }
            }

            @Serializable
            data class Nadpis(
                val text: Text,
            ) : Vec {

                override val zobrazit: (Stranky) -> Boolean get() = { true }
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
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.kontakty_prijmeni.toText()
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    )
            }

            @Serializable
            data class Jmeno(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.kontakty_jmeno.toText()
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    )
            }

            @Serializable
            data class Ulice(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.kontakty_ulice.toText()
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    )
            }

            @Serializable
            data class Mesto(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.kontakty_mesto.toText()
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    )
            }

            @Serializable
            data class Psc(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.kontakty_psc.toText()
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
            }

            @Serializable
            data class Telefon(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.kontakty_telefon.toText()
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    )
            }

            @Serializable
            data class Email(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.kontakty_email.toText()
                override val klavesnice: KeyboardOptions
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
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.poznamka.toText()
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
            }

            override val nazev get() = Strings.kontakty.toText()
            override val icon get() = Icons.Default.Person
            override val veci
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
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.detail_objektu_ztrata.toText()
                override val suffix get() = Strings.kW.toText()
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
            }

            @Serializable
            data class PotrebaTeplaNaVytapeni(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.detail_objektu_potreba_vytapeni.toText()
                override val suffix get() = Strings.kWh.toText()
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
            }

            @Serializable
            data class PotrebaTeplaNaTeplouVodu(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.detail_objektu_potreba_tv.toText()
                override val suffix get() = Strings.kWh.toText()
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
            }

            @Serializable
            data class VytapenaPlocha(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.detail_objektu_plocha.toText()
                override val suffix get() = Strings.m2.toText()
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
            }

            @Serializable
            data class VytapenyObjem(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.detail_objektu_objem.toText()
                override val suffix get() = Strings.m3.toText()
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
            }

            @Serializable
            data class NakladyNaVytapeni(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.detail_objektu_naklady.toText()
                override val suffix get() = Strings.mena.toText()
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
            }

            @Serializable
            data class DruhPaliva(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.detail_objektu_druh.toText()
            }

            @Serializable
            data class SpotrebaPaliva(
                override val text: String = "",
                override val vybraneJednotky: Text = Strings.q.toText(),
            ) : PisatkoSJednotkama {
                override fun text(text: String) = copy(text = text)
                override fun vybraneJednotky(vybraneJednotky: Text) = copy(vybraneJednotky = vybraneJednotky)
                override val popis get() = Strings.detail_objektu_spotreba.toText()
                override val jednotky get() = listOf(Strings.q.toText(), Strings.m3.toText(), Strings.kWh.toText())
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
            }

            @Serializable
            data class DruhPaliva2(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.detail_objektu_druh2.toText()
            }

            @Serializable
            data class SpotrebaPaliva2(
                override val text: String = "",
                override val vybraneJednotky: Text = Strings.q.toText(),
            ) : PisatkoSJednotkama {
                override fun vybraneJednotky(vybraneJednotky: Text) = copy(vybraneJednotky = vybraneJednotky)
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.detail_objektu_spotreba2.toText()
                override val jednotky get() = listOf(Strings.q.toText(), Strings.m3.toText(), Strings.kWh.toText())
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
            }

            @Serializable
            data class Poznamka(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.poznamka.toText()
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
            }

            override val nazev get() = Strings.detail_objektu.toText()
            override val icon get() = Icons.Default.Home
            override val veci
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
                override val vybrano: Text = Strings.vzduch_voda.toText(),
            ) : Vybiratko {
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.system_tc_typ.toText()
                override val moznosti
                    get() = { _: Stranky ->
                        listOf(Strings.vzduch_voda.toText(), Strings.zeme_voda.toText())
                    }
            }

            @Serializable
            data class ModelTC(
                override val vybrano: Text = Strings.vyberte.toText(),
            ) : Vybiratko {
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.system_tc_model.toText()
                override val moznosti
                    get() = { it: Stranky ->
                        when (it.system.typTC.vybrano) {
                            Strings.vzduch_voda.toText() -> listOf(
                                Strings.vyberte.toText(),
                                "RTC".toText(),
                                "RTC 6i".toText(),
                                "RTC 13e".toText(),
                                "RTC 20e".toText(),
                                "ECOAIR 600M".toText(),
                                "ECOAIR 614M".toText(),
                                "ECOAIR 622M".toText(),
                                "ECOAIR 510M".toText(),
                                "ECOAIR 400".toText(),
                                "ECOAIR 406".toText(),
                                "ECOAIR 408".toText(),
                                "ECOAIR 410".toText(),
                                "ECOAIR 415".toText(),
                                "ECOAIR 420".toText(),
                            )

                            Strings.zeme_voda.toText() -> listOf(
                                Strings.vyberte.toText(),
                                "ECOPART 400".toText(),
                                "ECOPART 406".toText(),
                                "ECOPART 408".toText(),
                                "ECOPART 410".toText(),
                                "ECOPART 412".toText(),
                                "ECOPART 414".toText(),
                                "ECOPART 417".toText(),
                                "ECOPART 435".toText(),
                                "ECOPART 600M".toText(),
                                "ECOPART 612M".toText(),
                                "ECOPART 616M".toText(),
                                "ECOHEAT 400".toText(),
                                "ECOHEAT 406".toText(),
                                "ECOHEAT 408".toText(),
                                "ECOHEAT 410".toText(),
                                "ECOHEAT 412".toText(),
                            )

                            else -> throw IllegalArgumentException()
                        }
                    }
            }

            @Serializable
            data class TypVnitrniJednotky(
                override val vybrano: Text = Strings.zadna.toText(),
            ) : Vybiratko {
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.system_typ_jednotky.toText()
                override val moznosti
                    get() = { _: Stranky ->
                        listOf(
                            Strings.zadna.toText(),
                            "RegulusBOX".toText(),
                            "RegulusHBOX 112".toText(),
                            "RegulusHBOX 212".toText(),
                        )
                    }
            }

            @Serializable
            data class TypNadrze(
                override val vybrano1: Text = Strings.zadna.toText(),
                override val vybrano2: Text = "".toText(),
            ) : DvojVybiratko {
                override val popis get() = Strings.system_nadrz_typ.toText()
                override val moznosti1
                    get() = { _: Stranky ->
                        listOf(
                            Strings.zadna.toText(),
                            "DUO".toText(),
                            "HSK".toText(),
                            "PS".toText(),
                        )
                    }
                override val moznosti2
                    get() = { it: Stranky ->
                        when (it.system.typNadrze.vybrano1) {
                            "DUO".toText() -> listOf(
                                "-".toText(),
                                "P".toText(),
                                "PR".toText(),
                                "K".toText(),
                                "K P".toText(),
                                "K PR".toText(),
                            )

                            "HSK".toText() -> listOf(
                                "P".toText(),
                                "P+".toText(),
                                "PR".toText(),
                                "PR+".toText(),
                                "PV".toText(),
                            )

                            "PS".toText() -> listOf(
                                "E+".toText(),
                                "ES+".toText(),
                                "N+".toText(),
                                "K+".toText(),
                                "2F".toText(),
                                "WF".toText(),
                            )

                            else -> emptyList()
                        }
                    }

                override fun vybrano1(vybrano1: Text) = copy(vybrano1 = vybrano1)
                override fun vybrano2(vybrano2: Text) = copy(vybrano2 = vybrano2)
            }

            @Serializable
            data class ObjemNadrze(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.system_nadrz_objem.toText()
                override val suffix get() = Strings.dm3.toText()
                override val zobrazit
                    get() = { it: Stranky ->
                        it.system.typNadrze.vybrano1 != Strings.zadna.toText()
                    }
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
            }

            @Serializable
            data class TypZasobniku(
                override val vybrano: Text = Strings.zadny.toText(),
            ) : Vybiratko {
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.system_zasobnik_typ.toText()
                override val moznosti
                    get() = { _: Stranky ->
                        listOf(
                            Strings.zadny.toText(),
                            "RDC".toText(),
                            "R2DC".toText(),
                            "R0BC".toText(),
                            "RBC".toText(),
                            "RBC HP".toText(),
                            "R2BC".toText(),
                            "NBC".toText(),
                            "RGC".toText(),
                        )
                    }
            }

            @Serializable
            data class ObjemZasobniku(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.sytem_zasobnik_objem.toText()
                override val suffix get() = Strings.dm3.toText()
                override val zobrazit
                    get() = { it: Stranky ->
                        it.system.typZasobniku.vybrano != Strings.zadny.toText()
                    }
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
            }

            @Serializable
            data class OtopnySystem(
                override val vybrano: Text = Strings.system_os_1okruh.toText(),
            ) : Vybiratko {
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.system_otopny_system.toText()
                override val moznosti
                    get() = { _: Stranky ->
                        listOf(
                            Strings.system_os_1okruh.toText(),
                            Strings.system_os_2okruhy.toText(),
                            Strings.system_os_3okruhy.toText(),
                            Strings.system_os_invertor.toText(),
                            Strings.jiny.toText(),
                        )
                    }
            }

            @Serializable
            data class CirkulaceTepleVody(
                override val zaskrtnuto: Boolean = false,
            ) : Zaskrtavatko {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override val popis: Text get() = Strings.system_cirkulace.toText()
            }

            @Serializable
            data class Poznamka(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.poznamka.toText()
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
            }

            override val nazev get() = Strings.system.toText()
            override val icon get() = Icons.Default.Category
            override val veci
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
                override val zaskrtnuto: Boolean = false,
            ) : Zaskrtavatko {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override val popis: Text get() = Strings.system_ohrev_bazenu.toText()
            }

            @Serializable
            data class DobaVyuzivani(
                override val vybrano: Text = Strings.bazen_doba_celorocni.toText(),
            ) : Vybiratko {
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.bazen_doba_uzivani.toText()
                override val moznosti
                    get() = { _: Stranky ->
                        listOf(
                            Strings.bazen_doba_celorocni.toText(),
                            Strings.bazen_doba_sezonni.toText(),
                        )
                    }
                override val zobrazit get() = { it: Stranky -> it.bazen.chciBazen.zaskrtnuto }
            }

            @Serializable
            data class Umisteni(
                override val vybrano: Text = Strings.bazen_umisteni_venkovni.toText(),
            ) : Vybiratko {
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.bazen_umisteni.toText()
                override val moznosti
                    get() = { _: Stranky ->
                        listOf(
                            Strings.bazen_umisteni_venkovni.toText(),
                            Strings.bazen_umisteni_vnitrni.toText(),
                        )
                    }
                override val zobrazit get() = { it: Stranky -> it.bazen.chciBazen.zaskrtnuto }
            }

            @Serializable
            data class DruhVody(
                override val vybrano: Text = Strings.bazen_druh_sladka.toText(),
            ) : Vybiratko {
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.bazen_druh_vody.toText()
                override val moznosti
                    get() = { _: Stranky ->
                        listOf(
                            Strings.bazen_druh_sladka.toText(),
                            Strings.bazen_druh_slana.toText(),
                        )
                    }
                override val zobrazit get() = { it: Stranky -> it.bazen.chciBazen.zaskrtnuto }
            }

            @Serializable
            data class Tvar(
                override val vybrano: Text = Strings.bazen_tvar_obdelnikovy.toText(),
            ) : Vybiratko {
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.bazen_tvar.toText()
                override val moznosti
                    get() = { _: Stranky ->
                        listOf(
                            Strings.bazen_tvar_obdelnikovy.toText(),
                            Strings.bazen_tvar_ovalny.toText(),
                            Strings.bazen_tvar_kruhovy.toText(),
                        )
                    }
                override val zobrazit get() = { it: Stranky -> it.bazen.chciBazen.zaskrtnuto }
            }

            @Serializable
            data class Delka(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.bazen_delka.toText()
                override val suffix get() = Strings.m.toText()
                override val zobrazit
                    get() = { it: Stranky ->
                        it.bazen.chciBazen.zaskrtnuto && it.bazen.tvar.vybrano != R.string.bazen_tvar_kruhovy.toText()
                    }
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
            }

            @Serializable
            data class Sirka(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.bazen_sirka.toText()
                override val suffix get() = Strings.m.toText()
                override val zobrazit
                    get() = { it: Stranky ->
                        it.bazen.chciBazen.zaskrtnuto && it.bazen.tvar.vybrano != R.string.bazen_tvar_kruhovy.toText()
                    }
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
            }

            @Serializable
            data class Prumer(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.bazen_radius.toText()
                override val suffix get() = Strings.m.toText()
                override val zobrazit
                    get() = { it: Stranky ->
                        it.bazen.chciBazen.zaskrtnuto && it.bazen.tvar.vybrano == R.string.bazen_tvar_kruhovy.toText()
                    }
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
            }

            @Serializable
            data class Hloubka(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.bazen_hloubka.toText()
                override val suffix get() = Strings.m.toText()
                override val zobrazit get() = { it: Stranky -> it.bazen.chciBazen.zaskrtnuto }
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
            }

            @Serializable
            data class Zakryti(
                override val vybrano: Text = Strings.zadne.toText(),
            ) : Vybiratko {
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.bazen_zakryti.toText()
                override val moznosti
                    get() = { _: Stranky ->
                        listOf(
                            Strings.zadne.toText(),
                            Strings.bazen_zakryti_pevna.toText(),
                            Strings.bazen_zakryti_polykarbonat.toText(),
                            Strings.jine.toText(),
                        )
                    }
                override val zobrazit get() = { it: Stranky -> it.bazen.chciBazen.zaskrtnuto }
            }

            @Serializable
            data class PozadovanaTeplota(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis = Strings.bazen_teplota.toText()
                override val suffix = Strings.stupen_c.toText()
                override val zobrazit get() = { it: Stranky -> it.bazen.chciBazen.zaskrtnuto }
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
            }

            @Serializable
            data class Poznamka(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.poznamka.toText()
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
            }

            override val nazev get() = Strings.bazen.toText()
            override val icon get() = Icons.Default.Pool
            override val veci
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
            data class TopneTelesoVNadrziTopeni(
                override val zaskrtnuto: Boolean = false,
                override val vybrano: Text = Strings.zdroje_stavajici.toText(),
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.zdroje_topne_teleso.toText()
                override val moznosti
                    get() = { _: Stranky ->
                        listOf(Strings.zdroje_stavajici.toText(), Strings.zdroje_nove.toText())
                    }
            }

            @Serializable
            data class ElektrokotelTopeni(
                override val zaskrtnuto: Boolean = false,
                override val vybrano: Text = Strings.zdroje_stavajici.toText(),
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.zdroje_elektrokotel.toText()
                override val moznosti
                    get() = { _: Stranky ->
                        listOf(Strings.zdroje_stavajici.toText(), Strings.zdroje_novy.toText())
                    }
            }

            @Serializable
            data class PlynovyKotelTopeni(
                override val zaskrtnuto: Boolean = false,
                override val vybrano: Text = Strings.zdroje_stavajici.toText(),
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.zdroje_plyn_kotel.toText()
                override val moznosti
                    get() = { _: Stranky ->
                        listOf(Strings.zdroje_stavajici.toText(), Strings.zdroje_novy.toText())
                    }
            }

            @Serializable
            data class KrbTopeni(
                override val zaskrtnuto: Boolean = false,
                override val vybrano: Text = Strings.zdroje_stavajici.toText(),
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.zdroje_krb.toText()
                override val moznosti
                    get() = { _: Stranky ->
                        listOf(Strings.zdroje_stavajici.toText(), Strings.zdroje_novy.toText())
                    }
            }

            @Serializable
            data class JinyTopeni(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.jiny.toText()
            }

            @Serializable
            data class TopneTelesoVNadrziTeplaVoda(
                override val zaskrtnuto: Boolean = false,
                override val vybrano: Text = Strings.zdroje_tv_do_zasuvky.toText(),
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.zdroje_topne_teleso.toText()
                override val moznosti
                    get() = { _: Stranky ->
                        listOf(Strings.zdroje_tv_do_zasuvky.toText(), Strings.zdroje_tv_z_regulace.toText())
                    }
            }

            @Serializable
            data class ElektrokotelTeplaVoda(
                override val zaskrtnuto: Boolean = false,
            ) : Zaskrtavatko {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override val popis get() = Strings.zdroje_elektrokotel.toText()
            }

            @Serializable
            data class PlynovyKotelTeplaVoda(
                override val zaskrtnuto: Boolean = false,
            ) : Zaskrtavatko {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override val popis get() = Strings.zdroje_plyn_kotel.toText()
            }

            @Serializable
            data class KrbTeplaVoda(
                override val zaskrtnuto: Boolean = false,
            ) : Zaskrtavatko {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override val popis get() = Strings.zdroje_krb.toText()
            }

            @Serializable
            data class JinyTeplaVoda(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.jiny.toText()
            }

            @Serializable
            data class Poznamka(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.poznamka.toText()
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
            }

            override val nazev get() = Strings.zdroje.toText()
            override val icon get() = Icons.Default.AcUnit
            override val veci
                get() = listOf(
                    listOf(
                        Nadpis(R.string.zdroje_top.toText()),
                        topneTelesoVNadrziTopeni,
                        elektrokotelTopeni,
                        plynovyKotelTopeni,
                        krbTopeni,
                        jinyTopeni
                    ),
                    listOf(
                        Nadpis(R.string.zdroje_tv.toText()),
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
                override val zaskrtnuto: Boolean = false,
                override val vybrano: Text = "500 mm".toText(),
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.prislusenstvi_hadice.toText()
                override val moznosti
                    get() = { _: Stranky ->
                        listOf("300 mm".toText(), "500 mm".toText(), "700 mm".toText(), "1000 mm".toText())
                    }
            }

            @Serializable
            data class TopnyKabel(
                override val zaskrtnuto: Boolean = false,
                override val vybrano: Text = "3,5 m".toText(),
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.prislusenstvi_topny_kabel.toText()
                override val moznosti
                    get() = { _: Stranky ->
                        listOf("3,5 m".toText(), "5 m".toText())
                    }
            }

            @Serializable
            data class DrzakNaStenu(
                override val zaskrtnuto: Boolean = false,
                override val vybrano: Text = Strings.na_stenu.toText(),
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.prislusenstvi_drzak_pro_tc.toText()
                override val moznosti
                    get() = { _: Stranky ->
                        listOf(Strings.na_stenu.toText(), Strings.na_izolovanou_stenu.toText())
                    }
            }

            @Serializable
            data class PokojovaJednotka(
                override val zaskrtnuto: Boolean = false,
            ) : Zaskrtavatko {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override val popis get() = Strings.prislusenstvi_pokojova_jednotka.toText() + " RC 25".toText()
            }

            @Serializable
            data class PokojoveCidlo(
                override val zaskrtnuto: Boolean = false,
                override val vybrano: Text = "RS 10".toText(),
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.prislusenstvi_pokojove_cidlo.toText()
                override val moznosti
                    get() = { _: Stranky ->
                        listOf("RS 10".toText(), "RSW 30 - WiFi".toText())
                    }
            }

            @Serializable
            data class Poznamka(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.poznamka.toText()
                override val klavesnice: KeyboardOptions
                    get() = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
            }

            override val nazev get() = Strings.prislusenstvi.toText()
            override val icon get() = Icons.Default.AddShoppingCart
            override val veci
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

context(Resources)
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
    if (kontakty.psc.text.length != 5) ""
    else kontakty.psc.text.substring(0, 3) + " " + kontakty.psc.text.substring(3, 5)
}</psc>
            <mesto>${kontakty.mesto.text}</mesto>
            <partner_ico>${kontakty.montazniFirma.ico}</partner_ico>
        </kontakt>
        <detailobjektu>
            <os_popis>${system.otopnySystem.vybrano.asString()}</os_popis>
            <tepelna_ztrata>${detailObjektu.tepelnaZtrata.text}</tepelna_ztrata>
            <rocni_spotreba_vytapeni>${detailObjektu.potrebaTeplaNaVytapeni.text}</rocni_spotreba_vytapeni>
            <rocni_spotreba_tv>${detailObjektu.potrebaTeplaNaTeplouVodu.text}</rocni_spotreba_tv>
            <vytapena_plocha>${detailObjektu.vytapenaPlocha.text}</vytapena_plocha>
            <vytapeny_objem>${detailObjektu.vytapenyObjem.text}</vytapeny_objem>
            <spotreba_paliva_druh>${detailObjektu.druhPaliva.text}</spotreba_paliva_druh>
            <spotreba_paliva_mnozstvi>${detailObjektu.spotrebaPaliva.text}</spotreba_paliva_mnozstvi>
            <spotreba_paliva_jednotky>${detailObjektu.spotrebaPaliva.vybraneJednotky.asString()}</spotreba_paliva_jednotky>
            <spotreba_paliva_2_druh>${detailObjektu.druhPaliva2.text}</spotreba_paliva_2_druh>
            <spotreba_paliva_2_mnozstvi>${detailObjektu.spotrebaPaliva2.text}</spotreba_paliva_2_mnozstvi>
            <spotreba_paliva_2_jednotky>${detailObjektu.spotrebaPaliva2.vybraneJednotky.asString()}</spotreba_paliva_2_jednotky>
            <rocni_platba_vytapeni>${detailObjektu.nakladyNaVytapeni.text} ${Strings.mena.toText().asString()}</rocni_platba_vytapeni>
        </detailobjektu>
        <tc>
            <typ>${system.typTC.vybrano.asString()}</typ>
            <model>${system.modelTC.vybrano.asString()}</model>
            <nadrz>${system.typNadrze.vybrano1.asString()} ${system.typNadrze.vybrano2.asString()} ${system.objemNadrze.text}</nadrz>
            <vnitrni_jednotka>${system.typVnitrniJednotky.vybrano.asString()}</vnitrni_jednotka>
        </tc>
        <zdrojeTop>
            <topne_teleso>${doplnkoveZdroje.topneTelesoVNadrziTopeni.text.asString()}</topne_teleso>
            <elektrokotel>${doplnkoveZdroje.elektrokotelTopeni.text.asString()}</elektrokotel>
            <plyn_kotel>${doplnkoveZdroje.plynovyKotelTopeni.text.asString()}</plyn_kotel>
            <krb_KTP>${doplnkoveZdroje.krbTopeni.text.asString()}</krb_KTP>
            <jiny_zdroj>${doplnkoveZdroje.jinyTopeni.text}</jiny_zdroj>
        </zdrojeTop>
        <tv>
            <zasobnik>${system.typZasobniku.vybrano.asString()} ${system.objemZasobniku.text}</zasobnik>
            <cirkulace>${if (system.cirkulaceTepleVody.zaskrtnuto) "Ano" else "Ne"}</cirkulace>
        </tv>
        <zdrojeTV>
            <topne_teleso>${doplnkoveZdroje.topneTelesoVNadrziTeplaVoda.text.asString()}</topne_teleso>
            <elektrokotel>${if (doplnkoveZdroje.elektrokotelTeplaVoda.zaskrtnuto) "Ano" else "Ne"}</elektrokotel>
            <plyn_kotel>${if (doplnkoveZdroje.plynovyKotelTeplaVoda.zaskrtnuto) "Ano" else "Ne"}</plyn_kotel>
            <krb_KTP>${if (doplnkoveZdroje.krbTeplaVoda.zaskrtnuto) "Ano" else "Ne"}</krb_KTP>
            <jiny_zdroj>${doplnkoveZdroje.jinyTeplaVoda.text}</jiny_zdroj>
        </zdrojeTV>
        <bazen>
            <ohrev>${if (bazen.chciBazen.zaskrtnuto) "Ano" else "Ne"}</ohrev>
            <doba_vyuzivani>${if (bazen.chciBazen.zaskrtnuto) bazen.dobaVyuzivani.vybrano.asString() else ""}</doba_vyuzivani>
            <umisteni>${if (bazen.chciBazen.zaskrtnuto) bazen.umisteni.vybrano.asString() else ""}</umisteni>
            <zakryti>${if (bazen.chciBazen.zaskrtnuto) bazen.zakryti.vybrano.asString() else ""}</zakryti>
            <tvar>${if (bazen.chciBazen.zaskrtnuto) bazen.tvar.vybrano.asString() else ""}</tvar>
            <sirka>${bazen.sirka.text}</sirka>
            <delka>${bazen.delka.text}</delka>
            <hloubka>${bazen.hloubka.text}</hloubka>
            <prumer>${bazen.prumer.text}</prumer>
            <teplota>${bazen.pozadovanaTeplota.text}</teplota>
            <voda>${if (bazen.chciBazen.zaskrtnuto) bazen.druhVody.vybrano.asString() else ""}</voda>
        </bazen>
        <prislusenstvi>
            <hadice>${prislusenstvi.hadice.text.asString()}</hadice>
            <topny_kabel>${prislusenstvi.topnyKabel.text.asString()}</topny_kabel>
            <drzak_na_tc>${prislusenstvi.drzakNaStenu.text.asString()}</drzak_na_tc>
            <pokojova_jednotka>${if (prislusenstvi.pokojovaJednotka.zaskrtnuto) "RC 25" else "Ne"}</pokojova_jednotka>
            <pokojove_cidlo>${prislusenstvi.pokojoveCidlo.text.asString()}</pokojove_cidlo>
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