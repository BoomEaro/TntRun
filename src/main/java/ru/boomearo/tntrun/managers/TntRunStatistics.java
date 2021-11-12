package ru.boomearo.tntrun.managers;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ru.boomearo.gamecontrol.objects.statistics.IStatisticsManager;
import ru.boomearo.gamecontrol.objects.statistics.StatsPlayer;
import ru.boomearo.tntrun.database.runnable.PutStats;
import ru.boomearo.tntrun.database.runnable.UpdateStats;
import ru.boomearo.tntrun.objects.statistics.TntStatsData;
import ru.boomearo.tntrun.objects.statistics.TntStatsType;

public class TntRunStatistics implements IStatisticsManager {

    private final ConcurrentMap<TntStatsType, TntStatsData> stats = new ConcurrentHashMap<TntStatsType, TntStatsData>();
    
    public TntRunStatistics() {
        for (TntStatsType type : TntStatsType.values()) {
            this.stats.put(type, new TntStatsData(type));
        }
    }
    
    @Override
    public TntStatsData getStatsData(String name) {
        TntStatsType type = null;
        try {
            type = TntStatsType.valueOf(name);
        }
        catch (Exception e) {}
        if (type == null) {
            return null;
        }
        
        return this.stats.get(type);
    }

    @Override
    public Collection<TntStatsData> getAllStatsData() {
        return this.stats.values();
    }
    
    public TntStatsData getStatsData(TntStatsType type) {
        return this.stats.get(type);
    }
    
    public void addStats(TntStatsType type, String name) {
        TntStatsData data = this.stats.get(type);
        StatsPlayer sp = data.getStatsPlayer(name);
        if (sp == null) {
            StatsPlayer newSp = new StatsPlayer(name, 1);
            data.addStatsPlayer(newSp);
            new PutStats(type, newSp);
            return;
        }
        sp.setValue(sp.getValue() + 1);
        new UpdateStats(type, sp);
    }

}
