package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.ArrayList;
import java.util.List;

public class Whirlwind extends XRPGActiveSkill {
    public Whirlwind(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    @SuppressWarnings("all")
    public void activate(Event event) {
        if(!(event instanceof PlayerItemHeldEvent)) return;
        PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;

        if (!isSkillReady()){
            e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        Player caster = e.getPlayer();
        final double horizontalRange = getSkillVariables().getDouble(getSkillLevel(), "horizontal-range", 5);
        final double verticalRange = getSkillVariables().getDouble(getSkillLevel(), "vertical-range", 2);

        List<LivingEntity> targets = new ArrayList(caster.getWorld().getNearbyEntities(caster.getLocation(), horizontalRange, verticalRange, horizontalRange, p -> p instanceof LivingEntity && p != caster));
        for (LivingEntity target:targets) {
            target.damage(getDamage(), caster);
        }

        setRemainingCooldown(getCooldown());
        updatedCasterMana();
    }

    @Override
    public void initialize() {

    }
}
