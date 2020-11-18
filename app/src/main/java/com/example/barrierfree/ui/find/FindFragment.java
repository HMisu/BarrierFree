package com.example.barrierfree.ui.find;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.barrierfree.Constant;
import com.example.barrierfree.R;
import com.example.barrierfree.models.RequestAddr;
import com.example.barrierfree.models.ResponseAddr;
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
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class FindFragment extends Fragment {

    private String TAG = "FindFragment";


    private FindViewModel findViewModel;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    LinearLayout linearLayoutTmap;
    TMapView tMapView;
    String uid;



    ImageButton btnCurrentLoc;

    ConstraintLayout llBottom;
    TextView tvAddress, tvType, tvDetail;
    Button bt_close;

    private BottomSheetDialog mBottomSheetDialog;

    ArrayList<ResponseAddr> items = new ArrayList<>();

    private FusedLocationProviderClient fusedLocationClient; // 마지막에 저장된 현재 위치값을 가져오기 위해 생성

    String AppKey = "7dEldMVxKoZfmjg1iqfBabOSQQ%2B2ysH%2FY9TgK4dPPb3nqa4YRvgpd%2FdAzN8xyFnEeOJpNCYlDcjzRREjwDMuGw%3D%3D"; // 지자체별 사고다발지역 공공데이터 키

    String OldmanApiKey = "7dEldMVxKoZfmjg1iqfBabOSQQ%2B2ysH%2FY9TgK4dPPb3nqa4YRvgpd%2FdAzN8xyFnEeOJpNCYlDcjzRREjwDMuGw%3D%3D"; // 보행 노인사고 다발지역정보 공공데이터 키

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        System.out.println("FindFragment");
//        findViewModel =
//                ViewModelProviders.of(this).get(FindViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        linearLayoutTmap = (LinearLayout) root.findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(getActivity());
        tMapView.setSKTMapApiKey("l7xx0bfddf7c9eaa4cc48ff35b5f8b73ce9d");

        linearLayoutTmap.addView(tMapView);

        btnCurrentLoc = (ImageButton)root.findViewById(R.id.btnCurrentLoc);
        llBottom = (ConstraintLayout)root.findViewById(R.id.llBottom);

        tvAddress = (TextView)root.findViewById(R.id.tvAddress);
        tvType = (TextView)root.findViewById(R.id.tvType);
        tvDetail = (TextView) root.findViewById(R.id.tvDetail);
        bt_close = (Button)root.findViewById(R.id.bt_close);
        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llBottom.setVisibility(View.GONE);
            }
        });

        llBottom.setVisibility(View.GONE);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());


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

                                        Log.d(TAG, "마지막 위치값 "+location.getLatitude()+" "+location.getLongitude());

                                        final double lat = location.getLatitude();
                                        final double lng = location.getLongitude();

                                        if(tMapView != null) {

                                            tMapView.removeTMapCircle("circle1");

                                            tMapView.setLocationPoint(lng, lat);
                                            tMapView.setCenterPoint(lng, lat);


                                            TMapPoint tMapPoint = new TMapPoint(lat, lng);
                                            TMapCircle tMapCircle = new TMapCircle();
                                            tMapCircle.setCenterPoint(tMapPoint);
                                            tMapCircle.setRadius(30);
                                            tMapCircle.setCircleWidth(2);
                                            tMapCircle.setLineColor(Color.BLUE);
                                            tMapCircle.setAreaColor(Color.BLUE);
//                                            tMapCircle.setAreaAlpha(100);
                                            tMapView.addTMapCircle("circle1", tMapCircle);


                                        }

                                    }
                                }
                            });
//
                }catch (Exception e) {
                    Log.e(TAG, "위치 가죠오기 처리중 에러발생 "+e.toString());
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

        return root;

    }


    @Override
    public void onResume() {
        super.onResume();

        try {


            if(tMapView != null) {
                tMapView.removeAllTMapCircle();
                tMapView.removeAllMarkerItem();


                tMapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
                    @Override
                    public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                        String id = tMapMarkerItem.getID();

                        Log.d(TAG, "마커 클릭 "+id);

//                        Toast.makeText(getActivity(), "클릭 아이디 "+id, Toast.LENGTH_SHORT).show();

                        if(id != null && !"".equals(id)) {
                            int seq = Integer.parseInt(id);
                            if(items != null) {
                                ResponseAddr addr = items.get(seq);
                                showBottomSheetDialog(addr);
                            }
                        }

                    }
                });

            }



        }catch (Exception e) {
            Log.e(TAG,"지도 초기화 에러 "+e.toString());
        }

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

                                Log.d(TAG, "마지막 위치값 "+location.getLatitude()+" "+location.getLongitude());

                                final double lat = location.getLatitude();
                                final double lng = location.getLongitude();


