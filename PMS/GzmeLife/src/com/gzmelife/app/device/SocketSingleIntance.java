package com.gzmelife.app.device;


import com.gzmelife.app.tools.MyLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Created by HHD on 2016/11/4.
 *
 * 一个安卓项目一个Socket
 */

public class SocketSingleIntance {

    MyLogger HHDLog=MyLogger.HHDLog();

    /** 声明InputStream */
    private InputStream input =null;
    /** 声明OutputStream */
    private OutputStream output =null;
    /** 声明Socket */
    private Socket socket;
    private OnReceiver receiver;


    /** 私有化静态（全局）实例 */
    private static SocketSingleIntance socketSingleIntance;
    /** 私有化构造方法 */
    private SocketSingleIntance(){}
    /** 公开全局访问点 */
    public static SocketSingleIntance getSocketSingleIntance(){
        if (null==socketSingleIntance){
            socketSingleIntance=new SocketSingleIntance();
        }
        return socketSingleIntance;
    }

    /** 赋值Socket */
    public Socket getSocket() {
        return socket;
    }
    /** 对外提供Socket访问 */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }



    /**
     * 生产者消费者设计模式（MSG=消费的对象）
     */
    class Message {
        private byte[] msg;
        private boolean flag = true;//用于标记发送和接收
        public Message() {
            super();
        }
        public Message(byte[] msg) {
            this.msg = msg;
        }
        public byte[] getMsg() {
            return msg;
        }
        public void setMsg(byte[] msg) {
            this.msg = msg;
        }

        /**
         * 接收数据（生产者）
         */
        public synchronized void receiveMessage() {
            if (flag){
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            {/** 具体接收业务 */
                byte[] resultTemp = new byte[512 * 3];
                int len = -1;
                try {
                    if (input != null || !socket.isClosed() || socket.isConnected()) {
                        len = input.read(resultTemp);
                    } else {
                        //
                    }
                    if (len == -1) {
                        //
                    } else {
                        /** 缓存Socket一次接过来的数据 */
                        byte[] result = new byte[len];
                        for (int i = 0; i < len; i++) {
                            result[i] = resultTemp[i];
                        }

                        android.os.Message msg = new android.os.Message();
                        msg.obj = result;//20161027把接收的数据封装为消息对象
//                        checkDataHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            flag=true;
            this.notify();
        }

        /**
         * 发送数据（消费者）
         * @param msg 待发送的数据
         */
        public synchronized void sendMessage(byte[] msg) {
            if (!flag){
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.setMsg(msg);
            {/** 具体发送业务 */

                if (socket == null || output == null || socket.isClosed()) {
                    if (socket == null) {
                        //
                    } else {
                        //
                    }
                    if (receiver != null) {
                        receiver.onFailure(0);
                    }
                    return;
                }
                try {
                    for (int i = 0; i < msg.length; i++) {
                        //
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                try {
                    if (msg != null) {
                        output.write(msg);
                        output.flush();
                    } else {
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
            flag=false;
            this.notify();
        }

    }

    /**
     * Socket接收数据线程
     */
    class ReceiveRunnable implements Runnable {
        private Message message;
        public ReceiveRunnable(Message msg) {
            this.message = msg;
        }
        @Override
        public void run() {
//            if (地址不是我的) {

//            }
            /** 地址是我的 */
            {
                message.receiveMessage();
            }
        }
    }

    /**
     * Socket发送数据线程
     */
    class SendRunnable implements Runnable {
        private Message message;
        public SendRunnable(Message msg) {
            this.message = msg;
        }
        @Override
        public void run() {

//            if (地址不是我的) {

//            }
            /** 地址是我的 */
            {
                message.sendMessage(message.msg);
            }
        }
    }

    /**
     * 回调接口：监听进度条等
     */
    public interface OnReceiver {
        /**
         * flag 0: 不处理，1：下载成功，2：下载失败,3:下载数据的百分比,4:连接成功,5:删除文件成功，6：获取设备状态成功, 7 :传文件到智能锅成功，8：传文件到智能锅的百分比 ,9:对时功能
         */
        void onSuccess(List<String> cookBookFileList, int flag, int now, int all);

        /**
         * flag 默认为0;-1：下载文件，文件大小=0;
         */
        void onFailure(int flag);
    }


}
