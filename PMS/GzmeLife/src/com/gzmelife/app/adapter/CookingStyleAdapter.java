package com.gzmelife.app.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gzmelife.app.R;
import com.gzmelife.app.bean.CookingStyleBean;
import com.gzmelife.app.tools.ImgLoader;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.views.CircleImageView;

/**
 */
@SuppressLint("InflateParams")
public class CookingStyleAdapter extends BaseAdapter {
    private Context context;
    private List<CookingStyleBean> list;
    private LayoutInflater inflater;
    String TAG="CookingStyleAdapter";
    public CookingStyleAdapter(Context context, List<CookingStyleBean> list) {
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
            convertView = inflater.inflate(R.layout.item_lv_cooking_style, null);
            viewHolder.layout_pic = convertView.findViewById(R.id.layout_pic);
            viewHolder.iv_pic = (CircleImageView) convertView.findViewById(R.id.iv_pic);
            viewHolder.iv_check = (CircleImageView) convertView.findViewById(R.id.iv_check);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CookingStyleBean bean = list.get(position);
        viewHolder.tv_title.setText(bean.getName());
        new ImgLoader(context).showPic(bean.getLogoPath(), viewHolder.iv_pic);
        if (bean.isChecked()) {
        	viewHolder.iv_check.setVisibility(View.VISIBLE);
        } else {
        	viewHolder.iv_check.setVisibility(View.GONE);
        }
        viewHolder.layout_pic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				list.get(position).setChecked(!list.get(position).isChecked());
				if (list.get(position).isChecked()) {
					viewHolder.iv_check.setVisibility(View.VISIBLE);
				} else {
					viewHolder.iv_check.setVisibility(View.GONE);
				}
			}
		});

        return convertView;
    }
    public static class ViewHolder {
        TextView tv_title;
        CircleImageView iv_check;
        CircleImageView iv_pic;
        View layout_pic;
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
				MyLog.i(TAG,"lll~"+list.get(i).getName());
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
				MyLog.i(TAG,"~~~~"+list.get(i).getId());
				result += list.get(i).getId() + ",";
			}
		}
		if (result.endsWith(",")) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
}
