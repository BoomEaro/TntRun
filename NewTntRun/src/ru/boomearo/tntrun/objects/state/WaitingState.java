package ru.boomearo.tntrun.objects.state;

import org.bukkit.entity.Player;

import ru.boomearo.tntrun.objects.Arena;
import ru.boomearo.tntrun.objects.TntPlayer;
import ru.boomearo.tntrun.objects.TntPlayer.IPlayerType;
import ru.boomearo.tntrun.objects.TntPlayer.LosePlayer;
import ru.boomearo.tntrun.objects.TntPlayer.PlayingPlayer;

public class WaitingState implements IGameState, AllowPlayers, AllowSpectators {

    @Override 
    public void initState(Arena arena) {
        for (TntPlayer tp : arena.getAllPlayers()) {
            IPlayerType type = tp.getPlayerType();
            //Возвращаем умерших к жизни так сказать.
            if (type instanceof LosePlayer) {
                tp.setPlayerType(new PlayingPlayer());
            }
            
            type.handleUpdate(tp);
        }
    }
    
    @Override
    public void autoUpdateHandler(Arena arena) {
        for (TntPlayer tp : arena.getAllPlayers()) {
            Player pl = tp.getPlayer();
            if (!arena.getArenaStructure().getArenaRegion().isInRegion(pl.getLocation())) {
                tp.getPlayerType().handleUpdate(tp);
            }
        }
        
        //Если мы набрали минимум то меняем статус
        if (arena.getAllPlayersType(PlayingPlayer.class).size() >= arena.getMinPlayers()) {
            arena.setGameState(new StartingState());
        }
    }

    @Override
    public void endState(Arena arena) {}

}
