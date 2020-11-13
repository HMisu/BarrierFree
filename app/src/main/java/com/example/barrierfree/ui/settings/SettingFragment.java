package com.example.barrierfree.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.example.barrierfree.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingFragment extends PreferenceFragmentCompat {
    SharedPreferences pref;
    ListPreference soundPrefer, vibratorPrefer, saftysound, dangersound, saftyvibrator, dangervibrator;
    SwitchPreference issoundPrefer, isvibratorPrefer, isnosound;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        addPreferencesFromResource(R.xml.setting);
        soundPrefer = (ListPreference)findPreference("setting_sound");
        saftysound = (ListPreference)findPreference("safty_sound");
        dangersound = (ListPreference)findPreference("danger_sound");
        saftyvibrator = (ListPreference)findPreference("safty_vibrator");
        dangervibrator = (ListPreference)findPreference("danger_vibrator");
        vibratorPrefer = (ListPreference)findPreference("setting_vibrator");
        issoundPrefer = (SwitchPreference)findPreference("sound");
        isvibratorPrefer = (SwitchPreference)findPreference("vibrator");
        isnosound = (SwitchPreference) findPreference("no_sound");





        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());


        pref.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d("tag","sp?" + sharedPreferences + "key" + key);
            }
        });

        if(!pref.getString("setting_sound", "").equals("")){
            soundPrefer.setSummary(pref.getString("setting_sound", "녹차"));
        }

        if(!pref.getString("setting_vibrator", "").equals("")){
            vibratorPrefer.setSummary(pref.getString("setting_vibrator", "고속반복"));
        }

//        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
//            String key = preference.getKey();
//            Log.d("tag","클릭된 Preference의 key는 "+key);
//            return false;
//        }


        pref.registerOnSharedPreferenceChangeListener(prefListener);
   }
    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("setting_sound")) {
                soundPrefer.setSummary(pref.getString("setting_sound", "녹차"));
                db.collection("alert").document(user.getUid()).update("alarm_type", pref.getString(key,"녹차"));
            }

            if (key.equals("safty_sound")) {
                saftysound.setSummary(pref.getString("safty_sound", "녹차"));
                db.collection("alert").document(user.getUid()).update("alarm_safty", pref.getString(key,"녹차"));
            }

            if (key.equals("danger_sound")) {
                soundPrefer.setSummary(pref.getString("danger_sound", "녹차"));
                db.collection("alert").document(user.getUid()).update("alarm_danger", pref.getString(key,"녹차"));
            }

            if (key.equals("safty_vibrator")) {
                vibratorPrefer.setSummary(pref.getString("setting_vibrator", "고속반복"));
                db.collection("alert").document(user.getUid()).update("vibrate_safty", pref.getString(key,"녹차"));
            }

            if (key.equals("danger_vibrator")) {
                vibratorPrefer.setSummary(pref.getString("setting_vibrator", "고속반복"));
                db.collection("alert").document(user.getUid()).update("vibrate_danger", pref.getString(key,"녹차"));
            }

            if (key.equals("setting_vibrator")) {
                vibratorPrefer.setSummary(pref.getString("setting_vibrator", "고속반복"));
                db.collection("alert").document(user.getUid()).update("vibrate_type", pref.getString(key,"녹차"));
            }

            if(key.equals("vibrator")){
                if(isvibratorPrefer.getSwitchTextOn().equals("ON")) {
                    db.collection("alert").document(user.getUid()).update("vibrate", pref.getBoolean(key, true));
                } else {
                    db.collection("alert").document(user.getUid()).update("vibrate", pref.getBoolean(key, false));
                }
            }

            if(key.equals("sound")){
                if(issoundPrefer.getSwitchTextOn().equals("ON")) {
                    db.collection("alert").document(user.getUid()).update("alarm", pref.getBoolean(key, true));
                } else {
                    db.collection("alert").document(user.getUid()).update("alarm", pref.getBoolean(key, false));
                }
            }
        }
    };


}