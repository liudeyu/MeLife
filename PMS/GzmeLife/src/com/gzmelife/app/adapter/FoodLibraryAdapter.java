package com.gzmelife.app.adapter;

import java.util.List;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.bean.LocalFoodMaterialLevelOne;
import com.gzmelife.app.bean.LocalFoodMaterialLevelThree;
import com.gzmelife.app.views.GridViewForScrollView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FoodLibraryAdapter extends BaseAdapter{
	Context context;
	List<LocalFoodMaterialLevelOne> foodLibrarys;
	ViewHolder viewHolder;
	
	public FoodLibraryAdapter(Context context,
			List<LocalFoodMaterialLevelOne> foodLibrarys) {
		super();
		this.context = context;
		this.foodLibrarys = foodLibrarys;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return foodLibrarys.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressLint("WrongViewCast")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView =LayoutInflater.from(context).inflate(R.layout.item_food_name, null);
			viewHolder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
			viewHolder.gv_food= (GridViewForScrollView) convertView.findViewById(R.id.tv_name);
			convertView.setTag(viewHolder);
		}else {
			viewHolder=(ViewHolder) convertView.getTag();
		}
		DbManager dbManager= KappAppliction.myApplication.db;
		List<LocalFoodMaterialLevelThree> foods = null;
		try {
			foods = dbManager.selector(LocalFoodMaterialLevelThree.class).where("pid", "=", foodLibrarys.get(position).getId()).findAll();
		} catch (DbException e) {
			e.printStackTrace();
		}
		viewHolder.gv_food.setAdapter(new GvFoodAdapter(foods, context));
		viewHolder.tv_name.setText(foodLibrarys.get(position).getName());
		return convertView;
	}
	
	class ViewHolder{
		TextView tv_name;
		GridViewForScrollView gv_food;
		
	}
		
	

}
