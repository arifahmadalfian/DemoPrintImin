package com.example.printimin

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.RemoteException
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.printimin.databinding.ActivityMainBinding
import com.example.printimin.imin.CuteR
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.imin.printerlib.IminPrintUtils
import com.itextpdf.text.Image
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.ByteArrayOutputStream

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
                val multiFormatWriter = MultiFormatWriter()
                val bitMatrixBarcode: BitMatrix = multiFormatWriter.encode(content, BarcodeFormat.CODE_128, 400, 100)
                val barcodeEncoderB = BarcodeEncoder()
                val bitmapBarcode = barcodeEncoderB.createBitmap(bitMatrixBarcode)

                val _bitmap = getBlackWhiteBitmap(bitmapBarcode)
                printText(content)
                printSingleBitmap(_bitmap)
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

    override fun onPause() {
        super.onPause()
    }

    fun getBlackWhiteBitmap(bitmap: Bitmap): Bitmap? {
        val w = bitmap.width
        val h = bitmap.height
        val resultBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
        var color = 0
        var a: Int
        var r: Int
        var g: Int
        var b: Int
        var r1: Int
        var g1: Int
        var b1: Int
        val oldPx = IntArray(w * h)
        val newPx = IntArray(w * h)
        bitmap.getPixels(oldPx, 0, w, 0, 0, w, h)
        for (i in 0 until w * h) {
            color = oldPx[i]
            r = Color.red(color)
            g = Color.green(color)
            b = Color.blue(color)
            a = Color.alpha(color)
            //黑白矩阵
            r1 = (0.33 * r + 0.59 * g + 0.11 * b).toInt()
            g1 = (0.33 * r + 0.59 * g + 0.11 * b).toInt()
            b1 = (0.33 * r + 0.59 * g + 0.11 * b).toInt()
            //检查各像素值是否超出范围
            if (r1 > 255) {
                r1 = 255
            }
            if (g1 > 255) {
                g1 = 255
            }
            if (b1 > 255) {
                b1 = 255
            }
            newPx[i] = Color.argb(a, r1, g1, b1)
        }
        resultBitmap.setPixels(newPx, 0, w, 0, 0, w, h)
        return getGreyBitmap(resultBitmap)
    }

    fun getGreyBitmap(bitmap: Bitmap?): Bitmap? {
        return if (bitmap == null) {
            null
        } else {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            val gray = IntArray(height * width)
            var e: Int
            var i: Int
            var j: Int
            var g: Int
            e = 0
            while (e < height) {
                i = 0
                while (i < width) {
                    j = pixels[width * e + i]
                    g = j and 16711680 shr 16
                    gray[width * e + i] = g
                    ++i
                }
                ++e
            }
            i = 0
            while (i < height) {
                j = 0
                while (j < width) {
                    g = gray[width * i + j]
                    if (g >= 128) {
                        pixels[width * i + j] = -1
                        e = g - 255
                    } else {
                        pixels[width * i + j] = -16777216
                        e = g - 0
                    }
                    if (j < width - 1 && i < height - 1) {
                        gray[width * i + j + 1] += 3 * e / 8
                        gray[width * (i + 1) + j] += 3 * e / 8
                        gray[width * (i + 1) + j + 1] += e / 4
                    } else if (j == width - 1 && i < height - 1) {
                        gray[width * (i + 1) + j] += 3 * e / 8
                    } else if (j < width - 1 && i == height - 1) {
                        gray[width * i + j + 1] += e / 4
                    }
                    ++j
                }
                ++i
            }
            val mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            mBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            mBitmap
        }
    }
}