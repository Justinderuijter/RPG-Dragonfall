package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ExplosiveProjectileData;
import me.xepos.rpg.skills.base.FireballStackData;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Fireball;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class Meteor extends XRPGSkill {
    private FireballStackData fireballStackData;

    public Meteor(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, FireballStackData fireballStackData) {
        super(xrpgPlayer, skillVariables, plugin);

        this.fireballStackData = fireballStackData;
        xrpgPlayer.getEventHandler("LEFT_CLICK").addSkill(this);
    }

    public Meteor(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("LEFT_CLICK").addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!hasCastItem()) return;
        if (!(event instanceof PlayerInteractEvent)) return;
        PlayerInteractEvent e = (PlayerInteractEvent) event;
        if (e.getItem() == null || e.getItem().getType() != Material.BLAZE_ROD) return;

        doMeteor(e);
    }

    @Override
    public void initialize() {
        for (XRPGSkill skill : getXRPGPlayer().getEventHandler("RIGHT_CLICK").getSkills()) {
            if (skill instanceof me.xepos.rpg.skills.Fireball) {
                this.fireballStackData = ((me.xepos.rpg.skills.Fireball) skill).getFireballStackData();
                return;
            }
        }
    }

    private void doMeteor(PlayerInteractEvent e) {
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
