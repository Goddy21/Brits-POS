package semeluo.history.book.britsposterminal.util

import android.content.Context
import android.os.PowerManager
import android.util.Log
import com.xcheng.printerservice.PrinterManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PrintHelper {
    private const val TAG = "PrintHelper"

    // Flag to keep track of printer state
    private var isPrinterOpened = false

    // Safe synchronous print
    fun printRawText(context: Context, text: String) {
        // Acquiring a wake lock to keep the device awake during printing
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Printer::WakeLock")
        wakeLock.acquire(10 * 60 * 1000L) // Keep the device awake for 10 minutes

        // Always get the instance on the main thread
        val printer = PrinterManager.getInstance(context)

        // Check if the printer is already open
        if (!isPrinterOpened) {
            Thread {
                try {
                    printer.printerOpen()
                    isPrinterOpened = true  // Mark the printer as open
                    printer.printText(context, text)
                    printer.printerCutPaper(1)
                    printer.printerClose()
                    isPrinterOpened = false // Mark the printer as closed after use
                    Log.i(TAG, "Print succeeded")
                } catch (e: Exception) {
                    Log.e(TAG, "Print failed", e)
                } finally {
                    wakeLock.release() // Release the wake lock
                }
            }.start()
        } else {
            Log.w(TAG, "Printer is already open.")
            wakeLock.release() // Release wake lock if printer is already open
        }
    }

    // Safe asynchronous print using coroutines
    suspend fun printRawTextAsync(context: Context, text: String) {
        // Acquiring a wake lock to keep the device awake during printing
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Printer::WakeLock")
        wakeLock.acquire(10 * 60 * 1000L) // Keep the device awake for 10 minutes

        // Get PrinterManager on main thread
        val printer = withContext(Dispatchers.Main) {
            PrinterManager.getInstance(context)
        }

        // Check if the printer is already open
        if (!isPrinterOpened) {
            withContext(Dispatchers.IO) {
                try {
                    printer.printerOpen()
                    isPrinterOpened = true  // Mark the printer as open
                    printer.printText(context, text)
                    printer.printerCutPaper(1)
                    printer.printerClose()
                    isPrinterOpened = false // Mark the printer as closed after use
                    Log.i(TAG, "Print succeeded")
                } catch (e: Exception) {
                    Log.e(TAG, "Print failed", e)
                } finally {
                    wakeLock.release() // Release the wake lock
                }
            }
        } else {
            Log.w(TAG, "Printer is already open.")
            wakeLock.release() // Release wake lock if printer is already open
        }
    }
}
