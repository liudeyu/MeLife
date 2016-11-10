package com.gzmelife.app;

public class UrlInterface {
	public static final String URL_HOST_IP = "http://112.74.202.110:8080";
	public static final String URL_HOST = URL_HOST_IP+"/pms/";

	public static final String  URL_FILEUPLOADBYBYTE = URL_HOST + "upload/fileUploadByByte.aspf";
	/**
	 * 上传菜谱
	 */
	public static final String  URL_PMSFILEUPLOADBYBYTE = URL_HOST + "upload/pmsFileUploadByByte.aspf";
	/**
	 * 上传临时菜谱
	 */
	public static final String  URL_PMSFILETEMPPORARY = URL_HOST + "upload/pmsFileTemporary.aspf" ;
	
	/**
	 * 检查临时菜谱
	 */
	public static final String  URL_PMSFILECHECK = URL_HOST + "upload/pmsFileCheck.aspf" ;
	
	/** Base64上传 */
	public static final String  URL_FILEUPLOAD = URL_HOST + "upload/fileUpload.aspf";
	
	/** 标准食材库，一级二级分类 */
	public static String URL_FOOD_MATERIAL_LIB = URL_HOST + "foodstore/findTowFoodStoreCategory.aspf";
	
	/** 标准食材库，三级分类 */												  
	public static String URL_FOOD_CATEGORY_ID = URL_HOST + "foodstore/getFoodStoreByCategoryId.aspf";
	
	/**
	 * 得到验证吗
	 */
	public static String URL_GETVALIDATECODE = URL_HOST + "appuser/getValidateCode.aspf";
	
	/**
	 * 修改密码
	 */
	public static String URL_RESETPASSWORD = URL_HOST + "/appuser/resetPassword.aspf";
	
	/**
	 * 注册
	 */
	public static String URL_REGISTER = URL_HOST + "appuser/register.aspf";
	
	

	/** 系统消息  page */
	public static String URL_SYSTEM_MSG = URL_HOST + "message/findSystemMessage.aspf";
	
	/** 获取更新PMS固件 */
	public static String URL_UPDATE_PMS = URL_HOST + "message/getEquipmentUpdate.aspf";
	
	/** 用户协议/关于我们/常见错误详情/系统消息    详情  
	 *  id
	 *  type 1=用户协议
				2=关于我们
				3=常见错误
				4=系统消息
	 *  
	 */
	public static String URL_MSG_DETAIL = URL_HOST + "webview.jsp";
	
	/** 查看我上传的菜谱  userId */
	public static String URL_MY_UPLOAD_COOKBOOK = URL_HOST + "menubook/findMyMenuBook.aspf";
	
	/** 下载菜谱 */
	public static String URL_DOWN_LOAD_MEUN_BOOK_FILE = URL_HOST + "menubook/downloadMenuBookFile.aspf";
	
	/** 查询用户信息  userId */
	public static String URL_USERINFO_DETAIL = URL_HOST + "appuser/findUserDetails.aspf";
	/** 更新用户信息  */
	public static String URL_UPDATE_USERINFO = URL_HOST + "appuser/updateUserInfo.aspf";
	
	/** 查看用户设备  userId */
	public static String URL_MY_DEVICES = URL_HOST + "appuser/findUserEquipments.aspf";
	/** 添加设备  userId、number */
	public static String URL_ADD_DEVICES = URL_HOST + "appuser/saveEquipments.aspf";
	/** 删除设备  equipmentId */
	public static String URL_DELETE_DEVICES = URL_HOST + "appuser/deleteEquipment.aspf";
	
	/** 12.电磁炉错误 /常见问题  userId */
	public static String URL_FAQ = URL_HOST + "error/findError.aspf";
	
	/** 查询菜谱分类  pType (1=菜系、2=口味) */
	public static String URL_FOOD_TASTE_CATEGORY = URL_HOST + "menubook/findMenuCategory.aspf";
	/**
	 * 登陆
	 */
	public static String URL_LOGIN  = URL_HOST+"/appuser/login.aspf";
	
	/**
	 * 查询食材分类和食材关系
	 */
	public static String URL_FOODSTORE  = URL_HOST+"foodstore/findfoodStoreCategoryLibrary.aspf";
	
	/**
	 * 查询食材库
	 */
	public static String URL_SERACHFOODSTORE  = URL_HOST+"foodstore/searchFoodStoreLibrary.aspf";
	
	/**
	 * 查询广告
	 */
	public static String URL_FINDAD  = URL_HOST+"menubook/findAd.aspf";
	
	/**
	 * 查询最新最热、最热、推荐菜谱
	 */
	public static String URL_FINDMENUBOOK  = URL_HOST+"menubook/findMenuBook.aspf";
	
	
	/**
	 * 查询最新最热、最热菜谱
	 */
	public static String URL_FINDMENUBOOKBYFOODSTORE  = URL_HOST+"foodstore/findMenuBookByFoodStore.aspf";
	
	/**
	 * 查询菜谱详情
	 */
	public static String URL_FINDMENUBOOKDETAILS  = URL_HOST+"menubook/findMenuBookDetails.aspf";
	
	/**
	 * 收藏菜谱
	 */
	public static String URL_SAVECOLLECTION  = URL_HOST+"menubook/saveCollection.aspf";
	
	/**
	 * 下载菜谱
	 */
	public static String URL_DOWNLOADMENUBOOKFILE  = URL_HOST+"menubook/downloadMenuBookFile.aspf";
	
	/**
	 * 查询收藏的菜谱
	 */
	public static String URL_FINDCOLLECTIONMENUBOOK  = URL_HOST+"menubook/findCollectionMenuBook.aspf";
	
	/**
	 * 删除收藏的菜谱
	 */
	public static String URL_DELETECOLLECTION  = URL_HOST+"menubook/deleteCollection.aspf";
	
	/**
	 * 取消收藏的菜谱
	 */
	public static String URL_CANCELCOLLECTION  = URL_HOST+"menubook/cancelCollection.aspf";
	
	/**
	 * 置顶菜谱
	 */
	public static String URL_TOPMENUBOOK  = URL_HOST+"menubook/topMenuBook.aspf";
	
	/**
	 * 菜谱搜索
	 */
	public static String URL_SEARCHMENUBOOK  = URL_HOST+"menubook/searchMenuBook.aspf";
	
	/**
	 * 查询二级菜谱分类
	 */
	public static String URL_FINDSECONDCATEGORY  = URL_HOST+"menubook/findSecondCategory.aspf";
	
	/**
	 * 查询一级二级菜谱分类关系
	 */
	public static String URL_FINDTWOMENUCATEGORY  = URL_HOST+"menubook/findTowMenuCategory.aspf";
	/**
	 * 菜谱评论
	 */
	public static String URL_CONTENT  = URL_HOST+"menubook/saveComments.aspf";
}
