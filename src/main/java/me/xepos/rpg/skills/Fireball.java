package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ProjectileData;
import me.xepos.rpg.skills.base.FireballStackData;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class Fireball extends XRPGSkill {
    private final FireballStackData fireballStackData;

    public Fireball(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        fireballStackData = new FireballStackData(xrpgPlayer, skillVariables, plugin);
        xrpgPlayer.getEventHandler("RIGHT_CLICK").addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!hasCastItem()) return;
        if (!(event instanceof PlayerInteractEvent)) return;
        PlayerInteractEvent e = (PlayerInteractEvent) event;

        doFireball(e);
    }

    @Override
    public void initialize() {

    }

    private void doFireball(PlayerInteractEvent e) {
        //Cancel if skill is still on cooldown and send a message.
        if (!isSkillReady()) {
            e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }

        //Skill logic
        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1F, 1F);
        org.bukkit.entity.Fireball fireball = e.getPlayer().launchProjectile(SmallFireball.class);
        fireball.setShooter(e.getPlayer());

        if (!getPlugin().projectiles.containsKey(fireball.getUniqueId())) {
            //For some reason damage is halved so doubling it to get proper value
            getPlugin().projectiles.put(fireball.getUniqueId(), new ProjectileData(fireball, getDamage() * 2, false, false, 10));
        }

        this.incrementFireBallStacks(this.fireballStackData.getMaxFireballStacks());
        this.fireballStackData.setLastStackGained(System.currentTimeMillis());
        setRemainingCooldown(getCooldown());

        TextComponent text = new TextComponent("You now have " + this.fireballStackData.getFireBallStacks() + " " + getSkillName() + " stacks");
        text.setColor(ChatColor.DARK_GREEN.asBungee());
        e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, text);
    }

    private void incrementFireBallStacks(byte maxFireballStacks) {
        if (this.fireballStackData.getFireBallStacks() < maxFireballStacks) {
            this.fireballStackData.setFireBallStacks((byte) (this.fireballStackData.getFireBallStacks() + 1));
        }
    }

    public FireballStackData getFireballStackData() {
        return fireballStackData;
    }
}
