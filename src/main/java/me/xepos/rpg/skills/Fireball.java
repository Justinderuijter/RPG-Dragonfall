package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ProjectileData;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Sound;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class Fireball extends XRPGActiveSkill {
    private byte fireBallStacks = 0;
    private final byte maxFireballStacks = (byte) getSkillVariables().getInt(getSkillLevel(),"max-stacks", 2);
    private long lastStackGained = System.currentTimeMillis();

    public Fireball(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent e)) return;

        doFireball(e);
    }

    @Override
    public void initialize() {

    }

    private void doFireball(PlayerItemHeldEvent e) {
        //Cancel if skill is still on cooldown and send a message.
        if (!isSkillReady()) {
            e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        if (lastStackGained + getCooldown() * 2000L < System.currentTimeMillis() && fireBallStacks != 0) {
            fireBallStacks = 0;
        }

        //Skill logic
        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1F, 1F);
        org.bukkit.entity.Fireball fireball = e.getPlayer().launchProjectile(SmallFireball.class);
        fireball.setShooter(e.getPlayer());

        if (!getPlugin().projectiles.containsKey(fireball.getUniqueId())) {
            //For some reason damage is halved so doubling it to get proper value
            ProjectileData data = new ProjectileData(fireball, getDamage(),20);

            getPlugin().projectiles.put(fireball.getUniqueId(), data);
        }

        this.incrementFireBallStacks(this.maxFireballStacks);
        this.lastStackGained = System.currentTimeMillis();
        setRemainingCooldown(getCooldown());
        updatedCasterMana();
    }

    private void incrementFireBallStacks(byte maxFireballStacks) {
        if (this.fireBallStacks < maxFireballStacks) {
            this.fireBallStacks++;
        }
    }

    public byte getFireBallStacks() {
        return fireBallStacks;
    }
}
