package com.example.barrierfree.ui.bottomNV;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.barrierfree.R;
import com.example.barrierfree.models.ListViewMember;
import com.example.barrierfree.ui.find.MyReceiver;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class BottomAlert extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private View view;
    private TextToSpeech tts;

    LocationManager lm;
    AlertAdapter adpalert;
    String uid;
    ViewGroup viewGroup;
    ListView lvalert;
    MyReceiver receiver;
    Boolean safe = Boolean.TRUE;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.app_bar_alert, container, false);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        Log.d("메시지", "알림창 개설완료");
        adpalert = new AlertAdapter(getActivity(), BottomAlert.this);

        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        lvalert = (ListView) view.findViewById(R.id.listview_alert);
        lvalert.setAdapter(adpalert);
//        adpalert = new AlertAdapter();
//        setListAdapter(adpalert);



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
                                                        long now = System.currentTimeMillis();
                                                        Date date = new Date(now);
                                                        SimpleDateFormat mFormat = new SimpleDateFormat("yy-MM-dd");
                                                        String time = mFormat.format(date);
                                                        adpalert.add(R.drawable.ic_alert,"연결 알림", document.getString("mem_name") + "님이 계정연열을 신청하셨숩니다.", time);
                                                        Log.d("메시지", "리스트뷰 추가 완료");
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


        db.collection("connection").whereEqualTo("mem_protect",user.getUid()).whereEqualTo("connect", true).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(final QueryDocumentSnapshot document : task.getResult()) {
                            uid = document.getString("mem_weak");
                            db.collection("location").document(uid).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    Log.d("취약자 연결 완료", uid);
                                                    Log.d("취약자의 위치값", "위도" + document.getDouble("latitude") + "경도" + document.getDouble("longitude"));

                                                    Bundle bundle = getArguments();

                                                    if(bundle != null) {
                                                        String safty_lati = bundle.getString("str_lati", "0");
                                                        String safty_longi = bundle.getString("str_longi", "0");


                                                        Log.d("BottomAlert, 안심지역의", "위도 " + safty_lati + "경도 " + safty_longi);

                                                        //if()

                                                        receiver = new MyReceiver();
                                                        IntentFilter filter = new IntentFilter("Location");
                                                        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, filter);

                                                        Intent intent = new Intent("Location");
                                                        PendingIntent pending = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 0, intent, 0);

                                                        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                            // TODO: Consider calling
                                                            //    ActivityCompat#requestPermissions
                                                            // here to request the missing permissions, and then overriding
                                                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                            //                                          int[] grantResults)
                                                            // to handle the case where the user grants the permission. See the documentation
                                                            // for ActivityCompat#requestPermissions for more details.

                                                        }
                                                        lm.addProximityAlert(Double.parseDouble(safty_lati), Double.parseDouble(safty_longi), 200, -1, pending);

                                                        double diffLati = LatitudeInDifference(50);
                                                        double diffLongi = LongitudeInDifference(Double.parseDouble(safty_longi), 50);

                                                        double meter_lati, meter_longi, now_latim, now_longim;
                                                        meter_lati = 110.940 * Double.parseDouble(safty_lati);
                                                        meter_longi = 90.180 * Double.parseDouble(safty_longi);
                                                        now_latim = 110.940 * document.getDouble("latitude");
                                                        now_longim = 90.180 * document.getDouble("longitude");

                                                        Log.d("메세지", "현재 위치의 위도 " + now_latim + " 경도 " + now_longim + "안심지역의 위도 " + meter_lati + " 경도 " + meter_longi);


                                                        if(now_latim >= (meter_lati - 500) && now_latim <= (meter_lati + 500)) {
                                                            Log.d("메시지","if문 하나통과");
                                                            if(now_longim >= (meter_longi - 500) && now_longim <= (meter_longi + 500)){
                                                                Log.d("메세지","안심지역임.");
                                                            } else {
                                                                Log.d("메세지", "안심지역을 벗어남.");
                                                                addIsSafty();
                                                                tts = new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
                                                                    @Override
                                                                    public void onInit(int status) {
                                                                        tts.setLanguage(Locale.KOREAN);
                                                                        speak();
                                                                    }
                                                                    private void speak(){
                                                                        tts.speak("취약자가 안심지역을 벗어났습니다.", TextToSpeech.QUEUE_FLUSH, null);
                                                                    }
                                                                });

                                                            }
                                                        }
                                                    }

                                                } else {
                                                    Log.d("메시지", "No Such Document");
                                                }
                                            } else {
                                                Log.d("메시지", "get failed with", task.getException());
                                            }
                                        }
                                    });
                        }
                    }
                });


        //String Receive = intent.getStringExtra("delete");


        return view;


    }

    @Override
    public void onStop() {
        super.onStop();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    public double LatitudeInDifference(int diff) {
        final int earth = 6371000;

        return (diff*360.0) / (2*Math.PI*earth);
    }

    public double LongitudeInDifference(double _latitude, int diff) {
        final int eatrh = 6371000;

        double add = Math.cos(0);
        double ddf = Math.cos(Math.toRadians(_latitude));

        return (diff*360.0) / (2*Math.PI*eatrh*Math.cos(Math.toRadians(_latitude)));
    }

    public void addIsSafty() {
        Log.d("메세지", "addIsSafty호출.");
        db.collection("connection").whereEqualTo("mem_protect", user.getUid()).whereEqualTo("connect", true).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("작업 완료", "Success");
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                uid = document.getString("mem_weak");
                                Log.d("DB연결완료", "mem_weak : "+ uid);
                                db.collection("members").document(uid).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        Log.d("메시지", "Member DB 연결완료 : " + document.getString("mem_name"));
                                                        long now = System.currentTimeMillis();
                                                        Date date = new Date(now);
                                                        SimpleDateFormat mFormat = new SimpleDateFormat("yy-MM-dd");
                                                        String time = mFormat.format(date);
                                                        adpalert.add(R.drawable.ic_alert,"위험 알림", document.getString("mem_name") + "님이 안심지역을 벗어났습니다.", time);
                                                        Log.d("메시지", "리스트뷰 추가 완료");
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
    }

}