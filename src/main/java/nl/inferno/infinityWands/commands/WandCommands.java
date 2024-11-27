package nl.inferno.infinityWands.commands;

import nl.inferno.infinityWands.managers.SpellManager;
import nl.inferno.infinityWands.spells.Spell;
import nl.inferno.infinityWands.utils.WandUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class WandCommands implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Dit commando kan alleen door spelers gebruikt worden!");
            return true;
        }

        Player player = (Player) sender;

        switch (label.toLowerCase()) {
            case "bind":
                handleBindCommand(player, args);
                break;
            case "wandinfo":
                handleWandInfo(player);
                break;
            case "spellbook":
                handleSpellbook(player);
                break;
            case "unbind":
                handleUnbind(player, args);
                break;
            case "getwand":
                handleGetWand(player);
                break;
        }
        return true;
    }

    private void handleGetWand(Player player) {
        if (player.hasPermission("infinitywands.createwand")) {
            ItemStack wand = WandUtils.createWand("Toverstaf", "G-Play");
            player.getInventory().addItem(wand);
            player.sendMessage(ChatColor.GREEN + "Je hebt een nieuwe toverstaf gekregen!");
        } else {
            player.sendMessage(ChatColor.RED + "Je hebt geen toestemming om dit commando uit te voeren!");
        }
    }

    private void handleWandInfo(Player player) {
        if (!WandUtils.isHoldingWand(player)) {
            player.sendMessage(ChatColor.RED + "Je moet een toverstaf vasthouden!");
            return;
        }

        player.sendMessage(ChatColor.GOLD + "=== Toverstaf Info ===");
        player.sendMessage(ChatColor.YELLOW + "Mode: " + WandUtils.getWandMode(player));

        Map<Integer, String> boundSpells = SpellManager.getPlayerSpells(player);
        player.sendMessage(ChatColor.YELLOW + "Gebonden Spreuken:");

        if (boundSpells.isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "- Geen spreuken gebonden");
        } else {
            boundSpells.forEach((slot, spellName) ->
                    player.sendMessage(ChatColor.GRAY + "- Slot " + (slot + 1) + ": " + ChatColor.GREEN + spellName));
        }
    }

    private void handleSpellbook(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Beschikbare Spreuken ===");

        List<Spell> spells = SpellManager.getAllSpells();
        for (Spell spell : spells) {
            player.sendMessage(ChatColor.GRAY + "- " + ChatColor.GREEN + spell.getName());
            player.sendMessage(ChatColor.GRAY + "  Mana: " + spell.getManaCost() + " | Cooldown: " + spell.getCooldown() + "s");
        }

        player.sendMessage(ChatColor.YELLOW + "Gebruik /bind <spreuk> <slot> om een spreuk te binden!");
    }


    private void handleBindCommand(Player player, String[] args) {
        if (!WandUtils.isHoldingWand(player)) {
            player.sendMessage(ChatColor.RED + "Je moet een toverstaf vasthouden!");
            return;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Gebruik: /bind <spell> <slot>");
            return;
        }

        String spellName = args[0];
        Optional<Spell> spellOpt = SpellManager.getSpellByName(spellName);

        if (spellOpt.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Spreuk '" + spellName + "' bestaat niet!");
            return;
        }

        try {
            int slot = Integer.parseInt(args[1]) - 1;
            if (slot < 0 || slot > 8) {
                player.sendMessage(ChatColor.RED + "Slot moet tussen 1 en 9 zijn!");
                return;
            }

            SpellManager.bindSpell(player, spellOpt.get(), slot);
            WandUtils.updateWandLore(player);
            player.sendMessage(ChatColor.GREEN + "Spreuk '" + spellName + "' gebonden aan slot " + (slot + 1) + "!");

        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Ongeldig slot nummer!");
        }
    }

    private void handleUnbind(Player player, String[] args) {
        if (!WandUtils.isHoldingWand(player)) {
            player.sendMessage(ChatColor.RED + "Je moet een toverstaf vasthouden!");
            return;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Gebruik: /unbind <slot>");
            return;
        }

        try {
            int slot = Integer.parseInt(args[0]) - 1;
            if (slot < 0 || slot > 8) {
                player.sendMessage(ChatColor.RED + "Slot moet tussen 1 en 9 zijn!");
                return;
            }

            SpellManager.unbindSpell(player, slot);
            WandUtils.updateWandLore(player);
            player.sendMessage(ChatColor.GREEN + "Spreuk verwijderd van slot " + (slot + 1) + "!");

        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Ongeldig slot nummer!");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return null;

        List<String> completions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("bind") && args.length == 1) {
            String partial = args[0].toLowerCase();
            completions = SpellManager.getAllSpells().stream()
                    .map(Spell::getName)
                    .filter(spell -> spell.toLowerCase().startsWith(partial))
                    .collect(Collectors.toList());
        }

        return completions;
    }
}