package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGSkill;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Locale;

public class Fortified extends XRPGSkill {
    public Fortified(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("DAMAGE_TAKEN").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

        double dmg = e.getDamage() * getDamageMultiplier();
        e.setDamage(dmg);

        if (!getSkillVariables().getBoolean("show-reduction", false)) return;
        TextComponent text = new TextComponent("Damage taken reduced by " + String.format(
                Locale.GERMAN, "%,.2f", dmg));
        text.setColor(ChatColor.GREEN.asBungee());
        ((Player) e.getEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR, text);
    }

    @Override
    public void initialize() {

    }
}
