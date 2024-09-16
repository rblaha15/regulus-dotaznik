package cz.regulus.dotaznik.prihlaseni

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.regulus.dotaznik.Repository
import cz.regulus.dotaznik.User
import cz.regulus.dotaznik.strings.strings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.seconds

@KoinViewModel
class PrihlaseniViewModel(
    private val repo: Repository,
    @InjectedParam private val navigateUp: () -> Unit,
) : ViewModel() {
    val employees = repo.people
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), emptyList())
    val representatives = employees.map {
        it.filter { clovek ->
            clovek.isRepresentative
        }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), emptyList())

    private val _newUser = MutableStateFlow(null as User?)
    val newUser = _newUser.asStateFlow()

    fun editNewUser(edit: (User?) -> User?) {
        _newUser.value = edit(_newUser.value)
    }

    private val _isEmployee = MutableStateFlow(false)
    val isEmployee = _isEmployee.asStateFlow()

    fun changeBeingEmployee(isEmployee: Boolean) {
        _isEmployee.value = isEmployee
        _newUser.value = null
    }

    fun confirm(
        error: (String) -> Unit,
    ) {
        val user = _newUser.value
            ?: if (isEmployee.value)
                return
            else {
                error(strings.logIn.representativeNeeded)
                return
            }
        if (user.name.isBlank()) {
            error(strings.logIn.nameNeeded)
            return
        }
        if (user.surname.isBlank()) {
            error(strings.logIn.surnameNeeded)
            return
        }
        if (user.email.isBlank()) {
            error(strings.logIn.emailNeeded)
            return
        }
        viewModelScope.launch {
            repo.logIn(user)
            withContext(Dispatchers.Main) {
                navigateUp()
            }
        }
    }

    fun cancel() {
        exitProcess(1)
    }
}