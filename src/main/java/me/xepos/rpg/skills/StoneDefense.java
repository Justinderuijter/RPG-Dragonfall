package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.tasks.RemoveBlocklistTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.util.RayTraceResult;

import java.util.HashSet;
import java.util.Set;

public class StoneDefense extends XRPGActiveSkill {
    public StoneDefense(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName(), this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent)) return;
        PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;

        Player player = e.getPlayer();
        if (!isSkillReady()) {
            player.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        createWall(player);
    }

    @Override
    public void initialize() {

    }

    private void createWall(Player caster) {
        RayTraceResult result = caster.getLocation().getWorld().rayTraceBlocks(caster.getEyeLocation(), caster.getEyeLocation().getDirection(), 5, FluidCollisionMode.NEVER, true);

        if (result != null && result.getHitBlock() != null) {

            double rotation = (caster.getLocation().getYaw() - 90) % 360;
            if (rotation < 0) {
                rotation += 360.0;
            }
            if (0 <= rotation && rotation < 45) {
                //North
                createNorthWall(result.getHitBlock());
                caster.sendMessage("North");
            } else if (45 <= rotation && rotation < 135) {
                //East
                createEastWall(result.getHitBlock());
                caster.sendMessage("East");
            } else if (135 <= rotation && rotation < 225) {
                //South
                createSouthWall(result.getHitBlock());
                caster.sendMessage("South");
            } else if (225 <= rotation && rotation < 315) {
                //West
                createWestWall(result.getHitBlock());
                caster.sendMessage("West");
            } else if (315 <= rotation && rotation < 360.0) {
                //North
                createNorthWall(result.getHitBlock());
                caster.sendMessage("North");
            }

            setRemainingCooldown(getCooldown());
            updatedCasterMana();
        }
    }

    private void createNorthWall(Block targetBlock){
        Set<Location> replacedBlocksLocation = new HashSet<>();

        final int offset = calculateOffset();
        Block bottomLeft = targetBlock.getLocation().add(0, 1, offset * -1).getBlock();
        getXRPGPlayer().getPlayer().sendMessage("Bottom left: " + bottomLeft.getLocation().getZ());

        for (int y = 0; y < 3; y++) {
            for (int z = 0; z < 3 + offset - 1; z++) {
                Block block = bottomLeft.getRelative(0, y, z);
                if (block.getType() == Material.AIR){
                    getPlugin().getTemporaryBlocks().put(block.getLocation(), block.getType());
                    replacedBlocksLocation.add(block.getLocation());
                    block.setType(Material.STONE);
                }
            }
        }

        new RemoveBlocklistTask(replacedBlocksLocation, getPlugin()).runTaskLater(getPlugin(), (long)(getSkillVariables().getDouble("duration", 5) * 20));
    }

    private void createEastWall(Block targetBlock){
        Set<Location> replacedBlocksLocation = new HashSet<>();

        final int offset = calculateOffset();
        Block bottomLeft = targetBlock.getLocation().add(offset * -1, 1, 0).getBlock();
        getXRPGPlayer().getPlayer().sendMessage("Bottom left: " + bottomLeft.getLocation().getX());


        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3 + offset -1; x++) {
                Block block = bottomLeft.getRelative(x, y, 0);
                if (block.getType() == Material.AIR){
                    getPlugin().getTemporaryBlocks().put(block.getLocation(), block.getType());
                    replacedBlocksLocation.add(block.getLocation());
                    block.setType(Material.STONE);
                }
            }
        }

        new RemoveBlocklistTask(replacedBlocksLocation, getPlugin()).runTaskLater(getPlugin(), (long)(getSkillVariables().getDouble("duration", 5) * 20));
    }

    private void createSouthWall(Block targetBlock){
        Set<Location> replacedBlocksLocation = new HashSet<>();

        final int offset = calculateOffset();
        Block bottomLeft = targetBlock.getLocation().add(0, 1, offset).getBlock();
        getXRPGPlayer().getPlayer().sendMessage("Bottom left: " + bottomLeft.getLocation().getZ());

        for (int y = 0; y < 3; y++) {
            for (int z = 0; z < 3 + offset -1; z++) {
                Block block = bottomLeft.getRelative(0, y, -z);
                if (block.getType() == Material.AIR){
                    getPlugin().getTemporaryBlocks().put(block.getLocation(), block.getType());
                    replacedBlocksLocation.add(block.getLocation());
                    block.setType(Material.STONE);
                }
            }
        }

        new RemoveBlocklistTask(replacedBlocksLocation, getPlugin()).runTaskLater(getPlugin(), (long)(getSkillVariables().getDouble("duration", 5) * 20));
    }

    private void createWestWall(Block targetBlock){
        Set<Location> replacedBlocksLocation = new HashSet<>();

        final int offset = calculateOffset();
        Block bottomLeft = targetBlock.getLocation().add(offset * -1, 1, 0).getBlock();
        getXRPGPlayer().getPlayer().sendMessage("Bottom left: " + bottomLeft.getLocation().getX());

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3 + offset -1; x++) {
                Block block = bottomLeft.getRelative(x, y, 0);
                if (block.getType() == Material.AIR){
                    getPlugin().getTemporaryBlocks().put(block.getLocation(), block.getType());
                    replacedBlocksLocation.add(block.getLocation());
                    block.setType(Material.STONE);
                }
            }
        }

        new RemoveBlocklistTask(replacedBlocksLocation, getPlugin()).runTaskLater(getPlugin(), (long)(getSkillVariables().getDouble("duration", 5) * 20));
    }

    private int calculateOffset(){
        if (getSkillLevel() <= 1) return 1;
        return getSkillLevel();
    }
}
