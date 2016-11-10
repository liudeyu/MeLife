package com.gzmelife.app.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gzmelife.app.R;
import com.gzmelife.app.device.Config;
import com.gzmelife.app.views.TipConfirmView;

/**
 */
@SuppressLint("InflateParams")
public class CookBookAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;

    private OnReceiver onReceiver;
    
    private LayoutInflater inflater;

    public CookBookAdapter(Context context, List<String> list, OnReceiver onReceiver) {
        super();
        this.context = context;
        this.list = list;
        this.onReceiver = onReceiver;

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
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_lv_pms_file, null);
            viewHolder.tv_string = (TextView) convertView.findViewById(R.id.tv_string);
            viewHolder.iv_download = (ImageView) convertView.findViewById(R.id.iv_download);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(list.size()!=0){
        	 viewHolder.tv_string.setText(list.get(position));
        }
       
        viewHolder.iv_download.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

                if (Config.cancelTransfer){
                    Config.cancelTransfer=false;
                }
				if (Config.isOtherInstruction){
                    TipConfirmView.showConfirmDialog2(context, "其他用户正在操作，请几秒后再来~", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TipConfirmView.dismiss();

                        }
                    });
                    return;
                }
				
				if (onReceiver != null) {
					onReceiver.onDownload(position);
				}
			}
		});
        convertView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (onReceiver != null) {
					onReceiver.onDelete(position);
				}
				return false;
			}
		});
        
        return convertView;
    }
    
    public interface OnReceiver {
    	void onDownload(int position);
    	void onDelete(int position);
    }

    public final static class ViewHolder {
        TextView tv_string;
        ImageView iv_download;
    }
}
