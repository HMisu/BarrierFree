package com.example.barrierfree.ui.member;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.barrierfree.MainActivity;
import com.example.barrierfree.R;
import com.example.barrierfree.SslWebViewConnect;
import com.example.barrierfree.models.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private Button btnauth, btnjoin, btnaddr;
    private TextView txtpw, txtemail, editname, social_mail, editbirth, editemail, editpw, checkpw, editphone, editaddr, editaddrdetail;
    private CheckBox chk1, chk2;
    private boolean social, chkemail, a;

    private WebView daum_webView;
    private TextView daum_result;
    private Handler handler;
    private Dialog dialog;

    //이메일 인증
    private TextView textView = null;
    private TextView message = null;
    private String emailCode = "", name = "", birth = "", email = "", pw = "", chkpw = "", phone = "", addr1 = "", addr2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        daum_result = (TextView) findViewById(R.id.daum_result);
        daum_webView = (WebView) findViewById(R.id.daum_webview);

        btnauth = (Button) findViewById(R.id.btn_chk_email);
        btnaddr = (Button) findViewById(R.id.btn_addr);
        btnjoin = (Button) findViewById(R.id.btn_join);

        txtpw = (TextView) findViewById(R.id.txt_check_pw);
        txtemail = (TextView) findViewById(R.id.txt_check_email);

        editname = (TextView) findViewById(R.id.edit_name);
        social_mail = (TextView) findViewById(R.id.social_mail);
        editbirth = (TextView) findViewById(R.id.edit_birth);
        editemail = (TextView) findViewById(R.id.edit_email);
        editpw = (TextView) findViewById(R.id.edit_pw);
        checkpw = (TextView) findViewById(R.id.check_pw);
        editphone = (TextView) findViewById(R.id.edit_phone);
        editaddr = (TextView) findViewById(R.id.edit_addr);
        editaddrdetail = (TextView) findViewById(R.id.edit_addr_detail);

        chk1 = (CheckBox) findViewById(R.id.chk_join1);
        chk2 = (CheckBox) findViewById(R.id.chk_join2);

        daum_result.setVisibility(View.GONE);
        daum_webView.setVisibility(View.GONE);
        social_mail.setVisibility(View.GONE);

        Intent intent = getIntent();

        social = intent.getBooleanExtra("SOCIAL_WHETHER", false);
        if (social) {
            user = mAuth.getCurrentUser();
            editname.setText(user.getDisplayName());
            social_mail.setText(user.getEmail());
            social_mail.setEnabled(false);
            social_mail.setVisibility(View.VISIBLE);
            editemail.setVisibility(View.GONE);
            btnauth.setVisibility(View.GONE);
            editpw.setVisibility(View.GONE);
            checkpw.setVisibility(View.GONE);
        }

        //이메일 발송 관련
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        btnauth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editemail.getText().toString().trim() == null || editemail.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                Pattern p = Pattern.compile("^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$");
                Matcher m = p.matcher(editemail.getText().toString().trim());
                if (!m.matches()) {
                    Toast.makeText(getApplicationContext(), "이메일 형식에 맞지 않습니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                db.collection("members").whereEqualTo("mem_email", editemail.getText().toString().trim()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot.isEmpty()) {
                                        a=true;
                                        Log.d("메시지", "a : "+String.valueOf(a));
                                    } else{
                                        a=false;
                                    }
                                } else {
                                    Log.d("메시지", "Error getting documents: ", task.getException());
                                    return;
                                }
                                chkemail=a;
                            }
                        });

                (new Handler()).postDelayed(new Runnable() {
                    public void run() {
                        if(chkemail == true)
                            Toast.makeText(getApplicationContext(), "사용가능한 이메일입니다", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), "사용할 수 없는 이메일입니다", Toast.LENGTH_SHORT).show();
                        Log.d("메시지", "chkemail : "+String.valueOf(chkemail));
                    }
                }, 700);
            }
        });

        editpw.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String regex_pw = "^[A-Za-z0-9]{7,}$";
                if(!editpw.getText().toString().trim().matches(regex_pw)) {
                    txtpw.setText("최소 7자, 최소 하나의 영문자 및 하나의 숫자로 구성하세요");
                    txtpw.setVisibility(View.VISIBLE);
                } else {
                    txtpw.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        checkpw.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!editpw.getText().toString().trim().equals(checkpw.getText().toString().trim())) {
                    txtpw.setText("비밀번호가 불일치합니다");
                    txtpw.setVisibility(View.VISIBLE);
                } else {
                    txtpw.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        btnaddr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daum_webView.setVisibility(View.VISIBLE);
                // WebView 초기화
                init_webView();
                // 핸들러를 통한 JavaScript 이벤트 반응
                handler = new Handler();
            }
        });

        btnjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = editname.getText().toString().trim();
                birth = editbirth.getText().toString().trim();
                email = editemail.getText().toString().trim();
                pw = editpw.getText().toString().trim();
                chkpw = checkpw.getText().toString().trim();
                phone = editphone.getText().toString().trim();
                addr1 = editaddr.getText().toString().trim();
                addr2 = editaddrdetail.getText().toString().trim();

                if (!chk1.isChecked() || !chk2.isChecked()) {
                    Toast.makeText(getApplicationContext(), "회원가입을 하시려면 위 사항들에 동의해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (social) {
                    if (birth.equals("") || birth == null || phone == null || addr1.equals("") || addr1 == null) {
                        Toast.makeText(getApplicationContext(), "입력하지 않은 항목이 있습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String uid = user.getUid();
                    email = user.getEmail();
                    String photo = user.getPhotoUrl().toString();
                    Member member = new Member(uid, name, birth, email, phone, addr1, addr2, photo);
                    insertMemeber(member, uid);
                } else {
                    if (chkemail == false) {
                        Toast.makeText(getApplicationContext(), "이메일의 중복 여부를 확인해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String regex_pw = "^[A-Za-z0-9]{7,}$";
                    if(editpw.getText().toString().trim().matches(regex_pw) == false) {
                        Toast.makeText(getApplicationContext(), "최소 7자, 최소 하나의 영문자 및 하나의 숫자로 구성하세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (name.equals("") || name == null || birth.equals("") || birth == null || email.equals("") || email == null || pw.equals("") || pw == null || chkpw.equals("") || chkpw == null || phone.equals("") || phone == null || addr1.equals("") || addr1 == null) {
                        Toast.makeText(getApplicationContext(), "입력하지 않은 항목이 있습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(JoinActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                user = mAuth.getCurrentUser();
                                email = user.getEmail();
                                String photo=null;
                                if(user.getPhotoUrl() != null){
                                    photo = user.getPhotoUrl().toString();
                                }
                                String uid = user.getUid();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("메시지", "User profile updated.");
                                                }
                                            }
                                        });
                                Member member = new Member(uid, name, birth, email, phone, addr1, addr2, photo);
                                insertMemeber(member, uid);
                                mAuth.useAppLanguage();
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("메시지", "Email sent.");
                                            Toast.makeText(JoinActivity.this, "인증 메일이 발송되었습니다." + user.getEmail(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.e("메시지", "sendEmailVerification", task.getException());
                                            Toast.makeText(JoinActivity.this, "인증 메일 발송에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    Toast.makeText(JoinActivity.this, "비밀번호를 7자리 이상 입력해주세요", Toast.LENGTH_SHORT).show();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    Toast.makeText(JoinActivity.this, "이메일 형식에 맞지 않습니다", Toast.LENGTH_SHORT).show();
                                } catch (FirebaseAuthUserCollisionException e) {
                                    Toast.makeText(JoinActivity.this, "이미 존재하는 이메일입니다", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(JoinActivity.this, "Exception", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (social) {
            mAuth.getCurrentUser().delete();
            mAuth.getInstance().signOut();
        }
        super.onBackPressed();
    }

    public void init_webView() {
        // JavaScript 허용
        daum_webView.getSettings().setJavaScriptEnabled(true);
        daum_webView.getSettings().setSupportMultipleWindows(true);
        daum_webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // webview url load. php 파일 주소
        daum_webView.loadUrl("https://barrierfree-b7959.web.app");
        daum_webView.setWebViewClient(new SslWebViewConnect());

        // JavaScript의 window.open 허용
        daum_webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        daum_webView.addJavascriptInterface(new JoinActivity.AndroidBridge(), "TestApp");
        // web client를 chrome으로 설정
        daum_webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                daum_webView = new WebView(JoinActivity.this);
                WebSettings webSettings = daum_webView.getSettings();
                webSettings.setJavaScriptEnabled(true);

                dialog = new Dialog(JoinActivity.this);
                dialog.setContentView(daum_webView);

                ViewGroup.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
                dialog.show();
                daum_webView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onCloseWindow(WebView window) {
                        dialog.dismiss();
                    }
                });

                ((WebView.WebViewTransport) resultMsg.obj).setWebView(daum_webView);
                resultMsg.sendToTarget();
                return true;
            }
        });
    }

    private void insertMemeber(Member member, String uid) { //update ui code here
        db.collection("members").document(uid)
                .set(member)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("메시지", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("메시지", "Error writing document", e);
                    }
                });
        Toast.makeText(JoinActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
        if (social) {
            Intent intent = new Intent(JoinActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(JoinActivity.this, CertifyEmailActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    editaddr.setText(String.format("%s %s", arg2, arg3));
                    dialog.dismiss();
                    daum_webView.setVisibility(View.GONE);
                }
            });
        }
    }

}