package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class BoneShield extends XRPGPassiveSkill {

    public BoneShield(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        xrpgPlayer.getPassiveEventHandler("DAMAGE_TAKEN").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
        Player player = (Player) e.getEntity();


        if (isSkillReady()) {
            double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            double threshold = getSkillVariables().getDouble(getSkillLevel(), "threshold", 50.0);

            if (player.getHealth() <= maxHealth / (100 / threshold)) {

                final double heartsPerFollower = getSkillVariables().getDouble(getSkillLevel(), "shield-per-follower", 2.0);

                player.setAbsorptionAmount(player.getAbsorptionAmount() + heartsPerFollower);
                player.sendMessage(ChatColor.DARK_GREEN + getSkillName() + " will absorb " + heartsPerFollower + " damage!");
                player.sendMessage(Utils.getPassiveCooldownMessage(getSkillName(), getCooldown()));
                setRemainingCooldown(getCooldown());
            }
        }
    }

    @Override
    public void initialize() {

    }
}
