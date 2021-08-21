package me.xepos.rpg.datatypes.armoreffects;

import me.xepos.rpg.utils.Utils;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerEvent;

public class HealComponent implements IEffectComponent{
    private final boolean isPercentage;
    private final double amount;
    private final EffectTarget effectTarget;

    public HealComponent(String effect){
        final String victim = "%victim%";
        String[] strings = effect.split(":");

        this.isPercentage = strings[1].endsWith("%");
        this.amount = Double.parseDouble(strings[1].substring(0, strings[1].length() - 1));
        if (strings[strings.length - 1].endsWith(victim)){
            effectTarget = EffectTarget.VICTIM;
        }else{
            effectTarget = EffectTarget.ATTACKER;
        }
    }


    @Override
    public void activate(Event event) {
        if (event instanceof EntityDamageByEntityEvent e) {
            final LivingEntity livingEntity;
            if (e.getDamager() instanceof Projectile projectile) {
                livingEntity = (LivingEntity) projectile.getShooter();
            } else {
                livingEntity = (LivingEntity) e.getDamager();
            }
            e.getDamager().sendMessage("Heal: " + amount);
            doHeal(livingEntity);
        }else if(event instanceof PlayerDeathEvent e){
            if (amount > 0){
                e.setCancelled(true);
                doHeal(e.getEntity());
            }
        }else if(event instanceof PlayerEvent e){
            doHeal(e.getPlayer());
        }
    }

    private void doHeal(LivingEntity livingEntity){
        if (amount > 0){
            if (isPercentage){
                Utils.healLivingEntity(livingEntity, getPercentageHealth(livingEntity));
                return;
            }
            Utils.healLivingEntity(livingEntity, amount);
        }else if (amount < 0){
            if (isPercentage){
                Utils.decreaseHealth(livingEntity, getPercentageHealth(livingEntity) * -1);
                return;
            }
            Utils.decreaseHealth(livingEntity, amount * -1);
        }
    }

    private double getPercentageHealth(LivingEntity livingEntity){
        return livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / (100 / amount);
    }
}
