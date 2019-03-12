package com.pcjz.lems.ui.workrealname.projectsituation.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.base.MyBaseAdapter;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.ui.workrealname.projectsituation.bean.DeviceDriverEntity;
import com.pcjz.lems.ui.workrealname.projectsituation.bean.DeviceEntity;
import com.pcjz.lems.ui.workrealname.projectsituation.bean.RelateMachineEntity;

import java.util.List;

/**
 * Created by Greak on 2017/9/21.
 */

public class RealtimeSituationAdapter extends MyBaseAdapter{

    private DeviceEntity mDeviceEntity;
    private String running_color = "#A7CF5E";
    private String norun_color = "#5BC0EC";
    private String runned_color = "#B38979";
    private String nouse_color = "#FDC25B";
    private String breaken_color = "#FF6A50";

    private Context mContext;

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        mContext = parent.getContext();
        ViewHolder holder = null;
        /*if(convertView == null || convertView.getTag() == null){
            convertView = getLayoutInflater(parent.getContext()).inflate( R.layout.item_realtime_situation, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }*/

        /*if(_data != null && _data.size() > 0){
            DeviceEntity deviceEntity = (DeviceEntity) _data.get(position);

            holder.tvDeviceName.setText(deviceEntity.getDevName());

            if(deviceEntity.getTotalTimes().equals("0")){
                holder.tvDeviceState.setText("未使用");
                holder.tvDeviceState.setTextColor(Color.parseColor(nouse_color));
                if(deviceEntity.getDevType().equals(ResultStatus.DEVICE_TYPE)){
                    holder.ivDevice.setImageResource(R.drawable.equipment_crane_icon_03);
                }else{
                    holder.ivDevice.setImageResource(R.drawable.equipment_elevator_icon_03);
                }
            }else{
                if(deviceEntity.getDevState().equals("0")){
                    holder.tvDeviceState.setText("已拆除");
                    holder.tvDeviceState.setTextColor(Color.parseColor(breaken_color));
                    if(deviceEntity.getDevType().equals(ResultStatus.DEVICE_TYPE)){
                        holder.ivDevice.setImageResource(R.drawable.equipment_crane_icon_04);
                    }else{
                        holder.ivDevice.setImageResource(R.drawable.equipment_elevator_icon_04);
                    }
                }else{
                    if(deviceEntity.getDevCurState().equals("0")){
                        holder.tvDeviceState.setText("未运行");
                        holder.tvDeviceState.setTextColor(Color.parseColor(norun_color));
                        if(deviceEntity.getDevType().equals(ResultStatus.DEVICE_TYPE)){
                            holder.ivDevice.setImageResource(R.drawable.equipment_crane_icon_02);
                        }else{
                            holder.ivDevice.setImageResource(R.drawable.equipment_elevator_icon_02);
                        }
                    }else{
                        holder.tvDeviceState.setText("运行中");
                        holder.tvDeviceState.setTextColor(Color.parseColor(running_color));
                        if(deviceEntity.getDevType().equals(ResultStatus.DEVICE_TYPE)){
                            holder.ivDevice.setImageResource(R.drawable.equipment_crane_icon_01);
                        }else{
                            holder.ivDevice.setImageResource(R.drawable.equipment_elevator_icon_01);
                        }
                        *//*if(deviceEntity.getTodayUseTimes().equals("0")){
                            holder.tvDeviceState.setText("运行中");
                            holder.tvDeviceState.setTextColor(Color.parseColor(running_color));
                            if(deviceEntity.getDevType().equals(ResultStatus.DEVICE_TYPE)){
                                holder.ivDevice.setImageResource(R.drawable.equipment_crane_icon_01);
                            }else{
                                holder.ivDevice.setImageResource(R.drawable.equipment_elevator_icon_01);
                            }
                        }else{
                            holder.tvDeviceState.setText("已运行");
                            holder.tvDeviceState.setTextColor(Color.parseColor(running_color));
                            if(deviceEntity.getDevType().equals(ResultStatus.DEVICE_TYPE)){
                                holder.ivDevice.setImageResource(R.drawable.equipment_crane_icon_05);
                            }else{
                                holder.ivDevice.setImageResource(R.drawable.equipment_elevator_icon_05);
                            }
                        }*//*

                    }
                }
            }
            holder.rlRelateCnt.addView(addRelateMachine(deviceEntity.getmRelateMachineList()));
            holder.rlDriverCnt.addView(addDriversList(deviceEntity.getMdevDrvList()));

        }*/

