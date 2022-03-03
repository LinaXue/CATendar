package com.example.firebase;

public class Events {
    String EVENT;
    String TIME;
    String DATE;
    String MONTH;
    String YEAR;
    String ENDDATE;
    String ENDMONTH;
    String ENDYEAR;
    String TYPE;


    public String getEVENT() {
        return EVENT;
    }

    public void setEVENT(String EVENT) {
        this.EVENT = EVENT;
    }

    public String getTIME() {
        return TIME;
    }

    public void setTIME(String TIME) {
        this.TIME = TIME;
    }

    public String getDATE() {
        return DATE;
    }

    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    public String getMONTH() {
        return MONTH;
    }

    public void setMONTH(String MONTH) {
        this.MONTH = MONTH;
    }

    public String getYEAR() {
        return YEAR;
    }

    public void setYEAR(String YEAR) {
        this.YEAR = YEAR;
    }

    public String getENDDATE() {
        return ENDDATE;
    }

    public void setENDDATE(String ENDDATE) {
        this.ENDDATE = ENDDATE;
    }

    public String getENDMONTH() {
        return ENDMONTH;
    }

    public void setENDMONTH(String ENDMONTH) {
        this.ENDMONTH = ENDMONTH;
    }

    public String getENDYEAR() {
        return ENDYEAR;
    }

    public void setENDYEAR(String ENDYEAR) {
        this.ENDYEAR = ENDYEAR;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public Events(String EVENT, String TIME, String DATE, String MONTH, String YEAR, String ENDDATE, String ENDMONTH, String ENDYEAR, String TYPE) {
        this.EVENT = EVENT;
        this.TIME = TIME;
        this.DATE = DATE;
        this.MONTH = MONTH;
        this.YEAR = YEAR;
        this.ENDDATE = ENDDATE;
        this.ENDMONTH = ENDMONTH;
        this.ENDYEAR = ENDYEAR;
        this.TYPE = TYPE;
    }


}
