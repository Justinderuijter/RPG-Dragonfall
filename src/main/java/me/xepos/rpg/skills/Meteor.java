package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ExplosiveProjectileData;
import me.xepos.rpg.skills.base.FireballStackData;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Fireball;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.util.Vector;

public class Meteor extends XRPGActiveSkill {
    private FireballStackData fireballStackData;

    public Meteor(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, FireballStackData fireballStackData) {
        super(xrpgPlayer, skillVariables, plugin);

        this.fireballStackData = fireballStackData;
        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    public Meteor(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getPassiveEventHandler("LEFT_CLICK").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent)) return;
        PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;

        doMeteor(e);
    }

    @Override
    public void initialize() {
        for (XRPGSkill skill : getXRPGPlayer().getPassiveEventHandler("RIGHT_CLICK").getSkills().values()) {
            if (skill instanceof me.xepos.rpg.skills.Fireball) {
                this.fireballStackData = ((me.xepos.rpg.skills.Fireball) skill).getFireballStackData();
                return;
            }
        }
    }

    private void doMeteor(PlayerItemHeldEvent e) {
        if (!isSkillReady()) {
            e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }
        int range = getSkillVariables().getInt("range", 32);
        final float explosionYield = (float) getSkillVariables().getDouble("explosion-yield", 2.0);
        final boolean setFire = getSkillVariables().getBoolean("explosion-fire", false);
        final boolean breakBlocks = getSkillVariables().getBoolean("explosion-break-block", false);

        //Meteor Skill logic
        Location loc = Utils.getTargetBlock(e.getPlayer(), range).getLocation();

        int stacks = 0;
        if (fireballStackData != null) {
            stacks = fireballStackData.getFireBallStacks();
        }
        loc.setY(loc.getY() + 15 - stacks * 2);
        org.bukkit.entity.Fireball fireball = loc.getWorld().spawn(loc, Fireball.class);
        fireball.setShooter(e.getPlayer());
        fireball.setDirection(new Vector(0, -1, 0));

        if (!getPlugin().projectiles.containsKey(fireball.getUniqueId())){
            ExplosiveProjectileData data = new ExplosiveProjectileData(fireball, explosionYield * (stacks + 1), 20);
            data.setsFire(setFire);
            data.destroysBlocks(breakBlocks);

            getPlugin().projectiles.put(fireball.getUniqueId(), data);
        }


        setRemainingCooldown(getCooldown());
    }
}
