package br.pedroso.productcatalog.network.catalogService.retrofit.entities

import com.google.gson.annotations.SerializedName

data class ServiceResult(val items: List<RetrofitProduct>)

data class RetrofitProduct(
    val id: String,
    val sku: String,
    val name: String,
    val price: RetrofitPrice,
    val brand: String,
    @SerializedName("image") val imageUrl: String,
    @SerializedName("published_at") val publishedAt: String,
    @SerializedName("is_active") val isActive: Boolean
)

data class RetrofitPrice(
    val current: Float,
    val original: Float,
    val currency: String
)