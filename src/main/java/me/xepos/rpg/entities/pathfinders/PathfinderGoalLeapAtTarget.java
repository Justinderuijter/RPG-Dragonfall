package me.xepos.rpg.entities.pathfinders;

import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import net.minecraft.server.v1_16_R3.Vec3D;

import java.util.EnumSet;

public class PathfinderGoalLeapAtTarget extends PathfinderGoal {
    private final EntityInsentient a;
    private EntityLiving b;
    private final float c;

    public PathfinderGoalLeapAtTarget(EntityInsentient var0, float var1) {
        this.a = var0;
        this.c = var1;
        this.a(EnumSet.of(Type.JUMP, Type.MOVE));
    }

    public boolean a() {
        if (this.a.isVehicle()) {
            return false;
        } else {
            this.b = this.a.getGoalTarget();
            if (this.b == null) {
                return false;
            } else {
                double var0 = this.a.h(this.b);
                if (!(var0 < 4.0D) && !(var0 > 16.0D)) {
                    if (!this.a.isOnGround()) {
                        return false;
                    } else {
                        return this.a.getRandom().nextInt(5) == 0;
                    }
                } else {
                    return false;
                }
            }
        }
    }

    public boolean b() {
        return !this.a.isOnGround();
    }

    public void c() {
        Vec3D var0 = this.a.getMot();
        Vec3D var1 = new Vec3D(this.b.locX() - this.a.locX(), 0.0D, this.b.locZ() - this.a.locZ());
        if (var1.g() > 1.0E-7D) {
            var1 = var1.d().a(0.4D).e(var0.a(0.2D));
        }

        this.a.setMot(var1.x, (double) this.c, var1.z);
    }
}
