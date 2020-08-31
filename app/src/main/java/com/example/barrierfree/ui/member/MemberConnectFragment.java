package com.example.barrierfree.ui.member;

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

import com.example.barrierfree.R;
import com.example.barrierfree.models.ListViewMember;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MemberConnectFragment extends Fragment{
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private View root;
    private ListView lvrequest, lvapply;
    private ListViewMemberAdpater adprequest, adpapply;

    private TextView editsearch;
    private Button btnsearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_memberconnect, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editsearch = (TextView)root.findViewById(R.id.edit_search);
        btnsearch = (Button)root.findViewById(R.id.btn_search_mem);

        // Adapter 생성
        adprequest = new ListViewMemberAdpater(getActivity());
        adpapply = new ListViewMemberAdpater(getActivity());

        lvrequest = (ListView)root.findViewById(R.id.listview_request_user);
        lvrequest.setAdapter(adprequest);
        lvapply = (ListView)root.findViewById(R.id.listview_apply_user);
        lvapply.setAdapter(adpapply);

        user = mAuth.getCurrentUser();

        db.collection("userConnect").whereEqualTo("connection", false).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(!document.getString("mem_applicant").equals(user.getUid())&&(document.getString("mem_weak").equals(user.getUid()) || document.getString("mem_protect").equals(user.getUid()))) {
                                    ListViewMember mem = new ListViewMember(document.getString("uid"), document.getString("mem_name"), document.getString("mem_email"),  document.getString("mem_photo"), "adprequest");
                                    adprequest.add(mem);
                                }
                            }
                        } else {
                            Log.d("메시지", "Error getting documents: ", task.getException());
                            return;
                        }
                    }
                });
        // Data가 변경 되있음을 알려준다.
        adprequest.notifyDataSetChanged();

        db.collection("userConnect").whereEqualTo("mem_applicant", user.getUid()).whereEqualTo("connection", false).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("mem_weak").equals(user.getUid()) || document.getString("mem_protect").equals(user.getUid())){
                                    Log.d("메시지", document.getId() + " => " + document.getData());
                                    ListViewMember mem = new ListViewMember(document.getString("uid"), document.getString("mem_name"), document.getString("mem_email"),  document.getString("mem_photo"), "adpapply");
                                    adpapply.add(mem);
                                }
                            }
                        } else {
                            Log.d("메시지", "Error getting documents: ", task.getException());
                            return;
                        }
                    }
                });
        adpapply.notifyDataSetChanged();

        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = editsearch.getText().toString().trim();
                String regex_num = "^[0-9]+$";
                String regex_email = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
                String regex_phone = "^01(?:0|1|[6-9])[-]?(\\d{3}|\\d{4})[-]?(\\d{4})$";
                if(search.matches(regex_email) == true){
                    db.collection("members").whereEqualTo("mem_email", search).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (querySnapshot.isEmpty()) {
                                            Toast.makeText(getActivity(), "입력하신 전화번호를 가진 회원이 없습니다. 다시 입력하세요", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("메시지", document.getId() + " => " + document.getData());
                                            ListViewMember mem = new ListViewMember(document.getString("uid"), document.getString("mem_name"), document.getString("mem_email"),  document.getString("mem_photo"), "adpsearch");
                                            adpapply.add(mem);
                                        }
                                        adpapply.notifyDataSetChanged();
                                    } else {
                                        Log.d("메시지", "Error getting documents: ", task.getException());
                                        return;
                                    }
                                }
                            });
                } else if((search.matches(regex_num) == true && search.length() == 11) || search.matches(regex_phone) == true){
                    if(search.matches(regex_phone) == true)
                        search = search.replaceAll("-","");
                    db.collection("members").whereEqualTo("mem_phone", search).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (querySnapshot.isEmpty()) {
                                            Toast.makeText(getActivity(), "입력하신 전화번호를 가진 회원이 없습니다. 다시 입력하세요", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("메시지", document.getId() + " => " + document.getData());
                                            ListViewMember mem = new ListViewMember(document.getString("uid"), document.getString("mem_name"), document.getString("mem_email"),  document.getString("mem_photo"), "adpsearch");
                                            adpapply.add(mem);
                                        }
                                        adpapply.notifyDataSetChanged();
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
}