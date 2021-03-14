package ru.boomearo.tntrun.objects.playertype;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import ru.boomearo.gamecontrol.GameControl;
import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.objects.TntPlayer;
import ru.boomearo.tntrun.objects.TntTeam;
import ru.boomearo.tntrun.utils.ExpFix;

public class SpectatingPlayer implements IPlayerType {

    @Override
    public void preparePlayer(TntPlayer player) {
        if (Bukkit.isPrimaryThread()) {
            task(player);
        }
        else {
            Bukkit.getScheduler().runTask(TntRun.getInstance(), () -> {
                task(player);
            });
        }
    }
    
    private void task(TntPlayer player) {
        Player pl = player.getPlayer();
        
        pl.setFoodLevel(20);
        pl.setHealth(pl.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        
        pl.setGameMode(GameMode.SPECTATOR);
        
        ExpFix.setTotalExperience(player.getPlayer(), 0);
        
        pl.getInventory().clear();
        
        TntTeam team = player.getTeam();
        Location loc = team.getSpawnPoint();
        if (loc != null) {
            GameControl.getInstance().asyncTeleport(pl, loc);
        }
    }
}