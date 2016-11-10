package com.gzmelife.app.adapter;

import java.util.ArrayList;

import com.gzmelife.app.R;
import com.gzmelife.app.bean.LocalFoodMaterialLevelThree;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class FoodAdapter extends BaseAdapter {
	ViewHolder viewHolder;
	Context context;
	ArrayList<LocalFoodMaterialLevelThree> foodBeans;
	String TAG = "FoodAdapter";

	public FoodAdapter(Context context,
			ArrayList<LocalFoodMaterialLevelThree> foodBeans) {
		super();
		this.context = context;
		this.foodBeans = foodBeans;
	}

	@Override
	public int getCount() {

		return foodBeans.size();
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
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.layout_lv_food, null);
			viewHolder.tv_name = (TextView) convertView
					.findViewById(R.id.tv_name);
			viewHolder.et_name = (EditText) convertView
					.findViewById(R.id.et_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tv_name.setText(foodBeans.get(position).getName());
		if (!"".equals(foodBeans.get(position).getWeight())) {
			viewHolder.et_name
					.setText(foodBeans.get(position).getWeight() + "");
		}
		// viewHolder.et_name.setOnClickListener(l);
		// viewHolder.tv_name
		// .setOnFocusChangeListener(new OnFocusChangeListener() {
		//
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// EditText et = (EditText) v;
		// if(!hasFocus){
		//
		// System.out.println(">>>>============");
		// }
		// }
		//
		// });
		// viewHolder.et_name.setText(foodBeans.get(position).getWeight() + "");
		viewHolder.et_name.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				// if (foodBeans.get(position).getWeight() == 0) {
				// // viewHolder.et_name.setText("0");
				// // } else {
				// viewHolder.et_name.setText("");
				// }
				if (viewHolder.et_name.getText().toString().equals("0")) {
					viewHolder.et_name.setText("");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
				if (s.equals("0")) {
					viewHolder.et_name.setText("");
				}else{
					 String string = viewHolder.et_name.getText().toString();
				}

			}
		});
		Log.i(TAG, String.valueOf(foodBeans.get(position).getId()));
		return convertView;
	}

	class ViewHolder {
		TextView tv_name;
		EditText et_name;
	}

}
