package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Locale;

public class Fortified extends XRPGPassiveSkill {
    public Fortified(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        xrpgPlayer.getPassiveEventHandler("DAMAGE_TAKEN").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

        double dmg = e.getDamage() * getDamageMultiplier();
        e.setDamage(dmg);

        if (!getSkillVariables().getBoolean(getSkillLevel(), "show-reduction", false)) return;
        TextComponent text = new TextComponent("Damage taken reduced by " + String.format(
                Locale.GERMAN, "%,.2f", dmg));
        text.setColor(ChatColor.GREEN.asBungee());
        ((Player) e.getEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR, text);
    }

    @Override
    public void initialize() {

    }
}
