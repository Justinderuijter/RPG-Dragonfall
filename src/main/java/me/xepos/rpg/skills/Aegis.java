package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.events.XRPGDamageTakenAddedEvent;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import me.xepos.rpg.tasks.RemoveDTModifierTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.List;

public class Aegis extends XRPGPassiveSkill {

    public Aegis(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getPassiveEventHandler("DAMAGE_TAKEN").addSkill(this.getClass().getSimpleName(), this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;

        doAegis((EntityDamageByEntityEvent) event);
    }

    @Override
    public void initialize() {

    }

    @SuppressWarnings("unchecked")
    private void doAegis(EntityDamageByEntityEvent e) {
        Player player = (Player) e.getEntity();
        if (player.getInventory().getItemInOffHand().getType() == Material.SHIELD) {

            if (!isSkillReady()) {
                return;
            }

            final double duration = getSkillVariables().getDouble("duration", 4.0);
            final double xRange = getSkillVariables().getDouble("x-range", 8);
            final double yRange = getSkillVariables().getDouble("y-range", 5);
            final double zRange = getSkillVariables().getDouble("z-range", xRange);

            List<Player> nearbyPlayers = new ArrayList(player.getWorld().getNearbyEntities(player.getLocation(), xRange, yRange, zRange, p -> p instanceof Player && getPartyManager().isPlayerAllied(player, (Player) p)));
            for (Player target : nearbyPlayers) {
                if (!canApplyBuffToFriendly(target)) continue;

                XRPGDamageTakenAddedEvent event = new XRPGDamageTakenAddedEvent(player, target, this, getDamageMultiplier());
                Bukkit.getServer().getPluginManager().callEvent(event);
                XRPGPlayer xrpgTarget = getPlugin().getXRPGPlayer(target, true);
                if (xrpgTarget != null && !event.isCancelled()) {
                    Utils.addDTModifier(xrpgTarget, getSkillName(), getDamageMultiplier());
                    target.sendMessage(player.getDisplayName() + " Granted you " + getSkillName() + "!");

                    new RemoveDTModifierTask(player, xrpgTarget, this).runTaskLaterAsynchronously(getPlugin(), (long) duration * 20);
                }

            }
            if (nearbyPlayers.size() > 0) {
                player.sendMessage(ChatColor.GREEN + "Applied " + getSkillName() + " to " + nearbyPlayers.size() + " player(s)!");
                setRemainingCooldown(getCooldown());
            }
        }

    }


}
