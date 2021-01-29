package com.rakasiwi.barandqrcodescanner.ui.Scan

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.rakasiwi.barandqrcodescanner.R
import com.rakasiwi.barandqrcodescanner.utility.ImageAnalyzer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.scanner_fragment.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@AndroidEntryPoint
class ScannerFragment : Fragment() {

    companion object {
        fun newInstance() = ScannerFragment()
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private val viewModel: ScannerViewModel by viewModels()
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var analyzer: ImageAnalyzer
    private var on = true



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        analyzer = ImageAnalyzer()
        cameraExecutor = Executors.newSingleThreadExecutor()
        return inflater.inflate(R.layout.scanner_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        if (allPermissionGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

    }


    @SuppressLint("UnsafeExperimentalUsageError")
    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder()
            .build()

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(previewView.surfaceProvider)

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(cameraExecutor, analyzer)

        val camera = cameraProvider.bindToLifecycle(
            this as LifecycleOwner,
            cameraSelector,
            imageAnalysis,
            preview
        )

        camera.cameraInfo.hasFlashUnit()

        flash_btn.setOnClickListener {
            if (on){
                on = false
                flash_btn.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_flash_off))
            }else{
                on = true
                flash_btn.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_flash_on))
            }

            camera.cameraControl.enableTorch(on)
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if (allPermissionGranted()){
                startCamera()
            }else{
                Toast.makeText(requireContext(),
                    getString(R.string.permissions_not_granted_by_the_user),
                    Toast.LENGTH_SHORT).show()
                activity?.finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.result.observe(viewLifecycleOwner,{
            result_txt.text = it
        })
    }

    private fun startCamera() {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }

}