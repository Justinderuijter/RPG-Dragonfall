package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.enums.SpellType;
import me.xepos.rpg.handlers.PassiveEventHandler;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class MasterEnchanter extends XRPGPassiveSkill {
    private final static SpellType[] spelltypes = new SpellType[]{SpellType.PASSIVE};

    public MasterEnchanter(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        if (!xrpgPlayer.getPassiveHandlerList().containsKey("EXP_GAIN")){
            xrpgPlayer.getPassiveHandlerList().put("EXP_GAIN", new PassiveEventHandler());
        }

        xrpgPlayer.getPassiveEventHandler("EXP_GAIN").addSkill(this.getClass().getSimpleName(), this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerExpChangeEvent e)) return;
        if (e.getAmount() >= 0) return;

        e.setAmount((int)(e.getAmount() * (1 + getSkillVariables().getDouble(getSkillLevel(), "experience-multiplier", 25) / 100)));
    }

    @Override
    public void initialize() {

    }
}
