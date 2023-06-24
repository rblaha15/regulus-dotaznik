package cz.regulus.dotaznik

import kotlinx.serialization.Serializable

@Serializable
sealed interface PrihlasenState {
    @Serializable
    object Odhasen : PrihlasenState
    @Serializable
    data class Prihlasen(
        val uzivatel: Clovek,
    ) : PrihlasenState
}