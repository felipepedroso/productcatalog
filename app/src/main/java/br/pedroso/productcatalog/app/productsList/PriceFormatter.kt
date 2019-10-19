package br.pedroso.productcatalog.app.productsList

fun formatPrice(price: Long, currency: String): String {
    val priceFloat = price.toFloat() / 100

    return String.format("$currency %.2f", priceFloat)
}