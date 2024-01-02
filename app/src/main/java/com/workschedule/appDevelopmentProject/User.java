package com.workschedule.appDevelopmentProject;

import android.net.Uri;

import java.util.ArrayList;

public class User {
    private String userID;
    private String userEmail;
    private String userName;
    private Uri userAvt;
    public User(String userID, String userName, String userEmail, Uri userAvt){
        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userAvt = userAvt;
    }
    public User(String userID, String userName, String userEmail){
        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
    }
    public static ArrayList<User> userArrayList = new ArrayList<>();
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public Uri getUserAvt() {
        return userAvt;
    }
}
