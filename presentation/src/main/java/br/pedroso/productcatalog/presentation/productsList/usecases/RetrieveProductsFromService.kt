package br.pedroso.productcatalog.presentation.productsList.usecases

import br.pedroso.productcatalog.domain.entities.Product
import br.pedroso.productcatalog.domain.network.CatalogServiceDataSource
import io.reactivex.Observable
import io.reactivex.Scheduler

class RetrieveProductsFromService(
    private val workerScheduler: Scheduler,
    private val catalogServiceDataSource: CatalogServiceDataSource
) {

    fun execute(): Observable<List<Product>> {
        return catalogServiceDataSource.retrieveAllProducts()
            .subscribeOn(workerScheduler)
    }
}