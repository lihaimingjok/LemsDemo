package com.pcjz.lems.ui.workrealname.manageequipment;

import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.entity.SelectEntity;
import com.pcjz.lems.ui.workrealname.projectsituation.bean.LineEntity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * created by yezhengyu on 2017/8/29 11:18
 */

public class CommonMethond {

    private static CommonMethond instance = null;

    public synchronized static CommonMethond getInstance() {
        if (instance == null) {
            instance = new CommonMethond();
        }
        return instance;
    }

    public Calendar mStartDate;
    public Calendar mEndDate;

    public CommonMethond() {
        mStartDate = Calendar.getInstance();
        mStartDate.set(2013, 2, 21);
        //系统当前时间
        mEndDate = Calendar.getInstance();
    }

    public String initStartTime() {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(new Date());
        startDate.add(Calendar.MONTH, -1);
        return getDate(startDate);
    }

    public String initTime() {
        return getDate(mEndDate);
    }

    public String getDate(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(calendar.getTime());
        return dateStr;
    }

    public Date initBeforeDateSix() {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(new Date());
        startDate.add(Calendar.DATE, -6);
        Date date = startDate.getTime();
        return date;
    }

    public String initBeforeSix() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(new Date());
        startDate.add(Calendar.DATE, -6);
        Date d = startDate.getTime();
        String day = sdf.format(d);
        return day;
    }

    public List<String> initAreaTime() {
        List<String> dates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar startDate = Calendar.getInstance();
        for (int i = 6; i >= 0; i--) {
            startDate.setTime(new Date());
            startDate.add(Calendar.DATE, -i);
            Date d = startDate.getTime();
            String day = sdf.format(d);
            dates.add(day);
        }
        return dates;
    }

    public List<LineEntity> initObjectTime() {
        List<LineEntity> devices = new ArrayList<>();
        List<String> initAreaTimes = initAreaTime();
        LineEntity listEntity;
        for (int i = 0; i < initAreaTimes.size(); i++) {
            listEntity = new LineEntity();
            listEntity.setTime(initAreaTimes.get(i));
            devices.add(listEntity);
        }
        return devices;
    }

    public Calendar initCustomeTimer(String time) {
        Calendar selectedDate;
        if (!StringUtils.isEmpty(time)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = sdf.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            selectedDate = Calendar.getInstance();
            selectedDate.setTime(date);
        } else {
            selectedDate = Calendar.getInstance();
        }
        return selectedDate;
    }

    public List<LineEntity> initStartEndObject(Date d, int days) {
        List<LineEntity> devices = new ArrayList<>();
        List<String> initAreaTimes = initStartEndTime(d, days);
        LineEntity listEntity;
        for (int i = 0; i < initAreaTimes.size(); i++) {
            listEntity = new LineEntity();
            listEntity.setTime(initAreaTimes.get(i));
            devices.add(listEntity);
        }
        return devices;
    }

    public List<String> initStartEndTime(Date d, int days) {
        List<String> dates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar now = Calendar.getInstance();
        for (int i = days; i >= 0; i--) {
            now.setTime(d);
            now.set(Calendar.DATE, now.get(Calendar.DATE) - i);
            Date date = now.getTime();
            String day = sdf.format(date);
            dates.add(day);
        }
        return dates;
    }

    /**
     * date2比date1多的天数
     */
    public static int differentDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2)   //同一年
        {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)    //闰年
                {
                    timeDistance += 366;
                } else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2 - day1);
        } else    //不同年
        {
            return day2 - day1;
        }
    }


    public List<SelectEntity> initDeviceTypes() {
        List<SelectEntity> mTypes = new ArrayList<>();
        SelectEntity selectInfo1 = new SelectEntity();
        selectInfo1.setId("1");
        selectInfo1.setName("人脸识别");
        selectInfo1.setSelect(false);
        SelectEntity selectInfo2 = new SelectEntity();
        selectInfo2.setId("2");
        selectInfo2.setName("虹膜识别");
        selectInfo2.setSelect(false);
        SelectEntity selectInfo3 = new SelectEntity();
        selectInfo3.setId("3");
        selectInfo3.setName("指纹识别");
        selectInfo3.setSelect(false);
        SelectEntity selectInfo4 = new SelectEntity();
        selectInfo4.setId("4");
        selectInfo4.setName("掌形识别");
        selectInfo4.setSelect(false);
        SelectEntity selectInfo5 = new SelectEntity();
        selectInfo5.setId("5");
        selectInfo5.setName("身份证识别");
        selectInfo5.setSelect(false);
        SelectEntity selectInfo6 = new SelectEntity();
        selectInfo6.setId("6");
        selectInfo6.setName("实名卡");
        selectInfo6.setSelect(false);
        mTypes.add(selectInfo1);
        mTypes.add(selectInfo2);
        mTypes.add(selectInfo3);
        mTypes.add(selectInfo4);
        mTypes.add(selectInfo5);
        mTypes.add(selectInfo6);
        return mTypes;
    }

    public List<SelectEntity> initDeviceStatus(String type) {
        List<SelectEntity> status = new ArrayList<>();
        SelectEntity selectInfo1 = new SelectEntity();
        SelectEntity selectInfo2 = new SelectEntity();
        SelectEntity selectInfo3 = new SelectEntity();
        if (StringUtils.equals(type, "workMechine")) {
            selectInfo1.setId("0");
            selectInfo1.setName("未使用");
            selectInfo1.setSelect(false);
            selectInfo2.setId("1");
            selectInfo2.setName("启用中");
            selectInfo2.setSelect(false);
            selectInfo3.setId("2");
            selectInfo3.setName("故障中");
            selectInfo3.setSelect(false);
        } else {
            selectInfo1.setId("1");
            selectInfo1.setName("未使用");
            selectInfo1.setSelect(false);
            selectInfo2.setId("2");
            selectInfo2.setName("启用中");
            selectInfo2.setSelect(false);
            selectInfo3.setId("0");
            selectInfo3.setName("已拆除");
            selectInfo3.setSelect(false);
        }
        status.add(selectInfo1);
        status.add(selectInfo2);
        status.add(selectInfo3);
        return status;
    }

    public List<SelectEntity> initSuppilers() {
        List<SelectEntity> suppilers = new ArrayList<>();
        SelectEntity selectInfo1 = new SelectEntity();
        selectInfo1.setId("ZK");
        selectInfo1.setName("中控");
        selectInfo1.setSelect(false);
        SelectEntity selectInfo2 = new SelectEntity();
        selectInfo2.setId("HW");
        selectInfo2.setName("汉王");
        selectInfo2.setSelect(false);
        suppilers.add(selectInfo1);
        suppilers.add(selectInfo2);
        return suppilers;
    }

    public int getIntMode(int maxvalue) {
        int mode;
        if (maxvalue > 0 && maxvalue <= 10) {
            mode = 1;
        } else if (maxvalue > 10 && maxvalue <= 20) {
            mode = 2;
        } else if (maxvalue > 20 && maxvalue <= 30) {
            mode = 3;
        } else if (maxvalue > 30 && maxvalue <= 40) {
            mode = 4;
        } else if (maxvalue > 40 && maxvalue <= 50) {
            mode = 5;
        } else if (maxvalue > 50 && maxvalue <= 60) {
            mode = 6;
        } else if (maxvalue > 60 && maxvalue <= 70) {
            mode = 7;
        } else if (maxvalue > 70 && maxvalue <= 80) {
            mode = 8;
        } else if (maxvalue > 80 && maxvalue <= 90) {
            mode = 9;
        } else {
            mode = 20;
        }
        return mode;
    }
}
