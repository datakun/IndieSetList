package com.kimjunu.indiesetlist.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PerformModel implements Parcelable {
    public String id = "";
    public String artist = "";
    public String date = "";
    public String venue = "";
    public String videoTitle = "";
    public String videoId = "";

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.artist);
        dest.writeString(this.date);
        dest.writeString(this.venue);
        dest.writeString(this.videoTitle);
        dest.writeString(this.videoId);
    }

    public PerformModel() {
    }

    protected PerformModel(Parcel in) {
        this.id = in.readString();
        this.artist = in.readString();
        this.date = in.readString();
        this.venue = in.readString();
        this.videoTitle = in.readString();
        this.videoId = in.readString();
    }

    public static final Parcelable.Creator<PerformModel> CREATOR = new Parcelable.Creator<PerformModel>() {
        @Override
        public PerformModel createFromParcel(Parcel source) {
            return new PerformModel(source);
        }

        @Override
        public PerformModel[] newArray(int size) {
            return new PerformModel[size];
        }
    };
}
