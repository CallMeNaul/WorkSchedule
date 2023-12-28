package com.workschedule.appDevelopmentProject;

public class User {
    private String userID;
    private String userName;
    private String userEmail;
    public User(String ID, String Name, String Email){
        this.userID = ID;
        this.userName = Name;
        this.userEmail = Email;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
