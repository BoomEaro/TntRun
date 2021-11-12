package ru.boomearo.tntrun.commands.tntrun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.commands.AbstractExecutor;
import ru.boomearo.tntrun.commands.CmdList;
import ru.boomearo.tntrun.managers.TntRunManager;
import ru.boomearo.tntrun.objects.TntArena;

public class CmdExecutorTntRun extends AbstractExecutor {

    public CmdExecutorTntRun() {
        super(new TntRunUse());
    }

    @Override
    public boolean zeroArgument(CommandSender sender, CmdList cmds) {
        cmds.sendUsageCmds(sender);
        return true;
    }

    private static final List<String> empty = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> ss = new ArrayList<>(Arrays.asList("join", "leave", "list"));
            if (sender.hasPermission("tntrun.admin")) {
                ss.add("createarena");
                ss.add("setspawnpoint");
            }
            List<String> matches = new ArrayList<>();
            String search = args[0].toLowerCase();
            for (String se : ss) {
                if (se.toLowerCase().startsWith(search)) {
                    matches.add(se);
                }
            }
            return matches;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("join")) {
                List<String> matches = new ArrayList<>();
                String search = args[1].toLowerCase();
                for (TntArena arena : TntRun.getInstance().getTntRunManager().getAllArenas()) {
                    if (arena.getName().toLowerCase().startsWith(search)) {
                        matches.add(arena.getName());
                    }
                }
                return matches;
            }
        }
        return empty;
    }

    @Override
    public String getPrefix() {
        return TntRunManager.prefix;
    }

    @Override
    public String getSuffix() {
        return " §8-" + TntRunManager.variableColor + " ";
    }
}
