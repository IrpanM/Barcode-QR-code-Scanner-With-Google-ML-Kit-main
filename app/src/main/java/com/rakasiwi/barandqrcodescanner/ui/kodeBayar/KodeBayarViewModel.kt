package com.rakasiwi.barandqrcodescanner.ui.kodeBayar

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.Barcode
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.Writer
import java.util.*

class KodeBayarViewModel : ViewModel() {

    fun IntRange.random() =
        Random().nextInt((endInclusive + 1) - start) + start

    fun generateBarCode():Bitmap{
        val uid  = UUID.randomUUID().toString()
        val barcodeEncoder = BarcodeEncoder()
        return barcodeEncoder.encodeBitmap(uid,BarcodeFormat.QR_CODE,520, 520)
    }
}