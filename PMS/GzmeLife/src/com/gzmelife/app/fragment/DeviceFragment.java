package com.gzmelife.app.fragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.espressif.iot.esptouch.demo_activity.EspWifiAdminSimple;
import com.gzmelife.Status.smartPotStatu;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.activity.AddDevicesActivity;
import com.gzmelife.app.activity.CookBookDetailActivity;
import com.gzmelife.app.activity.DeviceCenterActivity;
import com.gzmelife.app.activity.DeviceDetailActivity;
import com.gzmelife.app.activity.MainActivity;
import com.gzmelife.app.adapter.CookBookAdapter;
import com.gzmelife.app.adapter.DeviceCenterAdapter;
import com.gzmelife.app.bean.DeviceNameAndIPBean;
import com.gzmelife.app.dao.DevicesDAO;
import com.gzmelife.app.device.Config;
import com.gzmelife.app.device.SocketTool;
import com.gzmelife.app.tools.CountDownTimerUtil;
import com.gzmelife.app.tools.DateUtil;
import com.gzmelife.app.tools.DensityUtil;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.tools.MyLogger;
import com.gzmelife.app.tools.SharedPreferenceUtil;
import com.gzmelife.app.tools.ShowDialogUtil;
import com.gzmelife.app.tools.WifiUtil;
import com.gzmelife.app.views.TipConfirmView;

//20160913
@SuppressLint("InflateParams")
public class DeviceFragment extends Fragment {
    private String TAG = "DeviceFragment";
    MyLogger HHDLog = MyLogger.HHDLog();
    private TextView tv_title;
    private RadioButton rb_selfFile;
    private RadioButton rb_downFile;
    private Button btn_titleRight;
    private ImageView iv_titleLeft;
    private ImageView iv_titleRight;
    private ListView lv_file; // 设备内文件
    private ListView lv_pms; // 离线设备
    private View view_selfFile;
    private View view_downFile;
    private View layout_no_device;
    private View layout_connected;
    private View layout_devices;
    private SocketTool socketTool;
    private Dialog pDlg;
    private AlertDialog dlg;
    /**  true表示录波文件，false表示菜谱文件 */
    public static boolean fileFlag = true;
    private List<DeviceNameAndIPBean> deviceList = new ArrayList<DeviceNameAndIPBean>();
    private DeviceNameAndIPBean connectDeviceBean;
    private List<String> downFileList = new ArrayList<String>(); // 菜谱文件列表
    private List<String> selfFileList = new ArrayList<String>(); // 录波文件列表
    private CookBookAdapter downAdapter;
    private CookBookAdapter selfAdapter;
    private DeviceCenterAdapter deviceAdapter;
    public static String saveFileName;
    private String deleteFileName = "";
    private TimeCountOut outTime; // 与wifi建立连接
    private TimeCountOut outtime; // 与PMS建立连接
    private boolean isFirst = true;
    private LocalBroadcastManager broadcastManager;
    private Context context;
    private MainActivity activity;
    public String socket = "";
    public boolean isConnect = false;
    public boolean downLoadState = false;
    private EffectInVisiableHandler mtimeHandler;
    private final int MOBILE_QUERY = 123;
    private final int MOBILE_STOP = 10000;
    private int time = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView-->");
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_device, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated-->");
        context = this.getActivity();
        activity = (MainActivity) this.getActivity();
        initView();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(KappUtils.ACTION_PMS_STATUS);//20160919发送设备状态的广播
        // intentFilter.addCategory();
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        broadcastManager.registerReceiver(receiver, intentFilter);//20160919接收设备状态的广播
    }

    private class EffectInVisiableHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MOBILE_QUERY:
