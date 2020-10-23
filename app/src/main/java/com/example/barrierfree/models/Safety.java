package com.example.barrierfree.models;

public class Safety {
    private String sf_id;
    private String kind;
    private String name;
    private String addr;
    private double latitude; //경도
    private double longitude; //위도
    private String mem_weak;
    private String mem_register;

    public Safety(){}

    public Safety(String kind, String name, String addr, double latitude, double longitude, String mem_weak, String mem_register) {
        this.kind = kind;
        this.name = name;
        this.addr = addr;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mem_weak = mem_weak;
        this.mem_register = mem_register;
    }

    public Safety(String sf_id, String kind, String name, String addr, double latitude, double longitude, String mem_weak, String mem_register) {
        this.sf_id = sf_id;
        this.kind = kind;
        this.name = name;
        this.addr = addr;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mem_weak = mem_weak;
        this.mem_register = mem_register;
    }

    public void setSf_id(String sf_id) {
        this.kind = sf_id;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setMem_weak(String mem_weak) {
        this.mem_weak = mem_weak;
    }

    public void setMem_register(String mem_register) {
        this.mem_register = mem_register;
    }

    public String getSf_id() {
        return sf_id;
    }

    public String getKind() {
        return kind;
    }

    public String getName() {
        return name;
    }

    public String getAddr() {
        return addr;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getMem_weak() {
        return mem_weak;
    }

    public String getMem_register() {
        return mem_register;
    }
}
