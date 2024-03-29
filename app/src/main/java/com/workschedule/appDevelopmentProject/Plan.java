package com.workschedule.appDevelopmentProject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Plan
{
    public static ArrayList<Plan> plansList = new ArrayList<>();
    public static ArrayList<Plan> plansForDate(LocalDate date, ArrayList<Plan> pl)
    {
        ArrayList<Plan> plans = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = date.format(formatter);
        for(Plan plan : pl)
        {
            if(plan.getDate().equals(formattedDate))
                plans.add(plan);
        }
        return plans;
    }
    public static ArrayList<Plan> importantPlansForDate(LocalDate date, ArrayList<Plan> pl)
    {
        ArrayList<Plan> plans = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = date.format(formatter);
        for(Plan plan : pl)
        {
            if(plan.getDate().equals(formattedDate) && plan.isImportant)
                plans.add(plan);
        }
        return plans;
    }
    private String name;
    private String date;
    private String time;
    private String mota;
    private String ID;
    private boolean isImportant = false;
    public Plan(){}
    public Plan(String ID, String name, String mota, String date, String time, boolean imp)
    {
        this.ID = ID;
        this.name = name;
        this.date = date;
        this.time = time;
        this.mota = mota;
        this.isImportant = imp;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getDate()
    {
        return date;
    }
    public void setDate(String date)
    {
        this.date = date;
    }
    public String getTime()
    {
        return time;
    }
    public void setTime(String time)
    {
        this.time = time;
    }
    public void setMota(String mota)
    {
        this.mota = mota;
    }
    public String getMota()
    {
        return mota;
    }
    public void setID(String id)
    {
        this.ID = id;
    }
    public String getID()
    {
        return ID;
    }
    public boolean getImportant()
    {
        return isImportant;
    }

}