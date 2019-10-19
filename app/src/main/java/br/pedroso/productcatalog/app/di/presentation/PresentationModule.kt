package br.pedroso.productcatalog.app.di.presentation

import br.pedroso.productcatalog.app.di.presentation.productsList.ProductsListModule
import com.github.salomonbrys.kodein.Kodein

class PresentationModule {
    val graph = Kodein.Module {
        import(ProductsListModule().graph)
    }
}