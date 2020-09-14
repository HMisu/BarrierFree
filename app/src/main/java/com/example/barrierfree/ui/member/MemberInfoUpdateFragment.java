package com.example.barrierfree.ui.member;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.barrierfree.R;
import com.example.barrierfree.RoundImageView;
import com.example.barrierfree.SslWebViewConnect;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MemberInfoUpdateFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private View root;

    Bitmap bitmap;
    private boolean chkemail, a;

    private View dividing_line;
    private Button btnauth, btnaddr, btnupdate;
    private RoundImageView img;
    private TextView txtname, txtemail, titlepw, txtpw, txtbirth, editaddr, editaddrdetail, editphone, editemail, editpw;

    private WebView daum_webView;
    private TextView daum_result;
    private Handler handler;
    private Dialog dialog;

    private String email, phone, addr1, addr2, providerId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_member_info, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        daum_result = (TextView) root.findViewById(R.id.daum_result);
        daum_webView = (WebView) root.findViewById(R.id.daum_webview);

        btnauth = (Button) root.findViewById(R.id.btn_chk_email);
        btnaddr = (Button) root.findViewById(R.id.btn_addr);
        btnupdate = (Button) root.findViewById(R.id.btn_update);

        img = (RoundImageView) root.findViewById(R.id.img_user);
        txtname = (TextView) root.findViewById(R.id.txt_name);
        txtbirth = (TextView) root.findViewById(R.id.txt_birth);
        txtemail = (TextView) root.findViewById(R.id.txt_check_email);
        txtpw = (TextView) root.findViewById(R.id.txt_pw);
        editaddr = (TextView) root.findViewById(R.id.edit_addr);
        editaddrdetail = (TextView) root.findViewById(R.id.edit_addr_detail);
        editphone = (TextView) root.findViewById(R.id.edit_phone);
        editemail = (TextView) root.findViewById(R.id.edit_email);
        editpw = (TextView) root.findViewById(R.id.edit_pw);
        titlepw = (TextView) root.findViewById(R.id.title_pw);
        dividing_line = (View) root.findViewById(R.id.dividing_line);

        daum_result.setVisibility(View.GONE);
        daum_webView.setVisibility(View.GONE);
        //editpw.setVisibility(View.INVISIBLE);
        //txtpw.setVisibility(View.INVISIBLE);
        //titlepw.setVisibility(View.INVISIBLE);

        for (UserInfo profile : user.getProviderData()) {
            // Id of the provider (ex: google.com)
            providerId = profile.getProviderId();
        }

        if (providerId.equals("google.com")) {
            editemail.setFocusable(false);
            editemail.setClickable(false);
            dividing_line.setVisibility(View.INVISIBLE);
            btnauth.setVisibility(View.VISIBLE);
            btnauth.setEnabled(false);
            //btnauth.setBackgroundColor(R.drawable.btn_red);
            txtemail.setText("구글 로그인 시 이메일 수정이 불가능합니다");
        }

        Thread uThread = new Thread() {
            @Override
            public void run() {
                try {
                    if (user.getPhotoUrl() == null)
                        return;
                    URL url = new URL(user.getPhotoUrl().toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        if (user.getPhotoUrl() != null) {
            uThread.start();
            try {
                uThread.join();
                img.setImageBitmap(bitmap);
                img.setRectRadius(100f);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            img.setImageResource(R.drawable.ic_defaultuser);
        }

        txtname.setText(user.getDisplayName());
        editemail.setText(user.getEmail());

        db.collection("members").whereEqualTo("uid", user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                editaddr.setText(document.getString("mem_addr1"));
                                editaddrdetail.setText(document.getString("mem_addr2"));
                                editphone.setText(document.getString("mem_phone"));
                                txtbirth.setText(document.getString("mem_birth"));
                            }
                        } else {
                            Log.d("메시지", "Error getting documents: ", task.getException());
                            return;
                        }
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

        btnauth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editemail.getText().toString().trim() == null || editemail.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                Pattern p = Pattern.compile("^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$");
                Matcher m = p.matcher(editemail.getText().toString().trim());
                if (!m.matches()) {
                    Toast.makeText(getActivity(), "이메일 형식에 맞지 않습니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                db.collection("members").whereEqualTo("mem_email", editemail.getText().toString().trim()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot.isEmpty()) {
                                        a = true;
                                        Log.d("메시지", "a : " + String.valueOf(a));
                                    } else {
                                        a = false;
                                    }
                                } else {
                                    Log.d("메시지", "Error getting documents: ", task.getException());
                                    return;
                                }
                                chkemail = a;
                            }
                        });

                (new Handler()).postDelayed(new Runnable() {
                    public void run() {
                        if (chkemail == true)
                            Toast.makeText(getActivity(), "사용가능한 이메일입니다", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getActivity(), "사용할 수 없는 이메일입니다", Toast.LENGTH_SHORT).show();
                        Log.d("메시지", "chkemail : " + String.valueOf(chkemail));
                    }
                }, 600);
            }
        });

        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = editemail.getText().toString().trim();
                phone = editphone.getText().toString().trim();
                addr1 = editaddr.getText().toString().trim();
                addr2 = editaddrdetail.getText().toString().trim();

                if (!providerId.equals("google.com") && (editpw.getText().toString().equals("") || editpw.getText().toString() == null)) {
                    Toast.makeText(getActivity(), "비밀번호 확인이 필요합니다. 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!providerId.equals("google.com") && !user.getEmail().equals(email) && chkemail == false) {
                    Toast.makeText(getActivity(), "이메일의 중복 여부를 확인해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!providerId.equals("google.com") && !user.getEmail().equals(email)) {
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), editpw.getText().toString().trim());
                    // Prompt the user to re-provide their sign-in credentials
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("메시지", "User re-authenticated.");
                                    user.updateEmail(email)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("메시지", "User email address updated.");
                                                    }
                                                }
                                            });
                                }
                            });
                }

                db.collection("members").document(user.getUid())
                        .update("mem_email", email, "mem_phone", phone, "mem_addr1", addr1, "mem_addr2", addr2)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("메시지", "DocumentSnapshot successfully updated!");
                                refreshFragment();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("메시지", "Error updating document", e);
                            }
                        });
            }
        });

        return root;
    }

    public void refreshFragment() {
        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
        t.detach(this).attach(this).commitAllowingStateLoss();
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
        daum_webView.addJavascriptInterface(new MemberInfoUpdateFragment.AndroidBridge(), "TestApp");
        // web client를 chrome으로 설정
        daum_webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                daum_webView = new WebView(getContext());
                //daum_webView = new WebView(MemberInfoUpdateFragment.this)
                WebSettings webSettings = daum_webView.getSettings();
                webSettings.setJavaScriptEnabled(true);

                dialog = new Dialog(getContext());
                //dialog = new Dialog(MemberInfoUpdateFragment.this);
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
