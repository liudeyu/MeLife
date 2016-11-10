package com.gzmelife.app.adapter;

import java.util.List;

import com.gzmelife.app.R;
import com.gzmelife.app.activity.SecondClassifyActivity;
import com.gzmelife.app.bean.FindSecondCategoryBean;
import com.gzmelife.app.tools.ImgLoader;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 二级目录
 * 
 * @author chenxiaoyan
 *
 */
public class GVClassAdapter extends BaseAdapter {
	Context context;
	ViewHolder viewHolder;
	private List<FindSecondCategoryBean> findSecondCategoryBeanList ;

	public GVClassAdapter(Context context,List<FindSecondCategoryBean> findSecondCategoryBeanList) {
		this.context = context;
		this.findSecondCategoryBeanList=findSecondCategoryBeanList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return findSecondCategoryBeanList.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_gv_food, null);
			viewHolder.tv = (TextView) convertView.findViewById(R.id.tv);
			viewHolder.iv = (ImageView) convertView.findViewById(R.id.iv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tv.setText(findSecondCategoryBeanList.get(position).getName());
		new ImgLoader(context).showPic(findSecondCategoryBeanList.get(position).getLogoPath(),
				viewHolder.iv);
		viewHolder.iv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,
						SecondClassifyActivity.class);
				intent.putExtra("name", findSecondCategoryBeanList.get(position).getName());
				intent.putExtra("categoryId", findSecondCategoryBeanList.get(position).getId());
				context.startActivity(intent);
			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView tv;
		ImageView iv;

	}

}
