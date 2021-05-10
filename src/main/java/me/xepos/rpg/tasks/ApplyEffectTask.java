package me.xepos.rpg.tasks;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class ApplyEffectTask extends BukkitRunnable
{
    private final PotionEffect effect;
    private final LivingEntity livingEntity;

    public ApplyEffectTask(LivingEntity livingEntity, PotionEffect effect)
    {
        this.livingEntity = livingEntity;
        this.effect = effect;
    }

    @Override
    public void run()
    {
        livingEntity.addPotionEffect(effect);
    }
}
