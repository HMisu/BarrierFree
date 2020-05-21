package com.example.barrierfree.member;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.barrierfree.MainActivity;
import com.example.barrierfree.R;

public class LoginActivity extends AppCompatActivity {
    Button btnlogin;
    TextView link_join;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        btnlogin = (Button) findViewById(R.id.btn_login);
        link_join = (TextView) findViewById(R.id.link_join);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "로그인 완료", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        link_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "회원가입", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
