package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.util.RayTraceResult;

import java.util.ArrayList;
import java.util.List;

public class PhoenixBlessing extends XRPGActiveSkill {

    public PhoenixBlessing(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName(), this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent)) return;
        PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;

        Player player = e.getPlayer();

        if (!isSkillReady()) {
            player.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        RayTraceResult result = player.getLocation().getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 16, FluidCollisionMode.NEVER, true, 0.3, p -> p instanceof LivingEntity && p != player);
        if (result != null && result.getHitEntity() != null) {

            final int duration = getSkillVariables().getInt(getSkillLevel(), "duration", 5);

            LivingEntity entity = (LivingEntity) result.getHitEntity();

            if (entity instanceof Player) {
                if (getProtectionSet().isLocationValid(player.getLocation(), null)) {
                    entity.setNoDamageTicks(duration * 20);
                }
            } else {
                entity.setNoDamageTicks(duration * 20);
            }

            player.sendMessage(ChatColor.DARK_GREEN + "You applied " + getSkillName() + " to " + entity.getName() + "!");

            List<Player> nearbyPlayers = new ArrayList(entity.getLocation().getWorld().getNearbyEntities(entity.getLocation(), 16, 16, 16, p -> p instanceof Player && p != player));
            for (Player nearbyPlayer : nearbyPlayers) {
                nearbyPlayer.sendMessage(ChatColor.RED + player.getName() + " applied " + getSkillName() + " to " + entity.getName() + " for " + duration + " seconds!");
            }

            setRemainingCooldown(getCooldown());
            updatedCasterMana();
        }


    }

    @Override
    public void initialize() {

    }
}
