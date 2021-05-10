package me.xepos.rpg.skills.base;

import me.xepos.rpg.entities.Follower;

import java.util.List;

public interface IFollowerContainer {
    List<Follower> getFollowers();

    void addFollower(Follower follower);

    int getFollowerCount();

}
