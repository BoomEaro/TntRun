package ru.boomearo.tntrun.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.objects.TntArena;

public class ArenaListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent e) {
        if (e.getEntity() instanceof FallingBlock) {
            if (e.getTo() == Material.SAND) {
                Location loc = e.getBlock().getLocation();
                
                for (TntArena ar : TntRun.getInstance().getTntRunManager().getAllArenas()) {
                    if (ar.getArenaRegion().isInRegion(loc)) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
    
}
