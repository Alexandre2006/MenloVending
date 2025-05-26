package dev.thinkalex.menlovending.services.item

data class ItemDetailsResult(
    var itemId: String,
    val name: String,
    val price: Double,
)

abstract class ItemProvider {
    abstract fun getItemDetails(itemId: String, callback: (ItemDetailsResult?) -> Unit)
}