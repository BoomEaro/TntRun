package ru.boomearo.tntrun.board.pages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
            public String getText() {
                return TntRunManager.mainColor + new SimpleDateFormat("dd/MM/yyyy").format(new Date(System.currentTimeMillis()));
            }

        });
        
        holders.add(new AbstractHolder(this) {

            @Override
            public String getText() {
                return " ";
            }

        });
        
        holders.add(new AbstractHolder(this) {

            @Override
            protected String getText() {
                return TntRunManager.mainColor + "Карта: '" + TntRunManager.variableColor + tntPlayer.getArena().getName() + TntRunManager.mainColor + "'";
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
                return TntRunManager.mainColor + "Игроков: " + TntRunManager.variableColor + tntPlayer.getArena().getAllPlayers().size() + TntRunManager.mainColor + "/" + TntRunManager.otherColor + tntPlayer.getArena().getMaxPlayers();
            }
            
            @Override
            public long getMaxCacheTime() {
                return 0;
            }
            
        });
        
        holders.add(new AbstractHolder(this) {

            @Override
            protected String getText() {
                return TntRunManager.mainColor + "Статус: " + tntPlayer.getArena().getState().getName();
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
                return TntRunManager.mainColor + "Статистика: ";
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
            return TntRunManager.mainColor + type.getName() + ": " + TntRunManager.variableColor + "0";
        }
        
        return TntRunManager.mainColor + type.getName() + ": " + TntRunManager.variableColor + (long) sp.getValue();
    }
    
}
