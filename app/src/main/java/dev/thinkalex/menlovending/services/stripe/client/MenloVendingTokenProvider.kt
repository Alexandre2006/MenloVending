package dev.thinkalex.menlovending.services.stripe.client

import com.stripe.stripeterminal.external.callable.ConnectionTokenCallback
import com.stripe.stripeterminal.external.callable.ConnectionTokenProvider
import com.stripe.stripeterminal.external.models.ConnectionTokenException
import dev.thinkalex.menlovending.services.stripe.server.StripeServer

class MenloVendingTokenProvider(
) : ConnectionTokenProvider {

    override fun fetchConnectionToken(callback: ConnectionTokenCallback) {
        try {
            callback.onSuccess(StripeServer.getConnectionToken())
        } catch (e: Exception) {
            callback.onFailure(
                ConnectionTokenException("Failed to fetch connection token", e)
            )
        }
    }
}