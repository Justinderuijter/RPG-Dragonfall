package me.xepos.rpg.listeners;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ArmorEffect;
import me.xepos.rpg.datatypes.ArmorSetData;
import me.xepos.rpg.dugcore.events.AsyncTimeChangeEvent;
import me.xepos.rpg.enums.ArmorSetTriggerType;
import me.xepos.rpg.events.XRPGBaseProjectileFireEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;

public class ArmorSetListener implements Listener {
    private final XRPG plugin;

    public ArmorSetListener(XRPG plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChangeArmor(PlayerArmorChangeEvent event){
        ItemStack newItem = event.getNewItem();
        ItemStack oldItem = event.getOldItem();

        XRPGPlayer xrpgPlayer = plugin.getPlayerManager().getXRPGPlayer(event.getPlayer(), true);

        if (newItem != null && newItem.hasItemMeta() && hasSetPDC(newItem.getItemMeta())){
            if (oldItem != null){
                if (!oldItem.hasItemMeta() || !hasSetPDC(oldItem.getItemMeta())){
                    //Old item does not have setId
                    ArmorSetData newData = xrpgPlayer.increaseSetLevel(getSetId(newItem.getItemMeta()));
                    if (newData != null){
                        ArmorEffect effect = newData.getArmorEffects().get(ArmorSetTriggerType.SET_BONUS_CHANGE);
                        if (effect != null) effect.activate(event);
                    }
                }else if(hasSetPDC(oldItem.getItemMeta())){
                    //Old item has setId
                    String oldSetId = getSetId(oldItem.getItemMeta());
                    String newSetId = getSetId(newItem.getItemMeta());
                    if (!newSetId.equals(oldSetId)){
                        ArmorSetData oldData = xrpgPlayer.decreaseSetLevel(oldSetId);
                        if (oldData != null){
                            oldData.removeArmorModifiers(xrpgPlayer.getPlayer());
                        }
                        ArmorSetData newData = xrpgPlayer.increaseSetLevel(newSetId);
                        if (newData != null){
                             ArmorEffect effect = newData.getArmorEffects().get(ArmorSetTriggerType.SET_BONUS_CHANGE);
                             if (effect != null) effect.activate(event);
                        }

                    }
                }
            }
        }else{
            if (oldItem != null && oldItem.hasItemMeta() && hasSetPDC(oldItem.getItemMeta())){

                ArmorSetData oldData = xrpgPlayer.decreaseSetLevel(getSetId(oldItem.getItemMeta()));
                if (oldData != null){
                    oldData.removeArmorModifiers(xrpgPlayer.getPlayer());
                }
            }
        }
    }

    //Trigger before main event handler
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event){
        if (event instanceof EntityDamageByEntityEvent e){
            if (e.getEntity() instanceof Player player){
                XRPGPlayer xrpgPlayer = plugin.getPlayerManager().getXRPGPlayer(player, true);
                if (e.getDamager() instanceof LivingEntity){
                    xrpgPlayer.runArmorEffects(e, ArmorSetTriggerType.DEFEND_MOB);
                }else if (e.getDamager() instanceof Projectile){
                    //Player is defending from projectile
                }
            }

            if (e.getDamager() instanceof Player player){
                XRPGPlayer xrpgPlayer = plugin.getPlayerManager().getXRPGPlayer(player, true);
                if (e.getEntity() instanceof Player){
                    xrpgPlayer.runArmorEffects(e, ArmorSetTriggerType.ATTACK_PLAYER);
                }else if (e.getEntity() instanceof LivingEntity){
                    xrpgPlayer.runArmorEffects(e, ArmorSetTriggerType.ATTACK_MOB);
                }
                //Player is attacking
            }else if(e.getDamager() instanceof Projectile projectile){
                if (projectile.getShooter() instanceof Player player){
                    XRPGPlayer xrpgPlayer = plugin.getPlayerManager().getXRPGPlayer(player, true);
                    if (e.getEntity() instanceof Player){
                        xrpgPlayer.runArmorEffects(e, ArmorSetTriggerType.BOW_PLAYER);
                    }else if (e.getEntity() instanceof LivingEntity){
                        xrpgPlayer.runArmorEffects(e, ArmorSetTriggerType.BOW_MOB);
                    }
                }
            }
        }else{
            //Natural damage
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent e){
        XRPGPlayer xrpgPlayer = plugin.getPlayerManager().getXRPGPlayer(e.getEntity(), true);
        xrpgPlayer.runArmorEffects(e, ArmorSetTriggerType.DEATH);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent e){
        Player killer = e.getEntity().getKiller();
        if (killer == null) return;

        XRPGPlayer xrpgPlayer = plugin.getPlayerManager().getXRPGPlayer(killer, true);
        xrpgPlayer.runArmorEffects(e, ArmorSetTriggerType.KILL_MOB);
    }

    //Trigger after main Event handler
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onShootBow(EntityShootBowEvent e){
        if (!(e.getEntity() instanceof Player player)) return;
        XRPGPlayer xrpgPlayer = plugin.getPlayerManager().getXRPGPlayer(player, true);
        xrpgPlayer.runArmorEffects(e, ArmorSetTriggerType.SHOOT_BOW);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onExpGain(PlayerExpChangeEvent e){
        if (e.getAmount() > 0){
            XRPGPlayer xrpgPlayer = plugin.getPlayerManager().getXRPGPlayer(e.getPlayer(), true);
            xrpgPlayer.runArmorEffects(e, ArmorSetTriggerType.GAIN_EXP);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCustomProjectile(XRPGBaseProjectileFireEvent e){

    }

    @EventHandler
    public void AsyncTimeChange(AsyncTimeChangeEvent e){
        Collection<XRPGPlayer> xrpgPlayers = plugin.getPlayerManager().getXRPGPlayers().values();
        switch(e.getCurrentDayTime()){
            case DAY -> {
                runTimeChangeEvent(xrpgPlayers, e, ArmorSetTriggerType.TIME_CHANGE_DAY);
            }
            case NIGHT -> {
                runTimeChangeEvent(xrpgPlayers, e, ArmorSetTriggerType.TIME_CHANGE_NIGHT);
            }
        }
    }

    private void runTimeChangeEvent(Collection<XRPGPlayer> xrpgPlayers, AsyncTimeChangeEvent e, ArmorSetTriggerType type){
        //final byte maxPlayersPerTick = 10;

        Bukkit.getLogger().info("TIME CHANGED TO: " + e.getCurrentDayTime().name() + "!");
        Bukkit.getLogger().info("TIME CHANGED FOR " + xrpgPlayers.size() + " player(s)!");
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (XRPGPlayer xrpgPlayer:xrpgPlayers) {
                xrpgPlayer.runArmorEffects(xrpgPlayer.getPlayer(), type);
            }
        });
    }

    private boolean hasSetPDC(ItemMeta meta){
        return meta.getPersistentDataContainer().has(plugin.getKey("set"), PersistentDataType.STRING);
    }

    private String getSetId(ItemMeta meta){
        return meta.getPersistentDataContainer().get(plugin.getKey("set"), PersistentDataType.STRING);
    }


}
