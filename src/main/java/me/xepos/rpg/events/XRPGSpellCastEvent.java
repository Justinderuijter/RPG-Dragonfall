package me.xepos.rpg.events;

import me.xepos.rpg.enums.SpellType;
import me.xepos.rpg.skills.base.XRPGSkill;

public class XRPGSpellCastEvent extends AbstractSpellCastEvent {
    public XRPGSpellCastEvent(XRPGSkill skill, SpellType[] spellTypes) {
        super(skill, spellTypes);
    }
}
