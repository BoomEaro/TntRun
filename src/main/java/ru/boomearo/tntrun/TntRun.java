package ru.boomearo.tntrun;

import java.io.File;
import java.sql.SQLException;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import ru.boomearo.gamecontrol.GameControl;
import ru.boomearo.gamecontrol.exceptions.ConsoleGameException;
import ru.boomearo.gamecontrol.objects.statistics.StatsPlayer;
import ru.boomearo.tntrun.commands.tntrun.CmdExecutorTntRun;
import ru.boomearo.tntrun.database.Sql;
import ru.boomearo.tntrun.database.sections.SectionStats;
import ru.boomearo.tntrun.listeners.ArenaListener;
import ru.boomearo.tntrun.listeners.PlayerButtonListener;
import ru.boomearo.tntrun.listeners.PlayerListener;
import ru.boomearo.tntrun.listeners.SpectatorListener;
import ru.boomearo.tntrun.managers.TntRunManager;
import ru.boomearo.tntrun.objects.TntArena;
import ru.boomearo.tntrun.objects.TntTeam;
import ru.boomearo.tntrun.objects.statistics.TntStatsData;
import ru.boomearo.tntrun.objects.statistics.TntStatsType;
import ru.boomearo.tntrun.runnable.ArenasRunnable;

public class TntRun extends JavaPlugin {

    private TntRunManager arenaManager = null;

    private ArenasRunnable pmr = null;

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

        loadDataBase();
        loadDataFromDatabase();

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

        if (this.pmr == null) {
            this.pmr = new ArenasRunnable();
        }

        getLogger().info("Плагин успешно запущен.");
    }

    @Override
    public void onDisable() {
        try {
            getLogger().info("Отключаюсь от базы данных");
            Sql.getInstance().disconnect();
            getLogger().info("Успешно отключился от базы данных");
        }
        catch (Exception e) {
            e.printStackTrace();
            getLogger().info("Не удалось отключиться от базы данных...");
        }

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

    private void loadDataBase() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();

        }
        try {
            Sql.initSql();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadDataFromDatabase() {
        try {
            for (TntStatsType type : TntStatsType.values()) {
                TntStatsData data = this.arenaManager.getStatisticManager().getStatsData(type);
                for (SectionStats stats : Sql.getInstance().getAllStatsData(type).get()) {
                    data.addStatsPlayer(new StatsPlayer(stats.name, stats.value));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static TntRun getInstance() {
        return instance;
    }
}
