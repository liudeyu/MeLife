package com.gzmelife.app.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.espressif.iot.esptouch.demo_activity.EspWifiAdminSimple;
import com.gzmelife.app.R;
import com.gzmelife.app.device.Config;
import com.gzmelife.app.tools.MyLog;

/**
 */
@SuppressLint("InflateParams")
public class PMSWifiAdapter extends BaseAdapter {

    private Context context;
    private List<ScanResult> list;
    
    private OnReceiver onReceiver;

    private LayoutInflater inflater;

    public PMSWifiAdapter(Context context, List<ScanResult> list, OnReceiver onReceiver) {
        super();
        this.context = context;
        this.list = list;
        this.onReceiver  = onReceiver;

        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_lv_pms_wifi, null);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
            viewHolder.btn_connect = (Button) convertView.findViewById(R.id.btn_connect);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ScanResult bean = list.get(position);
        viewHolder.tv_name.setText(bean.SSID);
        String connectedSsid = new EspWifiAdminSimple(context).getWifiConnectedSsid();
        MyLog.d("position=" + position + ",connectedSsid=" + connectedSsid);
        // 直连模式下，只有此wifi等于手机所连的wifi，且等于手机所连接的PMS设备名字，才算是直连模式下的已连接(可能存在PMS设备名字还未清除但是wifi已断开、wifi未断开但与PMS的连接已断开)
        if (!TextUtils.isEmpty(connectedSsid) && bean.SSID.equals(connectedSsid) && bean.SSID.equals(Config.SERVER_HOST_NAME)) {
    		viewHolder.btn_connect.setText("已连接");
    		viewHolder.btn_connect.setBackgroundResource(R.drawable.shape_gray_bg);
    		viewHolder.tv_status.setText("已连接");
        } else {
        	viewHolder.btn_connect.setText("连接");
        	viewHolder.btn_connect.setBackgroundResource(R.drawable.shape_titlecolor_bg);
        	viewHolder.tv_status.setText("未连接");
        }
        
        viewHolder.btn_connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (viewHolder.btn_connect.getText().toString().equals("连接") && onReceiver != null) {
					onReceiver.onClick(position);
				}
			}
		});

        return convertView;
    }

    public interface OnReceiver {
    	void onClick(int position);
    }
    
    public final static class ViewHolder {
        TextView tv_name;
        TextView tv_status;
        Button btn_connect;
    }
}
