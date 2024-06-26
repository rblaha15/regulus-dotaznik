package cz.regulus.dotaznik

import kotlinx.serialization.Serializable

@Serializable
data class Produkty(
    val tcVzduchVoda: List<String> = emptyList(),
    val tcZemeVoda: List<String> = emptyList(),
    val boxy: List<String> = emptyList(),
    val nadrze: Map<String, List<String>> = emptyMap(),
    val zasobniky: List<String> = emptyList(),
)
