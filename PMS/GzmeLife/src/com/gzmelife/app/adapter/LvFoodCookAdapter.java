package com.gzmelife.app.adapter;

import java.util.ArrayList;
import java.util.List;

import com.gzmelife.app.R;
import com.gzmelife.app.bean.CookBookBean;
import com.gzmelife.app.tools.File2PMS;
import com.gzmelife.app.tools.ImgLoader;
import com.gzmelife.app.tools.PmsFile;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/** 私厨Adapter（ListView） */
public class LvFoodCookAdapter extends BaseAdapter {
	private String TAG="LvFoodCookAdapter";
	ViewHolder viewHolder;
	Context context;
	List<CookBookBean> cookBookBeans;

	public LvFoodCookAdapter(Context context) {
		this.context = context;
		cookBookBeans = new ArrayList<CookBookBean>();
	}

	public void setData(List<CookBookBean> cookBookBeans) {
		this.cookBookBeans = cookBookBeans;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cookBookBeans.size();
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_foodscooks, null);

			viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
			viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
//			viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tv_name.setText(cookBookBeans.get(position).getName());
		PmsFile pms;
		File2PMS file=new File2PMS();
		pms=file.getfile(cookBookBeans.get(position).getPath());
		Bitmap bimap=pms.bitmap;
//		new ImgLoader(context).showPic(cookBookBeans.get(position).getPath(), viewHolder.iv_icon,R.drawable.icon_pms_file);
		if(bimap!=null&&!bimap.isRecycled()){
			bimap=viewHolder.iv_icon.getDrawingCache();
			if(bimap!=null&&!bimap.isRecycled()){
				bimap.recycle();				
			}
			bimap=pms.bitmap;
			BitmapDrawable bd=new BitmapDrawable(bimap);
			viewHolder.iv_icon.setImageDrawable(bd);
		}else{
			viewHolder.iv_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_pms_file));			
		}
		Log.i(TAG,"icon的图标->"+cookBookBeans.get(position).getPath());
		//		viewHolder.tv_time.setText(cookBookBeans.get(position).getTime());

		return convertView;
	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_time;
	}

}
