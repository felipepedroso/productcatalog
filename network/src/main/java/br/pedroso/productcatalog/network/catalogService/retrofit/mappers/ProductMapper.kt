package br.pedroso.productcatalog.network.catalogService.retrofit.mappers

import br.pedroso.productcatalog.domain.entities.Price
import br.pedroso.productcatalog.domain.entities.Product
import br.pedroso.productcatalog.network.catalogService.retrofit.entities.RetrofitPrice
import br.pedroso.productcatalog.network.catalogService.retrofit.entities.RetrofitProduct
import org.jetbrains.annotations.TestOnly
import java.text.SimpleDateFormat
import java.util.*

sealed class ProductMapper {
    companion object {
        private const val SERVICE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
        private const val HTTP_PREFIX = "http://"
        private const val HTTPS_PREFIX = "https://"

        fun mapRetrofitToDomain(retrofitProduct: RetrofitProduct) = Product(
            id = retrofitProduct.id,
            sku = retrofitProduct.sku,
            name = retrofitProduct.name,
            price = PriceMapper.mapRetrofitToDomain(retrofitProduct.price),
            brand = retrofitProduct.brand,
            imageUrl = retrofitProduct.imageUrl.replace(HTTP_PREFIX, HTTPS_PREFIX),
            publishedAt = parseDate(retrofitProduct.publishedAt),
            isActive = retrofitProduct.isActive
        )

        fun parseDate(dateString: String): Date {
            val dateFormat = SimpleDateFormat(SERVICE_DATE_FORMAT, Locale.ENGLISH)
            return dateFormat.parse(dateString)
        }

        @TestOnly
        fun mapDomainToRetrofit(product: Product) = RetrofitProduct(
            id = product.id,
            sku = product.sku,
            name = product.name,
            price = PriceMapper.mapDomainToRetrofit(product.price),
            brand = product.brand,
            imageUrl = product.imageUrl.replace(HTTP_PREFIX, HTTPS_PREFIX),
            publishedAt = formatDate(product.publishedAt),
            isActive = product.isActive
        )

        @TestOnly
        private fun formatDate(date: Date): String {
            val dateFormat = SimpleDateFormat(SERVICE_DATE_FORMAT, Locale.ENGLISH)
            return dateFormat.format(date)
        }
    }
}

sealed class PriceMapper {
    companion object {
        private const val CENTS_CONVERSION_FACTOR = 100L

        fun mapRetrofitToDomain(retrofitPrice: RetrofitPrice) : Price{
            val conversionFactor = CENTS_CONVERSION_FACTOR
            return Price(
                current = (retrofitPrice.current * conversionFactor).toLong(),
                original = (retrofitPrice.original * conversionFactor).toLong(),
                currency = retrofitPrice.currency
            )
        }

        @TestOnly
        fun mapDomainToRetrofit(price: Price): RetrofitPrice {
            val conversionFactor = CENTS_CONVERSION_FACTOR.toFloat()
            return RetrofitPrice(
                current = price.current.toFloat() / conversionFactor,
                original = price.original.toFloat() / conversionFactor,
                currency = price.currency
            )
        }
    }
}