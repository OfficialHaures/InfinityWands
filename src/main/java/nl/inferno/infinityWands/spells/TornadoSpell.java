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
        super("Tornado", 15, 50.0, Particle.CLOUD);
    }

    @Override
    public void cast(Player player) {
        Location startLoc = player.getLocation();
        double maxHeight = 5.0;
        int duration = 100;

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

                for (double y = 0; y < height; y += 0.2) {
                    double radius = (y / maxHeight) * 2.0;
                    double x = Math.cos(angle + (y * 0.5)) * radius;
                    double z = Math.sin(angle + (y * 0.5)) * radius;

                    Location particleLoc = startLoc.clone().add(x, y, z);
                    player.getWorld().spawnParticle(Particle.CLOUD, particleLoc, 1, 0, 0, 0, 0);
                    player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, particleLoc, 1, 0.1, 0.1, 0.1, 0);
                }

                for (Entity entity : player.getWorld().getNearbyEntities(startLoc, 5, 5, 5)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        Location entityLoc = entity.getLocation();
                        Vector pull = startLoc.toVector().subtract(entityLoc.toVector()).normalize();
                        pull.setY(0.5);
                        entity.setVelocity(pull.multiply(0.5));
                    }
                }

                if (tick % 5 == 0) {
                    player.getWorld().playSound(startLoc, Sound.ENTITY_PHANTOM_FLAP, 0.5f, 0.5f);
                }

                height = Math.min(height + 0.2, maxHeight);
                angle += 0.2;
                tick++;
            }
        }.runTaskTimer(InfinityWands.getInstance(), 0L, 1L);
    }
}
