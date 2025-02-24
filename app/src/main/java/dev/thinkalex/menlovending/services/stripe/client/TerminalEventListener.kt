package dev.thinkalex.menlovending.services.stripe.client

import com.stripe.stripeterminal.external.callable.TerminalListener
import com.stripe.stripeterminal.external.models.ConnectionStatus
import com.stripe.stripeterminal.external.models.PaymentStatus

class TerminalEventListener(
    private val onConnectionStatusChangeCallback: (ConnectionStatus) -> Unit,
    private val onPaymentStatusChangeCallback: (PaymentStatus) -> Unit
) : TerminalListener {
    override fun onConnectionStatusChange(status: ConnectionStatus) {
        println("Connection status changed to: $status")
        onConnectionStatusChangeCallback(status)
    }

    override fun onPaymentStatusChange(status: PaymentStatus) {
        println("Payment status changed to: $status")
        onPaymentStatusChangeCallback(status)
    }
}