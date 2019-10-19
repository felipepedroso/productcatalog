package br.pedroso.productcatalog.device.database.room.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

const val CATALOG_TABLE_NAME = "products"

@Entity(tableName = CATALOG_TABLE_NAME)
data class RoomProduct(
    @PrimaryKey val id: String,
    val sku: String,
    val name: String,
    val brand: String,
    val imageUrl: String,
    val publishedAt: Long,
    val isActive: Boolean,
    val currentPrice: Long,
    val originalPrice: Long,
    val priceCurrency: String
)

