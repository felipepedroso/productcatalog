package br.pedroso.productcatalog.app

import android.app.Application
import br.pedroso.productcatalog.app.di.Injection
import com.github.salomonbrys.kodein.KodeinAware
import timber.log.Timber

class ProductCatalogApplication : Application(), KodeinAware {

    override val kodein by Injection(this).graph

    override fun onCreate() {
        super.onCreate()
        initializeTimber()
    }

    private fun initializeTimber() {
        Timber.plant(Timber.DebugTree())
    }
}