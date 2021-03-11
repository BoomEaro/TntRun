package ru.boomearo.tntrun.objects.state;

import ru.boomearo.gamecontrol.objects.states.IWaitingState;
import ru.boomearo.tntrun.objects.TntArena;
import ru.boomearo.tntrun.objects.TntPlayer;
import ru.boomearo.tntrun.objects.playertype.LosePlayer;
import ru.boomearo.tntrun.objects.playertype.PlayingPlayer;

public class WaitingState implements IWaitingState {

    private final TntArena arena;
    
    public WaitingState(TntArena arena) {
        this.arena = arena;
    }
    
    @Override
    public String getName() {
        return "Ожидание игроков";
    }
    
    @Override
    public TntArena getArena() {
        return this.arena;
    }
    
    @Override 
    public void initState() {
        this.arena.sendMessages("Ожидание игроков..");
        
        for (TntPlayer tp : this.arena.getAllPlayers()) {
            //Возвращаем умерших к жизни так сказать.
            if (tp.getPlayerType() instanceof LosePlayer) {
                tp.setPlayerType(new PlayingPlayer());
            }
            
            tp.getPlayerType().preparePlayer(tp);
        }
    }
    
    @Override
    public void autoUpdateHandler() {
        for (TntPlayer tp : this.arena.getAllPlayers()) {
            if (!this.arena.getArenaRegion().isInRegion(tp.getPlayer().getLocation())) {
                tp.getPlayerType().preparePlayer(tp);
            }
        }
        
        //Если мы набрали минимум то меняем статус
        if (this.arena.getAllPlayersType(PlayingPlayer.class).size() >= this.arena.getMinPlayers()) {
            this.arena.setGameState(new StartingState(this.arena));
        }
    }


}
