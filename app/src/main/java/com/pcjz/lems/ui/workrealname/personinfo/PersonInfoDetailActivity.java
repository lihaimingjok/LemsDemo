package com.pcjz.lems.ui.workrealname.personinfo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseActivity;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.webapi.MainApi;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Greak on 2017/9/19.
 */

public class PersonInfoDetailActivity extends BaseActivity implements View.OnClickListener {

    private DisplayImageOptions mOption = new DisplayImageOptions.Builder()
            .showStubImage(R.color.white)
            .showImageForEmptyUri(R.color.white)
            .showImageOnFail(R.color.white).cacheInMemory(true)
            .cacheOnDisc(false).bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY).build();


    private ScrollView slView;
    private RelativeLayout rlReason;
    private TextView tvTitle, tvMod, tvReason;
    private ImageButton ibSafe, ibRemark;
    private LinearLayout rlSafe, rlRemark, llRemarkBlank, llSafeBlank;
    private String mPid;
    private String passState;

    private TextView tvWorkNum, tvName, tvPhone, tvSex, tvPersonType, tvIDNum, tvIDEndTime, tvIDSign,
            tvIDAddr, tvSpecialType, tvSpecialEndDate, tvSpecialNum, tvSafeNum, tvRemark;
    private ImageView ivRealPic, ivFront, ivOpposite, ivSpecialPic, ivSafePic;
    private String strRealPic, strFront, strOpposite, strSpecicalPic, strSafePic;

    @Override
    protected void initView() {

        strRealPic = "";
        strFront = "";
        strOpposite = "";
        strSpecicalPic  = "";
        strSafePic  = "";
        passState = getIntent().getStringExtra("pstate");
        if (StringUtils.equals(passState, "8001")) {
            setContentView(R.layout.activity_person_pass);
        } else if(StringUtils.equals(passState, "8003")){
            setContentView(R.layout.activity_person_pass);
        }else{
            setContentView(R.layout.activity_person_detail);
        }


        slView = (ScrollView) findViewById(R.id.slView);
        rlReason = (RelativeLayout) findViewById(R.id.rlReason);

        tvReason = (TextView) findViewById(R.id.tvReason);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvMod = (TextView) findViewById(R.id.tv_titlebar_right);
        ibSafe = (ImageButton) findViewById(R.id.ibSafe);
        ibRemark = (ImageButton) findViewById(R.id.ibRemark);
        rlSafe = (LinearLayout) findViewById(R.id.rlSafe);
        rlRemark = (LinearLayout) findViewById(R.id.rlRemark);
        tvTitle.setText("人员信息");

        tvWorkNum = (TextView) findViewById(R.id.tvWorkNum);
        tvName = (TextView) findViewById(R.id.tvName);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvSex = (TextView) findViewById(R.id.tvSex);
        tvPersonType = (TextView) findViewById(R.id.tvPersonType);
        tvIDNum = (TextView) findViewById(R.id.tvIDNum);
        tvIDEndTime = (TextView) findViewById(R.id.tvIDEndTime);
        tvIDSign = (TextView) findViewById(R.id.tvIDSign);
        tvIDAddr = (TextView) findViewById(R.id.tvIDAddr);
        tvSpecialType = (TextView) findViewById(R.id.tvSpecialType);
        tvSpecialEndDate = (TextView) findViewById(R.id.tvSpecialEndDate);
        tvSpecialNum = (TextView) findViewById(R.id.tvSpecialNum);
        tvSafeNum = (TextView) findViewById(R.id.tvSafeNum);
        tvRemark = (TextView) findViewById(R.id.tvRemark);

        ivRealPic = (ImageView) findViewById(R.id.ivRealPic);
        ivFront = (ImageView) findViewById(R.id.ivFront);
        ivOpposite = (ImageView) findViewById(R.id.ivOpposite);
        ivSpecialPic = (ImageView) findViewById(R.id.ivSpecialPic);
        ivSafePic = (ImageView) findViewById(R.id.ivSafePic);

        llSafeBlank = (LinearLayout) findViewById(R.id.llSafeBlank);
        llRemarkBlank = (LinearLayout) findViewById(R.id.llRemarkBlank);

        ivRealPic.setOnClickListener(PersonInfoDetailActivity.this);
        ivFront.setOnClickListener(PersonInfoDetailActivity.this);
        ivOpposite.setOnClickListener(PersonInfoDetailActivity.this);
        ivSpecialPic.setOnClickListener(PersonInfoDetailActivity.this);
        ivSafePic.setOnClickListener(PersonInfoDetailActivity.this);

        tvMod.setOnClickListener(PersonInfoDetailActivity.this);
        ibSafe.setOnClickListener(PersonInfoDetailActivity.this);
        ibRemark.setOnClickListener(PersonInfoDetailActivity.this);
        mPid = getIntent().getStringExtra("pid");

        if (StringUtils.equals(passState, "8001")) {
            tvMod.setText("修改");
            rlReason.setVisibility(View.GONE);

        } else if (StringUtils.equals(passState, "8002")) {
            /*int padTop = dip2px(this, 85);
            slView.setTop(padTop);*/
            rlReason.setVisibility(View.VISIBLE);
            tvMod.setText("");
        } else {
            rlReason.setVisibility(View.GONE);
            tvMod.setText("");
        }

        initPersonInfo();
    }


    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPersonInfo();
    }

    private void initPersonInfo() {


        String GET_INFO_URL = "";
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
                TLog.log("personInfo -->"+httpResult);
                try {
                    JSONObject obj = new JSONObject(httpResult);
                    if (StringUtils.equals(obj.getString("code"), SysCode.SUCCESS)) {
                        JSONObject person = obj.getJSONObject("data").getJSONObject("person");
                        JSONObject device = obj.getJSONObject("data").getJSONObject("devicePerson");
                        JSONObject proPerson = obj.getJSONObject("data").getJSONObject("projectPerson");

                        if (!proPerson.isNull("empWorkCode")) {
                            tvWorkNum.setText(proPerson.getString("empWorkCode"));
                        }
                        if (!person.isNull("empName")) {
                            tvName.setText(person.getString("empName"));
                        }
                        if (!person.isNull("empPhone")) {
                            tvPhone.setText(person.getString("empPhone"));
                        }
                        if (!person.isNull("sex")) {
                            if (StringUtils.equals(person.getString("sex"), "1")) {
                                tvSex.setText("男");
                            } else {
                                tvSex.setText("女");
                            }
                        }
                        if (!proPerson.isNull("jobTypename")) {
                            tvPersonType.setText(proPerson.getString("jobTypename"));
                            if(proPerson.getString("jobTypename").equals(AppConfig.PERSON_TYPE)){
                                tvTitle.setText("特种人员信息");
                            }else{

                            }
                        }
                        if (!person.isNull("idCode")) {
                            tvIDNum.setText(person.getString("idCode"));
                        }
                        if (!person.isNull("idValiddate")) {


                            if(person.getString("idValiddate").indexOf(AppConfig.VALID_ID_DATE) != -1){
                                tvIDEndTime.setText(person.getString("idValiddate").substring(0, 10)+"-长期");
                            }else{
                                String date = person.getString("idValiddate").replace("-", ".");
                                if(date.length() > 10){
                                    tvIDEndTime.setText(date.substring(0, 10)+"-"+date.substring(11, 21));
                                }else{
                                    date = date + "-0000.00.00";
                                    tvIDEndTime.setText(date.substring(0, 10)+"-"+date.substring(11, 21));
                                }
                            }

                        }
                        if (!person.isNull("idAgency")) {
                            tvIDSign.setText(person.getString("idAgency"));
                        }
                        if (!person.isNull("empNativeplace")) {
                            tvIDAddr.setText(person.getString("empNativeplace"));
                        }
                        if (!proPerson.isNull("jobName")) {
                            tvSpecialType.setText(proPerson.getString("jobName"));
                        }
                        if (!device.isNull("certificateValidDate")) {
                            tvSpecialEndDate.setText(device.getString("certificateValidDate").substring(0, 10));
                        }
                        if (!device.isNull("certificateCode")) {
                            tvSpecialNum.setText(device.getString("certificateCode"));
                        }
                        if (!device.isNull("safetyAssessCode")) {
                            tvSafeNum.setText(device.getString("safetyAssessCode"));
                        }
                        if (!device.isNull("remark")) {
                            tvRemark.setText(device.getString("remark"));
                        }
                        if (!proPerson.isNull("auditRemark")) {
                            tvReason.setText(proPerson.getString("auditRemark"));
                        }

                        if (!proPerson.isNull("facephoto")) {
                            strRealPic = proPerson.getString("facephoto");
                            ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + proPerson.getString("facephoto"), ivRealPic);
                        }
                        if (!person.isNull("idphotoScan")) {
                            strFront = person.getString("idphotoScan");
                            ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + person.getString("idphotoScan"), ivFront);
                        }
                        if (!person.isNull("idphotoScan2")) {
                            strOpposite = person.getString("idphotoScan2");
                            ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + person.getString("idphotoScan2"), ivOpposite);
                        }
                        if (!device.isNull("specialCertificatePhoto")) {
                            strSpecicalPic = device.getString("specialCertificatePhoto");
                            ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + device.getString("specialCertificatePhoto"), ivSpecialPic);
                        }
                        if (!device.isNull("safetyAssessPhoto")) {
                            strSafePic = device.getString("safetyAssessPhoto");
                            ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + device.getString("safetyAssessPhoto"), ivSafePic);
                        }

                    } else {

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

    @Override
    public void setView() {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tv_titlebar_right:
                Intent intent = new Intent(PersonInfoDetailActivity.this, PersonInfoCheckActivity.class);
                intent.putExtra("pid", mPid);
                intent.putExtra("mState", "1001");
                startActivity(intent);
                break;
            case R.id.ibRemark:
                if (rlRemark.getVisibility() == View.GONE) {
                    rlRemark.setVisibility(View.VISIBLE);
                    llRemarkBlank.setVisibility(View.GONE);
                    ibRemark.setImageDrawable(getResources().getDrawable(R.drawable.ys_list_icon_narrowed));
                } else {
                    rlRemark.setVisibility(View.GONE);
                    llRemarkBlank.setVisibility(View.VISIBLE);
                    ibRemark.setImageDrawable(getResources().getDrawable(R.drawable.ys_list_icon_pack_up));
                }
                break;
            case R.id.ibSafe:
                if (rlSafe.getVisibility() == View.GONE) {
                    rlSafe.setVisibility(View.VISIBLE);
                    llSafeBlank.setVisibility(View.GONE);
                    ibSafe.setImageDrawable(getResources().getDrawable(R.drawable.ys_list_icon_narrowed));
                } else {
                    rlSafe.setVisibility(View.GONE);
                    llSafeBlank.setVisibility(View.VISIBLE);
                    ibSafe.setImageDrawable(getResources().getDrawable(R.drawable.ys_list_icon_pack_up));
                }
                break;
            case R.id.ivRealPic:
                if(strRealPic.length() > 0){
                    ArrayList<String> photos = new ArrayList<>();
                    Intent intent1 = new Intent(PersonInfoDetailActivity.this, ShowImageActivity.class);
                    intent1.putExtra("photoPath", strRealPic);
                    startActivity(intent1);
                }else{
                    AppContext.showToast(R.string.person_no_img);
                }

                break;
            case R.id.ivFront:
                if(strFront.length() > 0){
                    Intent intent2 = new Intent(PersonInfoDetailActivity.this, ShowImageActivity.class);
                    intent2.putExtra("photoPath", strFront);
                    startActivity(intent2);
                }else{
                    AppContext.showToast(R.string.person_no_img);
                }

                break;
            case R.id.ivOpposite:
                if(strOpposite.length() > 0){
                    Intent intent3 = new Intent(PersonInfoDetailActivity.this, ShowImageActivity.class);
                    intent3.putExtra("photoPath", strOpposite);
                    startActivity(intent3);
                }else{
                    AppContext.showToast(R.string.person_no_img);
                }

                break;
            case R.id.ivSpecialPic:
                if(strSpecicalPic.length() > 0){
                    Intent intent4 = new Intent(PersonInfoDetailActivity.this, ShowImageActivity.class);
                    intent4.putExtra("photoPath", strSpecicalPic);
                    startActivity(intent4);
                }else{
                    AppContext.showToast(R.string.person_no_img);
                }

                break;
            case R.id.ivSafePic:
                if(strSafePic.length() > 0){
                    Intent intent5 = new Intent(PersonInfoDetailActivity.this, ShowImageActivity.class);
                    intent5.putExtra("photoPath", strSafePic);
                    startActivity(intent5);
                }else{
                    AppContext.showToast(R.string.person_no_img);
                }

                break;
        }
    }
}
