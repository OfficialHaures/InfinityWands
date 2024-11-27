package nl.inferno.infinityWands.listeners;

import nl.inferno.infinityWands.managers.SpellManager;
import nl.inferno.infinityWands.spells.Spell;
import nl.inferno.infinityWands.utils.WandUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class WandListener implements Listener {

    @EventHandler
    public void onWandUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (!WandUtils.isWand(item)) return;

        event.setCancelled(true);

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Spell activeSpell = SpellManager.getSpell(player, SpellManager.getActiveSpellSlot(player));
            if (activeSpell != null) {
                castSpell(player, activeSpell);
            }
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (player.isSneaking()) {
                openSpellWheel(player);
            } else {
                cycleNextSpell(player);
            }
        }
    }

    @EventHandler
    public void onHotbarSwitch(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (WandUtils.isWand(item)) {
            int newSlot = event.getNewSlot();
            SpellManager.setActiveSpell(player, newSlot);
            showActiveSpell(player, newSlot);
        }
    }

    @EventHandler
    public void onHandSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (WandUtils.isWand(event.getOffHandItem()) || WandUtils.isWand(event.getMainHandItem())) {
            event.setCancelled(true);
            cycleSpellMode(player);
        }
    }

    private void cycleNextSpell(Player player) {
        Map<Integer, String> spells = SpellManager.getPlayerSpells(player);
        if (spells.isEmpty()) {
            WandUtils.sendActionBar(player, ChatColor.RED + "Geen spreuken gebonden!");
            return;
        }

        int currentSlot = SpellManager.getActiveSpellSlot(player);
        int nextSlot = findNextSpellSlot(player, currentSlot);

        if (nextSlot != -1) {
            SpellManager.setActiveSpell(player, nextSlot);
            Spell newSpell = SpellManager.getSpell(player, nextSlot);

            // Visual effects
            Location loc = player.getLocation().add(0, 1, 0);
            player.getWorld().spawnParticle(Particle.SPELL_WITCH, loc, 15, 0.2, 0.2, 0.2, 0);
            player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, loc, 20, 0.5, 0.5, 0.5, 0);

            // Sound effects
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.5f);
            player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.5f, 1.2f);

            // Visual feedback
            String spellName = ChatColor.GOLD + newSpell.getName();
            String mana = ChatColor.AQUA + "Mana: " + newSpell.getManaCost();
            String cooldown = ChatColor.GREEN + "CD: " + newSpell.getCooldown() + "s";

            WandUtils.sendActionBar(player, spellName + " §8| " + mana + " §8| " + cooldown);
        }
    }

    private void openSpellWheel(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 0.7f, 1.2f);

        Location loc = player.getLocation();
        double radius = 2;
        Map<Integer, String> spells = SpellManager.getPlayerSpells(player);

        for (int i = 0; i < 360; i += 360 / Math.max(1, spells.size())) {
            double angle = Math.toRadians(i);
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            loc.add(x, 1, z);
            player.getWorld().spawnParticle(Particle.SPELL_INSTANT, loc, 1, 0, 0, 0, 0);
            loc.subtract(x, 1, z);
        }

        player.sendMessage(ChatColor.GOLD + "=== Gebonden Spreuken ===");
        spells.forEach((slot, spellName) -> {
            player.sendMessage(ChatColor.GRAY + "➤ " + ChatColor.YELLOW + spellName);
        });
    }

    private void castSpell(Player player, Spell spell) {
        if (SpellManager.canCastSpell(player, spell)) {
            spell.cast(player);

            Location loc = player.getLocation().add(0, 1, 0);
            player.getWorld().spawnParticle(Particle.SPELL_WITCH, loc, 30, 0.3, 0.3, 0.3, 0.1);
            player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.0f, 1.0f);
        }
    }

    private void showActiveSpell(Player player, int slot) {
        Spell spell = SpellManager.getSpell(player, slot);
        if (spell != null) {
            String message = ChatColor.GREEN + "Actieve Spreuk: " + spell.getName();
            WandUtils.sendActionBar(player, message);
        }
    }

    private void cycleSpellMode(Player player) {
        WandUtils.cycleWandMode(player);
        String mode = WandUtils.getWandMode(player);
        player.sendMessage(ChatColor.AQUA + "Toverstaf modus veranderd naar: " + mode);
    }

    private int findNextSpellSlot(Player player, int currentSlot) {
        Map<Integer, String> spells = SpellManager.getPlayerSpells(player);

        for (int i = currentSlot + 1; i < 9; i++) {
            if (spells.containsKey(i)) return i;
        }

        for (int i = 0; i < currentSlot; i++) {
            if (spells.containsKey(i)) return i;
        }

        return -1;
    }
}
