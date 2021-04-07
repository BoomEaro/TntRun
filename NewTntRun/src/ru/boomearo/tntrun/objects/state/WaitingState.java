package ru.boomearo.tntrun.objects.state;

import ru.boomearo.gamecontrol.objects.states.IWaitingState;
import ru.boomearo.tntrun.managers.TntRunManager;
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
        return "§6Ожидание игроков";
    }
    
    @Override
    public TntArena getArena() {
        return this.arena;
    }
    
    @Override 
    public void initState() {
        this.arena.setForceStarted(false);
        
        this.arena.sendMessages(TntRunManager.prefix + "Ожидание игроков..");
        
        for (TntPlayer tp : this.arena.getAllPlayers()) {
            //Возвращаем умерших к жизни так сказать.
            if (tp.getPlayerType() instanceof LosePlayer) {
                tp.setPlayerType(new PlayingPlayer());
            }
            
            tp.getPlayerType().preparePlayer(tp);
            
            tp.sendBoard(0);
        }
    }
    
    @Override
    public void autoUpdateHandler() {
        //Если мы набрали минимум то меняем статус
        //Или если запущен принудительный старт
        if (this.arena.getAllPlayersType(PlayingPlayer.class).size() >= this.arena.getMinPlayers() || this.arena.isForceStarted()) {
            this.arena.setState(new StartingState(this.arena));
        }
        
        for (TntPlayer tp : this.arena.getAllPlayers()) {
            tp.getPlayer().spigot().respawn();
            
            if (!this.arena.getArenaRegion().isInRegionPoint(tp.getPlayer().getLocation())) {
                tp.getPlayerType().preparePlayer(tp);
            }
        }
    }


}
