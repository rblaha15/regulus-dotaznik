package cz.regulus.dotaznik

import android.app.Application
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(module {
                single(named("cache")) {
                    get<Context>().cacheDir!!
                }
                single(named("files")) {
                    get<Context>().filesDir!!
                }
                single {
                    get<Context>().resources!!
                }
            })
            defaultModule()
        }
    }
}