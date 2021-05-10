package me.xepos.rpg.entities.pathfinders;

import me.xepos.rpg.entities.Follower;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.PathfinderGoalTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.EnumSet;

public class PathfinderGoalOwnerHurtTarget extends PathfinderGoalTarget {
    private final Follower a; //this (CustomCreature)
    private final EntityLiving owner; //owner
    private EntityLiving b; //target
    private int c;

    public PathfinderGoalOwnerHurtTarget(Follower creature, EntityLiving owner) {
        super(creature, false);
        this.a = creature;
        this.owner = owner;
        this.a(EnumSet.of(Type.TARGET));
    }

    public boolean a() {
            EntityLiving entityliving = this.owner;
            if (entityliving == null) {
                return false;
            } else {
                this.b = entityliving.db(); //getBs, what is bs? Instance of the entity?
                int i = entityliving.dc(); //getBt, what is bt?
                return true;//i != this.c && this.a(this.b, PathfinderTargetCondition.a) && this.a.a(this.b, entityliving);
            }
    }

    public void c() {
        this.e.setGoalTarget(this.b, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true);
        EntityLiving entityliving = this.owner;
        if (entityliving != null) {
            ((Player) owner).sendMessage("Entered C");
            this.c = entityliving.dc();
        }

        super.c();
    }
}
