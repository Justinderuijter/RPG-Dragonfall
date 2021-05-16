package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.entities.Follower;
import me.xepos.rpg.entities.type.FollowerZombie;
import me.xepos.rpg.entities.type.FollowerZombieVillager;
import me.xepos.rpg.skills.base.IFollowerContainer;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import me.xepos.rpg.utils.Utils;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ArmyOfTheUndead extends XRPGPassiveSkill implements IFollowerContainer {
    private final List<Follower> followers = new ArrayList<>();

    public ArmyOfTheUndead(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getPassiveEventHandler("DAMAGE_DEALT").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

        if (!(e.getEntity() instanceof LivingEntity)) return;
        LivingEntity livingEntity = (LivingEntity) e.getEntity();

        if (livingEntity.getHealth() <= e.getFinalDamage() && followers.size() < getSkillVariables().getInt("max-followers", 3)) {
            recruitFollower(e);
            return; //Code below doesn't need to get executed when the target dies.
        }


        for (Follower follower : followers) {
            EntityLiving entityLiving = ((CraftLivingEntity) e.getEntity()).getHandle();
            if (entityLiving instanceof Follower) {
                if (!followers.contains(entityLiving))
                    follower.setGoalTarget(entityLiving, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true);
            } else {
                follower.setGoalTarget(entityLiving, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true);
            }
        }
    }

    @Override
    public void initialize() {

    }

    @Override
    public List<Follower> getFollowers() {
        return followers;
    }

    @Override
    public void addFollower(Follower follower) {
        followers.add(follower);
    }

    @Override
    public int getFollowerCount() {
        return followers.size();
    }

    @SuppressWarnings("unchecked")
    private void recruitFollower(EntityDamageByEntityEvent e) {

        EntityTypes<? extends Follower> type =
                (EntityTypes<? extends Follower>) ((CraftEntity) e.getEntity()).getHandle().getEntityType();

        Player killer = (Player) e.getDamager();

        Object instance = null;
        String entityTypeName = Utils.enumTypeFormatter(e.getEntity().getType().name(), "_");

        killer.sendMessage("me.xepos.rpg.entities.type.Follower" + entityTypeName);
        killer.sendMessage(type.toString());

        try {
            //Get supported type
            Class<?> clazz = Class.forName("me.xepos.rpg.entities.type.Follower" + entityTypeName);
            Constructor<?> constructor = clazz.getConstructor(EntityTypes.class, Location.class, LivingEntity.class);
            instance = constructor.newInstance(type, e.getEntity().getLocation(), killer);
        } catch (Exception ex) {
            if (entityTypeName.contains("llager")
                    || entityTypeName.equalsIgnoreCase("witch")
                    || entityTypeName.equalsIgnoreCase("vindicator")
                    || entityTypeName.equalsIgnoreCase("illusioner")
                    || entityTypeName.equalsIgnoreCase("evoker")) {
                instance = new FollowerZombieVillager(EntityTypes.ZOMBIE_VILLAGER, e.getEntity().getLocation(), killer);
            } else {
                instance = new FollowerZombie(EntityTypes.ZOMBIE, e.getEntity().getLocation(), killer);
            }

        } finally {
            //then do this stuff
            if (instance != null) {
                Follower follower = (Follower) instance;

                this.followers.add(follower);

                WorldServer world = ((CraftWorld) killer.getWorld()).getHandle();
                world.addEntity(follower);
            }
        }

    }
}
