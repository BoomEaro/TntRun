package ru.boomearo.tntrun.objects.state;

import org.bukkit.Bukkit;

import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.objects.Arena;
import ru.boomearo.tntrun.objects.TntPlayer;

public class RegenState implements IGameState {

    @Override
    public void initState(Arena arena) {
        arena.sendMessages("Начинается регенерация..");
        
        Bukkit.getScheduler().runTaskAsynchronously(TntRun.getInstance(), () -> {
            arena.regenArena();
        });
    }
    
    
    @Override
    public void autoUpdateHandler(Arena arena) {
        for (TntPlayer tp : arena.getAllPlayers()) {
            if (!arena.getArenaRegion().isInRegion(tp.getPlayer().getLocation())) {
                tp.getPlayerType().preparePlayer(tp);
            }
        }
    }

}
