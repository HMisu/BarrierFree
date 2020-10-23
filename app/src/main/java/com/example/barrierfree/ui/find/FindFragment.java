package com.example.barrierfree.ui.find;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import java.util.List;
import java.util.Locale;

public class FindFragment extends Fragment {

    private String TAG = "FindFragment";


    private FindViewModel findViewModel;

    LinearLayout linearLayoutTmap;
    TMapView tMapView;

    ImageButton btnCurrentLoc;

    ConstraintLayout llBottom;
    TextView tvAddress, tvDetail;
    Button bt_close;

    private BottomSheetDialog mBottomSheetDialog;

    ArrayList<ResponseAddr> items = new ArrayList<>();

    private FusedLocationProviderClient fusedLocationClient; // 마지막에 저장된 현재 위치값을 가져오기 위해 생성

    String AppKey = "7dEldMVxKoZfmjg1iqfBabOSQQ%2B2ysH%2FY9TgK4dPPb3nqa4YRvgpd%2FdAzN8xyFnEeOJpNCYlDcjzRREjwDMuGw%3D%3D"; // 공공데이터 키

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
                                    TMapCircle tMapCircle = new TMapCircle();
                                    tMapCircle.setCenterPoint(tMapPoint);
                                    tMapCircle.setRadius(30);
                                    tMapCircle.setCircleWidth(2);
                                    tMapCircle.setLineColor(Color.BLUE);
                                    tMapCircle.setAreaColor(Color.BLUE);
//                                    tMapCircle.setAreaAlpha(100);
                                    tMapView.addTMapCircle("circle1", tMapCircle);

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

    // 현재 위치의 주소를 가져온다 - 사용안함 - 티맵 api 로 대체
    public void getAddress(Double lat, Double lng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.KOREA);

