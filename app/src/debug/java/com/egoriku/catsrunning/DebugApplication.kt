package com.egoriku.catsrunning

import android.annotation.SuppressLint
import android.support.multidex.MultiDexApplication
import com.squareup.leakcanary.AndroidHeapDumper
import com.squareup.leakcanary.DefaultLeakDirectoryProvider
import com.squareup.leakcanary.HeapDumper
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber
import java.io.File

@SuppressLint("Registered")
open class DebugApplication : MultiDexApplication() {

    lateinit var dumper: TogglableHeapDumper

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        initLeakCanary()
    }

    fun initLeakCanary() {
        val leakDirectoryProvider = DefaultLeakDirectoryProvider(this)
        val defaultDumper = AndroidHeapDumper(this, leakDirectoryProvider)
        dumper = TogglableHeapDumper(defaultDumper)
        LeakCanary.refWatcher(this)
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
                HeapDumper.RETRY_LATER
            }
        }
    }
}
