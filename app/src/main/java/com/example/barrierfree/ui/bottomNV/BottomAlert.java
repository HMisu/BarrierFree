package com.example.barrierfree.ui.bottomNV;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.example.barrierfree.R;
import com.example.barrierfree.models.ListViewMember;
import com.example.barrierfree.ui.member.ListViewMemAdpater;
import com.example.barrierfree.ui.member.MemberConnectFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class BottomAlert extends ListFragment {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private AlertAdapter adpalert;

    String uid;

    ViewGroup viewGroup;
    private ListView lvalert;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.app_bar_alert, container, false);

//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w("Alert Log", "getInstanceId Failed", task.getException());
//                            return;
//                        }
//                        String token = task.getResult().getToken();
//                        Log.d("Alert Log", "알람 토큰 : " + token);
//                        Context context;
//                        context = container.getContext();
//                        Toast.makeText(context, token, Toast.LENGTH_SHORT).show();
//                    }
//                });
//        adapter = new CustomAdapter();
//        setListAdapter(adapter);



//        adapter.addItem("제목 2", "내용 2", "20-05-21");
//
//        adapter.addItem("제목 1", "내용 1", "20-05-03");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        Log.d("메시지", "알림창 개설완료");
        adpalert = new AlertAdapter(getActivity(), BottomAlert.this);

        lvalert = (ListView) viewGroup.findViewById(R.id.liistview_alert);
        lvalert.setAdapter(adpalert);
//        adpalert = new AlertAdapter();
//        setListAdapter(adpalert);
        adpalert.addItem("제목 2", "내용 2", "20-11-02");

        db.collection("connection").whereEqualTo("mem_respondent", user.getUid()).whereEqualTo("connect", true).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("작업 완료", "Success");
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                uid = document.getString("mem_protect");
                                Log.d("DB연결완료", "mem_protect : "+ uid);
                                db.collection("members").document(uid).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        Log.d("메시지", "Member DB 연결완료 : " + document.getString("mem_name"));
                                                        adpalert.addItem("계정연결 알림", document.getString("mem_name") + "님이 계정 연결을 신청하셨습니다,", "20-11-02");
                                                        adpalert.notifyDataSetChanged();
                                                    } else {
                                                        Log.d("메시지", "No such document");
                                                    }
                                                } else {
                                                    Log.d("메시지", "get failed with ", task.getException());
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.d("메시지", "Error to nofication : ", task.getException());
                            return;
                        }
                    }
                });



        return super.onCreateView(inflater, container, savedInstanceState);


    }
}