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
import com.gzmelife.app.bean.CategorySecondBean;
import com.gzmelife.app.tools.ImgLoader;
import com.gzmelife.app.views.CircleImageView;

/**
 */
@SuppressLint("InflateParams")
public class GoodFoodMaterialChildAdapter extends BaseAdapter {

	private Context context;
	private List<CategorySecondBean> list;

	private OnReceiver onReceiver;

	private LayoutInflater inflater;

	public GoodFoodMaterialChildAdapter(Context context,
			List<CategorySecondBean> list, OnReceiver onReceiver) {
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
		final ViewHolder viewHolder;
		if (null == convertView) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(
					R.layout.item_lv_standard_foodmaterial_child, null);
			viewHolder.tv_name = (TextView) convertView
					.findViewById(R.id.tv_name);
			viewHolder.iv_img = (CircleImageView) convertView
					.findViewById(R.id.iv_img);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		convertView.setClickable(true);
		CategorySecondBean bean = list.get(position);
		viewHolder.tv_name.setText(bean.getScName());
		new ImgLoader(context).showPic(bean.getScLogoPath(), viewHolder.iv_img,
				R.drawable.icon_pms_file);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onReceiver != null) {
					onReceiver.onClick(position);
				}
			}
		});

		return convertView;
	}

	public interface OnReceiver {
		void onClick(int position);
	}

	public static class ViewHolder {
		TextView tv_name;
		CircleImageView iv_img;
	}
}
