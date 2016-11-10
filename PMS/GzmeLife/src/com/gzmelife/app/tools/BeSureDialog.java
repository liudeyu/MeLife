package com.gzmelife.app.tools;



import com.gzmelife.app.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


public class BeSureDialog extends Dialog implements View.OnClickListener {
    private Context mContext;

    private TextView tv_sure, tv_notsure, tv_tips_content;
    private OnSelected onSelected;
    private String text = "";


    public BeSureDialog(Context context) {
        super(context, R.style.DialogDefalut);
        this.mContext = context;

    }

    public BeSureDialog(Context context, String text) {
        super(context, R.style.DialogDefalut);
        this.mContext = context;
        this.text = text;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_sureselected);
        setCanceledOnTouchOutside(false);
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(p);

        initViews();
    }


    private void initViews() {
        tv_sure = (TextView) findViewById(R.id.tv_sure);
        tv_notsure = (TextView) findViewById(R.id.tv_notsure);
        tv_tips_content = (TextView) findViewById(R.id.tv_tips_content);

        tv_sure.setOnClickListener(this);
        tv_notsure.setOnClickListener(this);
        if(DataUtil.isnotnull(this.text)){
        tv_tips_content.setText(this.text);
        }
    }


    public void cancel(View v) {
        dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_sure:
                if (null != onSelected) {
                    onSelected.onSureSelected();
                }
                dismiss();
                break;

            case R.id.tv_notsure:
                if (null != onSelected) {
                    onSelected.onNotSureSelected();
                }
                dismiss();
                break;
        }
    }

    public void setOnSelected(OnSelected selected) {
        this.onSelected = selected;
    }

    public interface OnSelected {
        public void onSureSelected();

        public void onNotSureSelected();
    }

}
