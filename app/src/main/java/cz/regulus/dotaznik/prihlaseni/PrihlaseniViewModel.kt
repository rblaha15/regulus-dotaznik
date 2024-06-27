package cz.regulus.dotaznik.prihlaseni

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.regulus.dotaznik.Repository
import cz.regulus.dotaznik.Uzivatel
import cz.regulus.dotaznik.strings.GenericStringsProvider
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
    val zamestnanci = repo.lidi
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), emptyList())
    val zastupci = zamestnanci.map {
        it.filter { clovek ->
            clovek.zastupce
        }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), emptyList())

    private val _novyClovek = MutableStateFlow(null as Uzivatel?)
    val novyClovek = _novyClovek.asStateFlow()

    fun upravitNovehoCloveka(upravit: (Uzivatel?) -> Uzivatel?) {
        _novyClovek.value = upravit(_novyClovek.value)
    }

    private val _jeZamestanec = MutableStateFlow(false)
    val jeZamestnanec = _jeZamestanec.asStateFlow()

    fun zmenitZdaJeZamestnanec(jeZamestanec: Boolean) {
        _jeZamestanec.value = jeZamestanec
        _novyClovek.value = null
    }

    fun potvrdit(
        chyba: (String) -> Unit,
    ) = with(GenericStringsProvider) {
        val clovek = _novyClovek.value
            ?: if (jeZamestnanec.value)
                return@with
            else {
                chyba(strings.jePotrebaZadatZastupce)
                return@with
            }
        if (clovek.jmeno.isBlank()) {
            chyba(strings.jePotrebaZadatJmeno)
            return@with
        }
        if (clovek.prijmeni.isBlank()) {
            chyba(strings.jePotrebaZadatPrijmeni)
            return@with
        }
        if (clovek.email.isBlank()) {
            chyba(strings.jePotrebaZadatEmail)
            return@with
        }
        viewModelScope.launch {
            repo.prihlasit(
                clovek
            )
            withContext(Dispatchers.Main) {
                navigateUp()
            }
        }
    }

    fun zrusit() {
        exitProcess(1)
    }
}