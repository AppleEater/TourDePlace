package com.example.uaharoni.tourdeplace.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable {
    private String name;
    private Address address;
    private Long id; // used for DB saving
    private String gPlaceId;    //Google PlaceID
    private String placeIconUrl;    // maps to "icon" in the gPlace API
    private PlacePhoto[] placePhotos;

    public Place(String name, Address address, Long id) {
        this.name = name;
        this.address = address;
        this.id = id;
    }

    public Place(String name, Address address, Long id, String gPlaceId, String placeIconUrl) {
        this.name = name;
        this.address = address;
        this.id = id;
        this.gPlaceId = gPlaceId;
        this.placeIconUrl = placeIconUrl;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.getName());
        parcel.writeString(this.getgPlaceId());
        parcel.writeString(this.getPlaceIconUrl());
        parcel.writeString(this.getAddress().getName());
        parcel.writeLong(this.getAddress().getAddLat());
        parcel.writeLong(this.getAddress().getAddLong());
    }

    protected Place(Parcel in) {
        name = in.readString();
        gPlaceId = in.readString();
        placeIconUrl = in.readString();
        this.address.setName(in.readString());
        this.address.setAddLat(in.readLong());
        this.address.setAddLong(in.readLong());
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getgPlaceId() {
        return gPlaceId;
    }

    public void setgPlaceId(String gPlaceId) {
        this.gPlaceId = gPlaceId;
    }

    public String getPlaceIconUrl() {
        return placeIconUrl;
    }

    public void setPlaceIconUrl(String placeIconUrl) {
        this.placeIconUrl = placeIconUrl;
    }
    public PlacePhoto[] getPlacePhotos() {
        return placePhotos;
    }

    public void setPlacePhotos(PlacePhoto[] placePhotos) {
        this.placePhotos = placePhotos;
    }


    @Override
    public int describeContents() {
        return 0;
    }
}