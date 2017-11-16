package com.happymeal.tupapp;


public class Schedule {

    private String coursecode;
    private String coursetitle;
    private String date;
    private String time;
    private String room;

    public String getCoursecode() {
        return coursecode;
    }

    public void setCoursecode(String coursecode) {
        this.coursecode = coursecode;
    }

    public String getCoursetitle() {
        return coursetitle;
    }

    public void setCoursetitle(String coursetitle) {
        this.coursetitle = coursetitle;
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

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Schedule(String coursecode, String coursetitle, String date, String time, String room) {

        this.coursecode = coursecode;
        this.coursetitle = coursetitle;
        this.date = date;
        this.time = time;
        this.room = room;
    }
}
