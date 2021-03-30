package ru.boomearo.tntrun.objects;

import org.bukkit.entity.Player;

import ru.boomearo.board.Board;
import ru.boomearo.board.exceptions.BoardException;
import ru.boomearo.board.objects.PlayerBoard;
import ru.boomearo.board.objects.boards.AbstractPageList;
import ru.boomearo.gamecontrol.objects.IGamePlayer;
import ru.boomearo.tntrun.board.TntPageList;
import ru.boomearo.tntrun.objects.playertype.IPlayerType;

public class TntPlayer implements IGamePlayer {

    private final String name;
    private final Player player;
    
    private IPlayerType playerType;
   
    private TntArena where;
    private TntTeam team;
    
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
    
    public void sendBoard(Integer index) {
        PlayerBoard pb = Board.getInstance().getBoardManager().getPlayerBoard(this.name);
        if (pb != null) {
            try {
                AbstractPageList apl;
                if (index == null) {
                    apl = Board.getInstance().getBoardManager().getPageListFactory().createPageList(pb);
                }
                else {
                    apl = new TntPageList(pb, this);
                }
                
                pb.setNewPageList(apl);
                
                if (index != null) {
                    pb.toPage(index, pb.getPageByIndex(index));
                }
            } 
            catch (BoardException e) {
                e.printStackTrace();
            }
        }
    }
    
}
