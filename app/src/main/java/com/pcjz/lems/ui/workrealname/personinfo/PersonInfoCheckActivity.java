package com.pcjz.lems.ui.workrealname.personinfo;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseActivity;
import com.pcjz.lems.business.base.basebean.BaseData;
import com.pcjz.lems.business.common.utils.BitmapUtils;
import com.pcjz.lems.business.common.utils.CommUtil;
import com.pcjz.lems.business.common.utils.CommonUtil;
import com.pcjz.lems.business.common.utils.MessageBus;
import com.pcjz.lems.business.common.utils.NetStateUtil;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TDevice;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.common.utils.ValidatorUtil;
import com.pcjz.lems.business.common.view.dialog.SelectPersonTypeDialog;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.config.ConfigPath;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.entity.SelectDialogEntity;
import com.pcjz.lems.business.entity.person.PersonEntity;
import com.pcjz.lems.business.model.personInfo.OnPersonInfoListener;
import com.pcjz.lems.business.model.personInfo.PersonInfoModel;
import com.pcjz.lems.business.model.personInfo.PersonInfoModelImpl;
import com.pcjz.lems.business.storage.SharedPreferencesManager;
import com.pcjz.lems.business.webapi.MainApi;
import com.pcjz.lems.business.widget.selectdialog.SelectInfo;
import com.pcjz.lems.business.widget.selectdialog.SelectorDialog;
import com.squareup.otto.BasicBus;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

import static com.pcjz.lems.business.base.BaseApplication.mContext;

/**
 * Created by Greak on 2017/9/18.
 */

public class PersonInfoCheckActivity extends BaseActivity implements OnPersonInfoListener, SelectorDialog.ICallback, View.OnClickListener{
    private static final int REQUEST_CODE_CAMERA_WHOLE = 1001;
    private static final int REQUEST_CODE_WHOLE = 1002;
    private static final int REQUEST_CODE_CAMERA_BRANCH = 1003;
    private static final int REQUEST_CODE_BRANCH = 1004;

    private static final int REQUEST_CODE_CAMERA_HEADER = 10010;
    private static final int REQUEST_CODE_HEADER = 10011;
    private static final int REQUEST_CODE_CAMERA_IDFRONT = 10020;
    private static final int REQUEST_CODE_IDFRONT = 10021;
    private static final int REQUEST_CODE_CAMERA_IDOPPO = 10030;
    private static final int REQUEST_CODE_IDOPPO = 10031;
    private static final int REQUEST_CODE_CAMERA_SEPCIAL= 10040;
    private static final int REQUEST_CODE_SEPCIAL = 10041;
    private static final int REQUEST_CODE_CAMERA_SAFE = 10050;
    private static final int REQUEST_CODE_SAFE = 10051;

    private View parentView;
    private TextView tvTitle, tvDate, tvSelectPersonType , tvSpecialType, specialTypeLable;
    private RelativeLayout rlTitle;
    private LinearLayout llbuttom, llybuttom;
    private PersonInfoModel personInfoModel;
    private Button mBtnNoPass, mBtnPass, mBtnDate;
    private ImageButton ibSafe, ibRemark;
    private LinearLayout rlSafe, rlRemark;
    private String mPid, mState;


    private RadioButton rbMan, rbWoman;

    private SelectPersonTypeDialog mSelectTypeDialog, mSpecialTypeDialog;
    private SelectDialogEntity mSelectPerosnType, mSelectSpecialType;
    private List<SelectDialogEntity> mSelectPerosnTypes, mSelectSpecialTypes;

    private RelativeLayout mParent;
    private PopupWindow mPopupWindow;
    private LinearLayout llPopup, llRemarkBlank, llSafeBlank;
    private Button btnCamera, btnPhoto, btnCancel;

    private String mPersonTypeId, mSpecialTypeId;
    private String mSexType;
    private Button btnSubmit;
    private TextView tvPhone;
    private ImageView ivHeader,ivOpposite, ivFront, ivSpecialPic, ivCardPic;
    private EditText etWorkNum, etName, etPhone, etIDNo, etIDEnddate, etIDEndtime,etIDAddr, etIDSign,
            etSpecialEnddate, etSpecialNo, etSafeNo, etRemark;
    private String realPic, IDFrontPic, IDOPpositePic, specialPic, safePic;
    private int mTempPhotoCode, mTempCameraCode;
    private Bitmap mTempBitMap;

    private String personId, devId, proId;
    private String mModCode;

    private static String PICTURE_NAME = "";
    private int tempTakePhotoCode = 8001; //默认是点击上传真实头像

    public BasicBus mBasicBus = MessageBus.getBusInstance();

    public String tempSelectedPersonType = "";

    private String CheckModCode = "";
    private String mUserId;
    private String mProjectId;

