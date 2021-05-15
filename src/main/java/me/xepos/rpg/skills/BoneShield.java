package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class BoneShield extends XRPGPassiveSkill {
    private ArmyOfTheUndead armyOfTheUndead;

    public BoneShield(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, @Nullable ArmyOfTheUndead armyOfTheUndead) {
        super(xrpgPlayer, skillVariables, plugin);

        this.armyOfTheUndead = armyOfTheUndead;
        xrpgPlayer.getEventHandler("DAMAGE_TAKEN").addSkill(this.getClass().getSimpleName() ,this);
    }

    public BoneShield(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("DAMAGE_TAKEN").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
        Player player = (Player) e.getEntity();


        if (isSkillReady()) {
            double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            double threshold = getSkillVariables().getDouble("threshold", 50.0);

            if (player.getHealth() <= maxHealth / (100 / threshold)) {

                final double heartsPerFollower = getSkillVariables().getDouble("shield-per-follower", 2.0);
                double absorptionHearts;
                if (armyOfTheUndead == null)
                    absorptionHearts = heartsPerFollower;
                else
                    absorptionHearts = this.armyOfTheUndead.getFollowerCount() * heartsPerFollower;

                player.setAbsorptionAmount(player.getAbsorptionAmount() + absorptionHearts);
                player.sendMessage(ChatColor.DARK_GREEN + getSkillName() + " will absorb " + absorptionHearts + " damage!");
                player.sendMessage(Utils.getPassiveCooldownMessage(getSkillName(), getCooldown()));
                setRemainingCooldown(getCooldown());
            }
        }
    }

    @Override
    public void initialize() {
        for (XRPGSkill skill : getXRPGPlayer().getEventHandler("DAMAGE DEALT").getSkills().values()) {
            if (skill instanceof ArmyOfTheUndead) {
                this.armyOfTheUndead = (ArmyOfTheUndead) skill;
                return;
            }
        }
    }
}
