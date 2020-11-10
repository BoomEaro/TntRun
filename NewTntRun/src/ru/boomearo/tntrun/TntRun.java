package ru.boomearo.tntrun;

import java.io.File;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import ru.boomearo.tntrun.managers.ArenaManager;
import ru.boomearo.tntrun.objects.Arena;
import ru.boomearo.tntrun.objects.Arena.ArenaStructure;
import ru.boomearo.tntrun.objects.region.CuboidRegion;
import ru.boomearo.tntrun.runnable.ArenaRunnable;
import ru.boomearo.tntrun.runnable.PlayerMoveRunnable;

public class TntRun extends JavaPlugin {
    
    private ArenaManager arenaManager = null;
    
    private ArenaRunnable ar = null;
    private PlayerMoveRunnable pmr = null;
    
    private static TntRun instance = null;

    public void onEnable() {
        instance = this;
        
        ConfigurationSerialization.registerClass(CuboidRegion.class);
        ConfigurationSerialization.registerClass(Arena.class);
        ConfigurationSerialization.registerClass(ArenaStructure.class);
        
        File configFile = new File(getDataFolder() + File.separator + "config.yml");
        if(!configFile.exists()) {
            getLogger().info("Конфиг не найден, создаю новый...");
            saveDefaultConfig();
        }
        
        if (this.arenaManager == null) {
            this.arenaManager = new ArenaManager();
        }
        
        
        if (this.ar == null) {
            this.ar = new ArenaRunnable();
        }
        
        if (this.pmr == null) {
            this.pmr = new PlayerMoveRunnable();
        }
        
        getLogger().info("Плагин успешно запущен.");
    }
    
    
    public void onDisable() {
        if (this.ar != null) {
            this.ar.interrupt();
        }
        
        ConfigurationSerialization.unregisterClass(CuboidRegion.class);
        ConfigurationSerialization.unregisterClass(Arena.class);
        ConfigurationSerialization.unregisterClass(ArenaStructure.class);
        
        getLogger().info("Плагин успешно выключен.");
    }
    
    public ArenaManager getArenaManager() {
        return this.arenaManager;
    }
    
    public File getSchematicDir() {
        return new File(this.getDataFolder(), File.separator + "schematics" + File.separator);
    }
    
    public static TntRun getInstance() { 
        return instance;
    }

}
