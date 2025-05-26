package dev.thinkalex.menlovending.ui.views

import android.widget.Toast
import dev.thinkalex.menlovending.ui.widgets.MenloVendingKeypad
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.PaymentIntentCallback
import com.stripe.stripeterminal.external.models.CaptureMethod
import com.stripe.stripeterminal.external.models.PaymentIntent
import com.stripe.stripeterminal.external.models.PaymentIntentParameters
import com.stripe.stripeterminal.external.models.PaymentMethodType
import com.stripe.stripeterminal.external.models.TerminalErrorCode
import com.stripe.stripeterminal.external.models.TerminalException
import dev.thinkalex.menlovending.services.item.ItemDetailsResult
import dev.thinkalex.menlovending.services.item.MockItemProvider
import dev.thinkalex.menlovending.services.item.StripeItemProvider
import dev.thinkalex.menlovending.services.stripe.server.StripeServer
import dev.thinkalex.menlovending.services.vending.MockVendingProvider
import dev.thinkalex.menlovending.services.vending.VendingResult
import dev.thinkalex.menlovending.ui.widgets.MenloVendingError
import dev.thinkalex.menlovending.ui.widgets.MenloVendingPurchase
import dev.thinkalex.menlovending.ui.widgets.MenloVendingStatusScaffold
import dev.thinkalex.menlovending.ui.widgets.MenloVendingSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val vendingProvider = MockVendingProvider()
    val itemProvider = StripeItemProvider()

    var currentScreen by mutableStateOf("keypad")

    var currentStatusMessage by mutableStateOf("")

    var currentErrorMessage by mutableStateOf("")
    var currentErrorDescription by mutableStateOf("")

    var selectedProduct by mutableStateOf<ItemDetailsResult?>(null)

    var transactionCancellable by mutableStateOf(false)

    var currentPaymentIntent by mutableStateOf<PaymentIntent?>(null)

    fun dispenseCallback(result: VendingResult) {
        when (result) {
            VendingResult.SUCCESS -> {
                currentStatusMessage = "Enjoy your ${selectedProduct?.name}!"
                currentScreen = "success"
                transactionCancellable = false
                capturePayment(currentPaymentIntent?.id ?: "")
            }
            VendingResult.OUT_OF_STOCK -> {
                currentErrorMessage = "Out of Stock"
                currentErrorDescription = "The selected item is not available.\n\nYou will not be charged."
                currentScreen = "error"
                transactionCancellable = false
                cancelPayment()
            }
            VendingResult.FAILURE -> {
                currentErrorMessage = "Vending Failed"
                currentErrorDescription = "An error occurred while dispensing the item.\n\nYou will not be charged."
                currentScreen = "error"
                transactionCancellable = false
                cancelPayment()
            }
        }
    }

    val confirmPaymentIntentCallback by lazy {
        object : PaymentIntentCallback {
            override fun onSuccess(paymentIntent: PaymentIntent) {
                // Update status to dispensing
                currentStatusMessage = "Dispensing ${selectedProduct?.name}..."

                // Start dispensing product
                vendingProvider.dispense(selectedProduct!!.itemId) { result ->
                    dispenseCallback(result)
                }
            }

            override fun onFailure(e: TerminalException) {
                // Handle failure
                currentErrorMessage = "An error occurred while confirming payment"
                currentErrorDescription = e.message ?: "Unknown error"

                // Navigate to error screen
                currentScreen = "error"
            }
        }
    }

    val collectPaymentMethodCallback by lazy {
        object : PaymentIntentCallback {
            override fun onSuccess(paymentIntent: PaymentIntent) {
                // Update status
                currentStatusMessage = "Processing payment..."
                transactionCancellable = false

                Terminal.getInstance().confirmPaymentIntent(paymentIntent, confirmPaymentIntentCallback)
            }

            override fun onFailure(e: TerminalException) {
                // Check if error is user-cancelled
                if (e.errorCode != TerminalErrorCode.CANCELED) {
                    // Handle failure
                    currentErrorMessage = "A payment error occurred"
                    currentErrorDescription = e.message ?: "Unknown error"

                    // Navigate to error screen
                    currentScreen = "error"
                }
            }
        }
    }

    val createPaymentIntentCallback by lazy {
        object : PaymentIntentCallback {
            override fun onSuccess(paymentIntent: PaymentIntent) {
                // Update status
                currentStatusMessage = "Tap card on reader to pay!"

                // Save payment intent
                currentPaymentIntent = paymentIntent

                Terminal.getInstance()
                    .collectPaymentMethod(paymentIntent, collectPaymentMethodCallback)
            }

            override fun onFailure(e: TerminalException) {
                // Handle failure
                currentErrorMessage = "An unexpected error occurred"
                currentErrorDescription = e.message ?: "Unknown error"

                // Navigate to error screen
                currentScreen = "error"
            }
        }
    }

    val cancelPaymentIntentCallback by lazy {
        object : PaymentIntentCallback {
            override fun onSuccess(paymentIntent: PaymentIntent) {
                println("Payment intent cancelled successfully")
            }

            override fun onFailure(e: TerminalException) {
                System.err.println("Failed to cancel payment intent: ${e.message}")
            }
        }
    }

    fun cancelPayment() {
        if (currentPaymentIntent != null) {
            Terminal.getInstance().cancelPaymentIntent(
                currentPaymentIntent!!,
                cancelPaymentIntentCallback
            )
        }
    }

    fun onCancel() {
        // Reset state
        selectedProduct = null
        currentStatusMessage = ""
        currentErrorMessage = ""
        currentErrorDescription = ""
        transactionCancellable = true

        // Cancel payment intent if it exists
        cancelPayment()

        // Clear payment intent
        currentPaymentIntent = null

        // Navigate back to keypad
        currentScreen = "keypad"
    }

    fun capturePayment(paymentIntentID: String) {
        GlobalScope.launch {
            StripeServer.capturePaymentIntent(paymentIntentID);
        }
    }

    fun initiatePurchase(itemNumber: String) {
        // Validate item number (item provider)
        val itemProviderResult = itemProvider.getItemDetails(itemNumber) { product ->
            if (product == null) {
                // Handle invalid item
                currentErrorMessage = "Invalid Item"
                currentErrorDescription = "The selected item does not exist."
                currentScreen = "error"
            } else {
                // Cancel previous transaction if any
                onCancel()

                // Update state
                selectedProduct = product
                currentStatusMessage = "Please wait..."

                // Update screen
                currentScreen = "purchase"

                // Create payment intent
                val paymentIntentParams =
                    PaymentIntentParameters.Builder(listOf(PaymentMethodType.CARD_PRESENT))
                        .setCaptureMethod(CaptureMethod.Manual)
                        .setAmount((product.price * 100).toLong()) // Convert to cents
                        .setCurrency("usd")
                        .build()
                Terminal.getInstance().createPaymentIntent(paymentIntentParams, createPaymentIntentCallback);
            }
        }
    }
}

@Composable
fun MainView(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current

    fun onConfirm(item: String) {
        viewModel.initiatePurchase(item)
    }

    fun onCancel() {
        viewModel.onCancel();
    }

    MenloVendingStatusScaffold {
        when (viewModel.currentScreen) {
            "keypad" -> {
                MenloVendingKeypad(
                    onConfirm = ::onConfirm
                )
            }
            "purchase" -> {
                MenloVendingPurchase(
                    product = viewModel.selectedProduct,
                    status = viewModel.currentStatusMessage,
                    onCancel = ::onCancel,
                    cancellable = viewModel.transactionCancellable,
                )
            }
            "success" -> {
                MenloVendingSuccess(
                    onCancel = ::onCancel,
                )
            }
            "error" -> {
                MenloVendingError(
                    errorMessage = viewModel.currentErrorMessage,
                    errorDescription = viewModel.currentErrorDescription,
                    onCancel = ::onCancel,
                )
            }
        }
    }
}