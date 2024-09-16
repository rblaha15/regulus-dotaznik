package cz.regulus.dotaznik

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name: String,
    val surname: String,
    val email: String,
    val crn: String = "",
    val koNumber: String,
    val isEmploee: Boolean,
)