package nl.inferno.infinityWands.spells;

import nl.inferno.infinityWands.InfinityWands;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TornadoSpell extends Spell {

    public TornadoSpell() {
        super("DarkTornado", 20, 75.0, Particle.SMOKE_LARGE);
    }

    @Override
    public void cast(Player player) {
        Location startLoc = player.getLocation();
        double maxHeight = 8.0; // Increased height
        int duration = 140; // 7 seconds

        new BukkitRunnable() {
            double height = 0;
            double angle = 0;
            int tick = 0;

            @Override
            public void run() {
                if (tick >= duration) {
                    cancel();
                    return;
                }

                // Create massive tornado shape
                for (double y = 0; y < height; y += 0.2) {
                    double radius = (y / maxHeight) * 3.5; // Increased radius

                    // Multiple spiral arms
                    for (int i = 0; i < 3; i++) {
                        double offsetAngle = angle + ((2 * Math.PI * i) / 3);
                        double x = Math.cos(offsetAngle + (y * 0.5)) * radius;
                        double z = Math.sin(offsetAngle + (y * 0.5)) * radius;

                        Location particleLoc = startLoc.clone().add(x, y, z);

                        // Black smoke
                        player.getWorld().spawnParticle(Particle.SMOKE_LARGE, particleLoc, 1, 0.1, 0.1, 0.1, 0);
                        player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, particleLoc, 2, 0.2, 0.2, 0.2, 0);

                        // Fire particles
                        if (tick % 3 == 0) {
                            player.getWorld().spawnParticle(Particle.FLAME, particleLoc, 1, 0.1, 0.1, 0.1, 0.02);
                            player.getWorld().spawnParticle(Particle.LAVA, particleLoc, 1, 0.1, 0.1, 0.1, 0);
                        }
                    }
                }

                // Strong entity pull
                for (Entity entity : player.getWorld().getNearbyEntities(startLoc, 8, 8, 8)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        Location entityLoc = entity.getLocation();
                        Vector pull = startLoc.toVector().subtract(entityLoc.toVector()).normalize();
                        pull.setY(0.7);
                        entity.setVelocity(pull.multiply(0.8));

                        // Damage entities caught in tornado
                        if (tick % 20 == 0) {
                            ((LivingEntity) entity).damage(4.0);
                        }
                    }
                }

                // Enhanced sound effects
                if (tick % 5 == 0) {
                    player.getWorld().playSound(startLoc, Sound.ENTITY_PHANTOM_FLAP, 1.0f, 0.5f);
                    player.getWorld().playSound(startLoc, Sound.BLOCK_FIRE_AMBIENT, 0.8f, 1.0f);
                }

                height = Math.min(height + 0.3, maxHeight);
                angle += 0.3;
                tick++;
            }
        }.runTaskTimer(InfinityWands.getInstance(), 0L, 1L);
    }
}
