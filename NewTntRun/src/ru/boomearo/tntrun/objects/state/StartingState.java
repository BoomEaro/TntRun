package ru.boomearo.tntrun.objects.state;

import ru.boomearo.gamecontrol.objects.states.ICountable;
import ru.boomearo.gamecontrol.objects.states.IGameState;
import ru.boomearo.tntrun.objects.TntArena;
import ru.boomearo.tntrun.objects.TntPlayer;
import ru.boomearo.tntrun.objects.TntPlayer.PlayingPlayer;

public class StartingState implements IGameState, ICountable {

    private final TntArena arena;
    
    private int count = 30;
    
    private int cd = 20;
    
    public StartingState(TntArena arena) {
        this.arena = arena;
    }
    
    @Override
    public String getName() {
        return "Начало игры";
    }
    
    @Override
    public TntArena getArena() {
        return this.arena;
    }
    
    @Override
    public void initState() {
        this.arena.sendMessages("Набралось достаточно игроков, начинаем игру!");
    }
    
    @Override
    public void autoUpdateHandler() {
        for (TntPlayer tp : this.arena.getAllPlayers()) {
            if (!this.arena.getArenaRegion().isInRegion(tp.getPlayer().getLocation())) {
                tp.getPlayerType().preparePlayer(tp);
            }
        }
        //Если игроков не достаточно для игры, то возвращаемся в ожидание
        if (this.arena.getAllPlayersType(PlayingPlayer.class).size() < this.arena.getMinPlayers()) {
            this.arena.sendMessages("Не достаточно игроков для старта!");
            this.arena.setGameState(new WaitingState(this.arena));
            return;
        }
        
        handleCount(this.arena);

    }
    
    
    @Override
    public int getCount() {
        return this.count;
    }
    
    @Override
    public void setCount(int count) {
        this.count = count;
    }
    
    private void handleCount(TntArena arena) {
        if (this.cd <= 0) {
            this.cd = 20;
            
            if (this.count <= 0) {
                arena.setGameState(new RunningState(arena, arena.getTimeLimit()));
                return;
            }
            
            arena.sendLevels(this.count);
            arena.sendMessages("Игра начнется через " + this.count);
            
            this.count--;
            
            return;
        }
        
        this.cd--;
    }

    
}
