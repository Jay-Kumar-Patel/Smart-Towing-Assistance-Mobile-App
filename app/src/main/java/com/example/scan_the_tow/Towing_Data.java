package com.example.scan_the_tow;

public class Towing_Data {

    String date,time,area,fine,noplate;

    public Towing_Data(){}

    public String getNoplate() {
        return noplate;
    }

    public void setNoplate(String noplate) {
        this.noplate = noplate;
    }

    public Towing_Data(String date, String time, String area, String fine, String noplate) {
        this.date = date;
        this.time = time;
        this.area = area;
        this.fine = fine;
        this.noplate = noplate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getFine() {
        return fine;
    }

    public void setFine(String fine) {
        this.fine = fine;
    }
}
