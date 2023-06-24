package cz.regulus.dotaznik

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pool
import androidx.compose.ui.graphics.vector.ImageVector
import com.regulus.dotaznik.R
import cz.regulus.dotaznik.Stranky.Stranka.Vec.*
import cz.regulus.dotaznik.Text.Companion.text
import cz.regulus.dotaznik.Text.*
import kotlinx.serialization.Serializable
import com.regulus.dotaznik.R.string as Strings

@Serializable
data class Stranky(
    val kontakty: Stranka.Kontakty = Stranka.Kontakty(),
    val detailObjektu: Stranka.DetailObjektu = Stranka.DetailObjektu(),
    val system: Stranka.System = Stranka.System(),
    val bazen: Stranka.Bazen = Stranka.Bazen(),
    val doplnkoveZdroje: Stranka.DoplnkoveZdroje = Stranka.DoplnkoveZdroje(),
    val prislusenstvi: Stranka.Prislusenstvi = Stranka.Prislusenstvi(),
) {
    context(Context)
    fun createXml() = """
        <?xml version="1.0" encoding="utf-8"?>
        <?xml-stylesheet type="text/xsl" href="dotaznik_app.xsl"?>
        
        <!-- Tento soubor byl vygenerován automaticky aplikací Regulus Dotazník; verze: 2.1 -->
        
        <xml>
            <system>
                <resi_tc>Ano</resi_tc>
                <cislo_ko>${prefsPrihlaseni.getString("kod", "Error")}</cislo_ko>
                <odesilatel>${prefsPrihlaseni.getString("email", "Error")}</odesilatel>
                <odberatel_ico>${prefsPrihlaseni.getString("ico", "")}</odberatel_ico>
            </system>
            <kontakt>
                <jmeno>${kontakty.jmeno}</jmeno>
                <prijmeni>${kontakty.prijmeni}</prijmeni>
                <telefon>${kontakty.telefon}</telefon>
                <email>${kontakty.email}</email>
                <ulice>${kontakty.ulice}</ulice>
                <psc>${
        if (kontakty.psc.text.length != 5) ""
        else kontakty.psc.text.substring(0, 3) + " " + kontakty.psc.text.substring(3, 5)
    }</psc>
                <mesto>${kontakty.mesto}</mesto>
                <partner_ico>${kontakty.icoMontazniFirmy.text}</partner_ico>
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
                <spotreba_paliva_jednotky>${detailObjektu.spotrebaPaliva.vybraneJednotky}</spotreba_paliva_jednotky>
                <spotreba_paliva_2_druh>${detailObjektu.druhPaliva2.text}</spotreba_paliva_2_druh>
                <spotreba_paliva_2_mnozstvi>${detailObjektu.spotrebaPaliva2.text}</spotreba_paliva_2_mnozstvi>
                <spotreba_paliva_2_jednotky>${detailObjektu.spotrebaPaliva2.jednotky}</spotreba_paliva_2_jednotky>
                <rocni_platba_vytapeni>${detailObjektu.nakladyNaVytapeni.text}</rocni_platba_vytapeni>
            </detailobjektu>
            <tc>
                <typ>${system.typTC.vybrano.asString()}</typ>
                <model>${system.modelTC.vybrano.asString()}</model>
                <nadrz>${system.typNadrze.vybrano1.asString()} ${system.typNadrze.vybrano2} ${system.objemNadrze}</nadrz>
                <vnitrni_jednotka>${system.typVnitrniJednotky}</vnitrni_jednotka>
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
                <sirka>${bazen.sirka}</sirka>
                <delka>${bazen.delka}</delka>
                <hloubka>${bazen.hloubka}</hloubka>
                <prumer>${bazen.prumer}</prumer>
                <teplota>${bazen.pozadovanaTeplota}</teplota>
                <voda>${if (bazen.chciBazen.zaskrtnuto) bazen.druhVody.vybrano.asString() else ""}</voda>
            </bazen>
            <prislusenstvi>
                <hadice>${prislusenstvi.hadice.text.asString()}</hadice>
                <topny_kabel>${prislusenstvi.topnyKabel.text.asString()}</topny_kabel>
                <drzak_na_tc>${prislusenstvi.drzakNaStenu.text.asString()}</drzak_na_tc>
                <pokojova_jednotka>${prislusenstvi.pokojovaJednotka.text.asString()}</pokojova_jednotka>
                <pokojove_cidlo>${prislusenstvi.pokojoveCidlo.text.asString()}</pokojove_cidlo>
            </prislusenstvi>
            <poznamka>
                <kontakty>${kontakty.poznamka}</kontakty>
                <detail_objektu>${detailObjektu.poznamka}</detail_objektu>
                <tv_tc_nadrz_a_os>${system.poznamka}</tv_tc_nadrz_a_os>
                <bazen>${bazen.poznamka}</bazen>
                <doplnkove_zdroje>${doplnkoveZdroje.poznamka}</doplnkove_zdroje>
                <prislusenstvi>${prislusenstvi.poznamka}</prislusenstvi>
            </poznamka>
        </xml>
    """.trimIndent()

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
            val zobrazit: (Stranka) -> Boolean

            @Serializable
            sealed interface Pisatko : Vec {
                val popis: Text
                val suffix: Text get() = Plain("")
                val text: String get() = ""
                override val zobrazit: (Stranka) -> Boolean get() = { true }

                fun text(text: String): Pisatko
            }

            @Serializable
            sealed interface PisatkoSJednotkama : Vec {
                val popis: Text
                val jednotky: List<Text>
                val text: String get() = ""
                val vybraneJednotky: Text get() = jednotky.first()
                override val zobrazit: (Stranka) -> Boolean get() = { true }

                fun text(text: String): PisatkoSJednotkama
                fun vybraneJednotky(vybraneJednotky: Text): PisatkoSJednotkama
            }

            @Serializable
            sealed interface Vybiratko : Vec {
                val popis: Text
                val moznosti: (Stranka) -> List<Text>
                val vybrano: Text
                override val zobrazit: (Stranka) -> Boolean get() = { true }

                fun vybrano(vybrano: Text): Vybiratko
            }

            @Serializable
            sealed interface DvojVybiratko : Vec {
                val popis: Text
                val moznosti1: (Stranka) -> List<Text>
                val moznosti2: (Stranka) -> List<Text>
                val vybrano1: Text
                val vybrano2: Text
                override val zobrazit: (Stranka) -> Boolean get() = { true }

                fun vybrano1(vybrano1: Text): DvojVybiratko
                fun vybrano2(vybrano2: Text): DvojVybiratko
            }

            @Serializable
            sealed interface Zaskrtavatko : Vec {
                val popis: Text
                val zaskrtnuto: Boolean get() = false
                override val zobrazit: (Stranka) -> Boolean get() = { true }

                fun zaskrtnuto(zaskrtnuto: Boolean): Zaskrtavatko
            }

            @Serializable
            sealed interface ZaskrtavatkoSVybiratkem : Vec {
                val popis: Text
                val moznosti: (Stranka) -> List<Text>
                val vybrano: Text
                val zaskrtnuto: Boolean get() = false
                override val zobrazit: (Stranka) -> Boolean get() = { true }

                val text get() = if (zaskrtnuto) vybrano else Plain("Ne")

                fun zaskrtnuto(zaskrtnuto: Boolean): ZaskrtavatkoSVybiratkem
                fun vybrano(vybrano: Text): ZaskrtavatkoSVybiratkem
            }

            @Serializable
            sealed class Jine(
                override val zobrazit: (Stranka) -> Boolean = { true },
            ) : Vec {
                @Serializable
                object MontazniFirma : Jine()
            }

            @Serializable
            data class Nadpis(
                val text: Text,
            ) : Vec {

                override val zobrazit: (Stranka) -> Boolean get() = { true }
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
            val icoMontazniFirmy: IcoMontazniFirmy = IcoMontazniFirmy(),
            val poznamka: Poznamka = Poznamka(),
        ) : Stranka {
            override fun kopirovatVec(novaVec: Vec) = when (novaVec) {
                is Email -> copy(email = novaVec)
                is IcoMontazniFirmy -> copy(icoMontazniFirmy = novaVec)
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
                override val popis get() = Strings.kontakty_prijmeni.text
            }

            @Serializable
            data class Jmeno(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.kontakty_jmeno.text
            }

            @Serializable
            data class Ulice(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.kontakty_ulice.text
            }

            @Serializable
            data class Mesto(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.kontakty_mesto.text
            }

            @Serializable
            data class Psc(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.kontakty_psc.text
            }

            @Serializable
            data class Telefon(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.kontakty_telefon.text
            }

            @Serializable
            data class Email(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.kontakty_email.text
            }

            @Serializable
            data class IcoMontazniFirmy(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.kontakty_ico.text
            }

            @Serializable
            data class Poznamka(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.poznamka.text
            }

            override val nazev get() = Strings.kontakty.text
            override val icon get() = Icons.Default.Person
            override val veci
                get() = listOf(
                    listOf(prijmeni, jmeno, ulice, mesto, psc, telefon, email),
                    listOf(Jine.MontazniFirma, icoMontazniFirmy),
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
                override val popis get() = Strings.detail_objektu_ztrata.text
                override val suffix get() = Strings.kW.text
            }

            @Serializable
            data class PotrebaTeplaNaVytapeni(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.detail_objektu_potreba_vytapeni.text
                override val suffix get() = Strings.kWh.text
            }

            @Serializable
            data class PotrebaTeplaNaTeplouVodu(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.detail_objektu_potreba_tv.text
                override val suffix get() = Strings.kWh.text
            }

            @Serializable
            data class VytapenaPlocha(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.detail_objektu_plocha.text
                override val suffix get() = Strings.m2.text
            }

            @Serializable
            data class VytapenyObjem(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.detail_objektu_objem.text
                override val suffix get() = Strings.m3.text
            }

            @Serializable
            data class NakladyNaVytapeni(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.detail_objektu_naklady.text
                override val suffix get() = Strings.mena.text
            }

            @Serializable
            data class DruhPaliva(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.detail_objektu_druh.text
            }

            @Serializable
            data class SpotrebaPaliva(
                override val text: String = "",
                override val vybraneJednotky: Text = Strings.q.text,
            ) : PisatkoSJednotkama {
                override fun text(text: String) = copy(text = text)
                override fun vybraneJednotky(vybraneJednotky: Text) = copy(vybraneJednotky = vybraneJednotky)
                override val popis get() = Strings.detail_objektu_spotreba.text
                override val jednotky get() = listOf(Strings.q.text, Strings.m3.text, Strings.kWh.text)
            }

            @Serializable
            data class DruhPaliva2(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.detail_objektu_druh2.text
            }

            @Serializable
            data class SpotrebaPaliva2(
                override val text: String = "",
                override val vybraneJednotky: Text = Strings.q.text,
            ) : PisatkoSJednotkama {
                override fun vybraneJednotky(vybraneJednotky: Text) = copy(vybraneJednotky = vybraneJednotky)
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.detail_objektu_spotreba2.text
                override val jednotky get() = listOf(Strings.q.text, Strings.m3.text, Strings.kWh.text)
            }

            @Serializable
            data class Poznamka(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.poznamka.text
            }

            override val nazev get() = Strings.detail_objektu.text
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
                override val vybrano: Text = Strings.vzduch_voda.text,
            ) : Vybiratko {
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.system_tc_typ.text
                override val moznosti
                    get() = { _: Stranka ->
                        listOf(Strings.vzduch_voda.text, Strings.zeme_voda.text)
                    }
            }

            @Serializable
            data class ModelTC(
                override val vybrano: Text = Strings.vyberte.text,
            ) : Vybiratko {
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.system_tc_model.text
                override val moznosti
                    get() = { it: Stranka ->
                        when ((it as System).typTC.vybrano) {
                            Strings.vzduch_voda.text -> listOf(
                                Strings.vyberte.text,
                                Plain("ECOAIR 400"),
                                Plain("ECOAIR 600"),
                                Plain("RTC 6i"),
                                Plain("RTC 12i"),
                                Plain("ECOAIR 614M"),
                                Plain("ECOAIR 622M"),
                                Plain("ECOAIR 510M"),
                                Plain("ECOAIR 406"),
                                Plain("ECOAIR 408"),
                                Plain("ECOAIR 410"),
                                Plain("ECOAIR 415"),
                                Plain("ECOAIR 420"),
                            )

                            Strings.zeme_voda.text -> listOf(
                                Strings.vyberte.text,
                                Plain("ECOPART"),
                                Plain("ECOHEAT"),
                                Plain("ECOPART 406"),
                                Plain("ECOPART 408"),
                                Plain("ECOPART 410"),
                                Plain("ECOPART 412"),
                                Plain("ECOPART 414"),
                                Plain("ECOPART 417"),
                                Plain("ECOPART 435"),
                                Plain("ECOHEAT 406"),
                                Plain("ECOHEAT 408"),
                                Plain("ECOHEAT 410"),
                                Plain("ECOHEAT 412"),
                            )

                            else -> throw IllegalArgumentException()
                        }
                    }
            }

            @Serializable
            data class TypVnitrniJednotky(
                override val vybrano: Text = Strings.zadna.text,
            ) : Vybiratko {
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.system_typ_jednotky.text
                override val moznosti
                    get() = { _: Stranka ->
                        listOf(
                            Strings.zadna.text,
                            Plain("RegulusBOX"),
                            Plain("EcoZenith i360"),
                            Plain("EcoZenith i250"),
                        )
                    }
            }

            @Serializable
            data class TypNadrze(
                override val vybrano1: Text = Strings.zadna.text,
                override val vybrano2: Text = Plain(""),
            ) : DvojVybiratko {
                override val popis get() = Strings.system_nadrz_typ.text
                override val moznosti1
                    get() = { _: Stranka ->
                        listOf(
                            Strings.zadna.text,
                            Plain("DUO"),
                            Plain("HSK"),
                            Plain("PS"),
                        )
                    }
                override val moznosti2
                    get() = { it: Stranka ->
                        when ((it as System).typNadrze.vybrano1) {
                            Plain("DUO") -> listOf(
                                Plain("-"),
                                Plain("P"),
                                Plain("PR"),
                                Plain("K"),
                                Plain("K P"),
                                Plain("K PR"),
                            )

                            Plain("HSK") -> listOf(
                                Plain("P"),
                                Plain("PR"),
                                Plain("PV"),
                            )

                            Plain("PS") -> listOf(
                                Plain("E+"),
                                Plain("ES+"),
                                Plain("N+"),
                                Plain("K+"),
                                Plain("2F"),
                                Plain("WF"),
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
                override val popis get() = Strings.system_nadrz_objem.text
                override val suffix get() = Strings.dm3.text
                override val zobrazit
                    get() = { it: Stranka ->
                        (it as System).typNadrze.vybrano1 != Strings.zadna.text
                    }
            }

            @Serializable
            data class TypZasobniku(
                override val vybrano: Text = Strings.zadny.text,
            ) : Vybiratko {
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.system_zasobnik_typ.text
                override val moznosti
                    get() = { _: Stranka ->
                        listOf(
                            Strings.zadny.text,
                            Plain("RDC"),
                            Plain("R2DC"),
                            Plain("R0BC"),
                            Plain("RBC"),
                            Plain("RBC HP"),
                            Plain("R2BC"),
                            Plain("NBC"),
                            Plain("RGC"),
                        )
                    }
            }

            @Serializable
            data class ObjemZasobniku(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.sytem_zasobnik_objem.text
                override val suffix get() = Strings.dm3.text
                override val zobrazit
                    get() = { it: Stranka ->
                        (it as System).typZasobniku.vybrano != Strings.zadny.text
                    }
            }

            @Serializable
            data class OtopnySystem(
                override val vybrano: Text = Strings.system_os_1okruh.text,
            ) : Vybiratko {
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.system_otopny_system.text
                override val moznosti
                    get() = { _: Stranka ->
                        listOf(
                            Strings.system_os_1okruh.text,
                            Strings.system_os_2okruhy.text,
                            Strings.system_os_invertor.text,
                            Strings.jiny.text,
                        )
                    }
            }

            @Serializable
            data class CirkulaceTepleVody(
                override val zaskrtnuto: Boolean = false,
            ) : Zaskrtavatko {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override val popis: Text get() = Strings.system_cirkulace.text
            }

            @Serializable
            data class Poznamka(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.poznamka.text
            }

            override val nazev get() = Strings.system.text
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
                override val popis: Text get() = Strings.system_ohrev_bazenu.text
            }

            @Serializable
            data class DobaVyuzivani(
                override val vybrano: Text = Strings.bazen_doba_celorocni.text,
            ) : Vybiratko {
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.bazen_doba_uzivani.text
                override val moznosti
                    get() = { _: Stranka ->
                        listOf(
                            Strings.bazen_doba_celorocni.text,
                            Strings.bazen_doba_sezonni.text,
                        )
                    }
                override val zobrazit get() = { it: Stranka -> (it as Bazen).chciBazen.zaskrtnuto }
            }

            @Serializable
            data class Umisteni(
                override val vybrano: Text = Strings.bazen_umisteni_venkovni.text,
            ) : Vybiratko {
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.bazen_umisteni.text
                override val moznosti
                    get() = { _: Stranka ->
                        listOf(
                            Strings.bazen_umisteni_venkovni.text,
                            Strings.bazen_umisteni_vnitrni.text,
                        )
                    }
                override val zobrazit get() = { it: Stranka -> (it as Bazen).chciBazen.zaskrtnuto }
            }

            @Serializable
            data class DruhVody(
                override val vybrano: Text = Strings.bazen_druh_sladka.text,
            ) : Vybiratko {
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.bazen_druh_vody.text
                override val moznosti
                    get() = { _: Stranka ->
                        listOf(
                            Strings.bazen_druh_sladka.text,
                            Strings.bazen_druh_slana.text,
                        )
                    }
                override val zobrazit get() = { it: Stranka -> (it as Bazen).chciBazen.zaskrtnuto }
            }

            @Serializable
            data class Tvar(
                override val vybrano: Text = Strings.bazen_tvar_obdelnikovy.text,
            ) : Vybiratko {
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.bazen_tvar.text
                override val moznosti
                    get() = { _: Stranka ->
                        listOf(
                            Strings.bazen_tvar_obdelnikovy.text,
                            Strings.bazen_tvar_ovalny.text,
                            Strings.bazen_tvar_kruhovy.text,
                        )
                    }
                override val zobrazit get() = { it: Stranka -> (it as Bazen).chciBazen.zaskrtnuto }
            }

            @Serializable
            data class Delka(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.bazen_delka.text
                override val suffix get() = Strings.m.text
                override val zobrazit
                    get() = { it: Stranka ->
                        (it as Bazen).chciBazen.zaskrtnuto && it.tvar.vybrano != R.string.bazen_tvar_kruhovy.text
                    }
            }

            @Serializable
            data class Sirka(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.bazen_sirka.text
                override val suffix get() = Strings.m.text
                override val zobrazit
                    get() = { it: Stranka ->
                        (it as Bazen).chciBazen.zaskrtnuto && it.tvar.vybrano != R.string.bazen_tvar_kruhovy.text
                    }
            }

            @Serializable
            data class Prumer(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.bazen_radius.text
                override val suffix get() = Strings.m.text
                override val zobrazit
                    get() = { it: Stranka ->
                        (it as Bazen).chciBazen.zaskrtnuto && it.tvar.vybrano == R.string.bazen_tvar_kruhovy.text
                    }
            }

            @Serializable
            data class Hloubka(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.bazen_hloubka.text
                override val suffix get() = Strings.m.text
                override val zobrazit get() = { it: Stranka -> (it as Bazen).chciBazen.zaskrtnuto }
            }

            @Serializable
            data class Zakryti(
                override val vybrano: Text = Strings.zadne.text,
            ) : Vybiratko {
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.bazen_zakryti.text
                override val moznosti
                    get() = { _: Stranka ->
                        listOf(
                            Strings.zadne.text,
                            Strings.bazen_zakryti_pevna.text,
                            Strings.bazen_zakryti_polykarbonat.text,
                            Strings.jine.text,
                        )
                    }
                override val zobrazit get() = { it: Stranka -> (it as Bazen).chciBazen.zaskrtnuto }
            }

            @Serializable
            data class PozadovanaTeplota(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis = Strings.bazen_teplota.text
                override val suffix = Strings.stupen_c.text
                override val zobrazit get() = { it: Stranka -> (it as Bazen).chciBazen.zaskrtnuto }
            }

            @Serializable
            data class Poznamka(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.poznamka.text
            }

            override val nazev get() = Strings.bazen.text
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
                override val vybrano: Text = Strings.zdroje_stavajici.text,
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.zdroje_topne_teleso.text
                override val moznosti
                    get() = { _: Stranka ->
                        listOf(Strings.zdroje_stavajici.text, Strings.zdroje_nove.text)
                    }
            }

            @Serializable
            data class ElektrokotelTopeni(
                override val zaskrtnuto: Boolean = false,
                override val vybrano: Text = Strings.zdroje_stavajici.text,
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.zdroje_elektrokotel.text
                override val moznosti
                    get() = { _: Stranka ->
                        listOf(Strings.zdroje_stavajici.text, Strings.zdroje_novy.text)
                    }
            }

            @Serializable
            data class PlynovyKotelTopeni(
                override val zaskrtnuto: Boolean = false,
                override val vybrano: Text = Strings.zdroje_stavajici.text,
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.zdroje_plyn_kotel.text
                override val moznosti
                    get() = { _: Stranka ->
                        listOf(Strings.zdroje_stavajici.text, Strings.zdroje_novy.text)
                    }
            }

            @Serializable
            data class KrbTopeni(
                override val zaskrtnuto: Boolean = false,
                override val vybrano: Text = Strings.zdroje_stavajici.text,
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.zdroje_krb.text
                override val moznosti
                    get() = { _: Stranka ->
                        listOf(Strings.zdroje_stavajici.text, Strings.zdroje_novy.text)
                    }
            }

            @Serializable
            data class JinyTopeni(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.jiny.text
            }

            @Serializable
            data class TopneTelesoVNadrziTeplaVoda(
                override val zaskrtnuto: Boolean = false,
                override val vybrano: Text = Strings.zdroje_tv_do_zasuvky.text,
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.zdroje_topne_teleso.text
                override val moznosti
                    get() = { _: Stranka ->
                        listOf(Strings.zdroje_tv_do_zasuvky.text, Strings.zdroje_tv_z_regulace.text)
                    }
            }

            @Serializable
            data class ElektrokotelTeplaVoda(
                override val zaskrtnuto: Boolean = false,
            ) : Zaskrtavatko {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override val popis get() = Strings.zdroje_elektrokotel.text
            }

            @Serializable
            data class PlynovyKotelTeplaVoda(
                override val zaskrtnuto: Boolean = false,
            ) : Zaskrtavatko {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override val popis get() = Strings.zdroje_plyn_kotel.text
            }

            @Serializable
            data class KrbTeplaVoda(
                override val zaskrtnuto: Boolean = false,
            ) : Zaskrtavatko {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override val popis get() = Strings.zdroje_krb.text
            }

            @Serializable
            data class JinyTeplaVoda(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.jiny.text
            }

            @Serializable
            data class Poznamka(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.poznamka.text
            }

            override val nazev get() = Strings.zdroje.text
            override val icon get() = Icons.Default.AcUnit
            override val veci
                get() = listOf(
                    listOf(
                        Nadpis(R.string.zdroje_top.text),
                        topneTelesoVNadrziTopeni,
                        elektrokotelTopeni,
                        plynovyKotelTopeni,
                        krbTopeni,
                        jinyTopeni
                    ),
                    listOf(
                        Nadpis(R.string.zdroje_tv.text),
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
                override val vybrano: Text = Plain("300 mm"),
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.prislusenstvi_hadice.text
                override val moznosti
                    get() = { _: Stranka ->
                        listOf(Plain("300 mm"), Plain("500 mm"), Plain("700 mm"), Plain("1000 mm"))
                    }
            }

            @Serializable
            data class TopnyKabel(
                override val zaskrtnuto: Boolean = false,
                override val vybrano: Text = Plain("3,5 m"),
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.prislusenstvi_topny_kabel.text
                override val moznosti
                    get() = { _: Stranka ->
                        listOf(Plain("3,5 m"), Plain("5 m"))
                    }
            }

            @Serializable
            data class DrzakNaStenu(
                override val zaskrtnuto: Boolean = false,
                override val vybrano: Text = Strings.na_stenu.text,
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.prislusenstvi_drzak_pro_tc.text
                override val moznosti
                    get() = { _: Stranka ->
                        listOf(Strings.na_stenu.text, Strings.na_izolovanou_stenu.text)
                    }
            }

            @Serializable
            data class PokojovaJednotka(
                override val zaskrtnuto: Boolean = false,
                override val vybrano: Text = Plain("RCD"),
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.prislusenstvi_pokojova_jednotka.text
                override val moznosti
                    get() = { _: Stranka ->
                        listOf(Plain("RCD"), Plain("RC 25"))
                    }
            }

            @Serializable
            data class PokojoveCidlo(
                override val zaskrtnuto: Boolean = false,
                override val vybrano: Text = Plain("RS 10"),
            ) : ZaskrtavatkoSVybiratkem {
                override fun zaskrtnuto(zaskrtnuto: Boolean) = copy(zaskrtnuto = zaskrtnuto)
                override fun vybrano(vybrano: Text) = copy(vybrano = vybrano)
                override val popis get() = Strings.prislusenstvi_pokojove_cidlo.text
                override val moznosti
                    get() = { _: Stranka ->
                        listOf(Plain("RS 10"), Plain("RSW 30"))
                    }
            }

            @Serializable
            data class Poznamka(
                override val text: String = "",
            ) : Pisatko {
                override fun text(text: String) = copy(text = text)
                override val popis get() = Strings.poznamka.text
            }

            override val nazev get() = Strings.prislusenstvi.text
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