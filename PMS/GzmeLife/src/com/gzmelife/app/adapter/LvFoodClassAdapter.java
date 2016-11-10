package com.gzmelife.app.adapter;

import java.util.List;

import com.gzmelife.app.R;
import com.gzmelife.app.activity.SecondaryFoodClassificationActivity;
import com.gzmelife.app.adapter.StandardFoodMaterialAdapter.OnReceiver;
import com.gzmelife.app.adapter.StandardFoodMaterialAdapter.ViewHolderChild;
import com.gzmelife.app.adapter.StandardFoodMaterialAdapter.ViewHolderParent;
import com.gzmelife.app.bean.CategoryFirstBean;
import com.gzmelife.app.bean.CategorySecondBean;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.views.GridViewForScrollView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * 一级目录
 * 
 * @author chenxiaoyan
 *
 */
public class LvFoodClassAdapter extends BaseExpandableListAdapter {

	private List<CategoryFirstBean> categoryFirstBeanList;

	private OnReceiver onReceiver;

	private Context context;
	private LayoutInflater inflater;
	String TAG = "LvFoodClassAdapter";
	ViewHolder viewHolder;

	public LvFoodClassAdapter(Context context,
			List<CategoryFirstBean> categoryFirstBeanList, OnReceiver onReceiver) {
		this.context = context;
		this.categoryFirstBeanList = categoryFirstBeanList;
		this.onReceiver = onReceiver;

		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getGroupCount() {
		return categoryFirstBeanList == null ? 0 : categoryFirstBeanList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return categoryFirstBeanList.get(groupPosition)
				.getSecondCategorieList() == null ? 0 : 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return categoryFirstBeanList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return categoryFirstBeanList.get(groupPosition)
				.getSecondCategorieList();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		ViewHolderParent viewHolderParent;
		if (convertView == null) {
			viewHolderParent = new ViewHolderParent();
			convertView = inflater.inflate(R.layout.item_food_library, null);
			// viewHolderParent.tv_name = (TextView)
			// convertView.findViewById(R.id.tv_name);
			convertView.setTag(viewHolderParent);
		} else {
			viewHolderParent = (ViewHolderParent) convertView.getTag();
		}

		CategoryFirstBean bean = (CategoryFirstBean) getGroup(groupPosition);
		viewHolderParent.tv_name.setText(bean.getFcName());

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onReceiver != null) {
					onReceiver.onClick(groupPosition, -1);
				}
			}
		});

		return convertView;
	}

	class ViewHolderParent {
		private TextView tv_name;
	}

	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	//
	// /*DbManager dbManager= KappAppliction.myApplication.db;
	// List<LocalFoodMaterialLevelThree> foods = null;
	// try {
	// foods =
	// dbManager.selector(LocalFoodMaterialLevelThree.class).where("pid", "=",
	// foodLibrarys.get(position).getId()).findAll();
	// } catch (DbException e) {
	// e.printStackTrace();
	// }*/
	// viewHolder.gv_food.setAdapter(new GvFoodClassAdapter(context));
	// /* viewHolder.tv_name.setText(foodLibrarys.get(position).getName());*/
	//
	// /*viewHolder.gv_food.setOnItemClickListener(new OnItemClickListener() {
	//
	// @Override
	// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
	// long arg3) {
	//
	// }
	// });*/
	// return convertView;
	// }

	class ViewHolder {
		TextView tv_name;
		GridViewForScrollView gv_food;

	}

	@SuppressWarnings("unchecked")
	@Override
	public View getChildView(final int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolderChild viewHolderChild;
		if (convertView == null) {
			viewHolderChild = new ViewHolderChild();
			convertView = inflater.inflate(R.layout.item_lv_food_material_2,
					null);
			viewHolderChild.gv_data = (GridViewForScrollView) convertView
					.findViewById(R.id.gv_data);
			convertView.setTag(viewHolderChild);
		} else {
			viewHolderChild = (ViewHolderChild) convertView.getTag();
		}

		List<CategorySecondBean> list = (List<CategorySecondBean>) getChild(
				groupPosition, childPosition);

		StandardFoodMaterialChildAdapter adapter = new StandardFoodMaterialChildAdapter(
				context, list,
				new StandardFoodMaterialChildAdapter.OnReceiver() {
					@Override
					public void onClick(int position) {
						MyLog.d("--->groupPosition=" + groupPosition
								+ ".position=" + position);
						if (onReceiver != null) {
							onReceiver.onClick(groupPosition, position);
						}
					}
				});
		viewHolderChild.gv_data.setAdapter(adapter);

		// viewHolderChild.gv_data.setOnItemClickListener(new
		// OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		//
		// }
		// });

		return convertView;
	}

	public interface OnReceiver {
		void onClick(int groupPosition, int position);
	}

	class ViewHolderChild {
		private GridViewForScrollView gv_data;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
