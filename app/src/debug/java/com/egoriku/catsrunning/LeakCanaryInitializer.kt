package com.egoriku.catsrunning

import android.app.Application
import com.squareup.leakcanary.AndroidHeapDumper
import com.squareup.leakcanary.DefaultLeakDirectoryProvider
import com.squareup.leakcanary.HeapDumper
import com.squareup.leakcanary.LeakCanary
import java.io.File

object LeakCanaryInitializer {

    lateinit var dumper: TogglableHeapDumper

    fun register(application: Application, isEnable: Boolean) {
        if (!isEnable) {
            return
        }

        val leakDirectoryProvider = DefaultLeakDirectoryProvider(application)
        val defaultDumper = AndroidHeapDumper(application, leakDirectoryProvider)
        dumper = TogglableHeapDumper(defaultDumper)

        LeakCanary
                .refWatcher(application)
                .heapDumper(dumper)
                .buildAndInstall()
    }

    class TogglableHeapDumper(val defaultDumper: HeapDumper) : HeapDumper {
        var isEnabled = false

        fun toggle(): Boolean {
            isEnabled = !isEnabled
            return isEnabled
        }

        override fun dumpHeap(): File {
            return if (isEnabled) {
                defaultDumper.dumpHeap()
            } else {
                File("temp file")
            }
        }
    }
}