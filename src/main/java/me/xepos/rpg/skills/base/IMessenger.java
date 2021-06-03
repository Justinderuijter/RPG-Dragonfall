package me.xepos.rpg.skills.base;

/**
 * An interface used for skills that want to push a message to the action bar.
 * Implementing this ensures that the message gets displayed correctly in combination with mana.
 */
public interface IMessenger {
    String getMessage();
}
