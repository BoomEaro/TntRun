package ru.boomearo.tntrun.objects.playertype;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.objects.TntArena;
import ru.boomearo.tntrun.objects.TntPlayer;

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
        
        pl.setGameMode(GameMode.SPECTATOR);
        pl.setLevel(0);
        pl.getInventory().clear();
        
        TntArena arena = player.getArena();
        
        pl.teleport(arena.getRandomSpawnLocation());
    }
}