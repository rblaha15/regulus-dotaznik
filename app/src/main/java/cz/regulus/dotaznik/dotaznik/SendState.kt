package cz.regulus.dotaznik.dotaznik

sealed interface SendState {
    data object Nothing : SendState

    data class MissingField(
        val fieldLabel: String,
    ) : SendState

    data class ConfirmSend(
        val email: String,
    ) : SendState

    data object Sending : SendState

    data object Success : SendState

    sealed interface Error : SendState {
        data object Offline : Error
        data object Other : Error
        data class Details(
            val error: String,
        ) : Error
    }

    data object ConfirmDataRemoval : SendState
}
