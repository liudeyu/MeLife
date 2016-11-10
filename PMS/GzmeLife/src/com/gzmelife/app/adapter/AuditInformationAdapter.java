package com.gzmelife.app.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.gzmelife.app.R;
import com.gzmelife.app.bean.MyUploadCookbookBean;

/**
 */
@SuppressLint("InflateParams")
public class AuditInformationAdapter extends BaseAdapter {

    private Context context;
    private List<MyUploadCookbookBean> list;
    
    private LayoutInflater inflater;

    public AuditInformationAdapter(Context context, List<MyUploadCookbookBean> list) {
        super();
        this.context = context;
        this.list = list;

        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
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
        final ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_lv_audit_information, null);
            viewHolder.tv_result = (TextView) convertView.findViewById(R.id.tv_result);
            viewHolder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            viewHolder.tv_tip = (TextView) convertView.findViewById(R.id.tv_tip);
            viewHolder.layout_gap = convertView.findViewById(R.id.layout_gap);
            viewHolder.cb_tip = (CheckBox) convertView.findViewById(R.id.cb_tip);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MyUploadCookbookBean bean = list.get(position);
        String str = "菜谱" + bean.getName();
        if (bean.getAuditStatus().equals("1") || bean.getAuditStatus().equals("2")) {
        	if (bean.getAuditStatus().equals("1")) {
        		str += "审核通过";
        	} else {
        		str += "待审核";
        	}
        	viewHolder.cb_tip.setVisibility(View.GONE);
        	
        	viewHolder.layout_gap.setVisibility(View.GONE);
    		viewHolder.tv_tip.setVisibility(View.GONE);
    		
    		convertView.setOnClickListener(null);
        } else {
        	str += "审核不通过";
        	viewHolder.cb_tip.setVisibility(View.VISIBLE);
        	
        	convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					list.get(position).setShowTip(!viewHolder.cb_tip.isChecked());
					viewHolder.cb_tip.setChecked(list.get(position).isShowTip());
					if (list.get(position).isShowTip()) {
						viewHolder.layout_gap.setVisibility(View.VISIBLE);
		        		viewHolder.tv_tip.setVisibility(View.VISIBLE);
					} else {
						viewHolder.layout_gap.setVisibility(View.GONE);
		        		viewHolder.tv_tip.setVisibility(View.GONE);
					}
				}
			});
        	
        	if (TextUtils.isEmpty(bean.getRemark())) {
        		viewHolder.tv_tip.setText("提示: ");
        	} else {
        		viewHolder.tv_tip.setText("提示: " + bean.getRemark());
        	}
        	
        	if (bean.isShowTip()) {
        		viewHolder.layout_gap.setVisibility(View.VISIBLE);
        		viewHolder.tv_tip.setVisibility(View.VISIBLE);
        	} else {
        		viewHolder.layout_gap.setVisibility(View.GONE);
        		viewHolder.tv_tip.setVisibility(View.GONE);
        	}
        }
        viewHolder.tv_result.setText(str);
        if (TextUtils.isEmpty(bean.getAuditTime())) { // 未审核，显示提交时间
        	viewHolder.tv_date.setText(bean.getCreateTime().substring(0, 10));
        } else {
        	viewHolder.tv_date.setText(bean.getAuditTime().substring(0, 10));
        }
        
        return convertView;
    }
    
    public static class ViewHolder {
        TextView tv_result;
        TextView tv_date;
        TextView tv_tip;
        View layout_gap;
        CheckBox cb_tip;
    }
}
