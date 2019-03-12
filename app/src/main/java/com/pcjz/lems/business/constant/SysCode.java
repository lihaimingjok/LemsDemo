package com.pcjz.lems.business.constant;

import com.pcjz.lems.R;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.context.AppContext;

/**
 * created by yezhengyu on 2017/4/11 09:46
 */
public class SysCode {
    public static final String SUCCESS = "0";
    //public static final String PROJECTPERIODID = "projectPeriodId";
    //public static final String PROJECTPERIODNAME = "projectPeriodName";

    public static final String PROJECTPERIODID = "projectPeriodId";
    public static final String PROJECTPERIODNAME = "projectPeriodName";

    //发送报验记录列表通知
    public static final String REGRESH_CHECK_RECORD_LIST = "refresh_check_record_list";
    //区域图片提交成功通知
    public static final String AREA_UPLOAD_IMAGES = "area_upload_images";
    //下载项目期数接口
    // public static final String OFFLINE_DOWNLOAD_URL = "http://172.16.12.150:81/quality/offline";
    public static final String OFFLINE_DOWNLOAD_URL = AppConfig.BASE_SERVER_URL + "quality/offline";
    //别名设置成功一次之后就不必再设置
    public static final String ALIAS_SETTING_SUCCESS = "alias_setting_success";
    //下载App的请求
    //public static final String APP_DOWNLOAD_URL = "http://172.16.12.150/static/";
    public static final String APP_DOWNLOAD_URL = AppConfig.BASE_SERVER_URL + "static/";
    //WiFi在线或离线切换发送通知
    public static final String STATE_WIFI_CHANGE = "state_wifi_change";
    //设置是否消息推送
    public static final String MESSAGE_STATUS = "message_status";
    //切换项目期数成功通知
    public static final String SWITCH_PERIOD1 = "switch_period1";
    public static final String SWITCH_PERIOD2 = "switch_period2";

    public static final String ACCEPTANCE_TYPE = "acceptance_type";
    public static final String ADD_ACCEPTANCE = "add_acceptance";
    public static final String ADD_NEW_ACCEPTANCE = "add_new_acceptance";
    public static final String TYPE_UNACCEPTANCE = "type_unacceptance";
    public static final String TYPE_ACCEPTANCE = "type_acceptance";
    public static final String TYPE_REACCEPTANCE = "type_reacceptance";

    public static final String TYPE_REGION_PERIOD = "1";
    public static final String TYPE_REGION_BUILD = "2";
    public static final String TYPE_REGION_FLOOR = "3";
    public static final String TYPE_REGION_ROOM = "4";

    public static final String OFFLINE_ALLREGIONSTREE = "全部部位树";
    public static final String OFFLINE_ALLMANAGERREGIONSTREE = "管理岗全部部位树";
    public static final String OFFLINE_PROCEDURES = "工序列表";
    public static final String OFFLINE_CONSTRUCTIONTEAM = "班组列表";
    public static final String OFFLINE_CONTRACTING = "承包商列表";
    public static final String OFFLINE_ACCEPTANCEBATCH = "验收批列表";
    public static final String OFFLINE_ACCEPTANCEPROCEDURES = "验收工序列表";
    public static final String OFFLINE_PERIOD = "期";
    public static final String OFFLINE_BUILD = "栋";
    public static final String OFFLINE_FLOOR = "层";
    public static final String OFFLINE_ROOM = "户";
    public static final String OFFLINE_FLOOR_ROOM = "层户";


    public static final String NETWORKSTATE_OFF = AppContext.mResource.getString(R.string.networkstate_off);
    public static final String NETWORKSTATE_ON = AppContext.mResource.getString(R.string.networkstate_on);
    public static final String PERIOD_LIST = AppContext.mResource.getString(R.string.period_list);
    public static final String OFF_HOUSECHART = AppContext.mResource.getString(R.string.offline_housechart);
    public static final String OFFLINE_PICTURE = "户型图片";
    public static final String ACTION_ACCEPTANCE_FINISH = "action_acceptance_finish";


    //应用下载区分
    public static final String APPINFO_APPLICATION = "application";
    public static final String APPINFO_MYDOWNLOADS = "MyDownloads";
    public static final String APPINFO_MOREAPPLIACTIONS = "MoreAppliactions";

    //postId
    public static final String POSTID_CONSTRUCTION_TEAM = "1";
    public static final String POSTID_SUPERVISOR = "2";
    public static final String POSTID_QUALIFIER = "3";
    public static final String POSTID_MANAGER_FIRST = "4";
    public static final String POSTID_MANAGER_SECOND = "5";
    //public static final String POSTID_OTHER = "6";

    public static final String ACTION_JPUSH_NOTIFICATION_RECEIVED = "action_jpush_notification_received";
    public static final String ACTION_RELOAD_DATA_SUCCESS = "action_reload_data_success";
    public static final String ACTION_RELOAD_DATA_FUCK_SUCCESS = "action_reload_data_fuck_success";
    //头像修改
    public static final String CHANGE_PORTRAIT_SUCCESS = "change_portrait_success";
    public static final int HANDLER_WEATHER_LOCATION_SUCCESS = 0001;
    public static final int HANDLER_WEATHER_LOCATION_FAILED = 0002;

    public static final String FAILURE_INTERNET = "failure_internet";
    //合格跳转已验收，不合适跳转拟复验
    public static final String JUMP_CURRENTINDEX = "jump_currentIndex";

    /**大型设备**/
    //项目期更换发送通知
    public static final String CHANGE_PROJECT_PERIOD = "change_project_period";

    //人员新增通知
    public static final String MSG_PERSON_ADD = "msg_person_add";
}
