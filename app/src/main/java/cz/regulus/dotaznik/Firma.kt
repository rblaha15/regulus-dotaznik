package cz.regulus.dotaznik

import kotlinx.serialization.Serializable

@Serializable
data class Firma(
    val jmeno: String,
    val ico: String,
)
