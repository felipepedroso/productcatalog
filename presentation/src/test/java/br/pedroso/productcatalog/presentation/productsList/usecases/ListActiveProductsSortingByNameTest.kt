package br.pedroso.productcatalog.presentation.productsList.usecases

import br.pedroso.productcatalog.domain.entities.Price
import br.pedroso.productcatalog.domain.entities.Product
import br.pedroso.productcatalog.network.catalogService.retrofit.mappers.ProductMapper
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Completable
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test

class ListActiveProductsSortingByNameTest {

    private lateinit var listActiveProductsSortingByName: ListActiveProductsSortingByName

    private lateinit var getAllProductsFromDatabase: GetAllProductsFromDatabase

    private lateinit var retrieveProductsFromService: RetrieveProductsFromService

    private lateinit var registerProductsOnDatabase: RegisterProductsOnDatabase

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
    }

    @Before
    fun setup() {
        getAllProductsFromDatabase = mock()

        retrieveProductsFromService = mock()

        registerProductsOnDatabase = mock()

        listActiveProductsSortingByName = ListActiveProductsSortingByName(
            getAllProductsFromDatabase,
            retrieveProductsFromService,
            registerProductsOnDatabase
        )
    }

    @Test
    fun testProductsSorting() {
        whenever(getAllProductsFromDatabase.execute()).thenReturn(Observable.just(fakeProducts.shuffled()))
        whenever(retrieveProductsFromService.execute()).thenReturn(Observable.just(fakeProducts))
        whenever(registerProductsOnDatabase.execute(any())).thenReturn(Completable.complete())

        val sortedFakeProducts = fakeProducts.sortedBy { it.name }

        val testObserver = listActiveProductsSortingByName.execute(false).test()

        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue { products ->
                products.forEachIndexed { index, product ->
                    if (sortedFakeProducts[index] != product) {
                        return@assertValue false
                    }
                }

                true
            }
    }

    @Test
    fun testProductsInactiveFiltering() {
        val inactiveProducts = fakeProducts.map { it.copy(isActive = false) }

        whenever(getAllProductsFromDatabase.execute()).thenReturn(Observable.just(inactiveProducts))
        whenever(retrieveProductsFromService.execute()).thenReturn(Observable.just(inactiveProducts))
        whenever(registerProductsOnDatabase.execute(any())).thenReturn(Completable.complete())

        val testObserver = listActiveProductsSortingByName.execute(false).test()

        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValueCount(0)
    }

}