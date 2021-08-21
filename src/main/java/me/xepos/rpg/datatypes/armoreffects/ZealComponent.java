package me.xepos.rpg.datatypes.armoreffects;

import me.xepos.rpg.XRPG;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ZealComponent implements IEffectComponent{
    private final double chance;
    private final byte extraHits;

    public ZealComponent(String effect){
        String[] strings = effect.split(":");

        byte hits = Byte.parseByte(strings[1]);

        if (hits < 1) hits = 1;

        this.extraHits = hits;

        if (strings.length == 2){
            this.chance = 100;
            return;
        }

        this.chance = Double.parseDouble(strings[2]);
    }

    @Override
    public void activate(Event event) {
        if (event instanceof EntityDamageByEntityEvent e){
            final LivingEntity target =  (LivingEntity) e.getEntity();
            LivingEntity attacker;

            if (e.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof LivingEntity){
                attacker = (LivingEntity) projectile.getShooter();
            }else{
                attacker = (LivingEntity) e.getDamager();
            }

            final double damage = e.getDamage();
            for (int i = 0; i < extraHits; i++) {
                Bukkit.getScheduler().runTaskLater(XRPG.getInstance(), () -> {
                    attacker.sendMessage("Attack launched");
                    target.setNoDamageTicks(0);
                    target.damage(damage, attacker);
                }, (i + 1) * 5);

            }
        }
    }
}
