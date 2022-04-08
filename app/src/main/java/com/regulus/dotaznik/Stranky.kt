package com.regulus.dotaznik

data class Stranky(
    val kontakty: Kontakty = Kontakty(),
    val detailObjektu: DetailObjektu = DetailObjektu(),
    val system: System_ = System_(),
    val bazen: Bazen = Bazen(),
    val zdrojeTop: ZdrojeTop = ZdrojeTop(),
    val zdrojeTv: ZdrojeTv = ZdrojeTv(),
    val prislusenstvi: Prislusenstvi = Prislusenstvi(),
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
        var topneTeleso: Boolean = false,
        var topneTelesoTypPos: Int = 0,
        var topneTelesoTyp: String = "",
        var elektrokotel: Boolean = false,
        var elektrokotelTypPos: Int = 0,
        var elektrokotelTyp: String = "",
        var plynKotel: Boolean = false,
        var plynKotelTypPos: Int = 0,
        var plynKotelTyp: String = "",
        var krb: Boolean = false,
        var krbTypPos: Int = 0,
        var krbTyp: String = "",
        var jiny: Boolean = false,
        var ktery: String = "",
    )

    data class ZdrojeTv(
        var topneTeleso: Boolean = false,
        var topneTelesoTypPos: Int = 0,
        var topneTelesoTyp: String = "",
        var elektrokotel: Boolean = false,
        var plynKotel: Boolean = false,
        var krb: Boolean = false,
        var jiny: Boolean = false,
        var ktery: String = "",
        var poznamka: String = "",
    )

    data class Prislusenstvi(
        var hadice: Boolean = false,
        var hadiceTypPos: Int = 0,
        var hadiceTyp: String = "",
        var topnyKabel: Boolean = false,
        var topnyKabelTypPos: Int = 0,
        var topnyKabelTyp: String = "",
        var drzakNaStenu: Boolean = false,
        var drzakNaStenuTypPos: Int = 0,
        var drzakNaStenuTyp: String = "",
        var pokojovaJednotka: Boolean = false,
        var pokojovaJednotkaTypPos: Int = 0,
        var pokojovaJednotkaTyp: String = "",
        var pokojoveCidlo: Boolean = false,
        var pokojoveCidloTypPos: Int = 0,
        var pokojoveCidloTyp: String = "",
        var poznamka: String = "",
    )
}
