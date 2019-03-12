package com.pcjz.lems.business.widget.selectdialog;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ${YGP} on 2017/5/11.
 */

public class SelectInfo implements Parcelable{
    private String id;
    private String name;
    private String tag;

    public SelectInfo() {

    }
    protected SelectInfo(Parcel in) {
        id = in.readString();
        name = in.readString();
        tag = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(tag);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SelectInfo> CREATOR = new Creator<SelectInfo>() {
        @Override
        public SelectInfo createFromParcel(Parcel in) {
            return new SelectInfo(in);
        }

        @Override
        public SelectInfo[] newArray(int size) {
            return new SelectInfo[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
