package semeluo.history.book.britsposterminal.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import semeluo.history.book.britsposterminal.CartItem
import semeluo.history.book.britsposterminal.R

class CartAdapter(
    private val onRemove: (Long) -> Unit,
    private val onQtyChange: (Long, Int) -> Unit
) : ListAdapter<CartItem, CartAdapter.VH>(Diff) {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.cartProductName)
        val qty: TextView = view.findViewById(R.id.cartQty)
        val total: TextView = view.findViewById(R.id.cartLineTotal)
        val btnRemove: ImageButton = view.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val cartItem = getItem(position)

        holder.name.text = cartItem.product.name
        holder.qty.text = "x${cartItem.qty}"
        holder.total.text = "$${"%.2f".format(cartItem.lineTotal)}"

        holder.btnRemove.setOnClickListener {
            onRemove(cartItem.product.id)
        }

        // long press to change qty (example)
        holder.itemView.setOnLongClickListener {
            val newQty = cartItem.qty - 1
            if (newQty > 0) {
                onQtyChange(cartItem.product.id, newQty)
            } else {
                onRemove(cartItem.product.id)
            }
            true
        }
    }

    companion object {
        private val Diff = object : DiffUtil.ItemCallback<CartItem>() {
            override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
                return oldItem.product.id == newItem.product.id
            }

            override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
