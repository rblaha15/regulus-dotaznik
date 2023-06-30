package cz.regulus.dotaznik

import android.app.Application
import android.content.Context
import cz.regulus.dotaznik.dotaznik.DotaznikViewModel
import cz.regulus.dotaznik.prihlaseni.PrihlaseniViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule
import java.io.File

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(module {
                single {
                    get<Context>().cacheDir!!
                }
                single {
                    get<Context>().resources!!
                }
            })
            defaultModule()
        }
    }
}