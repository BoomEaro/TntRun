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

import ru.boomearo.gamecontrol.GameControl;
import ru.boomearo.gamecontrol.exceptions.ConsoleGameException;
import ru.boomearo.gamecontrol.exceptions.GameControlException;
import ru.boomearo.gamecontrol.exceptions.PlayerGameException;
import ru.boomearo.gamecontrol.objects.IGameManager;
import ru.boomearo.gamecontrol.objects.states.IGameState;
import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.objects.TntArena;
import ru.boomearo.tntrun.objects.TntPlayer;
import ru.boomearo.tntrun.objects.TntTeam;
import ru.boomearo.tntrun.objects.playertype.IPlayerType;
import ru.boomearo.tntrun.objects.playertype.LosePlayer;
import ru.boomearo.tntrun.objects.playertype.PlayingPlayer;
import ru.boomearo.tntrun.objects.state.SpectatorFirst;
import ru.boomearo.tntrun.utils.ExpFix;

public final class TntRunManager implements IGameManager {

    private final ConcurrentMap<String, TntArena> arenas = new ConcurrentHashMap<String, TntArena>();

    private final ConcurrentMap<String, TntPlayer> players = new ConcurrentHashMap<String, TntPlayer>();
    
    private final TntRunStatistics stats = new TntRunStatistics();
    
    public static final String gameNameDys = "§8[§cTNTRun§8]";
    public static final String prefix = gameNameDys + ": §6";
    
    public static final double winReward = 4;

    public TntRunManager() {
        loadArenas();
        
    }

    @Override
    public String getGameName() {
        return "TNTRun";
    }

    @Override
    public String getGameDisplayName() {
        return gameNameDys;
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
            throw new PlayerGameException("Арена §6'§c" + arena + "§6' не найдена!");
        }

        int count = tmpArena.getAllPlayers().size();
        if (count >= tmpArena.getMaxPlayers()) {
            throw new PlayerGameException("Арена §6'§c" + arena + "§6' переполнена!");
        }
        
        TntTeam team = tmpArena.getFreeTeam();
        if (team == null) {
            throw new ConsoleGameException("Не найдено свободных команд!");
        }
        
        IGameState state = tmpArena.getState();

        IPlayerType type;
        
        boolean isSpec;
        
        //Если статус игры реализует это, значит добавляем игрока в наблюдатели сначала
        if (state instanceof SpectatorFirst) {
            type = new LosePlayer();
            isSpec = true;
        }
        else {
            type = new PlayingPlayer();
            isSpec = false;
        }
        
        //Создаем игрока
        TntPlayer newTp = new TntPlayer(pl.getName(), pl, type, tmpArena, team);

        //Добавляем в команду
        team.setPlayer(newTp);
        
        //Добавляем в арену
        tmpArena.addPlayer(newTp);

        //Добавляем в список играющих
        this.players.put(pl.getName(), newTp);

        //Обрабатываем игрока
        type.preparePlayer(newTp);
        
        if (isSpec) {
            pl.sendMessage(prefix + "Вы присоединились к арене §6'§c" + arena + "§6' как наблюдатель.");
            pl.sendMessage(prefix + "Чтобы покинуть игру, используйте несколько раз §cкнопку §6'§c1§6' или §cтелепортируйтесь к любому игроку §6используя возможность наблюдателя.");
            
            tmpArena.sendMessages(prefix + "Игрок §c" + pl.getName() + " §6присоединился к игре как наблюдатель!");
        }
        else {
            pl.sendMessage(prefix + "Вы присоединились к арене §6'§c" + arena + "§6'!");
            pl.sendMessage(prefix + "Чтобы покинуть игру, используйте §cМагма крем §6или команду §c/tr leave§6.");
            
            int currCount = tmpArena.getAllPlayersType(PlayingPlayer.class).size();
            if (currCount < tmpArena.getMinPlayers()) {
                pl.sendMessage(prefix + "Ожидание §c" + (tmpArena.getMinPlayers() - currCount) + " §6игроков для начала игры...");
            } 
            
            tmpArena.sendMessages(prefix + "Игрок §c" + pl.getName() + " §6присоединился к игре! " + getRemainPlayersArena(tmpArena), pl.getName());
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
            throw new ConsoleGameException("Игрок не в игре!");
        }

        TntTeam team = tmpPlayer.getTeam();
        
        //Удаляем у тимы игрока
        team.setPlayer(null);
        
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
            GameControl.getInstance().asyncTeleport(pl, loc);
        }

        pl.setGameMode(GameMode.ADVENTURE);
        
        ExpFix.setTotalExperience(pl, 0);
        
        pl.getInventory().clear();
        
        pl.sendMessage(prefix + "Вы покинули игру!");
        
        arena.sendMessages(prefix + "Игрок §c" + pl.getName() + " §6покинул игру! " + getRemainPlayersArena(arena), pl.getName());
        
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
    
    @Override
    public TntRunStatistics getStatisticManager() {
        return this.stats;
    }

    public TntArena getArenaByLocation(Location loc) {
        for (TntArena ar : TntRun.getInstance().getTntRunManager().getAllArenas()) {
            if (ar.getArenaRegion().isInRegion(loc)) {
                return ar;
            }
        }
        return null;
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
    
    public static String getRemainPlayersArena(TntArena arena) {
        return "§8[§6" + arena.getAllPlayersType(PlayingPlayer.class).size() + "§7/§c" + arena.getMaxPlayers() + "§8]";
    }
}
