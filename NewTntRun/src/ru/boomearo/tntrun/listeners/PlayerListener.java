package ru.boomearo.tntrun.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.exceptions.TntRunException;
import ru.boomearo.tntrun.objects.TntPlayer;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        Player pl = e.getPlayer();
        
        try {
            TntRun.getInstance().getArenaManager().leaveArena(pl);
        } 
        catch (TntRunException e1) {}
    }
    
    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        Player pl = e.getEntity();
        
        try {
            TntRun.getInstance().getArenaManager().leaveArena(pl);
        } 
        catch (TntRunException e1) {}
    }
    
    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) {
        Entity en = e.getEntity();
        if (en instanceof Player) {
            Player pl = (Player) en;
            
            TntPlayer tp = TntRun.getInstance().getArenaManager().getPlayerByName(pl.getName());
            if (tp != null) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e) {
        Player pl = e.getPlayer();
        TntPlayer tp = TntRun.getInstance().getArenaManager().getPlayerByName(pl.getName());
        if (tp != null) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        Player pl = e.getPlayer();
        TntPlayer tp = TntRun.getInstance().getArenaManager().getPlayerByName(pl.getName());
        if (tp != null) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        Player pl = e.getPlayer();
        TntPlayer tp = TntRun.getInstance().getArenaManager().getPlayerByName(pl.getName());
        if (tp != null) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent e) {
        Entity en = e.getEntity();
        if (en instanceof Player) {
            Player pl = (Player) en;
            TntPlayer tp = TntRun.getInstance().getArenaManager().getPlayerByName(pl.getName());
            if (tp != null) {
                e.setCancelled(true);
            }
        }
    }
}
