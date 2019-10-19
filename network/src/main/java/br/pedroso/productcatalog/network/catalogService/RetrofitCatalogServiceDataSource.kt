package br.pedroso.productcatalog.network.catalogService

import br.pedroso.productcatalog.domain.entities.Product
import br.pedroso.productcatalog.domain.errors.NetworkError
import br.pedroso.productcatalog.domain.network.CatalogServiceDataSource
import br.pedroso.productcatalog.network.catalogService.retrofit.CatalogService
import br.pedroso.productcatalog.network.catalogService.retrofit.mappers.ProductMapper
import io.reactivex.Observable
import io.reactivex.functions.Function
import retrofit2.HttpException
import java.net.SocketTimeoutException

class RetrofitCatalogServiceDataSource(
    private val catalogService: CatalogService
) : CatalogServiceDataSource {

    override fun retrieveAllProducts(): Observable<List<Product>> {
        return catalogService.retrieveAllProducts()
            .map { serviceResult ->
                serviceResult.items.map { ProductMapper.mapRetrofitToDomain(it) }
            }
            .flatMap { products ->
                if (products.isEmpty()) {
                    Observable.empty()
                } else {
                    Observable.just(products)
                }
            }
            .onErrorResumeNext(CatalogErrorMapper())
    }

    private class CatalogErrorMapper<T> : Function<Throwable, Observable<T>> {
        override fun apply(cause: Throwable): Observable<T> {
            val error = if (cause is HttpException && cause.code() == 404) {
                NetworkError.ContentNotFound(cause)
            } else if (cause is SocketTimeoutException) {
                NetworkError.TimeoutError(cause)
            } else {
                NetworkError.UnexpectedError(cause)
            }

            return Observable.error(error)
        }
    }

}