package ru.boomearo.tntrun.objects.state;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import ru.boomearo.gamecontrol.objects.states.ICountable;
import ru.boomearo.gamecontrol.objects.states.IRunningState;
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
    
    public RunningState(TntArena arena, int count) {
        this.arena = arena;
        this.count = count;
    }
    
    @Override
    public String getName() {
        return "Идет игра";
    }

    @Override
    public TntArena getArena() {
        return this.arena;
    }
    
    @Override
    public void initState() {
        //Подготавливаем всех игроков (например тп на точку возрождения)
        for (TntPlayer tp : this.arena.getAllPlayers()) {
            tp.getPlayerType().preparePlayer(tp);
        }
        
        this.arena.sendMessages("Игра началась!");
    }
    
    @Override
    public void autoUpdateHandler() {
        //Играть одним низя
        if (this.arena.getAllPlayersType(PlayingPlayer.class).size() <= 1) {
            this.arena.sendMessages("Не достаточно игроков для игры!");
            this.arena.setGameState(new EndingState(this.arena));
            return;
        }
        
        for (TntPlayer tp : this.arena.getAllPlayers()) {
            tp.getPlayer().spigot().respawn();
            
            if (!this.arena.getArenaRegion().isInRegion(tp.getPlayer().getLocation())) {
                if (tp.getPlayerType() instanceof PlayingPlayer) {
                    PlayingPlayer pp = (PlayingPlayer) tp.getPlayerType();
                    tp.setPlayerType(new LosePlayer());
                    
                    this.deathPlayers++;
                    
                    //Добавляем единицу в статистику поражений
                    TntRunStatistics trs = TntRun.getInstance().getTntRunManager().getStatisticManager();
                    trs.addStats(TntStatsType.Defeat, tp.getName());
                    
                    if (pp.getKiller() != null) {
                        if (tp.getName().equals(pp.getKiller())) {
                            this.arena.sendMessages("Игрок " + tp.getName() + " проиграл, свалившись в свою же яму!");
                        }
                        else {
                            this.arena.sendMessages("Игрок " + tp.getName() + " проиграл, свалившись в яму игрока " + pp.getKiller());
                        }
                    }
                    else {
                        this.arena.sendMessages("Игрок " + tp.getName() + " проиграл, зайдя за границы игры.");
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
                            this.arena.sendMessages("Игрок " + winner.getName() + " победил!");
                            
                            //Добавляем единицу в статистику побед
                            trs.addStats(TntStatsType.Wins, winner.getName());
                            
                            //В зависимости от того сколько игроков ПРОИГРАЛО мы получим награду.
                            double reward = TntRunManager.winReward + (this.deathPlayers * TntRunManager.winReward);
                            
                            Vault.addMoney(winner.getName(), reward);
                            
                            winner.getPlayer().sendMessage("Ваша награда за победу: " + reward);
                            
                            this.arena.setGameState(new EndingState(this.arena));
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
                arena.sendMessages("Время вышло! Ничья!");
                arena.setGameState(new EndingState(this.arena));
                return;
            }
            
            arena.sendLevels(this.count);
            
            if (this.count <= 10) {
                arena.sendMessages("Игра закончится через " + this.count);
            }
            else {
                if ((this.count % 30) == 0){
                    arena.sendMessages("Игра закончится через " + this.count);
                }
            }
            
            this.count--;
            
            return;
            
        }
        this.cd--;
    }

    public BlockOwner getBlockByLocation(Location loc) {
        return this.removedBlocks.get(convertLocToString(loc));
    }
    
    public void addBlock(Block block, String owner) {
        this.removedBlocks.put(convertLocToString(block.getLocation()), new BlockOwner(block.getType(), owner));
    }
    
    public static String convertLocToString(Location loc) {
        return loc.getBlockX() + "|" + loc.getBlockY() + "|" +  loc.getBlockZ();
    }

    public static class BlockOwner {
        private final Material mat;
        private final String owner;
        
        public BlockOwner(Material mat, String owner) {
            this.mat = mat;
            this.owner = owner;
        }
        
        public Material getMaterial() {
            return this.mat;
        }
        
        public String getName() {
            return this.owner;
        }
    }
}
