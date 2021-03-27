package ru.boomearo.tntrun.objects.state;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

import ru.boomearo.gamecontrol.GameControl;
import ru.boomearo.gamecontrol.exceptions.ConsoleGameException;
import ru.boomearo.gamecontrol.objects.states.ICountable;
import ru.boomearo.gamecontrol.objects.states.IRunningState;
import ru.boomearo.gamecontrol.utils.DateUtil;
import ru.boomearo.gamecontrol.utils.Vault;
import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.managers.TntRunManager;
import ru.boomearo.tntrun.managers.TntRunStatistics;
import ru.boomearo.tntrun.objects.TntArena;
import ru.boomearo.tntrun.objects.TntPlayer;
import ru.boomearo.tntrun.objects.playertype.LosePlayer;
import ru.boomearo.tntrun.objects.playertype.PlayingPlayer;
import ru.boomearo.tntrun.objects.statistics.TntStatsType;

public class RunningState implements IRunningState, ICountable, SpectatorFirst {

    private final TntArena arena;
    private int count;
    private int deathPlayers = 0;

    private int cd = 20;
    
    private final Map<String, BlockOwner> removedBlocks = new HashMap<String, BlockOwner>();
    
    private static final int SCAN_DEPTH = 3;  
    private static final double PLAYER_BOUNDINGBOX_ADD = 0.3;
    
    public RunningState(TntArena arena, int count) {
        this.arena = arena;
        this.count = count;
    }
    
    @Override
    public String getName() {
        return "§aИдет игра";
    }

    @Override
    public TntArena getArena() {
        return this.arena;
    }
    
    @Override
    public void initState() {
        try {
            GameControl.getInstance().getGameManager().setRegenGame(this.arena, true);
        } 
        catch (ConsoleGameException e) {
            e.printStackTrace();
        }
        
        //Подготавливаем всех игроков (например тп на точку возрождения)
        for (TntPlayer tp : this.arena.getAllPlayers()) {
            tp.getPlayerType().preparePlayer(tp);
            
            tp.sendBoard(1);
        }
        
        this.arena.sendMessages(TntRunManager.prefix + "Игра началась. Удачи!");
        this.arena.sendSounds(Sound.BLOCK_NOTE_BLOCK_PLING, 999, 2);
    }
    
    @Override
    public void autoUpdateHandler() {
        //Играть одним низя
        if (this.arena.getAllPlayersType(PlayingPlayer.class).size() <= 1) {
            this.arena.sendMessages(TntRunManager.prefix + "Не достаточно игроков для игры! §cИгра прервана.");
            this.arena.setState(new EndingState(this.arena));
            return;
        }
        
        for (TntPlayer tp : this.arena.getAllPlayers()) {
            tp.getPlayer().spigot().respawn();
            
            if (!this.arena.getArenaRegion().isInRegionPoint(tp.getPlayer().getLocation())) {
                if (tp.getPlayerType() instanceof PlayingPlayer) {
                    PlayingPlayer pp = (PlayingPlayer) tp.getPlayerType();
                    tp.setPlayerType(new LosePlayer());
                    
                    this.deathPlayers++;
                    
                    //Добавляем единицу в статистику поражений
                    TntRunStatistics trs = TntRun.getInstance().getTntRunManager().getStatisticManager();
                    trs.addStats(TntStatsType.Defeat, tp.getName());
                    
                    this.arena.sendSounds(Sound.ENTITY_WITHER_HURT, 999, 2);
                    
                    TntPlayer killer = pp.getKiller();
                    
                    if (killer != null) {
                        if (tp.getName().equals(killer.getName())) {
                            this.arena.sendMessages(TntRunManager.prefix + "§c" + tp.getPlayer().getDisplayName() + " §6проиграл, свалившись в свою же яму! " + TntRunManager.getRemainPlayersArena(this.arena, PlayingPlayer.class));
                        }
                        else {
                            this.arena.sendMessages(TntRunManager.prefix + "§c" + tp.getPlayer().getDisplayName() + " §6проиграл, свалившись в яму игрока §c" + killer.getPlayer().getDisplayName() + " " + TntRunManager.getRemainPlayersArena(this.arena, PlayingPlayer.class));
                        }
                    }
                    else {
                        this.arena.sendMessages(TntRunManager.prefix + "§c" + tp.getPlayer().getDisplayName() + " §6проиграл, зайдя за границы игры. " + TntRunManager.getRemainPlayersArena(this.arena, PlayingPlayer.class));
                    }
                    
                    Collection<TntPlayer> win = this.arena.getAllPlayersType(PlayingPlayer.class);
                    if (win.size() == 1) {
                        TntPlayer winner = null;
                        for (TntPlayer w : win) {
                            winner = w;
                            break;
                        }
                        if (winner != null) {
                            winner.setPlayerType(new LosePlayer());
                            
                            this.arena.sendTitle("", "§c" + winner.getPlayer().getDisplayName() + " §6победил!", 20, 20*15, 20);
                            
                            this.arena.sendMessages(TntRunManager.prefix + "§c" + winner.getPlayer().getDisplayName() + " §6победил!");
                            
                            this.arena.sendSounds(Sound.ENTITY_PLAYER_LEVELUP, 999, 2);
                            
                            //Добавляем единицу в статистику побед
                            trs.addStats(TntStatsType.Wins, winner.getName());
                            
                            //В зависимости от того сколько игроков ПРОИГРАЛО мы получим награду.
                            double reward = TntRunManager.winReward + (this.deathPlayers * TntRunManager.winReward);
                            
                            Vault.addMoney(winner.getName(), reward);
                            
                            winner.getPlayer().sendMessage(TntRunManager.prefix + "Ваша награда за победу: " + GameControl.getFormatedEco(reward));
                            
                            this.arena.setState(new EndingState(this.arena));
                            return;
                        }
                    }
                }
                
                tp.getPlayerType().preparePlayer(tp);
            }
            

            if (tp.getPlayerType() instanceof PlayingPlayer) {
                PlayingPlayer pp = (PlayingPlayer) tp.getPlayerType();
                
                BlockOwner bo = this.removedBlocks.get(convertLocToString(tp.getPlayer().getLocation()));
                if (bo != null) {
                    pp.setKiller(bo.getName());
                }
                
                handleDestroy(tp);
            }
        }
        
        
        handleCount(this.arena);
    }
    
