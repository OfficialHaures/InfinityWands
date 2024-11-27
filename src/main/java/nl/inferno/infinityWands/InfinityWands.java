package nl.inferno.infinityWands;

import nl.inferno.infinityWands.commands.WandCommands;
import nl.inferno.infinityWands.listeners.WandListener;
import nl.inferno.infinityWands.managers.SpellManager;
import nl.inferno.infinityWands.spells.FireballSpell;
import nl.inferno.infinityWands.spells.FrostNovaSpell;
import nl.inferno.infinityWands.spells.TornadoSpell;
import org.bukkit.plugin.java.JavaPlugin;

public final class InfinityWands extends JavaPlugin {

    private static InfinityWands instance;
    private SpellManager spellManager;

    @Override
    public void onEnable() {
        instance = this;
        this.spellManager = new SpellManager();

        registerDefaultSpells();

        WandCommands wandCommands = new WandCommands();
        getCommand("bind").setExecutor(wandCommands);
        getCommand("bind").setTabCompleter(wandCommands);
        getCommand("wandinfo").setExecutor(wandCommands);
        getCommand("spellbook").setExecutor(wandCommands);
        getCommand("unbind").setExecutor(wandCommands);
        getCommand("getwand").setExecutor(wandCommands);

        getServer().getPluginManager().registerEvents(new WandListener(), this);

        saveDefaultConfig();

        getLogger().info("InfinityWands is succesvol geladen!");
    }

    private void registerDefaultSpells() {
        SpellManager.registerSpell(new FireballSpell());
        SpellManager.registerSpell(new FrostNovaSpell());
        SpellManager.registerSpell(new TornadoSpell());
    }

    @Override
    public void onDisable() {
        getLogger().info("InfinityWands wordt uitgeschakeld!");
        instance = null;
    }

    public static InfinityWands getInstance() {
        return instance;
    }

    public SpellManager getSpellManager() {
        return spellManager;
    }
}
