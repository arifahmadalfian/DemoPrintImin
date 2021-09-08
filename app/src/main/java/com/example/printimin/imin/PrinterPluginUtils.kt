package com.example.printimin.imin

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.example.printimin.IMyAidlInterface

class PrinterPluginUtils(mContent: Activity) {

    private var iminPrintService: IMyAidlInterface? = null
    private val TAG = "PrinterPluginUtils==lsy==="

    init {
        val connection: ServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                iminPrintService = IMyAidlInterface.Stub.asInterface(service)
                Log.i(TAG, "==onServiceConnected=")
            }

            override fun onServiceDisconnected(name: ComponentName) {
                Log.i(TAG, "==onServiceDisconnected=")
                iminPrintService = null
            }

            override fun onBindingDied(name: ComponentName) {
                Log.i(TAG, "==onBindingDied=")
            }
        }

        val intent = Intent()
        //intent.setPackage("com.imin.printerPlugin");
        //intent.setAction("com.imin.printerPlugin.AidlPrintService");
        val component =
            ComponentName("com.imin.printerPlugin", "com.imin.printerPlugin.AidlPrintService")
        intent.component = component
        //mContent.startService(intent);
        mContent.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    @Throws(RemoteException::class)
    fun print(data: ByteArray?, contrast: Double) {
        Log.i(TAG, "iminPrintService$iminPrintService")
        if (iminPrintService != null) {
            //iminPrintService!!.printImage(data, contrast)
        }
    }

}
