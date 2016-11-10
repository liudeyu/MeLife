package com.gzmelife.app.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gzmelife.app.R;
import com.gzmelife.app.bean.SystemMsgBean;

/**
 */
@SuppressLint("InflateParams")
public class SystemMsgAdapter extends BaseAdapter {

    private Context context;
    private List<SystemMsgBean> list;
    
    private LayoutInflater inflater;

    public SystemMsgAdapter(Context context, List<SystemMsgBean> list) {
        super();
        this.context = context;
        this.list = list;

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
            convertView = inflater.inflate(R.layout.item_lv_faq, null);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SystemMsgBean bean = list.get(position);
        viewHolder.tv_title.setText(bean.getTitle());

        return convertView;
    }
    
    public static class ViewHolder {
        TextView tv_title;
    }
}
