package com.kimjunu.indiesetlist.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VenueModel implements Parcelable {
    public String id = "";
    public String name = "";

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
    }

    public VenueModel() {
    }

    protected VenueModel(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<VenueModel> CREATOR = new Parcelable.Creator<VenueModel>() {
        @Override
        public VenueModel createFromParcel(Parcel source) {
            return new VenueModel(source);
        }

        @Override
        public VenueModel[] newArray(int size) {
            return new VenueModel[size];
        }
    };
}
