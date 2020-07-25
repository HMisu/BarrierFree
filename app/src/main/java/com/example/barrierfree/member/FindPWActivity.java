package com.example.barrierfree.member;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.barrierfree.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class FindPWActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    //FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Button btnSend;
    private TextView editEmail, editName, editBirth;
    private String emailCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);

        mAuth = FirebaseAuth.getInstance();

        btnSend = (Button) findViewById(R.id.btn_send);
        editName = (TextView) findViewById(R.id.edit_name);
        editBirth = (TextView) findViewById(R.id.edit_birth);
        editEmail = (TextView) findViewById(R.id.edit_email);

        btnSend.setOnClickListener(new View.OnClickListener() {
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
                db.collection("members").whereEqualTo("mem_email", editEmail.getText().toString().trim()).whereEqualTo("mem_name",editName.getText().toString().trim()).whereEqualTo("mem_birth",editBirth.getText().toString().trim()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot.isEmpty()) {
                                        Toast.makeText(getApplicationContext(), "존재하지 않는 회원입니다.", Toast.LENGTH_SHORT).show();
                                    }
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        mAuth.sendPasswordResetEmail(editEmail.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(getApplicationContext(), "회원님의 이메일로 비밀번호 재설정 이메일이 발송되었습니다. 이메일을 확인해주세요.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    Log.d("메시지", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });
    }
}