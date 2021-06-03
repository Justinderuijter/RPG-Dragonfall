package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.handlers.PassiveEventHandler;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.Event;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;

public class MasterEnchanter extends XRPGPassiveSkill {
    public MasterEnchanter(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        if (!xrpgPlayer.getPassiveHandlerList().containsKey("ENCHANT")){
            xrpgPlayer.getPassiveHandlerList().put("ENCHANT", new PassiveEventHandler());
        }

        xrpgPlayer.getPassiveEventHandler("ENCHANT").addSkill(this.getClass().getSimpleName(), this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PrepareItemEnchantEvent)) return;
        PrepareItemEnchantEvent e = (PrepareItemEnchantEvent) event;

        for (EnchantmentOffer offer:e.getOffers()) {
            offer.setCost(Math.round(offer.getCost() / 2F));
        }
    }

    @Override
    public void initialize() {

    }
}
