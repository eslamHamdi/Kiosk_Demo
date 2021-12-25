package com.eslam.csp1401_test

import android.content.SharedPreferences
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsFragment : PreferenceFragmentCompat() {

    val args:SettingsFragmentArgs by navArgs()
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val listPreference: ListPreference? = findPreference("save_settings")



        val savePreference:Preference? = findPreference("finish")
        val sp:SharedPreferences  = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        //val value = sp.getString("finish","")

        val value = listPreference?.value

        savePreference?.setOnPreferenceClickListener {
            if (value == "Save&Exit")
            {
               findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToEventsFragment(args.accessToken))

                return@setOnPreferenceClickListener true
            }else
            {
                return@setOnPreferenceClickListener false
            }

        }
    }


}