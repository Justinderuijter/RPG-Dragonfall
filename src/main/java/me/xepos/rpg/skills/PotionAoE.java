package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.datatypes.SkillData;
import me.xepos.rpg.skills.base.XRPGPassiveSkill;
import me.xepos.rpg.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class PotionAoE extends XRPGPassiveSkill {

    public PotionAoE(XRPGPlayer xrpgPlayer, SkillData skillVariables, XRPG plugin, int skillLevel) {
        super(xrpgPlayer, skillVariables, plugin, skillLevel);

        xrpgPlayer.getPassiveEventHandler("CONSUME_ITEM").addSkill(this.getClass().getSimpleName() ,this);
    }

    @Override
    public void activate(Event event) {
        if (!(event instanceof PlayerItemConsumeEvent)) return;
        PlayerItemConsumeEvent e = (PlayerItemConsumeEvent) event;
        Player player = e.getPlayer();
        if (!isSkillReady()) {
            player.sendMessage(Utils.getCooldownMessage(getSkillName(), getRemainingCooldown()));
            return;
        }else if(!hasRequiredMana()){
            sendNotEnoughManaMessage();
            return;
        }

        final double xRange = getSkillVariables().getDouble(getSkillLevel(), "x-range", 10.0);
        final double yRange = getSkillVariables().getDouble(getSkillLevel(), "y-range", 5.0);
        final double zRange = getSkillVariables().getDouble(getSkillLevel(), "z-range", xRange);

        List<PotionEffect> potionEffects = new ArrayList<PotionEffect>() {{
            add(new PotionEffect(PotionEffectType.HEAL, 1, 1));
        }};

        Utils.addPotionEffects(getNearbyAlliedPlayers(player, xRange, yRange, zRange), potionEffects);

        setRemainingCooldown(getCooldown());
        updatedCasterMana();
    }

    @Override
    public void initialize() {

    }
}
