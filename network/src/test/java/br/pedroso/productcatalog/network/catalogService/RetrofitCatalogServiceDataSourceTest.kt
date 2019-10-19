package br.pedroso.productcatalog.network.catalogService

import br.pedroso.productcatalog.domain.entities.Price
import br.pedroso.productcatalog.domain.entities.Product
import br.pedroso.productcatalog.domain.errors.NetworkError
import br.pedroso.productcatalog.network.catalogService.retrofit.CatalogService
import br.pedroso.productcatalog.network.catalogService.retrofit.entities.ServiceResult
import br.pedroso.productcatalog.network.catalogService.retrofit.mappers.ProductMapper
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException

class RetrofitCatalogServiceDataSourceTest {
    private lateinit var catalogService: CatalogService

    private lateinit var catalogServiceDataSource: RetrofitCatalogServiceDataSource

    private val fakeProducts = (1..10).map { id ->
        val price = Price(
            currency = "EUR",
            current = id * 100L,
            original = id * 90L
        )

        Product(
            id = "WERTYUI$id",
            sku = "ASDFGHJ$id",
            name = "Product $id",
            imageUrl = "https://static-origin.zalora.com.my/p/jaxon-4980-310333-19.jpg",
            publishedAt = ProductMapper.parseDate("2018-12-12 14:00:00"),
            isActive = true,
            price = price,
            brand = "Brand $id"
        )
    }.toTypedArray()

    private val fakeRetrofitProducts = fakeProducts.map { ProductMapper.mapDomainToRetrofit(it) }

    private val fakeServiceResult = ServiceResult(fakeRetrofitProducts)

    private val emptyServiceResult = ServiceResult(emptyList())

    @Before
    fun setup() {
        catalogService = mock()

        catalogServiceDataSource = RetrofitCatalogServiceDataSource(catalogService)
    }

    @Test
    fun testRetrieveAllProducts() {
        whenever(catalogService.retrieveAllProducts()).thenReturn(Observable.just(fakeServiceResult))

        val testObserver = catalogServiceDataSource.retrieveAllProducts().test()

        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue { products ->
                products.forEachIndexed { index, product ->
                    if (fakeProducts[index] != product) {
                        return@assertValue false
                    }
                }

                true
            }

        verify(catalogService, times(1)).retrieveAllProducts()
    }

    @Test
    fun testRetrieveAllProductsWithEmptyResult() {
        whenever(catalogService.retrieveAllProducts()).thenReturn(Observable.just(emptyServiceResult))

        val testObserver = catalogServiceDataSource.retrieveAllProducts().test()

        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValueCount(0)

        verify(catalogService, times(1)).retrieveAllProducts()
    }

    @Test
    fun testRetrieveAllProductsWithContentNotFoundError() {
        val exception = HttpException(
            Response.error<String>(
                404,
                ResponseBody.create(null, "")
            )
        )

        whenever(catalogService.retrieveAllProducts()).thenReturn(Observable.error(exception))

        val testObserver = catalogServiceDataSource.retrieveAllProducts().test()

        testObserver
            .assertNotComplete()
            .assertError { it is NetworkError.ContentNotFound }
            .assertError { it.cause == exception }

        verify(catalogService, times(1)).retrieveAllProducts()

    }

    @Test
    fun testRetrieveAllProductsWithTimeoutError() {
        val exception = SocketTimeoutException()

        whenever(catalogService.retrieveAllProducts()).thenReturn(Observable.error(exception))

        val testObserver = catalogServiceDataSource.retrieveAllProducts().test()

        testObserver
            .assertNotComplete()
            .assertError { it is NetworkError.TimeoutError }
            .assertError { it.cause == exception }

        verify(catalogService, times(1)).retrieveAllProducts()
    }

    @Test
    fun testRetrieveAllProductsWithUnexpectedError() {
        val exception = Throwable()

        whenever(catalogService.retrieveAllProducts()).thenReturn(Observable.error(exception))

        val testObserver = catalogServiceDataSource.retrieveAllProducts().test()

        testObserver
            .assertNotComplete()
            .assertError { it is NetworkError.UnexpectedError }
            .assertError { it.cause == exception }

        verify(catalogService, times(1)).retrieveAllProducts()
    }

}