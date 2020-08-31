package com.example.barrierfree.models;

public class ListViewMember {
    private String uid;
    private String mem_name;
    private String mem_email;
    private String mem_photo;
    private String lv_id;

    public ListViewMember() {
    }

    public ListViewMember(String uid, String mem_name, String mem_email, String mem_photo, String lv_id) {
        this.uid = uid;
        this.mem_name = mem_name;
        this.mem_email = mem_email;
        this.mem_photo = mem_photo;
        this.lv_id = lv_id;
    }

    public void setMem_uid(String uid) {
        this.uid = uid;
    }

    public void setMem_name(String mem_name) {
        this.mem_name = mem_name;
    }

    public void setMem_email(String mem_email) {
        this.mem_email = mem_email;
    }

    public void setMem_photo(String mem_photo) {
        this.mem_photo = mem_photo;
    }

    public void setLv_id(String lv_id) {
        this.mem_photo = lv_id;
    }

    public String getMem_uid() {
        return uid;
    }

    public String getMem_name() {
        return mem_name;
    }

    public String getMem_email() {
        return mem_email;
    }

    public String getMem_photo() {
        return mem_photo;
    }

    public String getLv_id() {
        return lv_id;
    }
}
