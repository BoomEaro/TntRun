package ru.boomearo.tntrun.database.runnable;

import java.sql.SQLException;

import org.bukkit.scheduler.BukkitRunnable;

import ru.boomearo.gamecontrol.objects.statistics.StatsPlayer;
import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.database.Sql;
import ru.boomearo.tntrun.objects.statistics.TntStatsType;

public class PutStats extends BukkitRunnable {

    private final TntStatsType type;
	private final StatsPlayer player;
	
	public PutStats(TntStatsType type, StatsPlayer player) {
		this.player = player;
		this.type = type;
		runnable();
	}
	
	private void runnable() {
		this.runTaskAsynchronously(TntRun.getInstance());
	}
	
	@Override
	public void run() {
		try {
			Sql.getInstance().putStatsData(this.type, this.player.getName(), this.player.getValue());
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
