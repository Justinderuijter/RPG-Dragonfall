package me.xepos.rpg.datatypes;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SkillData {
    private final String name;
    private final String icon;
    private final HashMap<Integer, HashMap<String, Object>> levelData;

    public SkillData(String name, String icon, HashMap<Integer, HashMap<String, Object>> levelData){
        this.name = name;
        this.icon = icon;
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

    //For future proofing, will likely need some extra checks
    private Object getObject(int level, String name){
        return levelData.get(level).get(name);
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

    public double getDouble(int level, String name) {
        Object value = levelData.get(level).get(name);
        return value instanceof Number ? (double) value : 0;
    }
}
