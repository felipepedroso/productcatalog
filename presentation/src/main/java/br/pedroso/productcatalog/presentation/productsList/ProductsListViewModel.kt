package br.pedroso.productcatalog.presentation.productsList

import android.arch.lifecycle.ViewModel
import br.pedroso.productcatalog.domain.entities.ViewState
import br.pedroso.productcatalog.presentation.productsList.usecases.ListActiveProductsSortingByName
import io.reactivex.Observable
import io.reactivex.Scheduler

class ProductsListViewModel(
    private val uiScheduler: Scheduler,
    private val listActiveProductsSortingByName: ListActiveProductsSortingByName
) : ViewModel() {

    fun listActiveProducts(forceRefresh: Boolean): Observable<ViewState> {
        return listActiveProductsSortingByName.execute(forceRefresh)
            .compose(ProductsListViewStateTransformer())
            .observeOn(uiScheduler)
    }
}