package com.workschedule.appDevelopmentProject;

import java.util.ArrayList;

public class GroupPlan
{
    public static ArrayList<GroupPlan> groupPlanArrayList = new ArrayList<>();
    private String GroupPlanName;
    private String GroupPlanDate;
    private String GroupPlanTime;
    private String GroupPlanMota;
    private String GroupPlanID;
    private boolean isImportant = false;
    public GroupPlan(){}
    public GroupPlan(String ID, String GroupPlaName, String mota, String date, String time, boolean imp)
    {
        this.GroupPlanID = ID;
        this.GroupPlanName = GroupPlaName;
        this.GroupPlanDate = date;
        this.GroupPlanTime = time;
        this.GroupPlanMota = mota;
        this.isImportant = imp;
    }
    public String getGroupPlanName()
    {
        return GroupPlanName;
    }
    public void setGroupPlanName(String groupPlanName)
    {
        this.GroupPlanName = groupPlanName;
    }
    public String getGroupPlanDate()
    {
        return GroupPlanDate;
    }
    public void setGroupPlanDate(String groupPlanDate)
    {
        this.GroupPlanDate = groupPlanDate;
    }
    public String getGroupPlanTime()
    {
        return GroupPlanTime;
    }
    public void setGroupPlanTime(String groupPlanTime)
    {
        this.GroupPlanTime = groupPlanTime;
    }
    public void setGroupPlanMota(String groupPlanMota)
    {
        this.GroupPlanMota = groupPlanMota;
    }
    public String getGroupPlanMota()
    {
        return GroupPlanMota;
    }
    public void setGroupPlanID(String id)
    {
        this.GroupPlanID = id;
    }
    public String getGroupPlanID()
    {
        return GroupPlanID;
    }
    public boolean getImportant()
    {
        return isImportant;
    }

}
