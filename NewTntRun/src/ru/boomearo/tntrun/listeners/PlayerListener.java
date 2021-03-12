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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.managers.TntRunManager;
import ru.boomearo.tntrun.objects.TntPlayer;
import ru.boomearo.tntrun.objects.playertype.LosePlayer;

public class PlayerListener implements Listener {
    
    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        Player pl = e.getEntity();
        
        TntPlayer tp = TntRun.getInstance().getTntRunManager().getGamePlayer(pl.getName());
        if (tp != null) {
            LosePlayer lp = new LosePlayer();
            tp.setPlayerType(lp);
            
            e.setDroppedExp(0);
            e.getDrops().clear();
        }
    }
    
    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent e) {
        Player pl = e.getPlayer();
        
        TntPlayer tp = TntRun.getInstance().getTntRunManager().getGamePlayer(pl.getName());
        if (tp != null) {
            e.setRespawnLocation(tp.getArena().getRandomSpawnLocation());
            tp.getPlayerType().preparePlayer(tp);
        }
    }
    
    @EventHandler
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {
        Player pl = e.getPlayer();
        
        String msg = e.getMessage();
        if (msg.equalsIgnoreCase("/tntrun leave") || msg.equalsIgnoreCase("/tr leave")) {
            return;
        }
        
        TntPlayer tp = TntRun.getInstance().getTntRunManager().getGamePlayer(pl.getName());
        if (tp != null) {
            e.setCancelled(true);
            pl.sendMessage(TntRunManager.prefix + "Вы не можете использовать эти команды в игре!");
        }
    }
    
    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) {
        Entity en = e.getEntity();
        if (en instanceof Player) {
            Player pl = (Player) en;
            
            TntPlayer tp = TntRun.getInstance().getTntRunManager().getGamePlayer(pl.getName());
            if (tp != null) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e) {
        Player pl = e.getPlayer();
        TntPlayer tp = TntRun.getInstance().getTntRunManager().getGamePlayer(pl.getName());
        if (tp != null) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        Player pl = e.getPlayer();
        TntPlayer tp = TntRun.getInstance().getTntRunManager().getGamePlayer(pl.getName());
        if (tp != null) {
            e.setCancelled(true);
        }
    }
    
    
    @EventHandler
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent e) {
        Entity en = e.getEntity();
        if (en instanceof Player) {
            Player pl = (Player) en;
            TntPlayer tp = TntRun.getInstance().getTntRunManager().getGamePlayer(pl.getName());
            if (tp != null) {
                e.setCancelled(true);
            }
        }
    }
}
