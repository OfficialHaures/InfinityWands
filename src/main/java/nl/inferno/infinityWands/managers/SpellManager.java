package nl.inferno.infinityWands.managers;

import nl.inferno.infinityWands.spells.Spell;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class SpellManager {
    private static final Map<UUID, Map<Integer, Spell>> playerSpells = new HashMap<>();
    private static final Map<UUID, Map<Spell, Long>> cooldowns = new HashMap<>();
    private static final Map<String, Spell> registeredSpells = new HashMap<>();
    private static final Map<UUID, Integer> activeSpellSlots = new HashMap<>();

    public static void registerSpell(Spell spell) {
        registeredSpells.put(spell.getName().toLowerCase(), spell);
    }

    public static void bindSpell(Player player, Spell spell, int slot) {
        if (spell == null) return;
        playerSpells.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                   .put(slot, spell);
    }

    public static void unbindSpell(Player player, int slot) {
        playerSpells.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                   .remove(slot);
    }

    public static boolean canCastSpell(Player player, Spell spell) {
        Map<Spell, Long> playerCooldowns = cooldowns.computeIfAbsent(
            player.getUniqueId(), k -> new HashMap<>());

        long lastCast = playerCooldowns.getOrDefault(spell, 0L);
        long currentTime = System.currentTimeMillis();
        long remainingCooldown = (lastCast + (spell.getCooldown() * 1000)) - currentTime;

        if (remainingCooldown > 0) {
            player.sendMessage(ChatColor.RED + "Je moet nog " + (remainingCooldown / 1000) + " seconden wachten!");
            return false;
        }
        return true;
    }

    public static void castSpell(Player player, int slot) {
        Spell spell = getSpell(player, slot);
        if (spell != null && canCastSpell(player, spell)) {
            spell.cast(player);
            updateCooldown(player, spell);
        }
    }

    private static void updateCooldown(Player player, Spell spell) {
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(spell, System.currentTimeMillis());
    }

    public static Spell getSpell(Player player, int slot) {
        return playerSpells.getOrDefault(player.getUniqueId(), new HashMap<>())
                          .get(slot);
    }

    public static Optional<Spell> getSpellByName(String name) {
        return Optional.ofNullable(registeredSpells.get(name.toLowerCase()));
    }

    public static List<Spell> getAllSpells() {
        return new ArrayList<>(registeredSpells.values());
    }

    public static Map<Integer, String> getPlayerSpells(Player player) {
        Map<Integer, String> spellNames = new HashMap<>();
        playerSpells.getOrDefault(player.getUniqueId(), new HashMap<>())
                   .forEach((slot, spell) -> spellNames.put(slot, spell.getName()));
        return spellNames;
    }

    public static void clearPlayerSpells(Player player) {
        playerSpells.remove(player.getUniqueId());
        cooldowns.remove(player.getUniqueId());
    }

    public static void setActiveSpell(Player player, int nextSlot) {
        activeSpellSlots.put(player.getUniqueId(), nextSlot);
    }
    public static int getActiveSpellSlot(Player player) {
        return activeSpellSlots.getOrDefault(player.getUniqueId(), 0);
    }
}