    FunctionConfig.Builder functionConfigBuilder = new FunctionConfig.Builder()
            .setEnableCamera(true)
            .setEnableRotate(true)
            .setEnablePreview(true)
            .setMutiSelectMaxSize(1)
            ;
    final FunctionConfig functionConfig = functionConfigBuilder.build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBasicBus.register(this);

    }

    @Override
    public void setView() {
        parentView = getLayoutInflater().inflate(
                R.layout.activity_person_check, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);

        specialTypeLable = (TextView) findViewById(R.id.specialTypeLable);
        personInfoModel = new PersonInfoModelImpl();
        tvSelectPersonType = (TextView) findViewById(R.id.tvSelectPersonType);
        tvSelectPersonType.setOnClickListener(this);
        ibSafe = (ImageButton) findViewById(R.id.ibSafe);
        ibRemark = (ImageButton) findViewById(R.id.ibRemark);
        rlSafe = (LinearLayout) findViewById(R.id.rlSafe);
        rlRemark = (LinearLayout) findViewById(R.id.rlRemark);
        llbuttom = (LinearLayout) findViewById(R.id.llbuttom);
        llybuttom = (LinearLayout) findViewById(R.id.llybuttom);

        llSafeBlank = (LinearLayout) findViewById(R.id.llSafeBlank);
        llRemarkBlank = (LinearLayout) findViewById(R.id.llRemarkBlank);

        mBtnNoPass = (Button) findViewById(R.id.btnNoPass);
        mBtnPass = (Button) findViewById(R.id.btnPass);
        mBtnNoPass.setOnClickListener(this);
        mBtnPass.setOnClickListener(this);
        ibSafe.setOnClickListener(this);
        ibRemark.setOnClickListener(this);

        tvPhone = (TextView) findViewById(R.id.tvPhone);
        etWorkNum = (EditText) findViewById(R.id.etWorkNum);
        etName = (EditText) findViewById(R.id.etName);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etIDNo = (EditText) findViewById(R.id.etIDNo);
        etIDEnddate = (EditText) findViewById(R.id.etIDEnddate);
        etIDEndtime = (EditText) findViewById(R.id.etIDEndtime);
        etIDSign = (EditText) findViewById(R.id.etIDSign);
        etIDAddr = (EditText) findViewById(R.id.etIDAddr);
        etSpecialEnddate = (EditText) findViewById(R.id.etSpecialEnddate);
        etSpecialNo = (EditText) findViewById(R.id.etSpecialNo);
        etSafeNo = (EditText) findViewById(R.id.etSafeNo);
        etRemark = (EditText) findViewById(R.id.etRemark);

        ivHeader = (ImageView) findViewById(R.id.ivHeader);
        ivHeader.setOnClickListener(this);
        ivOpposite = (ImageView) findViewById(R.id.ivOpposite);
        ivOpposite.setOnClickListener(this);
        ivFront = (ImageView) findViewById(R.id.ivFront);
        ivFront.setOnClickListener(this);
        ivSpecialPic = (ImageView) findViewById(R.id.ivSpecialPic);
        ivSpecialPic.setOnClickListener(this);
        ivCardPic = (ImageView) findViewById(R.id.ivCardPic);
        ivCardPic.setOnClickListener(this);

        ibSafe = (ImageButton) findViewById(R.id.ibSafe);
        ibRemark = (ImageButton) findViewById(R.id.ibRemark);
        rlSafe = (LinearLayout) findViewById(R.id.rlSafe);
        rlRemark = (LinearLayout) findViewById(R.id.rlRemark);
        ibSafe.setOnClickListener(this);
        ibRemark.setOnClickListener(this);

        rbMan = (RadioButton) findViewById(R.id.rbMan);
        rbWoman =  (RadioButton) findViewById(R.id.rbWoman);
        rbMan.setChecked(true);
        mSexType = "1";
        rbMan.setOnClickListener(this);
        rbWoman.setOnClickListener(this);

        btnSubmit = (Button) findViewById(R.id.button);
        btnSubmit.setOnClickListener(this);

        tvSelectPersonType = (TextView) findViewById(R.id.tvSelectPersonType);
        tvSelectPersonType.setOnClickListener(this);
        tvSpecialType = (TextView) findViewById(R.id.tvSpecialType);
        tvSpecialType.setOnClickListener(this);

        View view = getLayoutInflater().inflate(R.layout.popup_upload_img,
                null);
        mParent = (RelativeLayout) view.findViewById(R.id.parent);
        llPopup = (LinearLayout) view.findViewById(R.id.ll_popup);
        btnCamera = (Button) view.findViewById(R.id.item_popupwindows_camera);
        btnPhoto = (Button) view.findViewById(R.id.item_popupwindows_Photo);
        btnCancel = (Button) view.findViewById(R.id.item_popupwindows_cancel);
        mPopupWindow = new PopupWindow(PersonInfoCheckActivity.this);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setContentView(view);

        mParent.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnPhoto.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        mPersonTypeId = "";
        mSpecialTypeId = "";
        realPic = "";
        IDFrontPic = "";
        IDOPpositePic = "";
        specialPic = "";
        safePic = "";

        mTempPhotoCode = 10010;
        mTempCameraCode = 10011;

        mPid = getIntent().getStringExtra("pid");
        mState = getIntent().getStringExtra("mState");

        mUserId = SharedPreferencesManager.getString(ResultStatus.USER_ID);
        mProjectId = SharedPreferencesManager.getString(mUserId + SysCode.PROJECTPERIODID);

        if(StringUtils.equals(mState, "1001")){
            llbuttom.setVisibility(View.VISIBLE);
            llybuttom.setVisibility(View.INVISIBLE);
            etPhone.setVisibility(View.VISIBLE);
            tvPhone.setVisibility(View.GONE);
            tvTitle.setText("修改人员信息");
        }else{
            llbuttom.setVisibility(View.INVISIBLE);
            llybuttom.setVisibility(View.VISIBLE);
            etPhone.setVisibility(View.GONE
            );
            tvPhone.setVisibility(View.VISIBLE);
            tvTitle.setText("人员信息");
        }

        initPersonInfo();

        initNetDatas();


    }

    @Override
    protected boolean hasLoading() {
        return true;
    }

    /**
     * 剪切图片
     *
     * @param uri
     */
    private void getCropImg(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        File dir0 = new File(ConfigPath.demsPath);
        if (!dir0.exists()) {
            dir0.mkdir();
        }
        File dir1 = new File(AppConfig.PICTURE_PATH);
        if (!dir1.exists()) {
            dir1.mkdir();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(AppConfig.PICTURE_PATH + "cutUserProait" + ".jpg")));
        intent.putExtra("return-data", false);
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 325);
        intent.putExtra("aspectY", 320);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("scale", true);
//        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, AppConfig.CAMERA_CROP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //拍照返回结果
                case AppConfig.CAMERA:
                    getCropImg(Uri.fromFile(new File(AppConfig.PICTURE_PATH + PICTURE_NAME + ".jpg")));
                    break;
                case AppConfig.CAMERA_CROP:
                    //得到 剪切后的数据
