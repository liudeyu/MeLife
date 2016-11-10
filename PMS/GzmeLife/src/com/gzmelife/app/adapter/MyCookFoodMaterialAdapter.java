package com.gzmelife.app.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.gzmelife.app.R;
import com.gzmelife.app.bean.LocalFoodMaterialLevelOne;
import com.gzmelife.app.bean.LocalFoodMaterialLevelThree;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.views.GridViewForScrollView;

/**
 * 二级列表
 */
@SuppressLint("InflateParams")
public class MyCookFoodMaterialAdapter extends BaseExpandableListAdapter {

	private String TAG="MyCookFoodMaterialAdapter";
	private List<LocalFoodMaterialLevelOne> parentList;
	private Map<String, List<LocalFoodMaterialLevelThree>> map;

	private boolean isClickable;

	private ArrayList<String> selectedList = new ArrayList<String>();
	private ArrayList<String> selectedListUID = new ArrayList<String>();
	private Context context;
	private LayoutInflater inflater;
	private int flag;

	public MyCookFoodMaterialAdapter(Context context,
			List<LocalFoodMaterialLevelOne> parentList,
			Map<String, List<LocalFoodMaterialLevelThree>> map, int flag) {
		this.context = context;
		this.parentList = parentList;
		this.map = map;
		this.flag = flag;

		inflater = LayoutInflater.from(context);
	}

	public void setClickable(boolean isClickable) {
		this.isClickable = isClickable;
	}

	@Override
	public int getGroupCount() {
		return parentList == null ? 0 : parentList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return parentList == null ? 0 : 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return parentList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		String key = parentList.get(groupPosition).getId() + "";
		return map.get(key).get(childPosition);
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
	public View getGroupView(int groupPosition, boolean isExpanded,
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

		LocalFoodMaterialLevelOne bean = (LocalFoodMaterialLevelOne) getGroup(groupPosition);
		viewHolderParent.tv_name.setText(bean.getName());
		return convertView;
	}

	class ViewHolderParent {
		private TextView tv_name;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		MyLog.d("--->child:" + groupPosition + "," + childPosition);
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

		String key = parentList.get(groupPosition).getId() + "";
		Log.i(TAG, "修改步骤UID  name:"+parentList.get(groupPosition).getName()+"--：id："+parentList.get(groupPosition).getId());
		List<LocalFoodMaterialLevelThree> list = map.get(key);
		MyLog.d("--->list.size:" + (list == null ? 0 : list.size()));
		MyCookFoodMaterialChildAdapter adapter = new MyCookFoodMaterialChildAdapter(
				context, list, flag,
				new MyCookFoodMaterialChildAdapter.OnReceiver() {
					@Override
					public void onCheckChange(String name,String id, boolean isChecked) {
						if (isChecked) {
							selectedList.add(name);
							selectedListUID.add(id);
							Log.i(TAG, "选择:"+selectedList.get(0)); 
						} else {
							selectedList.remove(name);
							selectedListUID.remove(id);
						}
					}
				});
		adapter.setClickable(isClickable);
		viewHolderChild.gv_data.setAdapter(adapter);
		return convertView;
	}

	public void initSelectedIds() {
		selectedList = new ArrayList<String>();
	}

	public List<String> getSelectedIds() {
		return selectedList;
	}
	public List<String> getSelectedIdsUid() {
		return selectedListUID;
	}
	
	class ViewHolderChild {
		private GridViewForScrollView gv_data;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void setData(
			List<LocalFoodMaterialLevelOne> myFoodMaterialCategoryList,
			Map<String, List<LocalFoodMaterialLevelThree>> myFoodMaterialMap) {
		this.parentList = myFoodMaterialCategoryList;
		this.map = myFoodMaterialMap;
		this.notifyDataSetChanged();
	}
}
