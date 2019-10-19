package br.pedroso.productcatalog.presentation.productsList.usecases

import br.pedroso.productcatalog.domain.device.CatalogDatabaseDatasource
import br.pedroso.productcatalog.domain.entities.Product
import io.reactivex.Completable
import io.reactivex.Scheduler

class RegisterProductsOnDatabase(
    private val workerScheduler: Scheduler,
    private val catalogDatabaseDatasource: CatalogDatabaseDatasource
) {
    fun execute(vararg products: Product): Completable {
        return catalogDatabaseDatasource.registerProduct(*products)
            .subscribeOn(workerScheduler)
    }
}