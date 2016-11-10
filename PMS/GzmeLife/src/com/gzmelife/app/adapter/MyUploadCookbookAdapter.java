package com.gzmelife.app.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gzmelife.app.R;
import com.gzmelife.app.bean.MyUploadCookbookBean;
import com.gzmelife.app.tools.ImgLoader;

/**
 */
@SuppressLint("InflateParams")
public class MyUploadCookbookAdapter extends BaseAdapter {

    private Context context;
    private List<MyUploadCookbookBean> list;
    
    private LayoutInflater inflater;

    public MyUploadCookbookAdapter(Context context, List<MyUploadCookbookBean> list) {
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
            convertView = inflater.inflate(R.layout.item_lv_my_upload_cookboon, null);
            viewHolder.tv_cookbookName = (TextView) convertView.findViewById(R.id.tv_cookbookName);
            viewHolder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            viewHolder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MyUploadCookbookBean bean = list.get(position);
        viewHolder.tv_cookbookName.setText(bean.getName());
        viewHolder.tv_date.setText(bean.getCreateTime().substring(0, 10));
        new ImgLoader(context).showPic(bean.getLogoPath(), viewHolder.iv_pic, R.drawable.icon_pms_file);

        return convertView;
    }
    
    public static class ViewHolder {
        TextView tv_cookbookName;
        TextView tv_date;
        ImageView iv_pic;
    }
}
