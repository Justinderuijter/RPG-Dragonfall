package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ExplosiveProjectileData;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Fireball;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.util.Vector;

public class Meteor extends XRPGActiveSkill {
    private me.xepos.rpg.skills.Fireball fireball;

    public Meteor(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

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
        for (XRPGSkill skill : getXRPGPlayer().getActiveHandler().getSkills().values()) {
            if (skill instanceof me.xepos.rpg.skills.Fireball) {
                this.fireball = ((me.xepos.rpg.skills.Fireball) skill);
                return;
            }
        }
    }

    private void doMeteor(PlayerItemHeldEvent e) {
        if (!isSkillReady()) {
            e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        int range = getSkillVariables().getInt(getSkillLevel(), "range", 32);
        final float explosionYield = (float) getSkillVariables().getDouble(getSkillLevel(), "explosion-yield", 2.0);
        final boolean setFire = getSkillVariables().getBoolean(getSkillLevel(), "explosion-fire", false);
        final boolean breakBlocks = getSkillVariables().getBoolean(getSkillLevel(), "explosion-break-block", false);

        //Meteor Skill logic
        Location loc = Utils.getTargetBlock(e.getPlayer(), range).getLocation();

        int stacks = 0;
        if (fireball != null) {
            stacks = fireball.getFireBallStacks();
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
        updatedCasterMana();
    }
}
