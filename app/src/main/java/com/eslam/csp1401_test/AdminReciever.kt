package com.eslam.csp1401_test

import android.app.admin.DeviceAdminReceiver
import android.content.ComponentName
import android.content.Context


class AdminReciever :DeviceAdminReceiver() {

    private val TAG = "DeviceAdminReceiver"
    fun getComponentName(context: Context): ComponentName? {
        return ComponentName(context.applicationContext, DeviceAdminReceiver::class.java)
    }
}