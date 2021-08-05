package me.xepos.rpg.datatypes.armoreffects;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;

public class PotionComponent implements IEffectComponent{
    private final PotionEffect potionEffect;
    private final EffectTarget effectTarget;

    public PotionComponent(String effect){
        final String attacker = "%attacker%";
        final String victim = "%victim%";

        String[] strings = effect.split(":");


        PotionEffectType potionEffectType = PotionEffectType.getByName(strings[1].toUpperCase());
        final int duration = getDuration(strings[2]);
        final int amplifier = getAmplifier(strings[3]);
        if (potionEffectType == null){
            Bukkit.getLogger().warning("Could not find potion effect " + strings[1].toUpperCase() + "!");
            Bukkit.getLogger().warning("Defaulting to HEAL!");
            potionEffectType = PotionEffectType.HEAL;
        }
        potionEffect = new PotionEffect(potionEffectType, duration, amplifier, false, false, true);

        //Determine the target
        int length = strings.length -1;
        if (strings[length].endsWith(victim)){
            this.effectTarget = EffectTarget.VICTIM;
            strings[length] = strings[length].substring(0, strings[length].length() - (victim.length() + 1));
        }else if(strings[length].endsWith(attacker)){
            this.effectTarget = EffectTarget.ATTACKER;
            strings[length] = strings[length].substring(0, strings[length].length() - (attacker.length() + 1));
        }else{
            this.effectTarget = EffectTarget.ATTACKER;
        }
    }

    @Override
    public void activate(Event event) {
        LivingEntity target = getTarget(event);
        if (target != null) target.addPotionEffect(potionEffect);
    }

    private @Nullable LivingEntity getTarget(Event event){
        if (event instanceof PlayerEvent e){
            if (e instanceof PlayerFishEvent fishEvent && fishEvent.getCaught() instanceof LivingEntity caught && effectTarget == EffectTarget.VICTIM){
                return caught;
            }
            return e.getPlayer();
        }else if(event instanceof EntityDamageByEntityEvent e){
            if (effectTarget == EffectTarget.ATTACKER) return (LivingEntity) e.getDamager();
            else return (LivingEntity) e.getEntity();
        }else if(event instanceof EntityDeathEvent e){
            if (effectTarget == EffectTarget.ATTACKER) return e.getEntity().getKiller();
            else return e.getEntity();
        }
        return null;
    }

    private int getDuration(String durationString){
        if (durationString.equalsIgnoreCase("MAX")){
            return Integer.MAX_VALUE;
        }else{
            Integer duration = Integer.getInteger(durationString);

            return duration == null ? Integer.MAX_VALUE : duration;
        }
    }

    private byte getAmplifier(String amplifierString){
        if (amplifierString.equalsIgnoreCase("MAX")){
            return Byte.MAX_VALUE;
        }else{
            byte amplifier;

            try{
                amplifier = Byte.parseByte(amplifierString);
            }catch (NumberFormatException e){
                amplifier = Byte.MAX_VALUE;
            }

            return amplifier;
        }
    }
}
