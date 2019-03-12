package com.pcjz.lems.ui.workrealname.personinfo;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

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

/**
 * Created by Greak on 2017/9/19.
 */

public class PersonInfoAddActivity  extends BaseActivity implements OnPersonInfoListener, SelectorDialog.ICallback, View.OnClickListener{
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

    private Context mContext;
    private View parentView;

    private TextView tvTitle, tvSelectPersonType, tvSpecialType, specialTypeLable;
    private PersonInfoModel personInfoModel;
    private ImageButton ibSafe, ibRemark;
    private LinearLayout rlSafe, rlRemark, llRemarkBlank, llSafeBlank;
    private RadioButton rbMan, rbWoman;

    private SelectPersonTypeDialog mSelectTypeDialog, mSpecialTypeDialog;
    private SelectDialogEntity mSelectPerosnType, mSelectSpecialType;
    private List<SelectDialogEntity> mSelectPerosnTypes, mSelectSpecialTypes;

    private RelativeLayout mParent;
    private PopupWindow mPopupWindow;
    private LinearLayout llPopup;
    private Button btnCamera, btnPhoto, btnCancel;

    private String mPersonTypeId, mSpecialTypeId;
    private String mSexType;
    private Button btnSubmit;
    private ImageView ivHeader,ivOpposite, ivFront, ivSpecialPic, ivCardPic;
    private EditText etJobNum, etName, etIDNo, etPhone, etIDEnddate, etIDEndtime, etIDAddr, etIDSign, etSpecialEnddate, etSpecialNo, etSafeNo, etRemark;
    private String realPic, IDFrontPic, IDOPpositePic, specialPic, safePic;
    private int mTempPhotoCode, mTempCameraCode;
    private Bitmap mTempBitMap;

    private static String PICTURE_NAME = "";
    private int tempTakePhotoCode = 8001; //默认是点击上传真实头像

    /*eventBus 消息*/
    public BasicBus mBasicBus = MessageBus.getBusInstance();

    public String tempSelectedPersonType = "";

    private String mUserId;
    private String mProjectId;

