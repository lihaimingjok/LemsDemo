package com.pcjz.lems.business.config;

import android.content.Context;
import android.os.Environment;

public class AppConfig {
    public static final int BANNER_TIME_SPAN = 200;//"http://172.16.12.42:8080/quality"张工
    public static final String CONTENT_TYPE = "application/json";//119.147.80.246:8081 http://116.7.226.222/quality

    //外网正式
    public static final String HOST = "116.7.226.222";
    public static final String API_PORT = "10001";
    public static final String BASE_SERVER_URL = "http://" + HOST + ":" + API_PORT + "/";

    //内网测试
    //public static final String WEBSERVICE_URL = "http://172.16.12.150:81/device/";
    //外网测试
    public static final String WEBSERVICE_URL = "http://116.7.226.222:10001/device/";
    //外网正式
//    public static final String WEBSERVICE_URL = "http://116.7.226.222:100/device/";
    //外网图片正式和测试端口100
    public static final String IMAGE_FONT_URL = "http://116.7.226.222:10001/static/";
    public static final String APK_URL = "http://116.7.226.222:10001/static/";
    //public static final String IMAGE_FONT_URL = "http://172.16.12.150:81/static/";
    //public static final String APK_URL = "http://172.16.12.150:81/static/";

    //登录接口
    public static final String LOGIN_URL = "client/login";
    //获取天气接口
    public static final String WEATHER_URL = "client/common/getWeather";
    //获取楼栋层户接口
    //	public static final String GETREGIONSTREE_URL = "client/userAuth/getRegionsTree";
    public static final String GETREGIONSTREE_URL = "client/projectTree/getAllRegionsTree";
    //获取全部树信息（权限过滤）
    public static final String GETREGIONSTREE_BYUSER_URL = "client/projectTree/getRegionsTreeByUser";

    public static final String OPERATROLE_URL = "operatRole/getPage";
    //获取天气接口
    public static final String GETWEATHER_URL = "client/common/getWeather";
    //获取报验工序接口
    public static final String ALL_DATATYPE_URL = "client/userAuth/getUserPrivilegesByDataTypeSign";
    public static final String PROJECTPERIOD_URL = "client/deviceProjectManager/getProjects";
    //获取报验工序接口
    public static final String GET_PROCEDUREINFO_URL = "client/procedureInfo/getListByRegion";
    //获取报验工序接口（包含权e限过滤）
    public static final String GET_PROCEDUREINFO_REGION_URL = "client/procedureInfo/getListByRegionAndUser";
    //获取项目期数列表（权限）
    public static final String GET_PROJECT_PERIOD_URL = "client/procedureInfo/getProjectPeriodByUser";
    //获取用户的操作权限接口
    public static final String USER_OPERATPRIVILEGES_URL = "client/userAuth/getUserOperatPrivileges";
    //获取报验人员接口
    public static final String GET_INSPECTORLIST_URL = "client/user/getInspectorList";
    //点击报验接口
    public static final String ACCEPTANCENOTEADD_URL = "client/acceptanceNote/add";
    //获取报验记录列表接口
    public static final String GETPAGEBYTEAM_URL = "client/acceptanceNote/getPageByTeam";
    //获取首页工作台未验收、已验收、拟复验接口
    public static final String GET_PAGEBYSTATUS_URL = "client/acceptanceBatch/getPageByStatus";
    public static final String ADD_BATCH_URL = "client/acceptanceBatch/addBatch";
    //上传图片接口
    public static final String IMG_UPLOAD_URL = "client/img/upload";
    public static final String IMGS_UPLOAD_URL = "client/img/uploadImgs";
    //修改密码
    public static final String USER_MOD_URL = "client/user/mod";
    //登出
    public static final String LOGIN_OUT_URL = "loginOut";
    //获取户型图信息（分区）
    public static final String HOUSEHOLD_CHART_URL = "client/householdChart/getHouseholdChartInfo";
    //请求质检或监理验收列表
    public static final String ALLACCEPTANCE_BATCH_BYUSER_URL = "client/acceptanceBatch/getAllAcceptanceBatchByUser";
    //上传多张图片 （添加隐蔽验收图片）
    public static final String ADD_HIDDEN_PHOTOS_URL = "client/hiddenPhotosRecord/addHiddenPhotos";
    //上传多张图片接口
    public static final String UPLOAD_FILES_URL = "client/img/uploadFiles";
    //下载项目期数接口
    public static final String OFFLINE_DOWNLOAD_URL = "offline";
    //提交验收
    public static final String COMMIT_ACCEPTANCE_URL = "client/acceptanceBatch/addAcceptanceBatchRecord";//提交验收
    public static final String COMMIT_NEW_ACCEPTANCE_URL = "client/acceptanceBatch/addNewBatchAcceptance";//提交验收
    public static final String GET_SUPERVISOR_URL = "client/user/getSupervisorList";
    public static final String SEND_BATCHNOTICE_URL = "client/acceptanceBatch/sendBatchNotice";


