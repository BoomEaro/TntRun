package ru.boomearo.tntrun.board.pages;

import java.util.ArrayList;
import java.util.List;

import ru.boomearo.board.objects.boards.AbstractHolder;
import ru.boomearo.board.objects.boards.AbstractPage;
import ru.boomearo.board.objects.boards.AbstractPageList;
import ru.boomearo.gamecontrol.objects.states.IGameState;
import ru.boomearo.gamecontrol.utils.DateUtil;
import ru.boomearo.tntrun.managers.TntRunManager;
import ru.boomearo.tntrun.objects.TntPlayer;
import ru.boomearo.tntrun.objects.playertype.LosePlayer;
import ru.boomearo.tntrun.objects.playertype.PlayingPlayer;
import ru.boomearo.tntrun.objects.state.EndingState;
import ru.boomearo.tntrun.objects.state.RunningState;

public class TntGamePage extends AbstractPage {

    private final TntPlayer tntPlayer;
    
    public TntGamePage(AbstractPageList pageList, TntPlayer tntPlayer) {
        super(pageList);
        this.tntPlayer = tntPlayer;
    }

    @Override
    public int getTimeToChange() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public String getTitle() {
        return TntRunManager.gameNameDys;
    }

    @Override
    protected List<AbstractHolder> createHolders() {
        List<AbstractHolder> holders = new ArrayList<AbstractHolder>();

        holders.add(new AbstractHolder(this) {

            @Override
            protected String getText() {
                return "§6Арена: '§c" + tntPlayer.getArena().getName() + "§6'";
            }
            
        });
        
        holders.add(new AbstractHolder(this) {

            @Override
            protected String getText() {
                return " ";
            }

        });

        holders.add(new AbstractHolder(this) {

            @Override
            protected String getText() {
                return "§6Статус: " + tntPlayer.getArena().getState().getName();
            }
            
            @Override
            public long getMaxCacheTime() {
                return 0;
            }
            
        });
        
        holders.add(new AbstractHolder(this) {

            @Override
            protected String getText() {
                IGameState state = tntPlayer.getArena().getState();
                if (state instanceof RunningState) {
                    RunningState rs = (RunningState) state;
                    
                    return "§6Игра закончится через: §c" + DateUtil.formatedTime(rs.getCount(), false, true);
                }
                else if (state instanceof EndingState) {
                    EndingState es = (EndingState) state;
                    return "§6Новая игра через: §c" + DateUtil.formatedTime(es.getCount(), false, true);
                }
                return " ";
            }
            
            @Override
            public long getMaxCacheTime() {
                return 0;
            }

        });
        
        holders.add(new AbstractHolder(this) {

            @Override
            protected String getText() {
                return "§6Наблюдателей: §c" + tntPlayer.getArena().getAllPlayersType(LosePlayer.class).size();
            }

        });
        
        holders.add(new AbstractHolder(this) {

            @Override
            protected String getText() {
                return " ";
            }

        });
        
        
        holders.add(new AbstractHolder(this) {

            @Override
            protected String getText() {
                return "§6Игроков: §c" + tntPlayer.getArena().getAllPlayersType(PlayingPlayer.class).size();
            }

        });
        
        return holders;
    }

}
