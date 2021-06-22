package me.xepos.rpg.datatypes;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SkillData {
    private final String name;
    private final String icon;
    private final HashMap<String, Object> statics;
    private final HashMap<Integer, HashMap<String, Object>> levelData;

    public SkillData(String name, String icon, HashMap<String, Object> statics, HashMap<Integer, HashMap<String, Object>> levelData){
        this.name = name;
        this.icon = icon;

        if (statics == null) this.statics = new HashMap<>();
        else this.statics = statics;

        this.levelData = levelData;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public HashMap<Integer, HashMap<String, Object>> getLevelData() {
        return levelData;
    }

    public List<String> getDescription(int level){
        List<String> description = (List<String>) levelData.get(level).get("description");
        Set<String> data = levelData.get(level).keySet();
        for (String line:description) {
            for (String variable:data) {
                line = StringUtils.replace(line, "%" + variable + "%", String.valueOf(variable));
            }
        }
        return description;
    }

    public List<String> getStringList(int level, String name){
        return (List<String>) levelData.get(level).get(name);
    }

    //For future proofing, will likely need some extra checks
    private Object getObject(int level, String name){
        Object value = levelData.get(level).get(name);

        if (value == null) return statics.get(name);

        return value;
    }

    public double getDamage(int level){
        Object value = getObject(level, "damage");
        return value instanceof Number ? (double) value : 0;
    }

    public int getMana(int level){
        Object value = getObject(level, "mana");
        return value instanceof Number ? (int) value : 0;
    }

    public double getCooldown(int level){
        Object value = getObject(level, "cooldown");
        return value instanceof Number ? (double) value : 0;
    }

    public double getDamageMultiplier(int level){
        Object value = getObject(level, "damage-multiplier");
        return value instanceof Number ? (double) value : 1.0;
    }

    public double getDouble(int level, String name) {
        Object value = getObject(level, name);
        return value instanceof Number ? (double) value : 0;
    }

    public double getDouble(int level, String name, double def) {
        Object value = getObject(level, name);
        return value instanceof Number ? (double) value : def;
    }

    public int getInt(int level, String name){
       Object value = getObject(level, name);
       return value instanceof Number ? (int) value : 0;
    }

    public int getInt(int level, String name, int def){
        Object value = getObject(level, name);
        return value instanceof Number ? (int) value : def;
    }

    public boolean getBoolean(int level, String name, boolean def){
        Object value = getObject(level, name);
        return value instanceof Boolean ? (boolean) value : def;
    }
}
