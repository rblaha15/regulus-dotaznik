package com.regulus.dotaznik

data class Clovek(
    var email: String = "",
    var jmeno: String = "",
    var prijmeni: String = "",
    var cislo_ko: String = "",
    var jeZastupce: Boolean = false,
    var ico: String = "",
)