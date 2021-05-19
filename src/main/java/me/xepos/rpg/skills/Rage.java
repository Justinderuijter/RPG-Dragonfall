package me.xepos.rpg.skills;

import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.AttributeModifierData;
import me.xepos.rpg.enums.ModifierType;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import me.xepos.rpg.tasks.RavagerRageTask;
import me.xepos.rpg.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;

public class Rage extends XRPGPassiveSkill {
    private byte currentRage = 0;
    private byte lastRage = 0;
    private final byte maxRage = 100;
    private byte rageLevel = 0;
    private boolean isLocked = false;
    BukkitTask rageTask = null;

    public Rage(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        double attackSpeedMultiplier = skillVariables.getDouble("atk-spd-multiplier", 1.65) - 1;
        AttributeModifier mod = new AttributeModifier(UUID.fromString("1d7a09c9-b6e2-4dc7-ab6f-8831dffcb111"), "RAGE_ATK_SPD", attackSpeedMultiplier, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

        Bukkit.getLogger().info("name: " + mod.getName());
        AttributeModifierManager.getInstance().put(ModifierType.POSITIVE, mod.getName(), mod, Attribute.GENERIC_ATTACK_SPEED);

        setRemainingCooldown(-1);
        xrpgPlayer.getPassiveEventHandler("DAMAGE_DEALT").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

        Player player = (Player) e.getDamager();
        rageLevel = getRageLevel(currentRage);

        if (!isLocked) //Prevent infinite looping
            applyDamageRageEffect(e);

        //increase rage count
        if (((LivingEntity) e.getEntity()).getHealth() <= e.getFinalDamage()) {
            incrementRage((byte) getSkillVariables().getInt("bonus-rage-on-kill"));
        }

        incrementRage((byte) getSkillVariables().getInt("rage-on-hit"));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Current Rage: " + currentRage + " (+)", ChatColor.RED.asBungee()));
        if (rageTask == null || rageTask.isCancelled())
            rageTask = new RavagerRageTask(getXRPGPlayer(), this, (byte) 5).runTaskTimerAsynchronously(getPlugin(), 100L, 100L);


    }

    @Override
    public void initialize() {

    }

    private void applyDamageRageEffect(EntityDamageByEntityEvent e) {
        Player player = (Player) e.getDamager();
        ConfigurationSection skillVariable = getSkillVariables();
        AttributeModifierData attackSpeedModifierData = AttributeModifierManager.getInstance().get(ModifierType.POSITIVE, "RAGE_ATK_SPD");

        switch (rageLevel) {
            case 0:
                Utils.removeUniqueModifier(player, attackSpeedModifierData);
            case 1:
                Utils.removeUniqueModifier(player, attackSpeedModifierData);
                e.setDamage(e.getDamage() * skillVariable.getDouble("rage-one-multiplier", 1.1));
                break;
            case 2:
                Utils.removeUniqueModifier(player, attackSpeedModifierData);
                e.setDamage(e.getDamage() * skillVariable.getDouble("rage-two-multiplier", 1.2));
                break;
            case 3:
                Utils.addUniqueModifier(player, attackSpeedModifierData);
                e.setDamage(e.getDamage() * skillVariable.getDouble("rage-three-multiplier", 1.3));
                break;
            case 4:
                Location loc = player.getLocation();
                loc.getWorld().playSound(loc, Sound.BLOCK_FIRE_AMBIENT, 2F, 1F);
                Utils.addUniqueModifier(player, attackSpeedModifierData);

                isLocked = true;

                if (!player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100, 0, false, false, true));
                }

                e.setDamage(e.getDamage() * skillVariable.getDouble("rage-four-multiplier", 1.3));

                double rageRange = skillVariable.getDouble("range", 3);

                if (e.getEntity() instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) e.getEntity();
                    List<Entity> entities = livingEntity.getNearbyEntities(rageRange, 3, rageRange);
                    for (Entity entity : entities) {
                        if (entity instanceof LivingEntity && entity != livingEntity && entity != player && !(entity instanceof Villager)) {
                            ((LivingEntity) entity).damage(e.getDamage(), player);
                            if (entity instanceof Player && lastRage <= 80)
                                entity.sendMessage(ChatColor.RED + player.getName() + " gave in to their rage!");
                        }
                    }
                }
                isLocked = false;
                break;
        }
    }

    private void incrementRage(byte count) {
        if (currentRage + count <= 100)
            currentRage = (byte) (currentRage + count);
        else
            currentRage = maxRage;
    }

    public void decreaseCurrentRage(byte count) {
        if (currentRage >= count)
            currentRage = (byte) (currentRage - count);
        else
            currentRage = 0;
    }

    public byte getCurrentRage() {
        return currentRage;
    }

    private byte getRageLevel(byte currentRage) {
        lastRage = currentRage;
        if (currentRage > 80)
            return 4;
        else if (currentRage > 60)
            return 3;
        else if (currentRage > 40)
            return 2;
        else if (currentRage > 20)
            return 1;
        else
            return 0;

    }
}
