package br.pedroso.productcatalog.app.productsList

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.support.v4.view.ViewCompat
import android.support.v7.graphics.Palette
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.pedroso.productcatalog.domain.entities.Price
import br.pedroso.productcatalog.domain.entities.Product
import br.pedroso.productcatalog.R
import br.pedroso.productcatalog.app.extensions.hide
import br.pedroso.productcatalog.app.extensions.show
import br.pedroso.productcatalog.app.utils.isLollipopOrNewer
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_product_content.view.*
import timber.log.Timber


class ProductsListAdapter(
    private val context: Context,
    private val itemOnClickListener: (Product, View) -> Unit
) : RecyclerView.Adapter<ProductsListAdapter.ViewHolder>() {
    private val productsList = mutableListOf<Product>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)

        val view = layoutInflater.inflate(R.layout.item_product, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = productsList.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val product = productsList[position]

        viewHolder.bindData(product)

        val productImageView = viewHolder.itemView.imageViewProductPhoto

        if (isLollipopOrNewer()) {
            ViewCompat.setTransitionName(productImageView, product.id)
        }

        viewHolder.itemView.setOnClickListener {
            itemOnClickListener.invoke(product, productImageView)
        }
    }

    fun setContent(productsList: List<Product>) {
        this.productsList.clear()

        if (productsList.isNotEmpty()) {
            this.productsList.addAll(productsList)
        }

        notifyDataSetChanged()
    }

    fun clearContent() = setContent(emptyList())

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(product: Product) {
            itemView.textViewProductTitle.text = product.name
            itemView.textViewProductBrand.text = product.brand

            setPrices(product.price)
            loadImage(product.imageUrl)
        }

        private fun loadImage(imageUrl: String) {
            itemView.progressBarLoadingImage.show()
            itemView.imageViewErrorPlaceholder.hide()

            Picasso
                .get()
                .load(imageUrl)
                .into(itemView.imageViewProductPhoto, object : Callback {
                    override fun onSuccess() {
                        itemView.progressBarLoadingImage.hide()
                        val bitmap = (itemView.imageViewProductPhoto.drawable as BitmapDrawable).bitmap
                        Palette
                            .from(bitmap)
                            .addFilter { _, hsl -> hsl[2] < 0.9f }
                            .generate { palette -> applyPalette(palette) }
                    }

                    override fun onError(e: Exception?) {
                        itemView.progressBarLoadingImage.hide()
                        itemView.imageViewErrorPlaceholder.show()
                        Timber.e(e, "Failed to load image.")
                    }
                })
        }

        private fun applyPalette(palette: Palette?) {
            palette?.let {

                val cardColor = it.dominantSwatch?.rgb
                val textColor = it.dominantSwatch?.bodyTextColor
                val titleTextColor = it.dominantSwatch?.titleTextColor

                if (itemView is CardView) {
                    cardColor?.let { itemView.setCardBackgroundColor(it) }
                    textColor?.let {
                        itemView.textViewProductPrice.setTextColor(it)
                        itemView.textViewProductPriceWithDiscount.setTextColor(it)
                        itemView.textViewProductBrand.setTextColor(it)
                    }
                    titleTextColor?.let { itemView.textViewProductTitle.setTextColor(it) }

                }
            }
        }

        private fun setPrices(price: Price) {
            val priceView = itemView.textViewProductPrice
            val priceWithDiscountView = itemView.textViewProductPriceWithDiscount

            priceView.show()
            priceWithDiscountView.show()


            with(price) {
                priceWithDiscountView.text = formatPrice(current, currency)

                if (original == current) {
                    priceView.hide()
                } else {
                    val formattedPrice = formatPrice(original, currency)
                    val spannableString = SpannableString(formattedPrice)
                    spannableString.setSpan(
                        StrikethroughSpan(),
                        0,
                        formattedPrice.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    priceView.text = spannableString

                    priceWithDiscountView.setTypeface(priceWithDiscountView.typeface, Typeface.BOLD)
                }
            }
        }

    }
}