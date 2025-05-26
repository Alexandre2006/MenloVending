package dev.thinkalex.menlovending.services.stripe.server

import com.stripe.Stripe
import com.stripe.exception.StripeException
import com.stripe.model.PaymentIntent
import com.stripe.model.Product
import com.stripe.model.terminal.ConnectionToken
import com.stripe.net.RequestOptions
import com.stripe.param.PaymentIntentCreateParams
import com.stripe.param.ProductRetrieveParams
import com.stripe.param.terminal.ConnectionTokenCreateParams
import com.stripe.service.ProductService
import dev.thinkalex.menlovending.BuildConfig
import dev.thinkalex.menlovending.services.item.ItemDetailsResult

object StripeServer {
    private const val STRIPE_API_KEY = BuildConfig.STRIPE_KEY

    init {
        Stripe.apiKey = STRIPE_API_KEY
    }

    // Connection Token Endpoint
    fun getConnectionToken(): String {
        val params = ConnectionTokenCreateParams.builder()
            .build()

        val connectionToken = ConnectionToken.create(params)
        return connectionToken.secret
    }

    fun capturePaymentIntent(paymentIntentID: String) {
        try {
            val intent: PaymentIntent = PaymentIntent.retrieve(paymentIntentID)
            intent.capture()
        } catch (e: StripeException) {
            println("Error capturing payment intent: ${e.message}")
        }
    }

    fun getProduct(productID: String): ItemDetailsResult? {
        // Retrieve product details from Stripe
        try {
            val params: ProductRetrieveParams = ProductRetrieveParams.builder()
                .addExpand("default_price")
                .build()
            val productDetails: Product = Product.retrieve(
                productID,
                params,
                RequestOptions.builder().setApiKey(STRIPE_API_KEY).build()
            )

            return ItemDetailsResult(
                itemId = productDetails.id,
                name = productDetails.name ?: "Unknown Product",
                price = productDetails.defaultPriceObject.unitAmount.toDouble() / 100.0 // Convert cents to dollars
            )

        } catch (e: StripeException) {
            println("Error retrieving product: ${e.message}")
            return null
        }

    }
}