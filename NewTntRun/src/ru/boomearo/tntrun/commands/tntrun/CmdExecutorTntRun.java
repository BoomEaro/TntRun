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
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
        if (arg3.length == 1) {
            List<String> ss = new ArrayList<String>(Arrays.asList("join", "leave", "list"));
            if (arg0.hasPermission("tntrun.admin")) {
                ss.add("createarena");
                ss.add("setspawnpoint");
            }
            List<String> matches = new ArrayList<>();
            String search = arg3[0].toLowerCase();
            for (String se : ss)
            {
                if (se.toLowerCase().startsWith(search))
                {
                    matches.add(se);
                }
            }
            return matches;
        }
        if (arg3.length == 2) {
            if (arg3[0].equalsIgnoreCase("join")) {
                List<String> ss = new ArrayList<String>();
                for (TntArena arena : TntRun.getInstance().getTntRunManager().getAllArenas()) {
                    ss.add(arena.getName());
                }
                List<String> matches = new ArrayList<>();
                String search = arg3[1].toLowerCase();
                for (String se : ss)
                {
                    if (se.toLowerCase().startsWith(search))
                    {
                        matches.add(se);
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
