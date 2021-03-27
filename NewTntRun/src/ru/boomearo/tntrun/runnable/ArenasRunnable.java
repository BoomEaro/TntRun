package ru.boomearo.tntrun.runnable;

import org.bukkit.scheduler.BukkitRunnable;

import ru.boomearo.gamecontrol.objects.states.IGameState;
import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.objects.TntArena;

public class ArenasRunnable extends BukkitRunnable {
    
    public ArenasRunnable() {
        runnable();
    }
    
    private void runnable() {
        this.runTaskTimer(TntRun.getInstance(), 1, 1);
    }
    
    @Override
    public void run() {
        for (TntArena arena : TntRun.getInstance().getTntRunManager().getAllArenas()) {
            
            IGameState state = arena.getState();
            
            state.autoUpdateHandler();
        }
    }

}
