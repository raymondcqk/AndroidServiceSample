package com.raymondqck.serviceconnection;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Service 与 Activity 通信
 * 1- intent putExtras
 * 2- bindService
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etInput;
    private TextView tvResult;
    private ServiceConnection myServiceConectcion;
    MyService.MyBind mMyBind;

    MyService.CallBack mCallBack;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tvResult.setText(msg.getData().getString("log"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }

    private void initView() {
        findViewById(R.id.btnStartService).setOnClickListener(this);
        findViewById(R.id.btnStopService).setOnClickListener(this);
        findViewById(R.id.btnBindService).setOnClickListener(this);
        findViewById(R.id.btnUnBindService).setOnClickListener(this);
        findViewById(R.id.btnSycnService).setOnClickListener(this);
        etInput = (EditText) findViewById(R.id.etInput);
        tvResult = (TextView) findViewById(R.id.tvResult);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartService:
                tvResult.setText("btnStartService");
                Intent i = new Intent(this, MyService.class);
                i.putExtra("data", etInput.getText().toString());

                startService(i);
                break;
            case R.id.btnStopService:
                stopService(new Intent(this, MyService.class));
                break;
            case R.id.btnBindService:
                myServiceConectcion = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        Log.i("MyService", "--->OnServiceConnected()");
                        mMyBind = (MyService.MyBind) service;
                        tvResult.setText(mMyBind.getData());
                        MyService myService = mMyBind.getService();
                        myService.setCallBack(new MyService.CallBack() {
                            @Override
                            public void onDataChanged(String log) {
                                Message msg = new Message();
                                Bundle b = new Bundle();
                                //b.putString("log", log);
                                String str = mMyBind.getLog();
                                b.putString("log",str);
                                msg.setData(b);
                                mHandler.sendMessage(msg);
                            }
                        });
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {

                    }
                };
                Intent iBind = new Intent(this, MyService.class);
                bindService(iBind, myServiceConectcion, BIND_AUTO_CREATE);
                break;
            case R.id.btnUnBindService:
                unbindService(myServiceConectcion);
                break;
            case R.id.btnSycnService:
                if (mMyBind != null) {
                    String data = etInput.getText().toString();
                    if (!data.equals("")) {
                        mMyBind.setData(data);
                        etInput.setText("");
                    } else {
                        mMyBind.setData("用户输入为空");
                    }
                }
                break;


        }
    }
}
