package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.enums.SpellType;
import me.xepos.rpg.events.XRPGSpellCastEvent;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.tasks.PurgatoryBatTask;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.util.RayTraceResult;

public class PurgatoryBat extends XRPGActiveSkill {
    private final static SpellType[] spelltypes = new SpellType[]{SpellType.ACTIVE, SpellType.DAMAGE};


    public PurgatoryBat(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent e)) return;

        XRPGSpellCastEvent spellCastEvent = new XRPGSpellCastEvent(this, spelltypes);
        Bukkit.getServer().getPluginManager().callEvent(spellCastEvent);

        if (spellCastEvent.isCancelled()) return;

        doPurgatoryBat(e.getPlayer());
    }

    @Override
    public void initialize() {

    }

    private void doPurgatoryBat(Player player) {
        if (!isSkillReady()) {
            player.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        final double range = getSkillVariables().getDouble(getSkillLevel(), "range", 16.0);
        RayTraceResult result = player.getLocation().getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), range, FluidCollisionMode.ALWAYS, true, 0.3, p -> p instanceof LivingEntity && p != player);

        if (result != null && result.getHitEntity() != null) {
            LivingEntity livingEntity = (LivingEntity) result.getHitEntity();

            final double interval = getSkillVariables().getDouble(getSkillLevel(), "interval", 1.0);
            final byte maxCount = (byte) getSkillVariables().getInt(getSkillLevel(), "max-procs", 5);
            final double duration = getSkillVariables().getDouble(getSkillLevel(), "dt-duration", 5);
            final double dtAmount = getSkillVariables().getDouble(getSkillLevel(), "dt-amount", 1.2);

            Bat bat = (Bat) livingEntity.getWorld().spawnEntity(livingEntity.getEyeLocation(), EntityType.BAT);
            bat.setAI(false);
            bat.setInvulnerable(true);
            bat.setCollidable(false);
            bat.setAwake(true);
            bat.setCustomName("Purgatory bat");
            bat.setCustomNameVisible(false);

            new PurgatoryBatTask(bat, player, getRawDamage(), maxCount, this, dtAmount, getPlugin(), (long) duration * 20L)
                    .runTaskTimer(getPlugin(), 10, (long) interval * 20L);

            setRemainingCooldown(getCooldown());
            updatedCasterMana();
        }
    }
}
