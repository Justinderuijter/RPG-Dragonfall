package me.xepos.rpg.utils;


import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import net.kyori.adventure.text.Component;
import net.minecraft.server.v1_16_R3.PacketPlayOutSetSlot;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

/*
This class is a temporary solution and should be replaced
with an instance of an implemented interface in the main class later!
*/
public class PacketUtils {
    private static final XRPG plugin = JavaPlugin.getPlugin(XRPG.class);

    public static void testingPacket(XRPGPlayer xrpgPlayer){
        final CraftPlayer craftPlayer = (CraftPlayer) xrpgPlayer.getPlayer();

        for (int i = 0; i < xrpgPlayer.getSpellKeybinds().size(); i++) {

            final String skillName = plugin.getSkillData(xrpgPlayer.getSpellKeybinds().get(i)).getString("name", "???");
            final String materialName = plugin.getSkillData(xrpgPlayer.getSpellKeybinds().get(i)).getString("icon", "BARRIER");

            ItemStack itemStack = new ItemStack(Material.valueOf(materialName));
            ItemMeta meta = itemStack.getItemMeta();
            meta.displayName(Component.text(skillName));
            itemStack.setItemMeta(meta);

            //For packets player inventory hotbar starts at index 36
            PacketPlayOutSetSlot pack = new PacketPlayOutSetSlot(0, i + 36, CraftItemStack.asNMSCopy(itemStack));

            craftPlayer.getHandle().playerConnection.sendPacket(pack);
        }
    }
}
