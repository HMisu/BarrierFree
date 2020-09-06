package com.example.barrierfree.models;

public class Connection {
    private String mem_protect;
    private String mem_weak;
    private String mem_applicant;
    private String mem_respondent;
    private boolean connect;

    public Connection() {
    }

    public Connection(String mem_protect, String mem_weak, String mem_applicant, String mem_respondent, boolean connect) {
        this.mem_protect = mem_protect;
        this.mem_weak = mem_weak;
        this.mem_applicant = mem_applicant;
        this.mem_respondent = mem_respondent;
        this.connect = connect;
    }

    public void setMem_protect(String mem_protect) {
        this.mem_protect = mem_protect;
    }

    public void setMem_weak(String mem_weak) {
        this.mem_weak = mem_weak;
    }

    public void setMem_applicant(String mem_applicant) {
        this.mem_applicant = mem_applicant;
    }

    public void setMem_respondent(String mem_respondent){
        this.mem_respondent = mem_respondent;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }

    public String getMem_protect() {
        return mem_protect;
    }

    public String getMem_weak() {
        return mem_weak;
    }

    public String getMem_applicant() {
        return mem_applicant;
    }

    public String getMem_respondent(){
        return mem_respondent;
    }
    public boolean getConnect() {
        return connect;
    }
}
