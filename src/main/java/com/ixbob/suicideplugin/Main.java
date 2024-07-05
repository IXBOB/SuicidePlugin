package com.ixbob.suicideplugin;

import com.ixbob.suicideplugin.event.SuicideCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Main extends JavaPlugin {

    public static Plugin instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        saveDefaultConfig();

        Objects.requireNonNull(this.getCommand("suicide")).setExecutor(new SuicideCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Plugin getInstance() {
        return instance;
    }
}
