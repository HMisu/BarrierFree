package com.example.barrierfree.ui.member;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.example.barrierfree.MainActivity;
import com.example.barrierfree.R;
import com.example.barrierfree.RoundImageView;
import com.example.barrierfree.SslWebViewConnect;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

public class MemberInfoUpdateFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private View root;
    private FirebaseStorage firebaseStorage;

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
    private final int GET_GALLERY_IMAGE = 200;

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
        firebaseStorage = FirebaseStorage.getInstance();

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

        if(user.getPhotoUrl() != null){
            Glide.with(getContext()).load(user.getPhotoUrl().toString()).into(img);
            img.setRectRadius(100f);
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

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);

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
                                    if (task.isSuccessful()) {
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
                                    else{
                                        Toast.makeText(getActivity(), "비밀번호를 잘못입력하셨습니다.", Toast.LENGTH_SHORT).show();
                                    }
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

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Check which request we're responding to
        if (requestCode == GET_GALLERY_IMAGE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK && data.getData() != null) {
                try {
                    Uri selectedImageUri = data.getData();
                    img.setImageURI(selectedImageUri);
                    Bitmap bitmap = resize(getContext(), selectedImageUri, 150);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] bitdata = baos.toByteArray();

                    //https://blog.naver.com/100race/221901045309
                    final StorageReference storageRef = firebaseStorage.getReference().child("images/" + selectedImageUri.getLastPathSegment());
                    //UploadTask uploadTask = storageRef.putFile(selectedImageUri);

                    UploadTask uploadTask = storageRef.putBytes(bitdata);
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            // Continue with the task to get the download URL
                            return storageRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) { // 다운로드 Url을 받을 수 있는곳
                                Uri imageUrl = task.getResult();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(imageUrl)
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

                                db.collection("members").document(user.getUid())
                                        .update("mem_photo", imageUrl.toString())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("메시지", "DocumentSnapshot successfully updated!");
                                                //refreshFragment();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("메시지", "Error updating document", e);
                                            }
                                        });
                                Toast.makeText(getActivity(),"프로필 사진이 변경되었습니다.",Toast.LENGTH_SHORT).show();
                                ((MainActivity)MainActivity.mContext).onResume();
                            } else {
                                Toast.makeText(getActivity(),"프로필 사진을 변경하지 못했습니다.",Toast.LENGTH_SHORT).show();
                                Log.d("메시지","프로필 이미지 변경 실패");

                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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

    //https://superwony.tistory.com/59
    private Bitmap resize(Context context, Uri uri, int resize){
        Bitmap resizeBitmap=null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); // 1번

            int width = options.outWidth;
            int height = options.outHeight;
            int samplesize = 1;

            while (true) {//2번
                if (width / 2 < resize || height / 2 < resize)
                    break;
                width /= 2;
                height /= 2;
                samplesize *= 2;
            }

            options.inSampleSize = samplesize;
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); //3번
            resizeBitmap=bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return resizeBitmap;
    }
}