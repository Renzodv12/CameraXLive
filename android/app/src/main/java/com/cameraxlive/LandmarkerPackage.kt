package com.cameraxlive

import androidx.camera.core.ExperimentalGetImage
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager
import java.util.Collections

@ExperimentalGetImage class LandmarkerPackage : ReactPackage {
    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> =
            Collections.singletonList(LandmarkerManager(reactContext))

    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> =
            emptyList()
}