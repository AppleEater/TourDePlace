package com.example.uaharoni.tourdeplace.model;

public class Address {
    private Long addLat, addLong;   // mapped under geometry/location in gPlaces API
    private String name;    //mapped as: vicinity in gPlaces API

    public Address(String name, Long addLat, Long addLong) {
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

    public Long getAddLong() {
        return addLong;
    }

    public void setAddLong(Long addLong) {
        this.addLong = addLong;
    }

    public Long getAddLat() {
        return addLat;
    }

    public void setAddLat(Long addLat) {
        this.addLat = addLat;
    }
}