//                    Bundle extras = data.getExtras();
//                    TLog.log("extras -->"+extras);
                    //if (extras != null) {
                    //如果图片不为空，提交；否则，提示用户信息
                    //如果有网络，提交；否则，提示用户信息
                    if (NetStateUtil.isNetworkConnected(this)) {
                        submitToWeb();
                    } else {
                        AppContext.showToast(AppConfig.STRING.NET_NOT_CONNECT);
                    }
                    //}
                    break;
                //从相册中挑选图片
                case AppConfig.PICTURE_PICK:

                    Uri uri = data.getData();
                    getCropImg(uri);
                    break;
            }
        }
    }

    private void takePic() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File dir0 = new File(ConfigPath.demsPath);
            if (!dir0.exists()) {
                dir0.mkdir();
            }
            File file = new File(AppConfig.PICTURE_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            PICTURE_NAME = String.valueOf(System.currentTimeMillis());
            File photo = new File(file, PICTURE_NAME + ".jpg");
            if (TDevice.getOSVersion() < 24) {
                Uri outPutFileUri = Uri.fromFile(photo);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutFileUri);
                startActivityForResult(intent, AppConfig.CAMERA);
            } else {
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, photo.getAbsolutePath());
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, AppConfig.CAMERA);
            }
        } else {
            new AlertDialog.Builder(PersonInfoCheckActivity.this)
                    .setTitle("提示")
                    .setMessage("请检查SD卡是否可用")
                    .setIcon(android.R.drawable.stat_sys_warning)
                    .setPositiveButton("",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    // TODO Auto-generated method stub
                                    arg0.dismiss();
                                }
                            })
                    .setNegativeButton("",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    // TODO Auto-generated method stub
                                    arg0.dismiss();
                                }
                            }).show();
        }
    }

    private void submitToWeb() {
        AppContext.getInstance().initWebApiUploadImg();
        File headFile = new File(AppConfig.PICTURE_PATH + "cutUserProait" + ".jpg");
        if (!headFile.exists()) {
            return;
        }
        MainApi.uploadFile(headFile, AppConfig.IMG_UPLOAD_URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log("getUploadPortrait : " + httpResult);
                    BaseData<String> basedata = new Gson().fromJson(httpResult,
                            new TypeToken<BaseData<String>>() {
                            }.getType());
                    if (StringUtils.equals(basedata.getCode(), SysCode.SUCCESS)) {

                        realPic = basedata.getData();
//                        ivHeader.setImageBitmap(mTempBitMap);
                        ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + realPic, ivHeader);
                        AppContext.getInstance().reinitWebApi();
                        return;
                    } else {
                        AppContext.showToast(basedata.getMsg());
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                AppContext.showToast("上传图片失败！");
                AppContext.getInstance().reinitWebApi();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                AppContext.getInstance().reinitWebApi();
                AppContext.showToast("上传图片失败！");
            }
        });
    }

    private void initPersonInfo(){
        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("projectPersonId", mPid);
            pData.put("params", paramObj);
            entity = new StringEntity(pData.toString(), "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        MainApi.requestCommon(this, AppConfig.GET_PERSON_INFO, entity, new AsyncHttpResponseHandler() {


            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String httpResult = new String(bytes);
                TLog.log("httpResult "+httpResult);
                try {
                    JSONObject obj = new JSONObject(httpResult);
                    if(StringUtils.equals(obj.getString("code"), SysCode.SUCCESS)){
                        JSONObject person = obj.getJSONObject("data").getJSONObject("person");
                        JSONObject device = obj.getJSONObject("data").getJSONObject("devicePerson");
                        JSONObject proPerson = obj.getJSONObject("data").getJSONObject("projectPerson");
                        mSelectPerosnType = new SelectDialogEntity();
                        mSelectSpecialType = new SelectDialogEntity();
                        if(!proPerson.isNull("empWorkCode")){
                            etWorkNum.setText(proPerson.getString("empWorkCode"));
                        }
                        if(!person.isNull("empName")){
                            etName.setText(person.getString("empName"));
                        }
                        if(!person.isNull("empPhone")){
                            etPhone.setText(person.getString("empPhone"));
                            tvPhone.setText(person.getString("empPhone"));
                        }
                        if(!person.isNull("sex")){
                            if(StringUtils.equals(person.getString("sex"), "1")){
                                rbMan.setChecked(true);
                                mSexType = "1";
                            }else{
                                rbWoman.setChecked(true);
                                mSexType = "0";
                            }
                        }
                        if(!proPerson.isNull("jobTypename")){
                            tvSelectPersonType.setText(proPerson.getString("jobTypename"));
                            mPersonTypeId = proPerson.getString("jobTypenameId");
                            mSelectPerosnType.setmSelectId(proPerson.getString("jobTypenameId"));
                            tempSelectedPersonType = proPerson.getString("jobTypename");
                            if(tempSelectedPersonType.equals(AppConfig.PERSON_TYPE)){
                                specialTypeLable.setText("特种作业信息");
                                if(StringUtils.equals(mState, "1001")){
                                    tvTitle.setText("修改特种人员信息");
                                }else{
                                    tvTitle.setText("特种人员信息");
                                }

                            }else{
                                specialTypeLable.setText("特种作业信息（选填）");
                            }
                        }
                        if(!person.isNull("idCode")){
                            etIDNo.setText(person.getString("idCode"));
                        }
                        if(!person.isNull("idValiddate")){
                            TLog.log("timesss -->"+person.getString("idValiddate"));
                            if(person.getString("idValiddate").indexOf(AppConfig.VALID_ID_DATE) != -1){
                                etIDEnddate.setText(person.getString("idValiddate").substring(0, 10));
                                etIDEndtime.setText("长期");
                            }else{
                                String date = person.getString("idValiddate").replace("-", ".");
                                if(date.length() > 10){
                                    etIDEnddate.setText(person.getString("idValiddate").substring(0, 10));
                                    etIDEndtime.setText(person.getString("idValiddate").substring(11, 21));
                                }else{
                                    date = date + "-0000.00.00";
                                    etIDEnddate.setText(date.substring(0, 10));
                                    etIDEndtime.setText(date.substring(11, 21));
                                }

                            }

                        }
                        if(!person.isNull("idAgency")){
                            etIDSign.setText(person.getString("idAgency"));
                        }
                        if(!person.isNull("empNativeplace")){
                            etIDAddr.setText(person.getString("empNativeplace"));

                        }
                        if(!proPerson.isNull("jobName")){
                            tvSpecialType.setText(proPerson.getString("jobName"));
                            mSpecialTypeId = proPerson.getString("jobNameId");
                            mSelectSpecialType.setmSelectId(proPerson.getString("jobNameId"));

                        }

                        if(!device.isNull("certificateValidDate")){
                            etSpecialEnddate.setText(device.getString("certificateValidDate").substring(0, 10));
                        }
                        if(!device.isNull("certificateCode")){
                            etSpecialNo.setText(device.getString("certificateCode"));
                        }
                        if(!device.isNull("safetyAssessCode")){
                            etSafeNo.setText(device.getString("safetyAssessCode"));
                        }
                        if(!device.isNull("remark")){
                            etRemark.setText(device.getString("remark"));
                        }
                        if(!proPerson.isNull("facephoto")){
                            realPic = proPerson.getString("facephoto");
                            ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + proPerson.getString("facephoto"), ivHeader);
                        }
                        if(!person.isNull("idphotoScan")){
                            IDFrontPic = person.getString("idphotoScan");
                            ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + person.getString("idphotoScan"), ivFront);
                        }
                        if(!person.isNull("idphotoScan2")){
                            IDOPpositePic = person.getString("idphotoScan2");
                            ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + person.getString("idphotoScan2"), ivOpposite);
                        }
                        if(!device.isNull("specialCertificatePhoto")){
                            specialPic = device.getString("specialCertificatePhoto");
                            ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + device.getString("specialCertificatePhoto"), ivSpecialPic);
                        }
                        if(!device.isNull("safetyAssessPhoto")){
                            safePic = device.getString("safetyAssessPhoto");
                            ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + device.getString("safetyAssessPhoto"), ivCardPic);
                        }

                        devId = device.getString("id");
                        proId = proPerson.getString("id");
                        personId = person.getString("id");

                    }else{

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        });
    }

    private void ModPersonInfo(){
        if(etWorkNum.getText().toString().length() < 1){
            AppContext.showToast(R.string.person_work_num);
            return;
        }
        if(etName.getText().toString().length() < 1){
            AppContext.showToast(R.string.person_name_tip);
            return;
        }
        if(!ValidatorUtil.isChinese(etName.getText().toString())){
            AppContext.showToast(R.string.person_name_err);
            return;
        }
        if(mPersonTypeId.length() < 1){
            AppContext.showToast(R.string.person_type_tip);
            return;
        }
        if(etPhone.getText().toString().length() < 1){
            AppContext.showToast(R.string.person_phone);
            return;
        }
        if(!CommUtil.isPhone(etPhone.getText().toString())){
            AppContext.showToast(R.string.error_please_input_rightusername);
            return;
        }
        if(realPic.length() < 1){
            AppContext.showToast(R.string.person_realpic_tip);
            return;
        }
        if(etIDNo.getText().toString().length() < 1){
            AppContext.showToast(R.string.person_idno_tip);
            return;
        }
        if(!ValidatorUtil.isIDCard(etIDNo.getText().toString())){
            AppContext.showToast(R.string.person_idno_err);
            return;
        }
        if(etIDEnddate.getText().toString().length() < 1){
            AppContext.showToast(R.string.person_idenddate_tip);
            return;
        }
        if(!ValidatorUtil.isValidDate(etIDEnddate.getText().toString())){
            AppContext.showToast(R.string.date_error);
            return;
        }
        if(etIDEndtime.getText().toString().length() < 1){
            AppContext.showToast(R.string.person_idendtime_tip);
            return;
        }
        if(etIDEndtime.getText().toString().equals(AppConfig.VALID_ID_DATE)){

        }else{
            if(!ValidatorUtil.isValidDate(etIDEndtime.getText().toString())){
                AppContext.showToast(R.string.date_error);
                return;
            }
        }

        if(etIDSign.getText().toString().length() < 1){
            AppContext.showToast(R.string.person_idsign_tip);
            return;
        }
        if(etIDAddr.getText().toString().length() < 1){
            AppContext.showToast(R.string.person_idaddr_tip);
            return;
        }
        if(IDFrontPic.length() < 1){
            AppContext.showToast(R.string.person_idfpic_tip);
            return;
        }
        if(IDOPpositePic.length() < 1){
            AppContext.showToast(R.string.person_idopic_tip);
            return;
        }
        if(tempSelectedPersonType.equals(AppConfig.PERSON_TYPE)){
            if(mSpecialTypeId.length() < 1){
                AppContext.showToast(R.string.person_special_id);
                return;
            }
            if(etSpecialEnddate.getText().toString().length() < 1){
                AppContext.showToast(R.string.person_special_date);
                return;
            }
            if(!ValidatorUtil.isValidTime(etSpecialEnddate.getText().toString())){
                AppContext.showToast(R.string.special_date_error);
                return;
            }
            if(etSpecialNo.getText().toString().length() < 1){
                AppContext.showToast(R.string.person_special_no);
                return;
            }
            if(specialPic.length() < 1){
                AppContext.showToast(R.string.person_special_pic);
                return;
            }
        }else{
            if(etSpecialEnddate.getText().toString().length() < 1){

            }else{
                if(!ValidatorUtil.isValidTime(etSpecialEnddate.getText().toString())){
                    AppContext.showToast(R.string.special_date_error);
                    return;
                }
            }

        }

        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        JSONObject secondData = new JSONObject();
        JSONObject person = new JSONObject();
        JSONObject devicePerson = new JSONObject();
        JSONObject projectPerson = new JSONObject();

        try {
            person.put("id", personId);
            person.put("empName", etName.getText().toString());
            person.put("empPhone", etPhone.getText().toString());
            person.put("idCode", etIDNo.getText().toString());
            person.put("sex", mSexType);
            person.put("empNativeplace", etIDAddr.getText().toString());
            person.put("idValiddate", etIDEnddate.getText().toString()+"-"+etIDEndtime.getText().toString());
            person.put("idAgency", etIDSign.getText().toString());
            person.put("idphotoScan", IDFrontPic);
            person.put("idphotoScan2", IDOPpositePic);

            devicePerson.put("id", devId);
            devicePerson.put("certificateCode", etSpecialNo.getText().toString());
            devicePerson.put("certificateValidDate", etSpecialEnddate.getText().toString());
            devicePerson.put("specialCertificatePhoto", specialPic);
            devicePerson.put("safetyAssessCode", etSafeNo.getText().toString());
            devicePerson.put("safetyAssessPhoto", safePic);
            devicePerson.put("remark", etRemark.getText().toString());

            projectPerson.put("id", proId);
            projectPerson.put("empWorkCode", etWorkNum.getText().toString());
            projectPerson.put("jobTypenameId", mPersonTypeId);
            projectPerson.put("jobNameId", mSpecialTypeId);
            projectPerson.put("facephoto", realPic);
            projectPerson.put("projectId", mProjectId);

            secondData.put("person", person);
            secondData.put("devicePerson", devicePerson);
            secondData.put("projectPerson", projectPerson);
            pData.put("params", secondData);

            entity = new StringEntity(pData.toString(), "UTF-8");

            MainApi.requestCommon(this, AppConfig.MOD_PERSON_INFO, entity, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String httpResult = new String(bytes);
                    try {
                        JSONObject obj = new JSONObject(httpResult);
                        if(StringUtils.equals(obj.getString("code"), SysCode.SUCCESS)){
                            mBasicBus.post(SysCode.MSG_PERSON_ADD);
                            AppContext.showToast(R.string.mod_person_success);
                            finish();
                        }else{
                            AppContext.showToast(R.string.mod_person_fail);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        AppContext.showToast(R.string.mod_person_fail);
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    AppContext.showToast(R.string.mod_person_fail);
                }
            });

//            submitToServer(entity);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void submitToServer(HttpEntity entity){

        MainApi.requestCommon(this, AppConfig.MOD_PERSON_INFO, entity, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String httpResult = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(httpResult);
                    if(StringUtils.equals(obj.getString("code"), SysCode.SUCCESS)){

                        mBasicBus.post(SysCode.MSG_PERSON_ADD);
                        AppContext.showToast(R.string.mod_person_success);
                        finish();
                    }else{
                        AppContext.showToast(R.string.mod_person_fail);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    AppContext.showToast(R.string.mod_person_fail);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                AppContext.showToast(R.string.mod_person_fail);
            }
        });



    }

    private void initNetDatas(){
        mSelectPerosnTypes =  PublicPersonInfoMethod.getCommonPersonType(mContext);
        mSelectSpecialTypes = PublicPersonInfoMethod.getSpecialPersonType(mContext);
    }

    public void onClick(View v) {
        InputMethodManager mInputMethodManager5 = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        int id = v.getId();
        switch (id) {
            case R.id.item_popupwindows_camera:
                if(tempTakePhotoCode == 8001){
                    takePic();
                    overridePendingTransition(R.anim.activity_translate_in,
                            R.anim.activity_translate_out);
                    mPopupWindow.dismiss();
                    llPopup.clearAnimation();
                }else{
                    GalleryFinal.openCamera(REQUEST_CODE_CAMERA_WHOLE, functionConfig, mOnHanlderResultCallback);
                    overridePendingTransition(R.anim.activity_translate_in,
                            R.anim.activity_translate_out);
                    mPopupWindow.dismiss();
                    llPopup.clearAnimation();
                }
                /*GalleryFinal.openCamera(REQUEST_CODE_CAMERA_WHOLE, functionConfig, mOnHanlderResultCallback);
                overridePendingTransition(R.anim.activity_translate_in,
                        R.anim.activity_translate_out);
                mPopupWindow.dismiss();
                llPopup.clearAnimation();*/
                break;
            case R.id.item_popupwindows_Photo:
                if(tempTakePhotoCode == 8001){
                    Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
                    albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(albumIntent, AppConfig.PICTURE_PICK);
                    overridePendingTransition(R.anim.activity_translate_in,
                            R.anim.activity_translate_out);
                    mPopupWindow.dismiss();
                    llPopup.clearAnimation();
                }else{
                    GalleryFinal.openGalleryMuti(REQUEST_CODE_WHOLE, functionConfig, mOnHanlderResultCallback);
                    overridePendingTransition(R.anim.activity_translate_in,
                            R.anim.activity_translate_out);
                    mPopupWindow.dismiss();
                    llPopup.clearAnimation();
                }
                /*GalleryFinal.openGalleryMuti(REQUEST_CODE_WHOLE, functionConfig, mOnHanlderResultCallback);
                overridePendingTransition(R.anim.activity_translate_in,
                        R.anim.activity_translate_out);
                mPopupWindow.dismiss();
                llPopup.clearAnimation();*/
                break;
            case R.id.parent:
                mPopupWindow.dismiss();
                llPopup.clearAnimation();
                break;
            case R.id.item_popupwindows_cancel:
                mPopupWindow.dismiss();
                llPopup.clearAnimation();
                break;
            case R.id.ivCardPic:
                mInputMethodManager5.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
                mTempPhotoCode = 10050;
                mTempCameraCode = 10051;
                tempTakePhotoCode = 8002;
                llPopup.startAnimation(AnimationUtils.loadAnimation(
                        PersonInfoCheckActivity.this,
                        R.anim.activity_translate_in));
                mPopupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0,
                        0);
                break;

            case R.id.ivSpecialPic:
                mInputMethodManager5.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
                mTempPhotoCode = 10040;
                mTempCameraCode = 10041;
                tempTakePhotoCode = 8002;
                llPopup.startAnimation(AnimationUtils.loadAnimation(
                        PersonInfoCheckActivity.this,
                        R.anim.activity_translate_in));
                mPopupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0,
                        0);
                break;
            case R.id.ivFront:
                mInputMethodManager5.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
                mTempPhotoCode = 10020;
                mTempCameraCode = 10021;
                tempTakePhotoCode = 8002;
                llPopup.startAnimation(AnimationUtils.loadAnimation(
                        PersonInfoCheckActivity.this,
                        R.anim.activity_translate_in));
                mPopupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0,
                        0);
                break;
            case R.id.ivOpposite:
                mInputMethodManager5.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
                mTempPhotoCode = 10030;
                mTempCameraCode = 10031;
                tempTakePhotoCode = 8002;
                llPopup.startAnimation(AnimationUtils.loadAnimation(
                        PersonInfoCheckActivity.this,
                        R.anim.activity_translate_in));
                mPopupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0,
                        0);
                break;
            case R.id.ivHeader:
                mInputMethodManager5.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
                mTempPhotoCode = 10010;
                mTempCameraCode = 10011;
                tempTakePhotoCode = 8001;
                llPopup.startAnimation(AnimationUtils.loadAnimation(
                        PersonInfoCheckActivity.this,
                        R.anim.activity_translate_in));
                mPopupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0,
                        0);
                break;
            case R.id.tvSelectPersonType:
                mSelectTypeDialog = new SelectPersonTypeDialog(this, mSelectPerosnType,  new SelectPersonTypeDialog.DataBackListener() {
                    @Override
                    public void getData(SelectDialogEntity entity) {
                        typeFinish(entity);
                    }
                });
                mSelectPerosnTypes =  PublicPersonInfoMethod.getCommonPersonType(mContext);
                mSelectTypeDialog.setInitSelecList(mSelectPerosnTypes);
                mSelectTypeDialog.setTitle("请选择人员类别");
                mSelectTypeDialog.show();
                break;
            case R.id.tvSpecialType:
                mSpecialTypeDialog = new SelectPersonTypeDialog(this, mSelectSpecialType,  new SelectPersonTypeDialog.DataBackListener() {
                    @Override
                    public void getData(SelectDialogEntity entity) {
                        SpecialFinish(entity);
                    }
                });
                mSelectSpecialTypes = PublicPersonInfoMethod.getSpecialPersonType(mContext);
                mSpecialTypeDialog.setInitSelecList(mSelectSpecialTypes);
                mSpecialTypeDialog.setTitle("请选择特种工种");
                mSpecialTypeDialog.show();
                break;
            case R.id.button:
                ModPersonInfo();
                break;
            case R.id.btnPass:
                CheckPersonInfo();
                /*showOrnotDialog();*/
                break;
            case R.id.btnNoPass:
                showReasonDialog();
                break;
            case R.id.ibRemark:
                if(rlRemark.getVisibility() == View.GONE){
                    rlRemark.setVisibility(View.VISIBLE);
                    ibRemark.setImageDrawable(getResources().getDrawable(R.drawable.ys_list_icon_narrowed));
                    llRemarkBlank.setVisibility(View.GONE);
                }else{
                    rlRemark.setVisibility(View.GONE);
                    llRemarkBlank.setVisibility(View.VISIBLE);
                    ibRemark.setImageDrawable(getResources().getDrawable(R.drawable.ys_list_icon_pack_up));
                }
                break;
            case R.id.ibSafe:
                if(rlSafe.getVisibility() == View.GONE){
                    rlSafe.setVisibility(View.VISIBLE);
                    llSafeBlank.setVisibility(View.GONE);
                    ibSafe.setImageDrawable(getResources().getDrawable(R.drawable.ys_list_icon_narrowed));
                }else{
                    rlSafe.setVisibility(View.GONE);
                    llSafeBlank.setVisibility(View.VISIBLE);
                    ibSafe.setImageDrawable(getResources().getDrawable(R.drawable.ys_list_icon_pack_up));
                }
                break;
            case R.id.rbMan:
                rbMan.setChecked(true);
                mSexType = "1";
                break;
            case R.id.rbWoman:
                rbWoman.setChecked(true);
                mSexType = "0";
                break;
        }
    }

    private void showReasonDialog() {
        final Dialog mDialog = new Dialog(this, R.style.select_dialog);
        mDialog.setContentView(R.layout.alert_reason_dialog);
        final EditText etReason = (EditText) mDialog.findViewById(R.id.etReason);
        TextView tvInfo = (TextView) mDialog.findViewById(R.id.tv_info);
        tvInfo.setText("确定是否不通过？");
        TextView tvConfirm = (TextView) mDialog.findViewById(R.id.confirm_inspect);
        tvConfirm.setText("确定");
        TextView tvConcel = (TextView) mDialog.findViewById(R.id.cancel_inspect);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reasnStr = "";
                HttpEntity entity = null;
                JSONObject pdata = new JSONObject();
                JSONObject obj = new JSONObject();
                JSONObject reason = new JSONObject();
                JSONObject proPerson = new JSONObject();
                reasnStr = etReason.getText().toString();

                if(reasnStr.length() < 1){
                    AppContext.showToast(R.string.reason_person_fail);
                    return;
                }

                try {
                    obj.put("projectPersonId", mPid);
                    obj.put("message", reasnStr);
                    obj.put("verify", "2");
                    pdata.put("params", obj);
                    entity = new StringEntity(pdata.toString(), "UTF-8");
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                MainApi.requestCommon(PersonInfoCheckActivity.this, AppConfig.CHECK_PERSON_INFO, entity, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        String httpResult = new String(bytes);
                        TLog.log("httpResult "+httpResult);
                        try {
                            JSONObject obj = new JSONObject(httpResult);
                            if(StringUtils.equals(obj.getString("code"), SysCode.SUCCESS)){
                                mBasicBus.post(SysCode.MSG_PERSON_ADD);
                                finish();
                                mDialog.dismiss();
                            }else{
                                AppContext.showToast(R.string.mod_person_fail);
                                mDialog.dismiss();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            AppContext.showToast(R.string.mod_person_fail);
                            mDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        AppContext.showToast(R.string.mod_person_fail);
                        mDialog.dismiss();
                    }
                });

            }
        });
        tvConcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*setResult(0);
                finish();*/
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {

        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {

                if (reqeustCode == REQUEST_CODE_CAMERA_WHOLE) {
                    readPicture(resultList.get(0).getPhotoPath());
                } else if (reqeustCode == REQUEST_CODE_WHOLE) {
                    for (int i = 0; i < resultList.size(); i++) {
                        int bitmapDegree = CommonUtil.getBitmapDegree(resultList.get(i).getPhotoPath());
                        Bitmap bm = CommonUtil.rotateBitmapByDegree(resultList.get(i).getBitmap(), bitmapDegree);
                        bm = BitmapUtils.compressImage(bm);
                        /*Bitmap waterBitmap = CommUtil.getInstance().addWaterBitmap(PersonInfoAddActivity.this, bm);*/                        resultList.get(i).setBitmap(bm);
                    }
                    mTempBitMap = resultList.get(0).getBitmap();
                    GetServerImgTempPath(resultList.get(0).getPhotoPath(), resultList.get(0).getBitmap());

                }
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
        }
    };



    private void GetServerImgTempPath(String photoPath, Bitmap bitmap){

        String imagePath = photoPath;
        Bitmap mbitmap = bitmap;
        File[] files = new File[1];
        try {
            files[0] = CommonUtil.saveFile(mbitmap, ConfigPath.downloadImagePath, CommonUtil.getPicNameFromPath(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        AppContext.getInstance().initWebApiUploadImg();
        MainApi.uploadFile(files[0], AppConfig.IMG_UPLOAD_URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    BaseData<String> basedata = new Gson().fromJson(httpResult,
                            new TypeToken<BaseData<String>>() {
                            }.getType());
                    if (StringUtils.equals(basedata.getCode(), SysCode.SUCCESS)) {
                        if(mTempPhotoCode == 10010){
                            /*realPic = basedata.getData();
                            ivHeader.setImageBitmap(mTempBitMap);*/
                        }else if(mTempPhotoCode == 10020){
                            IDFrontPic = basedata.getData();
                            ivFront.setImageBitmap(mTempBitMap);
                        }else if(mTempPhotoCode == 10030){
                            IDOPpositePic = basedata.getData();
                            ivOpposite.setImageBitmap(mTempBitMap);
                        }else if(mTempPhotoCode == 10040){
                            specialPic = basedata.getData();
                            ivSpecialPic.setImageBitmap(mTempBitMap);
                        }else if(mTempPhotoCode == 10050){
                            safePic = basedata.getData();
                            ivCardPic.setImageBitmap(mTempBitMap);
                        }

                    }else{
                        AppContext.showToast(R.string.error_uploadphoto);
                    }
                    AppContext.getInstance().reinitWebApi();
                } catch (Exception e) {
                    e.printStackTrace();
                    AppContext.showToast(R.string.error_uploadphoto);
                    AppContext.getInstance().reinitWebApi();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                AppContext.showToast(R.string.error_uploadphoto);
                AppContext.getInstance().reinitWebApi();
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                super.onProgress(bytesWritten, totalSize);
            }
        });
    }

    private void readPicture(String path) {
        //读取照片旋转的角度
        int bitmapDegree = CommonUtil.getBitmapDegree(path);
        PhotoInfo photoInfo = new PhotoInfo();
        photoInfo.setPhotoPath(path);

        //按某个角度进行旋转
        Bitmap bm = CommonUtil.rotateBitmapByDegree(photoInfo.getBitmap(), bitmapDegree);
        bm = BitmapUtils.compressImage(bm);
        Bitmap waterBitmap = CommUtil.getInstance().addWaterBitmap(PersonInfoCheckActivity.this, bm);
        photoInfo.setBitmap(bm);
        mTempBitMap = photoInfo.getBitmap();
        GetServerImgTempPath(photoInfo.getPhotoPath(), photoInfo.getBitmap());

    }

    private void CheckPersonInfo(){
        if(etWorkNum.getText().toString().length() < 1){
            AppContext.showToast(R.string.person_work_num);
            return;
        }
        if(etName.getText().toString().length() < 1){
            AppContext.showToast(R.string.person_name_tip);
            return;
        }
        if(!ValidatorUtil.isChinese(etName.getText().toString())){
            AppContext.showToast(R.string.person_name_err);
            return;
        }
        if(mPersonTypeId.length() < 1){
            AppContext.showToast(R.string.person_type_tip);
            return;
        }
        if(etPhone.getText().toString().length() < 1){
            AppContext.showToast(R.string.person_phone);
            return;
        }
        if(!CommUtil.isPhone(etPhone.getText().toString())){
            AppContext.showToast(R.string.error_please_input_rightusername);
            return;
        }
        if(realPic.length() < 1){
            AppContext.showToast(R.string.person_realpic_tip);
            return;
        }
        if(etIDNo.getText().toString().length() < 1){
            AppContext.showToast(R.string.person_idno_tip);
            return;
        }
        if(!ValidatorUtil.isIDCard(etIDNo.getText().toString())){
            AppContext.showToast(R.string.person_idno_err);
            return;
        }
        if(etIDEnddate.getText().toString().length() < 1){
            AppContext.showToast(R.string.person_idenddate_tip);
            return;
        }
        if(!ValidatorUtil.isValidDate(etIDEnddate.getText().toString())){
            AppContext.showToast(R.string.date_error);
            return;
        }
        if(etIDEndtime.getText().toString().length() < 1){
            AppContext.showToast(R.string.person_idendtime_tip);
            return;
        }
        if(etIDEndtime.getText().toString().equals(AppConfig.VALID_ID_DATE)){

        }else{
            if(!ValidatorUtil.isValidDate(etIDEndtime.getText().toString())){
                AppContext.showToast(R.string.date_error);
                return;
            }
        }
        if(etIDSign.getText().toString().length() < 1){
            AppContext.showToast(R.string.person_idsign_tip);
            return;
        }
        if(etIDAddr.getText().toString().length() < 1){
            AppContext.showToast(R.string.person_idaddr_tip);
            return;
        }
        if(IDFrontPic.length() < 1){
            AppContext.showToast(R.string.person_idfpic_tip);
            return;
        }
        if(IDOPpositePic.length() < 1){
            AppContext.showToast(R.string.person_idopic_tip);
            return;
        }
        if(tempSelectedPersonType.equals(AppConfig.PERSON_TYPE)){
            if(mSpecialTypeId.length() < 1){
                AppContext.showToast(R.string.person_special_id);
                return;
            }
            if(etSpecialEnddate.getText().toString().length() < 1){
                AppContext.showToast(R.string.person_special_date);
                return;
            }
            if(!ValidatorUtil.isValidTime(etSpecialEnddate.getText().toString())){
                AppContext.showToast(R.string.special_date_error);
                return;
            }
            if(etSpecialNo.getText().toString().length() < 1){
                AppContext.showToast(R.string.person_special_no);
                return;
            }
            if(specialPic.length() < 1){
                AppContext.showToast(R.string.person_special_pic);
                return;
            }
        }else{
            if(etSpecialEnddate.getText().toString().length() < 1){

            }else{
                if(!ValidatorUtil.isValidTime(etSpecialEnddate.getText().toString())){
                    AppContext.showToast(R.string.special_date_error);
                    return;
                }

            }
        }



        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        JSONObject secondData = new JSONObject();
        JSONObject person = new JSONObject();
        JSONObject devicePerson = new JSONObject();
        JSONObject projectPerson = new JSONObject();

        try {
            person.put("id", personId);
            person.put("empName", etName.getText().toString());
            person.put("empPhone", etPhone.getText().toString());
            person.put("idCode", etIDNo.getText().toString());
            person.put("sex", mSexType);
            person.put("empNativeplace", etIDAddr.getText().toString());
            person.put("idValiddate", etIDEnddate.getText().toString()+"-"+etIDEndtime.getText().toString());
            person.put("idAgency", etIDSign.getText().toString());
            person.put("idphotoScan", IDFrontPic);
            person.put("idphotoScan2", IDOPpositePic);

            devicePerson.put("id", devId);
            devicePerson.put("certificateCode", etSpecialNo.getText().toString());
            devicePerson.put("certificateValidDate", etSpecialEnddate.getText().toString());
            devicePerson.put("specialCertificatePhoto", specialPic);
            devicePerson.put("safetyAssessCode", etSafeNo.getText().toString());
            devicePerson.put("safetyAssessPhoto", safePic);
            devicePerson.put("remark", etRemark.getText().toString());

            projectPerson.put("id", proId);
            projectPerson.put("empWorkCode", etWorkNum.getText().toString());
            projectPerson.put("jobTypenameId", mPersonTypeId);
            projectPerson.put("jobNameId", mSpecialTypeId);
            projectPerson.put("facephoto", realPic);
            projectPerson.put("projectId", mProjectId);

            secondData.put("person", person);
            secondData.put("devicePerson", devicePerson);
            secondData.put("projectPerson", projectPerson);
            pData.put("params", secondData);

            entity = new StringEntity(pData.toString(), "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        MainApi.requestCommon(this, AppConfig.MOD_PERSON_INFO, entity, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String httpResult = new String(bytes);

                try {
                    JSONObject obj = new JSONObject(httpResult);
                    if(StringUtils.equals(obj.getString("code"), SysCode.SUCCESS)){
                        showOrnotDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });

    }
    private boolean isClickState = false;
    private void showOrnotDialog() {
        final Dialog mDialog = new Dialog(this, R.style.select_dialog);
        mDialog.setContentView(R.layout.alert_ornot_dialog);
        TextView tvInfo = (TextView) mDialog.findViewById(R.id.tv_info);
        tvInfo.setText("确定是否通过？");
        TextView tvConfirm = (TextView) mDialog.findViewById(R.id.confirm_inspect);
        tvConfirm.setText("确定");
        TextView tvConcel = (TextView) mDialog.findViewById(R.id.cancel_inspect);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isClickState){
                  /*AppContext.showToast("不能重复点击！");*/
                }else{
                    isClickState = true;
                    initLoading("请求中...");
                    HttpEntity entity = null;
                    JSONObject pdata = new JSONObject();
                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("projectPersonId", mPid);
                        obj1.put("verify", "1");
                        pdata.put("params", obj1);
                        entity = new StringEntity(pdata.toString(), "UTF-8");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    MainApi.requestCommon(PersonInfoCheckActivity.this, AppConfig.CHECK_PERSON_INFO, entity, new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            String httpResult = new String(bytes);
                            try {
                                JSONObject obj = new JSONObject(httpResult);
                                if(StringUtils.equals(obj.getString("code"), SysCode.SUCCESS)){
                                    mBasicBus.post(SysCode.MSG_PERSON_ADD);
                                    AppContext.showToast(R.string.check_person_success);
                                    finish();
                                    mDialog.dismiss();
                                    hideLoading();
                                }else{
                                    AppContext.showToast(R.string.mod_person_fail);
                                    mDialog.dismiss();
                                    hideLoading();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                AppContext.showToast(R.string.mod_person_fail);
                                mDialog.dismiss();
                                hideLoading();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            AppContext.showToast(R.string.mod_person_fail);
                            mDialog.dismiss();
                            hideLoading();
                        }
                    });

                }



            }
        });
        tvConcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*setResult(0);
                finish();*/
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBasicBus.unregister(this);
    }

    private void SpecialFinish(SelectDialogEntity entity){
        mSelectSpecialType = entity;
        mSpecialTypeId = entity.getmSelectId();
        tvSpecialType.setText(entity.getmSelectName());
    }

    private void typeFinish(SelectDialogEntity entity) {
        mSelectPerosnType = entity;
        mPersonTypeId = entity.getmSelectId();
        tvSelectPersonType.setText(entity.getmSelectName());
        tempSelectedPersonType = entity.getmSelectName();
        if(entity.getmSelectName().equals(AppConfig.PERSON_TYPE)){
            specialTypeLable.setText("特种作业信息");
        }else{
            specialTypeLable.setText("特种作业信息（选填）");
        }
    }


    private void displayResult(PersonEntity personEntity){
    }

    @Override
    public void setListener() {

    }


    @Override
    public void success(PersonEntity personEntity) {
        displayResult(personEntity);
    }

    @Override
    public void error() {

    }

    @Override
    public void finish(List selectedNames, List selectedIds, String mode) {

    }

    @Override
    public void finish(List selcectorNames, List selectorIds, List selectedNames, List selectedIds, String mode) {

    }

    @Override
    public ArrayList<SelectInfo> getNextSelectList(int currentPostion, String currentSelectName) {
        return null;
    }

    @Override
    public ArrayList<SelectInfo> getNextSelectList(int currentPostion, int selectedPosition, String currentSelectName) {
        return null;
    }

    @Override
    public void clearData() {

    }
}
