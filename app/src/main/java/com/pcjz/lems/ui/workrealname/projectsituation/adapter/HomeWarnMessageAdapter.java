package com.pcjz.lems.ui.workrealname.projectsituation.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.base.MyBaseAdapter;
import com.pcjz.lems.ui.workrealname.projectsituation.bean.WarnMsgEntity;

/**
 * Created by Greak on 2017/10/13.
 */

public class HomeWarnMessageAdapter extends MyBaseAdapter{
    private Context mContext;
    private onIDoListener mOnIDoListener;

    @Override
    protected View getRealView(final int position, View convertView, ViewGroup parent) {
        mContext = parent.getContext();
        ViewHolder holder = null;
        if(convertView == null || convertView.getTag() == null){
            convertView = getLayoutInflater(parent.getContext()).inflate( R.layout.activity_warnmsg_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(_data != null && _data.size() > 0){
            WarnMsgEntity mWarnMsgEntity = (WarnMsgEntity) _data.get(position);

            holder.tvWarnDev.setText("预警设备："+mWarnMsgEntity.getWarnDevice());
            holder.tvWarnContent.setText("预警内容："+mWarnMsgEntity.getWarnContent());

            holder.tvWarnTime.setText("发送预警时间："+FromatDate(mWarnMsgEntity.getWarnDate()));
            /*setOnIDoListener(holder, position);*/
            holder.llIDo.setTag(R.id.llIDo);
            holder.llIDo.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    mOnIDoListener.onIDoClick(position);
                }
            });


        }
        return convertView;
    }


    public interface onIDoListener{
        void onIDoClick(int position);
    }

    public void setOnIDoListener(onIDoListener onIDoListener){
        this.mOnIDoListener = onIDoListener;
    }


    private String FromatDate(String time) {
        return time.substring(0, 4) + "年" + time.substring(5, 7) + "月" + time.substring(8, 10) + "日 " +time.substring(11, 19);
    }

    public class ViewHolder{

        TextView tvWarnDev, tvWarnContent, tvWarnPart, tvWarnTime;
        LinearLayout llIDo;

        public ViewHolder(View itemView) {
            llIDo = (LinearLayout) itemView.findViewById(R.id.llIDo);
            tvWarnDev = (TextView) itemView.findViewById(R.id.tvWarnDev);
            tvWarnContent = (TextView) itemView.findViewById(R.id.tvWarnContent);
            /*tvWarnPart = (TextView) itemView.findViewById(R.id.tvWarnPart);*/
            tvWarnTime = (TextView) itemView.findViewById(R.id.tvWarnTime);

        }
    }

}
