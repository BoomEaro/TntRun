package ru.boomearo.tntrun.objects;

import org.bukkit.entity.Player;

import ru.boomearo.gamecontrol.objects.IGamePlayer;
import ru.boomearo.tntrun.objects.playertype.IPlayerType;

public class TntPlayer implements IGamePlayer {

    private final String name;
    private final Player player;
    
    private IPlayerType playerType;
   
    private TntArena where;
    
    public TntPlayer(String name, Player player, IPlayerType playerType, TntArena where) {
        this.name = name;
        this.player = player;
        this.playerType = playerType;
        this.where = where;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public Player getPlayer() {
        return this.player;
    }
    
    @Override
    public TntArena getArena() {
        return this.where;
    }
    
    public IPlayerType getPlayerType() {
        return this.playerType;
    }
    
    public void setPlayerType(IPlayerType playerType) {
        this.playerType = playerType;
    }
    
    
}
