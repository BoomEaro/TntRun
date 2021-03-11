package ru.boomearo.tntrun.objects;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import ru.boomearo.gamecontrol.objects.IGamePlayer;
import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.utils.RandomUtil;

public class TntPlayer implements IGamePlayer {

    private final String name;
    private final Player player;
    
    private IPlayerType playerType;
   
    private TntArena where;
    
    public TntPlayer(String name, Player player, IPlayerType playerType, TntArena where) {
        this.name = name;
        this.player = player;
        this.playerType = playerType;
        this.where = where;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public Player getPlayer() {
        return this.player;
    }
    
    @Override
    public TntArena getArena() {
        return this.where;
    }
    
    public IPlayerType getPlayerType() {
        return this.playerType;
    }
    
    public void setPlayerType(IPlayerType playerType) {
        this.playerType = playerType;
    }
    
    public static interface IPlayerType {
        public void preparePlayer(TntPlayer player);
    }
    
    public static class PlayingPlayer implements IPlayerType {
        
        private String killer;
        
        @Override
        public void preparePlayer(TntPlayer player) {
            if (Bukkit.isPrimaryThread()) {
                task(player);
            }
            else {
                Bukkit.getScheduler().runTask(TntRun.getInstance(), () -> {
                    task(player);
                });
            }
        }
        
        public String getKiller() {
            return this.killer;
        }
        
        public void setKiller(String killer) {
            this.killer = killer;
        }
        
        private void task(TntPlayer player) {
            Player pl = player.getPlayer();
            
            pl.setGameMode(GameMode.ADVENTURE);
            pl.setLevel(0);
            
            Inventory inv = pl.getInventory();
            inv.clear();
            
            for (ItemButton ib : ItemButton.values()) {
                inv.setItem(ib.getSlot(), ib.getItem());
            }

            TntArena arena = player.getArena();
            List<Location> spawns = arena.getSpawnPoints();
            
            Location loc = spawns.get(RandomUtil.getRandomNumberRange(0, spawns.size() - 1));
            
            pl.teleport(loc);
        }
        
    }

    public static class SpectatingPlayer implements IPlayerType {

        @Override
        public void preparePlayer(TntPlayer player) {
            if (Bukkit.isPrimaryThread()) {
                task(player);
            }
            else {
                Bukkit.getScheduler().runTask(TntRun.getInstance(), () -> {
                    task(player);
                });
            }
        }
        
        private void task(TntPlayer player) {
            Player pl = player.getPlayer();
            
            pl.setGameMode(GameMode.SPECTATOR);
            pl.setLevel(0);
            pl.getInventory().clear();
            
            TntArena arena = player.getArena();
            List<Location> spawns = arena.getSpawnPoints();
            
            Location loc = spawns.get(RandomUtil.getRandomNumberRange(0, spawns.size() - 1));
            
            pl.teleport(loc);
        }
    }
    
    //Проигравший игрок. По сути такие игроки становятся спектаторами поэтому наследуем это со спкетатора.
    public static class LosePlayer extends SpectatingPlayer {
        
    }
    
}
