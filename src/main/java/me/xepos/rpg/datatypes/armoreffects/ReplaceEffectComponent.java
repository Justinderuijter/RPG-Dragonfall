package me.xepos.rpg.datatypes.armoreffects;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

public abstract class ReplaceEffectComponent implements IEffectComponent{
    private EntityType entityType;

    public ReplaceEffectComponent(String effect){
        String[] strings = effect.split(":");
        try{
            entityType = EntityType.valueOf(strings[1]);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            Bukkit.getLogger().warning(strings[1] + " is not a valid entity type!");
            entityType = EntityType.SMALL_FIREBALL;
        }

    }

    public EntityType getEntityType() {
        return entityType;
    }
}
