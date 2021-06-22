package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class ShortFuse extends XRPGActiveSkill {
    public ShortFuse(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent)) return;
        PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;
        Player player = e.getPlayer();


        if (!isSkillReady()){
            player.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        final float yield = (float)getSkillVariables().getDouble(getSkillLevel(), "explosion-yield", 3.0);
        final boolean setFire = getSkillVariables().getBoolean(getSkillLevel(), "explosion-set-fire", false);
        final boolean breakBlocks = getSkillVariables().getBoolean(getSkillLevel(),"explosion-break-blocks", false);

        Location location = player.getLocation();
        if (location.getWorld() == null) return;

        setRemainingCooldown(getCooldown());
        updatedCasterMana();

        location.getWorld().playSound(location, Sound.ENTITY_CREEPER_PRIMED, 1.5F, 1F);
        location.getWorld().playEffect(location, Effect.SMOKE, 1);

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            player.getWorld().createExplosion(player.getLocation(), yield, setFire, breakBlocks, player);
        }, (long)getSkillVariables().getDouble(getSkillLevel(), "fuse", 1.5) * 20);

    }

    @Override
    public void initialize() {

    }
}
