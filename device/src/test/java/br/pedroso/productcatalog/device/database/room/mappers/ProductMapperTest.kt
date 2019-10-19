package br.pedroso.productcatalog.device.database.room.mappers

import br.pedroso.productcatalog.device.database.room.entities.RoomProduct
import br.pedroso.productcatalog.domain.entities.Price
import br.pedroso.productcatalog.domain.entities.Product
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class ProductMapperTest {
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


    private val fakeRoomProducts = (1..10).map { id ->

        RoomProduct(
            id = "WERTYUI$id",
            sku = "ASDFGHJ$id",
            name = "Product $id",
            imageUrl = "https://static-origin.zalora.com.my/p/jaxon-4980-310333-19.jpg",
            publishedAt = System.currentTimeMillis(),
            isActive = true,
            brand = "Brand $id",
            currentPrice = id * 100L,
            originalPrice = id * 90L,
            priceCurrency = "EUR"
        )
    }.toTypedArray()

    @Test
    fun testMapToDomain() {
        fakeRoomProducts
            .map { ProductMapper.mapRoomToDomain(it) }
            .forEachIndexed { index, product ->
                val roomProduct = fakeRoomProducts[index]

                assertEquals(roomProduct.id, product.id)
                assertEquals(roomProduct.sku, product.sku)
                assertEquals(roomProduct.brand, product.brand)
                assertEquals(roomProduct.imageUrl, product.imageUrl)
                assertEquals(roomProduct.isActive, product.isActive)
                assertEquals(roomProduct.name, product.name)
                assertEquals(roomProduct.publishedAt, product.publishedAt.time)
                assertEquals(roomProduct.currentPrice, product.price.current)
                assertEquals(roomProduct.originalPrice, product.price.original)
                assertEquals(roomProduct.priceCurrency, product.price.currency)
            }
    }

    @Test
    fun testMapToRoom() {
        fakeProducts
            .map { ProductMapper.mapDomainToRoom(it) }
            .forEachIndexed { index, roomProduct ->
                val product = fakeProducts[index]

                assertEquals(roomProduct.id, product.id)
                assertEquals(roomProduct.sku, product.sku)
                assertEquals(roomProduct.brand, product.brand)
                assertEquals(roomProduct.imageUrl, product.imageUrl)
                assertEquals(roomProduct.isActive, product.isActive)
                assertEquals(roomProduct.name, product.name)
                assertEquals(roomProduct.publishedAt, product.publishedAt.time)
                assertEquals(roomProduct.currentPrice, product.price.current)
                assertEquals(roomProduct.originalPrice, product.price.original)
                assertEquals(roomProduct.priceCurrency, product.price.currency)
            }
    }

}