    //获取验收批信息
    public static final String GET_BATCH_INFO_URL = "client/acceptanceBatch/getBatchInfo";
    //获取班组列表
    public static final String GET_PROJECTTEAM_URL = "client/team/getProjectTeamList";
    //获取总承包商，专业承包商
    public static final String GET_CONTRACTING_URL = "client/company/getPartnerCompanyList";
    //查看隐蔽验收照片
    public static final String GET_HIDDEN_PHOTOS_URL = "client/hiddenPhotosRecord/getHiddenPhotos";
    //删除隐蔽验收照片
    public static final String DELETE_HIDE_PHOTO_URL = "client/hiddenPhotos/deleteList";

    //离线下载列表
    public static final String GET_PROJECTDETAIL_LIST_URL = "client/procedureInfo/getProjectDetailList";

    //获取区域项目树
    public static final String GET_ORGPROJECTTREE_URL = "client/organization/getOrgProjectTree";
    public static final String GET_SCHEDULETREE_URL = "client/procedureScheduleRelate/getScheduleTree";
    public static final String GET_BUILDINGTOFLOORS_URL = "client/procedureScheduleRelate/getBuildingScheduleTree";
    public static final String GET_PROJECTPHOTOS_URL = "client/projectPeriodPhotos/getList";
    //增加项目实景照片
    public static final String ADD_PHOTOS_URL = "client/projectPeriodPhotos/addList";
    //删除项目实景照片
    public static final String DELETE_PHOTOS_URL = "client/projectPeriodPhotos/delete";
    //查看验收记录
    public static final String GET_ACCEPTANCE_RECORD_URL = "client/acceptanceBatch/getAcceptanceRecordInfo";
    //获取栋的工序验收进度
    public static final String GET_PROCEDURESCHEDULE_URL = "client/procedureScheduleRelate/getProcedureSchedule";

