package com.gzmelife.app.tools;
import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;

public class pop_dlg implements OnClickListener {
public interface dialog{
	public View initLayout(pop_dlg pop,LayoutInflater flater);
	public void onclick(pop_dlg pop,View v);
}
private dialog mdialog;
private Context mcontext;
private View mview;
private LayoutInflater inflater;
private AlertDialog myDialog;
public pop_dlg(Context context,dialog dialog) {
	// TODO Auto-generated constructor stub
	mcontext=context;
	mdialog=dialog;
	inflater=LayoutInflater.from(mcontext);
	mview=mdialog.initLayout(this,inflater);

	myDialog=new AlertDialog.Builder(mcontext).create();
	myDialog.setCanceledOnTouchOutside(true);
	myDialog.show();
	Window window = myDialog.getWindow();
//	window.setWindowAnimations(R.style.bottomAnimation); // 从底部弹出，往底部消失
	window.setGravity(Gravity.CENTER);
	LayoutParams layoutParams = window.getAttributes();
	layoutParams.width = (int) (mcontext.getResources().getDisplayMetrics().widthPixels*0.7);
	window.setAttributes(layoutParams);
	myDialog.setContentView(mview);
}
public void pop(){

}
@Override
public void onClick(View arg0) {
	// TODO Auto-generated method stub
	mdialog.onclick(this,arg0);
}
public void dismiss(){
	myDialog.dismiss();
}
}
