package com.gzmelife.app.adapter;

import java.util.List;

import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.activity.NetCookBookDetailActivity;
import com.gzmelife.app.activity.SecondClassifyActivity;
import com.gzmelife.app.adapter.CookFoodBeanAdapter.ViewHolder;
import com.gzmelife.app.bean.CoonFoodMenuBean;
import com.gzmelife.app.tools.ImgLoader;
import com.gzmelife.app.tools.ImgLoader_2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GvSecondClassifyAdapter extends BaseAdapter {
	Context context;

	String name;
	String categoryId;
	private List<CoonFoodMenuBean> categoryCoonFoodMenuBeanList;

	public GvSecondClassifyAdapter(Context context,
			List<CoonFoodMenuBean> categoryCoonFoodMenuBeanList) {
		this.context = context;
		this.categoryCoonFoodMenuBeanList = categoryCoonFoodMenuBeanList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return categoryCoonFoodMenuBeanList.size();
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
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_cookfood_more, null);
			viewHolder.item_layout = (LinearLayout) convertView
					.findViewById(R.id.item_layout);
			viewHolder.tv_name = (TextView) convertView
					.findViewById(R.id.tv_name);
			viewHolder.tv_num = (TextView) convertView
					.findViewById(R.id.tv_num);
			viewHolder.iv_food = (ImageView) convertView
					.findViewById(R.id.iv_food);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tv_name.setText(categoryCoonFoodMenuBeanList.get(position)
				.getName());
		viewHolder.tv_num.setText(categoryCoonFoodMenuBeanList.get(position)
				.getClickNumber() + "次阅读");
		new ImgLoader_2(context).showPic(
				categoryCoonFoodMenuBeanList.get(position).getLogoPath(),
				viewHolder.iv_food);
		final String id = categoryCoonFoodMenuBeanList.get(position).getId();
		if (categoryCoonFoodMenuBeanList.get(position).getLogoPath().equals("")) {
			viewHolder.iv_food.setImageResource(R.drawable.icon_pms_file);
		}
		if (position % 2 == 0) {
			viewHolder.item_layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context,
							NetCookBookDetailActivity.class);
					System.out.println("position===" + position % 2);
					String text = String.valueOf(viewHolder.tv_num.getText());
					String[] str = text.split("次阅读");
					int n = Integer.valueOf(str[0]);
					if (KappAppliction.getLiLogin() == 1) {
						n++;
					}
					viewHolder.tv_num.setText(String.valueOf(n + "次阅读"));

					intent.putExtra("menuBookId", categoryCoonFoodMenuBeanList
							.get(position).getId());
					context.startActivity(intent);
				}
			});
		} else if (position % 2 == 1) {
			viewHolder.item_layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context,
							NetCookBookDetailActivity.class);
					System.out.println("position===" + position % 2);
					String text = String.valueOf(viewHolder.tv_num.getText());
					String[] str = text.split("次阅读");
					int n = Integer.valueOf(str[0]);
					if (KappAppliction.getLiLogin() == 1) {
						n++;
					}
					viewHolder.tv_num.setText(String.valueOf(n + "次阅读"));

					intent.putExtra("menuBookId", categoryCoonFoodMenuBeanList
							.get(position).getId());
					context.startActivity(intent);
				}
			});
		}

		return convertView;
	}

	public static class ViewHolder {
		TextView tv_name;
		ImageView iv_food;
		TextView tv_num;
		LinearLayout item_layout;
	}

}
