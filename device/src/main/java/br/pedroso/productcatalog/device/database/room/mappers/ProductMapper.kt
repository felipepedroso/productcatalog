package br.pedroso.productcatalog.device.database.room.mappers

import br.pedroso.productcatalog.device.database.room.entities.RoomProduct
import br.pedroso.productcatalog.domain.entities.Price
import br.pedroso.productcatalog.domain.entities.Product
import java.util.*

class ProductMapper {
    companion object {
        fun mapRoomToDomain(roomProduct: RoomProduct): Product {
            val price = Price(
                currency = roomProduct.priceCurrency,
                current = roomProduct.currentPrice,
                original = roomProduct.originalPrice
            )

            return Product(
                id = roomProduct.id,
                sku = roomProduct.sku,
                name = roomProduct.name,
                brand = roomProduct.brand,
                price = price,
                imageUrl = roomProduct.imageUrl,
                publishedAt = Date(roomProduct.publishedAt),
                isActive = roomProduct.isActive
            )
        }

        fun mapDomainToRoom(product: Product): RoomProduct {
            val price = product.price
            return RoomProduct(
                id = product.id,
                sku = product.sku,
                name = product.name,
                brand = product.brand,
                imageUrl = product.imageUrl,
                publishedAt = product.publishedAt.time,
                isActive = product.isActive,
                currentPrice = price.current,
                originalPrice = price.original,
                priceCurrency = price.currency
            )
        }
    }
}