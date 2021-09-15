package com.example.printimin

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.RemoteException
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.printimin.databinding.ActivityMainBinding
import com.example.printimin.imin.CuteR
import com.example.printimin.imin.Utils.getBlackWhiteBitmap
import com.imin.printerlib.IminPrintUtils
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    var content = ""

    private lateinit var iminInit : IminPrintUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        iminInit = IminPrintUtils.getInstance(this@MainActivity)

        binding?.btnQr3?.setOnClickListener {
            content = binding?.etContent?.text.toString()
            getPrint(content, typeQr = 3, null)
        }
        binding?.btnQr4?.setOnClickListener {
            content = binding?.etContent?.text.toString()
            getPrint(content, typeQr = 4, null)
        }
        binding?.btnQr5?.setOnClickListener {
            content = binding?.etContent?.text.toString()
            getPrint(content, typeQr = 5, null)
        }
        binding?.btnBitmap100?.setOnClickListener {
            content = binding?.etContent?.text.toString()
            getPrint(content, null, typeBitmap = 100)
        }
        binding?.btnBitmap120?.setOnClickListener {
            content = binding?.etContent?.text.toString()
            getPrint(content, null, typeBitmap = 120)
        }
        binding?.btnBitmap140?.setOnClickListener {
            content = binding?.etContent?.text.toString()
            getPrint(content, null, typeBitmap = 140)
        }
    }

    private fun getPrint(content: String, typeQr: Int?, typeBitmap: Int?) {

        if(binding?.etContent?.text?.isEmpty() == true) {
            binding?.etContent?.error = "Input Content"
        } else {
            var status = ""
            iminInit.initPrinter(IminPrintUtils.PrintConnectType.SPI)
            iminInit.getPrinterStatus(
                    IminPrintUtils.PrintConnectType.SPI
            ) {
                when (it) {
                    -1 -> status = "The printer is not connected or powered on"
                    0 -> {
                        status = "The printer is normal"
                        print(content, typeQr, typeBitmap)
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

    private fun print(content: String, typeQr: Int?, typeBitmap: Int?) {
        with(iminInit){
            try {
//                val multiFormatWriter = MultiFormatWriter()
//                val bitMatrixBarcode: BitMatrix = multiFormatWriter.encode(content, BarcodeFormat.CODE_128, 400, 100)
//                val barcodeEncoderB = BarcodeEncoder()
//                val bitmapBarcode = barcodeEncoderB.createBitmap(bitMatrixBarcode)
//
//                val _bitmap = getBlackWhiteBitmap(bitmapBarcode)
//                printText(content)
//                printSingleBitmap(_bitmap)

                val qrBitmap = CuteR.ProductNormal("${this@MainActivity.content}", true, Color.BLACK)
                val qrStream = ByteArrayOutputStream()
                qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, qrStream)
                val _bitmap = getBlackWhiteBitmap(qrBitmap)

                //Qr
                typeQr?.let {
                    printText("Qr type $it $content")
                    printAndLineFeed()
                    setQrCodeSize(it)
                    printQrCode(content)
                }
                //bitmap
                typeBitmap?.let {
                    printText("bitmap type $it $content")
                    printAndLineFeed()
                    setBitmapWidth(it)
                    printSingleBitmap(_bitmap)
                }
                printAndFeedPaper(100)

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

}