package com.gzmelife.app.adapter;

import java.util.ArrayList;
import java.util.List;

import com.gzmelife.app.R;
import com.gzmelife.app.adapter.CookBookAdapter.OnReceiver;
import com.gzmelife.app.bean.CookBookMenuBookCommentsBean;
import com.gzmelife.app.tools.ImgLoader;
import com.gzmelife.app.views.CircleImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LvCommentAdapter extends BaseAdapter {
	Context context;
	ViewHolder viewHolder;
	private List<CookBookMenuBookCommentsBean> list = new ArrayList<CookBookMenuBookCommentsBean>();
	private LayoutInflater inflater;

	public LvCommentAdapter(Context context,
			List<CookBookMenuBookCommentsBean> list) {
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
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_lv_comment, null);
			viewHolder.civ_icon = (CircleImageView) convertView
					.findViewById(R.id.civ_icon);
			viewHolder.tv_Name = (TextView) convertView
					.findViewById(R.id.tv_Name);
			viewHolder.tv_comment = (TextView) convertView
					.findViewById(R.id.tv_comment);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		CookBookMenuBookCommentsBean bean = list.get(position);
		viewHolder.tv_Name.setText(bean.getUserNickName());
		viewHolder.tv_comment.setText(bean.getContent());
		new ImgLoader(context).showPic(bean.getUserLogoPath(),
				viewHolder.civ_icon);
		return convertView;
	}

	static class ViewHolder {
		CircleImageView civ_icon;
		TextView tv_Name;
		TextView tv_comment;
	}
}
