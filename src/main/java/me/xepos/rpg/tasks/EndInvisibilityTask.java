package me.xepos.rpg.tasks;

import com.mojang.datafixers.util.Pair;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEquipment;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class EndInvisibilityTask extends BukkitRunnable {

    Player player;
    List<Player> otherPlayers;

    public EndInvisibilityTask(Player player, List<Player> otherPlayers) {
        //this.plugin = plugin;
        this.player = player;
        this.otherPlayers = otherPlayers;
    }


    @Override
    public void run() {
        if (player.isInvisible())
        {
            player.setInvisible(false);
            final List<Pair<EnumItemSlot, ItemStack>> equipmentList = new ArrayList<>();
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(player.getInventory().getHelmet())));
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(player.getInventory().getChestplate())));
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(player.getInventory().getLeggings())));
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(player.getInventory().getBoots())));
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(player.getInventory().getItemInMainHand())));
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.OFFHAND, CraftItemStack.asNMSCopy(player.getInventory().getItemInOffHand())));
            final PacketPlayOutEntityEquipment entityEquipmentPacket = new PacketPlayOutEntityEquipment(player.getEntityId(), equipmentList);
            for (Player otherPlayer: otherPlayers) {
                ((CraftPlayer)otherPlayer).getHandle().playerConnection.sendPacket(entityEquipmentPacket);//send affected players the packet
            }
            player.sendMessage("Invisibility ended");
        }

    }
}
