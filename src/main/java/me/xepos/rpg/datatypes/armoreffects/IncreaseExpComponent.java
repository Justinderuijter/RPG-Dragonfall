package me.xepos.rpg.datatypes.armoreffects;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class IncreaseExpComponent implements IEffectComponent {
    private final boolean isPercentage;
    private final double amount;

    public IncreaseExpComponent(String effect) {

        String[] strings = effect.split(":");
        this.isPercentage = strings[1].endsWith("%");
        this.amount = Double.parseDouble(strings[1].substring(0, strings[1].length() - 1));
    }

    @Override
    public void activate(Event event) {
        if (event instanceof PlayerExpChangeEvent e && e.getAmount() > 0){
            if (isPercentage()) e.setAmount((int)(e.getAmount() * (1 + getAmount() / 100)));
            else e.setAmount(e.getAmount() + (int)getAmount());
        }
    }

    public boolean isPercentage() {
        return isPercentage;
    }

    public double getAmount() {
        return amount;
    }
}
