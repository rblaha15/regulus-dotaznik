package cz.regulus.dotaznik

import kotlinx.serialization.Serializable

@Serializable
data class Clovek(
    val cislo: String,
    val jmeno: String,
    val prijmeni: String,
    val email: String,
    val zastupce: Boolean,
    val ico: String = "",
) {
    val celeJmeno get() = "$jmeno $prijmeni"
}