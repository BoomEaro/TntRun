package ru.boomearo.tntrun.board.pages;

import java.util.ArrayList;
import java.util.List;

import ru.boomearo.board.objects.boards.AbstractHolder;
import ru.boomearo.board.objects.boards.AbstractPage;
import ru.boomearo.board.objects.boards.AbstractPageList;
import ru.boomearo.gamecontrol.objects.statistics.StatsPlayer;
import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.managers.TntRunManager;
import ru.boomearo.tntrun.objects.TntPlayer;
import ru.boomearo.tntrun.objects.statistics.TntStatsData;
import ru.boomearo.tntrun.objects.statistics.TntStatsType;

public class TntLobbyPage extends AbstractPage {

    private final TntPlayer tntPlayer;
    
    public TntLobbyPage(AbstractPageList pageList, TntPlayer tntPlayer) {
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
                return "§6Игроков: §c" + tntPlayer.getArena().getAllPlayers().size() + "§7/§c" + tntPlayer.getArena().getMaxPlayers();
            }
            
            @Override
            public long getMaxCacheTime() {
                return 0;
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
                return " ";
            }
            
        });
        
        holders.add(new AbstractHolder(this) {

            @Override
            protected String getText() {
                return "§6Статистика: ";
            }
            
        });
        
        holders.add(new AbstractHolder(this) {

            @Override
            protected String getText() {
                return getStatisticData(TntStatsType.Wins, tntPlayer.getName());
            }
            
            
        });
        
        holders.add(new AbstractHolder(this) {

            @Override
            protected String getText() {
                return getStatisticData(TntStatsType.Defeat, tntPlayer.getName());
            }
            
            
        });
        
        
        return holders;
    }

    private static String getStatisticData(TntStatsType type, String name) {
        TntStatsData data = TntRun.getInstance().getTntRunManager().getStatisticManager().getStatsData(type);
        StatsPlayer sp = data.getStatsPlayer(name);
        if (sp == null) {
            return "§6" + type.getName() + ": §c0";
        }
        
        return "§6" + type.getName() + ": §c" + (long) sp.getValue();
    }
    
}
