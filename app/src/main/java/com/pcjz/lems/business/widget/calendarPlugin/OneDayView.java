/*
* Copyright (C) 2015 Hansoo Lab.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.pcjz.lems.business.widget.calendarPlugin;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.pcjz.lems.R;

import java.util.Calendar;

/**
 * View to display a day
 * @author greak
 *
 */
public class OneDayView extends RelativeLayout {
 
    private static final String NAME = "OneDayView";
    private final String CLASS = NAME + "@" + Integer.toHexString(hashCode());
    
    /** number text field */
    private TextView dayTv;
    private LinearLayout dayLayout;
    /** message text field*/
    private TextView msgTv;
    /** Weather icon */
    private ImageView weatherIv;
    /** Value object for a day info */
    private OneDayData one;

    private int preClickNum;

    /**
     * OneDayView constructor
     * @param context context
     */
    public OneDayView(Context context) {
        super(context);
        init(context);
 
    }

    /**
     * OneDayView constructor for xml
     * @param context context
     * @param attrs AttributeSet
     */
    public OneDayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setTextColor(String color){
        dayTv.setTextColor(Color.parseColor(color));
    }

    public void setSelectedBg(int curdate, OneDayView preOneDayView, OneDayView oneDayView){

        if(preOneDayView != null){
            /*if(curdate == 1){
                preOneDayView.setBackgroundResource(R.color.white);
                preOneDayView.setTextColor("#0078D7");
            }else{*/
            /*}*/
            if(preOneDayView.get(Calendar.MONTH) != oneDayView.get(Calendar.MONTH) ){
                preOneDayView.setBackgroundResource(R.color.white);
                preOneDayView.setTextColor("#333333");
            }else{
                preOneDayView.setBackgroundResource(R.color.white);
                preOneDayView.setTextColor("#333333");
            }

            oneDayView.setBackgroundResource(R.drawable.bg_fill_oneday);
            oneDayView.setTextColor("#ffffff");
        }else{
            oneDayView.setBackgroundResource(R.drawable.bg_fill_oneday);
            oneDayView.setTextColor("#ffffff");
        }
    }
 
    private void init(Context context)
    {

        View v = View.inflate(context, R.layout.oneday, this);
        dayLayout = (LinearLayout) v.findViewById(R.id.oneday_topGroup);
        dayTv = (TextView) v.findViewById(R.id.onday_dayTv);
//        msgTv = (TextView) v.findViewById(R.id.onday_msgTv);
        one = new OneDayData();
        
    }
    
    /**
     * Set the day to display
     * @param year 4 digits of year
     * @param month Calendar.JANUARY ~ Calendar.DECEMBER
     * @param day day of month
     */
    public void setDay(int year, int month, int day) {
        this.one.cal.set(year, month, day);
    }

    /**
     * Set the day to display
     * @param cal Calendar instance
     */
    public void setDay(Calendar cal) {
        this.one.setDay((Calendar) cal.clone());
    }

    /**
     * Set the day to display
     * @param one OneDayData instance
     */
    public void setDay(OneDayData one) {
        this.one = one;
    }
    
    /**
     * Get the day to display
     * @return OneDayData instance
     */
    public OneDayData getDay() {
        return one;
    }

    /**
     * Set the message to display
     * @param msg message
     */
    public void setMessage(String msg){
        one.setMessage(msg);
    }

    /**
     * Get the message
     * @return message
     */
    public CharSequence getMessage(){
        return  one.getMessage();
    }

    /**
     * Same function with {@link Calendar#get(int)}<br>
     * <br>
     * Returns the value of the given field after computing the field values by
     * calling {@code complete()} first.
     * 
     * @param field Calendar.YEAR or Calendar.MONTH or Calendar.DAY_OF_MONTH
     *
     * @throws IllegalArgumentException
     *                if the fields are not set, the time is not set, and the
     *                time cannot be computed from the current field values.
     * @throws ArrayIndexOutOfBoundsException
     *                if the field is not inside the range of possible fields.
     *                The range is starting at 0 up to {@code FIELD_COUNT}.
     */
    public int get(int field) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return one.get(field);
    }


    /**
     * Updates UI
     */
    public void refresh(int month) {
        int curMonthDays = one.getDay().getActualMaximum(Calendar.DATE);
        int showDate = one.get(Calendar.DAY_OF_MONTH);
        int curMonth = one.get(Calendar.MONTH);
        dayTv.setGravity(Gravity.CENTER);
        if(month == curMonth){
            if(showDate == 1){
                dayTv.setText( (month + 1) + "月");
                dayTv.setTextColor(Color.parseColor("#38AFF7"));
            }else{
                dayTv.setText(String.valueOf(one.get(Calendar.DAY_OF_MONTH)));
                dayTv.setTextColor(Color.parseColor("#333333"));
            }

        }else{
            dayTv.setText(String.valueOf(one.get(Calendar.DAY_OF_MONTH)));
            dayTv.setTextColor(Color.parseColor("#DCDCDC"));
        }


        
    }
    
}