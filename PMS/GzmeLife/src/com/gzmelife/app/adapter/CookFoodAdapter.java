package com.gzmelife.app.adapter;

import java.util.ArrayList;
import java.util.List;

import com.gzmelife.app.R;
import com.gzmelife.app.activity.NetCookBookDetailActivity;
import com.gzmelife.app.adapter.CookingStyleAdapter.ViewHolder;
import com.gzmelife.app.adapter.GalleryAdapter.OnRecyclerViewItemClickListener;
import com.gzmelife.app.adapter.MyFoodMaterialChildAdapter.OnReceiver;
import com.gzmelife.app.bean.CookingStyleBean;
import com.gzmelife.app.bean.CoonFoodMenuBean;
import com.gzmelife.app.bean.LocalFoodMaterialLevelThree;
import com.gzmelife.app.tools.DensityUtil;
import com.gzmelife.app.tools.ImgLoader;
import com.gzmelife.app.views.CircleImageView;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//继承自 RecyclerView.Adapter
public class CookFoodAdapter extends BaseAdapter {
	Context context;

	private List<CoonFoodMenuBean> list = new ArrayList<CoonFoodMenuBean>();
	private boolean isClickable;

	public OnRecyclerViewItemClickListener mOnItemClickListener = null;
	private int flag;
	private ImageView selectView;
	private LayoutInflater inflater;
	private int position;

	/** flag 0:返回选择的id，1：返回选择的名字 */
	public CookFoodAdapter(Context context, List<CoonFoodMenuBean> list) {
		super();
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		// if(list == null){
		// return 0;
		// }else{
		// return list.size()/2 + list.size()%2;
		// }
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
		// TODO Auto-generated method stub
		// View v = LayoutInflater.from(context).inflate(R.layout.item_cookfood,
		// null);

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
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		CoonFoodMenuBean bean = list.get(position);
		viewHolder.tv_name.setText(bean.getName());
		viewHolder.tv_num.setText(bean.getClickNumber() + "次阅读");
		new ImgLoader(context).showPic(bean.getLogoPath(), viewHolder.iv_food);

		return convertView;
	}

	public static class ViewHolder {
		TextView tv_name;
		ImageView iv_food;
		TextView tv_num;
	}

	// public interface OnItemClickLitener
	// {
	// void onItemClick(View view, int position);
	// }
	//
	// private OnItemClickLitener mOnItemClickLitener;
	// public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
	// {
	// this.mOnItemClickLitener = mOnItemClickLitener;
	// }

	// @Override
	// public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
	// // View v = LayoutInflater.from(context).inflate(R.layout.item_cookfood,
	// // null);
	// View view = inflater.inflate(R.layout.item_cookfood, viewGroup, false);
	// // View v =
	// LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cookfood,
	// null);
	// // LinearLayout.LayoutParams lp = new
	// LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
	// ViewGroup.LayoutParams.WRAP_CONTENT);
	// // v.setLayoutParams(lp);
	//
	// ViewHolder viewHolder = new ViewHolder(view);
	//
	// viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
	// viewHolder.tv_num = (TextView) view.findViewById(R.id.tv_num);
	// viewHolder.imageView = (ImageView) view.findViewById(R.id.iv_food);
	//
	// return viewHolder;
	// }

	// // 将数据绑定到子View，会自动复用View
	// @Override
	// public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
	// // this.position=i;
	// viewHolder.tv_name.setText(list.get(i).getName());
	// viewHolder.tv_num.setText(list.get(i).getClickNumber());
	// // viewHolder.imageView.setText(list.get(i).getLogoPath());
	// new ImgLoader(context).showPic(list.get(i).getLogoPath(),
	// viewHolder.imageView);
	//
	//
	// // 注意这里使用getTag方法获取数据
	// // mOnItemClickListener.onItemClick(viewHolder.imageView, i);
	// // selectView = viewHolder.imageView;
	// // selectView.setSelected(true);
	//
	// //如果设置了回调，则设置点击事件
	// if (mOnItemClickLitener != null)
	// {
	// viewHolder.itemView.setOnClickListener(new OnClickListener()
	// {
	// @Override
	// public void onClick(View v)
	// {
	// mOnItemClickLitener.onItemClick(viewHolder.itemView, i);
	// }
	// });
	//
	// }
	//
	// }
	//
	// // RecyclerView显示数据条数
	// @Override
	// public int getItemCount() {
	// return list.size();
	// }

	// // 自定义的ViewHolder,减少findViewById调用次数
	// class ViewHolder extends RecyclerView.ViewHolder {
	//
	//
	// // 在布局中找到所含有的UI组件
	// public ViewHolder(View itemView) {
	// super(itemView);
	//
	// }
	// TextView tv_name;
	// ImageView imageView;
	// TextView tv_num;
	// }

	// public static interface OnRecyclerViewItemClickListener {
	// void onItemClick(View view, int position);
	// }

	// public interface OnReceiver {
	// void onClick(int position);
	// }
	//
	//
	//
	// public void setClickable(boolean isClickable) {
	// this.isClickable = isClickable;
	// }

}
