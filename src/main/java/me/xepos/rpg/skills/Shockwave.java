package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockIterator;

public class Shockwave extends XRPGSkill {
    public Shockwave(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("RIGHT_CLICK").addSkill(this.getClass().getSimpleName(), this);
    }

    @Override
    public void activate(Event event) {
        if (event instanceof PlayerInteractEvent){
            PlayerInteractEvent e = (PlayerInteractEvent) event;

            Player player = e.getPlayer();

            if (!isSkillReady()){
                player.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
                return;
            }

            BlockIterator iter = new BlockIterator(player.getWorld(), player.getLocation().toVector(), player.getEyeLocation().toVector(), -2, 16);
            Block lastBlock;
            while (iter.hasNext()) {
                lastBlock = iter.next();
                if (lastBlock.getType() == Material.AIR || lastBlock.isLiquid()) {
                    continue;
                }
                lastBlock.getWorld().playEffect(lastBlock.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
            }

            setRemainingCooldown(getCooldown());
        }
    }

    @Override
    public void initialize() {

    }
}
