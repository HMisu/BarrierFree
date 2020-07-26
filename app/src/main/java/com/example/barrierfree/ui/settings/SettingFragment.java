package com.example.barrierfree.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.barrierfree.R;
import com.example.barrierfree.ui.member.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingFragment extends PreferenceFragmentCompat {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    SharedPreferences pref;
    ListPreference soundPrefer, vibratorPrefer;
    Preference memberWithdrawal;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.setting);
        soundPrefer = (ListPreference)findPreference("setting_sound");
        vibratorPrefer = (ListPreference)findPreference("setting_vibrator");
        memberWithdrawal = (Preference)findPreference("setting_bye");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if(!pref.getString("setting_sound", "").equals("")){
            soundPrefer.setSummary(pref.getString("setting_sound", "녹차"));
        }
        if(!pref.getString("setting_vibrator", "").equals("")){
            vibratorPrefer.setSummary(pref.getString("setting_vibrator", "고속반복"));
        }
        if(!pref.getString("setting_vibrator", "").equals("")){
            vibratorPrefer.setSummary(pref.getString("setting_vibrator", ""));
        }

        memberWithdrawal.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                mAuth.getCurrentUser().delete();
                FirebaseAuth.getInstance().signOut();
                db.collection("members").document(user.getUid()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("메시지", "DocumentSnapshot successfully deleted!" );
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("메시지", "Error deleting document", e);
                            }
                        });
                ActivityCompat.finishAffinity(getActivity());
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                return true;
            }
        });
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
