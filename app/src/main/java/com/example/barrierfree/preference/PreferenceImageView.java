package com.example.barrierfree.preference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.example.barrierfree.R;
import com.example.barrierfree.RoundImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PreferenceImageView extends Preference {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    Bitmap bitmap;

    private TextView userName, userEmail;
    private Button btnLogout;

    View.OnClickListener btnLogoutClickListener;
    RoundImageView roundImageView;

    public PreferenceImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //onBindViewHolder() will be called after we call setImageClickListener() from SettingsFragment
    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        roundImageView = (RoundImageView)holder.findViewById(R.id.user_photo);
        userName = (TextView)holder.findViewById(R.id.txt_user_name);
        userEmail = (TextView)holder.findViewById(R.id.txt_user_email);
        btnLogout = (Button)holder.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(btnLogoutClickListener);

        userName.setText(user.getDisplayName());
        userEmail.setText(user.getEmail());
        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    //현재로그인한 사용자 정보를 통해 PhotoUrl 가져오기
                    if(user.getPhotoUrl() == null)
                        return;
                    URL url = new URL(user.getPhotoUrl().toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (MalformedURLException ee) {
                    ee.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }};
        mThread.start();
        try {
            mThread.join();
            roundImageView.setImageBitmap(bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        roundImageView.setRectRadius(150f);
    }

    public void setBtnLogoutClickListener(View.OnClickListener onClickListener){
        btnLogoutClickListener = onClickListener;
    }
}

//https://stackoverflow.com/questions/59507248/how-to-add-image-view-that-can-be-edited-inside-a-preferencescreen-using-prefere