package ru.boomearo.tntrun.objects.state;

import ru.boomearo.gamecontrol.GameControl;
import ru.boomearo.gamecontrol.exceptions.ConsoleGameException;
import ru.boomearo.gamecontrol.objects.states.IGameState;
import ru.boomearo.gamecontrol.runnable.RegenTask;
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
        try {
            GameControl.getInstance().getGameManager().queueRegenArena(new RegenTask(this.arena, () -> {
                TntArena arena = this.arena;
                
                IGameState state = arena.getState();
                if (state instanceof RunningState || state instanceof EndingState || state instanceof RegenState) {
                    arena.setState(new WaitingState(this.arena));
                }
            }));
        } 
        catch (ConsoleGameException e) {
            e.printStackTrace();
        }
    }
    
    
    @Override
    public void autoUpdateHandler() {
        for (TntPlayer tp : this.arena.getAllPlayers()) {
            tp.getPlayer().spigot().respawn();
            
            if (!this.arena.getArenaRegion().isInRegionPoint(tp.getPlayer().getLocation())) {
                tp.getPlayerType().preparePlayer(tp);
            }
        }
    }

}
