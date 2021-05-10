package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;

public class Souldraw extends XRPGSkill {
    public Souldraw(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("RIGHT_CLICK").addSkill(this);
    }

    @Override
    public void activate(Event event) {
        if (!hasCastItem()) return;
        if (!(event instanceof PlayerInteractEvent)) return;
        PlayerInteractEvent e = (PlayerInteractEvent) event;
        if (e.getItem() == null || e.getItem().getType() != Material.ENCHANTED_BOOK) return;

        if (Utils.isItemNameMatching(e.getItem(), "Book of Darkness")) {
            doSouldraw(e.getPlayer());
        }
    }

    @Override
    public void initialize() {

    }

    private void doSouldraw(Player caster) {
        if (isSkillReady()) {
            double range = getSkillVariables().getDouble("range", 16);

            RayTraceResult result = Utils.rayTrace(caster, range, FluidCollisionMode.NEVER);
            if (result.getHitEntity() != null) {
                double healRatio = getSkillVariables().getDouble("heal-per-damage", 0.5);

                LivingEntity target = (LivingEntity) result.getHitEntity();
                target.damage(getDamage(), caster);
                //Heal the attacker for half of the damage dealt
                Utils.healLivingEntity(caster, target.getLastDamage() * healRatio);
                setRemainingCooldown(getCooldown());
            }
        }
    }
}
