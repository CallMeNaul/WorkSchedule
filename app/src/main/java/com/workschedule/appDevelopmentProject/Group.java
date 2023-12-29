package com.workschedule.appDevelopmentProject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Group {
    private String groupName;
    private String groupDate;
    private String groupTime;
    private String groupID;
    public static ArrayList<Group> groupArrayList = new ArrayList<>();
    public static ArrayList<Group> AllGroups()
    {
        ArrayList<Group> groups = new ArrayList<>();
        for(Group group : groupArrayList)
        {
            groups.add(group);
        }
        return groups;
    }
    public Group(String groupID, String groupName, String groupDate, String groupTime){
        this.groupID = groupID;
        this.groupName = groupName;
        this.groupDate = groupDate;
        this.groupTime = groupTime;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupDate(String groupDate) {
        this.groupDate = groupDate;
    }

    public void setGroupTime(String groupTime) {
        this.groupTime = groupTime;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupDate() {
        return groupDate;
    }

    public String getGroupTime() {
        return groupTime;
    }
}
