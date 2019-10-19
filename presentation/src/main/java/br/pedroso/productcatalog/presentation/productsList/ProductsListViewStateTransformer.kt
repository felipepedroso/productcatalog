package br.pedroso.productcatalog.presentation.productsList

import br.pedroso.productcatalog.domain.entities.Product
import br.pedroso.productcatalog.domain.entities.ViewState
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class ProductsListViewStateTransformer : ObservableTransformer<List<Product>, ViewState> {
    override fun apply(upstream: Observable<List<Product>>): ObservableSource<ViewState> {
        return upstream
            .map { ViewState.ShowContent(it) as ViewState }
            .defaultIfEmpty(ViewState.Empty)
            .onErrorReturn { ViewState.Error(it) }
            .startWith(ViewState.Loading)
    }
}