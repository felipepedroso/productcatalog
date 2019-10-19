package br.pedroso.productcatalog.domain.network

import br.pedroso.productcatalog.domain.entities.Product
import io.reactivex.Observable

interface CatalogServiceDataSource {
    fun retrieveAllProducts(): Observable<List<Product>>
}