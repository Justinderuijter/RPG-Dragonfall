package me.xepos.rpg.entities.type.interfaces;

import net.minecraft.server.v1_16_R3.NavigationAbstract;
import net.minecraft.server.v1_16_R3.World;

@SuppressWarnings("unused")
public interface IFollowerSpider {

    NavigationAbstract b(World world);

    boolean isClimbing();

    boolean eL();

    void t(boolean flag);

    void tick();

}
