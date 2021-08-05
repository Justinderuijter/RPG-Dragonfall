package me.xepos.rpg.datatypes.armoreffects;

import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class IncreaseDamageComponent implements IEffectComponent {
    private final boolean isPercentage;
    private final double amount;

    public IncreaseDamageComponent(String effect){
        String[] strings = effect.split(":");

        this.isPercentage = strings[1].endsWith("%");
        this.amount = Double.parseDouble(strings[1].substring(0, strings[1].length() - 1));
    }

    @Override
    public void activate(Event event) {
        if (event instanceof EntityDamageByEntityEvent e){
            if (isPercentage()) e.setDamage(e.getDamage() * (1 + getAmount() / 100));
            else e.setDamage(e.getDamage() + getAmount());
        }
    }

    public boolean isPercentage() {
        return isPercentage;
    }

    public double getAmount() {
        return amount;
    }
}
