package ru.boomearo.tntrun.objects.state;

import ru.boomearo.gamecontrol.objects.states.ICountable;
import ru.boomearo.gamecontrol.objects.states.IGameState;
import ru.boomearo.gamecontrol.utils.DateUtil;
import ru.boomearo.tntrun.managers.TntRunManager;
import ru.boomearo.tntrun.objects.TntArena;
import ru.boomearo.tntrun.objects.TntPlayer;
import ru.boomearo.tntrun.objects.playertype.LosePlayer;
import ru.boomearo.tntrun.objects.playertype.PlayingPlayer;

public class EndingState implements IGameState, ICountable {

    private final TntArena arena;
    
    private int count = 5;
    
    private int cd = 20;
    
    public EndingState(TntArena arena) {
        this.arena = arena;
    }
    
    @Override
    public String getName() {
        return "§cКонец игры";
    }
    
    @Override
    public TntArena getArena() {
        return this.arena;
    }
    
    @Override
    public void initState() {
        this.arena.sendMessages(TntRunManager.prefix + "Игра закончена!");
        
        for (TntPlayer tp : this.arena.getAllPlayers()) {
            if (tp.getPlayerType() instanceof PlayingPlayer) {
                tp.setPlayerType(new LosePlayer());
            }
            tp.getPlayerType().preparePlayer(tp);
        }
    }
    
    @Override
    public void autoUpdateHandler() {
        for (TntPlayer tp : this.arena.getAllPlayers()) {
            tp.getPlayer().spigot().respawn();
            
            if (!this.arena.getArenaRegion().isInRegion(tp.getPlayer().getLocation())) {
                tp.getPlayerType().preparePlayer(tp);
            }
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
                arena.setState(new RegenState(arena));
                return;
            }
            
            arena.sendMessages(TntRunManager.prefix + "Следующая игра начнется через §c" + DateUtil.formatedTime(this.count, false));
            
            this.count--;
            
            return;
        }
        this.cd--;
    }


}
