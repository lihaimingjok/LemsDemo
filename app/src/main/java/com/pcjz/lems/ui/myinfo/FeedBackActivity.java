package com.pcjz.lems.ui.myinfo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pcjz.lems.R;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.base.basebean.Base;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.webapi.MainApi;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * created by yezhengyu on 2017/4/11.
 */

public class FeedBackActivity extends Activity {
    private EditText etContent;
    private Button btCommit;
    private RelativeLayout mRlBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        etContent = (EditText) findViewById(R.id.et_content);
        btCommit = (Button) findViewById(R.id.bt_commit);
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(R.string.feedback);
        mRlBack = (RelativeLayout) findViewById(R.id.rl_back);

        btCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isEmpty(etContent.getText().toString())) {
                    AppContext.showToast(R.string.feekback_content_no_null);
                    return;
                }
                submitFeedBack();
            }
        });
        mRlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                finish();
            }
        });
    }

    private void submitFeedBack() {
        HttpEntity entity = null;
        JSONObject obj = new JSONObject();
        try {
            JSONObject obj0 = new JSONObject();
            obj.put("feedbackType", "1");
            obj.put("feedbackContent", etContent.getText().toString());
            obj0.put("params", obj);
            String s = obj0.toString();
            entity = new StringEntity(s, "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        MainApi.requestCommon(this, AppConfig.SUBMIT_FEEDBACK_URL, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                try {
                    String httpResult = new String(bytes);
                    TLog.log("submitFeedBack : " + httpResult);
                    Base base = new Gson().fromJson(httpResult, Base.class);
                    if (StringUtils.equals(base.getCode(), ResultStatus.SUCCESS)) {
                        AppContext.showToast(R.string.feedback_submit_success);
                        closeKeyboard();
                        finish();
                    } else {
                        AppContext.showToast(base.getMsg());
                        finish();
                    }
                    return;
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                AppContext.showToast(R.string.feedback_content_submit_error);
                finish();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                AppContext.showToast(R.string.feedback_content_submit_error);
            }
        });
    }
    private void closeKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
