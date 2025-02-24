package dev.thinkalex.menlovending.services.manager

import android.annotation.SuppressLint
import android.content.Context
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.Callback
import com.stripe.stripeterminal.external.callable.Cancelable
import com.stripe.stripeterminal.external.callable.DiscoveryListener
import com.stripe.stripeterminal.external.callable.ReaderCallback
import com.stripe.stripeterminal.external.models.ConnectionConfiguration
import com.stripe.stripeterminal.external.models.ConnectionStatus
import com.stripe.stripeterminal.external.models.DiscoveryConfiguration
import com.stripe.stripeterminal.external.models.PaymentStatus
import com.stripe.stripeterminal.external.models.Reader
import com.stripe.stripeterminal.external.models.TerminalException
import com.stripe.stripeterminal.log.LogLevel
import dev.thinkalex.menlovending.BuildConfig
import dev.thinkalex.menlovending.services.stripe.client.MenloVendingReaderListener
import dev.thinkalex.menlovending.services.stripe.client.MenloVendingTokenProvider
import dev.thinkalex.menlovending.services.stripe.client.ReaderUpdate
import dev.thinkalex.menlovending.services.stripe.client.TerminalEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.round

object MenloVendingManager : DiscoveryListener {
    // State (Global)
    private val _state =
        MutableStateFlow(
            MenloVendingState(
                MenloVendingState.MenloVendingStatus.INITIALIZING,
                "Initializing...",
                ""
            )
        )

    val status: StateFlow<MenloVendingState> = _state

    // State (Stripe Terminal Connection Status & Payment Status)
    private var connectionStatus: ConnectionStatus = ConnectionStatus.NOT_CONNECTED
    private var paymentStatus: PaymentStatus = PaymentStatus.NOT_READY

    // Background Thread
    private var backgroundJob: Job? = null

