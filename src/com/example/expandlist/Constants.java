package com.example.expandlist;

import android.content.Context;
import android.os.Environment;

/**
 * 
 * @author JianFeng<br>
 * <br>
 *         share preference key name自定格式 <b>SP_KEY_xxx</b><br>
 *         api url格式 <b>URL_xxx</b><br>
 *         Intent Extra data 格式 <b>EXTRA_xxx</b><br>
 * 
 */
public class Constants {
	/**
	 * share preference root name
	 */
	public static final String SP_PREFS_NAME = "sp_prefs_name";
	public static final String SP_KEY_LANGUAGE = "sp_key_language";
	/**
	 * Intent Extra key name
	 */
	public static final String EXTRA_TAB_ACTIVITY_ID = "extra_tab_aid";
	public static final String EXTRA_SHOP_ID = "extra_shop_id";
	public static final String EXTRA_SHOP_NAME = "extra_shop_name";
	public static final String EXTRA_SHOP_ADDRESS = "extra_shop_address";
	public static final String EXTRA_BUILDING_ID = "extra_build_id";
	public static final String EXTRA_IMAGE_PATH = "extra_image_path";
	public static final String EXTRA_WHATS_ON_DATA="whats_on_data";
	public static final String EXTRA_ALL_ANGLE="all_angle";
	public static final String EXTRA_LANDMARK_FOCUS_DATA="land_mark_foucs";
	public static final String EXTRA_LANDMARK_FOCUS_IDS="landmark_focus_ids";
	public static final String EXTRA_IMAGE_NAME = "extra_image_name";
	public static final String EXTRA_OPERATION_SUCCESSFUL = "extra_operation_successful";
	public static final String EXTRA_LOCATE_BUILDING_IN_MAP = "extra_locate_building_in_map";

	/**
	 * other constants
	 */
	public static final boolean LOG_TO_FILE = false;
	public static final String PATH_LOCAL_IMAGE_LIBRARY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hkland_imgs";
	/**
	 * db field name
	 */
	public static final String DB_SHOP_TYPE_SHOP = "shop";
	public static final String DB_SHOP_TYPE_RESTAURANT = "restaurant";

	/*-- API URL --*/
	public static final String URL_DOMAIN = "61.239.248.194";
	public static final String URL_API_PREFIX = "http://" + URL_DOMAIN + "/hkland/api/";
	public static final String URL_UPLOAD_PREFIX = "http://" + URL_DOMAIN + "/static/hkland/upload/";
	public static final String URL_PHOTO_PREFIX = "http://" + URL_DOMAIN + "/static/hkland/upload/";

	public static final String URL_CHECK_VERSION = URL_API_PREFIX + "checkVersion.do";
	public static final String URL_ADD_N_UPDATE_DEVICE_TOKEN = URL_API_PREFIX + "addNUpdateDeviceToken.do";
	public static final String URL_UPGRADE_DATABASE = URL_UPLOAD_PREFIX + "db/hkland.sql";
	public static final String URL_CAMPAIN_PHOTO_ADD = URL_API_PREFIX + "campaignPhotoAdd.do";
	public static final String URL_CAMPAIN_PHOTO_LIKE = URL_API_PREFIX + "campaignPhotoLike.do";
	public static final String URL_CAMPAIN_PHOTO_LIST = URL_API_PREFIX + "campaignPhotoList.do";
	/*-- End API URL --*/
	
	/*-- Activity Request & Result Code --*/
	public static final int ACTIVITY_REQUEST_FINISH_CROP = 100;
	public static final int ACTIVITY_RESULT_ERROR = -100;
	/*-- End Activity Result Code --*/

	

	

		

}
