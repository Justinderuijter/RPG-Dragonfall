package me.xepos.rpg.skills;

import me.xepos.rpg.XRPG;
import me.xepos.rpg.XRPGPlayer;
import me.xepos.rpg.skills.base.XRPGSkill;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class LotusStrike extends XRPGSkill {
    public LotusStrike(XRPGPlayer xrpgPlayer, ConfigurationSection skillVariables, XRPG plugin) {
        super(xrpgPlayer, skillVariables, plugin);

        xrpgPlayer.getEventHandler("DAMAGE_DEALT").addSkill(this);
    }

    private final double potionDuration = getSkillVariables().getDouble("duration", 6);
    private int hitCount = 0;

    private final List<PotionEffect> dmgEffects = new ArrayList<PotionEffect>() {{
        add(new PotionEffect(PotionEffectType.SATURATION, (int) (potionDuration * 20), 0, false, false, true));
        add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, (int) (potionDuration * 20), 0, false, false, true));
        add(new PotionEffect(PotionEffectType.FAST_DIGGING, (int) (potionDuration * 20), 1, false, false, true));
        add(new PotionEffect(PotionEffectType.SPEED, (int) (potionDuration * 20), 0, false, false, true));
    }};

    @Override
    public void activate(Event event) {
        if (!hasCastItem()) return;
        if (!(event instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

        Player player = (Player) e.getDamager();
        double armor = player.getAttribute(Attribute.GENERIC_ARMOR).getValue();
        double toughness = player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();
        HashMap<Enchantment, Integer> enchantValues = getProtection(player);
        int protectionLevel = enchantValues.get(Enchantment.PROTECTION_ENVIRONMENTAL);
        int fireProtectionLevel = enchantValues.get(Enchantment.PROTECTION_FIRE);
        int explosionProtectionLevel = enchantValues.get(Enchantment.PROTECTION_EXPLOSIONS);
        int projectileProtectionLevel = enchantValues.get(Enchantment.PROTECTION_EXPLOSIONS);

        double fistDamage = 2 + (armor * 0.3) + (toughness * 0.25) + (protectionLevel * 0.25);

        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            e.setDamage(fistDamage);
            if (isCrit(explosionProtectionLevel)) {
                player.sendMessage(ChatColor.GREEN + "You hit a weak spot and dealt bonus damage!");
                e.setDamage(fistDamage * getDamageMultiplier());
            }
            doLotusHaste(projectileProtectionLevel);
            setFire(e, fireProtectionLevel);
            if (canUseLotus(player)) {
                //Lotus Strike logic
                double originalDamage = e.getDamage();
                double lotusDamage = originalDamage * getDamageMultiplier();
                e.setDamage(lotusDamage);
                player.sendMessage(ChatColor.GREEN + getSkillName() + " dealt " + String.format("%,.2f", lotusDamage - originalDamage) + " bonus damage!");
                player.playSound(player.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1F, 0.5F);
                applyTriggerEffect(player);
            } else {
                //If !canUseLotus
                incrementHitCount();
            }
        }
    }

    @Override
    public void initialize() {

    }

    private HashMap<Enchantment, Integer> getProtection(Player player) {
        ArrayList<ItemStack> armor = new ArrayList<ItemStack>() {{
            add(player.getEquipment().getHelmet());
            add(player.getEquipment().getChestplate());
            add(player.getEquipment().getLeggings());
            add(player.getEquipment().getBoots());
        }};
        HashMap<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>() {{
            put(Enchantment.PROTECTION_ENVIRONMENTAL, 0);
            put(Enchantment.PROTECTION_EXPLOSIONS, 0);
            put(Enchantment.PROTECTION_FIRE, 0);
            put(Enchantment.PROTECTION_PROJECTILE, 0);
        }};
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            for (ItemStack armorPiece : armor) {
                if (armorPiece != null && armorPiece.containsEnchantment(enchantment)) {
                    int enchantLevel = armorPiece.getEnchantmentLevel(enchantment);
                    enchantments.replace(enchantment, enchantments.get(enchantment) + enchantLevel);
                }
            }
        }
        return enchantments;
    }

    @SuppressWarnings("all")
    private boolean isCrit(int enchantLevel) {
        final double enchantToCritRatio = getSkillVariables().getDouble("enchant-to-crit-ratio", 3.125);
        Random rand = new Random();

        if (rand.nextInt(100) + 1 <= enchantLevel * enchantToCritRatio) //0-100
        {
            return true;
        }
        return false;
    }

    private void setFire(EntityDamageByEntityEvent e, int enchantLevel) {
        if (enchantLevel > 0 && e.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) e.getEntity();
            if (entity.getFireTicks() <= -1) {
                final int enchantToFireTicks = getSkillVariables().getInt("enchant-to-fire-ticks", 15);

                entity.setFireTicks(enchantLevel * enchantToFireTicks);
            }
        }
    }

    private void doLotusHaste(int enchantLevel) {
        final double enchantToLotusHaste = getSkillVariables().getDouble("enchant-to-lotus-haste-ratio", 2.0);
        Random rand = new Random();

        if (rand.nextInt(100) + 1 <= enchantLevel * enchantToLotusHaste) {
            incrementHitCount();
        }
    }

    public void incrementHitCount() {
        if (hitCount < 7) {
            hitCount++;
        }
    }

    public boolean canUseLotus(Player player) {
        if (hitCount == 6) {
            player.sendMessage(ChatColor.DARK_GREEN + "Lotus techniques are ready to be used!");
            return false;
        }
        return hitCount >= 6;
    }

    private void applyTriggerEffect(Player player) {
        Random rand = new Random();

        player.addPotionEffect(dmgEffects.get(rand.nextInt(dmgEffects.size())));
        hitCount = 0;
    }
}
