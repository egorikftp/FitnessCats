package com.egoriku.catsrunning.ui.activity

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.net.Uri
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import com.egoriku.catsrunning.BuildConfig
import com.egoriku.catsrunning.R
import com.egoriku.catsrunning.data.TracksDataManager
import com.egoriku.catsrunning.fragments.*
import com.egoriku.catsrunning.helpers.FragmentsTag
import com.egoriku.catsrunning.helpers.FragmentsTag.MAIN
import com.egoriku.catsrunning.helpers.FragmentsTag.NEW_MAIN
import com.egoriku.catsrunning.models.FitState
import com.egoriku.catsrunning.ui.fragment.TracksFragment
import com.egoriku.catsrunning.utils.FirebaseUtils
import com.egoriku.core_lib.Constants
import com.egoriku.core_lib.extensions.drawableCompat
import com.egoriku.core_lib.extensions.fromApi
import com.egoriku.core_lib.extensions.toApi
import com.firebase.ui.auth.AuthUI
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import kotlinx.android.synthetic.main.toolbar_scamper_activity.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class TracksActivity : AppCompatActivity() {

    companion object {
        const val NAV_DRAWER_SELECTED_POSITION = "Nav_drawer_position"
    }

    private lateinit var navigationDrawer: Drawer
    private var userEmail: String? = null
    private var userName: String? = null
    private var userPhoto: Uri? = null

    override fun onStart() {
        super.onStart()
        FirebaseUtils.getInstance().updateUserInfo(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracks)

        checkUserLogin()
        initDrawer(savedInstanceState)

        setSupportActionBar(toolbar_app)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            showFragment(TracksFragment.instance(), NEW_MAIN, null, true)
            navigationDrawer.setSelectionAtPosition(5)
        }
    }

    private fun checkUserLogin() {
        val user = FirebaseUtils.getInstance().user
        if (user != null) {
            userEmail = user.email
            userName = user.displayName
            userPhoto = user.photoUrl
        } else {
            openLoginActivity()
        }
    }

    private fun initDrawer(savedInstanceState: Bundle?) {
        navigationDrawer = DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar_app)
                .withAccountHeader(drawerHeader)
                .addDrawerItems(
                        PrimaryDrawerItem()
                                .withName(R.string.navigation_drawer_main_activity)
                                .withIcon(drawableCompat(this, R.drawable.ic_vec_tracks))
                                .withTag(MAIN),
                        PrimaryDrawerItem()
                                .withName(R.string.navigation_drawer_reminders)
                                .withIcon(drawableCompat(this, R.drawable.ic_vec_notifications))
                                .withTag(FragmentsTag.REMINDER),
                        PrimaryDrawerItem()
                                .withName(R.string.navigation_drawer_liked)
                                .withIcon(drawableCompat(this, R.drawable.ic_vec_favorite))
                                .withTag(FragmentsTag.LIKED),
                        PrimaryDrawerItem()
                                .withName(R.string.navigation_drawer_statistic)
                                .withIcon(drawableCompat(this, R.drawable.ic_vec_statistic))
                                .withTag(FragmentsTag.STATISTIC),
                        PrimaryDrawerItem()
                                .withName(R.string.navigation_drawer_main_activity_new)
                                .withIcon(drawableCompat(this, R.drawable.ic_vec_tracks))
                                .withTag(NEW_MAIN),
                        DividerDrawerItem(),
                        SecondaryDrawerItem()
                                .withName(R.string.navigation_drawer_exit)
                                .withIcon(drawableCompat(this, R.drawable.ic_vec_exit_from_app))
                                .withTag(FragmentsTag.EXIT))
                .addStickyDrawerItems(
                        PrimaryDrawerItem()
                                .withName(getString(R.string.build_version) + Constants.SPACE + BuildConfig.BUILD_VERSION)
                                .withTag(FragmentsTag.BUILD_VERSION),
                        PrimaryDrawerItem()
                                .withName(R.string.navigation_drawer_setting)
                                .withIcon(drawableCompat(this, R.drawable.ic_vec_settings))
                                .withTag(FragmentsTag.SETTINGS))
                .withOnDrawerItemClickListener { _, _, drawerItem ->
                    setDefaultToolbarColor()
                    when (drawerItem.tag.toString()) {
                        FragmentsTag.MAIN -> showFragment(AllFitnessDataFragment.newInstance(), FragmentsTag.MAIN, FragmentsTag.NEW_MAIN)
                        FragmentsTag.REMINDER -> showFragment(RemindersFragment.newInstance(), FragmentsTag.REMINDER, FragmentsTag.NEW_MAIN)
                        FragmentsTag.LIKED -> showFragment(LikedFragment.newInstance(), FragmentsTag.LIKED, FragmentsTag.NEW_MAIN)
                        FragmentsTag.STATISTIC -> showFragment(StatisticFragment.newInstance(), FragmentsTag.STATISTIC, FragmentsTag.NEW_MAIN)
                        FragmentsTag.EXIT -> when (FitState.getInstance().isFitRun) {
                            true -> toast(R.string.tracks_activity_error_exit_account)
                            false -> exitFromAccount()
                        }

                        FragmentsTag.SETTINGS -> showFragment(SettingsFragment.newInstance(), FragmentsTag.SETTINGS, FragmentsTag.NEW_MAIN)
                        FragmentsTag.NEW_MAIN -> showFragment(TracksFragment.instance(), FragmentsTag.NEW_MAIN, null, true)
                    }
                    false
                }
                .withSavedInstance(savedInstanceState)
                .build()
    }

    private val drawerHeader: AccountHeader
        get() = AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.primary_dark)
                .addProfiles(ProfileDrawerItem()
                        .withName(userName)
                        .withEmail(userEmail)
                        .withIcon(drawableCompat(this, R.drawable.ic_vec_cat_weary)))
                .build()

    @SuppressLint("CommitTransaction")
    private fun showFragment(fragment: Fragment, @FragmentsTag tag: String, @FragmentsTag clearToTag: String?, clearInclusive: Boolean = false) {
        val fragmentManager = supportFragmentManager

        if (clearToTag != null || clearInclusive) {
            fragmentManager.popBackStack(clearToTag, if (clearInclusive) FragmentManager.POP_BACK_STACK_INCLUSIVE else 0)
        }

        fragmentManager.beginTransaction().apply {
            replace(R.id.fragments_container, fragment, tag)
            addToBackStack(tag)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            commit()
        }
    }

    private fun exitFromAccount() {
        TracksDataManager.instance.apply {
            removeUIListener()
            clearData()
            close()
        }
        AuthUI.getInstance().signOut(this).addOnCompleteListener { openLoginActivity() }
    }

    private fun openLoginActivity() {
        startActivity<SplashActivity>(SplashActivity.IS_ANIMATE to true).apply {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ)
            finish()
        }
    }

    fun onFragmentStart(titleResId: Int) {
        if (supportActionBar != null) {
            supportActionBar!!.setTitle(titleResId)
        }
    }

    fun tabTitle(titleId: String) {
        supportActionBar?.title = titleId
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        navigationDrawer.actionBarDrawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        navigationDrawer.actionBarDrawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return navigationDrawer.actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (navigationDrawer.isDrawerOpen) {
            navigationDrawer.closeDrawer()
            return
        }
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
            return
        }
        super.onBackPressed()
    }

    fun animateToolbar(@ColorRes colorAccent: Int, @ColorRes colorPrimaryDark: Int) {
        val cx = toolbar_app.width / 2
        val cy = toolbar_app.height / 2
        val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

        toolbar_app.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

            @SuppressLint("NewApi")
            override fun onGlobalLayout() {
                toolbar_app.viewTreeObserver.removeOnGlobalLayoutListener(this)

                fromApi(VERSION_CODES.LOLLIPOP) {
                    val circularReveal = ViewAnimationUtils.createCircularReveal(toolbar_app, cx, cy, 0f, finalRadius)
                    toolbar_app.setBackgroundColor(ContextCompat.getColor(this@TracksActivity, colorPrimaryDark))
                    circularReveal.start()
                    window.statusBarColor = ContextCompat.getColor(this@TracksActivity, colorPrimaryDark)
                    toolbar_app.setBackgroundColor(ContextCompat.getColor(this@TracksActivity, colorAccent))
                }

                toApi(VERSION_CODES.LOLLIPOP) {
                    toolbar_app.setBackgroundColor(ContextCompat.getColor(this@TracksActivity, colorAccent))
                }
            }
        })
    }

    @SuppressLint("NewApi")
    private fun setDefaultToolbarColor() {
        toolbar_app.setBackgroundColor(ContextCompat.getColor(this, R.color.settings_toolbar_color))
        fromApi(VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.settings_toolbar_color_dark)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(NAV_DRAWER_SELECTED_POSITION, navigationDrawer.currentSelectedPosition)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        navigationDrawer.setSelectionAtPosition(savedInstanceState.getInt(NAV_DRAWER_SELECTED_POSITION))
    }
}