    fun initialize(context: Context? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            // Kill any existing background jobs
            backgroundJob?.cancel()

            // Set default status
            _state.value = _state.value.copy(
                status = MenloVendingState.MenloVendingStatus.INITIALIZING,
                statusMessage = "Initializing...",
                statusDetails = ""
            )
            connectionStatus = ConnectionStatus.NOT_CONNECTED
            paymentStatus = PaymentStatus.NOT_READY

            // Initialize Stripe
            if (context != null) {
                try {
                    val listener: TerminalEventListener = TerminalEventListener(
                        this@MenloVendingManager::onConnectionStatusChange,
                        this@MenloVendingManager::onPaymentStatusChange
                    )
                    val tokenProvider = MenloVendingTokenProvider()

                    if (!Terminal.isInitialized()) {
                        Terminal.initTerminal(context, LogLevel.VERBOSE, tokenProvider, listener)
                    }
                } catch (e: Exception) {
                    println("Failed to initialize Stripe SDK: ${e.message}")
                    _state.value = _state.value.copy(
                        status = MenloVendingState.MenloVendingStatus.FATAL,
                        statusMessage = "Failed to initialize Stripe SDK",
                        statusDetails = e.message ?: "Unknown Error"
                    )
                }
            }

            // Search for Stripe Terminal Devices
            discoverReaders()
        }
    }

    // Stripe Terminal Event Listeners
    fun onConnectionStatusChange(status: ConnectionStatus) {
        println("Connection status changed to: $status")
        connectionStatus = status
        updateStatus()
    }

    fun onPaymentStatusChange(status: PaymentStatus) {
        println("Payment status changed to: $status")
        paymentStatus = status
        updateStatus()
    }

    // Stripe Terminal Discovery / Connection
    var discoverCancelable: Cancelable? = null

    @SuppressLint("MissingPermission")
    private fun discoverReaders() {
        // Discovery Configuration
        val config = DiscoveryConfiguration.UsbDiscoveryConfiguration(
            timeout = 10,
            isSimulated = true
        )

        Terminal.getInstance().discoverReaders(
            config,
            this,
            object : Callback {
                override fun onSuccess() {
                    // Ignore, expected result
                }

                override fun onFailure(e: TerminalException) {
                    println("WEEEE WOOOOO")
                    fatalStatus("Failed to discover readers", e.message ?: "Unknown Error")
                }
            }
        )

    }

    override fun onUpdateDiscoveredReaders(readers: List<Reader>) {
        // Connection configuration
        val connectionConfig = ConnectionConfiguration.UsbConnectionConfiguration(
            BuildConfig.STRIPE_LOCATION,
            true,
            MenloVendingReaderListener(
                this::onReconnectStarted,
                this::onReconnectSucceeded,
                this::onReconnectFailed,
                this::onSoftwareUpdate
            )
        )

        // Connect to first reader
        if (readers.isNotEmpty()) {
            Terminal.getInstance().connectReader(
                readers[0],
                config = connectionConfig,
                connectionCallback = object : ReaderCallback {
                    override fun onSuccess(reader: Reader) {
                        println("Connected to reader")
                    }

                    override fun onFailure(e: TerminalException) {
                        fatalStatus("Failed to connect to reader", e.message ?: "Unknown Error")
                    }
                }
            )
        }
    }

    private fun onReconnectStarted() {
        _state.value = _state.value.copy(
            status = MenloVendingState.MenloVendingStatus.INITIALIZING,
            statusMessage = "Reconnecting to Stripe Terminal...",
            statusDetails = ""
        )
    }

    private fun onReconnectSucceeded() {
        _state.value = _state.value.copy(
            status = MenloVendingState.MenloVendingStatus.READY,
            statusMessage = "Reconnected to Stripe Terminal",
            statusDetails = ""
        )
    }

    private fun onReconnectFailed() {
        fatalStatus("Failed to reconnect to Stripe Terminal", "Unknown Error")
    }

    private fun onSoftwareUpdate(updateProgress: ReaderUpdate) {
        if (updateProgress.isUpdating) {
            _state.value = _state.value.copy(
                status = MenloVendingState.MenloVendingStatus.INITIALIZING,
                statusMessage = "Updating Stripe Terminal (${round(updateProgress.progress * 100)}%)",
                statusDetails = "Progress: ${round(updateProgress.progress * 100)}%"
            )
        } else {
            _state.value = _state.value.copy(
                status = MenloVendingState.MenloVendingStatus.READY,
                statusMessage = "Updated Stripe Terminal",
                statusDetails = ""
            )
        }
    }


    // Global Status Update
    private fun updateStatus() {
        // Check connection status (switch)
        when (connectionStatus) {
            ConnectionStatus.NOT_CONNECTED -> {
                _state.value = _state.value.copy(
                    status = MenloVendingState.MenloVendingStatus.ERROR,
                    statusMessage = "Disconnected from Stripe Terminal",
                    statusDetails = ""
                )
            }

            ConnectionStatus.CONNECTING -> {
                _state.value = _state.value.copy(
                    status = MenloVendingState.MenloVendingStatus.INITIALIZING,
                    statusMessage = "Connecting to Stripe Terminal...",
                    statusDetails = ""
                )
            }

            ConnectionStatus.CONNECTED -> {
                _state.value = _state.value.copy(
                    status = MenloVendingState.MenloVendingStatus.READY,
                    statusMessage = "Connected to Stripe Terminal",
                    statusDetails = ""
                )
            }

            ConnectionStatus.DISCOVERING -> {
                _state.value = _state.value.copy(
                    status = MenloVendingState.MenloVendingStatus.INITIALIZING,
                    statusMessage = "Searching for Stripe Terminal...",
                    statusDetails = ""
                )
            }
        }
    }

    private fun fatalStatus(message: String, details: String) {
        _state.value = _state.value.copy(
            status = MenloVendingState.MenloVendingStatus.FATAL,
            statusMessage = message,
            statusDetails = details
        )

        // Restart (15s delay)
        backgroundJob = CoroutineScope(Dispatchers.IO).launch {
            delay(15000)
            initialize()
        }
    }
}