        convertView = getLayoutInflater(parent.getContext()).inflate( R.layout.item_realtime_situation, null);

        if(_data != null && _data.size() > 0){
            LinearLayout llItemRealtime = (LinearLayout) convertView.findViewById(R.id.llItemRealtime);


            DeviceEntity deviceEntity = (DeviceEntity) _data.get(position);
            LinearLayout rlDriverCnt = (LinearLayout) convertView.findViewById(R.id.rl_driver);
            LinearLayout rlRelateCnt = (LinearLayout) convertView.findViewById(R.id.rl_relate);
            ImageView ivDevice = (ImageView) convertView.findViewById(R.id.ivDevice);
            TextView tvDeviceName = (TextView) convertView.findViewById(R.id.tvDevName);
            TextView tvDeviceState = (TextView) convertView.findViewById(R.id.tvDevState);
            tvDeviceName.setText(deviceEntity.getDevName());



            if(deviceEntity.getmRelateMachineList().size()  > 5 || deviceEntity.getMdevDrvList().size() > 5){

            }else{
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        dp2px(mContext, 100));
                llItemRealtime.setLayoutParams(lp);
            }



            if(deviceEntity.getDevState().equals("0")){
                tvDeviceState.setText("已拆除");
                tvDeviceName.setTextColor(Color.parseColor(breaken_color));
                tvDeviceState.setTextColor(Color.parseColor(breaken_color));
                if(deviceEntity.getDevType().equals(ResultStatus.DEVICE_TYPE)){
                    ivDevice.setImageResource(R.drawable.equipment_crane_icon_04);
                }else{
                    ivDevice.setImageResource(R.drawable.equipment_elevator_icon_04);
                }
            }else if(deviceEntity.getDevState().equals("1")) {
                tvDeviceState.setText("未使用");
                tvDeviceName.setTextColor(Color.parseColor(nouse_color));
                tvDeviceState.setTextColor(Color.parseColor(nouse_color));
                if(deviceEntity.getDevType().equals(ResultStatus.DEVICE_TYPE)){
                    ivDevice.setImageResource(R.drawable.equipment_crane_icon_03);
                }else{
                    ivDevice.setImageResource(R.drawable.equipment_elevator_icon_03);
                }
            }else {
                    if(deviceEntity.getDevCurState().equals("1")){

                        tvDeviceState.setText("运行中");
                        tvDeviceName.setTextColor(Color.parseColor(running_color));
                        tvDeviceState.setTextColor(Color.parseColor(running_color));
                        if(deviceEntity.getDevType().equals(ResultStatus.DEVICE_TYPE)){
                            ivDevice.setImageResource(R.drawable.equipment_crane_icon_01);
                        }else{
                            ivDevice.setImageResource(R.drawable.equipment_elevator_icon_01);
                        }

                    }else{
                        int tempTimes = Integer.parseInt(deviceEntity.getTodayUseTimes());
                        if(tempTimes > 0){

                            /*tvDeviceState.setText("已运行");
                            tvDeviceName.setTextColor(Color.parseColor(runned_color));
                            tvDeviceState.setTextColor(Color.parseColor(runned_color));
                            if(deviceEntity.getDevType().equals(ResultStatus.DEVICE_TYPE)){
                                ivDevice.setImageResource(R.drawable.equipment_crane_icon_05);
                            }else{
                                ivDevice.setImageResource(R.drawable.equipment_elevator_icon_05);
                            }*/

                            tvDeviceState.setText("未运行");
                            tvDeviceName.setTextColor(Color.parseColor(norun_color));
                            tvDeviceState.setTextColor(Color.parseColor(norun_color));
                            if(deviceEntity.getDevType().equals(ResultStatus.DEVICE_TYPE)){
                                ivDevice.setImageResource(R.drawable.equipment_crane_icon_02);
                            }else{
                                ivDevice.setImageResource(R.drawable.equipment_elevator_icon_02);
                            }
                        }else{
                            tvDeviceState.setText("未运行");
                            tvDeviceName.setTextColor(Color.parseColor(norun_color));
                            tvDeviceState.setTextColor(Color.parseColor(norun_color));
                            if(deviceEntity.getDevType().equals(ResultStatus.DEVICE_TYPE)){
                                ivDevice.setImageResource(R.drawable.equipment_crane_icon_02);
                            }else{
                                ivDevice.setImageResource(R.drawable.equipment_elevator_icon_02);
                            }
                        }


                    }
                }


