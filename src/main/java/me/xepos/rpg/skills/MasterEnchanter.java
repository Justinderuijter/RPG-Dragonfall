package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.handlers.PassiveEventHandler;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.Event;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;

public class MasterEnchanter extends XRPGPassiveSkill {
    public MasterEnchanter(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        if (!xrpgPlayer.getPassiveHandlerList().containsKey("ENCHANT")){
            xrpgPlayer.getPassiveHandlerList().put("ENCHANT", new PassiveEventHandler());
        }

        xrpgPlayer.getPassiveEventHandler("ENCHANT").addSkill(this.getClass().getSimpleName(), this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PrepareItemEnchantEvent)) return;
        PrepareItemEnchantEvent e = (PrepareItemEnchantEvent) event;

        float discount = (float) (1 - getSkillVariables().getDouble(getSkillLevel(), "enchanting-discount", 17.5) / 100);
        for (EnchantmentOffer offer:e.getOffers()) {
            offer.setCost(Math.round(offer.getCost() * discount));
        }
    }

    @Override
    public void initialize() {

    }
}