//              midLayoutInVisable();//当10秒到达后，作相应的操作。
//            	closeDlg();
//            	socketTool.closeSocket();
//				socketTool.initClientSocket();
//				iv_titleLeft.setImageResource(R.drawable.icon06);
//				KappUtils.showToast(context, "连接断开,正在尝试重新连接");
                    break;
                case MOBILE_STOP:
                    mtimeHandler.removeMessages(MOBILE_QUERY);
                    break;
            }
        }
    }

    public void resetTime() {
        mtimeHandler.removeMessages(MOBILE_QUERY);
        Message msg = mtimeHandler.obtainMessage(MOBILE_QUERY);
        mtimeHandler.sendMessageDelayed(msg, 10000);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {//20160919接收设备状态的广播
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(KappUtils.ACTION_PMS_STATUS)) {
                updatePmsStatus();
            }
        }
    };

    private void initView() {
        Log.i(TAG, "initView-->");
        tv_title = (TextView) getView().findViewById(R.id.tv_title);
        rb_selfFile = (RadioButton) getView().findViewById(R.id.rb_selfFile);
        rb_downFile = (RadioButton) getView().findViewById(R.id.rb_downFile);
        iv_titleLeft = (ImageView) getView().findViewById(R.id.iv_titleLeft);
        iv_titleRight = (ImageView) getView().findViewById(R.id.iv_titleRight);
        btn_titleRight = (Button) getView().findViewById(R.id.btn_titleRight);
        layout_no_device = getView().findViewById(R.id.layout_no_device);
        layout_connected = getView().findViewById(R.id.layout_connected);
        layout_devices = getView().findViewById(R.id.layout_devices);
        view_selfFile = getView().findViewById(R.id.view_selfFile);
        view_downFile = getView().findViewById(R.id.view_downFile);
        int normal = DensityUtil.dip2px(context, 12);//20160913左右上角按钮应统一封装在Bar上
        iv_titleLeft.setPadding(normal, normal, normal, normal);
        iv_titleRight.setImageResource(R.drawable.icon01);
        lv_file = (ListView) getView().findViewById(R.id.lv_file);
        lv_pms = (ListView) getView().findViewById(R.id.lv_pms);
        deviceAdapter = new DeviceCenterAdapter(context, deviceList);//20160920设置设备中心适配器数据
        lv_pms.setAdapter(deviceAdapter);//20160920添加数据到列表
        lv_pms.setOnItemClickListener(new OnItemClickListener() {//20160919设备列表（短）点击事件
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                connectDeviceBean = deviceList.get(position);
                h.sendEmptyMessage(2);
            }
        });
        lv_pms.setOnItemLongClickListener(new OnItemLongClickListener() {//20160919设备列表（长）点击事件
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deletePMSDevice(position);
                return true;
            }
        });

        rb_selfFile.setOnCheckedChangeListener(new OnCheckedChangeListener() {//20160919录波文件点击事件
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    Log.i(TAG, "点击  Config.flag=1;");
                    Config.flag = 1;
                    rb_selfFile.setTextColor(Color.parseColor("#ff5b80"));
                    view_selfFile.setVisibility(View.VISIBLE);
                    lv_file.setAdapter(selfAdapter);

                    selfFileList.clear();
                    showDlg();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            try {
                                socketTool.closeSocket();//20160913理解为关闭之前连接才能创建下面的连接
                                socketTool.initClientSocket(); // 根据不同的ip，建立不同的socket
//                                if (!socketTool.isStartHeartTimer()) {// 根据不同的ip，建立不同的socket
                                    socketTool.startHeartTimer();
//                                }
                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                            byte[] bufFilePath = {0x00};//20160913
                            System.out.print("----请求录波文件总数3333333----");
                            fileFlag = true;
                            selfFileList.clear();
                            if (Config.bufGetFileNum != null
                                    && bufFilePath != null
                                    && !bufFilePath.equals("")
                                    && !Config.bufGetFileNum.equals("")) {
                                try {
                                    socketTool.splitInstruction(Config.bufGetFileNum, bufFilePath);
                                } catch (Exception e) {
                                    // TODO: handle exception
                                }
                            }
                            Looper.loop();
                        }
                    }).start();
                } else {
                    rb_selfFile.setTextColor(Color.parseColor("#3f3f3f"));
                    view_selfFile.setVisibility(View.INVISIBLE);
                }
            }
        });
        rb_downFile.setOnCheckedChangeListener(new OnCheckedChangeListener() {//20160919菜谱文件点击事件
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    Config.flag = 2;
                    Log.i(TAG, "点击  Config.flag=2;");
                    rb_downFile.setTextColor(Color.parseColor("#ff5b80"));
                    view_downFile.setVisibility(View.VISIBLE);
                    if (downAdapter == null) {
                        downAdapter = new CookBookAdapter(context, downFileList, new CookBookAdapter.OnReceiver() {
                            @Override
                            public void onDownload(int position) {
                                Message msg = new Message();
                                msg.what = 3;
                                msg.arg1 = position;
                                msg.obj = "1";
                                h.sendMessage(msg);
                            }

                            @Override
                            public void onDelete(int position) {
                                deletePMSFile(1, position);
                            }
                        });
                    }

                    lv_file.setAdapter(downAdapter);
                    downFileList.clear();
                    showDlg();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Looper.prepare();
                                socketTool.closeSocket();
                                socketTool.initClientSocket();
//                                if (!socketTool.isStartHeartTimer()) {
                                    socketTool.startHeartTimer();
//                                }
//								}
                                System.out.print("----请求菜谱文件总数3333333----");
                                final byte[] bufFilePath = {0x01};
                                fileFlag = false;
                                downFileList.clear();
                                socketTool.splitInstruction(Config.bufGetFileNum, bufFilePath);
                                System.out.print("----发送4444----");
                                Looper.loop();
                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                        }
                    }).start();
                } else {
                    rb_downFile.setTextColor(Color.parseColor("#3f3f3f"));
                    view_downFile.setVisibility(View.INVISIBLE);
                }
            }
        });

        selfAdapter = new CookBookAdapter(context, selfFileList,
                new CookBookAdapter.OnReceiver() {
                    @Override
                    public void onDownload(int position) {
                        Message msg = new Message();
                        msg.what = 3;
                        msg.arg1 = position;
                        msg.obj = "2";
                        h.sendMessage(msg);
                    }

                    @Override
                    public void onDelete(int position) {
                        deletePMSFile(0, position);
                    }
                });
        lv_file.setAdapter(selfAdapter);

        btn_titleRight.setText("切换");
        btn_titleRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, DeviceCenterActivity.class));
            }
        });

        iv_titleRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddDevicesActivity.class);
                startActivity(intent);
            }
        });

        iv_titleLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(Config.SERVER_HOST_NAME)) { // 未连接，点击无效
                    startActivityForResult(new Intent(context,
                            DeviceDetailActivity.class), 101);
                }
            }
        });
    }

    private void getInfo() {
        PackageManager pm = getContext().getPackageManager();
        boolean permission = (PackageManager.FEATURE_WIFI.equals(pm.checkPermission("android.permission.ACCESS_WIFI_STATE", "packageName"))
        );
        if (permission) {
            connectPMS();
        } else {
            KappUtils.showToast(context, "没有wifi权限");
        }
    }

    private void initSocketTool() {
        Log.i(TAG, "initSocketTool");
        if (socketTool == null) {
            socketTool = new SocketTool(context, activity, new SocketTool.OnReceiver() {//20160923实例化SocketTool//实现接收数据接口
                @Override
                public void onSuccess(List<String> cookBookFileList, int flag, int now, int all) {
                    //flag 0: 不处理，1：下载成功，2：下载失败,3:下载数据的百分比,4:连接成功,5:删除文件成功，6：获取设备状态成功, 7 :传文件到智能锅成功，8：传文件到智能锅的百分比 ,9:对时功能
                    switch (flag) {
                        case 0:
                            closeDlg();
                            if (outTime != null) {
                                outTime.cancel();
                                outTime = null;
                            }
                            if (fileFlag) {

                                selfFileList.clear();
                                if (cookBookFileList != null) {
                                    selfFileList.addAll(cookBookFileList);
                                } else {
                                    KappUtils.showToast(context, "暂无录波文件");
                                }
                                selfAdapter.notifyDataSetChanged();//20160919更新列表数据
                            } else {
                                downFileList.clear();
                                if (cookBookFileList != null) {
                                    downFileList.addAll(cookBookFileList);
                                } else {
                                }
                                downAdapter.notifyDataSetChanged();
                            }
                            //如果不发起终止的消息,每次点击切换都会10秒过后会断开,若发起终止 则进入自动连接时会退出已连接的,要求重新连接
//								mtimeHandler.removeMessages(MOBILE_QUERY);
                            break;
                        case 1:
                            handler.sendEmptyMessage(1);
                            break;
                        case 2:
                            break;
                        case 3:
                            ShowDialogUtil.setProgress(now, all);
                            break;
                        case 4:
                            if (outTime != null) {
                                outTime.cancel();
                                outTime = null;
                            }
                            handler.sendEmptyMessage(2);
                            break;
                        case 5:
                            handler.sendEmptyMessage(5);
                            break;
                        case 6:
                            // 发送广播，四个主界面左上角图标变更.首次连接成功查询状态，之后每次心跳成功后发送查询
                            Intent intent = new Intent();
                            intent.setAction(KappUtils.ACTION_PMS_STATUS);
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                            Intent mIntent = new Intent("aaa");
                            mIntent.putExtra("yaner", "发送广播，相当于在这里传送数据");

                            // 发送广播
                            getActivity().sendBroadcast(mIntent);

                            break;
                        case 9:
                            // 发送广播，四个主界面左上角图标变更.首次连接成功查询状态，之后每次心跳成功后发送查询
                            System.out.print("----对时功能成功----");
                            onResume();
                            break;
                    }
                    if (flag != 5 && !TextUtils.isEmpty(deleteFileName)) {
                        handler.sendEmptyMessage(3);
                    }
                }

                @Override
                public void onFailure(int flag) {
                    //20160919flag 默认为0;-1：下载文件，文件大小=0;
                    if (!TextUtils.isEmpty(deleteFileName)) {
                        handler.sendEmptyMessage(3);
                    }
                    switch (flag) {
                        case -1:
                            handler.sendEmptyMessage(flag);
                            break;
                        default:
                            Config.SERVER_HOST_NAME = "";
                            handler.sendEmptyMessage(flag);
                            break;
                    }
                }
            });
        }
    }

    public static boolean isWifiConnected(Context context) {
        Log.i("DeviceFragment", "isWifiConnected-->");
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }

        return false;
    }

    private void connectPMS() {//20160919连接设备
        Log.i(TAG, "connectPMS-->");
        // 拿到PMS信息，判断当前网络下是否有那个wifi，有的话则连接上wifi，然后判断PMS的ip是否存在，然后与PMS连接
        boolean isOpenWifi = WifiUtil.openWifi(context);//20160919标记wifi开关状态
        if (!isOpenWifi) {
            while (!WifiUtil.isEnable(context)) {//20160919wifi未开启
                Log.i(TAG, "connectPMS-->开启wifi中");
                MyLog.d("开启wifi中");
                try {
                    Thread.sleep(1500);//20160919暂停执行
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            WifiUtil.startScan();//20160919扫描wifi
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //20160919在设备wifi局域网（获取wifi信息）
        List<WifiConfiguration> wifiTempList = WifiUtil.getWifiConfigurationList();//20160919已连过wifi
        boolean isExist = false;//20160919标记wifi是否存在
        for (int i = 0; i < wifiTempList.size(); i++) {
            WifiConfiguration wifiConfiguration = wifiTempList.get(i);//20160919获取wifi信息
            String str = wifiConfiguration.SSID.substring(1, wifiConfiguration.SSID.length() - 1);//20160919去掉wifi名称的引号部分

            //20160919通过直接连接
            if (str.equals(connectDeviceBean.getWifiName())) { // 当前可以连接指定wifi
                isExist = true;
                WifiUtil.connectWifi(wifiConfiguration, true);//20160919连接选定网络，禁用其他网络被
                Log.i(TAG, "connectPMS-->WifiUtil.connectWifi(wifiConfiguration, true)-->wifiName:;" + connectDeviceBean.getWifiName());
                Log.i(TAG, "h.sendEmptyMessage(0);");
                h.sendEmptyMessage(0);
                break;
            }

            //20160919通过路由器连接
            if (i == wifiTempList.size() - 1) {
                List<ScanResult> wifiScanList = WifiUtil.getWifiList();
                for (int j = 0; j < wifiScanList.size(); j++) {
                    ScanResult scanResult = wifiScanList.get(j);
                    if (scanResult.SSID.equals(connectDeviceBean.getWifiName()) && scanResult.capabilities.contains("[ESS]")) {//20160919contains：是否包含//ESS：扩展服务集（ESS）
                        isExist = true;
                        WifiUtil.connectWifi(scanResult.SSID, true);
                        Log.i(TAG, "connectPMS-->WifiUtil.connectWifi(wifiConfiguration, true)," + "最后一个wifiTempList-->wifiName:;" + connectDeviceBean.getWifiName());
                        Log.i(TAG, "h.sendEmptyMessage(0);");
                        h.sendEmptyMessage(0);
                        break;
                    }
                }
            }
        }

        if (!isExist) {
            KappUtils.showToast(context, "wifi：" + connectDeviceBean.getWifiName() + "不在范围内或已被清除配置信息");
            closeDlg();
        }
    }

    private void deletePMSDevice(int position) {//20160919（长）点击删除设备
        final DeviceNameAndIPBean bean = deviceList.get(position);//20160919列表上选中的设备信息
        TipConfirmView.showConfirmDialog(context, "是否确认删除Wifi为\"" + bean.getWifiName() + "\"网络下的设备\"" + bean.getName() + "\"？", new OnClickListener() {
            @Override
            public void onClick(View v) {
                TipConfirmView.dismiss();
                if (new DevicesDAO().deleteDeviceById(bean.getId())) {//20160919根据数据库PMS的Id删除设备
                    showDeviceList();
                    DeviceNameAndIPBean bean2 = SharedPreferenceUtil.getPmsInfo(context);//20160919存储在SP上的设备信息
                    if (bean.getName() != null && bean2.getName() != null) {//20160919
                        if (bean.getName().equals(bean2.getName())//20160919对比设备名称（选中对比SP）
                                && bean.getIp().equals(bean2.getIp())//20160919对比设备IP
                                && bean.getWifiName().equals(bean2.getWifiName())) //20160919对比设备wifi名
                        {
                            bean2 = new DeviceNameAndIPBean();
                            SharedPreferenceUtil.setPmsInfo(context, bean2);//20160919存储SP的设备信息置空
                            KappUtils.showToast(context, "删除成功");
                        }
                    }

                } else {
                    KappUtils.showToast(context, "删除失败");
                }
            }
        });
    }

    private void deletePMSFile(final int flag, int position) {
        final String fileName;
        if (flag == 0) {
            fileName = selfFileList.get(position);
        } else if (flag == 1) {
            fileName = downFileList.get(position);
        } else {
            KappUtils.showToast(context, "flag传值错误");
            return;
        }
        TipConfirmView.showConfirmDialog(context, "是否确认删除文件-\"" + fileName
                + "\"？", new OnClickListener() {
            @Override
            public void onClick(View v) {
                TipConfirmView.dismiss();
                deleteFileName = fileName;
                byte[] arr = null;
                try {
                    arr = deleteFileName.getBytes("gb2312");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                showDlg();
                if (flag == 0) {
                    socketTool.splitInstruction(Config.bufDelSelfFile, arr);
                } else if (flag == 1) {
                    socketTool.splitInstruction(Config.bufDelDownFile, arr);
                }
            }
        });
    }

    Handler h = new Handler(new Callback() {
        @Override
        public boolean handleMessage(final Message msg) {
            switch (msg.what) {
                case 0://20160914自动扫描连接设备（//通过直接连接或路由器连接）
                    Log.i(TAG, "h-->0-->");
                    OnEvent onEvent = new OnEvent() {
                        @Override
                        public void onTick(long millisUntilFinished) {//20160919发起倒计时（millisUntilFinished倒计时的剩余时间）
                            Log.i(TAG, "h-->0-->" + "onTick ." + millisUntilFinished);
                            MyLog.d("onTick ." + millisUntilFinished);
                            if (WifiUtil.getWifiInfo() != null//20160919连接网络信息不为空
                                    && connectDeviceBean != null//20160919设备的名称和IP不为空
                                    && !TextUtils.isEmpty(new EspWifiAdminSimple(context).getWifiConnectedSsid())//20160919SSID不为空//获取活跃状态或在建立中wifi的SSID
                                    && WifiUtil.getWifiInfo().getSSID().equals("\"" + connectDeviceBean.getWifiName() + "\""))//20160919连接的wifi和设备的wifi相同
                            {
                                KappUtils.showToast(context, "连接wifi成功");
                                MyLog.d("onTick 连接wifi成功." + millisUntilFinished);
                                Log.i(TAG, "h.sendEmptyMessage(1);");
                                h.sendEmptyMessage(1);//20160919获取本机IP
                            }
                        }

                        @Override
                        public void onFinish() {//20160919倒计时结束后会调用onFinish，倒计时结束后需要执行的操作
                            Log.i(TAG, "h-->0-->" + "onFinish .");
                            if (WifiUtil.getWifiInfo() != null
                                    && connectDeviceBean != null
                                    && !TextUtils.isEmpty(new EspWifiAdminSimple(context).getWifiConnectedSsid())
                                    && WifiUtil.getWifiInfo().getSSID().equals("\"" + connectDeviceBean.getWifiName() + "\"")) {
                                KappUtils.showToast(context, "连接wifi成功");
                                iv_titleLeft.setImageResource(R.drawable.icon04);//20160919图标
                                Log.i(TAG, "h.sendEmptyMessage(1);");
                                h.sendEmptyMessage(1);//20160914连接wifi成功
                            } else {
                                if (connectDeviceBean != null) {//20160919倒计时结束后没获取到所需信息则连接失败
                                    KappUtils.showToast(context, "连接wifi失败");
                                    connectDeviceBean = null;
                                }
                            }
                        }
                    };
                    outTime = new TimeCountOut(1000 * 15, 1000, onEvent);//20160919重新发起计时
                    outTime.start();
                    break;
                case 1://20160919获取本机IP
                    Log.i(TAG, "h-->1-->");
                    KappUtils.getLocalIP(context);//20160919获取本机IP
                    if (outTime != null) {
                        outTime.cancel();//20160919取消计时
                        outTime = null;
                    }
                    Config.SERVER_HOST_IP = connectDeviceBean.getIp();//20160919给设备赋值真正IP地址
                    // 倒计时10秒，10秒内没有指令与PMS连接成功，则给出提示，且去掉转圈
                    outtime = new TimeCountOut(10 * 1000, 1000, new OnEvent() {//20160919开始倒计时（10秒）
                        @Override
                        public void onFinish() {
                            handler.sendEmptyMessage(0);//20160914连接失败
                        }

                        @Override
                        public void onTick(long millisUntilFinished) {
                        }
                    });

                    initSocketTool();//20160919加载连接设备的数据

                    socketTool.firstConnect(); // 根据不同的ip，建立不同的socket
                    outtime.start();//20160919开始倒计时
                    break;
                case 2://20160919点击设备列表连接设备
                    Log.i(TAG, "h-->2-->");
                    showDlg();//20160919显示转圈
                    connectPMS();//20160919连接设备
                    break;
                case 3:
                    Log.i(TAG, "h-->3-->");
                    /** 20161010暂停下载 */
                    pDlg = ShowDialogUtil.getShowDialog(activity, R.layout.dialog_progressbar_2, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, 0, 0, false, View.VISIBLE);
                    String s = (String) msg.obj;
                    if ("1".equals(s)) {
                        downloadFromPMS(1, msg.arg1);
                    } else if ("2".equals(s)) {
                        downloadFromPMS(0, msg.arg1);
                    }
                    break;
            }
            return false;
        }
    });

    public void showDlg() {
        if (null != this && null != dlg && !dlg.isShowing()) {
            dlg.show();
        } else if (null != this && null == dlg) {
            dlg = ShowDialogUtil.getShowDialog(activity, R.layout.dialog_progressbar, 0, 0, true);
        }
    }

    public void closeDlg() {
        if (null != this && null != dlg && dlg.isShowing()) {
            dlg.dismiss();
        }
    }

    Handler handler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == -1 && pDlg != null && pDlg.isShowing()) {
                KappUtils.showToast(context, "文件不存在或其他异常，获取到文件大小为0");
            }
            switch (msg.what) {
                case -1:
                    break;
                case 0://20160914连接失败业务
                    if (pDlg != null && pDlg.isShowing()) {
//                        KappUtils.showToast(context, "文件下载失败");
                    }
                    if (connectDeviceBean != null) {
                        if (outtime != null) {
                            outtime.cancel();
                            outtime = null;
                        }
                        MyLog.d("与PMS的指令连接失败,清除connectDeviceBean");
                        Config.isConnext = false;//20160913设备离线
                        updatePmsStatus();//20160919更新离（在）线图标
                        KappUtils.showToast(context, "与PMS的指令连接失败");
                        connectDeviceBean = null;//20160919连接设备名称和IP置空
                        Config.SERVER_HOST_NAME = "";//20160919服务器名称置空
                    }
                    if (TextUtils.isEmpty(Config.SERVER_HOST_NAME)) {
                        showDeviceList();//20160919显示（之前连接过）设备列表
                    }
                    if (dlg != null)
                        dlg.dismiss();
                    break;
                case 1://20160914下载成功业务
                    closePDlg();
                    KappUtils.showToast(context, "下载成功");
                    downLoadState = false;
                    Intent intent = new Intent(context, CookBookDetailActivity.class);
                    intent.putExtra("filePath", DeviceFragment.saveFileName);
                    DeviceFragment.saveFileName = "";
                    startActivity(intent);
                    break;
                case 2: // PMS连接成功
                    KappUtils.showToast(context, "与PMS连接成功");
                    try {
                        socketTool.splitInstruction(Config.bufSetTime, new DateUtil().getCurrentTime());
                        System.out.print("----对时功能1----" + new DateUtil().getCurrentTime());
                        KappAppliction.state = 1;
                        if (connectDeviceBean != null) {
                            Config.SERVER_HOST_NAME = connectDeviceBean.getName();
                            connectDeviceBean = null;
                        }
                        Config.isConnext = true;//20160913设备在线
                        updatePmsStatus();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }


//				onResume();
                    break;
                case 3: // 删除文件失败
                    deleteFileName = "";
                    KappUtils.showToast(context, "文件删除失败");
                    break;
                case 5: // 删除文件成功
                    deleteFileName = "";
                    KappUtils.showToast(context, "文件删除成功");
                    if (rb_selfFile.isChecked()) {
                        getPMSSelfFileNum();
                    } else {
                        getPMSDownFileNum();
                        System.out.print("----请求菜谱文件总数110----");
                    }
                    break;
            }
            return false;
        }
    });

    private void closePDlg() {
        if (pDlg != null && pDlg.isShowing()) {
            pDlg.dismiss();
        }
    }

    private void showDeviceList() {//20160919显示（之前连接过）设备列表
        Log.i(TAG, "showDeviceList-->");
        tv_title.setText("我的设备中心");//20160919“标题”改变为“我的设备中心”
        btn_titleRight.setVisibility(View.GONE);//20160919隐藏“保存”按钮
        updatePmsStatus();//20160920更新PMS设备（图标）状态
        iv_titleRight.setVisibility(View.VISIBLE);//20160920右上角图标看见
        layout_connected.setVisibility(View.GONE);//20160920隐藏录波/菜谱文件
        List<DeviceNameAndIPBean> deviceListTemp = new DevicesDAO().getAllDevices();//20160920查询所有PMS设备

        if (deviceListTemp == null || deviceListTemp.size() == 0) { // 本地没有设备数据
            Log.i(TAG, "deviceListTemp.size-->本地没有设备数据");
            layout_no_device.setVisibility(View.VISIBLE);//20160920显示没有设备界面
            layout_devices.setVisibility(View.GONE);//20160920隐藏设备列表界面
        } else {
            layout_no_device.setVisibility(View.GONE);
            layout_devices.setVisibility(View.VISIBLE);
            deviceList.clear();//20160920清空设备列表
            deviceList.addAll(deviceListTemp);//20160920加载设备列表数据
            Log.i(TAG, "deviceListTemp.size-->" + String.valueOf(deviceListTemp.size()));
            Log.i(TAG, "deviceList-->" + "重新插入并更新");
            deviceAdapter.notifyDataSetChanged();//20160920更新设备中心适配器数据
        }
    }

    private void downloadFromPMS(int flag, int position) {
        try {

            if (flag == 0) { // PMS内的录波
                saveFileName = selfFileList.get(position);
            } else { // 1
                saveFileName = downFileList.get(position);
            }
            downLoadState = true;
            byte[] arr = null;
            try {
                arr = saveFileName.getBytes("gb2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            byte[] bs = new byte[arr.length + 1]; // 菜谱
            for (int i = 0; i < arr.length + 1; i++) {
                if (i == 0) {
                    if (flag == 0) {
                        bs[i] = 0x00;
                    } else {
                        bs[i] = 0x01;
                    }
                } else {
                    bs[i] = arr[i - 1];
                }
            }
            socketTool.splitInstruction(Config.bufFileLenth, bs);

        } catch (Exception e) {
            closePDlg();
        }
    }

    //20160919
    public static boolean isClearList = false;

    private void clearList() {
        Log.i(TAG, "clearList-->");
        if (isClearList) {
            isClearList = false;
            socketTool = null;
            rb_selfFile.setChecked(true);//20160916录波文件为选中状态
            selfFileList.clear();//20160916清空录波文件列表
            downFileList.clear();//20160916清空菜谱文件列表

            DeviceNameAndIPBean bean = new DeviceNameAndIPBean();
            bean.setName(Config.SERVER_HOST_NAME);
            bean.setWifiName(new EspWifiAdminSimple(context).getWifiConnectedSsid());
            bean.setIp(Config.SERVER_HOST_IP);
            // 更新上次所连接的设备信息
            SharedPreferenceUtil.setPmsInfo(context, bean);
            new DevicesDAO().save(bean); // 若本地（20160919数据库）没有该连接数据，则新增保存
        }
    }

    private void updatePmsStatus() {//20160919更新PMS设备状态
        Log.i(TAG, "updatePmsStatus-->");
        if (TextUtils.isEmpty(Config.SERVER_HOST_NAME)) {//20160914服务器地址为空则为离线状态
            iv_titleLeft.setImageResource(R.drawable.icon05);//20160914离线图标
        } else {
            iv_titleLeft.setImageResource(R.drawable.icon04);//20160914在线图标
        }
        // if(Config.isConnext==true){
        // iv_titleLeft.setImageResource(R.drawable.icon04);
        // }else{
        // iv_titleLeft.setImageResource(R.drawable.icon06);
        // }
        // else {
        // if (Config.PMS_ERRORS.size() > 0) {
        // iv_titleLeft.setImageResource(R.drawable.icon06);
        // } else {
        // iv_titleLeft.setImageResource(R.drawable.icon04);
        // }
        // }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (TextUtils.isEmpty(Config.SERVER_HOST_NAME)) {//20160920服务器（PMS设备）地址为空
            HHDLog.w("服务器（PMS设备）地址为空");
            if (socketTool != null) {
                socketTool.closeSocket();//20160920关闭Socket连接
                Log.i(TAG, "onResume-->socketTool.closeSocket()");
                socketTool = null;//20160920Socket置空
            }
            showDeviceList();//20160920显示设备列表
            if (isFirst) {//20160920首次连接
                isFirst = false;
                // 获取上次连接设备，自动连接
                connectDeviceBean = SharedPreferenceUtil.getPmsInfo(context);
                if (connectDeviceBean != null) {//20160914设备名称和ip不为空就连接
                    h.sendEmptyMessage(2);
                }
            }
        } else {
            Log.i(TAG, "onResume-->Config.SERVER_HOST_NAME-->非空:");

            clearList();//20160916清空两个列表

            tv_title.setText(Config.SERVER_HOST_NAME);//20160920“标题”改为当前PMS设备名称
            btn_titleRight.setVisibility(View.VISIBLE);//20160920显示右边按钮
            iv_titleRight.setVisibility(View.GONE);//20160920隐藏右边图标
            updatePmsStatus();////20160920更新PMS设备状态（左边图标）

            layout_devices.setVisibility(View.GONE);//20160920隐藏设备列表
            layout_no_device.setVisibility(View.GONE);//20160920隐藏没有设备提示界面
            layout_connected.setVisibility(View.VISIBLE);//20160920显示录波/菜谱文件
            initSocketTool();//20160920加载Socket工具

            if (!socketTool.isStartHeartTimer()) {//20160920心跳计时为未启动状态
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();//20160920初始化（使用）当前线程的Looper
                        try {
                            socketTool.initClientSocket(); // 根据不同的ip，建立不同的socket
                            socketTool.startHeartTimer();//20160920启动心跳计时
                            if (selfFileList.size() == 0 && downFileList.size() == 0) {//20160920录波&菜谱文件列表长度为0
                                System.out.print("----请求菜谱文件列表总数1----");
                                fileFlag = true;//20160920录波文件
//							showDlg();
                                getPMSSelfFileNum();//20160920获取录波文件数量
                            }
                            Looper.loop();//20160920运行在消息队列的线程中，一定要调用quit()结束循环
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                }).start();

            } else {//20160920心跳计时为开启状态
                if (selfFileList.size() == 0 && rb_selfFile.isChecked()) {//20160920如果录波列表为0而且为选中状态
                    final byte[] bufFilePath = {0x00};
                    System.out.print("----请求录波文件总数2----");
                    fileFlag = true;
                    // showDlg();
                    socketTool.splitInstruction(Config.bufGetFileNum, bufFilePath);//20160920获取录波文件数量
//					showDlg();
                } else if (rb_downFile.isChecked() && downFileList.size() == 0) {//20160920如果菜谱列表为0而且为选中状态
                    System.out.print("----请求菜谱文件列表总数2----");
                    fileFlag = false;
                    getPMSDownFileNum();
                }
            }
        }

        if (Config.flag == 2) {
            smartPotStatu smart = new smartPotStatu();
            if (smart.queryDirty()) {
                MenuFileRefrash();
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    socketTool.closeSocket();
                    socketTool.initClientSocket(); // 根据不同的ip，建立不同的socket
                    socketTool.startHeartTimer();
                    Looper.loop();
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }).start();

    }

    private void getPMSDownFileNum() {
        final byte[] bufFilePath = {0x01};//20160920查询录波文件列表F3 01
        fileFlag = false;//20160920显示录波文件列表
        downFileList.clear();//20160920清空录波文件列表
        socketTool.splitInstruction(Config.bufGetFileNum, bufFilePath);//20160920发送指令（“F3 01”查询文件数量）
        System.out.print("----发送1----");

    }

    private void getPMSSelfFileNum() { // 录波文件

        final byte[] bufFilePath = {0x00};//20160920数据长度
        fileFlag = true;//20160920录波文件
        selfFileList.clear();//20160920清空录波文件列表

        socketTool.splitInstruction(Config.bufGetFileNum, bufFilePath);//20160920“F3 00”获取录波文件数量
        System.out.print("----发送3----");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause-->");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (socketTool != null) {
            socketTool.closeSocket();
        }
        Config.SERVER_HOST_NAME = "";

        broadcastManager.unregisterReceiver(receiver);
        if (outtime != null) {
            outtime.cancel();
            outtime = null;
        }
        if (outTime != null) {
            outTime.cancel();
            outTime = null;
        }
    }

    public class TimeCountOut extends CountDownTimerUtil {
        private OnEvent onEvent;

        //20160919CountDownTimer(30000, 1000)中的30000，表示倒计时时间为30秒，1000表示每隔1秒钟调用一次onTick方法
        public TimeCountOut(long millisInFuture, long countDownInterval, OnEvent onEvent2) {
            super(millisInFuture, countDownInterval);
            this.onEvent = onEvent2;
        }

        @Override
        public void onFinish() {
            closeDlg();
            if (onEvent != null) {
                onEvent.onFinish();//20160919倒计时结束后会调用onFinish，倒计时结束后需要执行的操作
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (onEvent != null) {
                onEvent.onTick(millisUntilFinished);
            }
        }
    }

    public interface OnEvent {//20160919触发事件

        void onFinish();//20160919倒计时结束后会调用onFinish，倒计时结束后需要执行的操作

        void onTick(long millisUntilFinished);//20160919发起倒计时（millisUntilFinished倒计时的剩余时间）
    }

    @Override
    public void onActivityResult(int arg0, int arg1, Intent arg2) {
        if (arg1 == getActivity().RESULT_OK) {
            int position;
            switch (arg0) {
                case 101:
                    socket = arg2.getStringExtra("socket");
                    System.out.print("----socket----" + socket);
                    break;

                default:

                    break;
            }
        }
        super.onActivityResult(arg0, arg1, arg2);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {

        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        Log.i(TAG, "onHiddenChanged-->" + String.valueOf(hidden));
        if (hidden) {

        } else {
            if (Config.flag == 2) {
                smartPotStatu smart = new smartPotStatu();
                if (smart.queryDirty()) {
                    MenuFileRefrash();
                }
            }
        }
    }

    /*
     * 更新菜谱文件
     */
    private void MenuFileRefrash() {
        rb_downFile.setTextColor(Color.parseColor("#ff5b80"));
        view_downFile.setVisibility(View.VISIBLE);
        if (downAdapter == null) {
            downAdapter = new CookBookAdapter(context,
                    downFileList, new CookBookAdapter.OnReceiver() {
                @Override
                public void onDownload(int position) {
                    Message msg = new Message();
                    msg.what = 3;
                    msg.arg1 = position;
                    msg.obj = "1";
                    h.sendMessage(msg);
                    //downloadFromPMS(1, position);
                }

                @Override
                public void onDelete(int position) {
                    deletePMSFile(1, position);
                }
            });
        }

        lv_file.setAdapter(downAdapter);
        downFileList.clear();
        // onResume();
        // socketTool.initClientSocket(); // 根据不同的ip，建立不同的socket
        // getPMSDownFileNum();
        // initSocketTool();
        // downFileList.clear();
        showDlg();
//	new Handler().postDelayed(new Runnable(){
//        public void run() {
//        	if(dlg.isShowing()){
//        	closeDlg();
//        	socketTool.closeSocket();
//			socketTool.initClientSocket();
//			socketTool.PMS_Send(Config.bufConnect);
//			iv_titleLeft.setImageResource(R.drawable.icon06);
//			KappUtils.showToast(context, "连接断开,正在尝试重新连接");
//        	}
//        }
//     }, 15000);
        new Thread(new Runnable() {
            @Override
            public void run() {

                Looper.prepare();
                socketTool.closeSocket();
                socketTool.initClientSocket();
//                if (!socketTool.isStartHeartTimer()) {// 根据不同的ip，建立不同的socket
                    socketTool.startHeartTimer();
//                }
                // if (!socketTool.isStartHeartTimer()) {
                // socketTool.startHeartTimer();
                // System.out.print("----发送心跳包----");
                // }
                System.out.print("----请求菜谱文件总数3333333----");
                final byte[] bufFilePath = {0x01};
                fileFlag = false;
                downFileList.clear();
                socketTool.splitInstruction(Config.bufGetFileNum,
                        bufFilePath);
                System.out.print("----发送4444----");
                // getPMSDownFileNum();
                Looper.loop();
            }
        }).start();


//		resetTime();
//		mtimeHandler = new EffectInVisiableHandler();
//		Message msg = mtimeHandler.obtainMessage(MOBILE_QUERY);
//		mtimeHandler.sendMessageDelayed(msg, 10000);
//	if(!socketTool.isStartHeartTimer()){


        //
        System.out.print("----请求菜谱文件总数0----");

    }
}