package br.pedroso.productcatalog.network.catalogService.retrofit

import br.pedroso.productcatalog.network.catalogService.retrofit.entities.ServiceResult
import io.reactivex.Observable
import retrofit2.http.GET

interface CatalogService {
    @GET("mocked-data.json")
    fun retrieveAllProducts(): Observable<ServiceResult>
}