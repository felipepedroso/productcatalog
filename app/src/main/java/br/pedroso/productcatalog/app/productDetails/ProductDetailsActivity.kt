package br.pedroso.productcatalog.app.productDetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.MenuItem
import br.pedroso.productcatalog.domain.entities.Product
import br.pedroso.productcatalog.R
import br.pedroso.productcatalog.app.extensions.hide
import br.pedroso.productcatalog.app.extensions.show
import br.pedroso.productcatalog.app.utils.isLollipopOrNewer
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_product_details.*


class ProductDetailsActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_PRODUCT_IMAGE_NAME = "EXTRA_PRODUCT_IMAGE_NAME"
        private const val EXTRA_PRODUCT_IMAGE_URL = "EXTRA_PRODUCT_IMAGE_URL"
        private const val EXTRA_PRODUCT_IMAGE_TRANSITION_NAME = "EXTRA_PRODUCT_IMAGE_TRANSITION_NAME"

        fun createIntent(context: Context, product: Product): Intent {
            val intent = Intent(context, ProductDetailsActivity::class.java)

            intent.apply {
                putExtra(EXTRA_PRODUCT_IMAGE_URL, product.imageUrl)
                putExtra(EXTRA_PRODUCT_IMAGE_NAME, product.name)


                if (isLollipopOrNewer()) {
                    putExtra(EXTRA_PRODUCT_IMAGE_TRANSITION_NAME, product.id)
                }
            }

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
    }

    private fun setupView() {
        setContentView(R.layout.activity_product_details)

        supportPostponeEnterTransition()

        setupTransition()

        setupTitle()

        loadImage()
    }

    private fun setupTitle() {
        val productName =
            intent?.extras?.getString(EXTRA_PRODUCT_IMAGE_NAME) ?: getString(R.string.product_details_default_title)
        title = productName
    }

    private fun loadImage() {
        val imageUrl = intent?.extras?.getString(EXTRA_PRODUCT_IMAGE_URL)

        imageViewErrorPlaceholder.hide()

        if (imageUrl != null) {
            Picasso.get()
                .load(imageUrl)
                .noFade()
                .into(imageViewProductPhoto, object : Callback {
                    override fun onSuccess() {
                        supportStartPostponedEnterTransition()
                    }

                    override fun onError(e: Exception?) {
                        supportStartPostponedEnterTransition()
                        imageViewErrorPlaceholder.show()
                    }
                })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupTransition() {
        if (isLollipopOrNewer()) {
            val imageTransitionName = intent?.extras?.getString(EXTRA_PRODUCT_IMAGE_TRANSITION_NAME)

            if (imageTransitionName != null && !TextUtils.isEmpty(imageTransitionName)) {
                ViewCompat.setTransitionName(imageViewProductPhoto, imageTransitionName)
            }
        }
    }

}
