package com.example.barrierfree.member;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.barrierfree.R;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class FindPWActivity extends AppCompatActivity {
    private Button btnauth;
    private TextView editemail, txtnotice;
    private String emailCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);

        btnauth = (Button) findViewById(R.id.btn_email_auth);
        editemail = (TextView) findViewById(R.id.edit_email);
        txtnotice = (TextView) findViewById(R.id.txt_notice);

        txtnotice.setVisibility(View.GONE);

        btnauth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    GMailSender gMailSender = new GMailSender("bf2020449@gmail.com", "barrierfree2020!");
                    //GMailSender.sendMail(제목, 본문내용, 받는사람);
                    gMailSender.sendMail("[Barrier Free] 임시비밀번호가 발급되었습니다.", "임시비밀번호 : "+gMailSender.getEmailCode(), editemail.getText().toString());
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