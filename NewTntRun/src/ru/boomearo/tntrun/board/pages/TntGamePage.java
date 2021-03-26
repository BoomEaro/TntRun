package ru.boomearo.tntrun.board.pages;

import java.util.ArrayList;
import java.util.List;

import ru.boomearo.board.objects.boards.AbstractHolder;
import ru.boomearo.board.objects.boards.AbstractPage;
import ru.boomearo.board.objects.boards.AbstractPageList;
import ru.boomearo.gamecontrol.objects.states.IGameState;
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
                return "§7Арена: '§c" + tntPlayer.getArena().getName() + "§7'";
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
                return "§7Статус: " + tntPlayer.getArena().getState().getName();
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
                    
                    return "§7До конца: §c" + getFormattedTimeLeft(rs.getCount());
                }
                else if (state instanceof EndingState) {
                    EndingState es = (EndingState) state;
                    return "§7Новая игра: §c" + getFormattedTimeLeft(es.getCount());
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
                return "§7Наблюдателей: §c" + tntPlayer.getArena().getAllPlayersType(LosePlayer.class).size();
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
                return "§7Игроков: §c" + tntPlayer.getArena().getAllPlayersType(PlayingPlayer.class).size();
            }

        });
        
        return holders;
    }
    
    private static String getFormattedTimeLeft(int time) {
        int min = 0;
        int sec = 0;
        String minStr = "";
        String secStr = "";

        min = (int) Math.floor(time / 60);
        sec = time % 60;

        minStr = (min < 10) ? "0" + String.valueOf(min) : String.valueOf(min);
        secStr = (sec < 10) ? "0" + String.valueOf(sec) : String.valueOf(sec);

        return minStr + ":" + secStr;
      }

}
