package ru.boomearo.tntrun.objects.state;

import ru.boomearo.tntrun.objects.Arena;
import ru.boomearo.tntrun.objects.TntPlayer;
import ru.boomearo.tntrun.objects.TntPlayer.PlayingPlayer;
import ru.boomearo.tntrun.objects.TntPlayer.LosePlayer;

public class EndingState implements IGameState, ICountable {

    private int count = 10;
    
    private int cd = 20;
    
    @Override
    public void initState(Arena arena) {
        arena.sendMessages("Конец игры!");
        
        for (TntPlayer tp : arena.getAllPlayers()) {
            if (tp.getPlayerType() instanceof PlayingPlayer) {
                tp.setPlayerType(new LosePlayer());
            }
            tp.getPlayerType().preparePlayer(tp);
        }
    }
    
    @Override
    public void autoUpdateHandler(Arena arena) {
        for (TntPlayer tp : arena.getAllPlayers()) {
            if (!arena.getArenaRegion().isInRegion(tp.getPlayer().getLocation())) {
                tp.getPlayerType().preparePlayer(tp);
            }
        }
        
        handleCount(arena);
    }
    
    private void handleCount(Arena arena) {
        if (this.cd <= 0) {
            this.cd = 20;
            
            if (this.count <= 0) {
                arena.setGameState(new RegenState());
                return;
            }
            
            arena.sendMessages("Игра закончена! Следующая игра начнется через " + this.count);
            
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
