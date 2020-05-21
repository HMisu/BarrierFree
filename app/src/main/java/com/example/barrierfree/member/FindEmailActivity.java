package com.example.barrierfree.member;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.barrierfree.R;

public class FindEmailActivity extends AppCompatActivity {
    private Button btnauth;
    private TextView txtnotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_email);

        btnauth = (Button) findViewById(R.id.btn_email_auth);
        txtnotice = (TextView) findViewById(R.id.txt_notice);

        txtnotice.setVisibility(View.GONE);

        btnauth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtnotice.setVisibility(View.VISIBLE);
            }
        });
    }
}
