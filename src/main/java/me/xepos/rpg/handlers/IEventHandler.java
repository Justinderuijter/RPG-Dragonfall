package me.xepos.rpg.handlers;

public interface IEventHandler {
    void initialize();

    void clear();

    void removeSkill(String skillId);
}
