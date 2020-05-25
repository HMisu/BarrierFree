package com.example.barrierfree.member;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.example.barrierfree.R;
import com.example.barrierfree.SslWebViewConnect;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class JoinActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    private Button btnauth, btnjoin, btnaddr;
    private TextView txtemail, txtauth, txtpw, editname, editbirth, editemail, editpw, checkpw, editphone, editauth, editaddr, editaddrdetail;
    private CheckBox chk1, chk2;

    private WebView daum_webView;
    private TextView daum_result;
    private Handler handler;
    private Dialog dialog;

    //이메일 인증
    private TextView textView = null;
    private TextView message = null;
    private String emailCode = "", name = "", birth = "", email = "", auth = "", pw = "", chkpw = "", phone = "", addr1 = "", addr2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        //파이어베이스 접근 설정
        firebaseAuth = FirebaseAuth.getInstance();

        daum_result = (TextView) findViewById(R.id.daum_result);
        daum_webView = (WebView) findViewById(R.id.daum_webview);

        btnauth = (Button) findViewById(R.id.btn_email_auth);
        btnaddr = (Button) findViewById(R.id.btn_addr);
        btnjoin = (Button) findViewById(R.id.btn_join);

        txtemail = (TextView) findViewById(R.id.txt_check_email);
        txtauth = (TextView) findViewById(R.id.txt_check_auth);
        txtpw = (TextView) findViewById(R.id.txt_check_pw);

        editname = (TextView) findViewById(R.id.edit_name);
        editbirth = (TextView) findViewById(R.id.edit_birth);
        editemail = (TextView) findViewById(R.id.edit_email);
        editauth = (TextView) findViewById(R.id.edit_email_auth);
        editpw = (TextView) findViewById(R.id.edit_pw);
        checkpw = (TextView) findViewById(R.id.check_pw);
        editphone = (TextView) findViewById(R.id.edit_phone);
        editaddr = (TextView) findViewById(R.id.edit_addr);
        editaddrdetail = (TextView) findViewById(R.id.edit_addr_detail);

        chk1 = (CheckBox) findViewById(R.id.chk_join1);
        chk2 = (CheckBox) findViewById(R.id.chk_join2);

        daum_result.setVisibility(View.GONE);
        daum_webView.setVisibility(View.GONE);
        txtemail.setVisibility(View.GONE);
        txtauth.setVisibility(View.GONE);
        txtpw.setVisibility(View.GONE);
        editauth.setVisibility(View.GONE);

        //이메일 발송 관련
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        btnauth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtemail.setVisibility(v.VISIBLE);
                editauth.setVisibility(v.VISIBLE);

                if(editemail.getText().toString().trim() == null || editemail.getText().toString().trim().equals("")) {
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(editemail.getText().toString().trim(), "abc").addOnCompleteListener(JoinActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //가입 성공시
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(JoinActivity.this,"이메일 형식에 맞지 않습니다" ,Toast.LENGTH_SHORT).show();
                            } catch(FirebaseAuthUserCollisionException e) {
                                Toast.makeText(JoinActivity.this,"이미 존재하는 이메일입니다" ,Toast.LENGTH_SHORT).show();
                            } catch(Exception e) {
                                Toast.makeText(JoinActivity.this,"다시 확인해주세요" ,Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }
                    }
                });

                try {
                    GMailSender gMailSender = new GMailSender("bf2020449@gmail.com", "barrierfree2020!");
                    //GMailSender.sendMail(제목, 본문내용, 받는사람);
                    gMailSender.sendMail("[Barrier Free] 이메일 인증을 위한 인증번호가 발급되었습니다.", "이메일 인증 번호 : " + gMailSender.getEmailCode(), editemail.getText().toString().trim());
                    Toast.makeText(getApplicationContext(), "인증 메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
                    emailCode = gMailSender.getEmailCode();
                } catch (SendFailedException e) {
                    Toast.makeText(getApplicationContext(), "이메일 형식에 맞지 않습니다", Toast.LENGTH_SHORT).show();
                } catch (MessagingException e) {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        editauth.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtauth.setVisibility(View.VISIBLE);
                if (emailCode.equals(editauth.getText().toString())) {
                    txtauth.setVisibility(View.GONE);
                } else {
                    txtauth.setText("인증번호가 불일치합니다");
                }
            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        editpw.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editpw.getText().toString().trim().length() <= 6) {
                    txtpw.setText("비밀번호를 7자리 이상 입력해주세요");
                    txtpw.setVisibility(View.VISIBLE);
                } else{
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

                if (editpw.getText().toString().length() <= 6) {
                    txtpw.setText("비밀번호를 7자리 이상 입력해주세요");
                    txtpw.setVisibility(View.VISIBLE);
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
                //Intent intent = new Intent(getApplicationContext(), SearchAddress.class);
                //startActivity(intent);

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
                auth = editauth.getText().toString().trim();
                pw = editpw.getText().toString().trim();
                chkpw = checkpw.getText().toString().trim();
                phone = editphone.getText().toString().trim();
                addr1 = editaddr.getText().toString().trim();
                addr2 = editaddrdetail.getText().toString().trim();

                if (editpw.getText().toString().length() <= 6) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 7자리 이상 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (name.equals("") || name == null || birth.equals("") || birth == null || email.equals("") || email == null || auth.equals("") || auth == null || pw.equals("") || pw == null || chkpw.equals("") || chkpw == null || phone.equals("") || phone == null || addr1.equals("") || addr1 == null) {
                    Toast.makeText(getApplicationContext(), "입력하지 않은 항목이 있습니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!chk1.isChecked() || !chk2.isChecked()) {
                    Toast.makeText(getApplicationContext(), "회원가입을 하시려면 위 사항들에 동의해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                //파이어베이스에 신규계정 등록하기
                firebaseAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(JoinActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //가입 성공시
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            email = user.getEmail();
                            String uid = user.getUid();

                            Member member = new Member(uid, name, birth, email, phone, addr1, addr2);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference("Member");
                            reference.child(uid).setValue(member);

                            Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(JoinActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                Toast.makeText(JoinActivity.this,"비밀번호를 7자리 이상 입력해주세요" ,Toast.LENGTH_SHORT).show();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(JoinActivity.this,"이메일 형식에 맞지 않습니다" ,Toast.LENGTH_SHORT).show();
                            } catch(FirebaseAuthUserCollisionException e) {
                                Toast.makeText(JoinActivity.this,"이미 존재하는 이메일입니다" ,Toast.LENGTH_SHORT).show();
                            } catch(Exception e) {
                                Toast.makeText(JoinActivity.this,"Exception" ,Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
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
        // web client 를 chrome 으로 설정
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