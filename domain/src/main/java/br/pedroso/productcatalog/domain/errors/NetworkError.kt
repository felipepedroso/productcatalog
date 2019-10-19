package br.pedroso.productcatalog.domain.errors

sealed class NetworkError(cause: Throwable) : Throwable(cause) {
    class ContentNotFound(cause: Throwable) : NetworkError(cause)
    class TimeoutError(cause: Throwable) : NetworkError(cause)
    class UnexpectedError(cause: Throwable) : NetworkError(cause)
}