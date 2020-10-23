package com.example.barrierfree.models;

/**
 * 공공 데이터 api 호출시 사용
 * */
public class RequestAddr {

    private String sidoName = ""; // 시도 종류
    private String gugunName= ""; // 구군
    private int code = 0; // 코드값

    public RequestAddr(String sidoName, String gugunName, int code) {
        this.sidoName = sidoName;
        this.gugunName = gugunName;
        this.code = code;
    }

    public String getSidoName() {
        return sidoName;
    }

    public void setSidoName(String sidoName) {
        this.sidoName = sidoName;
    }

    public String getGugunName() {
        return gugunName;
    }

    public void setGugunName(String gugunName) {
        this.gugunName = gugunName;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
