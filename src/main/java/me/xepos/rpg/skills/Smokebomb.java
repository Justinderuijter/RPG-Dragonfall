package me.xepos.rpg.skills;

import com.mojang.datafixers.util.Pair;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.tasks.EndInvisibilityTask;
import me.xepos.rpg.utils.Utils;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.List;

public class Smokebomb extends XRPGActiveSkill {

    private int smokebombDuration = 10;

    public Smokebomb(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }


    @Override
    public void initialize() {

    }

    @Override
    public void activate(Event event) {
        if (event instanceof PlayerInteractEvent) {
            PlayerInteractEvent e = (PlayerInteractEvent) event;
            if (e.getHand() == EquipmentSlot.OFF_HAND) {
                return;
            }

            if (!isSkillReady()) {
                e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
                return;
            }else if(!hasRequiredMana()){
                sendNotEnoughManaMessage();
                return;
            }

            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_AIR) {

                //AssassinConfig assassinConfig = AssassinConfig.getInstance();

                e.getPlayer().setInvisible(true);
                e.getPlayer().sendMessage("You're now invisible!");
                //Custom packet sending starts here, if something breaks between versions it's probably this
                final List<Pair<EnumItemSlot, ItemStack>> equipmentList = new ArrayList<>();
                equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR))));
                equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR))));
                equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR))));
                equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR))));
                equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR))));
                equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.OFFHAND, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR))));
                //Creating the packet we're going to send
                final PacketPlayOutEntityEquipment entityEquipmentPacket = new PacketPlayOutEntityEquipment(e.getPlayer().getEntityId(), equipmentList);

                //List all affected players so we can undo the packet later
                List<Player> affectedPlayers = new ArrayList(e.getPlayer().getWorld().getNearbyEntities(e.getPlayer().getLocation(), 20, 10, 20, p -> p instanceof Player && p != e.getPlayer()));
                for (Player ent : affectedPlayers) {
                    ((CraftPlayer) ent).getHandle().playerConnection.sendPacket(entityEquipmentPacket);//send affected players the packet
                }

                setRemainingCooldown(getCooldown());
                updatedCasterMana();
                new EndInvisibilityTask(e.getPlayer(), affectedPlayers).runTaskLater(getPlugin(), smokebombDuration * 20L);
            }
        } else if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            Player player = (Player) e.getEntity();
            if (player.isInvisible()) {
                player.setInvisible(false);
                player.sendMessage("Invisibility ended by damage!");
            }
        } else if (event instanceof ProjectileLaunchEvent) {
            ProjectileLaunchEvent e = (ProjectileLaunchEvent) event;

            //Cancel throwing snowball if class specific item (Smoke Bomb)
            if (e.getEntity().getShooter() instanceof Player) {
                Player player = (Player) e.getEntity().getShooter();
                if (player.getInventory().getItemInMainHand().getType() == Material.SNOWBALL && Utils.isItemNameMatching(player.getInventory().getItemInMainHand(), "Smoke Bomb")) {
                    if (e.getEntity() instanceof Snowball) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}
