package com.gzmelife.app.device;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.activity.CheckUpdateActivity;
import com.gzmelife.app.activity.CookBookDetailActivity;
import com.gzmelife.app.fragment.DeviceFragment;
import com.gzmelife.app.tools.DataUtil;
import com.gzmelife.app.tools.FileUtils;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLogger;
import com.gzmelife.app.views.TipConfirmView;

import static com.gzmelife.app.device.Config.bufDownFileCancel;
import static com.gzmelife.app.device.Config.bufFileCancel;

/**
 * Created by HHD on 2016/10/16.
 * Socket类：发送指令；上传菜谱、固件；下载菜谱；解析、拼接帧数据；持续心跳
 *
 * 服务启动心跳
 */
public class SocketTool1 {

    MyLogger HHDLog=MyLogger.HHDLog();
    private String TAG = "SocketTool";
    private Socket socket;
    private OutputStream output;
    private InputStream input;
    private Context context;
    private Activity activity;
    public static HeartTimeCount heartTimer;
    private OnReceiver receiver;


    /** 缓存指令 */
    private byte[] instructionTemp = new byte[2];
    /** 标记每条指令是否首次发送，用于避开PMS和其他设备通讯：true=是首次 */
    private boolean isFirst = true;
    /** 当前是否正在发送命令：true=发送状态 */
    private boolean isSendCMD = false;
    /** 若指令发送不成功，3S后重发指令*/
    private int timeCnt = 0;
    /** 缓存重发一帧指令 */
    private byte[] bufLastTemp;
    /** 记录重发次数，若超过3次则进行重连的操作*/
    private int MaxReCnt = 0;
    /** 是否超时（大于9秒）：false=没超时 */
    private boolean RecTimeOut = false;
    /** 标记接收数据的总长度 */
    private int num = 0;
    /** 校验数据时缓存一帧数据 */
    private byte[] bufTemp = new byte[256 * 256];
    /** 指令发送是否成功、空闲状态、进行指令重发或心跳：false=非空闲状态 */
    private boolean ConFalg = false;
    /** PMS中录波文件列表总数 */
    private int fileNum = 0;
    /** 标记PMS中录波文件列表第几页 */
    private int frmIndex = 0;
    /** 标记PMS中录波文件列表最大一页 */
    private int maxIndex = 0;
    /** 发送的文件的总大小 */
    private int numDownZie = 0;
    /** 已经发到PMS的大小 */
    private int numDownNow = 0;
    // private int numUpZie = 0; // 上传到手机来的文件的大小
    /** 手机已经接收的大小 */
    private int numUpNow = 0;
    /** 请求文件的长度 */
    private byte[] bufRecFile;
    /** 缓存传到PMS的文件 */
    private byte[] bufSendFile = new byte[10 * 1024 * 1024];
    /** 一次最大发送到PMS的大小 */
    private int MaxPacket = 2 * 1024;
    /** PMS中菜谱文件列表 */
    private List<String> downFileList = new ArrayList<String>();
    /** PMS中录波文件列表 */
    private List<String> selfFileList = new ArrayList<String>();
    /** 缓存（进行到）帧数的byte数组 */
    private byte[] bufACK = {0x00, 0x00};
    /** 连接状态：true=连接成功（不必重连）、false=断开状态 */
    private boolean isConnected = false;
    /** 三次重连与指令的机会 若三次后还失败（isConnected=false且不再自动连接）*/
    int connectTimes = 3;
    /** 是否在心跳 */
    private boolean startHeart = false;


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
                        /**TODO:读进来2.8 */
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
                        HHDLog.i("读进---------------------------数据长度="+result.length);
                        System.out.println("\r\n接收---------------------------------------------------------------------------");
                        for (int i = 0; i < result.length; i++) {
                            if (i == 3) {
                                System.out.print("【 ");
                            }
                            if (i == 5) {
                                System.out.print("】 | ");
                            }
                            if (i == 6) {
                                System.out.print("| ");
                            }
                            System.out.print(byte2HexString(result[i]) + " ");
                        }
                        System.out.println("\r\n接收---------------------------------------------------------------------------");
                        System.out.println(" ");



                        {//20160929
                            /**TODO:1.7 取出isFirst*/
                            if (isFirst){
                                HHDLog.v("read里面isFirst="+isFirst);

                                if (result[5] != (byte) 0){//不等于空
                                    HHDLog.v("不等于空result[5]="+byte2HexString(result[5]));
                                    /**TODO:1.8 判断IP地址（isFirst=true） */
                                    if (result[5]!=Config.clientPort){/**TODO:1.10 IP地址非己*/
                                        /**TODO:1.11 提示*/
                                        TipConfirmView.showConfirmDialog2(context, "其他用户正在操作，请几秒后再来~", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                TipConfirmView.dismiss();
                                            }
                                        });
                                        return;/**TODO:1.11 不做任何*/
                                    }
                                } else {
                                    /**TODO:1.9 重发帧（IP地址是己） */
                                    sendFrame(bufLastTemp);
                                }

                            } else {
                                HHDLog.v("read里面isFirst="+isFirst);
                                /**TODO:不判断地址（isFirst=false）*/
                            }
                        }

                        android.os.Message msg = new android.os.Message();
                        msg.obj = result;//20161027把接收的数据封装为消息对象
                        checkDataHandler.sendMessage(msg);


