package com.raymondqck.serviceconnection;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by 陈其康 raymondchan on 2016/5/29 0029.
 */
public class MyService extends Service {
    private String mData;
    private int num;
    private boolean mykey;

    private String log;

    MyBind mMyBind;
    private CallBack mCallBack;


    public CallBack getCallBack() {
        return mCallBack;
    }

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    public static interface CallBack {
        public void onDataChanged(String log);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("MyService", "--->onBind()");
        mMyBind = new MyBind();
        return mMyBind;
    }

    public class MyBind extends Binder {
        public String getData() {
            return mData;
        }

        public String getLog() {
            return log;
        }

        public void setData(String data) {
            mData = data;
        }

        public MyService getService() {
            return MyService.this;
        }

        public void setLog(String str) {
            log = str;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("MyService", "--->OnCreate()");
        mykey = true;
        num = 0;
        //        mData = "MyService OnCreate Run";
        mData = "测试通过IBind获取Service的状态";
        new Thread() {
            @Override
            public void run() {
                super.run();
                //mykey
                while (mykey) {
                    if (mCallBack != null) {
                        log = num + ":" + mData;
                        Log.i("MyService", num + ":" + mData);

                        mCallBack.onDataChanged(log);//实时通知Activity更新数据
                        num++;
                        try {
                            sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //System.out.println(num + ":" + mData);
                    //mMyBind.setLog(num + ":" + mData);

                }

            }
        }.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mData = intent.getStringExtra("data");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mykey = false;

    }
}
