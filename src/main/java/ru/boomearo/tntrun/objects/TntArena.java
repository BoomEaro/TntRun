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
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import ru.boomearo.gamecontrol.objects.IForceStartable;
import ru.boomearo.gamecontrol.objects.arena.ClipboardRegenableGameArena;
import ru.boomearo.gamecontrol.objects.region.IRegion;
import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.managers.TntRunManager;
import ru.boomearo.tntrun.objects.playertype.IPlayerType;
import ru.boomearo.tntrun.objects.state.WaitingState;

public class TntArena extends ClipboardRegenableGameArena<TntPlayer> implements IForceStartable, ConfigurationSerializable {
    private final int minPlayers;
    private final int maxPlayers;
    private final int timeLimit;

    private final IRegion arenaRegion;
    private final ConcurrentMap<Integer, TntTeam> teams;

    private boolean forceStarted = false;

    public TntArena(String name, World world, Material icon, Location originCenter, int minPlayers, int maxPlayers, int timeLimit, IRegion arenaRegion, ConcurrentMap<Integer, TntTeam> teams) {
        super(name, world, icon, originCenter);
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.timeLimit = timeLimit;
        this.arenaRegion = arenaRegion;
        this.teams = teams;

        setState(new WaitingState(this));
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
    public TntRunManager getManager() {
        return TntRun.getInstance().getTntRunManager();
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
        return this.timeLimit;
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

    public Collection<TntPlayer> getAllPlayersType(Class<? extends IPlayerType> clazz) {
        Set<TntPlayer> tmp = new HashSet<>();
        for (TntPlayer tp : getAllPlayers()) {
            if (tp.getPlayerType().getClass() == clazz) {
                tmp.add(tp);
            }
        }
        return tmp;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("name", getName());
        result.put("icon", getIcon().name());
        result.put("minPlayers", this.minPlayers);
        result.put("maxPlayers", this.maxPlayers);
        result.put("timeLimit", this.timeLimit);

        result.put("world", getWorld().getName());
        result.put("region", this.arenaRegion);

        List<TntTeam> t = new ArrayList<>(this.teams.values());
        result.put("teams", t);
        result.put("arenaCenter", getOriginCenter());

        return result;
    }

    public static TntArena deserialize(Map<String, Object> args) {
        String name = null;
        Material icon = Material.STONE;
        int minPlayers = 2;
        int maxPlayers = 15;
        int timeLimit = 300;
        World world = null;
        IRegion region = null;
        List<TntTeam> teams = new ArrayList<>();
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
            catch (Exception ignored) {
            }
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

        ConcurrentMap<Integer, TntTeam> nTeams = new ConcurrentHashMap<>();
        for (TntTeam team : teams) {
            nTeams.put(team.getId(), team);
        }

        return new TntArena(name, world, icon, arenaCenter, minPlayers, maxPlayers, timeLimit, region, nTeams);
    }

}
