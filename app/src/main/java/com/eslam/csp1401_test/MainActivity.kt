package com.eslam.csp1401_test


import android.app.ActivityManager
import android.app.KeyguardManager
import android.app.admin.DevicePolicyManager
import android.app.admin.SystemUpdatePolicy
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.UserManager
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.get
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.viewpager.widget.ViewPager
import kotlin.system.exitProcess
import android.app.KeyguardManager.KeyguardLock
import android.util.Log

import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import androidx.navigation.ui.setupWithNavController


class MainActivity : AppCompatActivity() {

    lateinit var componentname: ComponentName
    lateinit var adminReciever: AdminReciever
    lateinit var dpm: DevicePolicyManager
    lateinit var navController: NavController
    lateinit var mDecorView: View

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        }
        setContentView(R.layout.activity_main)

        this.setSupportActionBar(findViewById(R.id.toolBar))
        val toolbar = findViewById<Toolbar>(R.id.toolBar)
        actionBar?.setDisplayHomeAsUpEnabled(true)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        val appBarConfiguration =
            AppBarConfiguration.Builder(setOf(R.id.homeFragment, R.id.eventsFragment)).build()
        toolbar.setupWithNavController(navController, appBarConfiguration)
        mDecorView = window.decorView



        //setupActionBarWithNavController(navController, appBarConfiguration)


        // Set an option to turn on lock task mode when starting the activity.
//        val options = ActivityOptions.makeBasic()
//        options.setLockTaskEnabled(true)
        adminReciever = AdminReciever()
        componentname = ComponentName(this, AdminReciever::class.java)
        //adminReciever.getComponentName(this)!!
        dpm = getSystemService(Context.DEVICE_POLICY_SERVICE)
                as DevicePolicyManager


        val keyguardManager: KeyguardManager =
            getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (dpm.isDeviceOwnerApp(packageName)) {
            setDefaultMyKioskPolicies(true);


        } else {
            Toast.makeText(
                applicationContext,
                "Not Device owner", Toast.LENGTH_SHORT
            )
                .show();
            hideSystemUI()
            startLockTask()
        }


// Start our kiosk app's main activity with our lock task mode option.
//        val packageManager = packageManager
//        val launchIntent = packageManager.getLaunchIntentForPackage("com.eslam.csp1401_test")
//        if (launchIntent != null) {
//            if (!keyguardManager.isDeviceLocked)
//            {
//                //startActivity(launchIntent, options.toBundle())
//
//            }
//
//        }


