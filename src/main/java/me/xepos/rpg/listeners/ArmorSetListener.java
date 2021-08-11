package me.xepos.rpg.listeners;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.events.XRPGBaseProjectileFireEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ArmorSetListener implements Listener {
    private final XRPG plugin;

    public ArmorSetListener(XRPG plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onChangeArmor(PlayerArmorChangeEvent event){
        ItemStack newItem = event.getNewItem();
        ItemStack oldItem = event.getOldItem();

        XRPGPlayer xrpgPlayer = plugin.getPlayerManager().getXRPGPlayer(event.getPlayer(), true);

        if (newItem != null && newItem.hasItemMeta() && hasSetPDC(newItem.getItemMeta())){
            if (oldItem != null){
                if (!oldItem.hasItemMeta() || !hasSetPDC(oldItem.getItemMeta())){
                    //Old item does not have setId
                    xrpgPlayer.increaseSetLevel(getSetId(newItem.getItemMeta()));
                }else if(hasSetPDC(oldItem.getItemMeta())){
                    //Old item has setId
                    String oldSetId = getSetId(oldItem.getItemMeta());
                    String newSetId = getSetId(newItem.getItemMeta());
                    if (!newSetId.equals(oldSetId)){
                        xrpgPlayer.decreaseSetLevel(oldSetId);
                        xrpgPlayer.increaseSetLevel(newSetId);
                    }
                }
            }
        }else{
            if (oldItem != null && oldItem.hasItemMeta() && hasSetPDC(oldItem.getItemMeta())){
                xrpgPlayer.decreaseSetLevel(getSetId(oldItem.getItemMeta()));
            }
        }
    }

    //Trigger before main event handler
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event){
        if (event instanceof EntityDamageByEntityEvent e){
            if (e.getEntity() instanceof Player player){
                if (e.getDamager() instanceof LivingEntity){
                    //Player is defending
                }else if (e.getDamager() instanceof Projectile){
                    //Player is defending from projectile
                }
            }

            if (e.getDamager() instanceof Player player){
                //Player is attacking
            }
        }else{
            //Natural damage
        }
    }

    //Trigger after main Event handler
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onShootBow(EntityShootBowEvent e){
        if (!(e.getEntity() instanceof Player player)) return;

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCustomProjectile(XRPGBaseProjectileFireEvent e){

    }

    private boolean hasSetPDC(ItemMeta meta){
        return meta.getPersistentDataContainer().has(plugin.getKey("set"), PersistentDataType.STRING);
    }

    private String getSetId(ItemMeta meta){
        return meta.getPersistentDataContainer().get(plugin.getKey("set"), PersistentDataType.STRING);
    }


}
