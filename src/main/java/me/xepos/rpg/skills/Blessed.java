package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.handlers.PassiveEventHandler;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

public class Blessed extends XRPGPassiveSkill {
    private static int manaPerLevel = -1;

    public Blessed(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        if (!xrpgPlayer.getPassiveHandlerList().containsKey("ATTRIBUTE")){
            xrpgPlayer.addPassiveEventHandler("ATTRIBUTE", new PassiveEventHandler());
        }

        xrpgPlayer.getPassiveEventHandler("ATTRIBUTE").addSkill(this.getClass().getSimpleName(), this);


        final int baseMana = getSkillVariables().getInt("base-mana-increase", 1);

        if (manaPerLevel == -1)
            manaPerLevel = getSkillVariables().getInt("mana-per-level", 2);

        final int manaIncrease = baseMana + manaPerLevel * (skillLevel -1);

        xrpgPlayer.setBaseMana(plugin.getClassInfo(xrpgPlayer.getClassId()).getBaseMana() + manaIncrease);
    }

    @Override
    public void activate(Event event) {

    }

    @Override
    public void initialize() {

    }

    @Override
    public void setSkillLevel(int skillLevel){
        final int levelDifference = skillLevel - getSkillLevel();
        getXRPGPlayer().setBaseMana(getXRPGPlayer().getBaseMana() + levelDifference * manaPerLevel);

        super.setSkillLevel(skillLevel);
    }
}
