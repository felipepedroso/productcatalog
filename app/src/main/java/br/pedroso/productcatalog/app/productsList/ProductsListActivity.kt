package br.pedroso.productcatalog.app.productsList

import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import br.pedroso.productcatalog.domain.entities.Product
import br.pedroso.productcatalog.domain.entities.ViewState
import br.pedroso.productcatalog.domain.errors.DatabaseError
import br.pedroso.productcatalog.domain.errors.NetworkError
import br.pedroso.productcatalog.presentation.productsList.ProductsListViewModel
import br.pedroso.productcatalog.R
import br.pedroso.productcatalog.app.base.BaseActivity
import br.pedroso.productcatalog.app.extensions.hide
import br.pedroso.productcatalog.app.extensions.show
import br.pedroso.productcatalog.app.extensions.viewModelProvider
import br.pedroso.productcatalog.app.productDetails.ProductDetailsActivity
import br.pedroso.productcatalog.app.utils.isLollipopOrNewer
import com.github.salomonbrys.kodein.LazyKodein
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import kotlinx.android.synthetic.main.activity_products_list_content.*
import kotlinx.android.synthetic.main.activity_products_list_error_state.*
import timber.log.Timber

class ProductsListActivity : BaseActivity() {

    private val kodein by lazy { LazyKodein(appKodein) }
    private val productsListViewModel by viewModelProvider { kodein.value.instance<ProductsListViewModel>() }
    private val productsAdapter by lazy { ProductsListAdapter(this, ::clickedOnProduct) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
    }

    private fun setupView() {
        setContentView(R.layout.activity_products_list)

        setupActionBar()

        setupRecyclerView()

        setupRetryButton()

        setupSwipeRefreshLayout()
    }

    private fun setupActionBar() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
    }

    private fun setupSwipeRefreshLayout() {
        contentContainer.setOnRefreshListener { loadProductsList(true) }
    }

    private fun setupRetryButton() {
        buttonRetry.setOnClickListener {
            loadProductsList()
        }
    }

    private fun setupRecyclerView() {
        val columnsAmount = resources.getInteger(R.integer.product_list_columns_amount)
        val manager = GridLayoutManager(this, columnsAmount)

        recyclerViewProductsList.apply {
            adapter = productsAdapter
            layoutManager = manager
            setHasFixedSize(true)
        }
    }

    override fun onResume() {
        super.onResume()
        loadProductsList()
    }

    private fun loadProductsList(forceRefresh: Boolean = false) {
        val subscription = productsListViewModel.listActiveProducts(forceRefresh)
            .doOnSubscribe { resetScreenState() }
            .subscribe(this::handleLoadProductsListNewState, this::logUnknownError)

        registerSubscription(subscription)
    }

    private fun resetScreenState() {
        contentContainer.show()
        errorStateContainer.hide()
    }

    private fun handleLoadProductsListNewState(viewState: ViewState) = when (viewState) {
        ViewState.Loading -> showLoading()
        is ViewState.Error -> showErrorState(viewState.error).also { hideLoading() }
        is ViewState.ShowContent<*> -> showProductsList(viewState.contentValue as List<Product>).also { hideLoading() }
        is ViewState.Empty -> showEmptyState().also { hideLoading() }
        else -> Timber.d("State not being used on this screen: $viewState")
    }

    private fun showEmptyState() {
        contentContainer.hide()
        errorStateContainer.show()
        displayErrorMessage(R.drawable.ic_empty, R.string.empty_state_message)
    }

    private fun showProductsList(productsList: List<Product>) {
        contentContainer.show()
        productsAdapter.setContent(productsList)
    }

    private fun showErrorState(error: Throwable) {
        errorStateContainer.show()
        contentContainer.hide()

        Timber.e(error, "Error during execution")

        handleError(error)
    }

    private fun handleError(error: Throwable) = when (error) {
        is NetworkError -> displayErrorMessage(R.drawable.ic_warning, R.string.network_error_message)
        is DatabaseError -> displayErrorMessage(R.drawable.ic_warning, R.string.database_error_message)
        else -> displayErrorMessage(R.drawable.ic_generic_error, R.string.generic_error_message)
    }

    private fun displayErrorMessage(@DrawableRes errorImageResource: Int, @StringRes errorMessageResource: Int) {
        imageViewErrorIcon.setImageResource(errorImageResource)
        textViewErrorMessage.setText(errorMessageResource)
    }

    private fun showLoading() {
        contentContainer.isRefreshing = true
    }

    private fun hideLoading() {
        contentContainer.isRefreshing = false
    }

    private fun logUnknownError(error: Throwable) = Timber.e(error, "Unknown error!")

    private fun clickedOnProduct(product: Product, productImageView: View) {
        val intent = ProductDetailsActivity.createIntent(this, product)

        if (isLollipopOrNewer()) {
            val productSharedElementName = ViewCompat.getTransitionName(productImageView) ?: product.id

            val options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this, productImageView, productSharedElementName)

            startActivity(intent, options.toBundle())
        } else {
            startActivity(intent)
        }
    }
}
