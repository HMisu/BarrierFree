package com.example.barrierfree.member;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.barrierfree.R;

public class FindEmailActivity extends AppCompatActivity {
    private Button btnauth;
    private TextView txtnotice, edit_name, edit_birth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_email);

        btnauth = (Button) findViewById(R.id.btn_email_auth);
        txtnotice = (TextView) findViewById(R.id.txt_notice);
        edit_name = (TextView) findViewById(R.id.edit_name);
        edit_birth = (TextView) findViewById(R.id.edit_birth);

        txtnotice.setVisibility(View.GONE);

        btnauth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit_name.getText().toString().trim() == "" || edit_name.getText().toString().trim() == null){
                    Toast.makeText(FindEmailActivity.this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if(edit_birth.getText().toString().trim() == "" || edit_birth.getText().toString().trim() == null){
                    Toast.makeText(FindEmailActivity.this, " 생년월일을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                txtnotice.setVisibility(View.VISIBLE);
            }
        });
    }
}
