package cz.regulus.dotaznik.dotaznik

import kotlinx.serialization.Serializable

@Serializable
data class Firma(
    val jmeno: String,
    val ico: String,
)
