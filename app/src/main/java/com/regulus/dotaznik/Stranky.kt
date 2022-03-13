package com.regulus.dotaznik

data class Stranky(
    val kontakty: Kontakty = Kontakty(),
    val detailObjektu: DetailObjektu = DetailObjektu(),
    val system: System_ = System_(),
    val bazen: Bazen = Bazen(),
    val zdrojeTop: ZdrojeTop = ZdrojeTop(),
    val zdrojeTv: ZdrojeTv = ZdrojeTv(),
) {

    data class Kontakty(
        var prijmeni: String = "",
        var jmeno: String = "",
        var ulice: String = "",
        var mesto: String = "",
        var psc: String = "",
        var telefon: String = "",
        var email: String = "",
        var firma: String = "",
        var ico: String = "",
        var poznamka: String = "",
    )

    data class DetailObjektu(
        var ztrata: String = "",
        var potrebaVytapeni: String = "",
        var potrebaTv: String = "",
        var plocha: String = "",
        var objem: String = "",
        var naklady: String = "",
        var druhPaliva: String = "",
        var spotreba: String = "",
        var spotrebaJednotkyPos: Int = 0,
        var spotrebaJednotky: String = "",
        var druhPaliva2: String = "",
        var spotreba2: String = "",
        var spotrebaJednotky2Pos: Int = 0,
        var spotrebaJednotky2: String = "",
        var poznamka: String = "",
    )

    data class System_(
        var tcTypPos: Int = 0,
        var tcTyp: String = "",
        var tcModelPos: Int = 0,
        var tcModel: String = "",
        var jednotkaTypPos: Int = 0,
        var jednotkaTyp: String = "",
        var nadrzTypPos: Int = 0,
        var nadrzTyp: String = "",
        var nadrzTyp2Pos: Int = 0,
        var nadrzTyp2: String = "",
        var nadrzObjem: String = "",
        var zasobnikTypPos: Int = 0,
        var zasobnikTyp: String = "",
        var zasobnikObjem: String = "",
        var osPos: Int = 0,
        var os: String = "",
        var cirkulace: Boolean = false,
        var poznamka: String = "",
    )

    data class Bazen(
        var chciBazen: Boolean = false,
        var dobaPos: Int = 0,
        var doba: String = "",
        var umisteniPos: Int = 0,
        var umisteni: String = "",
        var druhVodyPos: Int = 0,
        var druhVody: String = "",
        var tvarPos: Int = 0,
        var tvar: String = "",
        var delka: String = "",
        var sirka: String = "",
        var prumer: String = "",
        var hloubka: String = "",
        var zakrytiPos: Int = 0,
        var zakryti: String = "",
        var teplota: String = "",
        var poznamka: String = "",
    )

    data class ZdrojeTop(
        var topTopneTeleso: Boolean = false,
        var topTopneTelesoTypPos: Int = 0,
        var topTopneTelesoTyp: String = "",
        var topElektrokotel: Boolean = false,
        var topElektrokotelTypPos: Int = 0,
        var topElektrokotelTyp: String = "",
        var topPlynKotel: Boolean = false,
        var topPlynKotelTypPos: Int = 0,
        var topPlynKotelTyp: String = "",
        var topKrb: Boolean = false,
        var topKrbTypPos: Int = 0,
        var topKrbTyp: String = "",
        var topJiny: Boolean = false,
        var topKtery: String = "",
    )

    data class ZdrojeTv(
        var tvTopneTeleso: Boolean = false,
        var tvTopneTelesoTypPos: Int = 0,
        var tvTopneTelesoTyp: String = "",
        var tvElektrokotel: Boolean = false,
        var tvPlynKotel: Boolean = false,
        var tvKrb: Boolean = false,
        var tvJiny: Boolean = false,
        var tvKtery: String = "",
        var poznamka: String = "",
    )
}
