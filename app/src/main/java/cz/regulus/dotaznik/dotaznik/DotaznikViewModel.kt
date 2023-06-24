package cz.regulus.dotaznik.dotaznik

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.regulus.dotaznik.Repository
import cz.regulus.dotaznik.Stranky
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import kotlin.time.Duration.Companion.seconds

@KoinViewModel
class DotaznikViewModel(
    private val repo: Repository,
) : ViewModel() {
    val stranky = repo.stranky
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), Stranky())

    fun upravitStranky(stranky: Stranky) = viewModelScope.launch {
        repo.upravitStranky(stranky)
    }
}