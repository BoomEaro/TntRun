package ru.boomearo.tntrun.runnable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.NumberConversions;

import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.objects.Arena;
import ru.boomearo.tntrun.objects.TntPlayer;
import ru.boomearo.tntrun.objects.TntPlayer.PlayingPlayer;
import ru.boomearo.tntrun.objects.state.IGameState;
import ru.boomearo.tntrun.objects.state.RunningState;

public class ArenasRunnable extends BukkitRunnable {
    
    private static final int SCAN_DEPTH = 2;  
    private static final double PLAYER_BOUNDINGBOX_ADD = 0.3;
    
    public ArenasRunnable() {
        runnable();
    }
    
    private void runnable() {
        this.runTaskTimer(TntRun.getInstance(), 1, 1);
    }
    
    @Override
    public void run() {
        for (Arena arena : TntRun.getInstance().getArenaManager().getAllArenas()) {
            
            IGameState state = arena.getGameState();
            
            state.autoUpdateHandler(arena);
            
            if (state instanceof RunningState) {
                RunningState rs = (RunningState) state;
                for (TntPlayer tp : arena.getAllPlayersType(PlayingPlayer.class)) {
                    
                    Player pl = tp.getPlayer();
                    //Если игрок внутри арены
                    if (arena.getArenaRegion().isInRegion(pl.getLocation())) {
                        destroyBlock(pl.getLocation(), arena, rs);
                    }
                }
            }
        }
    }

    private static void destroyBlock(Location loc, Arena arena, RunningState rs) {
        int y = loc.getBlockY() + 1;
        Block block = null;
        for (int i = 0; i <= SCAN_DEPTH; i++) {
            block = getBlockUnderPlayer(loc.getWorld(), loc.getX(), y, loc.getZ());
            y--;
            if (block != null) {
                break;
            }
        }

        if (block != null) {
            final Block fBlock = block;
            Material m = fBlock.getType();
            if (m == Material.SAND || m == Material.RED_SAND) {
                Material mat = rs.getBlockByLocation(fBlock.getLocation());
                if (mat == null) {
                    rs.addBlock(fBlock);
                    
                    Bukkit.getScheduler().runTaskLater(TntRun.getInstance(), () -> {
                        
                        if (arena.getGameState() instanceof RunningState) {
                            //blockstodestroy.remove(fblock);
                            removeGLBlocks(fBlock);
                        }
                    }, 8);
                }
            }
        }
    }
    
    private static void removeGLBlocks(Block block) {
        //block.getWorld().spawnParticle(Particle.BLOCK_DUST, block.getLocation(), 1, 1, 1, 1, 1, block.getBlockData());
        //block.setType(Material.AIR);
        block = block.getRelative(BlockFace.DOWN);
        block.setType(Material.AIR);
    }
    
    private static Block getBlockUnderPlayer(World world, double x, int y, double z) {
        Block b11 = getBlock(world, x, y, z, +PLAYER_BOUNDINGBOX_ADD, -PLAYER_BOUNDINGBOX_ADD);
        if (b11.getType() != Material.AIR) {
            return b11;
        }
        Block b12 = getBlock(world, x, y, z, -PLAYER_BOUNDINGBOX_ADD, +PLAYER_BOUNDINGBOX_ADD);
        if (b12.getType() != Material.AIR) {
            return b12;
        }
        Block b21 = getBlock(world, x, y, z, +PLAYER_BOUNDINGBOX_ADD, +PLAYER_BOUNDINGBOX_ADD);
        if (b21.getType() != Material.AIR) {
            return b21;
        }
        Block b22 = getBlock(world, x, y, z, -PLAYER_BOUNDINGBOX_ADD, -PLAYER_BOUNDINGBOX_ADD);
        if (b22.getType() != Material.AIR) {
            return b22;
        }
        return null;
    }
    
    private static Block getBlock(World world, double x, int y, double z, double addx, double addz) {
        return world.getBlockAt(NumberConversions.floor(x + addx), y, NumberConversions.floor(z + addz));
    }
}
