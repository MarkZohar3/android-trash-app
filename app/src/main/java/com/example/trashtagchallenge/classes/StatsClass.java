package com.example.trashtagchallenge.classes;

public class StatsClass {
    private String username;
    private int points;

    public String getUsername() {
        return username;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public StatsClass(String username, int points) {
        this.username = username;
        this.points = points;
    }
}
