package semeluo.history.book.britsposterminal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CartViewModel : ViewModel() {

    private val _items = MutableLiveData<List<CartItem>>(emptyList())
    val items: LiveData<List<CartItem>> get() = _items

    // Renamed to match MainActivity call
    fun addProduct(product: Product) {
        val current = _items.value?.toMutableList() ?: mutableListOf()
        val existing = current.find { it.product.id == product.id }

        if (existing != null) {
            val updated = existing.copy(qty = existing.qty + 1)
            current[current.indexOf(existing)] = updated
        } else {
            current.add(CartItem(product, 1))
        }

        _items.value = current
    }

    fun remove(productId: Long) {
        val current = _items.value?.toMutableList() ?: return
        val updated = current.filter { it.product.id != productId }
        _items.value = updated
    }

    fun updateQty(productId: Long, qty: Int) {
        if (qty <= 0) {
            remove(productId)
            return
        }

        val current = _items.value?.toMutableList() ?: return
        val index = current.indexOfFirst { it.product.id == productId }

        if (index != -1) {
            val item = current[index]
            current[index] = item.copy(qty = qty)
            _items.value = current
        }
    }

    fun clear() {
        _items.value = emptyList()
    }

    fun total(): Double {
        return _items.value?.sumOf { it.lineTotal } ?: 0.0
    }
}
