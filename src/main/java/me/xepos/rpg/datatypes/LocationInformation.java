package me.xepos.rpg.datatypes;

import org.bukkit.Location;
import org.bukkit.World;

public class LocationInformation {
    private Location location;
    private int level;

    public LocationInformation(Location location, int level){
        this.location = location;
        this.level = level;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double distanceSquared(Location location){
        return this.location.distanceSquared(location);
    }

    public World getWorld(){
        return this.location.getWorld();
    }
}
