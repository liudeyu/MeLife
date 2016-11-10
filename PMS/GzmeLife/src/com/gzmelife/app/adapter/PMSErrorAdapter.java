package com.gzmelife.app.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gzmelife.app.R;
import com.gzmelife.app.device.Config;

/**
 */
@SuppressLint("InflateParams")
public class PMSErrorAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;
    
    private OnReceiver onReceiver;

    private LayoutInflater inflater;

    public PMSErrorAdapter(Context context, List<String> list, OnReceiver onReceiver) {
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
            convertView = inflater.inflate(R.layout.item_lv_pms_error, null);
            viewHolder.tv_error = (TextView) convertView.findViewById(R.id.tv_error);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position == 0) {
        	viewHolder.tv_title.setVisibility(View.VISIBLE);
        } else {
        	viewHolder.tv_title.setVisibility(View.INVISIBLE);
        }
        viewHolder.tv_error.setText(Html.fromHtml("<u>" + Config.errorDesc[Integer.parseInt(list.get(position))] + "</u>"));
        
        convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onReceiver != null) {
					onReceiver.onClick(Config.errorDesc[Integer.parseInt(list.get(position))]);
				}
			}
		});

        return convertView;
    }

    public interface OnReceiver {
    	void onClick(String position);
    }
    
    public static class ViewHolder {
        TextView tv_error;
        TextView tv_title;
    }
}
