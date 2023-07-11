package cz.regulus.dotaznik.dotaznik

sealed interface OdesilaniState {
    data object Nic : OdesilaniState

    data class OpravduOdeslat(
        val email: String,
    ) : OdesilaniState

    data object Odesilani : OdesilaniState

    data object Uspech : OdesilaniState

    sealed interface Error : OdesilaniState {
        data object Offline : Error
        data class Podrobne(
            val error: String,
        ) : Error

    }

    data object OdstranitData : OdesilaniState
}
