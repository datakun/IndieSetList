package com.kimjunu.indiesetlist.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class EventModel implements Parcelable {
    public String id = "";
    public DateTime date = new DateTime();
    public VenueModel venue = new VenueModel();
    public ArrayList<ArtistModel> artists = new ArrayList<>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeSerializable(this.date);
        dest.writeParcelable(this.venue, flags);
        dest.writeTypedList(this.artists);
    }

    public EventModel() {
    }

    protected EventModel(Parcel in) {
        this.id = in.readString();
        this.date = (DateTime) in.readSerializable();
        this.venue = in.readParcelable(VenueModel.class.getClassLoader());
        this.artists = in.createTypedArrayList(ArtistModel.CREATOR);
    }

    public static final Parcelable.Creator<EventModel> CREATOR = new Parcelable.Creator<EventModel>() {
        @Override
        public EventModel createFromParcel(Parcel source) {
            return new EventModel(source);
        }

        @Override
        public EventModel[] newArray(int size) {
            return new EventModel[size];
        }
    };
}
