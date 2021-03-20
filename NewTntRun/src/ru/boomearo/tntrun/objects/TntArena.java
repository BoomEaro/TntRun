package ru.boomearo.tntrun.objects;

import java.io.File;
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
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;

import ru.boomearo.gamecontrol.objects.IGameArena;
import ru.boomearo.gamecontrol.objects.IRegion;
import ru.boomearo.gamecontrol.objects.states.IGameState;
import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.managers.TntRunManager;
import ru.boomearo.tntrun.objects.playertype.IPlayerType;
import ru.boomearo.tntrun.objects.state.WaitingState;

public class TntArena implements IGameArena, ConfigurationSerializable {

    private final String name;
    
    private final int minPlayers;
    private final int maxPlayers;
    private final int timelimit;
    
    private final World world;
    private final IRegion arenaRegion;
    private final ConcurrentMap<Integer, TntTeam> teams;
    
    private final Location arenaCenter;
    
    private final Clipboard clipboard;
 
    private volatile IGameState state = new WaitingState(this);
    
    private final ConcurrentMap<String, TntPlayer> players = new ConcurrentHashMap<String, TntPlayer>();
    
    public TntArena(String name, int minPlayers, int maxPlayers, int timeLimit, World world, IRegion arenaRegion, ConcurrentMap<Integer, TntTeam> teams, Location arenaCenter, Clipboard clipboard) {
        this.name = name;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.timelimit = timeLimit;
        this.world = world;
        this.arenaRegion = arenaRegion;
        this.teams = teams;
        this.arenaCenter = arenaCenter;
        this.clipboard = clipboard;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public World getWorld() {
        return this.world;
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
    public void regen() {
        if (this.clipboard == null) {
            return;
        }
        
        try {
            Location loc = this.arenaCenter;
            
            ClipboardHolder ch = new ClipboardHolder(this.clipboard);
            
            com.sk89q.worldedit.world.World w = FaweAPI.getWorld(this.world.getName());

            EditSession es = new EditSessionBuilder(w).build();
            
            Operation op = ch.createPaste(es)
                    .to(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()))
                    .ignoreAirBlocks(false)
                    .copyEntities(false)
                    .copyBiomes(false)
                    .build();
            
            Operations.completeLegacy(op);
            
            es.flushQueue();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            setState(new WaitingState(this));
        }
    }
    
    public int getMinPlayers() {
        return this.minPlayers;
    }
    
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
    
    public Location getArenaCenter() {
        return this.arenaCenter;
    }
    
    public Clipboard getClipboard() {
        return this.clipboard;
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
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();

        result.put("name", this.name);
        result.put("minPlayers", this.minPlayers);
        result.put("maxPlayers", this.maxPlayers);
        result.put("timeLimit", this.timelimit);
        
        result.put("world", this.world.getName());
        result.put("region", this.arenaRegion);
        
        List<TntTeam> t = new ArrayList<TntTeam>(this.teams.values());
        result.put("teams", t);
        result.put("arenaCenter", this.arenaCenter);
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public static TntArena deserialize(Map<String, Object> args) {
        String name = null;
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


        Clipboard cb = null;
        try {
            File schem = new File(TntRun.getInstance().getSchematicDir(), name + ".schem");
            if (schem.exists() && schem.isFile()) {
                cb = FaweAPI.load(schem);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        
        ConcurrentMap<Integer, TntTeam> nTeams = new ConcurrentHashMap<Integer, TntTeam>();
        for (TntTeam team : teams) {
            nTeams.put(team.getId(), team);
        }
        
        return new TntArena(name, minPlayers, maxPlayers, timeLimit, world, region, nTeams, arenaCenter, cb);
    }


}
