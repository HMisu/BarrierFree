package com.example.barrierfree.ui.bottomNV;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import java.util.List;
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

    TextView mStatusView;
    MediaRecorder mRecorder;
    Thread runner;
    private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;
    final Runnable updater = new Runnable(){

        public void run(){
            updateTv();
        };
    };
    final Handler mHandler = new Handler();

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


        db.collection("connection").whereEqualTo("mem_applicant", user.getUid()).whereEqualTo("connect", true).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("작업 완료", "Success");
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                uid = document.getString("mem_respondent");
                                Log.d("DB연결완료", "mem_respondent : " + uid);
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
                                                        SimpleDateFormat mFormat = new SimpleDateFormat("yy-MM-dd a HH:mm");
                                                        String time = mFormat.format(date);
                                                        adpalert.add(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.ic_bell), "연결 알림", document.getString("mem_name") + "님께 계정연결을 신청하셨습니다.", time);
                                                        db.collection("alert").whereEqualTo("no_sound", false).whereEqualTo("alarm",true).whereEqualTo("user_id", uid).get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        Log.d("메시지","알람준비완료");
                                                                        db.collection("alert").document(user.getUid()).update("isAlert", true);
                                                                    }
                                                                });
                                                        Log.d("메시지", "리스트뷰 추가 완료");
                                                        adpalert.notifyDataSetChanged();
                                                        db.collection("alert").whereEqualTo("user_id", user.getUid()).whereEqualTo("vibrate", true).whereEqualTo("no_sound", false).get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        for (final QueryDocumentSnapshot document : task.getResult()) {
                                                                            db.collection("alert").document(user.getUid()).update("safeAlert", false);
                                                                            String vibe = document.getString("vibrate_type");
                                                                            final Vibrator vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                                                                            Log.d("알람메세지", vibe);
                                                                            if (vibe.equals("고속반복")) {
                                                                                long[] pattern = {400, 200, 400, 200, 400, 200, 400, 200, 400, 200};
                                                                                vibrator.vibrate(pattern, -1);
                                                                            } else if (vibe.equals("스타카토")) {
                                                                                long[] pattern = {100, 200, 100, 100, 100, 100, 100, 100, 100, 100};
                                                                                vibrator.vibrate(pattern, -1);
                                                                            } else if (vibe.equals("심장박동")) {
                                                                                long[] pattern = {170, 150, 100, 300, 170, 150, 100, 300, 170, 150, 100, 300};
                                                                                vibrator.vibrate(pattern, -1);
                                                                            } else if (vibe.equals("심포니")) {
                                                                                long[] pattern = {300, 90, 300, 90, 300, 90, 700, 100, 300, 90, 300, 90, 300, 90, 700, 100};
                                                                                vibrator.vibrate(pattern, -1);
                                                                            } else if (vibe.equals("일반진동")) {
                                                                                long[] pattern = {600, 700, 600, 700, 600, 700, 600, 700, 600, 700};
                                                                                vibrator.vibrate(pattern, -1);
                                                                            } else if (vibe.equals("S.O.S")) {
                                                                                long[] pattern = {80, 150, 80, 150, 80, 200, 500, 250, 500, 250, 500, 250};
                                                                                vibrator.vibrate(pattern, -1);
                                                                            } else if (vibe.equals("사용자 진동패턴")) {
                                                                                List<Long> patt = (List<Long>) document.get("user_vibe");
                                                                                Log.d("메시지", String.valueOf(patt));
                                                                                int i = 0;
                                                                                long[] pattern = new long[patt.size()];
                                                                                for(Long l : patt) {
                                                                                    pattern[i++] = l;
                                                                                }
                                                                                vibrator.vibrate(pattern, -1);
                                                                            } else {
                                                                                Toast.makeText(getActivity().getApplicationContext(), "진동이 설정되지 않았습니다.", Toast.LENGTH_SHORT);
                                                                            }
                                                                        }
                                                                    }
                                                                });
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

        db.collection("connection").whereEqualTo("mem_respondent", user.getUid()).whereEqualTo("connect", true).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("작업 완료", "Success");
                            for (final QueryDocumentSnapshot document : task.getResult()) {

                                uid = document.getString("mem_protect");
                                Log.d("DB연결완료", "mem_protect : " + uid);
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
                                                        SimpleDateFormat mFormat = new SimpleDateFormat("yy-MM-dd a HH:mm");
                                                        String time = mFormat.format(date);
                                                        adpalert.add(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.ic_bell), "연결 알림", document.getString("mem_name") + "님이 계정연결을 신청하셨습니다.", time);
                                                        db.collection("alert").whereEqualTo("no_sound", false).whereEqualTo("alarm",true).whereEqualTo("user_id", uid).get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        Log.d("메시지","알람준비완료");
                                                                        db.collection("alert").document(user.getUid()).update("isAlert", true);
                                                                    }
                                                                });
                                                        Log.d("메시지", "리스트뷰 추가 완료");
                                                        adpalert.notifyDataSetChanged();
                                                        db.collection("alert").whereEqualTo("user_id", user.getUid()).whereEqualTo("vibrate", true).whereEqualTo("no_sound", false).get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        for (final QueryDocumentSnapshot document : task.getResult()) {
                                                                            db.collection("alert").document(user.getUid()).update("safeAlert", false);
                                                                            String vibe = document.getString("vibrate_type");
                                                                            final Vibrator vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                                                                            Log.d("알람메세지", vibe);
                                                                            if (vibe.equals("고속반복")) {
                                                                                long[] pattern = {400, 200, 400, 200, 400, 200, 400, 200, 400, 200};
                                                                                vibrator.vibrate(pattern, -1);
                                                                            } else if (vibe.equals("스타카토")) {
                                                                                long[] pattern = {100, 200, 100, 100, 100, 100, 100, 100, 100, 100};
                                                                                vibrator.vibrate(pattern, -1);
                                                                            } else if (vibe.equals("심장박동")) {
                                                                                long[] pattern = {170, 150, 100, 300, 170, 150, 100, 300, 170, 150, 100, 300};
                                                                                vibrator.vibrate(pattern, -1);
                                                                            } else if (vibe.equals("심포니")) {
                                                                                long[] pattern = {300, 90, 300, 90, 300, 90, 700, 100, 300, 90, 300, 90, 300, 90, 700, 100};
                                                                                vibrator.vibrate(pattern, -1);
                                                                            } else if (vibe.equals("일반진동")) {
                                                                                long[] pattern = {600, 700, 600, 700, 600, 700, 600, 700, 600, 700};
                                                                                vibrator.vibrate(pattern, -1);
                                                                            } else if (vibe.equals("S.O.S")) {
                                                                                long[] pattern = {80, 150, 80, 150, 80, 200, 500, 250, 500, 250, 500, 250};
                                                                                vibrator.vibrate(pattern, -1);
                                                                            } else if (vibe.equals("사용자 진동패턴")) {
                                                                                List<Long> patt = (List<Long>) document.get("user_vibe");
                                                                                Log.d("메시지", String.valueOf(patt));
                                                                                int i = 0;
                                                                                long[] pattern = new long[patt.size()];
                                                                                for(Long l : patt) {
                                                                                    pattern[i++] = l;
                                                                                }
                                                                                vibrator.vibrate(pattern, -1);
                                                                            } else {
                                                                                Toast.makeText(getActivity().getApplicationContext(), "진동이 설정되지 않았습니다.", Toast.LENGTH_SHORT);
                                                                            }
                                                                        }
                                                                    }
                                                                });
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

        db.collection("alert").whereEqualTo("user_id", user.getUid()).whereEqualTo("alarm", true).whereEqualTo("no_sound",false).whereEqualTo("isAlert",true).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (final QueryDocumentSnapshot document : task.getResult()) {
                            String alert = document.getString("alarm_type");
                            Log.d("알람메세지", alert);
                            if (alert.equals("알람1")) {
                                MediaPlayer ringtone = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.alarm1);
                                ringtone.start();
                                db.collection("alert").document(user.getUid()).update("isAlert", false);
                            } else if (alert.equals("알람2")) {
                                MediaPlayer ringtone = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.alarm2);
                                ringtone.start();
                                db.collection("alert").document(user.getUid()).update("isAlert", false);
                            } else if (alert.equals("알람3")) {
                                MediaPlayer ringtone = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.alarm3);
                                ringtone.start();
                                db.collection("alert").document(user.getUid()).update("isAlert", false);
                            } else if (alert.equals("알람4")) {
                                MediaPlayer ringtone = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.alarm4);
                                ringtone.start();
                                db.collection("alert").document(user.getUid()).update("isAlert", false);
                            } else if (alert.equals("알람5")) {
                                MediaPlayer ringtone = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.alarm5);
                                ringtone.start();
                                db.collection("alert").document(user.getUid()).update("isAlert", false);
                            } else if (alert.equals("알람6")) {
                                MediaPlayer ringtone = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.alarm6);
                                ringtone.start();
                                db.collection("alert").document(user.getUid()).update("isAlert", false);
                            } else if (alert.equals("알람7")) {
                                MediaPlayer ringtone = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.alarm7);
                                ringtone.start();
                                db.collection("alert").document(user.getUid()).update("isAlert", false);
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "알람이 설정되지 않았습니다.", Toast.LENGTH_SHORT);
                            }
                        }
                    }
                });


        db.collection("connection").whereEqualTo("mem_protect", user.getUid()).whereEqualTo("connect", true).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (final QueryDocumentSnapshot document : task.getResult()) {
                            uid = document.getString("mem_weak");
                            db.collection("location").document(uid).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                final DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    Log.d("취약자 연결 완료", uid);
                                                    Log.d("취약자의 위치값", "위도" + document.getDouble("latitude") + "경도" + document.getDouble("longitude"));
                                                    final double now_latim = 110.940 * document.getDouble("latitude");
                                                    final double now_longim = 90.180 * document.getDouble("longitude");
                                                    db.collection("safety").whereEqualTo("mem_weak", uid).get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    Log.d("메세지", "안심지역 연결완료");
                                                                    for (final QueryDocumentSnapshot document : task.getResult()) {
//                                                                        double diffLati = LatitudeInDifference(50);
//                                                                        double diffLongi = LongitudeInDifference(document.getDouble("latitude"), 50);

                                                                        double meter_lati, meter_longi;
                                                                        meter_lati = 110.940 * document.getDouble("latitude");
                                                                        meter_longi = 90.180 * document.getDouble("longitude");

                                                                        Log.d("메세지", "현재 위치의 위도 " + now_latim + " 경도 " + now_longim + "안심지역의 위도 " + meter_lati + " 경도 " + meter_longi);

                                                                        if (now_latim >= (meter_lati - 500) && now_latim <= (meter_lati + 500)) {
                                                                            Log.d("메시지", "if문 하나통과");
                                                                            if (now_longim >= (meter_longi - 500) && now_longim <= (meter_longi + 500)) {
                                                                                Log.d("메세지", "안심지역임.");
                                                                            } else {
                                                                                Log.d("메세지", "안심지역을 벗어남.");
                                                                                addIsSafty();
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            });

                                                }

                                            } else {
                                                Log.d("메시지", "No Such Document");
                                            }
                                        }
                                    });
                        }
                    }
                });


        //String Receive = intent.getStringExtra("delete");


        if (runner == null)
        {
            runner = new Thread(){
                public void run()
                {
                    while (runner != null)
                    {
                        try
                        {
                            Thread.sleep(1000);
                            //Log.i("Noise", "Tock");
                        } catch (InterruptedException e) { };
                        mHandler.post(updater);
                    }
                }
            };
            runner.start();
            Log.d("Noise", "start runner()");
        }

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

        return (diff * 360.0) / (2 * Math.PI * earth);
    }

    public double LongitudeInDifference(double _latitude, int diff) {
        final int eatrh = 6371000;

        double add = Math.cos(0);
        double ddf = Math.cos(Math.toRadians(_latitude));

        return (diff * 360.0) / (2 * Math.PI * eatrh * Math.cos(Math.toRadians(_latitude)));
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
                                Log.d("DB연결완료", "mem_weak : " + uid);
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
                                                        SimpleDateFormat mFormat = new SimpleDateFormat("yy-MM-dd a HH:mm");
                                                        String time = mFormat.format(date);
                                                        adpalert.add(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.ic_alert), "위험 알림", document.getString("mem_name") + "님이 안심지역을 벗어났습니다.", time);
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

    public void onResume()
    {
        super.onResume();
        startRecorder();
    }

    public void onPause()
    {
        super.onPause();
        stopRecorder();
    }

    public void startRecorder(){
        int permiCheck = ContextCompat.checkSelfPermission(getContext().getApplicationContext(), Manifest.permission.RECORD_AUDIO);

        if (mRecorder == null)
        {
            if(permiCheck == PackageManager.PERMISSION_DENIED){

                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 0);

            }
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try
            {
                mRecorder.prepare();
            }catch (java.io.IOException ioe) {
                android.util.Log.e("[Monkey]", "IOException: " + android.util.Log.getStackTraceString(ioe));

            }catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " + android.util.Log.getStackTraceString(e));
            }
            try
            {
                mRecorder.start();
            }catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " + android.util.Log.getStackTraceString(e));
            }

            //mEMA = 0.0;
        }

    }
    public void stopRecorder() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void updateTv(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat mFormat = new SimpleDateFormat("yy-MM-dd a HH:mm");
        String time = mFormat.format(date);
        if ((getAmplitudeEMA() / 80) > 300){
            adpalert.add(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.ic_alert), "소리 감지", "큰소리가 감지되엇습니다.", time);
            adpalert.notifyDataSetChanged();
            final Vibrator vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(500);
        }
    }
    public double soundDb(double ampl){
        return  (20 * Math.log10(getAmplitudeEMA() / ampl));
    }
    public double getAmplitude() {
        if (mRecorder != null)
            return  (mRecorder.getMaxAmplitude());
        else
            return 0;

    }
    public double getAmplitudeEMA() {
        double amp =  getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }

}