//                                final double lat = 37.570841; // 서울 종로구
//                                final double lng = 126.985302;

//                                final double lat = 37.452811; // 인천광역시 남구 주안동
//                                final double lng = 126.693917;

//                                final double lat = 37.123421;
//                                final double lng = 128.123213;

//                                final double lat = 36.4801027; // 세종특별자치시
//                                final double lng = 127.2868467;

                                if(tMapView != null) {

                                    tMapView.removeTMapCircle("circle1");

                                    tMapView.setLocationPoint(lng, lat);
                                    tMapView.setCenterPoint(lng, lat);

                                    TMapPoint tMapPoint = new TMapPoint(lat, lng);
//                                    TMapCircle tMapCircle = new TMapCircle();
//                                    tMapCircle.setCenterPoint(tMapPoint);
//                                    tMapCircle.setRadius(30);
//                                    tMapCircle.setCircleWidth(2);
//                                    tMapCircle.setLineColor(Color.BLUE);
//                                    tMapCircle.setAreaColor(Color.BLUE);
////                                    tMapCircle.setAreaAlpha(100);
//                                    tMapView.addTMapCircle("circle1", tMapCircle);

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

                                // 주소 지오코딩 처리 호출 - 티맵api 사용
                                RequestResponseTask task = new RequestResponseTask();
                                task.execute(lat, lng);


                                // 주소 지오코딩 처리 호출 - 구글 api 사용 - 주석처리함
//                                getAddress(lat, lng);


                            }
                        }
                    });

        }catch (Exception e) {
            Log.e(TAG, "위치 가죠오기 처리중 에러발생 "+e.toString());
        }
    }


    // 지자체별 사고 데이터
    private void getAreaAccidentData(final int sidoCode, final int gugunCode, final int year) {

        Log.d(TAG, "getAreaAccidentData 실행");

        try {

            final OkHttpClient client = new OkHttpClient();

            client.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

            int numOfRows = 20; // 한 번에 검색되는 최대수

            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                    .url("http://apis.data.go.kr/B552061/frequentzoneLg/getRestFrequentzoneLg?ServiceKey="+AppKey + "&searchYearCd=" + year + "&siDo="+sidoCode+"&guGun="+gugunCode+"&numOfRows="+numOfRows+"&pageNo=1&type=json")
                    .addHeader("Accept", "application/json;")
                    .get()
                    .build();

            client.newCall(request).enqueue(new com.squareup.okhttp.Callback() {

                @Override
                public void onFailure(com.squareup.okhttp.Request request, IOException e) {
                    // 호출 실패
                    Log.e(TAG, request.toString());
                }

                @Override
                public void onResponse(com.squareup.okhttp.Response response) throws IOException {

                    String jsonData = response.body().string();

                    ArrayList<ResponseAddr> tempList = new ArrayList<>();


                    try {

                        JSONObject result = new JSONObject(jsonData);
                        Log.d(TAG, "지자체별 사고 검색결과 json " + result.toString());


                        String resultCode = result.optString("resultCode", "");
                        Log.d(TAG,"결과 코드 "+resultCode);

                        JSONObject resultItemsObj = result.optJSONObject("items");
                        JSONArray jarr = resultItemsObj.optJSONArray("item");

                        Log.i(TAG, "json item results : " + jarr.toString());

                        String total_count = result.optString("total_count", "0"); // 전체 검색된 수
                        int totalCnt = Integer.parseInt(total_count);


                        if (jarr.length() > 0) {

//                            final ArrayList<ResponseAddr> items = new ArrayList<>();

//                            items.clear();

                            for (int i = 0; i < jarr.length(); i++) {
                                ResponseAddr model = new ResponseAddr();

                                try {
                                    JSONObject jjj = jarr.getJSONObject(i);

                                    Log.d(TAG, "각 아이템정보 " + jjj.toString());

//                                    JSONObject road_address_obj = jjj.optJSONObject("la_crd"); // 경도
                                    final String temp_lat = jjj.optString("la_crd", "0"); // 위도
                                    final String temp_lng = jjj.optString("lo_crd", "0"); // 경도

                                    db.collection("location").document(uid).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        final DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            Log.d("취약자 연결 완료", uid);
                                                            Log.d("취약자의 위치값", "위도" + document.getDouble("latitude") + "경도" + document.getDouble("longitude"));
                                                            final int[] isDid = {1};
                                                            final double now_latim = 110.940 * document.getDouble("latitude");
                                                            final double now_longim = 90.180 * document.getDouble("longitude");
                                                            if(isDid[0] == 1) {
                                                                db.collection("safety").whereEqualTo("mem_weak", uid).get()
                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                Log.d("메세지", "위험지역 연결완료");
                                                                                for (final QueryDocumentSnapshot document : task.getResult()) {

                                                                                    double meter_lati, meter_longi;
                                                                                    meter_lati = 110.940 * document.getDouble("latitude");
                                                                                    meter_longi = 90.180 * document.getDouble("longitude");

                                                                                    Log.d("메세지", "현재 위치의 위도 " + now_latim + " 경도 " + now_longim + "위험지역의 위도 " + meter_lati + " 경도 " + meter_longi);

                                                                                    if (now_latim >= (Double.parseDouble(temp_lat) - 500) && now_latim <= (Double.parseDouble(temp_lat) + 500)) {
                                                                                        Log.d("메시지", "if문 하나통과");
                                                                                        if (now_longim >= (Double.parseDouble(temp_lng) - 500) && now_longim <= (Double.parseDouble(temp_lng) + 500)) {
                                                                                            Uri notificaiton = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                                                                                            Ringtone ringtone = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notificaiton);
                                                                                            ringtone.play();
                                                                                        } else {
                                                                                            Log.d("메세지", "위험지역 아님.");

                                                                                        }
                                                                                    }
                                                                                }
                                                                                isDid[0] = 0;
                                                                            }
                                                                        });
                                                            }

                                                        }

                                                    } else {
                                                        Log.d("메시지", "No Such Document");
                                                    }
                                                }
                                            });


                                    double lat = Double.parseDouble(temp_lat); // api 에서 위도값이 문자열로 넘어오기때문에 다시 변환한다
                                    double lng = Double.parseDouble(temp_lng);

                                    model.setLa_crd(lat);
                                    model.setLo_crd(lng);

                                    String geom_json = jjj.optString("geom_json", ""); // 주요 상세 지점들 정보
                                    model.setGeom_json(geom_json);

                                    String sido_sgg_nm = jjj.optString("sido_sgg_nm", ""); // 시도시군구명
                                    String spot_nm = jjj.optString("spot_nm", ""); // 지점명

                                    model.setSido_sgg_nm(sido_sgg_nm);
                                    model.setSpot_nm(spot_nm);

                                    int afos_fid = jjj.optInt("afos_fid", 0); // 다발지역 FID
                                    model.setAfos_fid(afos_fid);

                                    String afos_id = jjj.optString("afos_id", ""); // 다발지역 ID
                                    String bjd_cd = jjj.optString("bjd_cd", ""); // 시도시군구명
                                    String spot_cd = jjj.optString("spot_cd", ""); // 지점명

                                    model.setAfos_id(afos_id);
                                    model.setBjd_cd(bjd_cd);
                                    model.setSpot_cd(spot_cd);

                                    int occrrnc_cnt = jjj.optInt("occrrnc_cnt", 0); // 사고 발생 건수
                                    model.setOccrrnc_cnt(occrrnc_cnt);

                                    int caslt_cnt = jjj.optInt("caslt_cnt", 0); // 사상자수
                                    model.setCaslt_cnt(caslt_cnt);

                                    int dth_dnv_cnt = jjj.optInt("dth_dnv_cnt", 0); // 사망자수
                                    model.setDth_dnv_cnt(dth_dnv_cnt);

                                    int se_dnv_cnt = jjj.optInt("se_dnv_cnt", 0); // 중상자수
                                    model.setSe_dnv_cnt(se_dnv_cnt);

                                    int sl_dnv_cnt = jjj.optInt("sl_dnv_cnt", 0); // 경상자수
                                    model.setSl_dnv_cnt(sl_dnv_cnt);

                                    int wnd_dnv_cnt = jjj.optInt("wnd_dnv_cnt", 0); // 부상신고자수
                                    model.setWnd_dnv_cnt(wnd_dnv_cnt);

                                    model.setType(0); // 데이터 타입 설정

