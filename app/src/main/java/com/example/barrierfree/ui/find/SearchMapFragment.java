package com.example.barrierfree.ui.find;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.barrierfree.MainActivity;
import com.example.barrierfree.R;
import com.example.barrierfree.models.ResponseAddr;
import com.example.barrierfree.ui.bottomNV.BottomAlert;
import com.example.barrierfree.ui.member.SafetyEditFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * POI 정보를 통합검색하는 화면
 */
public class SearchMapFragment extends Fragment implements LocationListener {
    private String TAG = "SearchMapFragment";

    private BottomSheetDialog mBottomSheetDialog;
    Fragment fragment = new BottomAlert();
    private SharedPreferences mPreferences;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private TextToSpeech tts;

    double rfglongitude, rfglatitude, longitude = 0, latitude = 0;
    String weekuid, rfgaddr;
    int count;
    String uid;

    LocationManager lm;
    MyReceiver receiver;
    LinearLayout linearLayoutTmap;
    ConstraintLayout llBottom;
    EditText etSearch;
    Button btnSearch, bt_close;
    ImageButton btn_safety, btnCurrentLoc;
    TextView tvAddress, tvDetail, tvType, txt1;

    TMapView tMapView;

    GestureDetector detector;

    ArrayList<ResponseAddr> items = new ArrayList<>();

    private FusedLocationProviderClient fusedLocationClient; // 마지막에 저장된 현재 위치값을 가져오기 위해 생성

