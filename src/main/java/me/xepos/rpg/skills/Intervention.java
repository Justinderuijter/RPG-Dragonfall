package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.enums.SpellType;
import me.xepos.rpg.events.XRPGSpellCastEvent;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class Intervention extends XRPGActiveSkill {
    private final static SpellType[] spelltypes = new SpellType[]{SpellType.ACTIVE, SpellType.DISPLACEMENT};

    public Intervention(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent e)) return;

        XRPGSpellCastEvent spellCastEvent = new XRPGSpellCastEvent(this, spelltypes);
        Bukkit.getServer().getPluginManager().callEvent(spellCastEvent);

        if (spellCastEvent.isCancelled()) return;

        if (!isSkillReady()){
            e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        RayTraceResult result = Utils.rayTrace(e.getPlayer(), 16, FluidCollisionMode.NEVER);

        if (result != null && result.getHitEntity() != null){
            LivingEntity entity = (LivingEntity) result.getHitEntity();
            Location loc = entity.getLocation();
            Location targetLoc = e.getPlayer().getLocation();
            double pullForce = getSkillVariables().getDouble(getSkillLevel(), "pull-force", 1.0);

            double x = loc.getX() - targetLoc.getX();
            double y = loc.getY() - targetLoc.getY();
            double z = loc.getZ() - targetLoc.getZ();
            Vector velocity = new Vector(x, y, z).normalize().multiply(-pullForce);
            entity.setVelocity(velocity);

            setRemainingCooldown(getCooldown());
            updatedCasterMana();
        }

    }

    @Override
    public void initialize() {

    }
}
