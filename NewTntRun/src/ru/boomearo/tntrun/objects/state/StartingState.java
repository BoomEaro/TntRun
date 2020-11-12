package ru.boomearo.tntrun.objects.state;

import ru.boomearo.tntrun.objects.Arena;
import ru.boomearo.tntrun.objects.TntPlayer;
import ru.boomearo.tntrun.objects.TntPlayer.PlayingPlayer;

public class StartingState implements IGameState, ICountable, AllowPlayers, AllowSpectators {

    private int count = 30;
    
    private int cd = 20;
    
    @Override
    public void initState(Arena arena) {
        arena.sendMessages("Набралось достаточно игроков, начинаем игру!");
    }
    
    @Override
    public void autoUpdateHandler(Arena arena) {
        for (TntPlayer tp : arena.getAllPlayers()) {
            if (!arena.getArenaRegion().isInRegion(tp.getPlayer().getLocation())) {
                tp.getPlayerType().preparePlayer(tp);
            }
        }
        //Если игроков не достаточно для игры, то возвращаемся в ожидание
        if (arena.getAllPlayersType(PlayingPlayer.class).size() < arena.getMinPlayers()) {
            arena.sendMessages("Не достаточно игроков для старта!");
            arena.setGameState(new WaitingState());
            return;
        }
        
        handleCount(arena);

    }
    
    private void handleCount(Arena arena) {
        if (this.cd <= 0) {
            this.cd = 20;
            
            if (this.count <= 0) {
                arena.setGameState(new RunningState(arena.getTimeLimit()));
                return;
            }
            
            arena.sendLevels(this.count);
            arena.sendMessages("Игра начнется через " + this.count);
            
            this.count--;
            
            return;
        }
        
        this.cd--;
    }

    
    @Override
    public int getCount() {
        return this.count;
    }
    
    @Override
    public void setCount(int count) {
        this.count = count;
    }
}
