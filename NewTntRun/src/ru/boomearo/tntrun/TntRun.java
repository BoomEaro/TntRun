package ru.boomearo.tntrun;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.spawn.EssentialsSpawn;

import ru.boomearo.gamecontrol.GameControl;
import ru.boomearo.gamecontrol.exceptions.ConsoleGameException;
import ru.boomearo.gamecontrol.objects.states.IGameState;
import ru.boomearo.tntrun.commands.tntrun.CmdExecutorTntRun;
import ru.boomearo.tntrun.listeners.ArenaListener;
import ru.boomearo.tntrun.listeners.PlayerButtonListener;
import ru.boomearo.tntrun.listeners.PlayerListener;
import ru.boomearo.tntrun.listeners.SpectatorListener;
import ru.boomearo.tntrun.managers.TntRunManager;
import ru.boomearo.tntrun.objects.TntArena;
import ru.boomearo.tntrun.objects.region.CuboidRegion;
import ru.boomearo.tntrun.objects.state.RegenState;
import ru.boomearo.tntrun.runnable.ArenasRunnable;

public class TntRun extends JavaPlugin {
    
    private TntRunManager arenaManager = null;
    
    private ArenasRunnable pmr = null;
    
    private Essentials ess = null;
    private EssentialsSpawn essSpawn = null;
    
    public static final String prefix = "§8[§cTntRun§8]: §f";
    
    private static TntRun instance = null;

    public void onEnable() {
        instance = this;
        
        this.ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        this.essSpawn = (EssentialsSpawn) Bukkit.getPluginManager().getPlugin("EssentialsSpawn");
        
        ConfigurationSerialization.registerClass(CuboidRegion.class);
        ConfigurationSerialization.registerClass(TntArena.class);
        
        File configFile = new File(getDataFolder() + File.separator + "config.yml");
        if(!configFile.exists()) {
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
        
        if (this.pmr == null) {
            this.pmr = new ArenasRunnable();
        }
        
        getLogger().info("Плагин успешно запущен.");
    }
    
    
    public void onDisable() {
        try {
            GameControl.getInstance().getGameManager().unregisterGame(this.getClass());
        } 
        catch (ConsoleGameException e) {
            e.printStackTrace();
        }

        for (TntArena ar : this.arenaManager.getAllArenas()) {
            IGameState state = ar.getGameState();
            //Если выключение сервера застал в момент регенерации, то ничего не делаем
            if (state instanceof RegenState) {
                continue;
            }
            ar.regen();
        }
        
        ConfigurationSerialization.unregisterClass(CuboidRegion.class);
        ConfigurationSerialization.unregisterClass(TntArena.class);
        
        getLogger().info("Плагин успешно выключен.");
    }
    
    public TntRunManager getTntRunManager() {
        return this.arenaManager;
    }
    
    public Essentials getEssentials() {
        return this.ess;
    }
    
    public EssentialsSpawn getEssentialsSpawn() {
        return this.essSpawn;
    }
    
    public File getSchematicDir() {
        return new File(this.getDataFolder(), File.separator + "schematics" + File.separator);
    }
    
    
    public static TntRun getInstance() { 
        return instance;
    }

}
