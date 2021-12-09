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
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    lateinit var adminReciever: AdminReciever
    lateinit var dpm:DevicePolicyManager
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)





        // Set an option to turn on lock task mode when starting the activity.
//        val options = ActivityOptions.makeBasic()
//        options.setLockTaskEnabled(true)
        adminReciever = AdminReciever()
        val componentName = adminReciever.getComponentName(this)
        dpm = getSystemService(Context.DEVICE_POLICY_SERVICE)
                as DevicePolicyManager


        val keyguardManager: KeyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (dpm.isDeviceOwnerApp(packageName)) {
            setDefaultMyKioskPolicies(true);
        } else {
            Toast.makeText(
                applicationContext,
                "Not Device owner", Toast.LENGTH_SHORT)
                .show();
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
        dpm.setKeyguardDisabled(adminReciever.getComponentName(this)!!, active)
       dpm.setStatusBarDisabled(adminReciever.getComponentName(this)!!, active)

        // enable STAY_ON_WHILE_PLUGGED_IN
        enableStayOnWhilePluggedIn(active)

        // set system update policy
        if (active) {
            dpm.setSystemUpdatePolicy(
                adminReciever.getComponentName(this)!!,
                SystemUpdatePolicy.createWindowedInstallPolicy(60, 120)
            )
        } else {
            dpm.setSystemUpdatePolicy(
                adminReciever.getComponentName(this)!!,
                null
            )
        }

        // set this Activity as a lock task package
        dpm.setLockTaskPackages(
            adminReciever.getComponentName(this)!!,
            if (active) arrayOf(packageName) else arrayOf()
        )
        val intentFilter = IntentFilter(Intent.ACTION_MAIN)
        intentFilter.addCategory(Intent.CATEGORY_HOME)
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        if (active) {
            // set KIOSK activity as home intent receiver so that it is started
            // on reboot
            dpm.addPersistentPreferredActivity(
                adminReciever.getComponentName(this)!!, intentFilter, ComponentName(
                    packageName, MainActivity::class.java.name
                )
            )
        } else {
            dpm.clearPackagePersistentPreferredActivities(
                adminReciever.getComponentName(this)!!, packageName
            )
        }
    }

    private fun setUserRestriction(restriction: String, disallow: Boolean) {
        if (disallow) {
            dpm.addUserRestriction(
                adminReciever.getComponentName(this)!!,
                restriction
            )
        } else {
            dpm.clearUserRestriction(
                adminReciever.getComponentName(this)!!,
                restriction
            )
        }
    }

    private fun enableStayOnWhilePluggedIn(enabled: Boolean) {
        if (enabled) {
            dpm.setGlobalSetting(
                adminReciever.getComponentName(this)!!,
                Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                (BatteryManager.BATTERY_PLUGGED_AC
                        or BatteryManager.BATTERY_PLUGGED_USB
                        or BatteryManager.BATTERY_PLUGGED_WIRELESS).toString()
            )
        } else {
            dpm.setGlobalSetting(
                adminReciever.getComponentName(this)!!,
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        // Inflate the custom overflow menu
        // Inflate the custom overflow menu
        menuInflater.inflate(R.menu.exit_lockmode, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.exit)
        {
            setDefaultMyKioskPolicies(false)
        }

        return super.onOptionsItemSelected(item)
    }
}