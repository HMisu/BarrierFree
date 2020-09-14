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
    RoundImageView imageView;

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

        Thread uThread = new Thread() {
            @Override
            public void run() {
                try {
                    if (user.getPhotoUrl() == null)
                        return;
                    URL url = new URL(user.getPhotoUrl().toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        if (user.getPhotoUrl() != null) {
            uThread.start();
            try {
                uThread.join();
                imageView.setImageBitmap(bitmap);
                imageView.setRectRadius(100f);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            imageView.setImageResource(R.drawable.ic_defaultuser);
        }
    }

    public void setBtnLogoutClickListener(View.OnClickListener onClickListener) {
        btnLogoutClickListener = onClickListener;
    }
}

//https://stackoverflow.com/questions/59507248/how-to-add-image-view-that-can-be-edited-inside-a-preferencescreen-using-prefere