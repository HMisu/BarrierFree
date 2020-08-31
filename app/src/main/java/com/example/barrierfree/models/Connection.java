package com.example.barrierfree.models;

public class Connection {
    private String mem_protect;
    private String mem_weak;
    private String mem_applicant;
    private boolean connect;

    public Connection() {
    }

    public Connection(String mem_protect, String mem_weak, String mem_applicant, boolean connect) {
        this.mem_protect = mem_protect;
        this.mem_weak = mem_weak;
        this.mem_applicant = mem_applicant;
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

    public void setConnect(boolean connect) {
        this.connect = connect;
    }

}
