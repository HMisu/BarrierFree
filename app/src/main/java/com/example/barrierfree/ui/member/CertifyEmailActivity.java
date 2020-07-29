package com.example.barrierfree.ui.member;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.barrierfree.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CertifyEmailActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private Button btnsend;
    private TextView txtuseremail, txtcheckemail;
    private boolean login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certify_email);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        btnsend = (Button) findViewById(R.id.btn_email_send);
        txtuseremail = (TextView) findViewById(R.id.txt_user_email);
        txtcheckemail = (TextView) findViewById(R.id.txt_check_email);

        txtuseremail.setText("가입 이메일 주소 : "+mAuth.getCurrentUser().getEmail());

        Intent intent = getIntent();
        login = intent.getBooleanExtra("LOGIN_ACTIVITY", false);
        if(login) {
            txtcheckemail.setText("서비스를 이용하시려면\n이메일 주소를 인증해주세요.");
        }

        String content = txtcheckemail.getText().toString();
        SpannableString spannableString = new SpannableString(content);
        String word = "이메일";
        int start = content.indexOf(word);
        int end = start + word.length();
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#1abc9c")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtcheckemail.setText(spannableString);

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("메시지", "Email sent.");
                            Toast.makeText(CertifyEmailActivity.this,"Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("메시지", "sendEmailVerification", task.getException());
                            Toast.makeText(CertifyEmailActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public void onBackPressed() {
        mAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();

        super.onBackPressed();
    }
}