package com.pcjz.lems.ui.workrealname.personinfo;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseActivity;
import com.pcjz.lems.business.common.view.CircleFlowIndicator;
import com.pcjz.lems.business.common.view.ViewFlow;
import com.pcjz.lems.business.config.AppConfig;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.model.PhotoInfo;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by Greak on 2017/10/23.
 */

public class ShowImageActivity extends BaseActivity {
    private ViewFlow mViewFlow;
    private CircleFlowIndicator mFlowIndicator;
    private ImageView ivClose;
    private TextView tvTitle;
    private int mCurrentIndex = 1;
    private String householdAreaId;
    private String roomId;
    List<String> mImages = new ArrayList<String>();
    ArrayList<PhotoInfo> mPhotos = new ArrayList<>();
    private int position;
    private String mMode;

    private PhotoView mPhotoView;
    private PhotoViewAttacher mAttacher;

    private String curPhotoPath;

    @Override
    public void setView() {

    }

    @Override
    public void setListener() {
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_show_image);
        ivClose = (ImageView) findViewById(R.id.iv_close);
        tvTitle = (TextView) findViewById(R.id.tv_title);

        mPhotoView = (PhotoView) findViewById(R.id.photoView);
        curPhotoPath = getIntent().getStringExtra("photoPath");
        ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + curPhotoPath, mPhotoView);
        mAttacher = new PhotoViewAttacher(mPhotoView);
        /*mPhotoView.setImageBitmap(mBitmap);*/

        mAttacher.update();




    }




}
