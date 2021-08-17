package me.xepos.rpg.datatypes.armorconditions;

import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;

public class LMCondition implements IConditionComponent{
    private final boolean isBiggerThan;
    private final int level;
    private NamespacedKey levelledKey;

    public LMCondition(NamespacedKey levelledKey, String arg){
        this.levelledKey = levelledKey;
        this.isBiggerThan = arg.contains(">");
        this.level = Integer.parseInt(arg.substring(1));
    }

    @Override
    public boolean isMet(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent e)) return false;

        Integer mobLevel = e.getEntity().getPersistentDataContainer().get(levelledKey, PersistentDataType.INTEGER);
        if (mobLevel != null){
            if (isBiggerThan){
                return mobLevel > level;
            }else{
                if (mobLevel == -1) return false;

                return mobLevel < level;
            }
        }
        return false;
    }
}
