package com.kimjunu.indiesetlist.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ArtistModel implements Parcelable {
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

    public ArtistModel() {
    }

    protected ArtistModel(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<ArtistModel> CREATOR = new Parcelable.Creator<ArtistModel>() {
        @Override
        public ArtistModel createFromParcel(Parcel source) {
            return new ArtistModel(source);
        }

        @Override
        public ArtistModel[] newArray(int size) {
            return new ArtistModel[size];
        }
    };
}
