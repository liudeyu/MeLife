package com.gzmelife.app.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.gzmelife.app.R;
import com.gzmelife.app.bean.CategoryFirstBean;
import com.gzmelife.app.bean.CategorySecondBean;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.views.GridViewForScrollView;

/**
 */
@SuppressLint("InflateParams")
public class GoodFoodMaterialAdapter extends BaseExpandableListAdapter {

	private List<CategoryFirstBean> categoryFirstBeanList;

	private OnReceiver onReceiver;

	private Context context;
	private LayoutInflater inflater;

	public GoodFoodMaterialAdapter(Context context,
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
			convertView = inflater.inflate(R.layout.item_lv_food_material_1,
					null);
			viewHolderParent.tv_name = (TextView) convertView
					.findViewById(R.id.tv_name);
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
