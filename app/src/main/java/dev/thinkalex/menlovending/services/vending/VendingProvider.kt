package dev.thinkalex.menlovending.services.vending

enum class VendingResult {
    SUCCESS,
    FAILURE,
    OUT_OF_STOCK
}

abstract class VendingProvider {
    abstract fun dispense(itemId: String, callback: (VendingResult) -> Unit)
}