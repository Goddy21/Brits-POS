package semeluo.history.book.britsposterminal

data class Product(
    val id: Long,
    val name: String,
    val price: Double,
    val sku: String? = null
)
