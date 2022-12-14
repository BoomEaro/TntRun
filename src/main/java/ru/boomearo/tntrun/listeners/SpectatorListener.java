package ru.boomearo.tntrun.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import ru.boomearo.gamecontrol.GameControl;
import ru.boomearo.gamecontrol.exceptions.GameControlException;
import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.objects.TntPlayer;

public class SpectatorListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleportEvent(PlayerTeleportEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (e.getCause() == TeleportCause.SPECTATE) {
            Player pl = e.getPlayer();
            TntPlayer tp = TntRun.getInstance().getTntRunManager().getGamePlayer(pl.getName());
            if (tp != null) {

                try {
                    GameControl.getInstance().getGameManager().leaveGame(pl);
                }
                catch (GameControlException ignored) {
                }

                e.setCancelled(true);
            }
        }

    }

}
