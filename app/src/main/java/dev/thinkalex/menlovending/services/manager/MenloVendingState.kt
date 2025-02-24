package dev.thinkalex.menlovending.services.manager

data class MenloVendingState (
    // Status Overview
    val status: MenloVendingStatus,
    val statusMessage: String,
    val statusDetails: String,
) {
    enum class MenloVendingStatus {
        INITIALIZING,
        READY,
        WARNING,
        ERROR,
        FATAL
    }
}