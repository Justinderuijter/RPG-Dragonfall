package me.xepos.rpg.events;

import me.xepos.rpg.skills.base.XRPGSkill;

public interface SpellCastEvent {
    void onSpellCast(XRPGSkill skill);
}
