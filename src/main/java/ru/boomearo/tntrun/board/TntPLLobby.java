package ru.boomearo.tntrun.board;

import ru.boomearo.board.objects.PlayerBoard;
import ru.boomearo.board.objects.boards.AbstractHolder;
import ru.boomearo.board.objects.boards.AbstractPage;
import ru.boomearo.board.objects.boards.AbstractPageList;
import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.managers.TntRunManager;
import ru.boomearo.tntrun.objects.TntPlayer;
import ru.boomearo.tntrun.objects.TntStatsType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TntPLLobby extends AbstractPageList {

    private final TntPlayer tntPlayer;

    public TntPLLobby(PlayerBoard player, TntPlayer tntPlayer) {
        super(player);
        this.tntPlayer = tntPlayer;
    }

    @Override
    protected List<AbstractPage> createPages() {
        return List.of(new TntLobbyPage(this, this.tntPlayer));
    }

    public static class TntLobbyPage extends AbstractPage {

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
            List<AbstractHolder> holders = new ArrayList<>();

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
                    return TntRunManager.mainColor + "??????????: '" + TntRunManager.variableColor + tntPlayer.getArena().getName() + TntRunManager.mainColor + "'";
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
                    return TntRunManager.mainColor + "??????????????: " + TntRunManager.variableColor + tntPlayer.getArena().getAllPlayers().size() + "??8/" + TntRunManager.otherColor + tntPlayer.getArena().getMaxPlayers();
                }

                @Override
                public long getMaxCacheTime() {
                    return 0;
                }

            });

            holders.add(new AbstractHolder(this) {

                @Override
                protected String getText() {
                    return TntRunManager.mainColor + "????????????: " + tntPlayer.getArena().getState().getName();
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
                    return TntRunManager.mainColor + "????????????????????: ";
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
            double value = TntRun.getInstance().getTntRunManager().getStatisticManager().getStatsValueFromPlayer(type, name);
            return TntRunManager.mainColor + type.getName() + ": " + TntRunManager.variableColor + (long) value;
        }

    }


}
