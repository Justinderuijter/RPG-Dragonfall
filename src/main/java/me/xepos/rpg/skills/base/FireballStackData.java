package me.xepos.rpg.skills.base;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;


public class FireballStackData extends XRPGPassiveSkill {
    private byte fireBallStacks = 0;
    private final byte maxFireballStacks = (byte) getSkillVariables().getInt("max-stacks", 2);
    private long lastStackGained = System.currentTimeMillis();


    public FireballStackData(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        setRemainingCooldown(-1);
        xrpgPlayer.getEventHandler("RIGHT_CLICK").addSkill(this.getClass().getSimpleName() ,this);
        xrpgPlayer.getEventHandler("SNEAK_RIGHT_CLICK").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerInteractEvent)) return;
        PlayerInteractEvent e = (PlayerInteractEvent) event;

        if (lastStackGained + getCooldown() * 2000L < System.currentTimeMillis() && fireBallStacks != 0) {
            fireBallStacks = 0;
            TextComponent text = new TextComponent("Fireball stacks lost!");
            text.setColor(ChatColor.RED.asBungee());
            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, text);
        }
    }

    @Override
    public void initialize() {

    }

    public byte getFireBallStacks() {
        return fireBallStacks;
    }

    public void setFireBallStacks(byte fireBallStacks) {
        this.fireBallStacks = fireBallStacks;
    }

    public byte getMaxFireballStacks() {
        return maxFireballStacks;
    }

    public long getLastStackGained() {
        return lastStackGained;
    }

    public void setLastStackGained(long lastStackGained) {
        this.lastStackGained = lastStackGained;
    }
}
