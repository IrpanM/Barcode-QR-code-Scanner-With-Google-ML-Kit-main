package com.rakasiwi.barandqrcodescanner.utility

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.rakasiwi.barandqrcodescanner.memory.ResultMemory
import javax.inject.Inject

class ImageAnalyzer @Inject constructor():ImageAnalysis.Analyzer {

    override fun analyze(imageProxy: ImageProxy) {
        scanBarcode(imageProxy)
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    fun scanBarcode(imageProxy: ImageProxy){
        imageProxy.image?.let {image->
            val inputImage = InputImage.fromMediaImage(image,imageProxy.imageInfo.rotationDegrees)
            val barcodeScannerOptions = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_ALL_FORMATS
                )
                .build()

            val scanner = BarcodeScanning.getClient(barcodeScannerOptions)

            scanner.process(inputImage)
                .addOnCompleteListener {
                    imageProxy.close()
                    if (it.isSuccessful){
                        readBarcodeData(it.result as List<Barcode>)
                    }else{
                        it.exception?.printStackTrace()
                    }
                }
        }
    }

    private fun readBarcodeData(barcodes:List<Barcode>){
        for (barcode in barcodes) {

            val valueType = barcode.valueType
            // See API reference for complete list of supported types
            when (valueType) {
                Barcode.TYPE_WIFI -> {
                    val ssid = barcode.wifi!!.ssid
                    val password = barcode.wifi!!.password
                    val type = barcode.wifi!!.encryptionType

                    Log.i("result", "ssid : $ssid\npassword : $password\ntype : $type")
                    ResultMemory.setValue("SSID: $ssid\nPassword: $password\nType: $type")
                }
                Barcode.TYPE_URL -> {
                    val title = barcode.url!!.title
                    val url = barcode.url!!.url
                    ResultMemory.setValue("Title: $title\nurl: $url")
                    Log.i("result", "title : $title\nurl : $url")
                }

                Barcode.TYPE_TEXT -> {
                    val text = barcode.displayValue
                    ResultMemory.setValue("Result: $text")
                    Log.i("result", "result : $text")
                }

                Barcode.TYPE_PRODUCT -> {
                    val value = barcode.displayValue
                    ResultMemory.setValue("Result: $value")
                    Log.i("result", "result : $value")
                }
            }
        }
    }
}