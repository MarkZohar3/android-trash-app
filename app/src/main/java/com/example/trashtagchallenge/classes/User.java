package com.example.trashtagchallenge.classes;

public class User {
    String userID;
    String username;

    public User(){

    }

    public User(String userID, String username) {
        this.userID = userID;
        this.username = username;
    }

    public String getUserID() {
        return userID;
    }



    public String getUsername() {
        return username;
    }

}
