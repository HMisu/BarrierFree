package com.example.barrierfree.models;

public class Member {
    private String uid;
    private String mem_name;
    private String mem_birth;
    private String mem_email;
    private String mem_phone;
    private String mem_addr1;
    private String mem_addr2;
    private String mem_photo;

    public Member() {
    }

    public Member(String uid, String mem_name, String mem_birth, String mem_email, String mem_phone, String mem_addr1, String mem_addr2, String mem_photo) {
        this.uid = uid;
        this.mem_name = mem_name;
        this.mem_birth = mem_birth;
        this.mem_email = mem_email;
        this.mem_phone = mem_phone;
        this.mem_addr1 = mem_addr1;
        this.mem_addr2 = mem_addr2;
        this.mem_photo = mem_photo;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setMem_name(String mem_name) {
        this.mem_name = mem_name;
    }

    public void setMem_birth(String mem_birth) {
        this.mem_birth = mem_birth;
    }

    public void setMem_email(String mem_email) {
        this.mem_email = mem_email;
    }

    public void setMem_phone(String mem_phone) {
        this.mem_phone = mem_phone;
    }

    public void setMem_addr1(String mem_addr1) {
        this.mem_addr1 = mem_addr1;
    }

    public void setMem_addr2(String mem_addr2) {
        this.mem_addr2 = mem_addr2;
    }

    public void setMem_photo(String mem_photo) {
        this.mem_photo = mem_photo;
    }

    public String getUid() { return uid; }

    public String getMem_name() {
        return mem_name;
    }

    public String getMem_birth() {
        return mem_birth;
    }

    public String getMem_email() {
        return mem_email;
    }

    public String getMem_phone() {
        return mem_phone;
    }

    public String getMem_addr1() {
        return mem_addr1;
    }

    public String getMem_addr2() {
        return mem_addr2;
    }

    public String getMem_photo() {
        return mem_photo;
    }
}