    FunctionConfig.Builder functionConfigBuilder = new FunctionConfig.Builder()
            .setEnableCamera(true)
            .setEnableRotate(true)
            .setEnableCrop(true)
            .setEnablePreview(false)
            .setMutiSelectMaxSize(1)
            ;
   FunctionConfig functionConfig = functionConfigBuilder.build();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBasicBus.register(this);
    }

    @Override
    public void setView() {
        parentView = getLayoutInflater().inflate(
                R.layout.activity_personinfo_add, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {


        mContext = PersonInfoAddActivity.this;
        tvTitle = (TextView) findViewById(R.id.tv_title);
        specialTypeLable = (TextView) findViewById(R.id.specialTypeLable);
        tvTitle.setText("新增人员信息");
        personInfoModel = new PersonInfoModelImpl();

        llSafeBlank = (LinearLayout) findViewById(R.id.llSafeBlank);
        llRemarkBlank = (LinearLayout) findViewById(R.id.llRemarkBlank);

        etJobNum = (EditText) findViewById(R.id.etJobNum);
        etName = (EditText) findViewById(R.id.etName);
        etIDNo = (EditText) findViewById(R.id.etIDNo);
        etPhone = (EditText) findViewById(R.id.etPhone);
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
        mPopupWindow = new PopupWindow(PersonInfoAddActivity.this);
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

        mUserId = SharedPreferencesManager.getString(ResultStatus.USER_ID);
        mProjectId = SharedPreferencesManager.getString(mUserId + SysCode.PROJECTPERIODID);

        initNetDatas();
    }

    private void initNetDatas(){
        mSelectPerosnTypes =  PublicPersonInfoMethod.getCommonPersonType(mContext);
        mSelectSpecialTypes = PublicPersonInfoMethod.getSpecialPersonType(mContext);
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
            new AlertDialog.Builder(PersonInfoAddActivity.this)
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


    public void onClick(View v) {
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
//                GalleryFinal.openCrop(REQUEST_CODE_WHOLE, functionConfig, mOnHanlderResultCallback);

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
                InputMethodManager mInputMethodManager5 = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                mInputMethodManager5.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
                mTempPhotoCode = 10050;
                mTempCameraCode = 10051;
                tempTakePhotoCode = 8002;
                llPopup.startAnimation(AnimationUtils.loadAnimation(
                        PersonInfoAddActivity.this,
                        R.anim.activity_translate_in));
                mPopupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0,
                        0);
                break;

            case R.id.ivSpecialPic:
                InputMethodManager mInputMethodManager4 = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                mInputMethodManager4.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
                mTempPhotoCode = 10040;
                mTempCameraCode = 10041;
                tempTakePhotoCode = 8002;
                llPopup.startAnimation(AnimationUtils.loadAnimation(
                        PersonInfoAddActivity.this,
                        R.anim.activity_translate_in));
                mPopupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0,
                        0);
                break;
            case R.id.ivFront:
                InputMethodManager mInputMethodManager3 = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                mInputMethodManager3.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
                mTempPhotoCode = 10020;
                mTempCameraCode = 10021;
                tempTakePhotoCode = 8002;
                llPopup.startAnimation(AnimationUtils.loadAnimation(
                        PersonInfoAddActivity.this,
                        R.anim.activity_translate_in));
                mPopupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0,
                        0);
                break;
            case R.id.ivOpposite:
                InputMethodManager mInputMethodManager2 = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                mInputMethodManager2.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
                mTempPhotoCode = 10030;
                mTempCameraCode = 10031;
                tempTakePhotoCode = 8002;
                llPopup.startAnimation(AnimationUtils.loadAnimation(
                        PersonInfoAddActivity.this,
                        R.anim.activity_translate_in));
                mPopupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0,
                        0);
                break;
            case R.id.ivHeader:
                InputMethodManager mInputMethodManager1 = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                mInputMethodManager1.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
                mTempPhotoCode = 10010;
                mTempCameraCode = 10011;
                tempTakePhotoCode = 8001;
                llPopup.startAnimation(AnimationUtils.loadAnimation(
                        PersonInfoAddActivity.this,
                        R.anim.activity_translate_in));
                mPopupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0,
                        0);
                break;
            case R.id.button:
                AddPersonInfo();
                break;
            case R.id.rbMan:
                rbMan.setChecked(true);
                mSexType = "1";
                break;
            case R.id.rbWoman:
                rbWoman.setChecked(true);
                mSexType = "0";
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
            case R.id.ibRemark:
                if(rlRemark.getVisibility() == View.GONE){
                    rlRemark.setVisibility(View.VISIBLE);
                    llRemarkBlank.setVisibility(View.GONE);
                    ibRemark.setImageDrawable(getResources().getDrawable(R.drawable.ys_list_icon_narrowed));
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
        }
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
                    TLog.log("getUploadImages : " + httpResult);
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
        Bitmap waterBitmap = CommUtil.getInstance().addWaterBitmap(PersonInfoAddActivity.this, bm);
        photoInfo.setBitmap(bm);
        mTempBitMap = photoInfo.getBitmap();
        GetServerImgTempPath(photoInfo.getPhotoPath(), photoInfo.getBitmap());

    }

    private void AddPersonInfo(){

        if(etJobNum.getText().toString().length() < 1){
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


//        TLog.log("realPic "+realPic+"--"+IDFrontPic+"--"+IDOPpositePic+"--"+specialPic+"--"+safePic);

        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        JSONObject secondData = new JSONObject();
        JSONObject person = new JSONObject();
        JSONObject devicePerson = new JSONObject();
        JSONObject projectPerson = new JSONObject();

        try {

            person.put("empName", etName.getText().toString());
            person.put("empPhone", etPhone.getText().toString());
            person.put("idCode", etIDNo.getText().toString());
            person.put("sex", mSexType);
            person.put("empNativeplace", etIDAddr.getText().toString());
            person.put("idValiddate", etIDEnddate.getText().toString()+"-"+etIDEndtime.getText().toString());
            person.put("idAgency", etIDSign.getText().toString());
            person.put("idphotoScan", IDFrontPic);
            person.put("idphotoScan2", IDOPpositePic);

            devicePerson.put("certificateCode", etSpecialNo.getText().toString());
            devicePerson.put("certificateValidDate", etSpecialEnddate.getText().toString());
            devicePerson.put("safetyAssessCode", etSafeNo.getText().toString());
            devicePerson.put("safetyAssessPhoto", safePic);
            devicePerson.put("specialCertificatePhoto", specialPic);
            devicePerson.put("remark", etRemark.getText().toString());

            projectPerson.put("jobNameId", mSpecialTypeId);
            projectPerson.put("jobTypenameId", mPersonTypeId);
            projectPerson.put("empWorkCode", etJobNum.getText().toString());
            projectPerson.put("facephoto", realPic);
            projectPerson.put("projectId", mProjectId);

            secondData.put("person", person);
            secondData.put("devicePerson", devicePerson);
            secondData.put("projectPerson", projectPerson);
            pData.put("params", secondData);

            entity = new StringEntity(pData.toString(), "UTF-8");
            submitToServer(entity);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void submitToServer(HttpEntity entity){

        MainApi.requestCommon(this, AppConfig.ADD_PERSON_INFO, entity, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String httpResult = new String(bytes);
                TLog.log("addPerson"+httpResult);
                try {
                    JSONObject obj = new JSONObject(httpResult);
                    if(StringUtils.equals(obj.getString("code"), SysCode.SUCCESS)){
                        mBasicBus.post(SysCode.MSG_PERSON_ADD);
                        finish();
                        AppContext.showToast(R.string.add_person_success);
                    }else{
                        AppContext.showToast(R.string.add_person_fail);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    AppContext.showToast(R.string.add_person_fail);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                AppContext.showToast(R.string.add_person_fail);
            }
        });



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
        System.out.println("info -->"+new Gson().toJson(personEntity));
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

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "请在应用管理中打开“相机”访问权限！", Toast.LENGTH_LONG).show();
                setResult(0);
                finish();
            }
        }
    }
}
