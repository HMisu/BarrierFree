package com.example.barrierfree.preference;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.bumptech.glide.Glide;
import com.example.barrierfree.R;
import com.example.barrierfree.RoundImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PreferenceImageView extends Preference {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    Bitmap bitmap;

    private TextView userName, userEmail;
    private Button btnLogout;

    View.OnClickListener btnLogoutClickListener;
    RoundImageView imageView;

    Handler handler = new Handler();  // 외부쓰레드 에서 메인 UI화면을 그릴때 사

    public PreferenceImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //onBindViewHolder() will be called after we call setImageClickListener() from SettingsFragment
    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        imageView = (RoundImageView) holder.findViewById(R.id.user_photo);
        userName = (TextView) holder.findViewById(R.id.txt_user_name);
        userEmail = (TextView) holder.findViewById(R.id.txt_user_email);
        btnLogout = (Button) holder.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(btnLogoutClickListener);

        userName.setText(user.getDisplayName());
        userEmail.setText(user.getEmail());

        if(user.getPhotoUrl() != null){
            Glide.with(getContext()).load(user.getPhotoUrl().toString()).into(imageView);
            imageView.setRectRadius(100f);
        }
    }

    public void setBtnLogoutClickListener(View.OnClickListener onClickListener) {
        btnLogoutClickListener = onClickListener;
    }
}

//https://stackoverflow.com/questions/59507248/how-to-add-image-view-that-can-be-edited-inside-a-preferencescreen-using-prefere