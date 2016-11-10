package com.gzmelife.app.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CancelledException;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.CookBookStepAdapter;
import com.gzmelife.app.adapter.LvCommentAdapter;
import com.gzmelife.app.adapter.LvfoodSearchAdapter;
import com.gzmelife.app.adapter.FoodMangerSearchAdapter;
import com.gzmelife.app.bean.CookBookMenuBookBean;
import com.gzmelife.app.bean.CookBookMenuBookCommentsBean;
import com.gzmelife.app.bean.CookBookMenuBookStepsBean;
import com.gzmelife.app.bean.LocalFoodMaterialLevelOne;
import com.gzmelife.app.bean.LocalFoodMaterialLevelThree;
import com.gzmelife.app.bean.SearchMenuBookBean;
import com.gzmelife.app.bean.SerachFoodBean;
import com.gzmelife.app.dao.FoodMaterialDAO;
import com.gzmelife.app.tools.ImgLoader;
import com.gzmelife.app.tools.KappUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

@ContentView(R.layout.activity_good_my_food_search)
public class MyFoodMangerSearchActivity extends BaseActivity {
	@ViewInject(R.id.lv_food)
	ListView lv_food;

	@ViewInject(R.id.et_search)
	EditText et_search;

	@ViewInject(R.id.tv_title)
	TextView tv_title;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	@ViewInject(R.id.iv_sousuo)
	ImageView iv_sousuo;

	private List<SerachFoodBean> serachFoodBeanList = new ArrayList<SerachFoodBean>();
	
	private List<String> selectedList = new ArrayList<String>(); // 选中了的食材的名字
	private FoodMangerSearchAdapter lvFoodSearchAdapter;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		initView();

	}

	// 加载布局
	private void initView() {
		tv_title.setText("搜索");
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("我的食材");
		lv_food = (ListView) findViewById(R.id.lv_food);
		et_search = (EditText) findViewById(R.id.et_search);

		et_search.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				String searchContent = et_search.getText().toString().trim();
				et_search.setText("");
				if (arg1 == EditorInfo.IME_ACTION_SEARCH
						&& !searchContent.equals("")) {
					searchMenuBook(searchContent);
				}

				return false;
			}
		});
		iv_sousuo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String searchContent = et_search.getText().toString().trim();
				if ("".equals(searchContent)) {
					KappUtils.showToast(MyFoodMangerSearchActivity.this, "请输入您要查询的菜名");
				}else {
					searchMenuBook(searchContent);
				}
			}
		});
		
		lv_food.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(serachFoodBeanList.size()!=0||serachFoodBeanList!=null){
					serachFoodBeanList.remove(position);
					lvFoodSearchAdapter.notifyDataSetChanged();
				}
				return false;
			}
		});
		lv_food.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				LocalFoodMaterialLevelOne bean1 = new LocalFoodMaterialLevelOne();
				bean1.setName(serachFoodBeanList.get(position).getC_name());
					SerachFoodBean bean2 = new SerachFoodBean();
					bean2.setC_id(FoodMaterialDAO
							.saveLocalFoodMaterialLevelOne(bean1));
					bean2.setName(serachFoodBeanList.get(position).getName());
					FoodMaterialDAO.saveLocalFoodMaterialLevelThree(bean2);
				KappUtils.showToast(context, "食材添加成功");
			}
		});
	}

	private void searchMenuBook(String text) {
		// 搜索
		showDlg();
		RequestParams params = new RequestParams(
				UrlInterface.URL_SERACHFOODSTORE);
		params.addBodyParameter("foodStoreName", text);

		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				closeDlg();
				Gson gson = new Gson();
				JSONObject obj;
				try {
					obj = new JSONObject(result.trim());
					// 菜谱
				
					serachFoodBeanList = gson.fromJson(
							obj.getJSONObject("data").getJSONArray("foodStores")
									.toString(),
							new TypeToken<List<SerachFoodBean>>() {
							}.getType());
					if (serachFoodBeanList.size()==0) {
						KappUtils.showToast(MyFoodMangerSearchActivity.this, "搜索不到结果哦");
					}else {
						lvFoodSearchAdapter = new FoodMangerSearchAdapter(context,
								serachFoodBeanList);
						
						lv_food.setAdapter(lvFoodSearchAdapter);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				// TODO Auto-generated method stub
				closeDlg();
			}

			@Override
			public void onCancelled(CancelledException cex) {
				// TODO Auto-generated method stub
				closeDlg();
			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub
				closeDlg();
			}

		});

	}

}
