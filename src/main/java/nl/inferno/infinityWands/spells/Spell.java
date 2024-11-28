package nl.inferno.infinityWands.spells;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;


public abstract class Spell {
    private final String name;
    private final int cooldown;
    private final double manaCost;
    private final Particle particle;

    public Spell(String name, int cooldown, double manaCost, Particle particle) {
        this.name = name;
        this.cooldown = cooldown;
        this.manaCost = manaCost;
        this.particle = particle;
    }

    public abstract void cast(Player player);

    protected void spawnParticleCircle(Location location, double radius) {
        for (double angle = 0; angle < 360; angle += 10) {
            double x = radius * Math.cos(Math.toRadians(angle));
            double z = radius * Math.sin(Math.toRadians(angle));
            location.getWorld().spawnParticle(particle,
                location.clone().add(x, 0, z),
                1, 0, 0, 0, 0);
        }
    }

    // Getters
    public String getName() { return name; }
    public int getCooldown() { return cooldown; }
    public double getManaCost() { return manaCost; }
    public Particle getParticle() { return particle; }
}
