package com.pcjz.lems.ui.workrealname.personinfo;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseActivity;

import org.w3c.dom.Text;

/**
 * Created by Greak on 2017/9/20.
 */

public class PersonInfoPassActivity extends BaseActivity implements View.OnClickListener{
    private TextView tvTitle, tvReason;
    private ImageButton ibSafe, ibRemark;
    private LinearLayout rlSafe, rlRemark;

    @Override
    protected void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvReason = (TextView) findViewById(R.id.tvReason);
        ibSafe = (ImageButton) findViewById(R.id.ibSafe);
        ibRemark = (ImageButton) findViewById(R.id.ibRemark);
        rlSafe = (LinearLayout) findViewById(R.id.rlSafe);
        rlRemark = (LinearLayout) findViewById(R.id.rlRemark);
        tvTitle.setText("特种人员信息");
        tvReason.setText("审核不通过，头像不够清晰！");
        ibSafe.setOnClickListener(PersonInfoPassActivity.this);
        ibRemark.setOnClickListener(PersonInfoPassActivity.this);
    }

    @Override
    public void setView() {
        setContentView(R.layout.activity_person_pass);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {

            case R.id.ibRemark:
                if(rlRemark.getVisibility() == View.GONE){
                    rlRemark.setVisibility(View.VISIBLE);
                    ibRemark.setImageDrawable(getResources().getDrawable(R.drawable.ys_list_icon_narrowed));
                }else{
                    rlRemark.setVisibility(View.GONE);
                    ibRemark.setImageDrawable(getResources().getDrawable(R.drawable.ys_list_icon_pack_up));
                }
                break;
            case R.id.ibSafe:
                if(rlSafe.getVisibility() == View.GONE){
                    rlSafe.setVisibility(View.VISIBLE);
                    ibSafe.setImageDrawable(getResources().getDrawable(R.drawable.ys_list_icon_narrowed));
                }else{
                    rlSafe.setVisibility(View.GONE);
                    ibSafe.setImageDrawable(getResources().getDrawable(R.drawable.ys_list_icon_pack_up));
                }
                break;
        }
    }
}
