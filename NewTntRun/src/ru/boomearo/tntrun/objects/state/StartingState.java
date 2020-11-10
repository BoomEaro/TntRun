package ru.boomearo.tntrun.objects.state;

import org.bukkit.entity.Player;

import ru.boomearo.tntrun.objects.Arena;
import ru.boomearo.tntrun.objects.TntPlayer;
import ru.boomearo.tntrun.objects.TntPlayer.PlayingPlayer;

public class StartingState implements IGameState, ICountable, AllowPlayers, AllowSpectators {

    private int count = 10;
    
    @Override
    public void initState(Arena arena) {
        arena.sendMessages("Набралось достаточно игроков, начинаем игру!");
    }
    
    @Override
    public void autoUpdateHandler(Arena arena) {
        for (TntPlayer tp : arena.getAllPlayers()) {
            Player pl = tp.getPlayer();
            if (!arena.getArenaStructure().getArenaRegion().isInRegion(pl.getLocation())) {
                tp.getPlayerType().handleUpdate(tp);
            }
        }
        //Если игроков не достаточно для игры, то возвращаемся в ожидание
        if (arena.getAllPlayersType(PlayingPlayer.class).size() < arena.getMinPlayers()) {
            arena.setGameState(new WaitingState());
            arena.sendMessages("Не достаточно игроков для старта!");
            return;
        }
        
        if (this.count <= 0) {
            arena.setGameState(new RunningState(arena.getTimeLimit()));
            return;
        }
        this.count--;
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
