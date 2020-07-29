package com.example.barrierfree.ui.member;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.barrierfree.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindEmailActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Button btnSend;
    private TextView txtnotice, editname, editbirth, editphone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_email);

        btnSend = (Button) findViewById(R.id.btn_send);
        txtnotice = (TextView) findViewById(R.id.txt_notice);
        editname = (TextView) findViewById(R.id.edit_name);
        editbirth = (TextView) findViewById(R.id.edit_birth);
        editphone = (TextView) findViewById(R.id.edit_phone);

        txtnotice.setVisibility(View.GONE);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editname.getText().toString().trim() == "" || editname.getText().toString().trim() == null) {
                    Toast.makeText(FindEmailActivity.this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (editphone.getText().toString().trim() == "" || editphone.getText().toString().trim() == null) {
                    Toast.makeText(FindEmailActivity.this, " 전화번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (editbirth.getText().toString().trim() == "" || editbirth.getText().toString().trim() == null) {
                    Toast.makeText(FindEmailActivity.this, " 생년월일 6자리를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                db.collection("members").whereEqualTo("mem_name", editname.getText().toString().trim()).whereEqualTo("mem_phone", editphone.getText().toString().trim()).whereEqualTo("mem_birth", editbirth.getText().toString().trim()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot.isEmpty()) {
                                        Toast.makeText(getApplicationContext(), "존재하지 않는 회원입니다.", Toast.LENGTH_SHORT).show();
                                    }
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("메시지", document.getId() + " => " + document.getData());
                                        Map<String, Object> member = document.getData();
                                        String email = getMaskedEmail(member.get("mem_email").toString());
                                        txtnotice.setText(email);
                                        txtnotice.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    Log.d("메시지", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });
    }

    private String getMaskedEmail(String email) {
        String regex = "\\b(\\S+)+@(\\S+.\\S+)";
        Matcher matcher = Pattern.compile(regex).matcher(email);
        if (matcher.find()) {
            String id = matcher.group(1);
            int length = id.length();
            if (length < 3) {
                char[] c = new char[length];
                Arrays.fill(c, '*');
                return email.replace(id, String.valueOf(c));
            } else if (length == 3) {
                return email.replaceAll("\\b(\\S+)[^@][^@]+@(\\S+)", "$1**@$2");
            } else {
                return email.replaceAll("\\b(\\S+)[^@][^@][^@]+@(\\S+)", "$1***@$2");
            }
        }
        return email;
    }
}
