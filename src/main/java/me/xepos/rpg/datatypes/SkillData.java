package me.xepos.rpg.datatypes;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.util.NumberConversions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SkillData {
    private final String name;
    private final String icon;
    private final HashMap<String, Object> statics;
    private final HashMap<Integer, HashMap<String, Object>> levelData;

    public SkillData(String name, String icon, HashMap<String, Object> statics, HashMap<Integer, HashMap<String, Object>> levelData) {
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

    public List<String> getDescription(int level) {
        List<String> result = new ArrayList<>();
        List<String> description = (List<String>) getObject(level, "description");
        if (description == null) description = new ArrayList<>();

        HashMap<String, Object> levelDataRef = levelData.get(level);
        HashMap<String, Object> data = new HashMap<>(statics);
        for (String string : data.keySet()) {
            Bukkit.getLogger().severe(string);
        }
        if (levelDataRef != null) {
            data.putAll(levelDataRef);
        }
        for (String line : description) {
            String editedString = "";
            for (String variable : data.keySet()) {
                if (StringUtils.contains(line, "%" + variable + "%")) {
                    editedString = StringUtils.replace(line, "%" + variable + "%", String.valueOf(data.get(variable)));
                    break;
                }
                editedString = line;
            }
            result.add(editedString);
        }
        return result;
    }

    public List<String> getStringList(int level, String name) {
        List<String> result = (List<String>) getObject(level, name);
        if (result == null) {
            Bukkit.getLogger().severe("Level: " + level + ", name: " + name);
        }
        return result == null ? new ArrayList<>() : result;
    }

    //For future proofing, will likely need some extra checks
    private Object getObject(int level, String name) {
        HashMap<String, Object> map = levelData.get(level);

        if (map == null) return statics.get(name);

        Object value = map.get(name);

        if (value == null) return statics.get(name);

        return value;
    }

    public double getDamage(int level) {
        Object value = getObject(level, "damage");
        return value instanceof Number ? NumberConversions.toDouble(value) : 0;
    }

    public int getMana(int level) {
        Object value = getObject(level, "mana");
        return value instanceof Number ? (int) value : 0;
    }

    public double getCooldown(int level) {
        Object value = getObject(level, "cooldown");
        return value instanceof Number ? NumberConversions.toDouble(value) : 0;
    }

    public double getDamageMultiplier(int level) {
        Object value = getObject(level, "damage-multiplier");
        return value instanceof Number ? NumberConversions.toDouble(value) : 1.0;
    }

    public double getDouble(int level, String name) {
        Object value = getObject(level, name);
        return value instanceof Number ? NumberConversions.toDouble(value) : 0;
    }

    public double getDouble(int level, String name, double def) {
        Object value = getObject(level, name);
        return value instanceof Number ? NumberConversions.toDouble(value) : def;
    }

    public int getInt(int level, String name) {
        Object value = getObject(level, name);
        return value instanceof Number ? (int) value : 0;
    }

    public int getInt(int level, String name, int def) {
        Object value = getObject(level, name);
        return value instanceof Number ? (int) value : def;
    }

    public boolean getBoolean(int level, String name, boolean def) {
        Object value = getObject(level, name);
        return value instanceof Boolean ? (boolean) value : def;
    }
}
