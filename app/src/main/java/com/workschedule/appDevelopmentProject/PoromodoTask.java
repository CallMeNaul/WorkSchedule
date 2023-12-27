package com.workschedule.appDevelopmentProject;

public class PoromodoTask {
    private String taskID, name, note, time;
    private boolean isTick;
    public PoromodoTask(String ID, String na, String no, String ti, boolean isTick)
    {
        this.taskID = ID;
        this.name = na;
        this.note = no;
        this.time = ti;
        this.isTick = isTick;
    }

    public String getTaskID() {
        return taskID;
    }

    public String getName() {
        return name;
    }

    public String getNote() {
        return note;
    }

    public String getTime() {
        return time;
    }

    public boolean getTick(){
        return isTick;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTick(boolean tick) {
        isTick = tick;
    }
}
