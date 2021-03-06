package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.enums.SpellType;
import me.xepos.rpg.events.XRPGDamageTakenAddedEvent;
import me.xepos.rpg.events.XRPGSpellCastEvent;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import me.xepos.rpg.tasks.RemoveDTModifierTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.List;

public class Aegis extends XRPGPassiveSkill {
    private final static SpellType[] spelltypes = new SpellType[]{SpellType.PASSIVE, SpellType.AOEBUFF};

    public Aegis(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

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

            XRPGSpellCastEvent spellCastEvent = new XRPGSpellCastEvent(this, spelltypes);
            Bukkit.getServer().getPluginManager().callEvent(spellCastEvent);

            if (spellCastEvent.isCancelled()) return;

            final double duration = getSkillVariables().getDouble(getSkillLevel(), "duration", 4.0);
            final double xRange = getSkillVariables().getDouble(getSkillLevel(), "x-range", 8);
            final double yRange = getSkillVariables().getDouble(getSkillLevel(), "y-range", 5);
            final double zRange = getSkillVariables().getDouble(getSkillLevel(), "z-range", xRange);

            List<Player> nearbyPlayers = new ArrayList(player.getWorld().getNearbyEntities(player.getLocation(), xRange, yRange, zRange, p -> p instanceof Player && getPartySet().isPlayerAllied(player, (Player) p)));
            for (Player target : nearbyPlayers) {
                if (!canApplyBuffToFriendly(target)) continue;

                XRPGDamageTakenAddedEvent event = new XRPGDamageTakenAddedEvent(player, target, this, getDamageMultiplier());
                Bukkit.getServer().getPluginManager().callEvent(event);
                XRPGPlayer xrpgTarget = getPlugin().getPlayerManager().getXRPGPlayer(target, true);
                if (xrpgTarget != null && !event.isCancelled()) {
                    Utils.addDTModifier(xrpgTarget, getSkillName(), getSkillVariables().getDouble(getSkillLevel(), "damage-reduction", 5) / 100);
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
