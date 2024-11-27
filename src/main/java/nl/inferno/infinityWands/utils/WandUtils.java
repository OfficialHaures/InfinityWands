package nl.inferno.infinityWands.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import nl.inferno.infinityWands.InfinityWands;
import nl.inferno.infinityWands.managers.SpellManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WandUtils {
    private static final Map<UUID, String> wandModes = new HashMap<>();
    private static final NamespacedKey WAND_KEY = new NamespacedKey(InfinityWands.getInstance(), "wand_id");

    public static ItemStack createWand(String name, String type) {
        ItemStack wand = new ItemStack(Material.STICK);
        ItemMeta meta = wand.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + name);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Type: " + type);
        lore.add(ChatColor.GRAY + "");
        lore.add(ChatColor.YELLOW + "Gebonden Spreuken:");
        lore.add(ChatColor.GRAY + "Geen spreuken gebonden");

        meta.setLore(lore);
        meta.getPersistentDataContainer().set(WAND_KEY, PersistentDataType.STRING, UUID.randomUUID().toString());

        wand.setItemMeta(meta);
        return wand;
    }

    public static boolean isWand(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(WAND_KEY, PersistentDataType.STRING);
    }

    public static boolean isHoldingWand(Player player) {
        return isWand(player.getInventory().getItemInMainHand());
    }

    public static void updateWandLore(Player player) {
        if (!isHoldingWand(player)) return;

        ItemStack wand = player.getInventory().getItemInMainHand();
        ItemMeta meta = wand.getItemMeta();
        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.GRAY + "Type: " + getWandType(wand));
        lore.add(ChatColor.GRAY + "");
        lore.add(ChatColor.YELLOW + "Gebonden Spreuken:");

        Map<Integer, String> boundSpells = SpellManager.getPlayerSpells(player);
        if (boundSpells.isEmpty()) {
            lore.add(ChatColor.GRAY + "Geen spreuken gebonden");
        } else {
            boundSpells.forEach((slot, spellName) ->
                lore.add(ChatColor.GRAY + " Slot " + (slot + 1) + ": " + ChatColor.GREEN + spellName));
        }

        meta.setLore(lore);
        wand.setItemMeta(meta);
    }

    public static String getWandMode(Player player) {
        return wandModes.getOrDefault(player.getUniqueId(), "Normaal");
    }

    public static void cycleWandMode(Player player) {
        String currentMode = getWandMode(player);
        String newMode = switch (currentMode) {
            case "Normaal" -> "Krachtig";
            case "Krachtig" -> "Precies";
            default -> "Normaal";
        };
        wandModes.put(player.getUniqueId(), newMode);
    }

    public static void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    private static String getWandType(ItemStack wand) {
        List<String> lore = wand.getItemMeta().getLore();
        if (lore != null && !lore.isEmpty()) {
            String typeLine = lore.get(0);
            return typeLine.substring(typeLine.indexOf(":") + 2);
        }
        return "Onbekend";
    }
}
