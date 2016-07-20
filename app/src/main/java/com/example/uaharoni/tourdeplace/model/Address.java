package com.example.uaharoni.tourdeplace.model;

public class Address {
    private double addLat, addLong;   // mapped under geometry/location in gPlaces API
    private String name;    //mapped as: vicinity in gPlaces API

    public Address(String name, double addLat, double addLong) {
        this.name = name;
        this.addLat = addLat;
        this.addLong = addLong;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAddLong() {
        return addLong;
    }

    public void setAddLong(double addLong) {
        this.addLong = addLong;
    }

    public double getAddLat() {
        return addLat;
    }

    public void setAddLat(double addLat) {
        this.addLat = addLat;
    }
}