/*
 * Copyright (C) 2014 pengjianbo(pengjianbosoft@gmail.com), Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cn.finalteam.galleryfinal.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import cn.finalteam.galleryfinal.BitmapUtils2;
import cn.finalteam.toolsfinal.BitmapUtils;

/**
 * Desction:图片信息
 * Author:pengjianbo
 * Date:15/7/30 上午11:23
 */
public class PhotoInfo implements Serializable {

    private int photoId;
    private String photoPath;
    //private String thumbPath;
    private int width;
    private int height;

    private String photoPathId;
    private Bitmap bitmap;

    public String getPhotoPathId() {
        return photoPathId;
    }

    public void setPhotoPathId(String photoPathId) {
        this.photoPathId = photoPathId;
    }

    public PhotoInfo() {}

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public Bitmap getBitmap() {
        if (bitmap == null) {
            try {
                bitmap = revitionImageSize(photoPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public boolean equals(Object o) {
        if ( o == null || !(o instanceof PhotoInfo)) {
            return false;
        }
        PhotoInfo info = (PhotoInfo) o;
        if (info == null) {
            return false;
        }

        return TextUtils.equals(info.getPhotoPath(), getPhotoPath());
    }

    private Bitmap revitionImageSize(String path) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTempStorage = new byte[100 * 1024];
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inPurgeable = true;
        opts.inSampleSize = 4;
        opts.inInputShareable = true;
        Bitmap bm = BitmapFactory.decodeStream(is, null, opts);
        bm = BitmapUtils2.compressImage(bm);
        return bm;
    }
}
