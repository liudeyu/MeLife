package com.gzmelife.app.dao;

import java.util.List;

import org.xutils.ex.DbException;

import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.bean.DeviceNameAndIPBean;
import com.gzmelife.app.tools.MyLog;

public class DevicesDAO {
	/** 保存，先查询数据库内是否存在，不存在时才进行保存 */
	public void save(DeviceNameAndIPBean bean) {
		List<DeviceNameAndIPBean> allList = getAllDevices();
		boolean isExist = false;
		if (allList != null) {
			for (DeviceNameAndIPBean b : allList) {
				if (b.isSame(bean)) { // 如果全都一样，不用保存了，中断方法
					isExist = true;
					break;
				}
				if (b.isSameNameAndWifiName(bean)) { // 如果PMS名字和wifi名字一样，ip不一样，则删除旧的，下面会增加新的
					try {
						KappAppliction.db.deleteById(DeviceNameAndIPBean.class, b.getId());
					} catch (DbException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (!isExist) {
			MyLog.i("添加新设备信息到数据库");
			try {
				KappAppliction.db.save(bean);
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
//		MyLog.i("当前数据库内PMS信息：");
//		allList = getAllDevices();
//		if (allList != null && allList.size() > 0) {
//			for (DeviceNameAndIPBean b : allList) {
//				MyLog.i(b.toString());
//			}
//		}
	}
	
	public List<DeviceNameAndIPBean> getAllDevices() {
		try {
			return KappAppliction.db.findAll(DeviceNameAndIPBean.class);
		} catch (DbException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean deleteDeviceById(int id) {
		try {
			KappAppliction.db.deleteById(DeviceNameAndIPBean.class, id);
			return true;
		} catch (DbException e) {
			e.printStackTrace();
		}
		return false;
	}
}
