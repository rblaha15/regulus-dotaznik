package cz.regulus.dotaznik

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Products")
data class Products(
    val heatPumpsAirToWater: List<String> = emptyList(),
    val heatPumpsGroundToWater: List<String> = emptyList(),
    val indoorUnits: List<String> = emptyList(),
    val thermalStores: Map<String, List<String>> = emptyMap(),
    val waterTanks: List<String> = emptyList(),
)
