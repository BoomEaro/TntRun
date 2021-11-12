package ru.boomearo.tntrun.objects.statistics;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ru.boomearo.gamecontrol.objects.statistics.IStatsData;
import ru.boomearo.gamecontrol.objects.statistics.StatsPlayer;

public class TntStatsData implements IStatsData {

    private final TntStatsType type;

    private final ConcurrentMap<String, StatsPlayer> players = new ConcurrentHashMap<>();

    public TntStatsData(TntStatsType type) {
        this.type = type;
    }

    @Override
    public String getName() {
        return this.type.name();
    }

    @Override
    public StatsPlayer getStatsPlayer(String name) {
        return this.players.get(name);
    }

    @Override
    public Collection<StatsPlayer> getAllStatsPlayer() {
        return this.players.values();
    }

    public TntStatsType getType() {
        return this.type;
    }

    public void addStatsPlayer(StatsPlayer data) {
        this.players.put(data.getName(), data);
    }
}
