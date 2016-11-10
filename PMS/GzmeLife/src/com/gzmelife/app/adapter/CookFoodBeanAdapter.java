package com.gzmelife.app.adapter;

import java.util.ArrayList;
import java.util.List;

import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.activity.MainActivity;
import com.gzmelife.app.activity.NetCookBookDetailActivity;
import com.gzmelife.app.adapter.CookFoodAdapter.ViewHolder;
import com.gzmelife.app.bean.CoonFoodMenuBean;
import com.gzmelife.app.tools.ImageLoaderUtils_circle;
import com.gzmelife.app.tools.ImgLoader;
import com.gzmelife.app.tools.ImgLoader_2;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CookFoodBeanAdapter extends BaseAdapter implements OnClickListener {
	private Context context;

	private List<CoonFoodMenuBean> list;

	private LayoutInflater inflater;

	private String flagState;

	public CookFoodBeanAdapter(Context context, List<CoonFoodMenuBean> list,
			String flagState) {
		super();
		this.context = context;
		this.list = list;
		this.flagState = flagState;
		System.out.println("list>>>======"+list.size());
		inflater = LayoutInflater.from(this.context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size() % 2 == 0 ? list.size() / 2 : list.size() / 2 + 1;
		// return list == null ? 0 : list.size();
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
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		if (null == convertView) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_cookfood, null);
			viewHolder.tv_name = (TextView) convertView
					.findViewById(R.id.tv_name);
			viewHolder.tv_num = (TextView) convertView
					.findViewById(R.id.tv_num);
			viewHolder.iv_food = (ImageView) convertView
					.findViewById(R.id.iv_food);
			viewHolder.item_layout1 = (LinearLayout) convertView
					.findViewById(R.id.item_layout1);

			viewHolder.tv_name2 = (TextView) convertView
					.findViewById(R.id.tv_name2);
			viewHolder.tv_num2 = (TextView) convertView
					.findViewById(R.id.tv_num2);
			viewHolder.iv_food2 = (ImageView) convertView
					.findViewById(R.id.iv_food2);
			// viewHolder.reading = (TextView) convertView
			// .findViewById(R.id.reading);
			viewHolder.item_layout2 = (LinearLayout) convertView
					.findViewById(R.id.item_layout2);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		String item1 = "";
		String item2 = "";
		CoonFoodMenuBean bean1 = null;
		CoonFoodMenuBean bean2 = null;
		if (position * 2 + 1 < list.size()) {
			bean1 = list.get(position * 2);
			bean2 = list.get(position * 2 + 1);
			if (bean1 != null) {
				final String id = bean1.getId();
				viewHolder.tv_name.setText(bean1.getName());
				viewHolder.tv_num.setText(bean1.getClickNumber() + "次阅读");
				if (bean1.getLogoPath().equals("")) {
					viewHolder.iv_food
							.setImageResource(R.drawable.icon_pms_file);
				}
				new ImgLoader_2(context).showPic(bean1.getLogoPath(),
						viewHolder.iv_food);
				viewHolder.item_layout1
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(context,
										NetCookBookDetailActivity.class);
								// intent.putExtra("category",
								// categoryFirstBeanList.get(position));
								String text = String.valueOf(viewHolder.tv_num
										.getText());
								String[] str = text.split("次阅读");
								int n = Integer.valueOf(str[0]);
								if (KappAppliction.getLiLogin() == 1) {
									n++;
								}
								viewHolder.tv_num.setText(String.valueOf(n
										+ "次阅读"));
								intent.putExtra("flagState", flagState);
								intent.putExtra("menuBookId", id);
								context.startActivity(intent);

							}
						});
				viewHolder.item_layout1.setTag(bean1);
			}
			// if (bean1 == null) {
			// viewHolder.item_layout1.setVisibility(View.GONE);
			// }
			if (bean2 != null) {
				// if (!bean2.getId().equals("")
				// && !bean2.getClickNumber().equals("")
				// && !bean2.getLogoPath().equals("")
				// && !bean2.getName().equals("")) {
				final String id = bean2.getId();
				viewHolder.tv_name2.setText(bean2.getName());
				viewHolder.tv_num2.setText(bean2.getClickNumber() + "次阅读");
				viewHolder.iv_food2
						.setBackgroundResource(R.drawable.shape_top_corner_no_bottom_line);
				viewHolder.item_layout2
						.setBackgroundResource(R.drawable.shape_top_corner_no_bottom_line);
				if (bean2.getLogoPath().equals("")) {
					viewHolder.iv_food2
							.setBackgroundResource(R.drawable.shape_top_corner_no_bottom_line);
					viewHolder.iv_food2
							.setImageResource(R.drawable.icon_pms_file);
				}
				new ImgLoader_2(context).showPic(bean2.getLogoPath(),
						viewHolder.iv_food2);
				// ImageLoader.getInstance().displayImage(bean2.getLogoPath(),
				// viewHolder.iv_food2,
				// ImageLoaderUtils_circle.MyDisplayImageOptions());
				viewHolder.item_layout2
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(context,
										NetCookBookDetailActivity.class);
								// intent.putExtra("category",
								// categoryFirstBeanList.get(position));
								String text = String.valueOf(viewHolder.tv_num2
										.getText());
								String[] str = text.split("次阅读");
								int n = Integer.valueOf(str[0]);
								if (KappAppliction.getLiLogin() == 1) {

									n++;
								}

								viewHolder.tv_num2.setText(String.valueOf(n
										+ "次阅读"));
								intent.putExtra("flagState", flagState);
								intent.putExtra("menuBookId", id);
								context.startActivity(intent);
							}
						});
				// }

				viewHolder.item_layout2.setTag(bean2);
			}

		} else if (position * 2 + 1 == list.size()) {
			bean1 = list.get(position * 2);
			bean2 = null;
			if (bean1 != null) {
				final int num = 0;
				final String id = bean1.getId();
				String clickNumber = bean1.getClickNumber();
				viewHolder.tv_name.setText(bean1.getName());
				viewHolder.tv_num.setText(clickNumber + "次阅读");
				if (bean1.getLogoPath().equals("")) {
					viewHolder.iv_food
							.setImageResource(R.drawable.icon_pms_file);
				}
				new ImgLoader_2(context).showPic(bean1.getLogoPath(),
						viewHolder.iv_food);
				viewHolder.item_layout1
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(context,
										NetCookBookDetailActivity.class);

								// intent.putExtra("category",
								// categoryFirstBeanList.get(position));
								String text = String.valueOf(viewHolder.tv_num
										.getText());
								String[] str = text.split("次阅读");
								int n = Integer.valueOf(str[0]);
								if (KappAppliction.getLiLogin() == 1) {

									n++;
								}
								viewHolder.tv_num.setText(String.valueOf(n
										+ "次阅读"));
								intent.putExtra("flagState", flagState);
								intent.putExtra("menuBookId", id);
								context.startActivity(intent);

								// context.startActivityForResult(intent,10010);
							}
						});
				viewHolder.item_layout1.setTag(bean1);
			}

		}

		return convertView;
	}

	@Override
	public void onClick(View v) {
		// Toast.makeText(context, v.getTag().toString(),
		// Toast.LENGTH_LONG).show();
		// Intent intent = new Intent(context,
		// NetCookBookDetailActivity.class);
		// // intent.putExtra("category",
		// // categoryFirstBeanList.get(position));
		// intent.putExtra("position", v.get);
		// // intent.putExtra("menuBookId",
		// // categoryCoonFoodMenuBeanList.get(position - 1).getId());
		// context.startActivity(intent);
	}

	public static class ViewHolder {
		TextView tv_name;
		ImageView iv_food;
		TextView tv_num;
		LinearLayout item_layout1;

		LinearLayout item_layout2;
		TextView tv_name2;
		ImageView iv_food2;
		TextView tv_num2, reading;
	}

}
