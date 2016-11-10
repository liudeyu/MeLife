package com.gzmelife.app.adapter;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.gzmelife.app.R;
import com.gzmelife.app.bean.CategorySecondBean;
import com.gzmelife.app.tools.DensityUtil;
import com.gzmelife.app.tools.MyLog;

public class GalleryAdapter extends
		RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
	private LayoutInflater mInflater;
	private List<CategorySecondBean> mDatas;

	private int width = 0; // 每一项的长度

	String TAG = "GalleryAdapter";

	private Context context;
	private TextView selectView;
	private int position;

	private OnRecyclerViewItemClickListener mOnItemClickListener = null;

	public GalleryAdapter(Context context, List<CategorySecondBean> datats,
			int position) {
		this.context = context;
		this.position = position;
		mInflater = LayoutInflater.from(context);
		mDatas = datats;
	}

	public GalleryAdapter(Context context) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public ViewHolder(View arg0) {
			super(arg0);
		}

		TextView mTxt;
	}

	@Override
	public int getItemCount() {
		return mDatas == null ? 0 : mDatas.size();
	}

	/**
	 * 创建ViewHolder
	 */
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
		if (width == 0) {
			if (getItemCount() >= 3) {
				width = DensityUtil.getWidth(context) / 3;
			} else {
				width = DensityUtil.getWidth(context) / getItemCount();
			}
		}

		View view = mInflater.inflate(R.layout.item_text, viewGroup, false);
		LayoutParams lp = view.getLayoutParams();
		lp.width = width;
		view.setLayoutParams(lp);
		ViewHolder viewHolder = new ViewHolder(view);
		viewHolder.mTxt = (TextView) view.findViewById(R.id.tv);

		return viewHolder;
	}

	/**
	 * 设置值
	 */
	@Override
	public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
		viewHolder.mTxt.setText(mDatas.get(i).getScName());
		MyLog.d("--->i=" + i + ",position=" + position);
		if (position == i) {
			viewHolder.mTxt.setSelected(true);
			if (mOnItemClickListener != null) {
				// 注意这里使用getTag方法获取数据
				mOnItemClickListener.onItemClick(viewHolder.mTxt, mDatas.get(i)
						.getScId(), i);
				selectView = viewHolder.mTxt;
			}

		} else {
			viewHolder.mTxt.setSelected(false);
			// if (mOnItemClickListener != null) {
			// // 注意这里使用getTag方法获取数据
			// mOnItemClickListener.onItemClick(viewHolder.mTxt, mDatas.get(i)
			// .getScId(), i);
			// selectView = viewHolder.mTxt;
			// selectView.setSelected(true);
			// }
		}
		if (mDatas.get(i).getScId() == position) {
			viewHolder.mTxt.setSelected(true);
		}
		viewHolder.mTxt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				position = i;
				Log.i(TAG, "点击了");
				// selectView.setSelected(false);
				selectView = viewHolder.mTxt;
				// selectView.setSelected(true);
				if (mOnItemClickListener != null) {
					// 注意这里使用getTag方法获取数据
					mOnItemClickListener.onItemClick(v,
							mDatas.get(i).getScId(), i);
					// selectView = (TextView) v;
					v.setSelected(true);
				}
			}
		});
	}

	public static interface OnRecyclerViewItemClickListener {
		void onItemClick(View view, int position, int pos);
	}

	public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
		this.mOnItemClickListener = listener;
	}

}
