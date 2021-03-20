package ru.boomearo.tntrun.listeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.StructureGrowEvent;

import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.objects.TntArena;

public class ArenaListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (e.getEntity() instanceof FallingBlock) {
            if (e.getTo() == Material.SAND) {
                Location loc = e.getBlock().getLocation();
                
                TntArena arena = TntRun.getInstance().getTntRunManager().getArenaByLocation(loc);
                if (arena != null) {
                    e.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent e) {
        if (e.isCancelled()) {
            return;
        }
        
        Location loc = e.getLocation();
        
        TntArena arena = TntRun.getInstance().getTntRunManager().getArenaByLocation(loc);
        if (arena != null) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockGrowEvent(BlockGrowEvent e) {
        if (e.isCancelled()) {
            return;
        }
        
        Block b = e.getBlock();
        if (b == null) {
            return;
        }
        
        Location loc = b.getLocation();
        
        TntArena arena = TntRun.getInstance().getTntRunManager().getArenaByLocation(loc);
        if (arena != null) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockBurnEvent(BlockBurnEvent e) {
        if (e.isCancelled()) {
            return;
        }
        
        Block b = e.getBlock();
        if (b == null) {
            return;
        }
        
        Location loc = b.getLocation();
        
        TntArena arena = TntRun.getInstance().getTntRunManager().getArenaByLocation(loc);
        if (arena != null) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockFadeEvent(BlockFadeEvent e) {
        if (e.isCancelled()) {
            return;
        }
        
        Block b = e.getBlock();
        if (b == null) {
            return;
        }
        
        Location loc = b.getLocation();
        
        TntArena arena = TntRun.getInstance().getTntRunManager().getArenaByLocation(loc);
        if (arena != null) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockFormEvent(BlockFormEvent e) {
        if (e.isCancelled()) {
            return;
        }
        
        Block b = e.getBlock();
        if (b == null) {
            return;
        }
        
        Location loc = b.getLocation();
        
        TntArena arena = TntRun.getInstance().getTntRunManager().getArenaByLocation(loc);
        if (arena != null) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockIgniteEvent(BlockIgniteEvent e) {
        if (e.isCancelled()) {
            return;
        }
        
        Block b = e.getBlock();
        if (b == null) {
            return;
        }
        
        Location loc = b.getLocation();
        
        TntArena arena = TntRun.getInstance().getTntRunManager().getArenaByLocation(loc);
        if (arena != null) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockSpreadEvent(BlockSpreadEvent e) {
        if (e.isCancelled()) {
            return;
        }
        
        Block b = e.getBlock();
        if (b == null) {
            return;
        }
        
        Location loc = b.getLocation();
        
        TntArena arena = TntRun.getInstance().getTntRunManager().getArenaByLocation(loc);
        if (arena != null) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onStructureGrowEvent(StructureGrowEvent e) {
        if (e.isCancelled()) {
            return;
        }
        
        Location loc = e.getLocation();
        
        TntArena arena = TntRun.getInstance().getTntRunManager().getArenaByLocation(loc);
        if (arena != null) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onEntityExplodeEvent(EntityExplodeEvent e) {
        if (e.isCancelled()) {
            return;
        }

        handleExplode(e.blockList(), e);
    }

    @EventHandler
    public void onBlockExplodeEvent(BlockExplodeEvent e) {
        if (e.isCancelled()) {
            return;
        }

        handleExplode(e.blockList(), e);
    }
    
    private static void handleExplode(List<Block> bs, Cancellable e) {

        for (Block b : bs) {
            TntArena arena = TntRun.getInstance().getTntRunManager().getArenaByLocation(b.getLocation());
            if (arena != null) {
                e.setCancelled(true);
                return;
            }
        }

    }
}