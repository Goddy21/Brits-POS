package semeluo.history.book.britsposterminal.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import androidx.recyclerview.widget.RecyclerView
import semeluo.history.book.britsposterminal.Product
import semeluo.history.book.britsposterminal.R

class ProductListAdapter(
    private val products: List<Product>,
    private val onAdd: (Product) -> Unit
) : RecyclerView.Adapter<ProductListAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.productName)
        val price: TextView = view.findViewById(R.id.productPrice)
        val addBtn: MaterialButton = view.findViewById(R.id.addButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = products[position]
        holder.name.text = p.name
        holder.price.text = "$${"%.2f".format(p.price)}"
        holder.addBtn.setOnClickListener { onAdd(p) }
    }

    override fun getItemCount() = products.size
}
