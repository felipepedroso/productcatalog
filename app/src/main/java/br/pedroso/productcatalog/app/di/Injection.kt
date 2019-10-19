package br.pedroso.productcatalog.app.di

import android.app.Application
import br.pedroso.productcatalog.app.di.device.DeviceModule
import br.pedroso.productcatalog.app.di.network.NetworkModule
import br.pedroso.productcatalog.app.di.presentation.PresentationModule
import br.pedroso.productcatalog.app.di.schedulers.SchedulersModule
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.android.androidActivityScope
import com.github.salomonbrys.kodein.lazy

class Injection(private val application: Application) {
    init {
        application.registerActivityLifecycleCallbacks(androidActivityScope.lifecycleManager)
    }

    val graph = Kodein.lazy {
        import(SchedulersModule().graph)
        import(PresentationModule().graph)
        import(NetworkModule().graph)
        import(DeviceModule(application).graph)
    }
}