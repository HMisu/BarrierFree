package com.example.barrierfree.member;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.barrierfree.R;
import com.example.barrierfree.SslWebViewConnect;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class JoinActivity extends AppCompatActivity {
    private Button btnauth, btnjoin, btnaddr;
    private TextView txtemail, txtauth, txtpw, editname, editemail, editpw, checkpw, editphone, editauth, editaddr, editaddrdetail;

    private WebView daum_webView;
    private TextView daum_result;
    private Handler handler;
    private Dialog dialog;

    //이메일 인증
    private TextView textView = null;
    private TextView message = null;
    private String emailCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();
        user.put("mem_name", "Alan");
        user.put("mem_phone", "Mathison");
        user.put("mem_email", "Turing");
        user.put("mem_pw", 1912);


        daum_result = (TextView) findViewById(R.id.daum_result);
        daum_webView = (WebView) findViewById(R.id.daum_webview);

        btnauth = (Button) findViewById(R.id.btn_email_auth);
        btnaddr = (Button) findViewById(R.id.btn_addr);
        btnjoin = (Button) findViewById(R.id.btn_join);

        txtemail = (TextView) findViewById(R.id.txt_check_email);
        txtauth = (TextView) findViewById(R.id.txt_check_auth);
        txtpw = (TextView) findViewById(R.id.txt_check_pw);

        editname = (TextView) findViewById(R.id.edit_name);
        editemail = (TextView) findViewById(R.id.edit_email);
        editauth = (TextView) findViewById(R.id.edit_email_auth);
        editpw = (TextView) findViewById(R.id.edit_pw_join);
        checkpw = (TextView) findViewById(R.id.check_pw);
        editphone = (TextView) findViewById(R.id.edit_phone);
        editaddr = (TextView) findViewById(R.id.edit_addr);
        editaddrdetail = (TextView) findViewById(R.id.edit_addr_detail);

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
                try {
                    GMailSender gMailSender = new GMailSender("bf2020449@gmail.com", "barrierfree2020!");
                    //GMailSender.sendMail(제목, 본문내용, 받는사람);
                    gMailSender.sendMail("[Barrier Free] 이메일 인증을 위한 인증번호가 발급되었습니다.", "이메일 인증 번호 : "+gMailSender.getEmailCode(), editemail.getText().toString());
                    Toast.makeText(getApplicationContext(), "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
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

        editauth.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtauth.setVisibility(View.VISIBLE);
                if (emailCode.equals(editauth.getText().toString())) {
                    txtauth.setText("인증번호가 일치합니다");
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

        checkpw.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtpw.setVisibility(View.VISIBLE);
                if (editpw.getText().toString().equals(checkpw.getText().toString())) {
                    txtpw.setText("비밀번호가 일치합니다");
                } else {
                    txtpw.setText("비밀번호가 불일치합니다");
                }
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                txtpw.setVisibility(View.VISIBLE);
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
                String name = editname.getText().toString();
                String email = editemail.getText().toString();
                String auth = editauth.getText().toString();
                String pw = editpw.getText().toString();
                String chkpw = checkpw.getText().toString();
                String phone = editphone.getText().toString();
                String addr = editaddr.getText().toString();
                String addrdetail = editaddrdetail.getText().toString();
                //Log.v("메시지",email);

                if (name.equals("") || name == null || email.equals("") || email == null || auth.equals("") || auth == null || pw.equals("") || pw == null || chkpw.equals("") || chkpw == null || phone.equals("") || phone == null || addr.equals("") || addr == null || addrdetail.equals("") || addrdetail == null) {
                    Toast.makeText(getApplicationContext(), "입력하지 않은 항목이 있습니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "가입 완료", Toast.LENGTH_SHORT).show();
                finish();

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
                    //daum_result.setText(String.format("(%s) %s %s", arg1, arg2, arg3));
                    editaddr.setText(String.format("%s %s", arg2, arg3));
                    // WebView를 초기화 하지않으면 재사용할 수 없음
                    //init_webView();
                    //Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                    //intent.putExtra("val",daum_result.getText());
                    //startActivity(intent);
                    dialog.dismiss();
                    daum_webView.setVisibility(View.GONE);
                    //finish();
                }
            });
        }
    }
}
