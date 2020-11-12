package ru.boomearo.tntrun.objects.state;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import ru.boomearo.tntrun.objects.Arena;
import ru.boomearo.tntrun.objects.TntPlayer;
import ru.boomearo.tntrun.objects.TntPlayer.IPlayerType;
import ru.boomearo.tntrun.objects.TntPlayer.PlayingPlayer;
import ru.boomearo.tntrun.objects.TntPlayer.LosePlayer;

public class RunningState implements IGameState, ICountable, AllowSpectators {

    private int count;

    private int cd = 20;
    
    private final Map<String, Material> removedBlocks = new HashMap<String, Material>();
    
    public RunningState(int count) {
        this.count = count;
    }
    
    @Override
    public void initState(Arena arena) {
        //Подготавливаем всех игроков (например тп на точку возрождения)
        for (TntPlayer tp : arena.getAllPlayers()) {
            tp.getPlayerType().preparePlayer(tp);
        }
        
        arena.sendMessages("Игра началась!");
    }
    
    @Override
    public void autoUpdateHandler(Arena arena) {
        for (TntPlayer tp : arena.getAllPlayers()) {
            if (!arena.getArenaRegion().isInRegion(tp.getPlayer().getLocation())) {
                IPlayerType type = tp.getPlayerType();
                if (type instanceof PlayingPlayer) {
                    tp.setPlayerType(new LosePlayer());
                    
                    arena.sendMessages("Игрок " + tp.getName() + " проиграл!");
                    
                    Collection<TntPlayer> win = arena.getAllPlayersType(PlayingPlayer.class);
                    if (win.size() == 1) {
                        TntPlayer winner = null;
                        for (TntPlayer w : win) {
                            winner = w;
                            break;
                        }
                        if (winner != null) {
                            winner.setPlayerType(new LosePlayer());
                            arena.sendMessages("Игрок " + winner.getName() + " победил!");
                            arena.setGameState(new EndingState());
                            return;
                        }
                    }
                }
                
                type.preparePlayer(tp);
            }
        }
        
        //Играть одним низя
        if (arena.getAllPlayersType(PlayingPlayer.class).size() <= 1) {
            arena.sendMessages("Не достаточно игроков для игры!");
            arena.setGameState(new EndingState());
            return;
        }
        
        handleCount(arena);
    }
    
    private void handleCount(Arena arena) {
        if (this.cd <= 0) {
            this.cd = 20;
            
            if (this.count <= 0) {
                arena.sendMessages("Время вышло! Ничья!");
                arena.setGameState(new EndingState());
                return;
            }
            
            arena.sendLevels(this.count);
            arena.sendMessages("Игра закончится через " + this.count);
            
            this.count--;
            
            return;
            
        }
        this.cd--;
    }
    

    @Override
    public int getCount() {
        return this.count;
    }
    
    @Override
    public void setCount(int count) {
        this.count = count;
    }

    public Material getBlockByLocation(Location loc) {
        return this.removedBlocks.get(convertLocToString(loc));
    }
    
    public void addBlock(Block block) {
        this.removedBlocks.put(convertLocToString(block.getLocation()), block.getType());
    }
    
    public static String convertLocToString(Location loc) {
        return loc.getBlockX() + "|" + loc.getBlockY() + "|" +  loc.getBlockZ();
    }

}