    String AppKey = "7dEldMVxKoZfmjg1iqfBabOSQQ%2B2ysH%2FY9TgK4dPPb3nqa4YRvgpd%2FdAzN8xyFnEeOJpNCYlDcjzRREjwDMuGw%3D%3D"; // 공공데이터 키

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            latitude = bundle.getDouble("rfglatitude");
            longitude = bundle.getDouble("rfglongitude");
        } else {
            latitude = 0;
            longitude = 0;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        System.out.println("FindFragment");
        // findViewModel = ViewModelProviders.of(this).get(FindViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search_map, container, false);

        linearLayoutTmap = (LinearLayout) root.findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(getActivity());
        tMapView.setSKTMapApiKey("l7xx0bfddf7c9eaa4cc48ff35b5f8b73ce9d");

        linearLayoutTmap.addView(tMapView);

        llBottom = (ConstraintLayout) root.findViewById(R.id.llBottom);
        etSearch = (EditText) root.findViewById(R.id.etSearch);
        btnSearch = (Button) root.findViewById(R.id.btnSearch);
        btnCurrentLoc = (ImageButton) root.findViewById(R.id.btnCurrentLoc);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        db.collection("connection").whereEqualTo("connect", true).whereEqualTo("mem_protect", user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                weekuid = document.getString("mem_weak");
                            }
                            if (querySnapshot.isEmpty()) {
                                Log.d("메시지", "empty");
                                weekuid = user.getUid();
                            } else {
                                db.collection("location").document(weekuid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                final double weaklongitude = document.getDouble("longitude");
                                                final double weaklatitude = document.getDouble("latitude");


                                                // 취약자 위치 지도에 마커 표시하는(찍는) 곳
                                                TMapMarkerItem markerItem1 = new TMapMarkerItem();
                                                TMapPoint tMapPoint1 = new TMapPoint(weaklatitude, weaklongitude);
                                                Log.d("메시지", weaklongitude + " " + weaklatitude);
                                                // 마커 아이콘
                                                markerItem1.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
                                                markerItem1.setTMapPoint(tMapPoint1); // 마커의 좌표 지정
                                                markerItem1.setName("취약자"); // 마커의 타이틀 지정
                                                tMapView.addMarkerItem("markerItem1", markerItem1); // 지도에 마커 추가

                                                if (latitude == 0 && longitude == 0) {
                                                    tMapView.setCenterPoint(weaklongitude, weaklatitude);
                                                }
                                            }
                                        } else {
                                            Log.d("메시지", "get failed with ", task.getException());
                                        }
                                    }
                                });
                            }
                            db.collection("safety").whereEqualTo("mem_weak", weekuid).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot querySnapshot = task.getResult();
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Log.d("메시지", document.getString("name"));

                                                    TMapPoint tMapPoint = new TMapPoint(document.getDouble("latitude"), document.getDouble("longitude"));
                                                    TMapCircle tMapCircle = new TMapCircle();
                                                    tMapCircle.setCenterPoint(tMapPoint);
                                                    tMapCircle.setRadius(50);
                                                    tMapCircle.setCircleWidth(2);
                                                    tMapCircle.setLineColor(Color.BLUE);
                                                    tMapCircle.setAreaColor(Color.GRAY);
                                                    tMapCircle.setAreaAlpha(100);

                                                    tMapView.addTMapCircle(document.getId(), tMapCircle);

                                                    //미혜야 여기!
                                                    db.collection("connection").whereEqualTo("mem_protect", user.getUid()).whereEqualTo("connect", true).get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    final boolean[] did = {false};
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


                                                                                                if (did[0] == false) {
                                                                                                    db.collection("safety").whereEqualTo("mem_weak", uid).get()
                                                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                                    Log.d("메세지", "안심지역 연결완료");
                                                                                                                    for (final QueryDocumentSnapshot document : task.getResult()) {

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
                                                                                                                                db.collection("alert").document(user.getUid()).update("safeAlert", true);
                                                                                                                                //final Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                                                                                                                                tts = new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
                                                                                                                                    @Override
                                                                                                                                    public void onInit(int status) {
                                                                                                                                        tts.setLanguage(Locale.KOREAN);
                                                                                                                                        speak();
                                                                                                                                    }

                                                                                                                                    private void speak() {
                                                                                                                                        db.collection("alert").whereEqualTo("user_id", user.getUid()).whereEqualTo("alarm", true).whereEqualTo("safeAlert", true).get()
                                                                                                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                                                                    @Override
                                                                                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                                                                        for (final QueryDocumentSnapshot document : task.getResult()) {
                                                                                                                                                            db.collection("alert").document(user.getUid()).update("safeAlert", false);
                                                                                                                                                            tts.speak("주의 취약자가 안심지역을 벗어났습니다.", TextToSpeech.QUEUE_FLUSH, null);
                                                                                                                                                            String alert = document.getString("alarm_safty");
                                                                                                                                                            Log.d("알람메세지", alert);
                                                                                                                                                            if (alert.equals("알람1")) {
                                                                                                                                                                MediaPlayer ringtone = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.alarm1);
                                                                                                                                                                ringtone.start();
                                                                                                                                                            } else if (alert.equals("알람2")) {
                                                                                                                                                                MediaPlayer ringtone = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.alarm2);
                                                                                                                                                                ringtone.start();
                                                                                                                                                            } else if (alert.equals("알람3")) {
                                                                                                                                                                MediaPlayer ringtone = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.alarm3);
                                                                                                                                                                ringtone.start();
                                                                                                                                                            } else if (alert.equals("알람4")) {
                                                                                                                                                                MediaPlayer ringtone = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.alarm4);
                                                                                                                                                                ringtone.start();
                                                                                                                                                            } else if (alert.equals("알람5")) {
                                                                                                                                                                MediaPlayer ringtone = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.alarm5);
                                                                                                                                                                ringtone.start();
                                                                                                                                                            } else if (alert.equals("알람6")) {
                                                                                                                                                                MediaPlayer ringtone = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.alarm6);
                                                                                                                                                                ringtone.start();
                                                                                                                                                            } else if (alert.equals("알람7")) {
                                                                                                                                                                MediaPlayer ringtone = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.alarm7);
                                                                                                                                                                ringtone.start();
                                                                                                                                                            } else {
                                                                                                                                                                Toast.makeText(getActivity().getApplicationContext(), "알람이 설정되지 않았습니다.", Toast.LENGTH_SHORT);
                                                                                                                                                            }


                                                                                                                                                        }
                                                                                                                                                    }
                                                                                                                                                });
                                                                                                                                        db.collection("alert").whereEqualTo("user_id", user.getUid()).whereEqualTo("vibrate", true).whereEqualTo("no_sound", false).get()
                                                                                                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                                                                    @Override
                                                                                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                                                                        for (final QueryDocumentSnapshot document : task.getResult()) {
                                                                                                                                                            db.collection("alert").document(user.getUid()).update("safeAlert", false);
                                                                                                                                                            String vibe = document.getString("vibrate_safty");
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

                                                                                                                                    }
                                                                                                                                });

                                                                                                                            }

                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                            });
                                                                                                    did[0] = true;
                                                                                                }
                                                                                            }

                                                                                        } else {
                                                                                            Log.d("메시지", "No Such Document");
                                                                                        }
                                                                                    }
                                                                                });
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
                        } else {
                            Log.d("메시지", "Error getting documents: ", task.getException());
                            return;
                        }
                    }
                });

        if (latitude != 0 && longitude != 0) {
            tMapView.setCenterPoint(longitude, latitude);
        }

