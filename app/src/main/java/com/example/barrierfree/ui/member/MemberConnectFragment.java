package com.example.barrierfree.ui.member;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.barrierfree.R;
import com.example.barrierfree.RoundImageView;
import com.example.barrierfree.models.ListViewMember;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MemberConnectFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private View root;
    private ListView lvrequest, lvapply, lvsearch;
    private ListViewMemberAdpater adprequest, adpapply, adpsearch;

    private RoundImageView imgweak, imgprotect;
    private TextView editsearch, txtconnect, txtweak, txtprotect, txtweakname, txtprotectname;
    private Button btnsearch, btndelete;

    Boolean boolimg = false;
    Bitmap bitmap;
    String uid2 = null, uid = null, cn_id = null, cn_id2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_member_connect, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        imgweak = (RoundImageView) root.findViewById(R.id.weak_photo);
        imgprotect = (RoundImageView) root.findViewById(R.id.protector_photo);
        txtconnect = (TextView) root.findViewById(R.id.txt_connect);
        txtweak = (TextView) root.findViewById(R.id.text1);
        txtprotect = (TextView) root.findViewById(R.id.text2);
        txtweakname = (TextView) root.findViewById(R.id.weak_name);
        txtprotectname = (TextView) root.findViewById(R.id.protector_name);
        editsearch = (TextView) root.findViewById(R.id.edit_search);
        btnsearch = (Button) root.findViewById(R.id.btn_search_mem);
        btndelete = (Button) root.findViewById(R.id.btn_delete);

        txtconnect.setVisibility(root.VISIBLE);
        imgweak.setVisibility(root.INVISIBLE);
        imgprotect.setVisibility(root.INVISIBLE);
        txtweak.setVisibility(root.INVISIBLE);
        txtprotect.setVisibility(root.INVISIBLE);
        btndelete.setVisibility(root.INVISIBLE);

        // Adapter 생성
        adprequest = new ListViewMemberAdpater(getActivity(), MemberConnectFragment.this);
        adpapply = new ListViewMemberAdpater(getActivity(), MemberConnectFragment.this);
        adpsearch = new ListViewMemberAdpater(getActivity(), MemberConnectFragment.this);

        lvrequest = (ListView) root.findViewById(R.id.listview_request_user);
        lvrequest.setAdapter(adprequest);
        lvapply = (ListView) root.findViewById(R.id.listview_apply_user);
        lvapply.setAdapter(adpapply);
        lvsearch = (ListView) root.findViewById(R.id.listview_search_user);
        lvsearch.setAdapter(adpsearch);

        db.collection("connection").whereEqualTo("connect", true).whereEqualTo("mem_weak", user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                txtconnect.setVisibility(root.INVISIBLE);
                                imgweak.setVisibility(root.VISIBLE);
                                imgprotect.setVisibility(root.VISIBLE);
                                txtweak.setVisibility(root.VISIBLE);
                                txtprotect.setVisibility(root.VISIBLE);
                                btndelete.setVisibility(root.VISIBLE);

                                boolimg = true;
                                txtweakname.setText(user.getDisplayName());
                                uid2 = document.getString("mem_protect");
                                cn_id = document.getId();
                                if(user.getPhotoUrl() != null){
                                    Glide.with(getContext()).load(user.getPhotoUrl().toString()).into(imgweak);
                                    imgweak.setRectRadius(100f);
                                }
                            }
                            db.collection("members").whereEqualTo("uid", uid2).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (final QueryDocumentSnapshot document : task.getResult()) {
                                                    if (boolimg == false)
                                                        txtweakname.setText(document.getString("mem_name"));
                                                    else
                                                        txtprotectname.setText(document.getString("mem_name"));
                                                    if (document.getString("mem_photo") != null) {
                                                        if (boolimg == false) {
                                                            Glide.with(getContext()).load(document.getString("mem_photo")).into(imgweak);
                                                            imgweak.setRectRadius(100f);
                                                        } else {
                                                            Glide.with(getContext()).load(document.getString("mem_photo")).into(imgprotect);
                                                            imgprotect.setRectRadius(100f);
                                                        }
                                                    }
                                                }
                                            } else {
                                                Log.d("메시지", "Error getting documents: ", task.getException());
                                                return;
                                            }
                                        }
                                    });
                        } else {
                            Log.d("메시지", "Error getting documents: ", task.getException());
                            return;
                        }
                    }
                });

        db.collection("connection").whereEqualTo("connect", true).whereEqualTo("mem_protect", user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                txtconnect.setVisibility(root.INVISIBLE);
                                imgweak.setVisibility(root.VISIBLE);
                                imgprotect.setVisibility(root.VISIBLE);
                                txtweak.setVisibility(root.VISIBLE);
                                txtprotect.setVisibility(root.VISIBLE);
                                btndelete.setVisibility(root.VISIBLE);

                                boolimg = false;
                                uid2 = document.getString("mem_weak");
                                cn_id = document.getId();
                                txtprotectname.setText(user.getDisplayName());
                                if(user.getPhotoUrl() != null){
                                    Glide.with(getContext()).load(user.getPhotoUrl().toString()).into(imgprotect);
                                    imgprotect.setRectRadius(100f);
                                }
                            }
                            db.collection("members").whereEqualTo("uid", uid2).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (final QueryDocumentSnapshot document : task.getResult()) {
                                                    if (boolimg == false)
                                                        txtweakname.setText(document.getString("mem_name"));
                                                    else
                                                        txtprotectname.setText(document.getString("mem_name"));
                                                    if (document.getString("mem_photo") != null) {
                                                        if (boolimg == false) {
                                                            Glide.with(getContext()).load(document.getString("mem_photo")).into(imgweak);
                                                            imgweak.setRectRadius(100f);
                                                        } else {
                                                            Glide.with(getContext()).load(document.getString("mem_photo")).into(imgprotect);
                                                            imgprotect.setRectRadius(100f);
                                                        }
                                                    }
                                                }
                                            } else {
                                                Log.d("메시지", "Error getting documents: ", task.getException());
                                                return;
                                            }
                                        }
                                    });
                        } else {
                            Log.d("메시지", "Error getting documents: ", task.getException());
                            return;
                        }
                    }
                });

        db.collection("connection").whereEqualTo("mem_applicant", user.getUid()).whereEqualTo("connect", false).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getString("mem_weak").equals(user.getUid())) {
                                    uid = document.getString("mem_protect");
                                    cn_id = document.getId();
                                } else {
                                    uid = document.getString("mem_weak");
                                    cn_id = document.getId();
                                }
                                db.collection("members").whereEqualTo("uid", uid).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        ListViewMember mem = new ListViewMember(document.getString("uid"), document.getString("mem_name"), document.getString("mem_email"), document.getString("mem_photo"), user.getUid(), "adpapply", cn_id);
                                                        adpapply.add(mem);
                                                        adpapply.notifyDataSetChanged();
                                                    }
                                                } else {
                                                    Log.d("메시지", "Error getting documents: ", task.getException());
                                                    return;
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.d("메시지", "Error getting documents: ", task.getException());
                            return;
                        }
                    }
                });

        db.collection("connection").whereEqualTo("mem_respondent", user.getUid()).whereEqualTo("connect", false).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getString("mem_weak").equals(user.getUid())) {
                                    uid = document.getString("mem_protect");
                                    cn_id = document.getId();
                                } else {
                                    uid = document.getString("mem_weak");
                                    cn_id = document.getId();
                                }
                                db.collection("members").whereEqualTo("uid", uid).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        ListViewMember mem = new ListViewMember(document.getString("uid"), document.getString("mem_name"), document.getString("mem_email"), document.getString("mem_photo"), document.getString("uid"), "adprequest", cn_id);
                                                        adprequest.add(mem);
                                                        adprequest.notifyDataSetChanged();
                                                    }
                                                } else {
                                                    Log.d("메시지", "Error getting documents: ", task.getException());
                                                    return;
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.d("메시지", "Error getting documents: ", task.getException());
                            return;
                        }
                    }
                });

        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = editsearch.getText().toString().trim();
                String regex_num = "^[0-9]+$";
                String regex_email = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
                String regex_phone = "^01(?:0|1|[6-9])[-]?(\\d{3}|\\d{4})[-]?(\\d{4})$";

                if (adpsearch.getCount() > 0) {
                    adpsearch.clear();
                    adpsearch.notifyDataSetChanged();
                }

                if (search.matches(regex_email) == true) {
                    db.collection("members").whereEqualTo("mem_email", search).limit(1).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (querySnapshot.isEmpty()) {
                                            Toast.makeText(getActivity(), "입력하신 이메일을 가진 회원이 없습니다. 다시 입력하세요", Toast.LENGTH_SHORT).show();
                                        }
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (user.getUid().equals(document.getString("uid"))) {
                                                Toast.makeText(getActivity(), "회원 본인입니다", Toast.LENGTH_SHORT).show();
                                            } else if (uid2 != null) {
                                                if(uid2.equals(document.getString("uid")))
                                                    Toast.makeText(getActivity(), "이미 연결된 계정입니다", Toast.LENGTH_SHORT).show();
                                            } else {
                                                ListViewMember mem = new ListViewMember(document.getString("uid"), document.getString("mem_name"), document.getString("mem_email"), document.getString("mem_photo"), null, "adpsearch", null);
                                                adpsearch.add(mem);
                                                adpsearch.notifyDataSetChanged();
                                            }
                                        }
                                    } else {
                                        Log.d("메시지", "Error getting documents: ", task.getException());
                                        return;
                                    }
                                }
                            });
                } else if ((search.matches(regex_num) == true && search.length() == 11) || search.matches(regex_phone) == true) {
                    if (search.matches(regex_phone) == true)
                        search = search.replaceAll("-", "");
                    db.collection("members").whereEqualTo("mem_phone", search).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (querySnapshot.isEmpty()) {
                                            Toast.makeText(getActivity(), "입력하신 전화번호를 가진 회원이 없습니다. 다시 입력하세요", Toast.LENGTH_SHORT).show();
                                        }
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (user.getUid().equals(document.getString("uid"))) {
                                                Toast.makeText(getActivity(), "회원 본인입니다", Toast.LENGTH_SHORT).show();
                                            } else if (uid2.equals(document.getString("uid"))) {
                                                Toast.makeText(getActivity(), "이미 연결된 계정입니다", Toast.LENGTH_SHORT).show();
                                            } else {
                                                ListViewMember mem = new ListViewMember(document.getString("uid"), document.getString("mem_name"), document.getString("mem_email"), document.getString("mem_photo"), null, "adpsearch", null);
                                                adpsearch.add(mem);
                                                adpsearch.notifyDataSetChanged();
                                            }
                                        }
                                    } else {
                                        Log.d("메시지", "Error getting documents: ", task.getException());
                                        return;
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "이메일 혹은 전화번호 형식에 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("메시지", "cn_id : " + cn_id);
                FirebaseFirestore.getInstance().collection("connection").document(cn_id).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("메시지", "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("메시지", "Error deleting document", e);
                            }
                        });
                refreshFragment();
            }
        });

        return root;
    }

    public void refreshFragment() {
        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
        t.detach(this).attach(this).commitAllowingStateLoss();
    }
}