package com.regulus.dotaznik

data class Uzivatel(
    var email: String = "",
    var jmeno: String = "",
    var prijmeni: String = "",
    var cisloKo: String = "",
    var jeZastupce: Boolean = false,
    var ico: String = "",
)