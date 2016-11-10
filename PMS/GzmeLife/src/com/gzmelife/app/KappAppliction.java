package com.gzmelife.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.DbManager.DaoConfig;
import org.xutils.x;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.ShareSDK;

import com.gzmelife.app.activity.share_Test;
import com.gzmelife.app.bean.CityModel;
import com.gzmelife.app.bean.DistrictModel;
import com.gzmelife.app.bean.ProvinceModel;
import com.gzmelife.app.bean.UserInfoBean;
import com.gzmelife.app.tools.SharedPreferenceUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class KappAppliction extends Application {
	String TAG = "KappAppliction";

	public UserInfoBean user;
	public static KappAppliction myApplication;
	/**
	 * 电磁炉连接状态：1成功 2失败
	 */
	public static int  state=0;

	/**
	 * 登录状态1成功 2失败
	 */
	private static int  liLogin;
	

	public static int getLiLogin() {
		return SharedPreferenceUtil.getLoginState(myApplication);
	}

	public static void setLiLogin(int liLogin) {
		SharedPreferenceUtil.setLoginState(myApplication, liLogin);
	}

	/**
	 * 所有省
	 */
	private ProvinceModel[] mProvinceDatas;
	/**
	 * key - 省 value - 市
	 */
	private Map<String, CityModel[]> mCitisDatasMap = new HashMap<String, CityModel[]>();
	/**
	 * key - 市 values - 区
	 */
	private Map<String, DistrictModel[]> mDistrictDatasMap = new HashMap<String, DistrictModel[]>();

	public static DbManager db;
	
	public static ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	public void onCreate() {
		super.onCreate();
		x.Ext.init(this);
		x.Ext.setDebug(true);

		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
		ShareSDK.initSDK(this);
		share_Test.initApp();
		DaoConfig daoConfig = new DbManager.DaoConfig().setDbName("pms_db") // 设置数据库名称
				// .setDbDir(new File(DB_PATH)) //数据库路径
				.setDbVersion(1) // 数据库版本
				;
		db = x.getDb(daoConfig);

		myApplication = this;
		initProvinceDatas();
		
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		
		initImageLoader(this);
	}
	
	private void initImageLoader(Context context) {

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .threadPriority(Thread.NORM_PRIORITY - 1)
                .denyCacheImageMultipleSizesInMemory()
                // .memoryCache(new UsingFreqLimitedMemoryCache(4*1024*1024))
                .memoryCacheExtraOptions(480, 800)
                .discCacheExtraOptions(480, 800, null)
                // max width, max height
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .denyCacheImageMultipleSizesInMemory()// 自动缩放
                .writeDebugLogs() // Remove for release app

                // Initialize ImageLoader with configuration.


                // 设置内存缓存 默认为一个当前应用可用内存的1/8大小的LruMemoryCache
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))

                // 设置内存缓存的最大大小 默认为一个当前应用可用内存的1/8
                .memoryCacheSize(2 * 1024 * 1024)

                // 设置硬盘缓存的最大大小
                .discCacheSize(50 * 1024 * 1024)

                // 设置内存缓存最大大小占当前应用可用内存的百分比 默认为一个当前应用可用内存的1/8
                .memoryCacheSizePercentage(13)
                .build();
        ImageLoader.getInstance().init(config);
    }

	/**
	 * 解析省市区的XML数据
	 */
	private void initProvinceDatas() {
		List<ProvinceModel> provinceList = new ArrayList<ProvinceModel>();
		AssetManager asset = getAssets();
		try {
			InputStream input = asset.open("area_new.txt");
			String json = Inputstr2Str_Reader(input, null);
			JSONObject obj = new JSONObject(json);
			JSONArray dataList = obj.getJSONObject("data").getJSONArray(
					"provinces");
			for (int i = 0; i < dataList.length(); i++) {
				ProvinceModel province = new ProvinceModel();
				JSONObject proData = dataList.getJSONObject(i);
				JSONArray cityList = proData.getJSONArray("cities");
				List<CityModel> cityDataList = new ArrayList<CityModel>();
				if (cityList.length() == 0) {
					CityModel city = new CityModel();
					List<DistrictModel> districtDataList = new ArrayList<DistrictModel>();
					DistrictModel district = new DistrictModel();
					district.setName("");
					district.setId("");
					districtDataList.add(district);
					city.setName("");
					city.setId("");
					city.setDistrictList(districtDataList);
					cityDataList.add(city);
				} else {
					for (int j = 0; j < cityList.length(); j++) {
						CityModel city = new CityModel();
						JSONObject cityData = cityList.getJSONObject(j);
						JSONArray districtList = cityData
								.getJSONArray("districts");
						List<DistrictModel> districtDataList = new ArrayList<DistrictModel>();
						if (districtList.length() == 0) {
							DistrictModel district = new DistrictModel();
							district.setName("");
							district.setId("");
							districtDataList.add(district);
						} else {
							for (int k = 0; k < districtList.length(); k++) {
								DistrictModel district = new DistrictModel();
								JSONObject districtData = districtList
										.getJSONObject(k);
								district.setName(districtData
										.getString("districtName"));
								district.setId(districtData
										.getString("districtId"));
								districtDataList.add(district);
							}
						}
						city.setName(cityData.getString("cityName"));
						city.setId(cityData.getString("cityId"));
						city.setDistrictList(districtDataList);
						cityDataList.add(city);
					}
				}
				province.setName(proData.getString("provinceName"));
				province.setId(proData.getString("provinceId"));
				province.setCityList(cityDataList);
				provinceList.add(province);
			}
			// */
			mProvinceDatas = new ProvinceModel[provinceList.size()];
			for (int i = 0; i < provinceList.size(); i++) {
				// 遍历所有省的数据
				mProvinceDatas[i] = provinceList.get(i);
				List<CityModel> cityList = provinceList.get(i).getCityList();
				CityModel[] cityNames = new CityModel[cityList.size()];
				for (int j = 0; j < cityList.size(); j++) {
					// 遍历省下面的所有市的数据
					cityNames[j] = cityList.get(j);
					List<DistrictModel> districtList = cityList.get(j)
							.getDistrictList();
					DistrictModel[] distrinctNameArray = new DistrictModel[districtList
					                                                       .size()];
					DistrictModel[] distrinctArray = new DistrictModel[districtList
					                                                   .size()];
					for (int k = 0; k < districtList.size(); k++) {
						// 遍历市下面所有区/县的数据
						DistrictModel districtModel = new DistrictModel(
								districtList.get(k).getId(), districtList
								.get(k).getName());
						// 区/县对于的邮编，保存到mZipcodeDatasMap
						distrinctArray[k] = districtModel;
						distrinctNameArray[k] = districtModel;
					}
					// 市-区/县的数据，保存到mDistrictDatasMap
					mDistrictDatasMap.put(cityNames[j].getName(), distrinctNameArray);
				}
				// 省-市的数据，保存到mCitisDatasMap
				mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			// MyLog.i(MyLog.TAG_I_INFO, (mProvinceDatas == null) + ".");
			// if (mProvinceDatas != null) {
			// for (int i = 0; i < mProvinceDatas.length; i++) {
			// MyLog.i(MyLog.TAG_I_INFO, "i=" + mProvinceDatas[i]);
			// }
			// }
		}
	}

	private String Inputstr2Str_Reader(InputStream in, String encode) {
		String str = "";
		try {
			if (encode == null || encode.equals("")) {
				// 默认以utf-8形式
				encode = "utf-8";
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					in, encode));
			StringBuffer sb = new StringBuffer();

			while ((str = reader.readLine()) != null) {
				sb.append(str).append("\n");
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}

	public ProvinceModel[] getmProvinceDatas() {
		return mProvinceDatas;
	}

	public Map<String, CityModel[]> getmCitisDatasMap() {
		return mCitisDatasMap;
	}

	public Map<String, DistrictModel[]> getmDistrictDatasMap() {
		return mDistrictDatasMap;
	}

	public UserInfoBean getUser() {
		UserInfoBean u = SharedPreferenceUtil.getUser(this);
		if(u==null)
			return user;
		else {
			return u;
		}
	}

	public void setUser(UserInfoBean user) {
		this.user = user;
	}

}
