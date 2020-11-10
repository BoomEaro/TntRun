package ru.boomearo.tntrun.objects;

import java.io.File;
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
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import com.boydti.fawe.FaweAPI;
import com.sk89q.worldedit.extent.clipboard.Clipboard;

import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.objects.TntPlayer.IPlayerType;
import ru.boomearo.tntrun.objects.region.IRegion;
import ru.boomearo.tntrun.objects.state.IGameState;
import ru.boomearo.tntrun.objects.state.WaitingState;

public class Arena implements ConfigurationSerializable {

    private final String name;
    
    private final int minPlayers;
    private final int maxPlayers;
    private final int timelimit;
    
    private final ArenaStructure structure;
    
    private IGameState state = new WaitingState();
    
    private final ConcurrentMap<String, TntPlayer> players = new ConcurrentHashMap<String, TntPlayer>();
    
    public Arena(String name, int minPlayers, int maxPlayers, int timeLimit, ArenaStructure structure) {
        this.name = name;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.timelimit = timeLimit;
        this.structure = structure;
    }
    
    public String getName() {
        return this.name;
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
    
    public ArenaStructure getArenaStructure() {
        return this.structure;
    }
    
    public IGameState getGameState() {
        return this.state;
    }
    
    public void setGameState(IGameState state) {
        //Старое значение
        this.state.endState(this);
        
        //Устанавливаем новое
        this.state = state;
        
        //Инициализируем новое
        this.state.initState(this);
    }
    
    public void addPlayer(TntPlayer player) {
        this.players.put(player.getName(), player);
    }
    
    public void removePlayer(String name) {
        this.players.remove(name);
    }
    
    public Collection<TntPlayer> getAllPlayers() {
        return this.players.values();
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
        result.put("structure", this.structure);
        
        return result;
    }
    
    public static Arena deserialize(Map<String, Object> args) {
        String name = null;
        int minPlayers = 2;
        int maxPlayers = 15;
        int timeLimit = 300;
        ArenaStructure structure = null;

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

        Object str = args.get("structure");
        if (str != null) {
            structure = (ArenaStructure) str;
        }
        
        return new Arena(name, minPlayers, maxPlayers, timeLimit, structure);
    }
    
    public static class ArenaStructure implements ConfigurationSerializable {
        
        private final World world;
        private final IRegion arenaRegion;
        private final List<Location> spawnPoints;
        
        private final Location arenaCenter;
        
        private final String schematicName;
        private final Clipboard clipboard;
        
        public ArenaStructure(World world, IRegion arenaRegion, List<Location> spawnPoints, Location arenaCenter, String schematicName, Clipboard clipboard) {
            this.world = world;
            this.arenaRegion = arenaRegion;
            this.spawnPoints = spawnPoints;
            this.arenaCenter = arenaCenter;
            this.schematicName = schematicName;
            this.clipboard = clipboard;
        }
        
        public World getWorld() {
            return this.world;
        }
        
        public IRegion getArenaRegion() {
            return this.arenaRegion;
        }
        
        public List<Location> getSpawnPoints() {
            return this.spawnPoints;
        }
        
        public Location getArenaCenter() {
            return this.arenaCenter;
        }
        
        public String getSchematicName() {
            return this.schematicName;
        }
        
        public Clipboard getClipboard() {
            return this.clipboard;
        }

        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> result = new LinkedHashMap<String, Object>();

            result.put("world", this.world.getName());
            result.put("region", this.arenaRegion);
            result.put("spawnPoints", this.spawnPoints);
            result.put("arenaCenter", this.arenaCenter);
            result.put("schematicName", this.schematicName);
            
            return result;
        }
        
        @SuppressWarnings("unchecked")
        public static ArenaStructure deserialize(Map<String, Object> args) {
            World world = null;
            IRegion region = null;
            List<Location> spawnPoints = null;
            Location arenaCenter = null;
            String schematicName = null;

            Object wo = args.get("world");
            if (wo != null) {
                world = Bukkit.getWorld((String) wo);
            }

            Object re = args.get("region");
            if (re != null) {
                region = (IRegion) re;
            }

            Object sp = args.get("spawnPoints");
            if (sp != null) {
                spawnPoints = (List<Location>) sp;
            }

            Object ac = args.get("arenaCenter");
            if (ac != null) {
                arenaCenter = (Location) ac;
            }


            Object sn = args.get("schematicName");
            if (sn != null) {
                schematicName = (String) sn;
            }

            Clipboard cb = null;
            try {
                File schem = new File(TntRun.getInstance().getSchematicDir(), schematicName + ".schem");
                if (schem.exists() && schem.isFile()) {
                    cb = FaweAPI.load(schem);
                }
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
            
            return new ArenaStructure(world, region, spawnPoints, arenaCenter, schematicName, cb);
        }
    }
}