//                                    items.add(model);

                                    tempList.add(model);

                                } catch (Exception e) {
                                    Log.e(TAG, "주소 결과 파싱 에러 " + e.toString());
                                }
                            }


                        }

                    } catch (Throwable t) {

                        Log.e(TAG, "Could not parse malformed JSON: \"" + t.getMessage() + "\"");

                    } finally {

                        Log.d(TAG, "getAreaAccidentData 결과 데이터 수 "+tempList.size());
                        Log.e(TAG, jsonData);
                        items.addAll(tempList);

                        if(year == 2017) {
                            getAreaAccidentData(sidoCode, gugunCode, 2018);

                        }else if(year == 2018) {
                            getOldmanAccidentData(sidoCode, gugunCode, 2018);
                        }
                    }
                }
            });



        }catch (Exception e) {
            Log.e(TAG, "지자체별 사고 데이터 조회 에러 "+e.toString());

        }


    }

    // 노인 사고 데이터
    private void getOldmanAccidentData(int sidoCode, int gugunCode, int year) {

        Log.d(TAG, "getOldmanAccidentData 실행");

        final ArrayList<ResponseAddr> dataList = new ArrayList<>();

        try {

            final OkHttpClient client = new OkHttpClient();

            client.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

            int numOfRows = 20; // 한 번에 검색되는 최대수

            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                    .url("http://apis.data.go.kr/B552061/frequentzoneOldman/getRestFrequentzoneOldman?ServiceKey="+OldmanApiKey + "&searchYearCd=" + year + "&siDo="+sidoCode+"&guGun="+gugunCode+"&numOfRows="+numOfRows+"&pageNo=1&type=json")
                    .addHeader("Accept", "application/json;")
                    .get()
                    .build();

            client.newCall(request).enqueue(new com.squareup.okhttp.Callback() {

                @Override
                public void onFailure(com.squareup.okhttp.Request request, IOException e) {
                    // 호출 실패
                    Log.e(TAG, request.toString());
                }

                @Override
                public void onResponse(com.squareup.okhttp.Response response) throws IOException {

                    String jsonData = response.body().string();

                    ArrayList<ResponseAddr> tempList = new ArrayList<>();

                    try {

                        JSONObject result = new JSONObject(jsonData);
                        Log.d(TAG, "노인 사고 검색결과 json " + result.toString());


                        String resultCode = result.optString("resultCode", "");
                        Log.d(TAG,"결과 코드 "+resultCode);

                        JSONObject resultItemsObj = result.optJSONObject("items");
                        JSONArray jarr = resultItemsObj.optJSONArray("item");

                        Log.i(TAG, "json item results : " + jarr.toString());

                        String total_count = result.optString("total_count", "0"); // 전체 검색된 수
                        int totalCnt = Integer.parseInt(total_count);


                        if (jarr.length() > 0) {

//                            final ArrayList<ResponseAddr> items = new ArrayList<>();

//                            items.clear();

                            for (int i = 0; i < jarr.length(); i++) {
                                ResponseAddr model = new ResponseAddr();

                                try {
                                    JSONObject jjj = jarr.getJSONObject(i);

                                    Log.d(TAG, "각 아이템정보 " + jjj.toString());

//                                    JSONObject road_address_obj = jjj.optJSONObject("la_crd"); // 경도
                                    String temp_lat = jjj.optString("la_crd", "0"); // 위도
                                    String temp_lng = jjj.optString("lo_crd", "0"); // 경도

                                    double lat = Double.parseDouble(temp_lat); // api 에서 위도값이 문자열로 넘어오기때문에 다시 변환한다
                                    double lng = Double.parseDouble(temp_lng);

                                    model.setLa_crd(lat);
                                    model.setLo_crd(lng);

                                    String geom_json = jjj.optString("geom_json", ""); // 주요 상세 지점들 정보
                                    model.setGeom_json(geom_json);

                                    String sido_sgg_nm = jjj.optString("sido_sgg_nm", ""); // 시도시군구명
                                    String spot_nm = jjj.optString("spot_nm", ""); // 지점명

                                    model.setSido_sgg_nm(sido_sgg_nm);
                                    model.setSpot_nm(spot_nm);

                                    int afos_fid = jjj.optInt("afos_fid", 0); // 다발지역 FID
                                    model.setAfos_fid(afos_fid);

                                    String afos_id = jjj.optString("afos_id", ""); // 다발지역 ID
                                    String bjd_cd = jjj.optString("bjd_cd", ""); // 시도시군구명
                                    String spot_cd = jjj.optString("spot_cd", ""); // 지점명

                                    model.setAfos_id(afos_id);
                                    model.setBjd_cd(bjd_cd);
                                    model.setSpot_cd(spot_cd);

                                    int occrrnc_cnt = jjj.optInt("occrrnc_cnt", 0); // 사고 발생 건수
                                    model.setOccrrnc_cnt(occrrnc_cnt);

                                    int caslt_cnt = jjj.optInt("caslt_cnt", 0); // 사상자수
                                    model.setCaslt_cnt(caslt_cnt);

                                    int dth_dnv_cnt = jjj.optInt("dth_dnv_cnt", 0); // 사망자수
                                    model.setDth_dnv_cnt(dth_dnv_cnt);

                                    int se_dnv_cnt = jjj.optInt("se_dnv_cnt", 0); // 중상자수
                                    model.setSe_dnv_cnt(se_dnv_cnt);

                                    int sl_dnv_cnt = jjj.optInt("sl_dnv_cnt", 0); // 경상자수
                                    model.setSl_dnv_cnt(sl_dnv_cnt);

                                    int wnd_dnv_cnt = jjj.optInt("wnd_dnv_cnt", 0); // 부상신고자수
                                    model.setWnd_dnv_cnt(wnd_dnv_cnt);

                                    model.setType(1); // 데이터 타입 설정

//                                    items.add(model);
                                    tempList.add(model);

                                } catch (Exception e) {
                                    Log.e(TAG, "주소 결과 파싱 에러 " + e.toString());
                                }
                            }

                        }

                    } catch (Throwable t) {

                        Log.e(TAG, "Could not parse malformed JSON: \"" + t.getMessage() + "\"");

                    } finally {
                        Log.d(TAG, "getOldmanAccidentData 결과 데이터 수 "+tempList.size());
                        Log.e(TAG, jsonData);

                        items.addAll(tempList);



                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                // 결과를 지도 표시
                                processMapView(items);

                            }
                        });

                    }
                }
            });

        }catch (Exception e) {
            Log.e(TAG, "노인 사고 데이터 조회 에러 "+e.toString());
        }

    }


    // 공공데이터 api 호출
    public void getGongGongData(int sidoCode, int gugunCode) {


        try {
            items.clear();

            getAreaAccidentData(sidoCode, gugunCode, 2017);


        } catch (Exception ex) {
            Log.e(TAG, "데이터 호출 처리 에러 "+ex.toString());
        }
    }



    // 지도를 표시하는 부분 - 여기에 마커를 표시할 데이터를 파라미터로 받아야 한다
    private void processMapView(final ArrayList<ResponseAddr> datas ) {

        Log.d(TAG, "processMapView 실행");
        if(items != null) {

            Log.d(TAG,"데이터가 있다 "+items.size());

            final Bitmap bitmap_right = BitmapFactory.decodeResource(getResources(), R.drawable.i_go);

            for(int i = 0; i < items.size(); i++) {

                ResponseAddr addr = items.get(i);

                Bitmap bitmap = null;
                if(addr.getType() == 1) {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.loc_pin_blue);
                }else {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.loc_pin_red);
                }

                TMapPoint tMapPoint = new TMapPoint(addr.getLa_crd(), addr.getLo_crd());

                TMapMarkerItem marker = new TMapMarkerItem();
                marker.setID(String.valueOf(i));
                marker.setTMapPoint(tMapPoint);
                marker.setIcon(bitmap);
                marker.setName(addr.getSpot_nm());
                marker.setCalloutTitle(addr.getSpot_nm()); // 풍선뷰 표시 메시지 설정
                marker.setCanShowCallout(true); // 풍선뷰 사용 설정
                marker.setAutoCalloutVisible(false); // 풍선뷰가 자동으로 활성화되도록 설정
                marker.setCalloutRightButtonImage(bitmap_right);
                


//                TMapMarkerItem2 marker2 = new TMapMarkerItem2();
//                MarkerOverlay overlay = new MarkerOverlay(getActivity(), tMapView);
//                overlay.setID("id"+i);
//                overlay.setTMapPoint(tMapPoint);

//                TMapCircle tMapCircle = new TMapCircle();
//                tMapCircle.setCenterPoint(tMapPoint);
//                tMapCircle.setRadius(300);
//                tMapCircle.setCircleWidth(2);
//                tMapCircle.setLineColor(Color.BLUE);
//                tMapCircle.setAreaColor(Color.GRAY);
//                tMapCircle.setAreaAlpha(100);
//                tMapView.addTMapCircle("circle1", tMapCircle);

                tMapView.addMarkerItem(String.valueOf(i), marker);

//                tMapView.addMarkerItem2("marker"+i, overlay);
            }

            // 지도의 중심을 첫번째 데이터의 위치로 지정
            if(items.size() > 0) {
                ResponseAddr addr0 = items.get(0);
                tMapView.setLocationPoint(addr0.getLo_crd(), addr0.getLa_crd());
                tMapView.setCenterPoint(addr0.getLo_crd(), addr0.getLa_crd());
            }


        }else {
            // 데이터가 null
            Log.d(TAG, "데이터가 없다");
        }


    }


    // bottom sheet 다이얼로그 표시
    private void showBottomSheetDialog(final ResponseAddr addrDetail) {

        String type = "";
        if(addrDetail.getType() == 0) {
            type = "지자체별 사고다발 지역";

        }else if(addrDetail.getType() == 1) {
            type = "보행 노인 사고 다발 지역";
        }


        String detailText = "사고 발생 건수 : "+ addrDetail.getOccrrnc_cnt()+"건\n사상자수 : "+addrDetail.getCaslt_cnt()+"명\n";
        detailText += "사망자수 : "+addrDetail.getDth_dnv_cnt()+"명\n중상자수 : "+addrDetail.getSe_dnv_cnt()+"명\n";
        detailText += "경상자수 : "+addrDetail.getSl_dnv_cnt()+"명\n부상 신고자수 : "+addrDetail.getWnd_dnv_cnt()+"명";

//        final View view = getLayoutInflater().inflate(R.layout.bottom_sheet_loc, null);
//        ((TextView) view.findViewById(R.id.tvAddress)).setText(addrDetail.getSpot_nm());
//        ((TextView) view.findViewById(R.id.tvDetail)).setText(detailText);
//        (view.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mBottomSheetDialog.dismiss();
//            }
//        });
//
//        mBottomSheetDialog = new BottomSheetDialog(getActivity());
//        mBottomSheetDialog.setContentView(view);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
//
//        mBottomSheetDialog.show();
//        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                mBottomSheetDialog = null;
//            }
//        });


        llBottom.setVisibility(View.VISIBLE);

        tvType.setText(type);
        tvAddress.setText(addrDetail.getSpot_nm());
        tvDetail.setText(detailText);

    }


    private class RequestResponseTask extends AsyncTask<Double, Void, String> {

        @Override
        protected String doInBackground(Double... request) {
            Log.d("TASK", "티맵 지오코딩 request 위도 : " + request[0]);
            Log.d("TASK", "티맵 지오코딩 request 경도 : " + request[1]);

            String address = "";
            try {
                address = new TMapData().convertGpsToAddress(request[0], request[1]);
            }catch (Exception e) {
                Log.e("TASK", "티맵 지오코딩 에러 " + e.toString());
            }
//            return getResponse(request[0]);
            return address;
        }
        @Override
        protected void onPostExecute(String response) {
            Log.d("TASK", "티맵 지오코딩 response 결과 : " + response);

            ArrayList<RequestAddr> sidoList = Constant.sidoReqList; // 요청할 시도 구분의 코드값 리스트를 가져온다
            ArrayList<RequestAddr> gugunList = Constant.gugunReqList; // 요청할 구군 구분의 코드값 리스트를 가져온다

            String sidoMatchName = "";
            String gugunMatchName = "";
            int sidoMatchCode = 0;
            int gugunMatchCode = 0;

            String[] addrArr = response.split(" ");

            for(RequestAddr addr : sidoList) {
                if(addr.getSidoName().equals(addrArr[0])) {
                    sidoMatchName = addrArr[0];
                    sidoMatchCode = addr.getCode();
                }
            }

            String gugun = addrArr[1];

            if("세종특별자치시".equals(sidoMatchName)) {
                gugun = "세종특별자치시";
            }

            for(RequestAddr addr : gugunList) {
                if(addr.getGugunName().equals(gugun)) {
                    gugunMatchName = gugun;
                    gugunMatchCode = addr.getCode();
                }
            }


            Log.d(TAG,"지오코딩 후 매칭된 시도 이름 "+sidoMatchName);
            Log.d(TAG,"지오코딩 후 매칭된 시도 코드 "+sidoMatchCode);
            Log.d(TAG,"지오코딩 후 매칭된 구군 이름 "+gugunMatchName);
            Log.d(TAG,"지오코딩 후 매칭된 구군 코드 "+gugunMatchCode);


            // 여기서 공공데이터 api를 호출해야한다
            getGongGongData(sidoMatchCode, gugunMatchCode);

        }
    }
}