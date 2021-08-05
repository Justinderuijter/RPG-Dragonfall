package me.xepos.rpg.datatypes.armoreffects;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class IncreaseLootComponent implements IEffectComponent{
    private final boolean isPercentage;
    private final double amount;

    public IncreaseLootComponent(String effect){
        String[] strings = effect.split(":");

        this.isPercentage = strings[1].endsWith("%");
        this.amount = Double.parseDouble(strings[1].substring(0, strings[1].length() - 1));
    }

    @Override
    public void activate(Event event) {
        if (event instanceof EntityDeathEvent e){
            if (e.getEntity() instanceof Player) return;
            if (isPercentage){
                for (ItemStack item:e.getDrops()) {
                    int amount = (int)(item.getAmount() * (1 + this.amount / 100));
                    item.setAmount(amount);
                }
            }else{
                for (ItemStack item:e.getDrops()) {
                    item.setAmount(item.getAmount() + (int)this.amount);
                }
            }
        }
    }

}