    @Override
    public int getCount() {
        return this.count;
    }
    
    @Override
    public void setCount(int count) {
        this.count = count;
    }
    
    private void handleCount(TntArena arena) {
        if (this.cd <= 0) {
            this.cd = 20;
            
            if (this.count <= 0) {
                arena.sendMessages(TntRunManager.prefix + "Время вышло! §cНичья!");
                arena.setState(new EndingState(this.arena));
                return;
            }
            
            arena.sendLevels(this.count);
            
            if (this.count <= 10) {
                arena.sendMessages(TntRunManager.prefix + "Игра закончится через §c" + DateUtil.formatedTime(this.count, false));
            }
            else {
                if ((this.count % 30) == 0){
                    arena.sendMessages(TntRunManager.prefix + "Игра закончится через §c" + DateUtil.formatedTime(this.count, false));
                }
            }
            
            this.count--;
            
            return;
            
        }
        this.cd--;
    }
    
    //TODO сделать разрушение блоков как изначально задумано а не как костылем
    private void handleDestroy(TntPlayer tp) {
        Player pl = tp.getPlayer();
        //Если игрок внутри арены
        //if (this.arena.getArenaRegion().isInRegionPoint(pl.getLocation())) {
        //    destroyBlock(pl.getLocation(), this.arena, tp, this);
        //}
        
        destroyBlock(pl.getLocation(), this.arena, tp, this);
    }

    public BlockOwner getBlockByLocation(Location loc) {
        return this.removedBlocks.get(convertLocToString(loc));
    }
    
    public void addBlock(Block block, TntPlayer owner) {
        this.removedBlocks.put(convertLocToString(block.getLocation()), new BlockOwner(block.getType(), owner));
    }
    
    public static String convertLocToString(Location loc) {
        return loc.getBlockX() + "|" + loc.getBlockY() + "|" +  loc.getBlockZ();
    }

    public static class BlockOwner {
        private final Material mat;
        private final TntPlayer owner;
        
        public BlockOwner(Material mat, TntPlayer owner) {
            this.mat = mat;
            this.owner = owner;
        }
        
        public Material getMaterial() {
            return this.mat;
        }
        
        public TntPlayer getName() {
            return this.owner;
        }
    }
    
    private static void destroyBlock(Location loc, TntArena arena, TntPlayer owner, RunningState rs) {
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
                BlockOwner bo = rs.getBlockByLocation(fBlock.getLocation());
                if (bo == null) {
                    rs.addBlock(fBlock, owner);
                    
                    Bukkit.getScheduler().runTaskLater(TntRun.getInstance(), () -> {
                        
                        if (arena.getState() instanceof RunningState) {
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
        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_SAND_BREAK, 1, 1);
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
