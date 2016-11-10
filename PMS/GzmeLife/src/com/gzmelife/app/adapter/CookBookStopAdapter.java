package com.gzmelife.app.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gzmelife.app.R;
import com.gzmelife.app.activity.CookBookDetailActivity;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.tools.MyLogger;
import com.gzmelife.app.hhd.TimeNode;

/**
 * 菜谱详情Adapter
 */
public class CookBookStopAdapter extends BaseAdapter {

	MyLogger HHDLog = MyLogger.HHDLog();

	private int currentPosition = 0;
//	public TreeSet<TimeNodeFood> lvFoodTreeSet = new TreeSet<>();
	public ArrayList<String> timeNodeFoods = new ArrayList<>();
	Context context;
	ViewHolder viewHolder;
	List<TimeNode> listTimeNode;

	boolean isEdt;

	public CookBookStopAdapter(Context context, List<TimeNode> listTimeNode) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.listTimeNode = listTimeNode;
	}

	public boolean isEdt() {
		return isEdt;
	}

	public void setEdt(boolean isEdt) {
		this.isEdt = isEdt;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listTimeNode == null ? 0 : listTimeNode.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		MyLog.d("position=" + position);
		final TimeNode timeNode = listTimeNode.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_cook_book_step, null);
			viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			viewHolder.iv_add = (ImageView) convertView.findViewById(R.id.iv_add);
			viewHolder.iv_edt = (ImageView) convertView.findViewById(R.id.iv_edt);
			viewHolder.v_line = convertView.findViewById(R.id.v_line);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (isEdt) {
			viewHolder.iv_add.setVisibility(View.VISIBLE);
			viewHolder.iv_edt.setVisibility(View.VISIBLE);
		} else {
			viewHolder.iv_add.setVisibility(View.GONE);
			viewHolder.iv_edt.setVisibility(View.GONE);
		}

		/** 插入一个步骤 */
		viewHolder.iv_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int startTime;
				int endTime;

				if (position == 0) {
					startTime = 0;
				} else {
					startTime = listTimeNode.get(position - 1).times;
				}

				if (position == listTimeNode.size() - 1) {
					endTime = startTime + (5 * 60);
				} else {
					endTime = listTimeNode.get(position + 1).times;
				}

				((CookBookDetailActivity) context).edtStep(timeNode, startTime, endTime, false, position);
			}
		});

		/** 编辑选中步骤 */
		viewHolder.iv_edt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int startTime;
				int endTime;
				if (position == 0) {
					startTime = 0;
				} else {
					startTime = listTimeNode.get(position - 1).times;
				}
				if (position == listTimeNode.size() - 1) {
					endTime = startTime + (5 * 60);
				} else {
					endTime = listTimeNode.get(position + 1).times;
				}
				((CookBookDetailActivity) context).edtStep(timeNode, startTime, endTime, true, position);
			}
		});

		viewHolder.tv_name.setText(String.valueOf(position + 1));
		SimpleDateFormat format = new SimpleDateFormat("mm分ss秒");
		viewHolder.tv_time.setText(format.format(new Date(timeNode.times * 1000)));

		//20161013获取食材名称和重量{
//		StringBuffer foodNamesAndWgts = new StringBuffer();
		//20161013获取食材名称和重量}
		StringBuffer foodBuffer = new StringBuffer();//20161013用StringBuffer拼接菜谱名和重量
		for (int i = 0; i < timeNode.FoodNames.length; i++) {
			if (!TextUtils.isEmpty(timeNode.FoodNames[i])) {
				foodBuffer.append(timeNode.FoodNames[i]);
				foodBuffer.append(" ");

				foodBuffer.append(timeNode.FoodWgts[i]);
				foodBuffer.append(" g");
				foodBuffer.append("；");

				//20161013获取食材名称和重量{
//					foodNamesAndWgts.append("{");
//					foodNamesAndWgts.append(timeNode.FoodNames[i]);
//					foodNamesAndWgts.append(",");
//					foodNamesAndWgts.append(timeNode.FoodWgts[i]);
//					foodNamesAndWgts.append("}");
				//20161013获取食材名称和重量}
			}
		}

		// if(listTimeNode.size()-1==position&&position!=0){
		// viewHolder.v_line.setVisibility(View.GONE);
		// }
		viewHolder.tv_content.setText("步骤描述：" + timeNode.Tips + "\n\n重量变化：" + timeNode.wetsTemp+ " g\n\n食材：" + foodBuffer.toString());
//		timeNodeFoods.add(timeNodeFood);

		//20161013获取食材名称和重量{
//		System.out.println(foodNamesAndWgts.toString());
//		timeNodeFoods.add(foodNamesAndWgts.toString());
		//20161013获取食材名称和重量}

		//viewHolder.tv_content.setText();

//		String name = foodBuffer.toString();

//		String result = name.substring(0, name.indexOf(";"));
//		System.out.println("result=====" + name.replace(";", "g;"));
		return convertView;
	}


	class ViewHolder {
		TextView tv_name, tv_time, tv_content;//时间节点序号，当前步骤的时间节点，内容：步骤描述\n重量变化\n食材
		ImageView iv_add, iv_edt;//添加按钮，编辑按钮
		View v_line;//时间轴竖线
	}

}
