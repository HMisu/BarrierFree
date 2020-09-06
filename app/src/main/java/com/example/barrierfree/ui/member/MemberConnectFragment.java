package com.example.barrierfree.ui.member;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.example.barrierfree.R;
import com.example.barrierfree.models.ListViewMember;
import com.google.android.gms.tasks.OnCompleteListener;
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

    private TextView editsearch;
    private Button btnsearch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_memberconnect, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        editsearch = (TextView) root.findViewById(R.id.edit_search);
        btnsearch = (Button) root.findViewById(R.id.btn_search_mem);

        // Adapter 생성
        adprequest = new ListViewMemberAdpater(getActivity());
        adpapply = new ListViewMemberAdpater(getActivity());
        adpsearch = new ListViewMemberAdpater(getActivity());

        lvrequest = (ListView) root.findViewById(R.id.listview_request_user);
        lvrequest.setAdapter(adprequest);
        lvapply = (ListView) root.findViewById(R.id.listview_apply_user);
        lvapply.setAdapter(adpapply);
        lvsearch = (ListView) root.findViewById(R.id.listview_search_user);
        lvsearch.setAdapter(adpsearch);

        db.collection("connection").whereEqualTo("mem_applicant", user.getUid()).whereEqualTo("connect", false).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String uid = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getString("mem_weak").equals(user.getUid()))
                                    uid = document.getString("mem_protect");
                                else
                                    uid = document.getString("mem_weak");
                                Log.d("메시지",uid);
                                db.collection("members").whereEqualTo("uid", uid).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        ListViewMember mem = new ListViewMember(document.getString("uid"), document.getString("mem_name"), document.getString("mem_email"), document.getString("mem_photo"), user.getUid(),"adpapply");
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
                            String uid = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getString("mem_weak").equals(user.getUid()))
                                    uid = document.getString("mem_protect");
                                else
                                    uid = document.getString("mem_weak");
                                db.collection("members").whereEqualTo("uid", uid).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        ListViewMember mem = new ListViewMember(document.getString("uid"), document.getString("mem_name"), document.getString("mem_email"), document.getString("mem_photo"), document.getString("uid"),"adprequest");
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

        //search용 리스트 새로 만들기
        //요청 목록만 있는 한미수는 문제 X 그런데 신청 목록이 있는 두 유저는 로딩 느림.
        //editsearch 내용 없어지거나 변경되면 리스트 refresh 혹은 delete 추가

        editsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { //EditText에 변화가 있을 때
                if(adpsearch.getCount() != 0){
                    adpsearch.clear();
                    adpsearch.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable arg0) { //입력이 끝났을 때
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { //입력하기 전에 호출되는 API
                if(adpsearch.getCount() != 0){
                    adpsearch.clear();
                    adpsearch.notifyDataSetChanged();
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
                                            ListViewMember mem = new ListViewMember(document.getString("uid"), document.getString("mem_name"), document.getString("mem_email"), document.getString("mem_photo"), null ,"adpsearch");
                                            adpsearch.add(mem);
                                            adpsearch.notifyDataSetChanged();
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
                                            ListViewMember mem = new ListViewMember(document.getString("uid"), document.getString("mem_name"), document.getString("mem_email"), document.getString("mem_photo"), null, "adpsearch");
                                            adpsearch.add(mem);
                                            adpsearch.notifyDataSetChanged();
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
        return root;
    }

    public void refreshFragment(){
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        //FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}