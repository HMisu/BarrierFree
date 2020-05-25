package com.example.barrierfree.member;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.barrierfree.MainActivity;
import com.example.barrierfree.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    Button btnlogin;
    TextView link_join, link_findpw, link_findemail, email, pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //파이어베이스 접근 설정
        firebaseAuth = FirebaseAuth.getInstance();

        btnlogin = (Button) findViewById(R.id.btn_login);
        link_join = (TextView) findViewById(R.id.link_join);
        link_findpw = (TextView) findViewById(R.id.link_join);
        link_findemail = (TextView) findViewById(R.id.link_join);
        email = (TextView) findViewById(R.id.edit_email);
        pw = (TextView) findViewById(R.id.edit_pw);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signInWithEmailAndPassword(email.getText().toString().trim(), pw.getText().toString().trim())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(LoginActivity.this,"로그인 오류",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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
