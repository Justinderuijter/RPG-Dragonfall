package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class ShadowStep extends XRPGActiveSkill {

    private ArmorStand substitute = null;

    public ShadowStep(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }


    @Override
    public void activate(Event event) {
        if (event instanceof PlayerItemHeldEvent) {
            doSub((PlayerItemHeldEvent) event);
        }
    }

    @SuppressWarnings("all")
    private void setArmorStandArmor(Player player, ArmorStand armorStand) {
        HashMap<EquipmentSlot, ItemStack> armor = new HashMap() {
            {
                put(EquipmentSlot.CHEST, new org.bukkit.inventory.ItemStack(Material.LEATHER_CHESTPLATE));
                put(EquipmentSlot.LEGS, new org.bukkit.inventory.ItemStack(Material.LEATHER_LEGGINGS));
                put(EquipmentSlot.FEET, new org.bukkit.inventory.ItemStack(Material.LEATHER_BOOTS));
            }
        };

        EntityEquipment armorStandEquipement = armorStand.getEquipment();

        //Setting up the player head
        org.bukkit.inventory.ItemStack playerHead = new org.bukkit.inventory.ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getPlayer(player.getUniqueId()));
        playerHead.setItemMeta(skullMeta);
        armorStandEquipement.setHelmet(playerHead);

        //Setting up the leather armor
        for (EquipmentSlot slot : armor.keySet()) {
            org.bukkit.inventory.ItemStack item = armor.get(slot);
            LeatherArmorMeta itemMeta = (LeatherArmorMeta) item.getItemMeta();
            itemMeta.setColor(Color.BLACK);
            item.setItemMeta(itemMeta);

            armorStandEquipement.setItem(slot, armor.get(slot), true);
            armorStand.addEquipmentLock(slot, ArmorStand.LockType.REMOVING_OR_CHANGING);
        }
    }

    private void doSub(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();
        if (substitute == null) {
            if (!isSkillReady()) {
                player.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
                return;
            }else if(!hasRequiredMana()){
                sendNotEnoughManaMessage();
                return;
            }

            //Creating armorstand at player's location and setting the right properties
            ArmorStand armorStand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setCollidable(false);

            armorStand.setCustomName(player.getName());
            armorStand.setCustomNameVisible(true);


            setArmorStandArmor(player, armorStand);

            substitute = armorStand;
            setRemainingCooldown(getCooldown());
            updatedCasterMana();

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (substitute != null) {
                        substitute.remove();
                        substitute = null;
                    }
                }
            }.runTaskLater(getPlugin(), (long) getSkillVariables().getDouble(getSkillLevel(),"duration", 5.0) * 20);
        } else {
            player.teleport(substitute.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            substitute.remove();
            substitute = null;
        }
    }

    @Override
    public void initialize() {

    }

}