    //获取应用分页列表
    public static final String GET_APPINFOLIST_URL = "client/appInfo/getPage";
    //获取应用列表
    public static final String GET_APPLICATIONS_URL = "client/appInfo/getList";
    //获取应用详细信息
    public static final String GET_APPINFO_DETAIL_URL = "client/appInfo/get";
    //分页获取修改任务单列表
    public static final String GET_ALLACCEPTANCEBATCH_BYNOTICE_URL = "client/acceptanceBatch/getAllAcceptanceBatchByNotice";
    //意见反馈
    public static final String SUBMIT_FEEDBACK_URL = "client/feedbackInfo/add";
    //获取部位验收记录列表
    public static final String GET_REGION_ACCEPTANCE_RECORD_LIST = "client/acceptanceBatch/getRegionAcceptanceRecordList";
    //获取部位验收记录信息
    public static final String GET_REGION_ACCEPTANCE_RECORD_INFO = "client/acceptanceBatch/getRegionAcceptanceRecordInfo";
    //获取验收批记录详细信息
    public static final String GET_REGION_ACCEPTANCE_RECORD_DETAILINFO = "client/acceptanceBatch/getRegionAcceptanceRecordDetailInfo";
    //获取岗位列表
    public static final String GET_POST_INFO_LIST = "client/acceptanceNote/getPostInfoList";
    //获取项目合格率
    public static final String GET_PROJECT_ACCEPTANCE_STATISTICS = "client/acceptanceNote/getProjectAcceptanceStatistics";
    //获取项目合格率排名
    public static final String GET_PROJECT_ACCEPTANCE_STATISTICS_RANKING = "client/acceptanceNote/getProjectAcceptanceStatisticsRanking";
    //获取项目班组列表
    public static final String GET_TEAM_LIST_BY_PROJECT = "client/acceptanceNote/getTeamListByProject";
    //获取班组合格率
    public static final String GET_TEAM_ACCEPTANCE_STATISTICS = "client/acceptanceNote/getTeamAcceptanceStatistics";
    //获取班组合格率排名
    public static final String GET_TEAM_ACCEPTANCE_STATISTICS_RANKING = "client/acceptanceNote/getTeamAcceptanceStatisticsRanking";
    //获取总承包公司列表
    public static final String GET_COMPANY_LIST = "client/acceptanceNote/getCompanyList";
    //获取总承包合格率
    public static final String GET_COMPANY_ACCEPTANCE_STATISTICS = "client/acceptanceNote/getCompanyAcceptanceStatistics";
    //获取项目录入统计列表
    public static final String GET_PROJECT_ACCEPTANCE_COUNT = "client/acceptanceNote/getProjectAcceptanceCount";
    //获取项目录入统计列表
    public static final String GET_PROJECT_ACCEPTANCE_DETAIL_COUNT = "client/acceptanceNote/getProjectAcceptanceDetailCount";
    //安卓APP更新
    public static final String GET_LAST_VERSION = "client/appInfo/getLastVersion";
    //获取项目质检监理列表
    public static final String GET_POST_USER_LIST = "client/acceptanceNote/getPostUserList";
    //获取质检监理符合率
    public static final String GET_USER_ACCEPTANCE_STATISTICS_BY_POST = "client/acceptanceNote/getUserAcceptanceStatisticsByPost";
    //获取报验员列表
    public static final String GET_TEAM_INSPECTOR_LIST = "client/acceptanceNote/getTeamInspectorList";
    //获取报验统计
    public static final String GET_TEAM_ACCEPTANCE_COUNT_BY_PROJECT = "client/acceptanceNote/getTeamAcceptanceCountByProject";
    //工作台信息
    public static final String GET_INFO = "client/tenantProjectApp/getInfo";


    /**##########大型设备人员管理#########**/
    //获取一般人员类别
    public static final String GET_COMMON_PERSON_TYPE = "client/laborPersonInfo/get/jobTypename";
    //获取特种工种
    public static final String GET_SPECIAL_PERSON_TYPE = "client/laborPersonInfo/get/jobName";
    //新增人员
    public static final String ADD_PERSON_INFO = "client/laborPersonInfo/add";
    //获取人员列表
    public static final String GET_PERSON_PAGE = "client/laborPersonInfo/getPage";
    //获取人员信息
    public static final String GET_PERSON_INFO = "client/laborPersonInfo/get";
    //修改人员信息
    public static final String MOD_PERSON_INFO = "client/laborPersonInfo/edit";
    //审核人员是否通过
    public static final String CHECK_PERSON_INFO = "client/laborPersonInfo/verify";
    //身份证长期
    public static final String VALID_ID_DATE = "长期";

