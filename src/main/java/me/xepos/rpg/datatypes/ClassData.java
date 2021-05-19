package me.xepos.rpg.datatypes;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ClassData {
    private double health = 20.0;
    private Set<String> skills;
    private Set<String> keyBindOrder;

    public ClassData(double health, Set<String> skillIds, Set<String> keyBindOrder){
        this.health = health;
        this.skills = skillIds;
        this.keyBindOrder = keyBindOrder;
    }

    public double getHealth() {
        return health;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public Set<String> getKeyBindOrder() {return keyBindOrder;}
}
