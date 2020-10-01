package com.example.barrierfree.service;

import android.os.Handler;

public class ServiceThread2 extends Thread {
    Handler handler;
    boolean isRun = true;

    public ServiceThread2(Handler handler) {
        this.handler = handler;
    }

    public void stopForever() {
        synchronized (this) {
            this.isRun = false;
        }
    }

    public void run() {
        //반복적으로 수행할 작업을 한다
        while (isRun) {
            handler.sendEmptyMessage(0); //쓰레드에 있는 핸들러에게 메세지를 보냄
            try {
                Thread.sleep( 60000 ); //1000->1초씩 쉰다.
            } catch (Exception e) {
            }
        }
    }
}
