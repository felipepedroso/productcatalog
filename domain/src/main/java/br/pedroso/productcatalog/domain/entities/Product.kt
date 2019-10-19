package br.pedroso.productcatalog.domain.entities

import java.util.*

data class Product(
    val id: String,
    val sku: String,
    val name: String,
    val price: Price,
    val brand: String,
    val imageUrl: String,
    val publishedAt: Date,
    val isActive: Boolean
) {
    override fun equals(other: Any?): Boolean {
        return if (other is Product) {
            id == other.id &&
                    sku == other.sku &&
                    name == other.name &&
                    price == other.price &&
                    brand == other.brand &&
                    imageUrl == imageUrl &&
                    publishedAt.time == other.publishedAt.time &&
                    isActive == other.isActive
        } else {
            false
        }
    }
}

data class Price(
    val current: Long,
    val original: Long,
    val currency: String
)
