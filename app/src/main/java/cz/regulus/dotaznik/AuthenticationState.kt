package cz.regulus.dotaznik

import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthenticationState {
    @Serializable
    data object LoggedOut : AuthenticationState
    @Serializable
    data class LoggedIn(
        val user: User,
    ) : AuthenticationState
}

val AuthenticationState.userOrNull get() = if (this is AuthenticationState.LoggedIn) user else null