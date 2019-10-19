package br.pedroso.productcatalog.domain.entities

sealed class ViewState {
    object Loading : ViewState()
    class ShowContent<out T>(val contentValue: T) : ViewState()
    class Error(val error: Throwable) : ViewState()
    object Empty : ViewState()

    override fun toString() = this.javaClass.simpleName!!
}
