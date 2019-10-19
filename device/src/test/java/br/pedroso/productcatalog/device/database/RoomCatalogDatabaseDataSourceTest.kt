package br.pedroso.productcatalog.device.database

import br.pedroso.productcatalog.device.database.room.dao.CatalogDao
import br.pedroso.productcatalog.device.database.room.mappers.ProductMapper
import br.pedroso.productcatalog.domain.entities.Price
import br.pedroso.productcatalog.domain.entities.Product
import br.pedroso.productcatalog.domain.errors.DatabaseError
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import java.util.*

class RoomCatalogDatabaseDataSourceTest {

    private lateinit var catalogDao: CatalogDao

    private lateinit var catalogDatabaseDataSource: RoomCatalogDatabaseDataSource

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
            publishedAt = Date(System.currentTimeMillis()),
            isActive = true,
            price = price,
            brand = "Brand $id"
        )
    }.toTypedArray()

    private val fakeRoomProducts = fakeProducts.map { ProductMapper.mapDomainToRoom(it) }

    private val fakeException = Throwable("FAKE EXCEPTION")

    @Before
    fun setup() {
        catalogDao = mock()

        catalogDatabaseDataSource = RoomCatalogDatabaseDataSource(catalogDao)
    }

    @Test
    fun testRegisterOneProduct() {
        val productIndex = 0
        val productToRegister = fakeProducts[productIndex]
        val roomProduct = fakeRoomProducts[productIndex]

        val testObserver = catalogDatabaseDataSource.registerProduct(productToRegister).test()

        testObserver
            .assertNoErrors()
            .assertComplete()

        verify(catalogDao, times(1)).registerProduct(roomProduct)
    }

    @Test
    fun testRegisterNProducts() {
        val testObserver = catalogDatabaseDataSource.registerProduct(*fakeProducts).test()

        testObserver
            .assertNoErrors()
            .assertComplete()

        fakeRoomProducts.forEach { roomProduct ->
            verify(catalogDao, times(1)).registerProduct(roomProduct)
        }
    }

    @Test
    fun testRegisterProductsErrorMapping() {
        whenever(catalogDao.registerProduct(any())).thenAnswer { throw fakeException }

        val productIndex = 0
        val productToRegister = fakeProducts[productIndex]
        val roomProduct = fakeRoomProducts[productIndex]

        val testObserver = catalogDatabaseDataSource.registerProduct(productToRegister).test()

        testObserver
            .assertNotComplete()
            .assertError { it is DatabaseError }
            .assertError { it.cause == fakeException }

        verify(catalogDao, times(1)).registerProduct(roomProduct)
    }


    @Test
    fun testRegisterNProductsErrorMapping() {
        whenever(catalogDao.registerProduct(any())).thenAnswer { throw fakeException }

        val testObserver = catalogDatabaseDataSource.registerProduct(*fakeProducts).test()

        testObserver
            .assertNotComplete()
            .assertError { it is DatabaseError }
            .assertError { it.cause == fakeException }

        verify(catalogDao, times(1)).registerProduct(any())
    }

    @Test
    fun testGetAllProducts() {
        whenever(catalogDao.getAllProducts()).thenReturn(Single.just(fakeRoomProducts))

        val testObserver = catalogDatabaseDataSource.getAllProducts().test()

        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue {
                it == fakeProducts.toList()
            }

        verify(catalogDao, times(1)).getAllProducts()
    }

    @Test
    fun testGetAllProductsWithEmptyListReturn() {
        whenever(catalogDao.getAllProducts()).thenReturn(Single.just(emptyList()))

        val testObserver = catalogDatabaseDataSource.getAllProducts().test()

        testObserver
            .assertNoErrors()
            .assertValueCount(0)
            .assertComplete()

        verify(catalogDao, times(1)).getAllProducts()
    }

    @Test
    fun testGetAllProductsErrorMapping() {
        whenever(catalogDao.getAllProducts()).thenReturn(Single.error(fakeException))

        val testObserver = catalogDatabaseDataSource.getAllProducts().test()

        testObserver
            .assertNotComplete()
            .assertValueCount(0)
            .assertError { it is DatabaseError }
            .assertError { it.cause == fakeException }

        verify(catalogDao, times(1)).getAllProducts()
    }
}