package com.gzmelife.app.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.gzmelife.app.R;
import com.gzmelife.app.bean.TasteBean;

/**
 */
@SuppressLint("InflateParams")
public class TasteAdapter extends BaseAdapter {

    private Context context;
    private List<TasteBean> list;
    
    private LayoutInflater inflater;

    public TasteAdapter(Context context, List<TasteBean> list) {
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
            convertView = inflater.inflate(R.layout.item_lv_taste, null);
            viewHolder.cb_taste = (CheckBox) convertView.findViewById(R.id.cb_taste);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        TasteBean bean = list.get(position);
        viewHolder.cb_taste.setText(bean.getName());
    	viewHolder.cb_taste.setChecked(bean.isChecked());
        viewHolder.cb_taste.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				list.get(position).setChecked(!list.get(position).isChecked());
				viewHolder.cb_taste.setChecked(list.get(position).isChecked());
			}
		});

        return convertView;
    }
    
    public static class ViewHolder {
        CheckBox cb_taste;
    }

	public String getChecked() {
		String result = "";
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).isChecked()) {
				result += i + ",";
			}
		}
		if (result.endsWith(",")) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
	
	
	public String getCheckedName() {
		String result = "";
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).isChecked()) {
				result += list.get(i).getName() + ",";
			}
		}
		if (result.endsWith(",")) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
	
	public String getCheckedId() {
		String result = "";
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).isChecked()) {
				result += list.get(i).getId() + ",";
			}
		}
		if (result.endsWith(",")) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
}
