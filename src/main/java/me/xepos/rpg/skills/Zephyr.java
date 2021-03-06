package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.enums.SpellType;
import me.xepos.rpg.events.XRPGSpellCastEvent;
import me.xepos.rpg.skills.base.XRPGActiveSkill;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Zephyr extends XRPGActiveSkill {
    private final static SpellType[] spelltypes = new SpellType[]{SpellType.ACTIVE, SpellType.DISPLACEMENT};

    private Fireball fireball;

    public Zephyr(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel, boolean isEventSkill) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel, isEventSkill);

        xrpgPlayer.getActiveHandler().addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemHeldEvent e)) return;

        XRPGSpellCastEvent spellCastEvent = new XRPGSpellCastEvent(this, spelltypes);
        Bukkit.getServer().getPluginManager().callEvent(spellCastEvent);

        if (spellCastEvent.isCancelled()) return;

        doZephyr(e);
    }

    @Override
    public void initialize() {
        for (XRPGSkill skill : getXRPGPlayer().getActiveHandler().getSkills().values()) {
            if (skill instanceof Fireball) {
                this.fireball = ((Fireball) skill);
                return;
            }
        }
    }

    private void doZephyr(PlayerItemHeldEvent e) {
        if (!isSkillReady()) {
            e.getPlayer().sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }
        //WizardConfig wizardConfig = WizardConfig.getInstance();

        List<LivingEntity> entities = Utils.getLivingEntitiesInLine(e.getPlayer(), 16);
        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PUFFER_FISH_BLOW_UP, 0.5F, 1F);

        int fireBallStacks = 0;
        if (fireball != null) {
            fireBallStacks = fireball.getFireBallStacks();
        }

        final long duration = (long) getSkillVariables().getDouble(getSkillLevel(),"duration", 3.0);
        for (LivingEntity entity : entities) {
            if (entity != e.getPlayer()) {
                //Subtract 1 from the count to account for user
                if (entity instanceof Player) {
                    Player target = (Player) entity;
                    target.playSound(target.getLocation(), Sound.ENTITY_PUFFER_FISH_BLOW_UP, 0.5F, 1F);
                    if (getProtectionSet().isLocationValid(e.getPlayer().getLocation(), target.getLocation()))
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, (int) ((entities.size() - 1) * 20 + duration * 20), fireBallStacks, false, false, false));
                } else
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, (int) ((entities.size() - 1) * 20 + duration * 20), fireBallStacks, false, false, false));

            }
        }
        setRemainingCooldown(getCooldown() - fireBallStacks);
        updatedCasterMana();
    }
}
