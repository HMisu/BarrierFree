package com.example.barrierfree;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class UserVibratorActivity extends AppCompatActivity {


    TextView timer, textView;
    Button vibButton, stop, save;
    String[] sec = new String[30];
    long[] pattern = new long[sec.length];

    int count = 0;

    long outTime;
    long myBaseTime;
    long myPauseTime;
    Vibrator myvib;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_vibrator);

        vibButton = (Button)findViewById(R.id.button);
        stop = (Button)findViewById(R.id.button2);
        save = (Button)findViewById(R.id.save_btn);
        timer = (TextView)findViewById(R.id.textView);
        myvib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);


        vibButton.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event)
            {
                if(MotionEvent.ACTION_DOWN == event.getAction())
                {
                    myBaseTime = SystemClock.elapsedRealtime();
                    System.out.println((myBaseTime));
                    myTimer.sendEmptyMessage(0);
                    myvib.vibrate(8000);
                    Log.d("메세지", "버튼이 눌려짐");
                    if(count == 0){
                        pattern[count] = outTime;
                        count++;
                    } else if(count % 2 == 0){
                        pattern[count-1] = outTime;
                        count++;
                    }
                }
                if(MotionEvent.ACTION_UP == event.getAction())
                {
                    myBaseTime = SystemClock.elapsedRealtime();
                    System.out.println((myBaseTime));
                    myTimer.sendEmptyMessage(0);
                    pattern[count-1] = outTime;
                    count++;
                    myvib.cancel();
                    Log.d("메세지", "버튼이 올라감");
                }
                return true;
            }});

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTimer.removeMessages(0);
                timer.setText("기록중지");
                stop.setText("다시 진동");
                myvib.vibrate(pattern, -1);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Long> arrayList = new ArrayList<>();
                for(long temp : pattern) {
                    arrayList.add(temp);
                }
                Log.d("진동패턴", String.valueOf(arrayList));
                db.collection("alert").document(user.getUid()).update("user_vibe", arrayList);
            }
        });
    }

    Handler myTimer = new Handler(){
        public void handleMessage(Message msg){
            timer.setText(getTimeOut());


            //sendEmptyMessage 는 비어있는 메세지를 Handler 에게 전송하는겁니다.
            myTimer.sendEmptyMessage(0);
        }
    };

    String getTimeOut(){
        long now = SystemClock.elapsedRealtime(); //애플리케이션이 실행되고나서 실제로 경과된 시간(??)^^;
        outTime = now - myBaseTime;
        String easy_outTime = String.format("%d", outTime);

        return easy_outTime;
    }


}