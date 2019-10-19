package br.pedroso.productcatalog.presentation.productsList.usecases

import br.pedroso.productcatalog.domain.entities.Product
import io.reactivex.Completable
import io.reactivex.Observable

class ListActiveProductsSortingByName(
    private val getAllProductsFromDatabase: GetAllProductsFromDatabase,
    private val retrieveProductsFromService: RetrieveProductsFromService,
    private val registerProductsOnDatabase: RegisterProductsOnDatabase
) {

    fun execute(forceRefresh: Boolean): Observable<List<Product>> {
        val productsObservable = if (forceRefresh) {
            retrieveFromService()
        } else {
            getFromDatabase()
        }

        return productsObservable
            .flatMap { productsList ->
                val activeProducts = productsList
                    .asSequence()
                    .filter { it.isActive }
                    .sortedBy { it.name.toLowerCase() }
                    .toList()

                if (activeProducts.isNotEmpty()) {
                    Observable.just(activeProducts)
                } else {
                    Observable.empty()
                }
            }
    }

    private fun getFromDatabase(): Observable<List<Product>> {
        return getAllProductsFromDatabase.execute()
            .switchIfEmpty(retrieveFromService())
    }

    private fun retrieveFromService(): Observable<List<Product>> {
        return retrieveProductsFromService.execute()
            .flatMap { products ->
                registerProductsOnDatabase(products).andThen(Observable.just(products))
            }
    }

    private fun registerProductsOnDatabase(products: List<Product>): Completable {
        return registerProductsOnDatabase.execute(*products.toTypedArray())
    }

}