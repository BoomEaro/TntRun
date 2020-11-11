package ru.boomearo.tntrun.managers;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.exceptions.TntRunConsoleException;
import ru.boomearo.tntrun.exceptions.TntRunException;
import ru.boomearo.tntrun.exceptions.TntRunPlayerException;
import ru.boomearo.tntrun.objects.Arena;
import ru.boomearo.tntrun.objects.TntPlayer;
import ru.boomearo.tntrun.objects.TntPlayer.IPlayerType;
import ru.boomearo.tntrun.objects.TntPlayer.PlayingPlayer;
import ru.boomearo.tntrun.objects.TntPlayer.SpectatingPlayer;
import ru.boomearo.tntrun.objects.state.AllowPlayers;
import ru.boomearo.tntrun.objects.state.AllowSpectators;
import ru.boomearo.tntrun.objects.state.IGameState;

public final class ArenaManager {

    private final ConcurrentMap<String, Arena> arenas = new ConcurrentHashMap<String, Arena>();
    
    private final ConcurrentMap<String, TntPlayer> players = new ConcurrentHashMap<String, TntPlayer>();
    
    private final Object lock = new Object();
    
    public ArenaManager() {
        loadArenas();
    }
    
    @SuppressWarnings("unchecked")
    public void loadArenas() {
        TntRun.getInstance().reloadConfig();
        
        FileConfiguration fc = TntRun.getInstance().getConfig();
        List<Arena> arenas = (List<Arena>) fc.getList("arenas");
        for (Arena ar : arenas) {
            try {
                addArena(ar);
            } 
            catch (TntRunException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void saveArenas() {
        FileConfiguration fc = TntRun.getInstance().getConfig();
        fc.set("arenas", null);
        
        for (Arena arena : this.arenas.values()) {
            fc.set("arenas." + arena.getName(), arena);
        }
       
        TntRun.getInstance().saveConfig();
    }
    
    public void addArena(Arena arena) throws TntRunException {
        if (arena == null) {
            throw new TntRunConsoleException("Арена не может быть нулем!");
        }
        
        Arena tmpArena = this.arenas.get(arena.getName());
        if (tmpArena != null) {
            throw new TntRunConsoleException("Арена " + arena.getName() + " уже создана!");
        }
        
        this.arenas.put(arena.getName(), arena);
    }
    
    public void removeArena(String name) throws TntRunException {
        if (name == null) {
            throw new TntRunConsoleException("Название не может быть нулем!");
        }
        
        this.arenas.remove(name);
    }
    
    public Arena getArenaByName(String name) {
        return this.arenas.get(name);
    }
    
    public Collection<Arena> getAllArenas() {
        return this.arenas.values();
    }
    
    public TntPlayer getPlayerByName(String name) {
        return this.players.get(name);
    }
    
    public Collection<TntPlayer> getAllPlayers() {
        return this.players.values();
    }
    
    public void joinArena(Player player, IPlayerType playerType, String arena) throws TntRunException {
        if (player == null) {
            throw new TntRunConsoleException("Игрок не может быть нулем!");
        }
        if (playerType == null) {
            throw new TntRunConsoleException("Тип игрока не может быть нулем!");
        }
        if (arena == null) {
            throw new TntRunConsoleException("Арена не может быть нулем!");
        }
        
        synchronized (this.lock) {
            TntPlayer tmpPlayer = this.players.get(player.getName());
            if (tmpPlayer != null) {
                throw new TntRunPlayerException("Игрок уже в игре!");
            }
            
            Arena tmpArena = this.arenas.get(arena);
            if (tmpArena == null) {
                throw new TntRunPlayerException("Арена " + arena + " не найдена!");
            }
            
            IGameState state = tmpArena.getGameState();
            
            if (playerType instanceof PlayingPlayer) {
                if (state instanceof AllowPlayers) {
                    throw new TntRunPlayerException("Вы не можете присоединться к этой игре!");
                }
                
                int count = tmpArena.getAllPlayersType(PlayingPlayer.class).size();
                if (count >= tmpArena.getMaxPlayers()) {
                    throw new TntRunPlayerException("Арена " + arena + " переполнена!");
                }
                
                
            }
            else if (playerType instanceof SpectatingPlayer) {
                if (state instanceof AllowSpectators) {
                    throw new TntRunPlayerException("Вы не можете наблюдать за этой игрой!");
                }
            }

            //Создаем игрока
            TntPlayer newTp = new TntPlayer(player.getName(), player, playerType, tmpArena);
            
            //Добавляем в арену
            tmpArena.addPlayer(newTp);
            
            //Добавляем в список играющих
            this.players.put(player.getName(), newTp);
            
            //Обрабатываем игрока
            playerType.preparePlayer(newTp);
        }
    }

    public void leaveArena(Player player) throws TntRunException {
        if (player == null) {
            throw new TntRunConsoleException("Игрок не может быть нулем!");
        }
        
        synchronized (this.lock) {
            TntPlayer tmpPlayer = this.players.get(player.getName());
            if (tmpPlayer == null) {
                throw new TntRunPlayerException("Игрок не в игре!");
            }
            
            tmpPlayer.getArena().removePlayer(player.getName());
            
            this.players.remove(player.getName());
            
            //TODO teleport to spawn
        }
    }
    
}
