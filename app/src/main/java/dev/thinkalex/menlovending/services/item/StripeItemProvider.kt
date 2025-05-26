package dev.thinkalex.menlovending.services.item

import dev.thinkalex.menlovending.services.stripe.server.StripeServer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class StripeItemProvider : ItemProvider() {
    private val productIDs = mapOf(
        "00" to "prod_SNeGiVVKvewlwS",
        "01" to "prod_SNeGiVVKvewlwS",
        "02" to "prod_SNeGiVVKvewlwS"
    )

    override fun getItemDetails(itemId: String, callback: (ItemDetailsResult?) -> Unit) {
        GlobalScope.launch {
            if (productIDs.contains(itemId)) {
                val result: ItemDetailsResult? = StripeServer.getProduct(productIDs[itemId]!!)
                if (result != null) {
                    result.itemId = itemId
                    callback(result)
                } else {
                    callback(null)
                }
            } else {
                callback(null)
            }
        }
    }
}