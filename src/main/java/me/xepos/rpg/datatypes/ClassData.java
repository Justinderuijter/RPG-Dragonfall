package me.xepos.rpg.datatypes;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ClassData {
    private double health = 20.0;
    private Set<String> skills;

    public ClassData(double health, Set<String> skillIds){
        this.health = health;
        this.skills = skillIds;
    }

    public double getHealth() {
        return health;
    }

    public Set<String> getSkills() {
        return skills;
    }
}
