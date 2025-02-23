package dev.thinkalex.menlovending.services.stripe.server

import com.stripe.Stripe
import com.stripe.model.PaymentIntent
import com.stripe.model.terminal.ConnectionToken
import com.stripe.param.PaymentIntentCreateParams
import com.stripe.param.terminal.ConnectionTokenCreateParams

object StripeServer {
    private const val STRIPE_API_KEY = "STRIPE_KEY"

    init {
        Stripe.apiKey = STRIPE_API_KEY
    }

    // Connection Token Endpoint
    val connectionToken: String
        // Endpoints
        get() {
            val params = ConnectionTokenCreateParams.builder()
                .build()

            val connectionToken = ConnectionToken.create(params)
            return connectionToken.secret
        }

    // Create Payment Intent Endpoint
    fun createPaymentIntent(amount: Long?): String {
        val createParams = PaymentIntentCreateParams.builder()
            .setCurrency("usd")
            .setAmount(amount)
            .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
            .build()

        val intent = PaymentIntent.create(createParams)
        return intent.id
    }

    // Capture Payment Intent Endpoint
    fun capturePaymentIntent(paymentIntentId: String): String {
        val intent = PaymentIntent.retrieve(paymentIntentId)
        val capturedIntent = intent.capture()
        return capturedIntent.id
    }

}