package ru.boomearo.tntrun.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import ru.boomearo.gamecontrol.objects.states.IGameState;
import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.objects.TntArena;
import ru.boomearo.tntrun.objects.state.RunningState;

public class ArenaListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntitySpawnEvent(EntitySpawnEvent e) {
        Location loc = e.getLocation();

        TntArena arena = TntRun.getInstance().getTntRunManager().getArenaByLocation(loc);
        if (arena != null) {

            IGameState state = arena.getState();
            if (state instanceof RunningState) {
                e.setCancelled(false);
                return;
            }

            e.setCancelled(true);
        }
    }

}
