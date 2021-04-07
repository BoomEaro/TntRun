package ru.boomearo.tntrun.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import ru.boomearo.gamecontrol.objects.IForceStartable;
import ru.boomearo.gamecontrol.objects.arena.ClipboardRegenableGameArena;
import ru.boomearo.gamecontrol.objects.region.IRegion;
import ru.boomearo.gamecontrol.objects.states.IGameState;
import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.managers.TntRunManager;
import ru.boomearo.tntrun.objects.playertype.IPlayerType;
import ru.boomearo.tntrun.objects.state.WaitingState;

public class TntArena extends ClipboardRegenableGameArena implements IForceStartable, ConfigurationSerializable {
    private final int minPlayers;
    private final int maxPlayers;
    private final int timelimit;
    
    private final IRegion arenaRegion;
    private final ConcurrentMap<Integer, TntTeam> teams;
 
    private volatile IGameState state = new WaitingState(this);
    
    private final ConcurrentMap<String, TntPlayer> players = new ConcurrentHashMap<String, TntPlayer>();
    
    private boolean forceStarted = false;
    
    public TntArena(String name, World world, Material icon, Location originCenter, int minPlayers, int maxPlayers, int timeLimit, IRegion arenaRegion, ConcurrentMap<Integer, TntTeam> teams) {
        super(name, world, icon, originCenter);
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.timelimit = timeLimit;
        this.arenaRegion = arenaRegion;
        this.teams = teams;
    }

    @Override
    public boolean isForceStarted() {
        return this.forceStarted;
    }

    @Override
    public void setForceStarted(boolean force) {
        this.forceStarted = force;
    }

    @Override
    public TntPlayer getGamePlayer(String name) {
        return this.players.get(name);
    }
    
    @Override
    public Collection<TntPlayer> getAllPlayers() {
        return this.players.values();
    }
    
    @Override
    public TntRunManager getManager() {
        return TntRun.getInstance().getTntRunManager();
    }
    
    @Override
    public IGameState getState() {
        return this.state;
    }
    
    @Override
    public int getMinPlayers() {
        return this.minPlayers;
    }
    
    @Override
    public int getMaxPlayers() {
        return this.maxPlayers;
    }
    
    public int getTimeLimit() {
        return this.timelimit;
    }
    
    public IRegion getArenaRegion() {
        return this.arenaRegion;
    }
    
    public TntTeam getTeamById(int id) {
        return this.teams.get(id);
    }
    
    public Collection<TntTeam> getAllTeams() {
        return this.teams.values();
    }
    
    public TntTeam getFreeTeam() {
        for (TntTeam team : this.teams.values()) {
            if (team.getPlayer() == null) {
                return team;
            }
        }
        return null;
    }

    public void setState(IGameState state) {
        //Устанавливаем новое
        this.state = state;
        
        //Инициализируем новое
        this.state.initState();
    }
    
    public void addPlayer(TntPlayer player) {
        this.players.put(player.getName(), player);
    }
    
    public void removePlayer(String name) {
        this.players.remove(name);
    }
    
    public void sendMessages(String msg) {
        sendMessages(msg, null);
    }
    public void sendMessages(String msg, String ignore) {
        for (TntPlayer tp : this.players.values()) {
            if (ignore != null) {
                if (tp.getName().equals(ignore)) {
                    continue;
                }
            }
            
            Player pl = tp.getPlayer();
            if (pl.isOnline()) {
                pl.sendMessage(msg);
            }
        }
    }
    
    public void sendLevels(int level) {
        if (Bukkit.isPrimaryThread()) {
            handleSendLevels(level);
        }
        else {
            Bukkit.getScheduler().runTask(TntRun.getInstance(), () -> {
                handleSendLevels(level);
            });
        }
    }
    
    public void sendSounds(Sound sound, float volume, float pitch, Location loc) {
        for (TntPlayer tp : this.players.values()) {
            Player pl = tp.getPlayer();
            if (pl.isOnline()) {
                pl.playSound((loc != null ? loc : pl.getLocation()), sound, volume, pitch);
            }
        }
    }
    
    public void sendSounds(Sound sound, float volume, float pitch) {
        sendSounds(sound, volume, pitch, null);
    }
    
    private void handleSendLevels(int level) {
        for (TntPlayer tp : this.players.values()) {
            Player pl = tp.getPlayer();
            if (pl.isOnline()) {
                pl.setLevel(level);
            }
        }
    }
    
    public Collection<TntPlayer> getAllPlayersType(Class<? extends IPlayerType> clazz) {
        Set<TntPlayer> tmp = new HashSet<TntPlayer>();
        for (TntPlayer tp : this.players.values()) {
            if (tp.getPlayerType().getClass() == clazz) {
                tmp.add(tp);
            }
        }
        return tmp;
    }
    
    public void sendTitle(String first, String second, int in, int stay, int out) {
        for (TntPlayer sp : this.players.values()) {
            Player pl = sp.getPlayer();
            if (pl.isOnline()) {
                pl.sendTitle(first, second, in, stay, out);
            }
        }
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();

        result.put("name", getName());
        result.put("icon", getIcon().name());
        result.put("minPlayers", this.minPlayers);
        result.put("maxPlayers", this.maxPlayers);
        result.put("timeLimit", this.timelimit);
        
        result.put("world", getWorld().getName());
        result.put("region", this.arenaRegion);
        
        List<TntTeam> t = new ArrayList<TntTeam>(this.teams.values());
        result.put("teams", t);
        result.put("arenaCenter", getOriginCenter());
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public static TntArena deserialize(Map<String, Object> args) {
        String name = null;
        Material icon = Material.STONE;
        int minPlayers = 2;
        int maxPlayers = 15;
        int timeLimit = 300;
        World world = null;
        IRegion region = null;
        List<TntTeam> teams = new ArrayList<TntTeam>();
        Location arenaCenter = null;

        Object na = args.get("name");
        if (na != null) {
            name = (String) na;
        }
        
        Object ic = args.get("icon");
        if (ic != null) {
            try {
                icon = Material.valueOf((String) ic);
            }
            catch (Exception e) {}
        }

        Object minp = args.get("minPlayers");
        if (minp != null) {
            minPlayers = ((Number) minp).intValue();
        }

        Object maxp = args.get("maxPlayers");
        if (maxp != null) {
            maxPlayers = ((Number) maxp).intValue();
        }

        Object tl = args.get("timeLimit");
        if (tl != null) {
            timeLimit = ((Number) tl).intValue();
        }
        
        Object wo = args.get("world");
        if (wo != null) {
            world = Bukkit.getWorld((String) wo);
        }

        Object re = args.get("region");
        if (re != null) {
            region = (IRegion) re;
        }

        Object sp = args.get("teams");
        if (sp != null) {
            teams = (List<TntTeam>) sp;
        }

        Object ac = args.get("arenaCenter");
        if (ac != null) {
            arenaCenter = (Location) ac;
        }

        ConcurrentMap<Integer, TntTeam> nTeams = new ConcurrentHashMap<Integer, TntTeam>();
        for (TntTeam team : teams) {
            nTeams.put(team.getId(), team);
        }
        
        return new TntArena(name, world, icon, arenaCenter, minPlayers, maxPlayers, timeLimit, region, nTeams);
    }

}
