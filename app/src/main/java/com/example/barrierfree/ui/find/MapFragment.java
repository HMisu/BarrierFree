package com.example.barrierfree.ui.find;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.barrierfree.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

public class MapFragment extends Fragment {
    private FindViewModel findViewModel;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    double longitude, latitude;
    String weekuid;
    TMapView tMapView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //        findViewModel =
        //                ViewModelProviders.of(this).get(FindViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery2, container, false);
        LinearLayout linearLayoutTmap = (LinearLayout) root.findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(getActivity());
        tMapView.setSKTMapApiKey("l7xx0bfddf7c9eaa4cc48ff35b5f8b73ce9d");
        linearLayoutTmap.addView(tMapView);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        db.collection("connection").whereEqualTo("connect", true).whereEqualTo("mem_protect", user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                weekuid = document.getString("mem_weak");
                            }
                            db.collection("location").document(weekuid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            longitude = document.getDouble("longitude");
                                            latitude = document.getDouble("latitude");
                                            // 취약자 위치 지도에 마커 표시하는(찍는) 곳
                                            TMapMarkerItem markerItem1 = new TMapMarkerItem();
                                            TMapPoint tMapPoint1 = new TMapPoint(latitude, longitude);
                                            Log.d("메시지", longitude + " "+latitude);
                                            // 마커 아이콘
                                            markerItem1.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
                                            markerItem1.setTMapPoint(tMapPoint1); // 마커의 좌표 지정
                                            markerItem1.setName("취약자"); // 마커의 타이틀 지정
                                            tMapView.addMarkerItem("markerItem1", markerItem1); // 지도에 마커 추가
                                            tMapView.setCenterPoint(longitude, latitude);
                                        }
                                    } else {
                                        Log.d("메시지", "get failed with ", task.getException());
                                    }
                                }
                            });
                        } else {
                            Log.d("메시지", "Error getting documents: ", task.getException());
                            return;
                        }
                    }
                });


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}