//        receiver = new MyReceiver();
//        IntentFilter filter = new IntentFilter("Location");
//        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, filter);
//
//        Intent intent = new Intent("Location");
//        PendingIntent pending = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        Log.d("위도와 경도", "latitude : " + document.getDouble("latitude") + " longitude : " + document.getDouble("longitude"));
//        Log.d("널 값 확인", String.valueOf(pending) + ", getContenxt : " + String.valueOf(getActivity().getApplicationContext()));
//        lm.addProximityAlert(document.getDouble("latitude"), document.getDouble("longitude"),200, -1, pending);
//
//        if(latitude != 0 || longitude != 0){
//            tMapView.setCenterPoint(longitude, latitude);
//        }

        tvAddress = (TextView) root.findViewById(R.id.tvAddress);
        tvDetail = (TextView) root.findViewById(R.id.tvDetail);
        tvType = (TextView) root.findViewById(R.id.tvType);
        bt_close = (Button) root.findViewById(R.id.bt_close);
        btn_safety = (ImageButton) root.findViewById(R.id.btn_safety);
        txt1 = (TextView) root.findViewById(R.id.txt1);
        btn_safety.setVisibility(View.VISIBLE);
        txt1.setVisibility(View.VISIBLE);
        llBottom.setVisibility(View.GONE);

        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llBottom.setVisibility(View.GONE);
            }
        });

        //안심지역
        btn_safety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;
                db.collection("safety").whereEqualTo("mem_register", user.getUid()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        count = count + 1;
                                    }
                                    if (count >= 3) {
                                        Toast.makeText(getActivity(), "최대 등록 갯수 3개를 초과했습니다", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("rfgAddr", rfgaddr);
                                        bundle.putDouble("rfglatitude", rfglatitude);
                                        bundle.putDouble("rfglongitude", rfglongitude);
                                        bundle.putBoolean("rfgAdd", true);

                                        String fragmentTag = new SafetyEditFragment().getClass().getSimpleName();
                                        Fragment fragmentClass = new SafetyEditFragment();
                                        ((MainActivity) getActivity()).replaceFragment(fragmentTag, fragmentClass, bundle);
                                    }
                                } else {
                                    Log.d("메시지", "Error getting documents: ", task.getException());
                                    return;
                                }
                            }
                        });
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidekeyboard();

                String searchText = etSearch.getText().toString().trim();
                if (searchText.length() > 0) {
                    searchLocation(searchText);
                } else {
                    Toast.makeText(getActivity(), "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {

                    case EditorInfo.IME_ACTION_SEARCH:
                        // Toast.makeText(getActivity(), "검색", Toast.LENGTH_LONG).show();
                        hidekeyboard();

                        String searchText = etSearch.getText().toString().trim();
                        if (searchText.length() > 0) {
                            searchLocation(searchText);
                        } else {
                            Toast.makeText(getActivity(), "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        // Toast.makeText(getActivity(), "기본", Toast.LENGTH_LONG).show();
                        return false;
                }
                return true;
            }
        });

        tMapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                String id = tMapMarkerItem.getID();
                Log.d(TAG, "마커 클릭 " + id);
                // Toast.makeText(getActivity(), "클릭 아이디 "+id, Toast.LENGTH_SHORT).show();

                if (id != null && !"".equals(id)) {
                    int seq = Integer.parseInt(id);
                    if (items != null) {
                        ResponseAddr addr = items.get(seq);
                        showBottomSheetDialog(addr);
                    }
                }
            }
        });

        tMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            @Override
            public boolean onPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                llBottom.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                return false;
            }
        });

        btnCurrentLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling more details.
                        Toast.makeText(getActivity(), "위치 정보 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 디바이스에 기록된 마지막 위치값을 가져온다
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        // Logic to handle location object

                                        Log.d(TAG, "마지막 위치값 " + location.getLatitude() + " " + location.getLongitude());

                                        final double lat = location.getLatitude();
                                        final double lng = location.getLongitude();

                                        if (tMapView != null) {

//                                            tMapView.removeTMapCircle("circle1");

                                            tMapView.setLocationPoint(lng, lat);
                                            tMapView.setCenterPoint(lng, lat);

                                            TMapPoint tMapPoint = new TMapPoint(lat, lng);
//                                            TMapCircle tMapCircle = new TMapCircle();
//                                            tMapCircle.setCenterPoint(tMapPoint);
//                                            tMapCircle.setRadius(30);
//                                            tMapCircle.setCircleWidth(2);
//                                            tMapCircle.setLineColor(Color.BLUE);
//                                            tMapCircle.setAreaColor(Color.BLUE);
//                                            // tMapCircle.setAreaAlpha(100);
//                                            tMapView.addTMapCircle("circle1", tMapCircle);

                                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.loc_pin_me);

                                            TMapMarkerItem marker = new TMapMarkerItem();
                                            marker.setID(String.valueOf(99999));
                                            marker.setTMapPoint(tMapPoint);
                                            marker.setIcon(bitmap);
//                                    marker.setName(addr.getSpot_nm());
//                                    marker.setCalloutTitle(addr.getSpot_nm()); // 풍선뷰 표시 메시지 설정
                                            marker.setCanShowCallout(false); // 풍선뷰 사용 설정
                                            marker.setAutoCalloutVisible(false); // 풍선뷰가 자동으로 활성화되도록 설정
//                                    marker.setCalloutRightButtonImage(bitmap_right);

                                            tMapView.addMarkerItem(String.valueOf(99999), marker);

                                        }

                                    }
                                }
                            });
                } catch (Exception e) {
                    Log.e(TAG, "위치 가져오기 처리중 에러발생 " + e.toString());
                }

            }
        });

        tMapView.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {
            @Override
            public void onLongPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint) {
                try {
                    if (items != null) {
                        items.clear();
                    }
                    if (tMapView != null) {
                        tMapView.removeAllTMapCircle();
                        tMapView.removeAllMarkerItem();
                    }

                    Log.d(TAG, "지도 롱클릭 좌표 " + tMapPoint.toString());

                    rfglongitude = tMapPoint.getLongitude();
                    rfglatitude = tMapPoint.getLatitude();

                    RequestResponseTask task = new RequestResponseTask();
                    task.execute(tMapPoint.getLatitude(), tMapPoint.getLongitude());
                } catch (Exception e) {
                    Log.e(TAG, "지도 롱클릭 에러 " + e.toString());
                }
            }
        });
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling more details.
                Toast.makeText(getActivity(), "위치 정보 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            db.collection("connection").whereEqualTo("connect", true).whereEqualTo("mem_protect", user.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(Task<QuerySnapshot> task) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (task.isSuccessful()) {
                                if (querySnapshot.isEmpty()) {
                                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                    fusedLocationClient.getLastLocation()
                                            .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                                @Override
                                                public void onSuccess(Location location) {
                                                    // Got last known location. In some rare situations this can be null.
                                                    if (location != null) {
                                                        // Logic to handle location object
                                                        Log.d(TAG, "마지막 위치값 " + location.getLatitude() + " " + location.getLongitude());

                                                        final double lat = location.getLatitude();
                                                        final double lng = location.getLongitude();

                                                        if (tMapView != null) {
                                                            if (latitude == 0 && longitude == 0) {
                                                                tMapView.setLocationPoint(lng, lat);
                                                                tMapView.setCenterPoint(lng, lat);
                                                            }
                                                        }
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

        } catch (Exception e) {
            Log.e(TAG, "위치 가져오기 처리중 에러발생 " + e.toString());
        }
    }

    // 지도를 표시하는 부분 - 여기에 마커를 표시할 데이터를 파라미터로 받아야 한다
    private void processMapView(final ArrayList<ResponseAddr> items) {
        if (items != null) {
            final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.poi_dot);
            final Bitmap bitmap_right = BitmapFactory.decodeResource(getResources(), R.drawable.i_go);

            for (int i = 0; i < items.size(); i++) {

                ResponseAddr addr = items.get(i);

                TMapPoint tMapPoint = new TMapPoint(addr.getLa_crd(), addr.getLo_crd());

                TMapMarkerItem marker = new TMapMarkerItem();
                marker.setID("" + i);
                marker.setTMapPoint(tMapPoint);
                marker.setIcon(bitmap);
                marker.setName(addr.getSpot_nm());
                marker.setCalloutTitle(addr.getSpot_nm()); // 풍선뷰 표시 메시지 설정
                marker.setCanShowCallout(true); // 풍선뷰 사용 설정
                marker.setAutoCalloutVisible(false); // 풍선뷰가 자동으로 활성화되도록 설정
                marker.setCalloutRightButtonImage(bitmap_right);

                tMapView.addMarkerItem("" + i, marker);
                // tMapView.addMarkerItem2("marker"+i, overlay);
            }
            // 지도의 중심을 첫번째 데이터의 위치로 지정
            if (items.size() > 0) {
                ResponseAddr addr0 = items.get(0);
                tMapView.setLocationPoint(addr0.getLo_crd(), addr0.getLa_crd());
                tMapView.setCenterPoint(addr0.getLo_crd(), addr0.getLa_crd());
            }
        } else {
            // 데이터가 null
        }
    }


    // bottom sheet 다이얼로그 표시
    private void showBottomSheetDialog(final ResponseAddr addrDetail) {
        String detailText = "";

        llBottom.setVisibility(View.VISIBLE);

        rfgaddr = addrDetail.getSido_sgg_nm();
        tvType.setText("");
        tvType.setVisibility(View.GONE);
        tvAddress.setText(addrDetail.getSpot_nm());
        tvDetail.setText(addrDetail.getSido_sgg_nm());

        Log.d("메시지", "addrDetail.getSpot_nm() : " + addrDetail.getSpot_nm());
        Log.d("메시지", "addrDetail.getSido_sgg_nm() : " + addrDetail.getSido_sgg_nm());
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class RequestResponseTask extends AsyncTask<Double, Void, String> {
        double lat = 0;
        double lng = 0;

        @Override
        protected String doInBackground(Double... request) {
            Log.d("TASK", "티맵 리버스 지오코딩 request 위도 : " + request[0]);
            Log.d("TASK", "티맵 리버스 지오코딩 request 경도 : " + request[1]);

            lat = request[0];
            lng = request[1];

            String address = "";
            try {
                address = new TMapData().convertGpsToAddress(lat, lng);
            } catch (Exception e) {
                Log.e("TASK", "티맵 리버스 지오코딩 에러 " + e.toString());
            }
            return address;
        }

        @Override
        protected void onPostExecute(String response) {
            Log.d("TASK", "티맵 리버스 지오코딩 response 결과 : " + response);

            ResponseAddr addr = new ResponseAddr();
            addr.setSpot_nm(response);
            addr.setSido_sgg_nm(response);
            addr.setLa_crd(lat);
            addr.setLo_crd(lng);
            items.add(addr);

            processMapView(items);
        }
    }

    private void searchLocation(String name) {
        try {
            if (tMapView != null) {
//                tMapView.removeAllTMapCircle(); // 써클만 안지워지도록 주석처리
                tMapView.removeAllMarkerItem();

                // 전체를 지우면 안된다

            }

            items.clear();

            TMapData tmapdata = new TMapData();

            tmapdata.findAllPOI(name, new TMapData.FindAllPOIListenerCallback() {
                @Override
                public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                    for (int i = 0; i < poiItem.size(); i++) {
                        TMapPOIItem item = poiItem.get(i);

                        Log.d(TAG, "검색된 POI Name: " + item.getPOIName().toString() + ", " +
                                "Address: " + item.getPOIAddress().replace("null", "") + ", " + item.getPOIContent() +
                                "Point: " + item.getPOIPoint().toString());

                        ResponseAddr addr = new ResponseAddr();
                        addr.setSpot_nm(item.getPOIName());
                        addr.setSido_sgg_nm(item.getPOIAddress().replace("null", ""));
                        addr.setLa_crd(item.getPOIPoint().getLatitude());
                        addr.setLo_crd(item.getPOIPoint().getLongitude());
                        items.add(addr);
                    }


                    processMapView(items);

                }
            });
        } catch (Exception e) {
            Log.e(TAG, "poi 검색 에러 " + e.toString());
        }
    }

    private void hidekeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}