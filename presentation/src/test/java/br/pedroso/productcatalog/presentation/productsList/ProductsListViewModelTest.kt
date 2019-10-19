package br.pedroso.productcatalog.presentation.productsList

import br.pedroso.productcatalog.domain.entities.Price
import br.pedroso.productcatalog.domain.entities.Product
import br.pedroso.productcatalog.domain.entities.ViewState
import br.pedroso.productcatalog.network.catalogService.retrofit.mappers.ProductMapper
import br.pedroso.productcatalog.presentation.productsList.usecases.ListActiveProductsSortingByName
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

class ProductsListViewModelTest {
    private val testScheduler = Schedulers.trampoline()

    private lateinit var listActiveProductsSortingByName: ListActiveProductsSortingByName

    private lateinit var productsListViewModel: ProductsListViewModel

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
        listActiveProductsSortingByName = mock()

        productsListViewModel = ProductsListViewModel(testScheduler, listActiveProductsSortingByName)
    }

    @Test
    fun testViewStateMappingWithContent() {
        whenever(listActiveProductsSortingByName.execute(any())).thenReturn(Observable.just(fakeProducts))

        val testObserver = productsListViewModel.listActiveProducts(true).test()

        testObserver
            .assertComplete()
            .assertNoErrors()
            .assertValueCount(2)
            .assertValueAt(0) { it is ViewState.Loading }
            .assertValueAt(1) {
                if (it is ViewState.ShowContent<*>) {
                    val contentProducts = it.contentValue as List<Product>

                    contentProducts.forEachIndexed { index, product ->
                        if (product != fakeProducts[index]) {
                            return@assertValueAt false
                        }
                    }

                    true
                } else {
                    false
                }
            }
    }

    @Test
    fun testViewStateMappingWithEmptyValue() {
        whenever(listActiveProductsSortingByName.execute(any())).thenReturn(Observable.empty())

        val testObserver = productsListViewModel.listActiveProducts(true).test()

        testObserver
            .assertComplete()
            .assertNoErrors()
            .assertValueCount(2)
            .assertValueAt(0) { it is ViewState.Loading }
            .assertValueAt(1) { it is ViewState.Empty }
    }

    @Test
    fun testViewStateMappingWithError() {
        val exception = Throwable()

        whenever(listActiveProductsSortingByName.execute(any())).thenReturn(Observable.error(exception))

        val testObserver = productsListViewModel.listActiveProducts(true).test()

        testObserver
            .assertComplete()
            .assertNoErrors()
            .assertValueCount(2)
            .assertValueAt(0) { it is ViewState.Loading }
            .assertValueAt(1) { it is ViewState.Error && it.error == exception }
    }

}