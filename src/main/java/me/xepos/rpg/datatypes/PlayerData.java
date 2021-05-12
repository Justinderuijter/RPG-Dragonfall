package me.xepos.rpg.datatypes;

import java.util.HashMap;

public class PlayerData {
    private String classId = "test";
    private long lastClassChange = 0;
    private int freeChangeTickets = 2;
    private HashMap<String, ClassData> classes = new HashMap<>();

    public PlayerData(String classId, int freeChangeTickets, long lastClassChange){
        this.classId = classId;
        this.freeChangeTickets = freeChangeTickets;
        this.lastClassChange = lastClassChange;
    }



    public ClassData getClassData(String classId){
        return classes.get(classId);
    }

    public void addClassData(String classId, ClassData classData){
        this.classes.put(classId, classData);
    }
}
