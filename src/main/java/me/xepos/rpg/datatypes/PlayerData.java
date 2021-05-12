package me.xepos.rpg.datatypes;

import java.util.HashMap;

public class PlayerData {
    private String classId;
    private long lastClassChange = 0;
    private int freeChangeTickets = 2;
    private final HashMap<String, ClassData> classes = new HashMap<>();

    public PlayerData(String classId, int freeChangeTickets, long lastClassChange){
        this.classId = classId;
        this.freeChangeTickets = freeChangeTickets;
        this.lastClassChange = lastClassChange;
    }

    public void setClassId(String classId){
        this.classId = classId;
    }

    public String getClassId() {
        return classId;
    }

    public long getLastClassChange() {
        return lastClassChange;
    }

    public int getFreeChangeTickets() {
        return freeChangeTickets;
    }

    public ClassData getClassData(String classId){
        return classes.get(classId);
    }

    public void addClassData(String classId, ClassData classData){
        this.classes.put(classId, classData);
    }
}
