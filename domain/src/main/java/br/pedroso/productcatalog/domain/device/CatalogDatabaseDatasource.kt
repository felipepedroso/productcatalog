package br.pedroso.productcatalog.domain.device

import br.pedroso.productcatalog.domain.entities.Product
import io.reactivex.Completable
import io.reactivex.Observable

interface CatalogDatabaseDatasource {
    fun registerProduct(vararg products: Product): Completable

    fun getAllProducts(): Observable<List<Product>>
}