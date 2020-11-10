package ru.boomearo.tntrun.runnable;

import java.util.concurrent.TimeUnit;

import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.objects.Arena;

public class ArenaRunnable extends AbstractTimer {

    public ArenaRunnable() {
        super("ArenaRunnable", TimeUnit.SECONDS, 1);
    }

    @Override
    public void task() throws Throwable {
        update();
    }

    public static void update() {
        try {
            for (Arena arena : TntRun.getInstance().getArenaManager().getAllArenas()) {
                arena.getGameState().autoUpdateHandler(arena);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}
