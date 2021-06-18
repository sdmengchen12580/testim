package com.example.imchat;

import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpIm {

    //发送数据的客户端Socket
    private Socket socket;
    private OutputStream out = null;
    private InputStream inputStream = null;
    private static String TAG = "测试im";
    private MsgReceiverCallBack msgReceiverCallBack;

    public Socket getSocket() {
        return socket;
    }

    public interface MsgReceiverCallBack {
        void receiverMsg(String msg);
    }

    public TcpIm(){

    }

    //接收方的ip地址 接收方的端口号
    public void initIm(String ip, int port, MsgReceiverCallBack msgReceiverCallBack) {
        try {
            close();
            this.msgReceiverCallBack = msgReceiverCallBack;
            socket = new Socket(ip, port);
            Log.e(TAG, "初始化socket：isBound" + socket.isBound() + " isConnected" + socket.isConnected());
            out = socket.getOutputStream();
            inputStream = socket.getInputStream();
            //FIXME 读取
            byte buffer[] = new byte[10 * 1024];
            int temp = 0;
            // 从InputStream当中读取客户端所发送的数据
            while (true) {
                if ((temp = inputStream.read(buffer)) != -1) {
                    String content = new String(buffer, 0, temp);
                    Log.e(TAG, "接收消息=" + content);  //打印接收到的信息
                    if (msgReceiverCallBack != null) {
                        msgReceiverCallBack.receiverMsg(content);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //发送数据
    public void sendMessage(String msg) {
        Log.e(TAG, "发送消息时的状态：isBound" + socket.isBound() + " isConnected" + socket.isConnected());
        try {
            out.write(msg.getBytes());
            out.flush();
            Log.e(TAG, "发送消息=" + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //关闭连接
    public void close() {
        try{
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket.isInputShutdown()) { //判断输入流是否为打开状态
                try {
                    socket.shutdownInput();  //关闭输入流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket.isOutputShutdown()) {  //判断输出流是否为打开状态
                try {
                    socket.shutdownOutput(); //关闭输出流（如果是在给对方发送数据，发送完毕之后需要关闭输出，否则对方的InputStream可能会一直在等待状态）
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket.isConnected()) {  //判断是否为连接状态
                try {
                    socket.close();  //关闭socket
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){}
    }
}
