package com.example.imchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TcpIm tcpIm;
    private RecoderViewShopGroup show_receiver;
    private EditText et_input_content;
    private TextView tv_check;
    private Button bt_send;
    private ScrollView scrollView;
    //网络监听
    private IntentFilter intentFilter;
    private NetworkChangReceiver networkChangeReceiver;
    //断网=0     !bound=0   断开连接=0   重连次数
    private int a = 0;
    private int b = 0;
    private int c = 0;
    private int d = 0;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        initChat();
        scrollView = findViewById(R.id.scrollView);
        tv_check = findViewById(R.id.tv_check);
        show_receiver = findViewById(R.id.show_receiver);
        et_input_content = findViewById(R.id.et_input_content);
        bt_send = findViewById(R.id.bt_send);
        click();
        checkOnLine();
    }

    private void click() {
        //发送
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tcpIm != null&&tcpIm.getSocket()!=null) {
                    final String content = et_input_content.getText().toString().trim();
                    if (content.equals("")) {
                        Toast.makeText(MainActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            tcpIm.sendMessage(content);
                        }
                    }).start();
                    return;
                }
                Toast.makeText(MainActivity.this, "tcpIm==null||socket==null", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //--------------------------工具类--------------------------
    private void checkOnLine()//初始化检测是否在线
    {
        // 实现网络状态监听
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangReceiver();
        this.registerReceiver(networkChangeReceiver, intentFilter);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (tcpIm != null) {
                    if (tcpIm.getSocket() != null) {
                        if (!tcpIm.getSocket().isBound()) {
                            b++;
                            stopTimer();
                            handler.sendEmptyMessage(2);
                            return;
                        }
                        if (!tcpIm.getSocket().isConnected()) {
                            c++;
                            stopTimer();
                            handler.sendEmptyMessage(2);
                            return;
                        }
                    }
                }
            }
        }, 5000, 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    class NetworkChangReceiver extends BroadcastReceiver //监听网络
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) MainActivity.this.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
            } else {
                stopTimer();
                a++;
                handler.sendEmptyMessage(2);
            }
        }
    }

    private void initChat() //聊天初始化
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                tcpIm = new TcpIm();
                tcpIm.initIm("test.ws.52binya.com", 18282, new TcpIm.MsgReceiverCallBack() {
                    @Override
                    public void receiverMsg(String msg) {
                        Message msgSend = new Message();
                        msgSend.obj = msg;
                        msgSend.what = 1;
                        handler.sendMessage(msgSend);
                    }
                });
            }
        }).start();
    }

    public String getTimeToday() //获取今天时间
    {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //服务器消息接收
            if (msg.what == 1) {
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView textView = new TextView(MainActivity.this);
                textView.setPadding(0, 0, 0, 0);
                textView.setText(((String) msg.obj).trim() + getTimeToday());
                show_receiver.addView(textView, lp);
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
            //数据展示
            else if (msg.what == 2) {
                d++;
                tv_check.setText("断网=" + a + "，!bound=0" + b + "，断开连接=" + c + ",重连=" + d);
                initChat();
                checkOnLine();
            }
        }
    };
}
