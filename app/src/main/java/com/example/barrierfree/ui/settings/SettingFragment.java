package com.example.barrierfree.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.barrierfree.R;

public class SettingFragment extends PreferenceFragmentCompat {

    SharedPreferences pref;
    ListPreference soundPrefer, vibratorPrefer;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.setting);
        soundPrefer = (ListPreference)findPreference("setting_sound");
        vibratorPrefer = (ListPreference)findPreference("setting_vibrator");

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if(!pref.getString("setting_sound", "").equals("")){
            soundPrefer.setSummary(pref.getString("setting_sound", "녹차"));
        }

        if(!pref.getString("setting_vibrator", "").equals("")){
            vibratorPrefer.setSummary(pref.getString("setting_vibrator", "고속반복"));
        }

        pref.registerOnSharedPreferenceChangeListener(prefListener);
    }
    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("setting_sound")) {
                soundPrefer.setSummary(pref.getString("setting_sound", "녹차"));
            }

            if (key.equals("setting_vibrator")) {
                vibratorPrefer.setSummary(pref.getString("setting_vibrator", "고속반복"));
            }
        }
    };
}
