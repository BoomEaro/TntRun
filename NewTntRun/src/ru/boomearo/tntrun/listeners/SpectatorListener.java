package ru.boomearo.tntrun.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;

import ru.boomearo.gamecontrol.GameControl;
import ru.boomearo.gamecontrol.exceptions.GameControlException;
import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.objects.TntPlayer;

public class SpectatorListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleportEvent(PlayerTeleportEvent e) {
        if (e.getCause() == TeleportCause.SPECTATE) {
            Player pl = e.getPlayer();
            TntPlayer tp = TntRun.getInstance().getTntRunManager().getGamePlayer(pl.getName());
            if (tp != null) {
                
                try {
                    GameControl.getInstance().getGameManager().leaveGame(pl);
                }
                catch (GameControlException e1) {}
                
                e.setCancelled(true);
            }
        }
        
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerStartSpectatingEntityEvent(PlayerStartSpectatingEntityEvent e) {
        Player pl = e.getPlayer();
        
        TntPlayer tp = TntRun.getInstance().getTntRunManager().getGamePlayer(pl.getName());
        if (tp != null) {
            e.setCancelled(true);
        }
        
    }
    
}
