package ru.boomearo.tntrun.objects;

import org.bukkit.entity.Player;

import ru.boomearo.gamecontrol.objects.IGamePlayer;
import ru.boomearo.tntrun.objects.playertype.IPlayerType;

public class TntPlayer implements IGamePlayer {

    private final String name;
    private final Player player;

    private IPlayerType playerType;

    private final TntArena where;
    private final TntTeam team;

    public TntPlayer(String name, Player player, IPlayerType playerType, TntArena where, TntTeam team) {
        this.name = name;
        this.player = player;
        this.playerType = playerType;
        this.where = where;
        this.team = team;
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

    public TntTeam getTeam() {
        return this.team;
    }

    public IPlayerType getPlayerType() {
        return this.playerType;
    }

    public void setPlayerType(IPlayerType playerType) {
        this.playerType = playerType;
    }

}
