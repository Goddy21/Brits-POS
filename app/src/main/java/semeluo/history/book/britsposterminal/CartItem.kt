package semeluo.history.book.britsposterminal

data class CartItem(
    val product: Product,
    var qty: Int = 1
) {
    val lineTotal: Double
        get() = product.price * qty
}
