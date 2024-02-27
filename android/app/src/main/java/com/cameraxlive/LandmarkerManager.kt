package com.cameraxlive;

import androidx.camera.core.ExperimentalGetImage
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewManagerDelegate

@ExperimentalGetImage @ReactModule(name = LandmarkerManager.NAME)
class LandmarkerManager(private val mCallerContext: ReactApplicationContext) :
        SimpleViewManager<Landmarker>() {

private val mDelegate: ViewManagerDelegate<Landmarker>

    init {
            mDelegate = delegate
            }

            override fun getDelegate(): ViewManagerDelegate<Landmarker> {
        return mDelegate
        }

        override fun getName(): String {
        return NAME
        }

        override fun createViewInstance(reactContext: ThemedReactContext): Landmarker {
        val landmarker = Landmarker(mCallerContext)
        return landmarker
        }

        companion object {
        const val NAME = "Landmarker"
        }

        }