package com.example.barrierfree.ui.member;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.barrierfree.MainActivity;
import com.example.barrierfree.R;
import com.example.barrierfree.preference.PreferenceImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;

public class MemberInfoFragment extends PreferenceFragmentCompat {
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    PreferenceImageView preferenceImageView;
    Preference withdrawal, connectPrefer, infoPrefer, pwPrefer;

    SharedPreferences pref;
    String fragmentTag, providerId;
    Fragment fragmentClass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(R.color.colorBackGray));

        return view;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.mypage);
        withdrawal = (Preference) findPreference("setting_withdrawal");
        connectPrefer = (Preference) findPreference("setting_connect");
        infoPrefer = (Preference) findPreference("setting_meminfo");
        pwPrefer = (Preference) findPreference("setting_mempw");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        connectPrefer.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                fragmentTag = new MemberConnectFragment().getClass().getSimpleName();
                fragmentClass = new MemberConnectFragment();
                ((MainActivity) getActivity()).replaceFragment(fragmentTag, fragmentClass);
                return true;
            }
        });

        infoPrefer.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                fragmentTag = new MemberInfoUpdateFragment().getClass().getSimpleName();
                fragmentClass = new MemberInfoUpdateFragment();
                ((MainActivity) getActivity()).replaceFragment(fragmentTag, fragmentClass);
                return true;
            }
        });

        pwPrefer.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                for (UserInfo profile : user.getProviderData()) {
                    // Id of the provider (ex: google.com)
                    providerId = profile.getProviderId();
                }
                if(providerId.equals("google.com")){
                    Toast.makeText(getActivity(), "구글 로그인 시 비밀변호 변경이 불가합니다", Toast.LENGTH_SHORT).show();
                } else {
                    fragmentTag = new MemberPWUpdateFragment().getClass().getSimpleName();
                    fragmentClass = new MemberPWUpdateFragment();
                    ((MainActivity) getActivity()).replaceFragment(fragmentTag, fragmentClass);
                }
                return true;
            }
        });

        withdrawal.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FirebaseFirestore.getInstance().collection("members").document(user.getUid()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("메시지", "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("메시지", "Error deleting document", e);
                            }
                        });
                mAuth.getCurrentUser().delete();
                FirebaseAuth.getInstance().signOut();
                (new Handler()).postDelayed(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        ActivityCompat.finishAffinity(getActivity());
                    }
                }, 700);
                return true;
            }
        });

        preferenceImageView = (PreferenceImageView) findPreference("image_preference");

        if (preferenceImageView != null)
            preferenceImageView.setBtnLogoutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
    }
}