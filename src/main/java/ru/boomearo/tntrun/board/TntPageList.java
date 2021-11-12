package ru.boomearo.tntrun.board;

import java.util.ArrayList;
import java.util.List;

import ru.boomearo.board.objects.PlayerBoard;
import ru.boomearo.board.objects.boards.AbstractPage;
import ru.boomearo.board.objects.boards.AbstractPageList;
import ru.boomearo.tntrun.board.pages.TntGamePage;
import ru.boomearo.tntrun.board.pages.TntLobbyPage;
import ru.boomearo.tntrun.objects.TntPlayer;

public class TntPageList extends AbstractPageList {

    private final TntPlayer tntPlayer;
    
    public TntPageList(PlayerBoard player, TntPlayer tntPlayer) {
        super(player);
        this.tntPlayer = tntPlayer;
    }

    @Override
    protected List<AbstractPage> createPages() {
        List<AbstractPage> pages = new ArrayList<>();
        
        pages.add(new TntLobbyPage(this, this.tntPlayer));
        pages.add(new TntGamePage(this, this.tntPlayer));
        
        return pages;
    }

}
