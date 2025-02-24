package dev.thinkalex.menlovending.services.stripe.client

import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.Cancelable
import com.stripe.stripeterminal.external.callable.MobileReaderListener
import com.stripe.stripeterminal.external.models.DisconnectReason
import com.stripe.stripeterminal.external.models.Reader
import com.stripe.stripeterminal.external.models.ReaderSoftwareUpdate
import com.stripe.stripeterminal.external.models.TerminalException

data class ReaderUpdate(
    val isUpdating: Boolean,
    val progress: Float
)

class MenloVendingReaderListener (
    private val onReconnectStarted: () -> Unit,
    private val onReconnectSucceeded: () -> Unit,
    private val onReconnectFailed: () -> Unit,
    private val onReaderUpdate: (ReaderUpdate) -> Unit
) : MobileReaderListener {
    override fun onReaderReconnectStarted(reader: Reader, cancelReconnect: Cancelable, reason: DisconnectReason) {
        onReconnectStarted()
    }

    override fun onReaderReconnectSucceeded(reader: Reader) {
        onReconnectSucceeded()
    }

    override fun onReaderReconnectFailed(reader: Reader) {
        onReconnectFailed()
    }

    override fun onStartInstallingUpdate(update: ReaderSoftwareUpdate, cancelable: Cancelable?) {
        onReaderUpdate(ReaderUpdate(true, 0f))
    }

    override fun onReportReaderSoftwareUpdateProgress(progress: Float) {
        onReaderUpdate(ReaderUpdate(true, progress))
    }

    override fun onFinishInstallingUpdate(update: ReaderSoftwareUpdate?, e: TerminalException?) {
        onReaderUpdate(ReaderUpdate(false, 100f))
    }

    override fun onReportAvailableUpdate(update: ReaderSoftwareUpdate) {
        Terminal.getInstance().installAvailableUpdate();
    }
}