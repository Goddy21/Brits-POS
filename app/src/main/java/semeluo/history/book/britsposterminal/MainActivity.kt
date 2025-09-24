package semeluo.history.book.britsposterminal

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import semeluo.history.book.britsposterminal.adapters.CartAdapter
import semeluo.history.book.britsposterminal.adapters.ProductListAdapter
import semeluo.history.book.britsposterminal.databinding.ActivityMainBinding
import semeluo.history.book.britsposterminal.util.PrintHelper
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val cartVM: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter

    private val sampleProducts = listOf(
        Product(1, "Petrol", 10.0),
        Product(2, "Diesel", 15.0),
        Product(3, "E-Oil", 20.0),
        Product(4, "Gb-Oil", 1.25),
        Product(5, "ATF", 1.25),
        Product(6, "Tea", 2.5)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAndRequestBluetoothPermission()

        // --- Products Grid ---
        binding.rvProducts.layoutManager = GridLayoutManager(this, 2)
        binding.rvProducts.adapter = ProductListAdapter(sampleProducts) { product ->
            cartVM.addProduct(product)
        }

        // --- Cart Recycler ---
        cartAdapter = CartAdapter(
            onRemove = { id -> cartVM.remove(id) },
            onQtyChange = { id, q -> cartVM.updateQty(id, q) }
        )
        binding.rvCart.layoutManager = LinearLayoutManager(this)
        binding.rvCart.adapter = cartAdapter

        // --- Observe LiveData ---
        cartVM.items.observe(this) { list ->
            cartAdapter.submitList(list)
            val total = cartVM.total()
            binding.tvTotal.text = "Total: $${"%.2f".format(total)}"
            binding.tvQuickTotal.text = "Total: $${"%.2f".format(total)}"
        }

        // --- Clear Button ---
        binding.btnClear.setOnClickListener {
            cartVM.clear()
            Toast.makeText(this, "Cart cleared", Toast.LENGTH_SHORT).show()
        }

        // --- Print Button ---
        binding.btnPrint.setOnClickListener {
            val receipt = buildReceiptString()

            // Launch coroutine on Main thread
            lifecycleScope.launch {
                try {
                    // Call asynchronous P10 print function
                    PrintHelper.printRawTextAsync(this@MainActivity, receipt)
                    Toast.makeText(this@MainActivity, "Printing...", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Print failed: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        }

        // --- Mpesa Button ---
        binding.btnMpesa.setOnClickListener {
            Toast.makeText(this, "M-Pesa flow started (server needed)", Toast.LENGTH_SHORT).show()
        }

        // --- Card Button ---
        binding.btnCard.setOnClickListener {
            Toast.makeText(this, "Card flow (open drop-in / hosted)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildReceiptString(): String {
        val sb = StringBuilder()
        sb.append("Merchant: Mkononi Ltd\n")
        sb.append("Receipt\n")
        sb.append("-------------------------------\n")
        cartVM.items.value?.forEach {
            sb.append("${it.product.name} x${it.qty} ${"%.2f".format(it.lineTotal)}\n")
        }
        sb.append("-------------------------------\n")
        sb.append("TOTAL: ${"%.2f".format(cartVM.total())}\n")
        sb.append("Thank you!\n")
        return sb.toString()
    }

    // --- Bluetooth Permission Handling ---
    private val requestBluetoothPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private fun checkAndRequestBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestBluetoothPermission.launch(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
    }
}