//                        {
//                                /** 接收的总长度（有可能是两帧） */
//                                int receiveLen = 0;
//                                receiveLen = result.length;
//                                HHDLog.d("接收的总长度（有可能是两帧）=" + receiveLen);
//                                /** 当前解析帧的长度数据的长度L=功能码+子功能码+数据域长度 */
//                                int l = 0;
//                                /** 正确完整的一帧数据的长度（多帧数据重叠时） */
//                                int frameLen = 0;
//
//                                int checkSUM = 0;
//                            boolean isCheck=true;
//                            while (isCheck) {
//                                if (result[0] == (byte) 0xA5) {//是A5开头
//                                    HHDLog.d("是A5开头=" + byte2HexString(result[0]));
//                                    l = DataUtil.hexToTen(result[1]) + DataUtil.hexToTen(result[2]) * 256;
//                                    frameLen = l + 4;
//                                    /** 缓存正确的一帧数据（根据数据的长度L+4） */
//                                    byte[] frameTemp = new byte[frameLen];
//                                    for (int i = 0; i < frameLen; i++) {//根据len取帧
//                                        frameTemp[i] = result[i];
//                                    }
//                                    for (int i = 1; i < frameLen-1; i++) {
//                                        int temp = frameTemp[i];
//                                        if (temp < 0) {
//                                            temp += 256;
//                                        }
//                                        checkSUM += temp;
//                                    }
//                                    if (DataUtil.hexToTen(frameTemp[frameLen-1]) == (checkSUM % 256)) {
//                                        num=frameLen;
//                                        matching(frameTemp, frameLen);
//                                        isCheck=false;
//                                        HHDLog.d("校验码相等=" + DataUtil.hexToTen(frameTemp[frameLen-1]));
//                                    } else {
//                                        HHDLog.d("校验码不相等，是=" + DataUtil.hexToTen(frameTemp[frameLen - 1]));
//                                    }
//                                } else {
//                                    HHDLog.d("不是A5开头，是=" + byte2HexString(result[0]));
//                                }
//                            }
//                        }

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

            /** TODO:不是第一次发送（isFirst=false）2.6 */
            if (!isFirst) {
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
                            /** TODO: 写出去 2.7 */
                            output.write(msg);
                            output.flush();

                            System.out.println("\r\n发送---------------------------------------------------------------------------");
                            for (int i = 0; i < msg.length; i++) {
                                if (i == 3) {
                                    System.out.print("【 ");
                                }
                                if (i == 5) {
                                    System.out.print("】 | ");
                                }
                                if (i == 6) {
                                    System.out.print("| ");
                                }
                                System.out.print(byte2HexString(msg[i]) + " ");
                            }
                            System.out.println("\r\n发送---------------------------------------------------------------------------");
                            System.out.println(" ");
                            HHDLog.i("写出数据长度="+msg.length);

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
            }/** TODO:1.6 跳过只改flag */
            flag=false;
            this.notify();
            HHDLog.v("isFirst="+isFirst);

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
     * 封装发送帧数据功能（发送后启动接收线程）
     *
     * 用到地方：发指令、数据指令、重发指令和数据指令
     *
     * @param frameData 帧数据（拼接好的数据）
     */
    private void sendFrame(byte[] frameData){
        /** TODO:待测试流程
         */
//        switch (frameData[3]) {
//            case (byte) 0xF4:
//                break;
//            default:
//                break;
//        }

        /**TODO:1.1 取本次指令2.1*/
        HHDLog.v("缓存指令="+byte2HexString(instructionTemp[0])+" "+byte2HexString(instructionTemp[1]));
        HHDLog.v("本次指令="+byte2HexString(frameData[3])+" "+byte2HexString(frameData[4]));
        /**TODO:1.2 对比上次指令2.2*/
        if (frameData[3] == instructionTemp[0]) {
            /**TODO:对比：相同 2.3*/
            if (frameData[4] == instructionTemp[1]) {
                /**TODO:缓存本次指令2.4*/
                instructionTemp[0]=frameData[3];
                instructionTemp[1]=frameData[4];
                /**TODO:isFirst=false2.5*/
                isFirst=false;//20161028不是首次
                HHDLog.v("isFirst="+isFirst);
            }
        } else {/**TODO:1.3 对比：不相同*///20161028不相同就保存此次指令
            /**TODO:1.4 缓存本次指令*/
            instructionTemp[0]=frameData[3];
            instructionTemp[1]=frameData[4];
            /**TODO:1.5 isFirst=true*/
            isFirst=true;//20161028是首次
            HHDLog.v("isFirst="+isFirst);
        }




        Message msg=new Message(frameData);

        String s=byte2HexString(frameData[0]);
        HHDLog.v("frameData="+s);

        SendRunnable sendRunnable =new SendRunnable(msg);
        Thread sendThread=new Thread(sendRunnable);
        sendThread.start();

        ReceiveRunnable receiveRunnable=new ReceiveRunnable(msg);
        Thread receiveThread = new Thread(receiveRunnable);
        receiveThread.start();
    }

    public SocketTool1(Context context, OnReceiver onReceiver) {
        this.context = context;
        this.receiver = onReceiver;
    }

    /**
     * 只有DeviceFragment用到
     *
     * @param context
     * @param activity
     * @param onReceiver
     */
    public SocketTool1(Context context, Activity activity, OnReceiver onReceiver) {
        this.context = context;
        this.activity = activity;
        this.receiver = onReceiver;
    }

    /**
     * 初始化客户端Socket
     */
    public void initClientSocket() {
        try {
            socket = new Socket(Config.SERVER_HOST_IP, Config.SERVER_HOST_PORT);
            output = socket.getOutputStream();
            input = socket.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 首次进入“设备”界面时调用（三次连接机会）
     */
    public void firstConnect() {
        isConnected = false;
        connectTimes = 3;
        connectHandler.sendEmptyMessage(0);
    }

    /**
     * 关闭Socket连接
     */
    public void closeSocket() {
        try {
            if (output != null) {
                output.close();
                output = null;
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
            if (heartTimer != null) {
                heartTimer.cancel();
                heartTimer = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分析（PMS-》手机）数据，匹配相应的指令
     *
     * @param buf 接到的帧数据
     * @param num 帧数据的下标
     */
    private void matching(byte[] buf, int num) {
        int len = DataUtil.hexToTen(bufTemp[1]) + DataUtil.hexToTen(bufTemp[2]) * 256;
        ConFalg = true;
        isSendCMD = false;

        /** 截获其他客户端动作 */
//        if (buf[5] != Config.clientPort) {
//            switch (buf[3]) {
//                case (byte) 0xF3:
//                    if (buf[4] == 0x00) {
//                        KappUtils.showToast(context, "【" + byte2HexString(buf[5]) + "】获取录波文件数量");
//                        break;
//                    } else if (buf[4] == 0x01) {
//                        KappUtils.showToast(context, "【" + byte2HexString(buf[5]) + "】查询录波文件列表");
//                        break;
//                    } else if (buf[4] == 0x02) {
//                        KappUtils.showToast(context, "【" + byte2HexString(buf[5]) + "】遍历完毕");
//                        break;
//                    }
//                    break;
//
//                case (byte) 0xF4:
//                    if (buf[4] == 0x00) {
//                        KappUtils.showToast(context, "【" + byte2HexString(buf[5]) + "】获取录波文件大小");
//                        break;
//                    } else if (buf[4] == 0x01) {
//                        Config.isOtherInstruction = true;
//                        break;
//                    } else if (buf[4] == 0x02) {
//                        KappUtils.showToast(context, "【" + byte2HexString(buf[5]) + "】录波发送结束");
//                        Config.isOtherInstruction = false;
//                        break;
//                    } else if (buf[4] == 0x03) {
//                        KappUtils.showToast(context, "【" + byte2HexString(buf[5]) + "】中断文件传输");
//                        Config.isOtherInstruction = false;
//                        break;
//                    }
//                    break;
//
//                case (byte) 0xF5:
//                    if (buf[4] == 0x00) {
//                        KappUtils.showToast(context, "【" + byte2HexString(buf[5]) + "】获取录波文件大小");
//                        break;
//                    } else if (buf[4] == 0x01) {
//
//                        Config.isOtherInstruction = true;//20160927
//                        break;
//
//                    } else if (buf[4] == 0x02) {
//                        KappUtils.showToast(context, "【" + byte2HexString(buf[5]) + "】数据发送结束");
//                        Config.isOtherInstruction = false;//20160927
//                        break;
//                    }
//                    break;
//
//                case (byte) 0xF6:
//                    if (buf[4] == 0x00) {
//                        KappUtils.showToast(context, "【" + byte2HexString(buf[5]) + "】生成录波文件操作");
//                        break;
//                    } else if (buf[4] == 0x01) {
//                        KappUtils.showToast(context, "【" + byte2HexString(buf[5]) + "】删除APP下载菜谱文件操作");
//                        break;
//                    }
//                    break;
//            }
//            return;
//        }

        /** 客户端地址匹配 */
        switch (buf[3]) {
            case (byte) 0xF3: // 文件列表
                if (buf[4] == 0x00) { // 得到文件数目
                    fileNum = DataUtil.hexToTen(bufTemp[6]) + DataUtil.hexToTen(bufTemp[7]) * 256;
                    if (DeviceFragment.fileFlag) {
                        selfFileList.clear();
                    } else {
                        downFileList.clear();
                    }
                    if (fileNum > 0) {
                        frmIndex = 1;
                        maxIndex = fileNum / 25;
                        if ((fileNum % 25) > 0) {
                            maxIndex++;
                        }
                        ACK(frmIndex);
                        splitInstruction(Config.bufListFile, bufACK);
                    } else {
                        if (receiver != null) {
                            receiver.onSuccess(null, 0, 0, 0);
                        }
                    }
                } else if (buf[4] == 0x01) { // 得到文件名称
                    String filename = "";
                    for (int index = 8; index < num - 40; index += 40) { // 9->8
                        String aa = "";
                        try {
                            aa = new String(buf, index, 40, "gbk");
                        } catch (UnsupportedEncodingException e1) {
                            e1.printStackTrace();
                        }
                        try {
                            filename = aa.replace("\0", "");
                        } catch (Exception e) {
                            filename = aa;
                        }
                        if (DeviceFragment.fileFlag) {
                            selfFileList.add(filename);
                        } else {
                            downFileList.add(filename);
                        }
                    }
                    if (DeviceFragment.fileFlag) {
                        if (selfFileList.size() < fileNum) { // 加帧判断
                            frmIndex++;
                            ACK(frmIndex);
                            splitInstruction(Config.bufListFile, bufACK);
                        } else {
                            splitInstruction(Config.bufListFileOver,null);
                        }
                    } else {
                        if (downFileList.size() < fileNum) { // 加帧判断
                            frmIndex++;
                            ACK(frmIndex);
                            splitInstruction(Config.bufListFile, bufACK);
                        } else {
                            splitInstruction(Config.bufListFileOver,null);
                        }
                    }
                } else if (buf[4] == 0x02) { // 遍历完成
                    if (DeviceFragment.fileFlag) {
                        if (receiver != null) {
                            receiver.onSuccess(selfFileList, 0, 0, 0);
                        }
                    } else {
                        if (receiver != null) {
                            receiver.onSuccess(downFileList, 0, 0, 0);
                        }
                    }
                }
                break;
            case (byte) 0xF4: // 上召文件
                if (buf[4] == 0x01) { // 得到文件数据
                    if (Config.cancelTransfer) {/** 21061009取消传输 */
                        splitInstruction(bufFileCancel,null);
                        return;
                    }
                    downloadCookbook(buf);
                } else if (buf[4] == 0x00) { // 得到文件长度
                    int fileLen = DataUtil.hexToTen(buf[6]) + DataUtil.hexToTen(buf[7]) * 256
                            + DataUtil.hexToTen(buf[8]) * 256 * 256 + DataUtil.hexToTen(buf[9]) * 256 * 256 * 256;
                    if (fileLen == 0) {
                        if (receiver != null) {
                            receiver.onFailure(-1);
                        }
                        return;
                    }
                    numUpNow = 0;
                    bufRecFile = new byte[fileLen];
                    frmIndex = 1;
                    maxIndex = fileLen / MaxPacket;
                    if ((fileLen % MaxPacket) > 0) {
                        maxIndex++;
                    }
                    ACK(frmIndex);
                    splitInstruction(Config.bufFileAck, bufACK);
                } else if (buf[4] == 0x02) {
                    DeviceFragment.saveFileName = FileUtils.PMS_FILE_PATH + FileUtils.getFileName(DeviceFragment.saveFileName);
                    FileUtils.writeTextToFile(DeviceFragment.saveFileName, bufRecFile);
                    if (receiver != null) {
                        receiver.onSuccess(null, 1, 0, 0);
                    } else {
                        receiver.onFailure(0);
                    }
                }  else if (buf[4] == 0x03) {/** 20161009收到中断确认 */
                    Config.cancelTransfer = false;
                }
                break;
            case (byte) 0xF5: // 下传文件
                if (buf[4] == 0x00) { // 发送文件大小和文件名，得到确认
                    if (buf[6] == 0x01) {
                        Config.numDownNow = 0;
                        Config.frmIndex = 1;
                        uploadFile( Config.frmIndex);
                    } else if (buf[6] == 0x00) {
                        if (receiver != null) {
                            receiver.onFailure(50000);
                        }
                    }
                } else if (buf[4] == 0x01) { // 发送文件一帧，得到确认
                    if (buf[6] == 0x01) {
                        if (Config.cancelTransfer) {/** 21061009取消传输 */
                            splitInstruction(bufDownFileCancel,null);
                            return;
                        }
                        Config.frmIndex++;
                        if (receiver != null) {
                            receiver.onSuccess(null, 8, Config.numDownNow, Config.numDownZie);
                        } else {
                            //
                        }
                        if (Config.numDownZie > Config.numDownNow) {
                            uploadFile( Config.frmIndex);
                        } else {
                            splitInstruction(Config.bufDownFileStop,null);
                        }
                    } else if (buf[6] == 0x00) {
                        uploadFile( Config.frmIndex);
                    }
                } else if (buf[4] == 0x02) {
                    if (receiver != null) {
                        receiver.onSuccess(null, 7, 0, 0);
                    } else {
                        //
                    }
                }
                else if (buf[4] == 0x03) {/** 20161009收到中断确认 */
                    Config.cancelTransfer = false;
                }
                break;
            case (byte) 0xF6:
                if (buf[4] == (byte) 0x00) {
                    if (buf[6] == 0x01) {
                        if (receiver != null) {
                            receiver.onSuccess(null, 5, 0, 0);
                        }
                    } else {
                        if (receiver != null) {
                            receiver.onFailure(0);
                        }
                    }
                } else if (buf[4] == (byte) 0x01) {
                    if (buf[6] == 0x01) {
                        if (receiver != null) {
                            receiver.onSuccess(null, 5, 0, 0);
                        }
                    } else {
                        if (receiver != null) {
                            receiver.onFailure(0);
                        }
                    }
                }
                break;
            case (byte) 0xF7:
                if (buf[4] == (byte) 0x00) {
                    Config.SYSTEM_A = ((DataUtil.hexToTen(buf[6]) + 256 * DataUtil.hexToTen(buf[7])) * 1650.0 / 48803.38944) + "A";
                    Config.SYSTEM_V = ((DataUtil.hexToTen(buf[8]) + 256 * DataUtil.hexToTen(buf[9])) / 10.0) + "V";
                    Config.SYSTEM_W = DataUtil.hexToTen(buf[15]) * 10 + "W";
                    Config.PMS_TEMP = ((DataUtil.hexToTen(buf[10]) + 256 * DataUtil.hexToTen(buf[11])) / 100.0) + "度";
                    Config.ROOM_TEMP = ((DataUtil.hexToTen(buf[12]) + 256 * DataUtil.hexToTen(buf[13])) / 100.0) + "度";
                    switch (DataUtil.hexToTen(buf[14])) {
                        case 0:
                            Config.PMS_STATUS = "POWEROFF";
                            break;
                        case 1:
                            Config.PMS_STATUS = "POWERON";
                            break;
                        case 2:
                            Config.PMS_STATUS = "POWERSTANDBY";
                            break;
                        case 3:
                            Config.PMS_STATUS = "POWERHALT";
                            break;
                    }
                    Config.PMS_ERRORS.clear();
                    StringBuffer sb = new StringBuffer();
                    for (int i = 17; i > 15; i--) {
                        String hex = Integer.toHexString(buf[i]);
                        int ihex = Integer.parseInt(hex);
                        String r = "";
                        if (ihex < 10) {
                            r = String.format("%02d", ihex);
                        } else {
                            r = ihex + "";
                        }
                        sb.append(r);
                    }
                    String result = hexString2binaryString(sb.toString());
                    StringBuffer sBuf = new StringBuffer();
                    for (int i = result.length(); i > 0; i--) {
                        sBuf.append(result.substring(i - 1, i));
                    }
                    for (int i = 0; i < result.length(); i++) {
                        if (sBuf.substring(i, i + 1).equals("1")) {
                            Config.PMS_ERRORS.add((i) + "");
                        }
                    }
                    if (receiver != null) {
                        receiver.onSuccess(null, 6, 0, 0);
                    }
                } else {
                    if (receiver != null) {
                        receiver.onFailure(0);
                    }
                }
                break;
            case (byte) 0xF8:/** 连接成功 */
                if (buf[4] == (byte) 0x00) { // 连接确认报文，回复PMS的MAC
                    isConnected = true;
                    if (receiver != null) {
                        receiver.onSuccess(null, 4, 0, 0);
                    }
                } else if (buf[4] == (byte) 0x02) {
                    // 心跳报文
                }
                break;
            case (byte) 0xF2: {/** 对时 */
                if (buf[4] == (byte) 0x00) {
                    if (receiver != null) {
                        receiver.onSuccess(null, 9, 0, 0);
                    }
                }
            }
            break;
        }
    }

    /**
     * 拼接指令帧数据
     *
     * @param instruction 指令（除SocketTool有5处用到）
     */
    public void splitInstruction_原版(byte[] instruction) {//splitInstruction
        int addNum = 0;
        byte[] bufTemp = new byte[instruction.length + 5];
        bufTemp[0] = (byte) 0xA5;
        bufTemp[1] = (byte) (instruction.length % 256 + 1);
        bufTemp[2] = (byte) (instruction.length / 256);
        for (int i = 0; i < instruction.length; i++) {
            bufTemp[i + 3] = instruction[i];
        }
        bufTemp[instruction.length + 3] = Config.clientPort;
        for (int i = 1; i < instruction.length + 4; i++) {
            addNum += bufTemp[i];
        }
        bufTemp[instruction.length + 4] = (byte) (addNum % 256);
        try {
            bufLastTemp = new byte[bufTemp.length];
            for (int i = 0; i < bufTemp.length; i++) {
                bufLastTemp[i] = bufTemp[i];
            }
            // 记录所发的指令，用于后续若指令失败时重发
            bufLastTemp = new byte[bufTemp.length];
            //sendMessage(bufTemp);
            sendFrame(bufTemp);


            timeCnt = 0;
            Config.timeCntHeart = 0;
            isSendCMD = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拼接指令帧数据（带数据）
     *
     * @param functionCode 功能码
     * @param data        数据
     */
    public void splitInstruction(byte[] functionCode, byte[] data) {

        if (data!=null){
            int addNum = 0;
            int i = 0;
            int len = functionCode.length + data.length + 1;// PMS格式长度+数据长度+校验码
            byte[] bufTemp = new byte[len + 4];/*Lotus 2016-07-27 修改长度*/
            bufTemp[0] = (byte) 0xA5;
            bufTemp[1] = (byte) (len % 256);
            bufTemp[2] = (byte) ((len / 256) % 256);
            bufTemp[3] = functionCode[0];
            bufTemp[4] = functionCode[1];
            if (Config.clientPort == -1) {
                return;
            }
            bufTemp[5] = Config.clientPort;
            for (i = 6; i < len + 3; i++) {
                bufTemp[i] = data[i - 6];
            }
            for (i = 1; i < len + 3; i++) {
                addNum += bufTemp[i];
            }
            bufTemp[len + 3] = (byte) (addNum % 256);
            try {
//            sendMessage(bufTemp);
                sendFrame(bufTemp);
                timeCnt = 0;
                Config.timeCntHeart = 0;
                isSendCMD = true;
//            HHDLog.v("心跳时间为0表示上传清空="+Config.timeCntHeart);
            } catch (Exception e) {
                e.printStackTrace();
            }
            bufLastTemp = new byte[bufTemp.length];
            for (i = 0; i < bufTemp.length; i++) {
                bufLastTemp[i] = bufTemp[i];
            }
        } else {
            int addNum = 0;
            byte[] bufTemp = new byte[functionCode.length + 5];
            bufTemp[0] = (byte) 0xA5;
            bufTemp[1] = (byte) (functionCode.length % 256 + 1);
            bufTemp[2] = (byte) (functionCode.length / 256);
            for (int i = 0; i < functionCode.length; i++) {
                bufTemp[i + 3] = functionCode[i];
            }
            bufTemp[functionCode.length + 3] = Config.clientPort;
            for (int i = 1; i < functionCode.length + 4; i++) {
                addNum += bufTemp[i];
            }
            bufTemp[functionCode.length + 4] = (byte) (addNum % 256);
            try {
                bufLastTemp = new byte[bufTemp.length];
                for (int i = 0; i < bufTemp.length; i++) {
                    bufLastTemp[i] = bufTemp[i];
                }
                bufLastTemp = new byte[bufTemp.length];
                //sendMessage(bufTemp);
                sendFrame(bufTemp);
                timeCnt = 0;
                Config.timeCntHeart = 0;
                isSendCMD = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /** F4 01
     * PMS（菜谱）-》手机（菜谱）
     *
     * @param buf 每帧数据
     */
    private void downloadCookbook(byte[] buf) {
        int fileLen = DataUtil.hexToTen(buf[1]) + DataUtil.hexToTen(buf[2]) * 256 - 9;//200160928//fileLen：长度L：功能码、子功能码以及数据域的长度之和
        int allFileLen = DataUtil.hexToTen(buf[6]) + DataUtil.hexToTen(buf[7]) * 256 + DataUtil.hexToTen(buf[8]) * 256 * 256 + DataUtil.hexToTen(buf[9]) * 256 * 256 * 256;/** 20160920新增加的 */
        for (int i = 0; i < 1037; i++) {
        }
        for (int i = 0; i < fileLen; i++) {
            bufRecFile[numUpNow + i] = buf[i + 12];//20160928//bufRecFile：当前菜谱文件总长度
        }
        for (int j = 0; j < bufRecFile.length; j++) {
        }
        numUpNow += fileLen;// 40380 40124//20160928//fileLen：进度总长度（int）
        if (receiver != null) {
            receiver.onSuccess(null, 3, numUpNow, bufRecFile.length);
        }
        if (numUpNow == bufRecFile.length) {//20160928需要修改：帧序号=文件的长度/每帧大小
            splitInstruction(Config.bufFileStop,null);
        } else {
            frmIndex++;
            ACK(frmIndex);
            splitInstruction(Config.bufFileAck, bufACK);//frmIndex（int）=帧序号=bufACK（byte）
        }
    }

    /** F5 01
     * 手机-》PMS
     *
     * @param index 第几帧
     */
    private void uploadFile(int index) {
        int lenth = Config.numDownZie - Config.numDownNow;// 所需下载长度为剩下的长度 但不大于最大请求长度
        if (lenth > (MaxPacket)) {
            lenth = (MaxPacket);
        }
        byte[] bufR = new byte[lenth + 2];
        bufR[0] = (byte) (index % 256);
        bufR[1] = (byte) (index / 256);
        for (int i = 0; i < lenth; i++) {
            bufR[i + 2] = Config.bufSendFile[(index - 1) * MaxPacket + i];//            bufR[i + 2] = bufSendFile[(index - 1) * MaxPacket + i];
        }
        splitInstruction(Config.bufDownFileData, bufR);
        Config.numDownNow = Config.numDownNow + lenth;
    }

    /** F5 00
     * 手机（菜谱）-》PMS（菜谱）；获取文件名称、大小
     */
    public void uploadCookbookInfo() {
        String fileName;
        File file;
        if (CookBookDetailActivity.stat == false) {
            file = new File(CookBookDetailActivity.filePath);
            fileName = CookBookDetailActivity.filePath.substring(CookBookDetailActivity.filePath.lastIndexOf('/'));// 文件名长度的限制
        } else {
            file = new File(CookBookDetailActivity.newFilePath);
            fileName = CookBookDetailActivity.newFilePath.substring(CookBookDetailActivity.newFilePath.lastIndexOf('/'));// 文件名长度的限制
        }
        if (fileName.startsWith("\\")) {
            fileName = fileName.replace("\\", "");
        }
        if (fileName.startsWith("/")) {
            fileName = fileName.replace("/", "");
        }
        if (fileName.length() > 39) {
            Looper.prepare();
            KappUtils.showToast(context, "文件名称超长，请重新选择");
            Looper.loop();
            if (receiver != null) {
                receiver.onFailure(0);
            }
            return;
        }
        byte[] bufFile = FileUtils.getBytesFromFile(file);// 要发送的文件数据
        int check = 0;
        bufSendFile = new byte[bufFile.length];
        Config.bufSendFile = new byte[bufFile.length];//
        for (int i = 0; i < bufFile.length; i++) {
            bufSendFile[i] = bufFile[i];
            Config.bufSendFile[i] = bufFile[i];//
            check += bufFile[i];
        }
        byte[] arr = null;// 文件名的字节长度
        try {
            arr = fileName.getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] bufFileInfo = new byte[arr.length + 8];/* Lotus 2016-07-27 因为文件名称长度问题,需要给8位,跟文档上面4位不符*/// 所传参数，文件名长度+文件长度（均为字节长度）
        bufFileInfo[0] = (byte) (bufFile.length % 256);
        bufFileInfo[1] = (byte) ((bufFile.length / 256) % 256);
        bufFileInfo[2] = (byte) ((bufFile.length / 256 / 256) % 256);
        bufFileInfo[3] = (byte) ((bufFile.length / 256 / 256 / 256) % 256);
        bufFileInfo[4] = (byte) (check % 256);
        bufFileInfo[5] = (byte) ((check / 256) % 256);
        bufFileInfo[6] = (byte) ((check / 256 / 256) % 256);
        bufFileInfo[7] = (byte) ((check / 256 / 256 / 256) % 256);
        numDownNow = 0;
        numDownZie = (int) bufFile.length;
        Config.numDownZie = (int) bufFile.length;//
        for (int i = 0; i < arr.length; i++) {
            bufFileInfo[i + 8] = arr[i];
        }
        ConFalg = false;
        splitInstruction(Config.bufDownFileInfo, bufFileInfo);
    }

    /** F5 00
     * 手机（固件）-》PMS（固件）
     */
    public void uploadFirmwareInfo() {
        String fileName;
        File file;
        file = new File(CheckUpdateActivity.result1.getPath());
        // 文件名长度的限制
        int index = CheckUpdateActivity.result1.getPath().lastIndexOf('/');
        if (index > 0) {
            index = index + 1;
        }
        fileName = CheckUpdateActivity.result1.getPath().substring(index);
        if (fileName.length() > 39) {
            Looper.prepare();
            KappUtils.showToast(context, "文件名称超长，请重新选择");
            Looper.loop();
            if (receiver != null) {
                receiver.onFailure(0);
            }
            return;
        }
        byte[] bufFile = FileUtils.getBytesFromFile(file);// 要发送的文件数据
        int check = 0;
        bufSendFile = new byte[bufFile.length];
        //Config.bufSendFile = new byte[bufFile.length];//
        for (int i = 0; i < bufFile.length; i++) {
            bufSendFile[i] = bufFile[i];
            //Config.bufSendFile[i] = bufFile[i];//
            check += bufFile[i];
        }

        // 文件名的字节长度
        byte[] arr = null;
        try {
            arr = fileName.getBytes("gb2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 所传参数，文件名长度+文件长度（均为字节长度）
        byte[] bufFileInfo = new byte[arr.length + 8];
        bufFileInfo[0] = (byte) (bufFile.length % 256);
        bufFileInfo[1] = (byte) ((bufFile.length / 256) % 256);
        bufFileInfo[2] = (byte) ((bufFile.length / 256 / 256) % 256);
        bufFileInfo[3] = (byte) ((bufFile.length / 256 / 256 / 256) % 256);
        bufFileInfo[4] = (byte) (check % 256);
        bufFileInfo[5] = (byte) ((check / 256) % 256);
        bufFileInfo[6] = (byte) ((check / 256 / 256) % 256);
        bufFileInfo[7] = (byte) ((check / 256 / 256 / 256) % 256);
        numDownNow = 0;
        numDownZie = (int) bufFile.length;
        //Config.numDownZie = (int) bufFile.length;//
        for (int i = 0; i < arr.length; i++) {
            bufFileInfo[i + 8] = arr[i];
        }
        ConFalg = false;
        splitInstruction(Config.bufDownFileInfo, bufFileInfo);
    }

    /**
     * 校验数据
     */
    Handler checkDataHandler = new Handler(new Callback() {
        /**
         * 校验数据的正确性
         *
         * @param msg 需要校验的数据
         * @return 是否正确
         */
        @Override
        public boolean handleMessage(android.os.Message msg) {
            byte[] result = (byte[]) msg.obj;
            try {
                /** 拼接A5再次校验：false=不开始再次校验 */
                boolean isAgain = false;
                /** 当前解析帧的长度数据的长度L=功能码+子功能码+数据域长度 */
                /** 正确完整的一帧数据的长度（多帧数据重叠时） */
                int frameLen = (DataUtil.hexToTen(result[1]) + DataUtil.hexToTen(result[2]) * 256)+4;
//                HHDLog.d("Len+4=" + frameLen);

                /** 校验算术和 */
                int checkSUM = 0;

                /** 接收的总长度（有可能是两帧） */
                int receiveLen = 0;
                receiveLen = result.length;
//                HHDLog.d("接收的总长度（有可能是两帧）=" + receiveLen);

                /** 是否校验结束：false=校验结束 */
                boolean isCheck = true;
                while (isCheck) {
                    if (isAgain) {
                        if (result[0] == (byte) 0xA5) {//是A5开头
                            if (frameLen>=num){//限制接收的长度为有效长度
                                if (num < 3) {//拼接前三
                                    bufTemp[num] = result[num];
                                    num++;
                                } else if (num == 3) {//拼接四
//                                len = DataUtil.hexToTen(bufTemp[1]) + DataUtil.hexToTen(bufTemp[2]) * 256;
                                    bufTemp[num] = result[num];
                                    num++;
                                } else if (num > 3) {//拼接四后面
                                    if (num < frameLen + 3 && num < result.length) {//解析不到最后一位时
                                        bufTemp[num] = result[num];
                                        num++;
                                    } else {//解析到最后一位时
                                        if (num < result.length) {
                                            bufTemp[num] = result[num];
                                            num++;
                                        }
                                        for (int i = 1; i < num - 1; i++) {//解析完成开始校验
                                            int temp = bufTemp[i];
                                            if (temp < 0) {
                                                temp += 256;
                                            }
                                            checkSUM += temp;
                                        }
                                        if (DataUtil.hexToTen(bufTemp[num - 1]) == (checkSUM % 256)) {//对比校验码
                                            if (frameLen==num){
                                                matching(bufTemp, num);
                                            }
                                            isCheck = false;
                                            return true;
                                        } else {//校验不通过结束校验
                                            if (frameLen > 0) {
                                                isCheck = false;
                                            }
                                        }
                                        if (frameLen > 0) {//不开始再次校验
                                            isAgain = false;
                                            num = 0;
                                        }
                                    }
                                }
                            } else {
                                //
                            }
                        } else {
                            HHDLog.d("不是A5开头，是=" + byte2HexString(result[0]));
                        }

                    } else {//不是不开始再次校验状态
                        /** 转换一个地址码：用于替换 */
                        byte addressCode = (byte) 0xA5;
                        /** 缓存地址码 */
                        byte addressCodeTemp = result[0];
                        if (addressCodeTemp == addressCode) {
                            num = 0;
                            bufTemp[num] = addressCodeTemp;
                            isAgain = true;
                            num++;
                            timeCnt = 0;
                            Config.timeCntHeart = 0;
                            RecTimeOut = false;
                        } else {
                            //
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Config.SERVER_HOST_NAME = "";
                if (receiver != null) {
                    receiver.onFailure(0);
                }
                ConFalg = false;
                return false;
            }
            return false;
        }
    });

    /**
     * 检查Socket连接状态连接
     */
    Handler connectHandler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    if (!isConnected && connectTimes > 0) {
                        connectTimes--;
                        connectHandler.sendEmptyMessage(1);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                closeSocket();
                                initClientSocket();
                                splitInstruction(Config.bufConnect,null);
                                new Handler().postDelayed(new Runnable() {/** 延迟2秒执行此线程 */
                                @Override
                                public void run() {
                                    connectHandler.sendEmptyMessage(0);
                                }
                                }, 2000);
                                Looper.loop();
                            }
                        }).start();
                    } else {
                        if (!isConnected) {
                            if (receiver != null) {
                                receiver.onFailure(0);
                            }
                            KappAppliction.state = 2;
                            KappUtils.showToast(context, "与PMS连接已经断开");
                        }
                    }
                    break;
                case 1:
//                    HHDLog.i("重连剩余次数：" + connectTimes);
                    break;
            }
            return false;
        }
    });


    /**
     * 心跳计时类（重发指令、心跳）
     *
     * 可以尝试单例设计模式（解决心跳加快问题）
     */
    class HeartTimeCount extends CountDownTimer {
        /**
         * 倒计时构造方法
         *
         * @param millisInFuture 从开始调用start()到倒计时完成并onFinish()方法被调用的毫秒数（单位毫秒）
         * @param countDownInterval 接收onTick(long)回调的间隔时间（单位毫秒）
         */
        HeartTimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        /** 倒计时完成时被调用   */
        @Override
        public void onFinish() {
            //
        }

        /**
         * 固定间隔被调用（不会在之前一次调用完成前被调用）
         * @param millisUntilFinished 倒计时剩余时间
         *
         * start ()：启动倒计时
         */
        @Override
        public void onTick(long millisUntilFinished) {
            try {
                if (!TextUtils.isEmpty(Config.SERVER_HOST_NAME) && socket != null && socket.isConnected()) {
                    timeCnt++;
                    if (timeCnt > 9) {
                        RecTimeOut = true;
                        timeCnt = 0;
                    }
                    if (ConFalg) {
                        if (RecTimeOut && isSendCMD) {// 如果是发送超时等，三次重发机会，否则尝试重连
                            MaxReCnt++;
//                            sendMessage(bufLastTemp);
                            sendFrame(bufLastTemp);
                            isSendCMD = true;
                            timeCnt = 0;
                            RecTimeOut = false;
                            if (MaxReCnt > 2) {
                                MaxReCnt = 0;
                                ConFalg = false;
                                KappUtils.showToast(context, "操作失败，进行重连");
                                Config.SERVER_HOST_NAME = "";
                                connectTimes = 3;
                                isConnected = false;
                                connectHandler.sendEmptyMessage(0);
                            }
                        }
                        Config.timeCntHeart++;/** 心跳计时，30S无操作发送心跳指令 */
//                        HHDLog.v("当前心跳时间="+Config.timeCntHeart);
                        if (Config.timeCntHeart >= 30) {
                            splitInstruction(Config.bufHearbeat,null);
//                            Config.timeCntHeart = 0;
                        }
                    }
                }
            } catch (Exception e) {
                //
            }
        }
    }

    /**
     * 监听进度条等
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

    /** 判断是否正在心跳 */
    public boolean isStartHeartTimer() {
        return startHeart;
    }

    /**
     * 启动心跳计时
     */
    public void startHeartTimer() {
        if (heartTimer!=null){//20161028HHD
            heartTimer.cancel();
            heartTimer=null;
        }
        heartTimer = new HeartTimeCount(Long.MAX_VALUE, 1000);
        heartTimer.start();
        startHeart = true;
        ConFalg = true;
    }

    /**
     * 将当前获取第几帧的值转化为byte数组来传
     */
    private void ACK(int index) {
        bufACK[0] = (byte) (index % 256);
        bufACK[1] = (byte) (index / 256);
    }

    /**
     * 字节转哈希字符串
     *
     * @param b 需要转换的byte
     * @return 相应的String
     */
    public static String byte2HexString(byte b) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(HexCode[(b >>> 4) & 0x0f]);
        buffer.append(HexCode[b & 0x0f]);
        return buffer.toString();
    }

    /**
     * 哈希字符串转二进制字符串
     *
     * @param hexString 哈希字符串
     * @return 二进制字符串
     */
    private String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return "";
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    /***
     * 字节转哈希字符串
     *
     * @param b 需要转的字节
     * @return 哈希字符串
     */
    public static String bytetoHexString(byte b) {
        String result = Integer.toHexString(b & 0xFF);
        if (result.length() == 1) {
            result = '0' + result;
        }
        return result;
    }

    /** 哈希表 */
    private static char[] HexCode = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 字节转整数
     *
     * @param res 需要转换的字节数组
     * @return 整数
     */
    public static int bytes2int(byte[] res) {// 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000
        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) | ((res[2] << 24) >>> 8) | (res[3] << 24);// “|” 表示按位或
        return targets;
    }

    /**
     * 字节数组转对象// bytearray to object
     *
     * @param bytes 需要转换的字节数组
     * @return 对象
     */
    public static Object byte2Object(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(bi);
            obj = oi.readObject();
            bi.close();
            oi.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 对象转字节数组// object to bytearray
     *
     * @param obj 需要转换的对象
     * @return 相应的字节数组
     */
    public static byte[] object2Byte(java.lang.Object obj) {
        byte[] bytes = null;
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);
            bytes = bo.toByteArray();
            bo.close();
            oo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

}