            rlRelateCnt.addView(addRelateMachine(deviceEntity.getmRelateMachineList()));
            rlDriverCnt.addView(addDriversList(deviceEntity.getMdevDrvList()));

        }
        return convertView;
    }

    private View addRelateMachine(List<RelateMachineEntity> mList){
        LinearLayout llView = new LinearLayout(mContext);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llView.setOrientation(LinearLayout.VERTICAL);

        llView.setLayoutParams(lp);
        RelateMachineEntity mRelateMachineEntity = null;
        if(mList.size() > 0){
            for(int i =0; i < mList.size(); i ++){
                mRelateMachineEntity = new RelateMachineEntity();
                mRelateMachineEntity = mList.get(i);
                TextView tvDrivers = new TextView(mContext);
                tvDrivers.setTextColor(Color.parseColor("#333333"));
                tvDrivers.setTextSize(12);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, dp2px(mContext, 20));
                tvDrivers.setText(mRelateMachineEntity.getRelateMacName());
                tvDrivers.setPadding(0, 0, 0, 0);
                tvDrivers.setMaxLines(1);
                tvDrivers.setEllipsize(TextUtils.TruncateAt.END);
                tvDrivers.setGravity(Gravity.CENTER);
                tvDrivers.setLayoutParams(params);
                llView.addView(tvDrivers);
            }
            /*if(mList.size() == 1){
                llView.setPadding(0, dp2px(mContext, 40), 0, 0);

            }else if(mList.size() == 2){
                llView.setPadding(0, dp2px(mContext, 25), 0, 0);
            }else if(mList.size() == 3){
                llView.setPadding(0, dp2px(mContext, 20), 0, 0);
            }else{

            }*/

        }


        return llView;
    }

    private View addDriversList(List<DeviceDriverEntity> mList){
        LinearLayout llView = new LinearLayout(mContext);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llView.setOrientation(LinearLayout.VERTICAL);
        llView.setLayoutParams(lp);
        DeviceDriverEntity devDriverEntity = null;
        if(mList.size() > 0){
            for(int i =0; i < mList.size(); i ++){
                devDriverEntity = new DeviceDriverEntity();
                devDriverEntity = mList.get(i);
                TextView tvDrivers = new TextView(mContext);
                tvDrivers.setTextColor(Color.parseColor("#333333"));
                tvDrivers.setTextSize(12);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, dp2px(mContext, 20));
                if(devDriverEntity.getDrvSate().equals("0")){
                    tvDrivers.setText(devDriverEntity.getDrvName());
                }else{
                    tvDrivers.setText(devDriverEntity.getDrvName()+"（已上岗）");
                }
                tvDrivers.setPadding(0, 0, 0, 0);
                tvDrivers.setMaxLines(1);
                tvDrivers.setEllipsize(TextUtils.TruncateAt.END);
                tvDrivers.setGravity(Gravity.CENTER_VERTICAL);
                tvDrivers.setLayoutParams(params);
                llView.addView(tvDrivers);
            }
            /*if(mList.size() == 1){
                llView.setPadding(0, dp2px(mContext, 40), 0, 0);

            }else if(mList.size() == 2){
                llView.setPadding(0, dp2px(mContext, 25), 0, 0);
            }if(mList.size() == 3){
                llView.setPadding(0, dp2px(mContext, 20), 0, 0);
            }else{

            }*/
        }


        return llView;
    }

    private int dp2px(Context context, float dpValue ) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private  int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public class ViewHolder{
        ImageView ivDevice;
        TextView tvDeviceName, tvRelateDevice, tvDeviceState;
        LinearLayout rlDriverCnt;
        LinearLayout rlRelateCnt;
        public ViewHolder(View view){
            rlDriverCnt = (LinearLayout) view.findViewById(R.id.rl_driver);
            rlRelateCnt = (LinearLayout) view.findViewById(R.id.rl_relate);
            ivDevice = (ImageView) view.findViewById(R.id.ivDevice);
            tvDeviceName = (TextView) view.findViewById(R.id.tvDevName);
            tvRelateDevice = (TextView) view.findViewById(R.id.tvRelateDev);
            tvDeviceState = (TextView) view.findViewById(R.id.tvDevState);
            view.setTag(this);
        }
    }
}