    /*********大型设备项目设备管理**********/
    //获取大型设备类型
    public static final String GET_EQUIPMENT_TYPE = "client/deviceLargeDeviceType/getAll";
    //提交大型设备
    public static final String SUBMIT_ADD_LARGE_EQUIPMENT = "client/deviceLargeDeviceInfo/add";
    //获取项目的所有大型设备
    public static final String GET_ALL_LARGE_EQUIPMENT = "client/deviceLargeDeviceInfo/getAll";
    //获取考勤机列表
    public static final String GET_WORK_MECHINE_LIST = "client/deviceAttendanceDeviceInfo/getPage";
    //新增考勤机
    public static final String ADD_WORK_MECHINE = "client/deviceAttendanceDeviceInfo/add";
    //获取考勤机信息
    public static final String GET_WORK_MECHINE_INFO = "client/deviceAttendanceDeviceInfo/get";
    //修改考勤机信息
    public static final String MODIFY_WORK_MECHINE_INFO = "client/deviceAttendanceDeviceInfo/edit";
    //删除考勤机信息
    public static final String DELETE_WORK_MECHINE_INFO = "client/deviceAttendanceDeviceInfo/delete";
    //获取大型设备信息
    public static final String GET_EQUIPMENT_INFO = "client/deviceLargeDeviceInfo/get";
    //获取大型设备列表
    public static final String GET_EQUIPMENT_LIST = "client/deviceLargeDeviceInfo/getPage";
    //修改大型设备信息
    public static final String MODIFY_EQUIPMENT_INFO = "client/deviceLargeDeviceInfo/edit";
    //解除绑定人员
    public static final String DELETE_BIND_PERSON = "client/deviceLargeDeviceInfo/unbindPerson";
    //获取大型设备下绑定的考勤机的人员
    public static final String GET_LARGE_EQUIPMENT_BIND_WORK_MECHINE_PERSONS = "/client/deviceLargeDeviceInfo/getPersons";
    //删除大型设备
    public static final String DELETE_EQUIPMENT_INFO = "client/deviceLargeDeviceInfo/delete";
    //操作记录分页查询
    public static final String GET_OPERATE_RECORD_LIST = "client/deviceOperatRecord/getPage";
    //获取设备运行统计
    public static final String GET_DEVICE_RUN_STATICS = "client/deviceProjectRelate/getProjectDeviceRunningStatistics";

    /**##########大型设备项目概况管理#########**/
    //特种作业人员
    public static final String PERSON_TYPE = "特种作业人员";
    //今日设备实时概况
    public static final String TODAY_DEVICE_SUMMARY = "client/deviceProjectRelate/getProjectDeviceProfile";
    //获取设备运行概况
    public static final String RUNNING_DEVICE_SUMMARY = "client/deviceProjectRelate/getProjectDeviceRanStatistics";
    //获取预警消息列表
    public static final String WARN_MSG_LIST = "client/deviceWarnMessageInfo/getMessagePage";
    //预警消息已读
    public static final String WARN_MSG_READ = "client/deviceWarnMessageInfo/toIsRead";

    public static final int BITMAP_COMPRESS_PERCENT = 60;
    public static int pageSize = 6;
    public static final int PHOTO_COUNT = 100;
    public static final int STREET_PAGESIZE = 6;
    public static final int STREET_PAGESIZE1 = 7;
    public static final String SPEECH_APPID = "55f292ba";
    public static final String PICTURE_PATH = Environment
            .getExternalStorageDirectory() + "/LEMS/Pictures/";

    public static final String LOCAL_PATH = "https://appstore.crc.com.cn/MDM_SERVICE/appInfoDetail/getAppInfo?appPackageName=cn.com.crcement.cctgic&devicefamily=a";//cctgps
    public static final String APK_NAME = "cn_com_crcement_ccterp.apk";//cn_com_crcement_ccterp
    public static final String LOCAL_PATH1 = Environment
            .getExternalStorageDirectory().getPath()
            + "/cn/com/crcement/ccterp/update/";
    public static final int MAX_PAGE_SIZE = 10000;
    public static final int DEFAULT_CURRENT_PAGE = 1;
    public static boolean MANAGER = false;
    public static int OFFLINE_DOWNLOAD_TITLE = 13;

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE = "LEMS";

    /**
     * 根目录
     */
    public static final String ROOT_FILE = "pcjz_lems";

    /**
     * 临时文件文件夹
     */
    public static final String FILE_TEMP = "temp";

    public static final String FILE_IMG = "img";

    //照相机
    public static final int CAMERA = 0x3005;
    public static final int PICTURE_PICK = 0x3006;
    public static final int CAMERA_CROP = 0x3007;

    /**
     * 字符串
     */
    public interface STRING {
        String HEAD_IMAGE_FAIL = "头像获取失败";
        String NET_NOT_CONNECT = "网络未连接，请先连接网络！";
    }

    public static int getPageSize(Context context) {
        /*int height = TDevice.getHeightPixels(context);
        return (int)(height/(50*TDevice.getDensity(context)));*/
        return 15;
    }

    public static void init(Context context) {
        pageSize = getPageSize(context);
    }
}
