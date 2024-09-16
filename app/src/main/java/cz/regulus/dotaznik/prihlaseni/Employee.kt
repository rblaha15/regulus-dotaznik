package cz.regulus.dotaznik.prihlaseni

import cz.regulus.dotaznik.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Employee(
    @SerialName("jmeno")
    val name: String,
    @SerialName("prijmeni")
    val surname: String,
    val email: String,
    @SerialName("zastupce")
    val isRepresentative: Boolean,
    @SerialName("cislo")
    val number: String,
)

val Employee.wholeName get() = "$name $surname"

fun Employee.createUser(isItMe: Boolean) =
    if (isItMe) User(
        name = name,
        surname = surname,
        email = email,
        koNumber = number,
        isEmploee = true
    )
    else User(
        name = "",
        surname = "",
        email = "",
        koNumber = number,
        isEmploee = false,
    )