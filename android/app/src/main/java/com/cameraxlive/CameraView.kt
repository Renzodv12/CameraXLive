@file:Suppress("BlockingMethodInNonBlockingContext")

package com.cameraxlive

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.common.Triangle
import java.lang.Float.max
import java.util.concurrent.ExecutorService
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/*
    Only works in portrait mode so far since the readjustment formula takes the value of width directly
 */

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun CameraView(executor: ExecutorService, context: Context) {
    val screenHeightPx = remember { mutableStateOf(0f) }
    val screenWidthPx = remember { mutableStateOf(0f) }
    var boundsList by remember { mutableStateOf(listOf<Barcode>()) }

    var scaleFactor = 1f
    var scaleHeight: Float
    var scaleWidth: Float
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val lifecycleOwner = LocalLifecycleOwner.current

    val builder = Preview.Builder()
    val preview = builder
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .build()
    val previewView = remember { PreviewView(context) }
    val imageAnalysis: ImageAnalysis = remember {
        ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .build().also {
                    it.setAnalyzer(
                            executor
                    ) { imageProxy ->
                        /**
                         * TODO: Separate this snippet into a different file
                         * detectFace(imageProxy)
                         *}
                         */
                        val image = BitmapUtils.getBitmap(imageProxy, false)
                        if (image != null) {
                            val defaultDetector = BarcodeScanning.getClient()

                            defaultDetector.process(InputImage.fromBitmap(image, 0))
                                    .addOnSuccessListener { result ->
                                        // Task completed successfully
                                        if (result != null) {
                                            boundsList = result
                                        }
                                        imageProxy.close()
                                    }.addOnFailureListener { e ->
                                        // Task failed with an exception
                                        Log.d("FAIL", "The result has , $e")
                                    }
                        }
                    }
                }
    }
    val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    // 2
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, imageAnalysis
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }
    // 3
    Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coordinates ->
                        screenHeightPx.value = coordinates.size.height.toFloat()
                        screenWidthPx.value = coordinates.size.width.toFloat()
                        scaleHeight = screenHeightPx.value / 640
                        scaleWidth = screenWidthPx.value / 480
                        scaleFactor = max(scaleWidth, scaleHeight)
                        Log.d("RATIO", "Composable ratio $scaleHeight : $scaleWidth")
                    }
    ) {
        //Works till here
        AndroidView(
                { previewView
                }, modifier = Modifier
                .fillMaxSize()
        )
        Log.d("Started", "$previewView")
        Canvas(
                Modifier.fillMaxSize()
        ) {
            Log.d("Started", "Camend2")
            Log.d("cAMERA", boundsList[0].toString())

        }
    }

}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
        suspendCoroutine { continuation ->
            ProcessCameraProvider.getInstance(this).also { cameraProvider ->
                cameraProvider.addListener({
                    continuation.resume(cameraProvider.get())
                }, ContextCompat.getMainExecutor(this))
            }
        }