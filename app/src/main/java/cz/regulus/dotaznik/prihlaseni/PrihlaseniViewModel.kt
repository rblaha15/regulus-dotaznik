package cz.regulus.dotaznik.prihlaseni

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.regulus.dotaznik.R
import cz.regulus.dotaznik.Clovek
import cz.regulus.dotaznik.Repository
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
        println("$it 1")
        it.filter { clovek ->
            clovek.zastupce
        }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), emptyList())

    private val _novyClovek = MutableStateFlow(null as Clovek?)
    val novyClovek = _novyClovek.asStateFlow()

    fun upravitNovehoCloveka(upravit: (Clovek?) -> Clovek?) {
        _novyClovek.value = upravit(_novyClovek.value)
    }

    private val _jeZamestanec = MutableStateFlow(false)
    val jeZamestnanec = _jeZamestanec.asStateFlow()

    fun zmenitZdaJeZamestnanec(jeZamestanec: Boolean) {
        _jeZamestanec.value = jeZamestanec
        _novyClovek.value = null
    }

    fun potvrdit(
        chyba: (Int) -> Unit,
    ) {
        val clovek = _novyClovek.value
            ?: if (jeZamestnanec.value)
                return
            else {
                chyba(R.string.je_potreba_zadat_zastupce)
                return
            }
        if (clovek.jmeno.isBlank()) {
            chyba(R.string.je_potreba_zadat_jmeno)
            return
        }
        if (clovek.prijmeni.isBlank()) {
            chyba(R.string.je_potreba_zadat_prijmeni)
            return
        }
        if (clovek.email.isBlank()) {
            chyba(R.string.je_potreba_zadat_email)
            return
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