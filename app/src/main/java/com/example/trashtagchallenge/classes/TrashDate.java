package com.example.trashtagchallenge.classes;

public class TrashDate {
    private int day;
    private int month;
    private int year;
    private int week;

    public TrashDate(int day, int month, int year, int week) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.week = week;
    }

    public TrashDate() {
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getWeek() {
        return week;
    }
}
