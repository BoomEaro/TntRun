package ru.boomearo.tntrun;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.spawn.EssentialsSpawn;

import ru.boomearo.tntrun.commands.tntrun.CmdExecutorTntRun;
import ru.boomearo.tntrun.listeners.ArenaListener;
import ru.boomearo.tntrun.listeners.PlayerListener;
import ru.boomearo.tntrun.listeners.SpectatorListener;
import ru.boomearo.tntrun.managers.ArenaManager;
import ru.boomearo.tntrun.objects.Arena;
import ru.boomearo.tntrun.objects.region.CuboidRegion;
import ru.boomearo.tntrun.runnable.ArenasRunnable;

public class TntRun extends JavaPlugin {
    
    private ArenaManager arenaManager = null;
    
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
        ConfigurationSerialization.registerClass(Arena.class);
        
        File configFile = new File(getDataFolder() + File.separator + "config.yml");
        if(!configFile.exists()) {
            getLogger().info("Конфиг не найден, создаю новый...");
            saveDefaultConfig();
        }
        
        if (this.arenaManager == null) {
            this.arenaManager = new ArenaManager();
        }
        
        getCommand("tntrun").setExecutor(new CmdExecutorTntRun());
        
        getServer().getPluginManager().registerEvents(new ArenaListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new SpectatorListener(), this);
        
        if (this.pmr == null) {
            this.pmr = new ArenasRunnable();
        }
        
        getLogger().info("Плагин успешно запущен.");
    }
    
    
    public void onDisable() {
        
        ConfigurationSerialization.unregisterClass(CuboidRegion.class);
        ConfigurationSerialization.unregisterClass(Arena.class);
        
        getLogger().info("Плагин успешно выключен.");
    }
    
    public ArenaManager getArenaManager() {
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
