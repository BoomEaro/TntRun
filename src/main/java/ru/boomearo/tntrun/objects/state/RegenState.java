package ru.boomearo.tntrun.objects.state;

import ru.boomearo.gamecontrol.GameControl;
import ru.boomearo.gamecontrol.exceptions.ConsoleGameException;
import ru.boomearo.gamecontrol.objects.states.AbstractRegenState;
import ru.boomearo.gamecontrol.runnable.RegenTask;
import ru.boomearo.tntrun.managers.TntRunManager;
import ru.boomearo.tntrun.objects.TntArena;
import ru.boomearo.tntrun.objects.TntPlayer;

public class RegenState extends AbstractRegenState implements SpectatorFirst {

    private final TntArena arena;

    public RegenState(TntArena arena) {
        this.arena = arena;
    }

    @Override
    public String getName() {
        return "§6Регенерация арены";
    }

    @Override
    public TntArena getArena() {
        return this.arena;
    }

    @Override
    public void initState() {
        this.arena.sendMessages(TntRunManager.prefix + "Начинаем регенерацию арены..");

        try {
            setWaitingRegen(true);
            GameControl.getInstance().getGameManager().queueRegenArena(new RegenTask(this.arena));
        }
        catch (ConsoleGameException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void autoUpdateHandler() {
        for (TntPlayer tp : this.arena.getAllPlayers()) {
            tp.getPlayer().spigot().respawn();

            if (!this.arena.getArenaRegion().isInRegionPoint(tp.getPlayer().getLocation())) {
                tp.getPlayerType().preparePlayer(tp);
            }
        }

        if (!isWaitingRegen()) {
            this.arena.setState(new WaitingState(this.arena));
        }
    }

}
