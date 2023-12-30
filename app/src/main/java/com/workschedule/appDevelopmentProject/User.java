package com.workschedule.appDevelopmentProject;

import java.util.ArrayList;

public class User {
    private String userID;
    private String userEmail;
    public User(String ID, String Name, String Email){
        this.userID = ID;
        this.userEmail = Email;
    }
    public static ArrayList<User> userArrayList = new ArrayList<>();
    public User(String userID, String userEmail){
        this.userID = userID;
        this.userEmail = userEmail;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
