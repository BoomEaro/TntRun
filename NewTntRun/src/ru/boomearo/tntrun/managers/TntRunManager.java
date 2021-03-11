package ru.boomearo.tntrun.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import ru.boomearo.gamecontrol.exceptions.ConsoleGameException;
import ru.boomearo.gamecontrol.exceptions.GameControlException;
import ru.boomearo.gamecontrol.exceptions.PlayerGameException;
import ru.boomearo.gamecontrol.objects.IGameManager;
import ru.boomearo.gamecontrol.objects.states.IGameState;

import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.objects.TntArena;
import ru.boomearo.tntrun.objects.TntPlayer;
import ru.boomearo.tntrun.objects.playertype.IPlayerType;
import ru.boomearo.tntrun.objects.playertype.LosePlayer;
import ru.boomearo.tntrun.objects.playertype.PlayingPlayer;
import ru.boomearo.tntrun.objects.state.SpectatorFirst;
import ru.boomearo.tntrun.utils.ExpFix;

public final class TntRunManager implements IGameManager {

    private final ConcurrentMap<String, TntArena> arenas = new ConcurrentHashMap<String, TntArena>();

    private final ConcurrentMap<String, TntPlayer> players = new ConcurrentHashMap<String, TntPlayer>();
    
    public static final double winReward = 4;

    public TntRunManager() {
        loadArenas();
    }

    @Override
    public String getGameName() {
        return "TntRun";
    }

    @Override
    public String getGameDisplayName() {
        return "TntRun";
    }

    @Override
    public JavaPlugin getPlugin() {
        return TntRun.getInstance();
    }

    @Override
    public TntPlayer join(Player pl, String arena) throws ConsoleGameException, PlayerGameException {
        if (pl == null || arena == null) {
            throw new ConsoleGameException("Аргументы не должны быть нулем!");
        }

        TntPlayer tmpPlayer = this.players.get(pl.getName());
        if (tmpPlayer != null) {
            throw new ConsoleGameException("Игрок уже в игре!");
        }

        TntArena tmpArena = this.arenas.get(arena);
        if (tmpArena == null) {
            throw new PlayerGameException("Арена " + arena + " не найдена!");
        }

        int count = tmpArena.getAllPlayers().size();
        if (count >= tmpArena.getMaxPlayers()) {
            throw new PlayerGameException("Арена " + arena + " переполнена!");
        }
        
        IGameState state = tmpArena.getGameState();

        IPlayerType type;
        
        //Если статус игры реализует это, значит добавляем игрока в наблюдатели сначала
        if (state instanceof SpectatorFirst) {
            type = new LosePlayer();
            pl.sendMessage("Вы присоединились к арене " + arena + " как наблюдатель.");
            pl.sendMessage("Чтобы покинуть игру, используйте несколько раз кнопку '1' или телепортируйтесь к любому игроку используя возможность наблюдателя.");
        }
        else {
            type = new PlayingPlayer();
            pl.sendMessage("Вы присоединились к арене " + arena + "!");
            
            if (tmpArena.getAllPlayers().size() < tmpArena.getMinPlayers()) {
                pl.sendMessage("Ожидание " + tmpArena.getMinPlayers() + " игроков для начала игры...");
            }
        }

        //Создаем игрока
        TntPlayer newTp = new TntPlayer(pl.getName(), pl, type, tmpArena);

        //Добавляем в арену
        tmpArena.addPlayer(newTp);

        //Добавляем в список играющих
        this.players.put(pl.getName(), newTp);

        //Обрабатываем игрока
        type.preparePlayer(newTp);
        
        
        for (TntPlayer tpa : tmpArena.getAllPlayers()) {
            if (tpa.getName().equals(pl.getName())) {
                continue;
            }
            tpa.getPlayer().sendMessage("Игрок " + pl.getName() + " присоединился к игре!");
        }
        
        return newTp;
    }

    @Override
    public void leave(Player pl) throws ConsoleGameException, PlayerGameException {
        if (pl == null) {
            throw new ConsoleGameException("Аргументы не должны быть нулем!");
        }

        TntPlayer tmpPlayer = this.players.get(pl.getName());
        if (tmpPlayer == null) {
            throw new PlayerGameException("Игрок не в игре!");
        }

        TntArena arena = tmpPlayer.getArena();
        
        arena.removePlayer(pl.getName());

        this.players.remove(pl.getName());

        if (Bukkit.isPrimaryThread()) {
            handlePlayerLeave(pl, arena);
        }
        else {
            Bukkit.getScheduler().runTask(TntRun.getInstance(), () -> {
                handlePlayerLeave(pl, arena);
            });
        }
    }

    private static void handlePlayerLeave(Player pl, TntArena arena) {
        Location loc = TntRun.getInstance().getEssentialsSpawn().getSpawn("default");
        if (loc != null) {
            pl.teleport(loc);
        }

        pl.setGameMode(GameMode.ADVENTURE);
        
        ExpFix.setTotalExperience(pl, 0);
        
        pl.getInventory().clear();
        
        pl.sendMessage("Вы покинули игру!");
        
        for (TntPlayer tpa : arena.getAllPlayers()) {
            if (tpa.getName().equals(pl.getName())) {
                continue;
            }
            tpa.getPlayer().sendMessage("Игрок " + pl.getName() + " покинул игру!");
        }
    }
    
    @Override
    public TntPlayer getGamePlayer(String name) {
        return this.players.get(name);
    }

    @Override
    public TntArena getGameArena(String name) {
        return this.arenas.get(name);
    }
    
    @Override
    public Collection<TntArena> getAllArenas() {
        return this.arenas.values();
    }
    
    @Override
    public Collection<TntPlayer> getAllPlayers() {
        return this.players.values();
    }
    
    @SuppressWarnings("unchecked")
    public void loadArenas() {

        FileConfiguration fc = TntRun.getInstance().getConfig();
        List<TntArena> arenas = (List<TntArena>) fc.getList("arenas");
        if (arenas != null) {
            for (TntArena ar : arenas) {
                try {
                    addArena(ar);
                } 
                catch (GameControlException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void saveArenas() {
        FileConfiguration fc = TntRun.getInstance().getConfig();

        List<TntArena> tmp = new ArrayList<TntArena>(this.arenas.values());
        fc.set("arenas", tmp);

        TntRun.getInstance().saveConfig();
    }

    public void addArena(TntArena arena) throws ConsoleGameException {
        if (arena == null) {
            throw new ConsoleGameException("Арена не может быть нулем!");
        }

        TntArena tmpArena = this.arenas.get(arena.getName());
        if (tmpArena != null) {
            throw new ConsoleGameException("Арена " + arena.getName() + " уже создана!");
        }

        this.arenas.put(arena.getName(), arena);
    }

    public void removeArena(String name) throws ConsoleGameException {
        if (name == null) {
            throw new ConsoleGameException("Название не может быть нулем!");
        }

        TntArena tmpArena = this.arenas.get(name);
        if (tmpArena == null) {
            throw new ConsoleGameException("Арена " + name + " не найдена!");
        }

        this.arenas.remove(name);
    }


}
