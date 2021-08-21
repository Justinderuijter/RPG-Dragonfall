package me.xepos.rpg.datatypes.armoreffects;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import org.bukkit.event.Event;

public class IncreaseMcMMOExpComponent implements IEffectComponent{
    private final boolean isPercentage;
    private final double amount;

    public IncreaseMcMMOExpComponent(String effect){
        String[] strings = effect.split(":");
        this.isPercentage = strings[1].endsWith("%");
        this.amount = Double.parseDouble(strings[1].substring(0, strings[1].length() - 1));
    }

    @Override
    public void activate(Event event) {
        if (event instanceof McMMOPlayerXpGainEvent e){
            if (isPercentage){
                e.setRawXpGained(e.getRawXpGained() * (float)(1 + amount / 100));
            }else{
                e.setRawXpGained(e.getRawXpGained() + (float) amount);
            }
        }
    }
}
