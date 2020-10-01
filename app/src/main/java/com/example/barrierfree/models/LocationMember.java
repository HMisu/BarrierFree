package com.example.barrierfree.models;

public class LocationMember {
    private double latitude; //경도
    private double longitude; //위도
    private String time;

    public LocationMember() {
    }

    public LocationMember(double latitude, double longitude, String time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public String getTime() {
        return time;
    }
}
