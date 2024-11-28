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

import java.util.*;

public class ConvulsionSpell extends Spell {
    private final Random random = new Random();
    private final Set<Location> affectedLocations = new HashSet<>();
    private final Queue<Location> spreadQueue = new LinkedList<>();

    public ConvulsionSpell() {
        super("Convulsion", 25, 80.0, Particle.SOUL_FIRE_FLAME);
    }

    @Override
    public void cast(Player player) {
        Location target = player.getTargetBlock(null, 30).getLocation();
        spreadQueue.add(target);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 200 || spreadQueue.isEmpty()) {
                    cancel();
                    return;
                }

                for (int i = 0; i < 3; i++) {
                    if (!spreadQueue.isEmpty()) {
                        Location current = spreadQueue.poll();
                        createEffectAt(current, player);
                        addSpreadLocations(current);
                    }
                }

                if (ticks % 5 == 0) {
                    target.getWorld().playSound(target, Sound.BLOCK_SOUL_SAND_BREAK, 1.0f, 0.5f);
                    target.getWorld().playSound(target, Sound.PARTICLE_SOUL_ESCAPE, 0.8f, 0.8f);
                }

                ticks++;
            }
        }.runTaskTimer(InfinityWands.getInstance(), 0L, 1L);
    }

    private void createEffectAt(Location loc, Player caster) {
        loc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, loc, 5, 0.2, 0, 0.2, 0);
        loc.getWorld().spawnParticle(Particle.SOUL, loc, 3, 0.1, 0, 0.1, 0);
        loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc, 2, 0.1, 0, 0.1, 0.05);

        for (Entity entity : loc.getWorld().getNearbyEntities(loc, 1, 2, 1)) {
            if (entity instanceof LivingEntity && entity != caster) {
                ((LivingEntity) entity).damage(4.0);
                Vector knockback = entity.getLocation().toVector().subtract(loc.toVector()).normalize();
                entity.setVelocity(knockback.multiply(0.5).setY(0.2));
            }
        }
    }

    private void addSpreadLocations(Location center) {
        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {-1,-1}, {1,-1}, {-1,1}};

        for (int[] dir : directions) {
            Location newLoc = center.clone().add(dir[0], 0, dir[1]);
            if (!affectedLocations.contains(newLoc) && random.nextDouble() < 0.7) {
                affectedLocations.add(newLoc);
                spreadQueue.add(newLoc);
            }
        }
    }
}
