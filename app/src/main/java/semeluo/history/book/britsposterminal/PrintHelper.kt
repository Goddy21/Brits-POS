package semeluo.history.book.britsposterminal.util

import android.content.*
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.xcheng.printerservice.IPrinterService
import com.xcheng.printerservice.IPrinterCallback

object PrintHelper {
    private const val TAG = "PrintHelper"

    private var printerService: IPrinterService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            printerService = IPrinterService.Stub.asInterface(service)
            isBound = true
            Log.i(TAG, "PrinterService connected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            printerService = null
            isBound = false
            Log.w(TAG, "PrinterService disconnected")
        }
    }

    fun bindService(context: Context) {
        val intent = Intent("com.xcheng.printerservice.IPrinterService")
        intent.setPackage("com.xcheng.printerservice")
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService(context: Context) {
        if (isBound) {
            context.unbindService(connection)
            isBound = false
        }
    }

    fun printText(text: String) {
        if (!isBound || printerService == null) {
            Log.e(TAG, "PrinterService not bound")
            return
        }
        try {
            val callback = object : IPrinterCallback.Stub() {
                override fun onStart() {
                    Log.i(TAG, "Printing started...")
                }

                override fun onComplete() {
                    Log.i(TAG, "Print completed successfully")
                }

                override fun onException(code: Int, msg: String?) {
                    Log.e(TAG, "Print failed: code=$code, msg=$msg")
                }

                override fun onLength(p0: Long, p1: Long) {
                    Log.i(TAG, "Print progress: $p0 / $p1 bytes")
                }
            }

            printerService?.printText(text + "\n", callback)

        } catch (e: RemoteException) {
            Log.e(TAG, "RemoteException while printing", e)
        }
    }
}