        try {

            addresses = geocoder.getFromLocation(lat, lng, 1);

            if (addresses != null && addresses.size() > 0) {

                String gugunName = addresses.get(0).getLocality();
                String sidoName = addresses.get(0).getAdminArea();

                // getLocality의 값이 null인 경우 특별시나 광역시
                if(gugunName == null) {
                    gugunName = addresses.get(0).getSubLocality();
                }
                Log.d(TAG, "지오코딩 결과 주소 getLocality : "+addresses.get(0).getLocality()); // 안양시 , 제천시, null
                Log.d(TAG, "지오코딩 결과 주소 getAdminArea : "+addresses.get(0).getAdminArea()); // 경기도, 충청북도, 서울특별시
                Log.d(TAG, "지오코딩 결과 주소 getCountryName : "+addresses.get(0).getCountryName()); // 대한민국
                Log.d(TAG, "지오코딩 결과 주소 getSubLocality : "+addresses.get(0).getSubLocality()); // 만안구 - null - 종로구
                Log.d(TAG, "지오코딩 결과 주소 getFeatureName : "+addresses.get(0).getFeatureName()); // 603-15 - 936 - 123
                Log.d(TAG, "지오코딩 결과 주소 getSubAdminArea : "+addresses.get(0).getSubAdminArea());

                ArrayList<RequestAddr> sidoList = Constant.sidoReqList; // 요청할 시도 구분의 코드값 리스트를 가져온다
                ArrayList<RequestAddr> gugunList = Constant.gugunReqList; // 요청할 구군 구분의 코드값 리스트를 가져온다

                String sidoMatchName = "";
                String gugunMatchName = "";
                int sidoMatchCode = 0;
                int gugunMatchCode = 0;

                for(RequestAddr addr : sidoList) {
                    if(addr.getSidoName().equals(sidoName)) {
                        sidoMatchName = sidoName;
                        sidoMatchCode = addr.getCode();
                    }
                }

                for(RequestAddr addr : gugunList) {
                    if(addr.getGugunName().equals(gugunName)) {
                        gugunMatchName = gugunName;
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

        } catch (IOException e) {

            Log.e(TAG, "주소 지도코딩 에러 "+e.toString());
        }

    }

    // 공공데이터 api 호출
    public void getGongGongData(int sidoCode, int gugunCode) {

        final OkHttpClient client = new OkHttpClient();

        client.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

        try {

            String year = "2018"; // 최근데이터가 잘안나와서 2018년으로 임의로 지정함
            int numOfRows = 10; // 한 번에 검색되는 최대수

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

                    try {

                        JSONObject result = new JSONObject(jsonData);
                        Log.d(TAG, "검색결과 json " + result.toString());


                        String resultCode = result.optString("resultCode", "");
                        Log.d(TAG,"결과 코드 "+resultCode);

                        JSONObject resultItemsObj = result.optJSONObject("items");
                        JSONArray jarr = resultItemsObj.optJSONArray("item");

                        Log.i(TAG, "json item results : " + jarr.toString());

                        String total_count = result.optString("total_count", "0"); // 전체 검색된 수
                        int totalCnt = Integer.parseInt(total_count);


                        if (jarr.length() > 0) {

//                            final ArrayList<ResponseAddr> items = new ArrayList<>();

                            items.clear();

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

                                    String sido_sgg_nm = jjj.optString("sido_sgg_nm", "");
                                    String spot_nm = jjj.optString("spot_nm", "");

                                    model.setSido_sgg_nm(sido_sgg_nm);
                                    model.setSpot_nm(spot_nm);

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


                                    items.add(model);

                                } catch (Exception e) {
                                    Log.e(TAG, "주소 결과 파싱 에러 " + e.toString());
                                }
                            }

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    // 주요 사고지점의 데이터의 써클 표시 좌표 데이터 추출
//                                    try {
//                                        ArrayList<ResponseAddr> tempArr = new ArrayList<>();
//                                        for (int i = 0; i < items.size(); i++) {
//                                            ResponseAddr tempData = items.get(i);
//                                            String geomJsonString = tempData.getGeom_json();
//
//                                            JSONObject geomJson = new JSONObject(geomJsonString);
//
//                                            Log.d(TAG,"점데이터 "+geomJson);
//
////                                            String coordi = geomJson.optString("coordinates", "");
//                                            JSONArray jarr = geomJson.optJSONArray("coordinates");
//
//                                            Log.d(TAG, "점데이터 json array 1단계 "+jarr.toString());
//
//                                            String coordiString = jarr.toString();
//
//                                            String coordiReplace = coordiString.replaceAll("\\[\\[", "");
//                                            coordiReplace = coordiReplace.replaceAll("\\]\\]", "");
//
//                                            Log.d(TAG, "점데이터 json array 데이터 가공중 "+coordiReplace);
//
//                                            String[] coordiSplit = coordiReplace.split("\\],\\[");
//
//                                            if(coordiSplit != null && coordiSplit.length > 0) {
//                                                for(String detailLoc : coordiSplit) {
//
//                                                    detailLoc = detailLoc.replaceAll("\\[", "");
//                                                    detailLoc = detailLoc.replaceAll("\\]", "");
//
//                                                    String[] detailLocArr = detailLoc.split(",");
//
//
//                                                    Log.d(TAG, "점데이터 json array 데이터 가공후 콤마 스플릿 "+detailLoc);
//                                                    Log.d(TAG, "점데이터 json array 데이터 가공후 콤마 스플릿 lng "+detailLocArr[0]);
//                                                    Log.d(TAG, "점데이터 json array 데이터 가공후 콤마 스플릿 lat "+detailLocArr[1]);
//
//                                                    double detail_lat = Double.parseDouble(detailLocArr[1]);
//                                                    double detail_lng = Double.parseDouble(detailLocArr[0]);
//
//
//                                                    ResponseAddr addr = new ResponseAddr();
//                                                    addr.setSpot_nm(tempData.getSpot_nm());
//                                                    addr.setSido_sgg_nm(tempData.getSido_sgg_nm());
//                                                    addr.setLo_crd(detail_lng);
//                                                    addr.setLa_crd(detail_lat);
//                                                    tempArr.add(addr);
//
//                                                }
//                                            }
//
////                                            JSONArray jarr2 = jarr.getJSONArray(0);
//
////                                            Log.d(TAG, "점데이터 json array 2단계 "+jarr2.toString());
//
//                                        }
//
//                                        // 나온 결과로 지도 표시
//                                        processMapView(tempArr);
//
//                                    }catch (Exception e) {
//                                        Log.e(TAG, "json 파싱 에러 "+e.toString());
//
//                                        //  결과로 지도 표시
//                                        processMapView(items);
//
//                                    }

                                    // 결과를 지도 표시
                                    processMapView(items);


                                }
                            });

                        }

                    } catch (Throwable t) {

                        Log.e(TAG, "Could not parse malformed JSON: \"" + t.getMessage() + "\"");

                    } finally {

                        Log.e(TAG, jsonData);

                    }
                }
            });


        } catch (Exception ex) {
            // Handle the error

        }
    }


    // 지도를 표시하는 부분 - 여기에 마커를 표시할 데이터를 파라미터로 받아야 한다
    private void processMapView(final ArrayList<ResponseAddr> items ) {

        if(items != null) {

            final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.poi_dot);
            final Bitmap bitmap_right = BitmapFactory.decodeResource(getResources(), R.drawable.i_go);

            for(int i = 0; i < items.size(); i++) {

                ResponseAddr addr = items.get(i);

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
        }


    }


    // bottom sheet 다이얼로그 표시
    private void showBottomSheetDialog(final ResponseAddr addrDetail) {

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
