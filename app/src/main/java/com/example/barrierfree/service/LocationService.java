package com.example.barrierfree.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.barrierfree.models.LocationMember;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;

public class LocationService extends Service {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    ServiceThread thread1;
    ServiceThread2 thread2;

    LocationManager lm;
    double longitude1, latitude1, longitude2, latitude2;
    String time1, time2;
    Date d1, d2;
    DateFormat df;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e("메시지", "onCreate()");
        // LocationListener 핸들
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("메시지", "onStartCommand()");
        myServiceHandler handler = new myServiceHandler();
        myServiceHandler2 handler2 = new myServiceHandler2();
        thread1 = new ServiceThread(handler);
        thread1.stopForever();
        thread2 = new ServiceThread2(handler2);
        thread2.start();
        //return super.onStartCommand(intent, flags, startId);
        //Service가 강제 종료되었을 경우 시스템이 다시 Service를 재시작 시켜 주지만 intent 값을 null로 초기화 시켜서 재시작
        return START_STICKY;
    }

    //서비스가 종료될 때 할 작업
    @Override
    public void onDestroy() {
        Log.e("메시지", "onDestroy()");
        myServiceHandler handler = new myServiceHandler();
        myServiceHandler2 handler2 = new myServiceHandler2();
        thread1 = new ServiceThread(handler);
        thread1.start();
        thread2 = new ServiceThread2(handler2);
        thread2.stopForever();
        //super.onDestroy();
    }

    //https://lcw126.tistory.com/291
    @Override
    public void onTaskRemoved(Intent rootIntent) { //핸들링 하는 부분
        Log.e("Error", "onTaskRemoved - 강제 종료 " + rootIntent);
        myServiceHandler handler = new myServiceHandler();
        myServiceHandler2 handler2 = new myServiceHandler2();
        thread1 = new ServiceThread(handler);
        thread1.start();
        thread2 = new ServiceThread2(handler2);
        thread2.stopForever();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("메시지", "onUnbind()");
        return super.onUnbind(intent);
    }

    public class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            //https://bottlecok.tistory.com/54
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location location = lm.getLastKnownLocation(GPS_PROVIDER);
                String provider = location.getProvider();
                longitude2 = location.getLongitude();
                latitude2 = location.getLatitude();

                df = new SimpleDateFormat("yy:MM:dd:HH:mm", Locale.KOREA);
                Calendar cal = Calendar.getInstance();
                time2 = df.format(cal.getTime());
                try {
                    d2 = df.parse(time2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Log.d("메시지", "위치정보 : " + provider + "\n" +
                        "위도 : " + longitude2 + "\n" +
                        "경도 : " + latitude2 + "\n" +
                        "시간 : " + time2);

                lm.requestLocationUpdates(GPS_PROVIDER, 1000, 1, gpsLocationListener);
                lm.requestLocationUpdates(NETWORK_PROVIDER, 1000, 1, gpsLocationListener);

                saveLocation();
            }
        }
    }

    public class myServiceHandler2 extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            //https://bottlecok.tistory.com/54
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location location = lm.getLastKnownLocation(GPS_PROVIDER);
                String provider = location.getProvider();
                longitude2 = location.getLongitude();
                latitude2 = location.getLatitude();

                df = new SimpleDateFormat("yy:MM:dd:HH:mm", Locale.KOREA);
                Calendar cal = Calendar.getInstance();
                time2 = df.format(cal.getTime());
                try {
                    d2 = df.parse(time2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Log.d("메시지", "위치정보 : " + provider + "\n" +
                        "위도 : " + longitude2 + "\n" +
                        "경도 : " + latitude2 + "\n" +
                        "시간 : " + time2);

                lm.requestLocationUpdates(GPS_PROVIDER, 1000, 1, gpsLocationListener);
                lm.requestLocationUpdates(NETWORK_PROVIDER, 1000, 1, gpsLocationListener);

                saveLocation();
            }
        }
    }

    public void saveLocation(){
        db.collection("location").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        longitude1 = document.getDouble("longitude");
                        latitude1 = document.getDouble("latitude");
                        time1 = document.getString("time");

                        //미터(Meter) 단위 거리 계산
                        double distanceMeter = distance(latitude1, longitude1, latitude2, longitude2, "meter");

                        long minute = 0, diff = 0;
                        if(time1 != null){
                            try {
                                d1 = df.parse(time1);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            diff = d2.getTime() - d1.getTime();
                            minute = diff / 60000;
                        }

                        if (distanceMeter >= 100 || (time1 != null && minute >= 5)) {
                            LocationMember mem = new LocationMember(latitude2, longitude2, time2);
                            Log.d("메시지", String.valueOf(mem));
                            db.collection("location").document(user.getUid())
                                    .set(mem)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("메시지", "DocumentSnapshot successfully written!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("메시지", "Error writing document", e);
                                        }
                                    });
                        }
                    } else {
                        Log.d("메시지", "No such document");
                        LocationMember mem = new LocationMember(latitude2, longitude2, time2);
                        Log.d("메시지", time2);
                        db.collection("location").document(user.getUid())
                                .set(mem)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("메시지", "DocumentSnapshot successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("메시지", "Error writing document", e);
                                    }
                                });
                    }
                } else {
                    Log.d("메시지", "get failed with ", task.getException());
                }
            }
        });
    }
    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    /* 두 지점간의 거리 계산
     * @param lat1 지점 1 위도
     * @param lon1 지점 1 경도
     * @param lat2 지점 2 위도
     * @param lon2 지점 2 경도
     * @param unit 거리 표출단위
     * @return
     */
    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if (unit == "meter") {
            dist = dist * 1609.344;
        }
        return (dist);
    }

    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
