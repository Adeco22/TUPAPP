package com.happymeal.tupapp;


public class Semester{

    private String semester;
    private String schoolyear;

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getSchoolyear() {
        return schoolyear;
    }

    public void setSchoolyear(String schoolyear) {
        this.schoolyear = schoolyear;
    }

    public Semester(String semester, String schoolyear) {

        this.semester = semester;
        this.schoolyear = schoolyear;
    }
}
