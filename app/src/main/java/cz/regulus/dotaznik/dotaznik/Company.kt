package cz.regulus.dotaznik.dotaznik

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Company(
    @SerialName("jmeno")
    val name: String,
    @SerialName("ico")
    val crn: String,
)
