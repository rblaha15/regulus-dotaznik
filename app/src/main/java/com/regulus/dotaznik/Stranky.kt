package com.regulus.dotaznik

import com.regulus.dotaznik.Stranky.Stranka.Vec.Nadpis
import com.regulus.dotaznik.Stranky.Stranka.Vec.Spacer
import com.regulus.dotaznik.Text.Companion.text
import com.regulus.dotaznik.Text.Plain
import com.regulus.dotaznik.R.string as Strings

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

    sealed class Stranka(
        val nazev: Text,
        val veci: List<List<Vec>>,
    ) {

        sealed class Vec {
            abstract val zobrazit: (Stranka) -> Boolean

            data class Pisatko(
                val popis: Text,
                val suffix: Text = Plain(""),
                val text: String = "",
                override val zobrazit: (Stranka) -> Boolean = { true },
            ) : Vec()

            data class PisatkoSJednotkama(
                val popis: Text,
                val jednotky: List<Text>,
                val text: String = "",
                val vybraneJednotky: Text = jednotky.first(),
                override val zobrazit: (Stranka) -> Boolean = { true },
            ) : Vec()

            data class Vybiratko(
                val popis: Text,
                val moznosti: (Stranka) -> List<Text>,
                val vybrano: Text,
                override val zobrazit: (Stranka) -> Boolean = { true },
            ) : Vec() {
                constructor(
                    popis: Text,
                    moznosti: List<Text>,
                    vybrano: Text = moznosti.first(),
                    zobrazit: (Stranka) -> Boolean = { true },
                ) : this(popis, { moznosti }, vybrano, zobrazit)
            }

            data class DvojVybiratko(
                val popis: Text,
                val moznosti1: (Stranka) -> List<Text>,
                val moznosti2: (Stranka) -> List<Text>,
                val vybrano1: Text,
                val vybrano2: Text,
                override val zobrazit: (Stranka) -> Boolean = { true },
            ) : Vec() {
                constructor(
                    popis: Text,
                    moznosti1: List<Text>,
                    moznosti2: List<Text>,
                    vybrano1: Text = moznosti1.first(),
                    vybrano2: Text = moznosti2.first(),
                    zobrazit: (Stranka) -> Boolean = { true },
                ) : this(popis, { moznosti1 }, { moznosti2 }, vybrano1, vybrano2, zobrazit)

                constructor(
                    popis: Text,
                    moznosti1: (Stranka) -> List<Text>,
                    moznosti2: List<Text>,
                    vybrano1: Text,
                    vybrano2: Text = moznosti2.first(),
                    zobrazit: (Stranka) -> Boolean = { true },
                ) : this(popis, moznosti1, { moznosti2 }, vybrano1, vybrano2, zobrazit)

                constructor(
                    popis: Text,
                    moznosti1: List<Text>,
                    moznosti2: (Stranka) -> List<Text>,
                    vybrano1: Text = moznosti1.first(),
                    vybrano2: Text,
                    zobrazit: (Stranka) -> Boolean = { true },
                ) : this(popis, { moznosti1 }, moznosti2, vybrano1, vybrano2, zobrazit)
            }

            data class Zaskrtavatko(
                val popis: Text,
                val zaskrtnuto: Boolean = false,
                override val zobrazit: (Stranka) -> Boolean = { true },
            ) : Vec()

            data class ZaskrtavatkoSVybiratkem(
                val popis: Text,
                val moznosti: (Stranka) -> List<Text>,
                val vybrano: Text,
                val zaskrtnuto: Boolean = false,
                override val zobrazit: (Stranka) -> Boolean = { true },
            ) : Vec() {
                constructor(
                    popis: Text,
                    moznosti: List<Text>,
                    vybrano: Text = moznosti.first(),
                    zaskrtnuto: Boolean = false,
                    zobrazit: (Stranka) -> Boolean = { true },
                ) : this(popis, { moznosti }, vybrano, zaskrtnuto, zobrazit)
            }

            sealed class Jine(
                override val zobrazit: (Stranka) -> Boolean = { true },
            ) : Vec() {
                object MontazniFirma : Jine()
            }

            object Spacer : Vec() {
                override val zobrazit: (Stranka) -> Boolean = { true }
            }

            data class Nadpis(
                val text: Text,
            ) : Vec() {
                override val zobrazit: (Stranka) -> Boolean = { true }
            }
        }

        data class Kontakty(
            val prijmeni: Vec.Pisatko = Vec.Pisatko(Strings.kontakty_prijmeni.text),
            val jmeno: Vec.Pisatko = Vec.Pisatko(Strings.kontakty_jmeno.text),
            val ulice: Vec.Pisatko = Vec.Pisatko(Strings.kontakty_ulice.text),
            val mesto: Vec.Pisatko = Vec.Pisatko(Strings.kontakty_mesto.text),
            val psc: Vec.Pisatko = Vec.Pisatko(Strings.kontakty_psc.text),
            val telefon: Vec.Pisatko = Vec.Pisatko(Strings.kontakty_telefon.text),
            val email: Vec.Pisatko = Vec.Pisatko(Strings.kontakty_email.text),
            val icoMontazniFirmy: Vec.Pisatko = Vec.Pisatko(Strings.kontakty_ico.text),
            val poznamka: Vec.Pisatko = Vec.Pisatko(Strings.poznamka.text),
        ) : Stranka(
            nazev = Strings.kontakty.text,
            veci = listOf(
                listOf(prijmeni, jmeno, ulice, mesto, psc, telefon, email),
                listOf(Vec.Jine.MontazniFirma, icoMontazniFirmy),
                listOf(Spacer, poznamka),
            )
        )

        data class DetailObjektu(
            val tepelnaZtrata: Vec.Pisatko = Vec.Pisatko(Strings.detail_objektu_ztrata.text, Strings.kW.text),
            val potrbaTeplaNaVytapeni: Vec.Pisatko = Vec.Pisatko(Strings.detail_objektu_potreba_vytapeni.text, Strings.kWh.text),
            val potrebaTeplaNaTeplouVodu: Vec.Pisatko = Vec.Pisatko(Strings.detail_objektu_potreba_tv.text, Strings.kWh.text),
            val vytapenaPlocha: Vec.Pisatko = Vec.Pisatko(Strings.detail_objektu_plocha.text, Strings.m2.text),
            val vytapenyObjem: Vec.Pisatko = Vec.Pisatko(Strings.detail_objektu_objem.text, Strings.m3.text),
            val nakladyNaVytapeni: Vec.Pisatko = Vec.Pisatko(Strings.detail_objektu_naklady.text, Strings.mena.text),
            val druhPaliva: Vec.Pisatko = Vec.Pisatko(Strings.detail_objektu_druh.text),
            val spotrebaPaliva: Vec.PisatkoSJednotkama = Vec.PisatkoSJednotkama(
                popis = Strings.detail_objektu_spotreba.text,
                jednotky = listOf(Strings.q.text, Strings.m3.text, Strings.kWh.text)
            ),
            val druhPaliva2: Vec.Pisatko = Vec.Pisatko(Strings.detail_objektu_druh2.text),
            val spotrebaPaliva2: Vec.PisatkoSJednotkama = Vec.PisatkoSJednotkama(
                popis = Strings.detail_objektu_spotreba2.text,
                jednotky = listOf(Strings.q.text, Strings.m3.text, Strings.kWh.text)
            ),
            val poznamka: Vec.Pisatko = Vec.Pisatko(Strings.poznamka.text),
        ) : Stranka(
            nazev = Strings.detail_objektu.text,
            veci = listOf(
                listOf(tepelnaZtrata),
                listOf(potrbaTeplaNaVytapeni, potrebaTeplaNaTeplouVodu),
                listOf(vytapenaPlocha, vytapenyObjem),
                listOf(nakladyNaVytapeni, druhPaliva, spotrebaPaliva, druhPaliva2, spotrebaPaliva2),
                listOf(Spacer, poznamka),
            )
        )

        data class System(
            val typTC: Vec.Vybiratko = Vec.Vybiratko(
                popis = Strings.system_tc_typ.text,
                moznosti = listOf(Strings.vzduch_voda.text, Strings.zeme_voda.text)
            ),
            val modelTC: Vec.Vybiratko = Vec.Vybiratko(
                popis = Strings.system_tc_model.text,
                moznosti = { it: Stranka ->
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
                },
                vybrano = Strings.vyberte.text
            ),
            val typVnitrniJednotky: Vec.Vybiratko = Vec.Vybiratko(
                popis = Strings.system_typ_jednotky.text,
                moznosti = listOf(
                    Strings.zadna.text,
                    Plain("RegulusBOX"),
                    Plain("EcoZenith i360"),
                    Plain("EcoZenith i250"),
                )
            ),
            val typNadrze: Vec.DvojVybiratko = Vec.DvojVybiratko(
                popis = Strings.system_nadrz_typ.text,
                moznosti1 = listOf(
                    Strings.zadna.text,
                    Plain("DUO"),
                    Plain("HSK"),
                    Plain("PS"),
                ),
                moznosti2 = {
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

                        else -> listOf(Plain(""))
                    }
                },
                vybrano2 = Plain("")
            ),
            val objemNadrze: Vec.Pisatko = Vec.Pisatko(
                popis = Strings.system_nadrz_objem.text,
                suffix = Strings.dm3.text,
                zobrazit = {
                    (it as System).typNadrze.vybrano1 != Strings.zadna.text
                }
            ),
            val typZasobniku: Vec.Vybiratko = Vec.Vybiratko(
                popis = Strings.system_zasobnik_typ.text,
                moznosti = listOf(
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
            ),
            val objemZasobniku: Vec.Pisatko = Vec.Pisatko(
                popis = Strings.sytem_zasobnik_objem.text,
                suffix = Strings.dm3.text,
                zobrazit = {
                    (it as System).typZasobniku.vybrano != Strings.zadny.text
                }
            ),
            val otopnySystem: Vec.Vybiratko = Vec.Vybiratko(
                popis = Strings.system_otopny_system.text,
                moznosti = listOf(
                    Strings.system_os_1okruh.text,
                    Strings.system_os_2okruhy.text,
                    Strings.system_os_invertor.text,
                    Strings.jiny.text,
                )
            ),
            val cirkulaceTepleVody: Vec.Zaskrtavatko = Vec.Zaskrtavatko(Strings.system_cirkulace.text),
            val poznamka: Vec.Pisatko = Vec.Pisatko(Strings.poznamka.text),
        ) : Stranka(
            nazev = Strings.detail_objektu.text,
            veci = listOf(
                listOf(typTC, modelTC),
                listOf(typVnitrniJednotky),
                listOf(typNadrze, objemNadrze),
                listOf(typZasobniku, objemZasobniku),
                listOf(otopnySystem),
                listOf(cirkulaceTepleVody),
                listOf(Spacer, poznamka),
            )
        )

        data class Bazen(
            val chciBazen: Vec.Zaskrtavatko = Vec.Zaskrtavatko(Strings.system_ohrev_bazenu.text),
            val dobaVyuzivani: Vec.Vybiratko = Vec.Vybiratko(
                popis = Strings.bazen_doba_uzivani.text,
                moznosti = listOf(
                    Strings.bazen_doba_celorocni.text,
                    Strings.bazen_doba_sezonni.text,
                ),
                zobrazit = { (it as Bazen).chciBazen.zaskrtnuto }
            ),
            val umisteni: Vec.Vybiratko = Vec.Vybiratko(
                popis = Strings.bazen_umisteni.text,
                moznosti = listOf(
                    Strings.bazen_umisteni_venkovni.text,
                    Strings.bazen_umisteni_vnitrni.text,
                ),
                zobrazit = { (it as Bazen).chciBazen.zaskrtnuto }
            ),
            val druhVody: Vec.Vybiratko = Vec.Vybiratko(
                popis = Strings.bazen_druh_vody.text,
                moznosti = listOf(
                    Strings.bazen_druh_sladka.text,
                    Strings.bazen_druh_slana.text,
                ),
                zobrazit = { (it as Bazen).chciBazen.zaskrtnuto }
            ),
            val tvar: Vec.Vybiratko = Vec.Vybiratko(
                popis = Strings.bazen_tvar.text,
                moznosti = listOf(
                    Strings.bazen_tvar_obdelnikovy.text,
                    Strings.bazen_tvar_ovalny.text,
                    Strings.bazen_tvar_kruhovy.text,
                ),
                zobrazit = { (it as Bazen).chciBazen.zaskrtnuto }
            ),
            val delka: Vec.Pisatko = Vec.Pisatko(
                popis = Strings.bazen_delka.text,
                suffix = Strings.m.text,
                zobrazit = { (it as Bazen).chciBazen.zaskrtnuto && it.tvar.vybrano != R.string.bazen_tvar_kruhovy.text }
            ),
            val sirka: Vec.Pisatko = Vec.Pisatko(
                popis = Strings.bazen_sirka.text,
                suffix = Strings.m.text,
                zobrazit = { (it as Bazen).chciBazen.zaskrtnuto && it.tvar.vybrano != R.string.bazen_tvar_kruhovy.text }
            ),
            val prumer: Vec.Pisatko = Vec.Pisatko(
                popis = Strings.bazen_radius.text,
                suffix = Strings.m.text,
                zobrazit = { (it as Bazen).chciBazen.zaskrtnuto && it.tvar.vybrano == R.string.bazen_tvar_kruhovy.text }
            ),
            val hloubka: Vec.Pisatko = Vec.Pisatko(
                popis = Strings.bazen_hloubka.text,
                suffix = Strings.m.text,
                zobrazit = { (it as Bazen).chciBazen.zaskrtnuto }
            ),
            val zakryti: Vec.Vybiratko = Vec.Vybiratko(
                popis = Strings.bazen_zakryti.text,
                moznosti = listOf(
                    Strings.zadne.text,
                    Strings.bazen_zakryti_pevna.text,
                    Strings.bazen_zakryti_polykarbonat.text,
                    Strings.jine.text,
                ),
                zobrazit = { (it as Bazen).chciBazen.zaskrtnuto }
            ),
            val pozadovanaTeplota: Vec.Pisatko = Vec.Pisatko(
                popis = Strings.bazen_teplota.text,
                suffix = Strings.stupen_c.text,
                zobrazit = { (it as Bazen).chciBazen.zaskrtnuto }
            ),
            val poznamka: Vec.Pisatko = Vec.Pisatko(Strings.poznamka.text),
        ) : Stranka(
            nazev = Strings.detail_objektu.text,
            veci = listOf(
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
                    Spacer,
                    poznamka
                ),
            )
        )

        data class DoplnkoveZdroje(
            val topneTelesoVNadrziTopeni: Vec.ZaskrtavatkoSVybiratkem = Vec.ZaskrtavatkoSVybiratkem(
                popis = Strings.zdroje_topne_teleso.text,
                moznosti = listOf(Strings.zdroje_stavajici.text, Strings.zdroje_nove.text),
            ),
            val elektrokotelTopeni: Vec.ZaskrtavatkoSVybiratkem = Vec.ZaskrtavatkoSVybiratkem(
                popis = Strings.zdroje_elektrokotel.text,
                moznosti = listOf(Strings.zdroje_stavajici.text, Strings.zdroje_novy.text),
            ),
            val plynovyKotelTopeni: Vec.ZaskrtavatkoSVybiratkem = Vec.ZaskrtavatkoSVybiratkem(
                popis = Strings.zdroje_plyn_kotel.text,
                moznosti = listOf(Strings.zdroje_stavajici.text, Strings.zdroje_novy.text),
            ),
            val krbTopeni: Vec.ZaskrtavatkoSVybiratkem = Vec.ZaskrtavatkoSVybiratkem(
                popis = Strings.zdroje_krb.text,
                moznosti = listOf(Strings.zdroje_stavajici.text, Strings.zdroje_novy.text),
            ),
            val jinyTopeni: Vec.Pisatko = Vec.Pisatko(Strings.jiny.text),

            val topneTelesoVNadrziTeplaVoda: Vec.ZaskrtavatkoSVybiratkem = Vec.ZaskrtavatkoSVybiratkem(
                popis = Strings.zdroje_topne_teleso.text,
                moznosti = listOf(Strings.zdroje_tv_do_zasuvky.text, Strings.zdroje_tv_z_regulace.text),
            ),
            val elektrokotelTeplaVoda: Vec.Zaskrtavatko = Vec.Zaskrtavatko(Strings.zdroje_elektrokotel.text),
            val plynovyKotelTeplaVoda: Vec.Zaskrtavatko = Vec.Zaskrtavatko(Strings.zdroje_plyn_kotel.text),
            val krbTeplaVoda: Vec.Zaskrtavatko = Vec.Zaskrtavatko(Strings.zdroje_krb.text),
            val jinyTeplaVoda: Vec.Pisatko = Vec.Pisatko(Strings.jiny.text),
            val poznamka: Vec.Pisatko = Vec.Pisatko(Strings.poznamka.text),
        ) : Stranka(
            nazev = Strings.detail_objektu.text,
            veci = listOf(
                listOf(
                    Nadpis(R.string.zdroje_top.text),
                    topneTelesoVNadrziTopeni, elektrokotelTopeni, plynovyKotelTopeni, krbTopeni, jinyTopeni
                ),
                listOf(
                    Nadpis(R.string.zdroje_tv.text),
                    topneTelesoVNadrziTeplaVoda, elektrokotelTeplaVoda, plynovyKotelTeplaVoda, krbTeplaVoda, jinyTeplaVoda
                ),
                listOf(Spacer, poznamka),
            )
        )

        data class Prislusenstvi(
            val hadice: Vec.ZaskrtavatkoSVybiratkem = Vec.ZaskrtavatkoSVybiratkem(
                popis = Strings.prislusenstvi_hadice.text,
                moznosti = listOf(Plain("300 mm"), Plain("500 mm"), Plain("700 mm"), Plain("1000 mm"))
            ),
            val topnyKabel: Vec.ZaskrtavatkoSVybiratkem = Vec.ZaskrtavatkoSVybiratkem(
                popis = Strings.prislusenstvi_topny_kabel.text,
                moznosti = listOf(Plain("3,5 m"), Plain("5 m"))
            ),
            val drzakNaStenu: Vec.ZaskrtavatkoSVybiratkem = Vec.ZaskrtavatkoSVybiratkem(
                popis = Strings.prislusenstvi_drzak_pro_tc.text,
                moznosti = listOf(Strings.na_stenu.text, Strings.na_izolovanou_stenu.text)
            ),
            val pokojovaJednotka: Vec.ZaskrtavatkoSVybiratkem = Vec.ZaskrtavatkoSVybiratkem(
                popis = Strings.prislusenstvi_pokojova_jednotka.text,
                moznosti = listOf(Plain("RCD"), Plain("RC 25"))
            ),
            val pokojoveCidlo: Vec.ZaskrtavatkoSVybiratkem = Vec.ZaskrtavatkoSVybiratkem(
                popis = Strings.prislusenstvi_pokojove_cidlo.text,
                moznosti = listOf(Plain("RS 10"), Plain("RSW 30"))
            ),
            val poznamka: Vec.Pisatko = Vec.Pisatko(Strings.poznamka.text),
        ) : Stranka(
            nazev = Strings.detail_objektu.text,
            veci = listOf(
                listOf(hadice, topnyKabel, drzakNaStenu, pokojovaJednotka, pokojoveCidlo, Spacer, poznamka),
            )
        )
    }
}