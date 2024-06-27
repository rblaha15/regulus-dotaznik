package cz.regulus.dotaznik

import kotlinx.serialization.Serializable

@Serializable
data class Uzivatel(
    val jmeno: String,
    val prijmeni: String,
    val email: String,
    val ico: String = "",
    val cisloKo: String,
    val jeZamestnanec: Boolean,
)