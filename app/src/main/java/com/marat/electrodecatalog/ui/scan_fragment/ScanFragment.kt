package com.marat.electrodecatalog.ui.scan_fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.marat.electrodecatalog.R
import com.marat.electrodecatalog.ui.camera.*
import com.marat.electrodecatalog.ui.electrode_detailed.ElectrodeDetailedFragment
import com.marat.electrodecatalog.utils.addSystemTopAndBottomPadding
import com.marat.electrodecatalog.utils.addSystemTopPadding
import kotlinx.android.synthetic.main.fragment_scan.*
import java.io.IOException

class ScanFragment : Fragment(R.layout.fragment_scan), BarcodeGraphicTracker.BarcodeUpdateListener {

    private val mGraphicOverlay by lazy {
        view?.findViewById<GraphicOverlay<BarcodeGraphic>>(R.id.graphicOverlay)
    }
    private var mCameraSource: CameraSource? = null
    private var barcodeDetected = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarLayout.addSystemTopPadding()
        toolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val rc =
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource()
        } else {
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        val permissions =
            arrayOf(Manifest.permission.CAMERA)
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            requestPermissions(permissions, RC_HANDLE_CAMERA_PERM)
        }
    }

    @SuppressLint("InlinedApi")
    private fun createCameraSource() {
        val context = context
        val barcodeDetector = BarcodeDetector
            .Builder(context?.applicationContext)
            .build()
        val barcodeFactory = BarcodeTrackerFactory(mGraphicOverlay, this)
        barcodeDetector.setProcessor(
            MultiProcessor.Builder(barcodeFactory).build()
        )
        val builder =
            CameraSource.Builder(context, barcodeDetector)
                .setFlashMode(Camera.Parameters.FLASH_MODE_AUTO)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1024, 1024)
                .setRequestedFps(15.0f)
                .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
        mCameraSource = builder.build()
    }

    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    override fun onPause() {
        super.onPause()
        preview?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        preview?.release()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createCameraSource()
            return
        }
        parentFragmentManager.popBackStack()
    }

    @SuppressLint("MissingPermission")
    @Throws(SecurityException::class)
    private fun startCameraSource() {
        if (mCameraSource != null) {
            try {
                preview.start(mCameraSource, mGraphicOverlay)
            } catch (e: IOException) {
                mCameraSource?.release()
                mCameraSource = null
            }
        }
    }

    override fun onBarcodeDetected(barcode: Barcode) {
        val code = barcode.rawValue

        Log.d(TAG, "format ${barcode.format}")
        Log.d(TAG, "value ${barcode.rawValue}")

        if (code.length >= 13 && !barcodeDetected) {
            activity?.runOnUiThread {
                barcodeDetected = true
                Log.d(javaClass.canonicalName, "code: $code")
                parentFragmentManager.popBackStack()
                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragmentContainer,
                        ElectrodeDetailedFragment::class.java,
                        Bundle().apply { putString(ElectrodeDetailedFragment.ARG_BARCODE, code) }
                    )
                    .addToBackStack(ElectrodeDetailedFragment::class.java.canonicalName)
                    .commit()
            }
        }
    }

    companion object {
        private const val TAG = "BaggageTagScanFragment"
        private const val RC_HANDLE_CAMERA_PERM = 2
    }
}