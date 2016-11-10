package com.gzmelife.app.dao;

import java.util.ArrayList;
import java.util.List;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.bean.LocalFoodMaterialLevelOne;
import com.gzmelife.app.bean.LocalFoodMaterialLevelThree;
import com.gzmelife.app.bean.SerachFoodBean;
import com.gzmelife.app.tools.MyLog;

public class FoodMaterialDAO {
	/** 保存本地食材分类，先查询数据库内是否存在，不存在时才进行保存.返回分类ID */
	public static int saveLocalFoodMaterialLevelOne(
			LocalFoodMaterialLevelOne bean) {
		int categoryId = getCategoryId(bean.getName());
		if (categoryId == -1) {
			try {
				KappAppliction.db.save(bean);
			} catch (DbException e) {
				e.printStackTrace();
			}
			categoryId = getCategoryId(bean.getName());
		}
		return categoryId;
	}

	/** 保存本地食材，返回ID。先查询数据库内是否存在，若存在返回-1 */
	public static int saveLocalFoodMaterialLevelThree(
			LocalFoodMaterialLevelThree bean) {
		int categoryId = getFoodMaterialId(bean);
		if (categoryId == -1) {
			try {
				KappAppliction.db.save(bean);
			} catch (DbException e) {
				e.printStackTrace();
			}
			categoryId = getFoodMaterialId(bean);
			return categoryId;
		} else {
			return -1;
		}
	}

	/** 保存本地食材，返回ID。先查询数据库内是否存在，若存在返回-1 */
	public static int saveLocalFoodMaterialLevelThree(SerachFoodBean bean) {
		int categoryId = getFoodMaterialId(bean);
		if (categoryId == -1) {
			try {
				KappAppliction.db.save(bean);
			} catch (DbException e) {
				e.printStackTrace();
			}
			categoryId = getFoodMaterialId(bean);
			return categoryId;
		} else {
			return -1;
		}
	}

	/** 删除本地食材 */
	public static void deleteLocalFoodMaterialLevelThree(List<String> idList) {
		if (idList == null || idList.size() == 0) {
			MyLog.d("要删除的食材id参数有误");
			return;
		}
		for (String str : idList) {
			try {
				KappAppliction.db.deleteById(LocalFoodMaterialLevelThree.class,
						Integer.parseInt(str));
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
	}

	/** 删除本地食材 */
	public static void deleteLocalFoodMaterialLevelThreeById(List idList) {
		if (idList == null || idList.size() == 0) {
			MyLog.d("要删除的食材id参数有误");
			return;
		}
		for (int i = 0; i < idList.size(); i++) {
			try {
				KappAppliction.db.deleteById(LocalFoodMaterialLevelThree.class,
						idList.get(i));
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
		// for (int str : idList) {
		// try {
		// KappAppliction.db.deleteById(LocalFoodMaterialLevelThree.class,
		// Integer.parseInt(str));
		// } catch (DbException e) {
		// e.printStackTrace();
		// }
		// }
	}

	public static List<LocalFoodMaterialLevelOne> getAllCategory() {
		try {
			List<LocalFoodMaterialLevelOne> list = KappAppliction.db
					.findAll(LocalFoodMaterialLevelOne.class);
			return list;
		} catch (DbException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<LocalFoodMaterialLevelThree> getAllFoodMaterialByCategoryId(
			int categoryId) {
		List<LocalFoodMaterialLevelThree> list = new ArrayList<LocalFoodMaterialLevelThree>();
		try {
 			List<DbModel> dbModels = KappAppliction.db
					.findDbModelAll(new SqlInfo(
							"SELECT * FROM localFoodMaterialLevelThree WHERE pid="
									+ categoryId));
			if (dbModels != null) {
				for (DbModel model : dbModels) {
					LocalFoodMaterialLevelThree bean = new LocalFoodMaterialLevelThree();
					bean.setId(model.getInt("id"));
					bean.setPid(model.getInt("pid"));
					bean.setName(model.getString("name"));
					list.add(bean);
				}
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static ArrayList<String> getAllFoodMaterialByCategoryIds(
			int categoryId) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			List<DbModel> dbModels = KappAppliction.db
					.findDbModelAll(new SqlInfo(
							"SELECT * FROM localFoodMaterialLevelThree WHERE pid="
									+ categoryId));
			if (dbModels != null) {
				for (DbModel model : dbModels) {
					LocalFoodMaterialLevelThree bean = new LocalFoodMaterialLevelThree();
					bean.setId(model.getInt("id"));
					bean.setPid(model.getInt("pid"));
					bean.setName(model.getString("name"));
					list.add(model.getString("name"));
				}
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return list;
	}

	/** 获取本地食材的分类ID，不存在返回-1 */
	public static int getCategoryId(String name) {
		try {
			DbModel bean = KappAppliction.db.findDbModelFirst(new SqlInfo(
					"SELECT id FROM localFoodMaterialLevelOne WHERE name='"
							+ name + "'"));
			if (bean != null) {
				return bean.getInt("id");
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/** 获取本地食材的分类ID，不存在返回-1 */
	public static int getCategoryIds(String name) {
		try {
			DbModel bean = KappAppliction.db.findDbModelFirst(new SqlInfo(
					"SELECT id FROM localFoodMaterialLevelThree WHERE name='"
							+ name + "'"));
			if (bean != null) {
				return bean.getInt("id");
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/** 获取本地食材的ID，不存在返回-1 */
	public static int getFoodMaterialId(LocalFoodMaterialLevelThree bean) {
		try {
			DbModel dbModel = KappAppliction.db.findDbModelFirst(new SqlInfo(
					"SELECT id FROM localFoodMaterialLevelThree WHERE name='"
							+ bean.getName() + "' and pid=" + bean.getPid()));
			if (dbModel != null) {
				return dbModel.getInt("id");
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/** 获取本地食材的ID，不存在返回-1 */
	public static int getFoodMaterialId(SerachFoodBean bean) {
		try {
			DbModel dbModel = KappAppliction.db.findDbModelFirst(new SqlInfo(
					"SELECT id FROM localFoodMaterialLevelThree WHERE name='"
							+ bean.getName() + "' and pid=" + bean.getC_id()));
			if (dbModel != null) {
				return dbModel.getInt("id");
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/** 获取本地食材的ID，不存在返回-1 */
	public static int getFoodMaterialIds(SerachFoodBean bean) {
		try {
			DbModel dbModel = KappAppliction.db.findDbModelFirst(new SqlInfo(
					"SELECT id FROM localFoodMaterialLevelThree WHERE name='"
							+ bean.getName() + "' and pid=" + bean.getC_id()));
			if (dbModel != null) {
				return dbModel.getInt("id");
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return -1;
	}

}
