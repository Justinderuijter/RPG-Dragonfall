package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.ProjectileData;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.enums.SpellType;
import me.xepos.rpg.events.XRPGSpellCastEvent;
import me.xepos.rpg.handlers.BowEventHandler;
import me.xepos.rpg.skills.base.XRPGBowSkill;
import me.xepos.rpg.utils.DamageUtils;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class ExposeWeakness extends XRPGBowSkill {
    private final static SpellType[] spelltypes = new SpellType[]{SpellType.ACTIVE, SpellType.DAMAGE};

    private boolean isActive = false;
    private final BowEventHandler bowHandler;

    public ExposeWeakness(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        bowHandler = (BowEventHandler) getXRPGPlayer().getPassiveEventHandler("SHOOT_BOW");
        getXRPGPlayer().getPassiveEventHandler("DAMAGE_DEALT").addSkill(this.getClass().getSimpleName(), this);
        getXRPGPlayer().getActiveHandler().addSkill(this.getClass().getSimpleName(), this);

    }

    @Override
    public void activate(Event event) {
        if(event instanceof PlayerItemHeldEvent e) {

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
            isActive = true;

        } else if(event instanceof EntityDamageByEntityEvent){
            if (isActive) {
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
                LivingEntity target = (LivingEntity) e.getEntity();

                if (target instanceof Player && canHurtTarget((Player) target)) return;

                Utils.decreaseHealth(target, calculateDamage(target));

                //Setting the handler could fail so checking here
                if (bowHandler != null) {
                    bowHandler.setActiveBowSkill(null);
                }

                isActive = false;
                setRemainingCooldown(getCooldown());
                updatedCasterMana();
            }

        } else if(event instanceof EntityShootBowEvent){
            //This currently does currentHP damage due to how projectileData works
            if (isActive){
                isActive = false;
                EntityShootBowEvent e = (EntityShootBowEvent) event;
                if (e.getProjectile() instanceof Arrow arrow){
                    arrow.setDamage(0);

                    ProjectileData data =  new ProjectileData(arrow, getXRPGPlayer().getLevel(), 20);
                    data.setDamageMultiplier(1 - (getMaxHealthDamage()/100));

                    getPlugin().projectiles.put(arrow.getUniqueId(), data);
                }

                setRemainingCooldown(getCooldown());
                updatedCasterMana();
            }
        }


    }

    @Override
    public void initialize() {

    }

    private double calculateDamage(LivingEntity target){
        double maxHealth = target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

        return (maxHealth / (100 / getMaxHealthDamage())) * DamageUtils.getSpellDamageMultiplier(getSkillLevel(), target);
    }

    private double getMaxHealthDamage(){
        return getSkillVariables().getDouble(getSkillLevel(), "max-health-damage", 25);
    }
}
