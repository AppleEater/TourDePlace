package com.example.uaharoni.tourdeplace.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable {
    private String name;
    private Address address;
    private Long id; // used for DB saving
    private String gPlaceId;    //Google PlaceID
    private String placeIconUrl;    // maps to "icon" in the gPlace API
    private float placeRating;
    private PlacePhoto[] placePhotos;

    public Place(String name, Address address, Long id) {
        this.name = name;
        this.address = address;
        this.id = id;
    }

    public Place(String name, Address address, String gPlaceId, String placeIconUrl) {
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
        parcel.writeDoubleArray(new double[]{this.getAddress().getAddLat(),this.getAddress().getAddLong()});
    }

    protected Place(Parcel in) {
        name = in.readString();
        gPlaceId = in.readString();
        placeIconUrl = in.readString();
        this.address.setName(in.readString());
        double[] addCoordinates = new double[2];
        in.readDoubleArray(addCoordinates);
        this.address.setAddLat(addCoordinates[0]);
        this.address.setAddLong(addCoordinates[1]);
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

    public float getPlaceRating() {
        return placeRating;
    }

    public void setPlaceRating(float placeRating) {
        this.placeRating = placeRating;
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