package cz.regulus.dotaznik

import kotlinx.serialization.Serializable

@Serializable
data class Zamestnanec(
    val jmeno: String,
    val prijmeni: String,
    val email: String,
    val zastupce: Boolean,
    val cislo: String,
) {
    val celeJmeno get() = "$jmeno $prijmeni"

}

fun Zamestnanec.vytvoritUzivatele(jsemToJa: Boolean) =
    if (jsemToJa) Uzivatel(
        jmeno = jmeno,
        prijmeni = prijmeni,
        email = email,
        cisloKo = cislo,
        jeZamestnanec = true
    )
    else Uzivatel(
        jmeno = "",
        prijmeni = "",
        email = "",
        cisloKo = cislo,
        jeZamestnanec = false,
    )