//        // Create an intent filter to specify the Home category.
//        val filter = IntentFilter(Intent.ACTION_MAIN)
//        filter.addCategory(Intent.CATEGORY_HOME)
//        filter.addCategory(Intent.CATEGORY_DEFAULT)
//
//// Set the activity as the preferred option for the device.
//        val activity = ComponentName(this, MainActivity::class.java)
//
//        dpm.addPersistentPreferredActivity(componentName!!, filter, activity)
//
//        arrayOf(
//            UserManager.DISALLOW_FACTORY_RESET,
//            UserManager.DISALLOW_SAFE_BOOT,
//            UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA,
//            UserManager.DISALLOW_ADJUST_VOLUME,
//            UserManager.DISALLOW_ADD_USER).forEach { dpm.addUserRestriction(componentName, it) }
    }

    private fun setDefaultMyKioskPolicies(active: Boolean) {
        // set user restrictions
        setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, active)
        setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, false)
        setUserRestriction(UserManager.DISALLOW_ADD_USER, active)
        setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, active)
        setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, active)


        // disable keyguard and status bar
        dpm.setKeyguardDisabled(componentname, active)
        dpm.setStatusBarDisabled(componentname, active)

        // enable STAY_ON_WHILE_PLUGGED_IN
        enableStayOnWhilePluggedIn(active)

        // set system update policy
        if (active) {
            dpm.setSystemUpdatePolicy(
                //adminReciever.getComponentName(this)!!
                componentname,
                SystemUpdatePolicy.createWindowedInstallPolicy(60, 120)
            )
        } else {
            dpm.setSystemUpdatePolicy(
                componentname,
                null
            )
        }

        // set this Activity as a lock task package
        dpm.setLockTaskPackages(
            componentname,
            if (active) arrayOf(packageName) else arrayOf()
        )
        val intentFilter = IntentFilter(Intent.ACTION_MAIN)
        intentFilter.addCategory(Intent.CATEGORY_HOME)
        //intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        if (active) {
            // set KIOSK activity as home intent receiver so that it is started
            // on reboot
            dpm.addPersistentPreferredActivity(
                componentname, intentFilter, ComponentName(
                    packageName, MainActivity::class.java.name
                )
            )
        } else {
            dpm.clearPackagePersistentPreferredActivities(
                componentname, packageName
            )
        }
    }

    private fun setUserRestriction(restriction: String, disallow: Boolean) {
        if (disallow) {
            dpm.addUserRestriction(
                componentname,
                restriction
            )
        } else {
            dpm.clearUserRestriction(
                componentname,
                restriction
            )
        }
    }

    private fun enableStayOnWhilePluggedIn(enabled: Boolean) {
        if (enabled) {
            dpm.setGlobalSetting(
                componentname,
                Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                (BatteryManager.BATTERY_PLUGGED_AC
                        or BatteryManager.BATTERY_PLUGGED_USB
                        or BatteryManager.BATTERY_PLUGGED_WIRELESS).toString()
            )
        } else {
            dpm.setGlobalSetting(
                componentname,
                Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                "0"
            )
        }
    }

    override fun onStart() {
        super.onStart()
        // start lock task mode if its not already active

        if (dpm.isLockTaskPermitted(this.packageName)) {
            val am = getSystemService(
                ACTIVITY_SERVICE
            ) as ActivityManager
            if (am.lockTaskModeState ==
                ActivityManager.LOCK_TASK_MODE_NONE
            ) {
                startLockTask()


            }
        }
    }

    override fun onPause() {
        super.onPause()

        val activityManager = applicationContext
            .getSystemService(ACTIVITY_SERVICE) as ActivityManager
        activityManager.moveTaskToFront(taskId, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        // Inflate the custom overflow menu
        // Inflate the custom overflow menu
        menuInflater.inflate(R.menu.exit_lockmode, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

//        if(item.itemId == R.id.exit)
//        {
//            setDefaultMyKioskPolicies(false)
//
//        }

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.exit -> {
                if (dpm.isDeviceOwnerApp(packageName))
                {
                    setDefaultMyKioskPolicies(false)

                }else
                {
                    stopLockTask()

                }

                finish()
                exitProcess(0)
            }

        }

        return super.onOptionsItemSelected(item)
    }

    // This snippet hides the system bars.


    override fun onBackPressed() {

        if (navController.currentDestination == navController.graph.get(R.id.createEventFragment)) {
            super.onBackPressed()
        }

    }

    private fun hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        val containerView = findViewById<FragmentContainerView>(R.id.fragment_container)
        containerView.fitsSystemWindows = true



        mDecorView.setSystemUiVisibility(
           // View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                   // View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                     //View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or// hide nav bar
                     View.SYSTEM_UI_FLAG_FULLSCREEN or// hide status bar
                    View.SYSTEM_UI_FLAG_LOW_PROFILE or
                     View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

//        val window = this.window
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        window.statusBarColor = this.resources.getColor(R.color.purple_500)



    }

//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//
//        if (keyCode == KeyEvent.KEYCODE_HOME) {
//            Log.d(null, "onKeyDown: entered ")
//            startActivity(Intent(this,MainActivity::class.java))
//            return true
//        } else {
//            return super.onKeyDown(keyCode, event)
//        }
//
//    }

//    override fun onAttachedToWindow() {
//        val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
//        val lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE)
//        lock.disableKeyguard()
//
//    }

//    fun changeDefaultWindow(){
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//    }

    override fun onUserLeaveHint() {
        //super.onUserLeaveHint()


        startActivity(Intent(this,MainActivity::class.java))

    }




}