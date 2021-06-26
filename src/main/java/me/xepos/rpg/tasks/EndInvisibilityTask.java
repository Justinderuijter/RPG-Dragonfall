package me.xepos.rpg.tasks;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
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
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(player.getInventory().getHelmet())));
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.e, CraftItemStack.asNMSCopy(player.getInventory().getChestplate())));
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.d, CraftItemStack.asNMSCopy(player.getInventory().getLeggings())));
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.c, CraftItemStack.asNMSCopy(player.getInventory().getBoots())));
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.a, CraftItemStack.asNMSCopy(player.getInventory().getItemInMainHand())));
            equipmentList.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.b, CraftItemStack.asNMSCopy(player.getInventory().getItemInOffHand())));
            final PacketPlayOutEntityEquipment entityEquipmentPacket = new PacketPlayOutEntityEquipment(player.getEntityId(), equipmentList);
            for (Player otherPlayer: otherPlayers) {
                ((CraftPlayer)otherPlayer).getHandle().b.sendPacket(entityEquipmentPacket);//send affected players the packet
            }
            player.sendMessage("Invisibility ended");
        }

    }
}
