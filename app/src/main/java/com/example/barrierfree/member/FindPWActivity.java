package com.example.barrierfree.member;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.barrierfree.GMailSender;
import com.example.barrierfree.R;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class FindPWActivity extends AppCompatActivity {
    private Button btnauth;
    private TextView editEmail, txtnotice, editName, editBirth;
    private String emailCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);

        btnauth = (Button) findViewById(R.id.btn_email_auth);
        editName = (TextView) findViewById(R.id.edit_name);
        editBirth = (TextView) findViewById(R.id.edit_birth);
        editEmail = (TextView) findViewById(R.id.edit_email);
        txtnotice = (TextView) findViewById(R.id.txt_notice);

        txtnotice.setVisibility(View.GONE);

        btnauth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editName.getText().toString().trim() == "" || editName.getText().toString().trim() == null){
                    Toast.makeText(FindPWActivity.this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if(editBirth.getText().toString().trim() == "" || editBirth.getText().toString().trim() == null){
                    Toast.makeText(FindPWActivity.this, " 생년월일을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if(editEmail.getText().toString().trim() == "" || editEmail.getText().toString().trim() == null){
                    Toast.makeText(FindPWActivity.this, " 이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    GMailSender gMailSender = new GMailSender("bf2020449@gmail.com", "barrierfree2020!");
                    //GMailSender.sendMail(제목, 본문내용, 받는사람);
                    gMailSender.sendMail("[Barrier Free] 임시비밀번호가 발급되었습니다.", "임시비밀번호 : "+gMailSender.getEmailCode(), editEmail.getText().toString().trim());
                    Toast.makeText(getApplicationContext(), "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
                    txtnotice.setVisibility(View.VISIBLE);
                    emailCode = gMailSender.getEmailCode();
                    Log.v("메시지", emailCode);
                } catch (SendFailedException e) {
                    Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                } catch (MessagingException e) {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}