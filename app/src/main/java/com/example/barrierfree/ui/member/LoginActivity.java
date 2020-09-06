package com.example.barrierfree.ui.member;

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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = null;
    private static final int RC_SIGN_IN = 900;
    private GoogleSignInClient googleSignInClient;
    private Button btn_login;
    private SignInButton btn_loginGoogle;
    private TextView link_join, link_findpw, link_findEmail, email, pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //파이어베이스 접근 설정
        mAuth = FirebaseAuth.getInstance();

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_loginGoogle = findViewById(R.id.btn_login_google);
        link_join = (TextView) findViewById(R.id.link_join);
        link_findpw = (TextView) findViewById(R.id.link_find_pw);
        link_findEmail = (TextView) findViewById(R.id.link_find_email);
        email = (TextView) findViewById(R.id.edit_email);
        pw = (TextView) findViewById(R.id.edit_pw);

        if (mAuth.getCurrentUser() != null) {
            if(!mAuth.getCurrentUser().isEmailVerified()){
                Intent intent = new Intent(this, CertifyEmailActivity.class);
                intent.putExtra("LOGIN_ACTIVITY",true);
                startActivity(intent);
                finish();
                return;
            }
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().trim() == "" || email.getText().toString().trim() == null){
                    Toast.makeText(LoginActivity.this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if(pw.getText().toString() == "" || pw.getText().toString().trim() == null){
                    Toast.makeText(LoginActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), pw.getText().toString().trim())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user, false);
                                } else {
                                    updateUI(null, false);
                                }
                            }
                        });
            }
        });

        //Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        btn_loginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        link_findEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });

        link_findpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindPWActivity.class);
                startActivity(intent);
                finish();
            }
        });

        link_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
        }
    }

    // 사용자가 정상적으로 로그인한 후에 GoogleSignInAccount 개체에서 ID 토큰을 가져와서
    // Firebase 사용자 인증 정보로 교환하고 Firebase 사용자 인증 정보를 사용해 Firebase에 인증합니다.
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(isNew == true){
                                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                                intent.putExtra("SOCIAL_WHETHER",true);
                                startActivity(intent);
                                finish();
                            } else{
                                updateUI(user, true);
                            }
                        } else {
                            updateUI(null, true);
                        }

                    }
                });
    }

    private void updateUI(FirebaseUser user, boolean social) {
        if (user != null) {
            if(social == false && !user.isEmailVerified()){
                Intent intent = new Intent(this, CertifyEmailActivity.class);
                intent.putExtra("LOGIN_ACTIVITY",true);
                startActivity(intent);
                finish();
                return;
            }
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else{
            Toast.makeText(LoginActivity.this, "로그인을 실패하였습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
