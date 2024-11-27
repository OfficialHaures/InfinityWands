package nl.inferno.infinityWands.spells;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FrostNovaSpell extends Spell {

    public FrostNovaSpell() {
        super("FrostNova", 10, 30.0, Particle.SNOWFLAKE);
    }

    @Override
    public void cast(Player player) {
        for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
            if (entity instanceof LivingEntity && entity != player) {
                LivingEntity living = (LivingEntity) entity;
                living.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 2));
            }
        }

        for (double radius = 0; radius <= 5; radius += 0.5) {
            spawnParticleCircle(player.getLocation(), radius);
        }

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);
    }
}
