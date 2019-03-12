package com.pcjz.lems.ui.workrealname.personinfo;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseActivity;
import com.pcjz.lems.business.entity.person.PersonEntity;
import com.pcjz.lems.business.model.personInfo.OnPersonInfoListener;
import com.pcjz.lems.business.model.personInfo.PersonInfoModel;
import com.pcjz.lems.business.model.personInfo.PersonInfoModelImpl;
import com.pcjz.lems.business.volley.VolleyRequest;
import com.pcjz.lems.business.widget.calendarPlugin.MonthlyFragment;
import com.pcjz.lems.business.widget.calendarPlugin.OneDayView;
import com.pcjz.lems.business.widget.calendarPlugin.OneMonthView;

import org.w3c.dom.Text;

import java.util.Calendar;

/**
 * Created by Greak on 2017/9/13.
 */

public class PersonInfoActivity extends BaseActivity implements OnPersonInfoListener{

    private View parentView;
    private TextView tvTitle, tvDate;
    private RelativeLayout rlTitle;
    private PersonInfoModel personInfoModel;
    private Button mBtn, mBtnDate;
    private int curYear;

    private PopupWindow mPopupCalendar;
    private LinearLayout llPopup;
    private View cancleViewBtn;
    private OneDayView preOneDayView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setView() {
        parentView = getLayoutInflater().inflate(
                R.layout.activity_personinfo, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText("特种人员信息");
        personInfoModel = new PersonInfoModelImpl();


        View view = getLayoutInflater().inflate(R.layout.calendar_popwindow,
                null);
        llPopup = (LinearLayout) view.findViewById(R.id.ll_popup);
        tvDate = (TextView) view.findViewById(R.id.tvSelectedDate);
        mBtnDate = (Button) view.findViewById(R.id.btnSureDate);
        cancleViewBtn = view.findViewById(R.id.cancleViewBtn);
        mBtnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupCalendar.dismiss();
                llPopup.clearAnimation();
            }
        });
        cancleViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupCalendar.dismiss();
                llPopup.clearAnimation();
            }
        });
        mPopupCalendar = new PopupWindow();
        mPopupCalendar.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupCalendar.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupCalendar.setBackgroundDrawable(new BitmapDrawable());
        mPopupCalendar.setFocusable(true);
        mPopupCalendar.setOutsideTouchable(true);
        mPopupCalendar.setContentView(view);

        mBtn = (Button) findViewById(R.id.button);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llPopup.startAnimation(AnimationUtils.loadAnimation(
                        PersonInfoActivity.this,
                        R.anim.activity_translate_in));
                mPopupCalendar.showAtLocation(parentView, Gravity.BOTTOM, 0,
                        0);
            }
        });

        MonthlyFragment mf = (MonthlyFragment) getSupportFragmentManager().findFragmentById(R.id.monthly);
        mf.setOnMonthChangeListener(new MonthlyFragment.OnMonthChangeListener() {

            @Override
            public void onChange(int year, int month) {
                curYear = year;
                tvDate.setText(year + "-" + (month + 1) + "-" + "01");
            }

            @Override
            public void onDayClick(OneDayView dayView) {

                tvDate.setText(curYear + "-" + (dayView.get(Calendar.MONTH) + 1) + "-" + dayView.get(Calendar.DAY_OF_MONTH));
                dayView.setSelectedBg(dayView.get(Calendar.DAY_OF_MONTH), preOneDayView, dayView);
                preOneDayView = dayView;

            }




        });
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
}
