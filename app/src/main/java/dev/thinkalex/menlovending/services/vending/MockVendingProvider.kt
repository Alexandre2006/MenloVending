package dev.thinkalex.menlovending.services.vending

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MockVendingProvider : VendingProvider() {
    override fun dispense(itemId: String, callback: (VendingResult) -> Unit) {
        GlobalScope.launch {
            if (itemId == "00") {
                delay(10000)
                callback(VendingResult.OUT_OF_STOCK)
            } else if (itemId == "01") {
                delay(5000)
                callback(VendingResult.SUCCESS)
            } else {
                callback(VendingResult.FAILURE)
            }
        }

    }
}