package com.example.barrierfree;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UserVibratorActivity extends AppCompatActivity {

    Button start_btn;
    Button record_btn;
    Button vibrator_btn;
    TextView myRec;
    TextView myOutput;
    String[] sec = new String[30];
    long[] pattern = new long[sec.length];
    long outTime;
    String second="";

    final static int Init = 0;

    final static int Run = 1;
    final static int Pause = 2;

    int vib = 0;
    int cur_Status = Init;
    int myCount = 1;
    int count = 0;
    long myBaseTime;
    long myPauseTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_vibrator);

        myOutput = (TextView) findViewById(R.id.time_out);
        myRec = (TextView)findViewById(R.id.textView2);
        start_btn = (Button)findViewById(R.id.user_btn_start);
        record_btn = (Button)findViewById(R.id.user_btn_record);

    }

    protected void onDestroy(){
        super.onDestroy();
    }

    public void myOnClick(View v){
        switch (v.getId()){
            case R.id.user_btn_start:
                switch (cur_Status){
                    case Init:
                        myBaseTime = SystemClock.elapsedRealtime();
                        System.out.println((myBaseTime));
                        myTimer.sendEmptyMessage(0);
                        start_btn.setText("멈춤");
                        record_btn.setText("시간초 만큼 진동");
                        record_btn.setEnabled(true);
                        cur_Status = Run;
                        break;

                    case Run:
                        myPauseTime = SystemClock.elapsedRealtime();
                        start_btn.setText("시작");
                        record_btn.setText("리셋");
                        cur_Status = Pause;
                        break;

                    case Pause:
                        long now = SystemClock.elapsedRealtime();
                        myBaseTime += (now-myPauseTime);
                        start_btn.setText("멈춤");
                        if(myCount % 2 == 0){
                            record_btn.setText("시간초 만큼 진동");
                            myCount++;
                        }
                        else {
                            record_btn.setText("시간초 만큼 대기");
                            myCount++;
                        }

                        cur_Status = Run;
                        break;
                }
                break;
            case R.id.user_btn_record:
                switch (cur_Status){
                    case Run:
                        String str = myRec.getText().toString();

                        if(count == 0){
                            pattern[count] = outTime;
                            str += String.format("%d. %s만큼 진동\n", count+1, getTimeOut());
                            myRec.setText(str);
                            count++;
                        }
                        else if (count % 2 == 1) {
                            pattern[count] = outTime - pattern[count - 1];
                            str += String.format("%d. %s만큼 대기\n", count+1, Integer.parseInt(getTimeOut()) - pattern[count - 1]);
                            myRec.setText(str);
                            count++;
                        }
                        else {
                            pattern[count] = outTime - pattern[count - 1];
                            str += String.format("%d. %s만큼 진동\n", count+1, Integer.parseInt(getTimeOut()) - pattern[count - 1]);
                            myRec.setText(str);
                            count++;
                        }

                        break;

                    case Pause:
                        //핸들러를 멈춤
                        myTimer.removeMessages(0);

                        start_btn.setText("시작");
                        if(myCount % 2 == 0){
                            record_btn.setText("시간초 만큼 진동");
                            myCount++;
                        }
                        else {
                            record_btn.setText("시간초 만큼 대기");
                            myCount++;
                        }
                        cur_Status = Init;
                        myCount = 0;
                        final Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(pattern,-1);

                        record_btn.setEnabled(false);


                        break;
                }
                break;

        }
    }
    Handler myTimer = new Handler(){
        public void handleMessage(Message msg){
            myOutput.setText(getTimeOut());


            //sendEmptyMessage 는 비어있는 메세지를 Handler 에게 전송하는겁니다.
            myTimer.sendEmptyMessage(0);
        }
    };
    //현재시간을 계속 구해서 출력하는 메소드
    String getTimeOut(){
        long now = SystemClock.elapsedRealtime(); //애플리케이션이 실행되고나서 실제로 경과된 시간(??)^^;
        outTime = now - myBaseTime;
        String easy_outTime = String.format("%d", outTime);

        return easy_outTime;
    }
}
