package com.example.printimin

import android.os.Bundle
import android.os.RemoteException
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.printimin.databinding.ActivityMainBinding
import com.imin.printerlib.IminPrintUtils

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    private lateinit var iminInit : IminPrintUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        iminInit = IminPrintUtils.getInstance(this@MainActivity)

        binding?.btnPrint?.setOnClickListener {

            if(binding?.etContent?.text?.isEmpty() == true) {
                binding?.etContent?.error = "Input Content"
            } else {
                val content = binding?.etContent?.text.toString()
                var status = ""
                iminInit.initPrinter(IminPrintUtils.PrintConnectType.SPI)
                iminInit.getPrinterStatus(
                    IminPrintUtils.PrintConnectType.SPI
                ) {
                    when (it) {
                        -1 -> status = "The printer is not connected or powered on"
                        0 -> {
                            status = "The printer is normal"
                            getPrint(content)
                        }
                        1 -> status = "The printer is not connected or powered on"
                        3 -> status = "Print head open"
                        7 -> status = "No Paper Feed"
                        8 -> status = "Paper Running Out"
                        99 -> status = "Other errors"
                        else -> Unit
                    }
                    Toast.makeText(
                        this@MainActivity,
                        status,
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }
        }
    }

    private fun getPrint(content: String) {
        with(iminInit){
            try {
                printText(content)
                printAndFeedPaper(100);
                printBarCode(4, content, 1)
                printAndFeedPaper(100);
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        iminInit.release()
        super.onDestroy()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
    }
}