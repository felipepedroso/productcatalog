package br.pedroso.productcatalog.presentation.productsList.usecases

import br.pedroso.productcatalog.domain.device.CatalogDatabaseDatasource
import br.pedroso.productcatalog.domain.entities.Product
import io.reactivex.Observable
import io.reactivex.Scheduler

class GetAllProductsFromDatabase(
    private val workerScheduler: Scheduler,
    private val catalogDatabaseDatasource: CatalogDatabaseDatasource
) {

    fun execute(): Observable<List<Product>> {
        return catalogDatabaseDatasource.getAllProducts()
            .subscribeOn(workerScheduler)
    }
}