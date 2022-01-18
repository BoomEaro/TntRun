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
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

import ru.boomearo.gamecontrol.GameControl;
import ru.boomearo.gamecontrol.exceptions.ConsoleGameException;
import ru.boomearo.gamecontrol.objects.states.ICountable;
import ru.boomearo.gamecontrol.objects.states.IRunningState;
import ru.boomearo.gamecontrol.utils.Vault;
import ru.boomearo.serverutils.utils.other.DateUtil;
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

    private final Map<String, BlockOwner> removedBlocks = new HashMap<>();

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
            this.arena.sendMessages(TntRunManager.prefix + "Не достаточно игроков для игры! " + TntRunManager.variableColor + "Игра прервана.");
            this.arena.setState(new EndingState(this.arena));
            return;
        }

        for (TntPlayer tp : this.arena.getAllPlayers()) {
            tp.getPlayer().spigot().respawn();

            if (!this.arena.getArenaRegion().isInRegionPoint(tp.getPlayer().getLocation())) {
                handleDeath(tp);
                continue;
            }

            if (tp.getPlayerType() instanceof PlayingPlayer) {
                PlayingPlayer pp = (PlayingPlayer) tp.getPlayerType();

                //Убиваем игрока если он оказался в лаве или воде
                Material curr = tp.getPlayer().getLocation().getBlock().getType();
                if (curr == Material.LAVA || curr == Material.WATER) {
                    handleDeath(tp);
                    continue;
                }

                BlockOwner bo = this.removedBlocks.get(convertLocToString(tp.getPlayer().getLocation()));
                if (bo != null) {
                    pp.setKiller(bo.getName());
                }

                Player pl = tp.getPlayer();

                destroyBlock(pl.getLocation(), this.arena, tp, this);
            }
        }


        handleCount(this.arena);
    }

    private void handleDeath(TntPlayer tp) {
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
                    this.arena.sendMessages(TntRunManager.prefix + tp.getPlayer().getDisplayName() + TntRunManager.mainColor + " проиграл, свалившись в свою же яму! " + TntRunManager.getRemainPlayersArena(this.arena, PlayingPlayer.class));
                }
                else {
                    this.arena.sendMessages(TntRunManager.prefix + tp.getPlayer().getDisplayName() + TntRunManager.mainColor + " проиграл, свалившись в яму игрока " + killer.getPlayer().getDisplayName() + " " + TntRunManager.getRemainPlayersArena(this.arena, PlayingPlayer.class));
                }
            }
            else {
                this.arena.sendMessages(TntRunManager.prefix + tp.getPlayer().getDisplayName() + TntRunManager.mainColor + " проиграл, зайдя за границы игры. " + TntRunManager.getRemainPlayersArena(this.arena, PlayingPlayer.class));
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

                    this.arena.sendTitle("", "§c" + winner.getPlayer().getDisplayName() + " §7победил!", 20, 20 * 15, 20);

                    this.arena.sendMessages(TntRunManager.prefix + winner.getPlayer().getDisplayName() + TntRunManager.mainColor + " победил!");

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
                arena.sendMessages(TntRunManager.prefix + "Время вышло! " + TntRunManager.variableColor + "Ничья!");
                arena.setState(new EndingState(this.arena));
                return;
            }

            arena.sendLevels(this.count);

            if (this.count <= 10) {
                arena.sendMessages(TntRunManager.prefix + "Игра закончится через " + TntRunManager.variableColor + DateUtil.formatedTime(this.count, false));
            }
            else {
                if ((this.count % 30) == 0) {
                    arena.sendMessages(TntRunManager.prefix + "Игра закончится через " + TntRunManager.variableColor + DateUtil.formatedTime(this.count, false));
                }
            }

            this.count--;

            return;

        }
        this.cd--;
    }


    private static void destroyBlock(Location loc, TntArena arena, TntPlayer owner, RunningState rs) {
        int y = loc.getBlockY() + 1;
        Block mBlock = null;
        for (int i = 0; i <= SCAN_DEPTH; i++) {
            mBlock = getBlockUnderPlayer(loc.getWorld(), loc.getX(), y, loc.getZ());
            y--;
            if (mBlock != null) {
                break;
            }
        }

        //Ищем основной блок "песка"
        if (mBlock == null) {
            return;
        }

        //Получаем блок под пском
        Block tntBlock = mBlock.getRelative(BlockFace.DOWN);

        //Если под этим блоком нет тнт то игнорим.
        if (tntBlock.getType() != Material.TNT) {
            return;
        }

        //Если основной блок сломан то ничего не делаем
        BlockOwner bo = rs.getBlockByLocation(mBlock.getLocation());
        if (bo != null) {
            return;
        }

        //Добавляем основной блок
        rs.addBlock(mBlock, owner);

        final Block mmBlock = mBlock;

        //Выполняем задачу через 8 тиков для разрушения блока 
        Bukkit.getScheduler().runTaskLater(TntRun.getInstance(), () -> {

            //Разрушаем только если игра действительно идет
            if (arena.getState() instanceof RunningState) {
                //blockstodestroy.remove(fblock);
                removeGLBlocks(mmBlock, tntBlock);
            }
        }, 8);
    }

    private static void removeGLBlocks(Block mBlock, Block tntBlock) {
        BlockData data = mBlock.getBlockData();

        mBlock.setType(Material.AIR);
        tntBlock.setType(Material.AIR);

        FallingBlock fb = mBlock.getWorld().spawnFallingBlock(mBlock.getLocation().clone().add(0.5d, 0, 0.5d), data);
        fb.setDropItem(false);
        fb.setHurtEntities(false);
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

    public BlockOwner getBlockByLocation(Location loc) {
        return this.removedBlocks.get(convertLocToString(loc));
    }

    public void addBlock(Block block, TntPlayer owner) {
        this.removedBlocks.put(convertLocToString(block.getLocation()), new BlockOwner(block.getType(), owner));
    }

    public static String convertLocToString(Location loc) {
        return loc.getBlockX() + "|" + loc.getBlockY() + "|" + loc.getBlockZ();
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
}
