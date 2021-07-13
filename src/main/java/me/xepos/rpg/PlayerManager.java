package me.xepos.rpg;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PlayerManager {
    private final ConcurrentHashMap<UUID, XRPGPlayer> RPGPlayers;
    private final ConcurrentHashMap<UUID, List<Consumer<Player>>> consumerMap;

    protected PlayerManager(){
        RPGPlayers = new ConcurrentHashMap<>();
        consumerMap = new ConcurrentHashMap<>();
    }

    public void put(UUID uuid, XRPGPlayer xrpgPlayer){
        RPGPlayers.put(uuid, xrpgPlayer);
    }

    public void put(Player player, XRPGPlayer xrpgPlayer){
        RPGPlayers.put(player.getUniqueId(), xrpgPlayer);
    }

    public XRPGPlayer remove(UUID uuid){
        return RPGPlayers.remove(uuid);
    }

    public XRPGPlayer remove(Player player){
        return RPGPlayers.remove(player.getUniqueId());
    }

    public boolean containsPlayer(UUID uuid){
        return RPGPlayers.containsKey(uuid);
    }

    public boolean containsPlayer(Player player){
        return RPGPlayers.containsKey(player.getUniqueId());
    }

    public ConcurrentHashMap<UUID, XRPGPlayer> getXRPGPlayers() {
        return RPGPlayers;
    }

    public XRPGPlayer getXRPGPlayer(Player player, boolean force) {
        XRPGPlayer xrpgPlayer = RPGPlayers.get(player.getUniqueId());
        if (xrpgPlayer == null) return null;
        else if (force || xrpgPlayer.isClassEnabled()){
            return xrpgPlayer;
        }

        return null;
    }

    public XRPGPlayer getXRPGPlayer(Player player){
        return this.getXRPGPlayer(player, false);
    }

    public XRPGPlayer getXRPGPlayer(UUID playerUUID, boolean force) {
        XRPGPlayer xrpgPlayer = RPGPlayers.get(playerUUID);
        if (xrpgPlayer == null) return null;
        else if (force || xrpgPlayer.isClassEnabled()){
            return xrpgPlayer;
        }

        return null;
    }

    public XRPGPlayer getXRPGPlayer(UUID playerUUID) {
        return this.getXRPGPlayer(playerUUID, false);
    }

    public void addLoginConsumer(UUID uuid, List<Consumer<Player>> consumers){
        if (consumerMap.containsKey(uuid)){
            consumerMap.get(uuid).addAll(consumers);
        }else{
            consumerMap.put(uuid, consumers);
        }
    }

    public void addLoginConsumer(UUID uuid, Consumer<Player> consumer){
        if (consumerMap.containsKey(uuid)){
            consumerMap.get(uuid).add(consumer);
        }else{
            List<Consumer<Player>> list = new ArrayList<>(){{
                add(consumer);
            }};
            consumerMap.put(uuid, list);
        }
    }

    @SuppressWarnings("all")
    public void consumeLoginConsumers(UUID uuid){
        Player player = Bukkit.getPlayer(uuid);
        List<Consumer<Player>> consumers = consumerMap.get(uuid);
        if (consumers == null || player == null) return;

        Iterator<Consumer<Player>> consumerIterator = consumers.iterator();
        while(consumerIterator.hasNext()){
            consumerIterator.next().accept(player);
        }
    }

    public int getLoginTaskCount(){
        return consumerMap.size();
    }
}
