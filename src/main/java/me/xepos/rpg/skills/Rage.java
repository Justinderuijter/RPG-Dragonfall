package me.xepos.rpg.skills;

import me.xepos.rpg.AttributeModifierManager;
import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.AttributeModifierData;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.enums.ModifierType;
import me.xepos.rpg.skills.base.IMessenger;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import me.xepos.rpg.tasks.RavagerRageTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class Rage extends XRPGPassiveSkill implements IMessenger {
    private byte currentRage = 0;
    private static final byte maxRage = 100;
    private byte rageLevel = 0;
    BukkitTask rageTask = null;

    private static final String attackSpeedAttributeName = XRPG.modifierPrefix + "RAGE_ATK_SPD";

    public Rage(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        double attackSpeedMultiplier = 1 + skillVariables.getDouble(skillLevel, "atk-spd-multiplier", 65.0) / 100;
        AttributeModifier mod = new AttributeModifier(UUID.fromString("1d7a09c9-b6e2-4dc7-ab6f-8831dffcb111"), attackSpeedAttributeName, attackSpeedMultiplier, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

        AttributeModifierManager.getInstance().put(ModifierType.POSITIVE, mod.getName(), mod, Attribute.GENERIC_ATTACK_SPEED);

        setRemainingCooldown(-1);
        xrpgPlayer.getPassiveEventHandler("DAMAGE_DEALT").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof EntityDamageByEntityEvent e)) return;
        if (!getXRPGPlayer().getPlayer().getInventory().getItemInMainHand().getType().toString().endsWith("_AXE")) return;

        rageLevel = getRageLevel(currentRage);

        applyDamageRageEffect(e);

        //increase rage count
        if (((LivingEntity) e.getEntity()).getHealth() <= e.getFinalDamage()) {
            incrementRage((byte) getSkillVariables().getInt(getSkillLevel(), "bonus-rage-on-kill"));
        }

        incrementRage((byte) getSkillVariables().getInt(getSkillLevel(), "rage-on-hit"));
        getXRPGPlayer().sendActionBarMessage();
        if (rageTask == null || rageTask.isCancelled())
            rageTask = new RavagerRageTask(getXRPGPlayer(), this, (byte) 5).runTaskTimer(getPlugin(), 100L, 100L);


    }

    @Override
    public void initialize() {

    }

    private void applyDamageRageEffect(EntityDamageByEntityEvent e) {
        Player player = (Player) e.getDamager();
        SkillData skillVariable = getSkillVariables();
        AttributeModifierData attackSpeedModifierData = AttributeModifierManager.getInstance().get(ModifierType.POSITIVE, attackSpeedAttributeName);

        switch (rageLevel) {
            case 0:
                Utils.removeUniqueModifier(player, attackSpeedModifierData);
            case 1:
                Utils.removeUniqueModifier(player, attackSpeedModifierData);
                e.setDamage(e.getDamage() * (1 + skillVariable.getDouble(getSkillLevel(), "rage-one-multiplier", 10.0) / 100));
                break;
            case 2:
                Utils.removeUniqueModifier(player, attackSpeedModifierData);
                e.setDamage(e.getDamage() * (1 + skillVariable.getDouble(getSkillLevel(), "rage-two-multiplier", 20) / 100));
                break;
            case 3:
                Utils.addUniqueModifier(player, attackSpeedModifierData);
                e.setDamage(e.getDamage() * (1 + skillVariable.getDouble(getSkillLevel(), "rage-three-multiplier", 30.0) / 100));
                break;
        }
    }

    private void incrementRage(byte count) {
        if (currentRage + count <= 100)
            currentRage = (byte) (currentRage + count);
        else
            currentRage = maxRage;
    }

    public void decreaseCurrentRage(byte count) {
        if (currentRage >= count)
            currentRage = (byte) (currentRage - count);
        else
            currentRage = 0;
    }

    public byte getCurrentRage() {
        return currentRage;
    }

    private byte getRageLevel(byte currentRage) {
        if (currentRage > 80)
            return 3;
        else if (currentRage > 50)
            return 2;
        else if (currentRage > 20)
            return 1;
        else
            return 0;

    }

    @Override
    public String getMessage() {
        return "Rage " + getRageLevel(currentRage) + ": " + currentRage;
    }
}
