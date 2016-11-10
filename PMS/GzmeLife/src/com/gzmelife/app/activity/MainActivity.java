package com.gzmelife.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.fragment.PrivateFragment;
import com.gzmelife.app.fragment.DeviceFragment;
import com.gzmelife.app.fragment.GoodFoodFragment;
import com.gzmelife.app.fragment.PersonalCenterFragment;
import com.gzmelife.app.tools.MyLog;

public class MainActivity extends BaseActivity implements
		OnCheckedChangeListener,OnClickListener {
	public FrameLayout fl_content;
	public RadioGroup rg_mian;
	public RadioButton rb_device;
	public RadioButton rb_cookBook;
	public RadioButton rb_goodFood;
	public RadioButton rb_personalCenter;
	public PrivateFragment cookBookFragment;
	public DeviceFragment deviceFragment;
	public GoodFoodFragment goodFoodFragment;
	// CopyOfGoodFoodFragment copyOfGoodFoodFragment;

	public PersonalCenterFragment personalCenterFragment;
	public FragmentManager fragmentManager;
	public FragmentTransaction ft;
	String TAG = "MainActivity";

	String state;
	
	//点击本地菜谱1确认点击0未点击
	public static int localFlag=0;

	private int flag = 0;
	
	public String flagState="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (getIntent().getStringExtra("flag") != null) {
			state = getIntent().getStringExtra("flag");
		} else {
			state = "";
		}
		flag=2;
		if(getIntent().getStringExtra("flagState") != null){
			flagState = getIntent().getStringExtra("flagState");
		}

		if (getIntent().getStringExtra("state") != null) {
			state = getIntent().getStringExtra("state");
		} else {
			state = "";
		}
	
		
		initView();
	}
	
	private void initView(){
		fl_content = (FrameLayout) findViewById(R.id.fl_content);
		rg_mian = (RadioGroup) findViewById(R.id.rg_mian);
		rb_device = (RadioButton) findViewById(R.id.rb_device);
		rb_cookBook = (RadioButton) findViewById(R.id.rb_cookBook);
		rb_goodFood = (RadioButton) findViewById(R.id.rb_goodFood);
		rb_personalCenter = (RadioButton) findViewById(R.id.rb_personalCenter);
		rg_mian.setOnCheckedChangeListener(this);
		fragmentManager = getSupportFragmentManager();
		if (state.equals(110)) {
			// deviceFragment = new DeviceFragment();
			// ft.add(R.id.fl_content, deviceFragment);
			rb_device.setChecked(true);
		}
		if (state.equals("2")) {
			// deviceFragment = new DeviceFragment();
			// ft.add(R.id.fl_content, deviceFragment);
			rb_cookBook.setChecked(true);
		}
		
		if(flag==2){
			rb_goodFood.setChecked(true);
		}
		
		if (flagState.equals("1")) {
			goodFoodFragment.flag1=1;
		} else if (flagState.equals("2")) {
			goodFoodFragment.flag1=2;
		} else if (flagState.equals("3")) {
			goodFoodFragment.flag1=3;
		}
	}
	
	private void initview() {
        cookBookFragment = new PrivateFragment();
        android.app.FragmentManager fm = getFragmentManager();
        android.app.FragmentTransaction ft = fm.beginTransaction();
        this.ft.add(R.id.fl_content, cookBookFragment);
        ft.commit();
    }

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (group.getId() == R.id.rg_mian) {
			hide();
			ft = fragmentManager.beginTransaction();
			switch (checkedId) {
			case R.id.rb_device:
//				deviceFragment = new DeviceFragment();
//				ft.add(R.id.fl_content, deviceFragment);
				if (deviceFragment == null) {
						deviceFragment = new DeviceFragment();
				ft.add(R.id.fl_content, deviceFragment);
				} else {
					ft.show(deviceFragment);
				}

				flag = 0;
				break;
			case R.id.rb_cookBook:
				// 食谱
//				if (state.equals("local")) {
//					cookBookFragment = new PrivateFragment();
//					ft.add(R.id.fl_content, cookBookFragment);
////					if (cookBookFragment == null) {
////						
////					} else {
////						ft.show(cookBookFragment);
////					}
//				}
//				cookBookFragment = new PrivateFragment();
//				ft.add(R.id.fl_content, cookBookFragment);
				if (cookBookFragment == null) {
					cookBookFragment = new PrivateFragment();
					ft.add(R.id.fl_content, cookBookFragment);
				} else {
					ft.show(cookBookFragment);
				}
				flag = 1;
				break;
			case R.id.rb_goodFood:
//				goodFoodFragment = new GoodFoodFragment();
//				ft.add(R.id.fl_content, goodFoodFragment);
				if (goodFoodFragment == null) {
					goodFoodFragment = new GoodFoodFragment();
					ft.add(R.id.fl_content, goodFoodFragment);
				} else {
					ft.show(goodFoodFragment);
					goodFoodFragment.reloadUI();
				}
				flag = 2;
				break;
			case R.id.rb_personalCenter:
				if (KappAppliction.myApplication.getUser() == null) {
					switch (flag) {
					case 0:
						rb_device.setChecked(true);
						break;
					case 1:
						rb_cookBook.setChecked(true);
						break;
					case 2:
						rb_goodFood.setChecked(true);
						break;
					}
					flag += 4;
					startActivity(new Intent(MainActivity.this,
							LoginActivity.class));
					return;
				} else {
//					MyLog.d("--->1");
//					personalCenterFragment = new PersonalCenterFragment();
//					MyLog.d("--->3");
//					ft.add(R.id.fl_content, personalCenterFragment);
					if (personalCenterFragment == null) {
						MyLog.d("--->2");
						personalCenterFragment = new PersonalCenterFragment();
						MyLog.d("--->3");
						ft.add(R.id.fl_content, personalCenterFragment);
						MyLog.d("--->4");
					} else {
						MyLog.d("--->5");
						ft.show(personalCenterFragment);
						MyLog.d("--->6");
					}
					flag = 3;
				}
				break;
			default:
				break;
			}
			ft.commit();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyLog.d("--->flag=" + flag + ","
				+ (KappAppliction.myApplication.getUser() == null));
		if (flag >= 4) {
			if (KappAppliction.myApplication.getUser() != null) {
				rb_personalCenter.setChecked(true);
			} else {
				flag -= 4;
			}
		} else if (flag == 3 && KappAppliction.myApplication.getUser() == null) {
			rb_goodFood.setChecked(true);
		} else if (flag == 0) {
			rb_device.setChecked(true);
		} else if(flag==1){
			rb_cookBook.setChecked(true);
		} else if(localFlag==1){
			rb_cookBook.setChecked(true);
		}
	}

	
	private void hide() {
		ft = fragmentManager.beginTransaction();
		if (cookBookFragment != null) {
			ft.hide(cookBookFragment);
		}
		if (deviceFragment != null) {
			ft.hide(deviceFragment);
		}
		if (goodFoodFragment != null) {
			ft.hide(goodFoodFragment);
		}
		if (personalCenterFragment != null) {
			ft.hide(personalCenterFragment);
		}
		ft.commit();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	//fargment跳转fargment方法
	public void setVis(int localF) {
		// TODO Auto-generated method stub
		
		if(localF==1){
			rb_cookBook.setChecked(true);
		}else if(localF==2){
			initView();
			rb_cookBook.setChecked(true);
			localFlag=2;
//			cookBookFragment = new PrivateFragment();
//			ft.add(R.id.fl_content, cookBookFragment);
			if (cookBookFragment == null) {
				cookBookFragment = new PrivateFragment();
				ft.add(R.id.fl_content, cookBookFragment);
			} else {
				ft.show(cookBookFragment);
			}
			cookBookFragment.flagSstate=2;
		}
	}
	
	//fargment跳转fargment方法
		public void setView(String localF) {
			// TODO Auto-generated method stub
			initView();
			if(localF.equals("1")){
				goodFoodFragment.flag1=1;
			}else if(localF.equals("2")){
				goodFoodFragment.flag1=2;
			}else if(localF.equals("3")){
				goodFoodFragment.flag1=3;
			}
		}

}
