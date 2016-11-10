package com.gzmelife.app.adapter;

import java.util.List;

import com.gzmelife.app.R;
import com.gzmelife.app.bean.FindTowMenuCategoryFirstBean;
import com.gzmelife.app.tools.ImgLoader;
import com.gzmelife.app.views.CircleImageView;
import com.gzmelife.app.views.GridViewForScrollView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LvAllFoodClassAdapter extends BaseAdapter {
	Context context;
	ViewHolder holder;
	String TAG="LvAllFoodClassAdapter";
	OnItemClickListener onItemClickListener;
	private List<FindTowMenuCategoryFirstBean> categoryFirstBeanList ;
	
	public LvAllFoodClassAdapter(Context context,List<FindTowMenuCategoryFirstBean> categoryFirstBeanList) {
		this.context=context;
		this.categoryFirstBeanList=categoryFirstBeanList;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return categoryFirstBeanList == null ? 0 : categoryFirstBeanList.size();
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
	public View getView( final int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			holder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.item_lv_all_food_class, null);
			holder.iv_head=(CircleImageView) convertView.findViewById(R.id.iv_head);
			holder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
			holder.iv_arrow= (ImageView) convertView.findViewById(R.id.iv_arrow);
			holder.gv_item= (GridViewForScrollView) convertView.findViewById(R.id.gv_item);
			holder.rl_layout= (RelativeLayout) convertView.findViewById(R.id.rl_layout);
			convertView.setTag(holder);
		}else {
			holder=(ViewHolder) convertView.getTag();
		}
		
		holder.tv_name.setText(categoryFirstBeanList.get(position).getMfcName());
		 new ImgLoader(context).showPic(categoryFirstBeanList.get(position).getMfcLogoPath(), holder.iv_head, R.drawable.icon_pms_file);
		
		holder.gv_item.setTouch(true);
		holder.gv_item.setAdapter(new GvItemAdapter( context,categoryFirstBeanList.get(position).getSecondCategorieList()));
		holder.rl_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				GridView gv_item = (GridView) lv.getChildAt(position)
//						.findViewById(R.id.gv_item);
//				ImageView iv_arrow = (ImageView) lv.getChildAt(position)
//						.findViewById(R.id.iv_arrow);
//				if (holder.gv_item.getVisibility() == View.VISIBLE) {
//					holder.gv_item.setVisibility(View.GONE);
//					holder.iv_arrow.setSelected(false);
//				} else {
//					holder.gv_item.setVisibility(View.VISIBLE);
//					holder.iv_arrow.setSelected(true);
//				}
				/*Log.i(TAG,position+ "==================");
				if(holder.gv_item.getVisibility()==View.VISIBLE){
					holder.gv_item.setVisibility(View.GONE);
					holder.iv_arrow.setSelected(false);
				}else {
					holder.gv_item.setVisibility(View.VISIBLE);
					holder.iv_arrow.setSelected(true);
				}*/
				onItemClickListener.onItemClick(position);
			}
		});
		return convertView;
	}
	
	
	public void setOnItemsClickListener(OnItemClickListener onItemClickListener){
		this.onItemClickListener=onItemClickListener;
	}
	
	 public interface OnItemClickListener{
		public void onItemClick(int position);
	}
	static class ViewHolder{
		CircleImageView  iv_head;
		TextView tv_name ;
		ImageView iv_arrow;
		GridViewForScrollView gv_item;
		RelativeLayout rl_layout;
	}
	
	
	
	
	
	
}
