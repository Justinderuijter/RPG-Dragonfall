package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.events.XRPGDamageTakenAddedEvent;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.tasks.RemoveDTModifierTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class Aegis extends XRPGSkill {

    public Aegis(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("RIGHT_CLICK").addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerInteractEvent)) return;

        doAegis((PlayerInteractEvent) event);
    }

    @Override
    public void initialize() {

    }

    @SuppressWarnings("unchecked")
    private void doAegis(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (player.getInventory().getItemInOffHand().getType() == Material.SHIELD) {
            if (!isSkillReady()) {
                player.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
                return;
            }

            final double duration = getSkillVariables().getDouble("duration", 4.0);
            final double xRange = getSkillVariables().getDouble("x-range", 8);
            final double yRange = getSkillVariables().getDouble("y-range", 5);
            final double zRange = getSkillVariables().getDouble("z-range", xRange);

            List<Player> nearbyPlayers = new ArrayList(player.getWorld().getNearbyEntities(player.getLocation(), xRange, yRange, zRange, p -> p instanceof Player && getPartyManager().isPlayerAllied(player, (Player) p)));
            for (Player target : nearbyPlayers) {
                //Applying the DTModifier if event is cancelled
                XRPGDamageTakenAddedEvent event = new XRPGDamageTakenAddedEvent(player, target, this, getDamageMultiplier());
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    XRPGPlayer xrpgTarget = getPlugin().getXRPGPlayer(target);
                    Utils.addDTModifier(xrpgTarget, getName(), getDamageMultiplier());
                    target.sendMessage(player.getDisplayName() + " Granted you " + getSkillName() + "!");

                    new RemoveDTModifierTask(player, xrpgTarget, this).runTaskLater(getPlugin(), (long) duration * 20);
                }
            }
            if (nearbyPlayers.size() > 0) {
                player.sendMessage(ChatColor.GREEN + "Applied " + getSkillName() + " to " + nearbyPlayers.size() + " player(s)!");
                setRemainingCooldown(getCooldown());
            }
        }

    }


}
