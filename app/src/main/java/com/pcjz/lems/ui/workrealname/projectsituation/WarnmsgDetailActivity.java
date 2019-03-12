package com.pcjz.lems.ui.workrealname.projectsituation;

import android.view.View;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseActivity;

/**
 * Created by Greak on 2017/9/23.
 */

public class WarnmsgDetailActivity extends BaseActivity implements View.OnClickListener{
    private TextView tvTitle, tvDevName, tvDevContent, tvWarnTime;



    @Override
    protected void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText("预警消息详情");

        tvDevName = (TextView) findViewById(R.id.tvDevName);
        tvDevContent = (TextView) findViewById(R.id.tvDevContent);
        tvWarnTime = (TextView) findViewById(R.id.tvWarnTime);


        tvDevName.setText("预警设备："+getIntent().getStringExtra("wmDev"));
        tvDevContent.setText("预警内容："+getIntent().getStringExtra("wmCnt"));
        tvWarnTime.setText("发送预警时间："+FromatDate(getIntent().getStringExtra("wmTime")));

    }

    private String FromatDate(String time) {
        return time.substring(0, 4) + "年" + time.substring(5, 7) + "月" + time.substring(8, 10) + "日 " +time.substring(11, 19);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void setView() {
        setContentView(R.layout.activity_warnmsg_detail);
    }

    @Override
    public void setListener() {

    }
}
