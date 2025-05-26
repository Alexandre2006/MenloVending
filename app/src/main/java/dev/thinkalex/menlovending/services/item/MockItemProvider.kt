package dev.thinkalex.menlovending.services.item

import dev.thinkalex.menlovending.services.vending.VendingResult

class MockItemProvider : ItemProvider() {
    private val items = mapOf(
        "00" to ItemDetailsResult("00", "Out-Of-Stock Demo Item", 1.50),
        "01" to ItemDetailsResult("01", "In-Stock Demo-Item", 1.00),
        "02" to ItemDetailsResult("02", "Unknown Failure Demo Item", 2.00),
    )

    override fun getItemDetails(itemId: String, callback: (ItemDetailsResult?) -> Unit) {
        if (items.contains(itemId)) {
            callback(items[itemId])
        } else {
            callback(null)
        }
    }
}