package me.xepos.rpg.datatypes;

import org.bukkit.craftbukkit.libs.jline.internal.Nullable;

import java.util.HashMap;

public class PlayerData {
    private String classId;
    private boolean isClassEnabled;
    private long lastClassChange = 0;
    private long lastBookReceived = 0;
    private HashMap<String, ClassData> classes;

    public PlayerData(String classId, long lastClassChange, long lastBookReceived, boolean isClassEnabled) {
        this.classId = classId;
        this.isClassEnabled = isClassEnabled;
        this.lastClassChange = lastClassChange;
        this.lastBookReceived = lastBookReceived;
        this.classes = new HashMap<>();
    }

    public PlayerData(@Nullable String classId){
        this.classId = classId;
        this.lastClassChange = 0;
        this.lastBookReceived = 0;
        this.isClassEnabled = true;
        this.classes = new HashMap<>();
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public long getLastClassChange() {
        return lastClassChange;
    }

    public void setLastClassChange(long lastClassChange) {
        this.lastClassChange = lastClassChange;
    }

    public long getLastBookReceived() {
        return lastBookReceived;
    }

    public void setLastBookReceived(long lastBookReceived) {
        this.lastBookReceived = lastBookReceived;
    }

    public boolean isClassEnabled() {
        return isClassEnabled;
    }

    public void setClassEnabled(boolean classEnabled) {
        isClassEnabled = classEnabled;
    }

    public HashMap<String, ClassData> getClasses() {
        return classes;
    }

    public ClassData getClassData(String classId){
        ClassData classData = classes.get(classId);
        if (classData == null){
            classData = new ClassData();
        }
        return classData;
    }

    public void setClasses(HashMap<String, ClassData> classes) {
        this.classes = classes;
    }

    public void addClassData(String classId, ClassData classData){
        this.classes.put(classId, classData);
    }

    @Override
    public String toString(){
        StringBuilder string = new StringBuilder("PlayerData{{ClassId:" + classId + "}{+ " + isClassEnabled + " +}}");
        for (String classId:this.classes.keySet()) {
            string.append(classes.get(classId).toString());
        }

        return string.toString();
    }
}
