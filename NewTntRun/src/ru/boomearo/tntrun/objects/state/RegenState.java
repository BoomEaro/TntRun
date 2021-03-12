package ru.boomearo.tntrun.objects.state;

import ru.boomearo.gamecontrol.GameControl;
import ru.boomearo.gamecontrol.objects.states.IGameState;
import ru.boomearo.tntrun.managers.TntRunManager;
import ru.boomearo.tntrun.objects.TntArena;
import ru.boomearo.tntrun.objects.TntPlayer;

public class RegenState implements IGameState, SpectatorFirst {
    
    private final TntArena arena;
    
    public RegenState(TntArena arena) {
        this.arena = arena;
    }
    
    @Override
    public String getName() {
        return "§6Регенерация арены";
    }
    
    @Override
    public TntArena getArena() {
        return this.arena;
    }
    
    @Override
    public void initState() {
        this.arena.sendMessages(TntRunManager.prefix + "Начинаем регенерацию арены..");
        
        //Добавляем регенерацию в очередь.
        GameControl.getInstance().getGameManager().queueRegenArena(this.arena);
    }
    
    
    @Override
    public void autoUpdateHandler() {
        for (TntPlayer tp : this.arena.getAllPlayers()) {
            tp.getPlayer().spigot().respawn();
            
            if (!this.arena.getArenaRegion().isInRegion(tp.getPlayer().getLocation())) {
                tp.getPlayerType().preparePlayer(tp);
            }
        }
    }

}
