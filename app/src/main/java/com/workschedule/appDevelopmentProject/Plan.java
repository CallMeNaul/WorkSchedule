package com.workschedule.appDevelopmentProject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Plan
{
    public static ArrayList<Plan> plansList = new ArrayList<>();
    public static ArrayList<Plan> plansForDate(LocalDate date)
    {
        ArrayList<Plan> plans = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = date.format(formatter);
        for(Plan plan : plansList)
        {

            if(plan.getDate().equals(formattedDate))
                plans.add(plan);
        }
        return plans;
    }
    private String name;
    private String date;
    private String time;
    private String mota;
    public Plan(){}
    public Plan(String name, String mota, String date, String time)
    {
        this.name = name;
        this.date = date;
        this.time = time;
        this.mota = mota;
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
    public void setMota(String mota){this.mota = mota;}
    public String getMota(){return mota;}
}