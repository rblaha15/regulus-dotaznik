package cz.regulus.dotaznik.dotaznik

sealed interface OdesilaniState {
    object Nic : OdesilaniState

    data class OpravduOdeslat(
        val email: String,
    ) : OdesilaniState

    object Odesilani: OdesilaniState

    object UspechAOdstranitData : OdesilaniState

    sealed interface Error : OdesilaniState {
        object Offline : Error

        data class Podrobne(
            val error: String,
        ) : Error
    }
}
