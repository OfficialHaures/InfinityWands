package nl.inferno.infinityWands.spells;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

public class FireballSpell extends Spell {

    public FireballSpell() {
        super("Fireball", 5, 20.0, Particle.FLAME);
    }

    @Override
    public void cast(Player player) {
        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setYield(2.0f);
        fireball.setIsIncendiary(true);

        spawnParticleCircle(player.getLocation(), 1.5);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.0f);
    }
}
