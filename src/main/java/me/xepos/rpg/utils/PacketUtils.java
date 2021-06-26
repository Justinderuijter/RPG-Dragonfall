package me.xepos.rpg.utils;


import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import net.minecraft.server.v1_16_R3.PacketPlayOutSetSlot;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

/*
This class is a temporary solution and should be replaced
with an instance of an implemented interface in the main class later!
*/
public class PacketUtils {
    private static final XRPG plugin = JavaPlugin.getPlugin(XRPG.class);

    public static void sendSpellmodePacket(XRPGPlayer xrpgPlayer){
        final CraftPlayer craftPlayer = (CraftPlayer) xrpgPlayer.getPlayer();

        for (int i = 0; i < xrpgPlayer.getSpellKeybinds().size(); i++) {

            final String keyBindId = xrpgPlayer.getSpellKeybinds().get(i);
            if (keyBindId == null) continue;
            final SkillData skillData = plugin.getSkillData(keyBindId);
            final String skillName = skillData.getName();
            final String materialName = skillData.getIcon();
            final int skillLevel = xrpgPlayer.getActiveHandler().getSkills().get(keyBindId).getSkillLevel();

            ItemStack itemStack = Utils.buildItemStack(Material.getMaterial(materialName), skillName, skillData.getDescription(skillLevel));
            ItemMeta meta = itemStack.getItemMeta();
            //meta.displayName(Component.text(skillName));
            itemStack.setItemMeta(meta);

            //For packets player inventory hotbar starts at index 36
            PacketPlayOutSetSlot pack = new PacketPlayOutSetSlot(0, i + 36, CraftItemStack.asNMSCopy(itemStack));

            craftPlayer.getHandle().playerConnection.sendPacket(pack);
        }
    }
}
