package ru.boomearo.tntrun.objects.state;

import ru.boomearo.tntrun.objects.Arena;
import ru.boomearo.tntrun.objects.TntPlayer;

public class EndingState implements IGameState, ICountable {

    private int count = 10;
    
    @Override
    public void initState(Arena arena) {
        for (TntPlayer tp : arena.getAllPlayers()) {
            tp.getPlayerType().handleUpdate(tp);
        }
    }
    
    @Override
    public void autoUpdateHandler(Arena arena) {
        if (this.count <= 0) {
            arena.setGameState(new RegenState());
            return;
        }
        this.count--;
        arena.sendMessages("Игра закончилась! " + this.count);
    }

    @Override
    public void endState(Arena arena) {}
    
    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public void setCount(int count) {
        this.count = count;
    }


}
