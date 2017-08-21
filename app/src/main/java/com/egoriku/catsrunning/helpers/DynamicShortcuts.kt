package com.egoriku.catsrunning.helpers

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.annotation.RequiresApi
import android.support.annotation.StringRes
import com.egoriku.catsrunning.R
import com.egoriku.catsrunning.activities.FitActivity
import com.egoriku.catsrunning.models.Constants.Extras.KEY_TYPE_FIT

class DynamicShortcuts(val context: Context) {

    @RequiresApi(Build.VERSION_CODES.M)
    private val shortcutManager: ShortcutManager = context.getSystemService(ShortcutManager::class.java)

    companion object {
        private const val WALKING = "walking_shortcut"
        private const val RUNNING = "running_shortcut"
        private const val CYCLING = "cycling_shortcut"
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    fun buildShortcuts() {
        if (shortcutManager.dynamicShortcuts.size == 0) {
            val shortCuts = mutableListOf<ShortcutInfo>()
            shortCuts.add(getShortcut(WALKING, R.string.type_walking, R.drawable.ic_vec_shortcuts_walk, TypeFit.WALKING))
            shortCuts.add(getShortcut(RUNNING, R.string.type_running, R.drawable.ic_vec_shortcuts_run, TypeFit.RUNNING))
            shortCuts.add(getShortcut(CYCLING, R.string.type_cycling, R.drawable.ic_vec_shortcuts_cycling, TypeFit.CYCLING))
            shortcutManager.addDynamicShortcuts(shortCuts)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun getShortcut(id: String, @StringRes label: Int, @DrawableRes icon: Int, @TypeFit typeFit: Int): ShortcutInfo {
        return ShortcutInfo.Builder(context, id)
                .setShortLabel(context.getString(label))
                .setLongLabel(context.getString(label))
                .setIcon(Icon.createWithResource(context, icon))
                .setIntent(Intent(Intent.ACTION_VIEW, Uri.EMPTY, context, FitActivity::class.java).putExtra(KEY_TYPE_FIT, typeFit))
                .build()
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    fun clearShortcuts() {
        if (shortcutManager.dynamicShortcuts.size > 0) {
            shortcutManager.disableShortcuts(listOf(WALKING, RUNNING, CYCLING))
        }
    }
}
