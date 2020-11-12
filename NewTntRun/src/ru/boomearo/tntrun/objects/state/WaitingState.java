package ru.boomearo.tntrun.objects.state;

import ru.boomearo.tntrun.objects.Arena;
import ru.boomearo.tntrun.objects.TntPlayer;
import ru.boomearo.tntrun.objects.TntPlayer.LosePlayer;
import ru.boomearo.tntrun.objects.TntPlayer.PlayingPlayer;

public class WaitingState implements IGameState, AllowPlayers, AllowSpectators {

    @Override 
    public void initState(Arena arena) {
        arena.sendMessages("Ожидание игроков..");
        
        for (TntPlayer tp : arena.getAllPlayers()) {
            //Возвращаем умерших к жизни так сказать.
            if (tp.getPlayerType() instanceof LosePlayer) {
                tp.setPlayerType(new PlayingPlayer());
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
        
        //Если мы набрали минимум то меняем статус
        if (arena.getAllPlayersType(PlayingPlayer.class).size() >= arena.getMinPlayers()) {
            arena.setGameState(new StartingState());
        }
    }

}
