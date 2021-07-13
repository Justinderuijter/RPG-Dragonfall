package me.xepos.rpg.tasks;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.dependencies.combat.parties.PartySet;
import me.xepos.rpg.dependencies.combat.protection.ProtectionSet;
import me.xepos.rpg.events.XRPGDamageTakenAddedEvent;
import me.xepos.rpg.events.XRPGDamageTakenRemovedEvent;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Bat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class PurgatoryBatTask extends BukkitRunnable {

    private byte count = 0;
    private final byte maxCount;
    private final Bat bat;
    private final Player player;
    private final double damage;
    private final double dtAmount;
    private final XRPGSkill skill;
    private final PartySet partyManager;
    private final ProtectionSet protectionSet;
    private final XRPG plugin;
    private final long delay;


    public PurgatoryBatTask(Bat bat, Player player, double damage, byte maxCount, XRPGSkill skill, double dtAmount, XRPG plugin, long debuffDuration) {
        this.bat = bat;
        this.player = player;
        this.damage = damage;
        this.dtAmount = dtAmount;
        this.maxCount = maxCount;
        this.skill = skill;
        this.partyManager = plugin.getPartySet();
        this.protectionSet = plugin.getProtectionSet();
        this.plugin = plugin;
        this.delay = debuffDuration;
    }

    @Override
    public void run() {
        if (count >= maxCount) {
            bat.remove();
            this.cancel();
            return;
        }


        List<LivingEntity> livingEntities = new ArrayList(bat.getWorld().getNearbyEntities(bat.getLocation(), 2, 2, 2, p -> p instanceof LivingEntity && p != player && p != bat));
        for (LivingEntity entity : livingEntities) {
            if (entity instanceof Player) {
                Player target = (Player) entity;
                if (protectionSet.isLocationValid(player.getLocation(), entity.getLocation()) && partyManager.isPlayerAllied(player, target)) {
                    target.damage(damage, player);

                    if (count == 0) {
                        XRPGPlayer xrpgTarget = plugin.getPlayerManager().getXRPGPlayer(target, true);
                        XRPGDamageTakenAddedEvent event = new XRPGDamageTakenAddedEvent(player, target, skill, dtAmount);
                        Bukkit.getServer().getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            Utils.addDTModifier(xrpgTarget, skill.getSkillName(), dtAmount);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    XRPGDamageTakenRemovedEvent eventRemove = new XRPGDamageTakenRemovedEvent(player, target, skill);
                                    Bukkit.getServer().getPluginManager().callEvent(eventRemove);
                                }
                            }.runTaskLater(plugin, delay);
                        }
                    }
                }
            } else {
                entity.damage(damage, player);

            }
        }
        count++;
    }
}
