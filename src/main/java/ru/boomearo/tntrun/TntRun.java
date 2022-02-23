package ru.boomearo.tntrun;

import java.io.File;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import ru.boomearo.gamecontrol.GameControl;
import ru.boomearo.gamecontrol.exceptions.ConsoleGameException;
import ru.boomearo.tntrun.commands.tntrun.CmdExecutorTntRun;
import ru.boomearo.tntrun.listeners.ArenaListener;
import ru.boomearo.tntrun.listeners.PlayerButtonListener;
import ru.boomearo.tntrun.listeners.PlayerListener;
import ru.boomearo.tntrun.listeners.SpectatorListener;
import ru.boomearo.tntrun.managers.TntRunManager;
import ru.boomearo.tntrun.objects.TntArena;
import ru.boomearo.tntrun.objects.TntTeam;

public class TntRun extends JavaPlugin {

    private TntRunManager arenaManager = null;

    private static TntRun instance = null;

    @Override
    public void onEnable() {
        instance = this;

        ConfigurationSerialization.registerClass(TntArena.class);
        ConfigurationSerialization.registerClass(TntTeam.class);

        File configFile = new File(getDataFolder() + File.separator + "config.yml");
        if (!configFile.exists()) {
            getLogger().info("Конфиг не найден, создаю новый...");
            saveDefaultConfig();
        }

        if (this.arenaManager == null) {
            this.arenaManager = new TntRunManager();
        }

        try {
            GameControl.getInstance().getGameManager().registerGame(this.getClass(), this.arenaManager);
        }
        catch (ConsoleGameException e) {
            e.printStackTrace();
        }

        getCommand("tntrun").setExecutor(new CmdExecutorTntRun());

        getServer().getPluginManager().registerEvents(new ArenaListener(), this);

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerButtonListener(), this);

        getServer().getPluginManager().registerEvents(new SpectatorListener(), this);

        getLogger().info("Плагин успешно запущен.");
    }

    @Override
    public void onDisable() {
        try {
            GameControl.getInstance().getGameManager().unregisterGame(this.getClass());
        }
        catch (ConsoleGameException e) {
            e.printStackTrace();
        }

        ConfigurationSerialization.unregisterClass(TntArena.class);
        ConfigurationSerialization.unregisterClass(TntTeam.class);

        getLogger().info("Плагин успешно выключен.");
    }

    public TntRunManager getTntRunManager() {
        return this.arenaManager;
    }

    public static TntRun getInstance() {
        return instance;
    }
}
