package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.util.BlockIterator;

public class Shockwave extends XRPGActiveSkill {
    public Shockwave(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (event instanceof PlayerItemHeldEvent){
            PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;

            Player player = e.getPlayer();

            if (!isSkillReady()){
                player.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
                return;
            }

            BlockIterator iter = new BlockIterator(player.getWorld(), player.getLocation().toVector(), player.getLocation().getDirection().setY(0), -1, 16);
            Block lastBlock;
            while (iter.hasNext()) {
                lastBlock = iter.next();
                if (lastBlock.getType() == Material.AIR || lastBlock.isLiquid()) {
                    Block newBlock = lastBlock.getLocation().clone().subtract(0,1,0).getBlock();
                    if(newBlock.getType() != Material.AIR && !newBlock.isLiquid()){
                        lastBlock = newBlock;
                    }else{
                        break;
                    }
                }
                lastBlock.getWorld().playEffect(lastBlock.getLocation(), Effect.MOBSPAWNER_FLAMES, 2);
            }
            setRemainingCooldown(getCooldown());
        }
    }

    @Override
    public void initialize() {

    }
}
