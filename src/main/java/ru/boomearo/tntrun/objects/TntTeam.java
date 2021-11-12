package ru.boomearo.tntrun.objects;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class TntTeam implements ConfigurationSerializable {

    private final int id;

    private Location loc = null;

    private TntPlayer player = null;

    public TntTeam(int id, Location loc) {
        this.id = id;
        this.loc = loc;
    }

    public int getId() {
        return this.id;
    }

    public Location getSpawnPoint() {
        return this.loc;
    }

    public void setSpawnPoint(Location loc) {
        this.loc = loc;
    }

    public TntPlayer getPlayer() {
        return this.player;
    }

    public void setPlayer(TntPlayer player) {
        this.player = player;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();

        result.put("id", this.id);
        result.put("spawnPoint", this.loc);

        return result;
    }

    public static TntTeam deserialize(Map<String, Object> args) {
        int id = -1;
        Location loc = null;

        Object i = args.get("id");
        if (i != null) {
            id = ((Number) i).intValue();
        }

        Object l = args.get("spawnPoint");
        if (l != null) {
            loc = (Location) l;
        }

        return new TntTeam(id, loc);
    }
}
