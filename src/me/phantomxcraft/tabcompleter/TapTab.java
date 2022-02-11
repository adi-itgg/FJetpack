package me.phantomxcraft.tabcompleter;

import me.phantomxcraft.config.JetpackManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static me.phantomxcraft.utils.Fungsi.*;

public class TapTab implements TabCompleter {
    public static final List<String> ListCommand = new ArrayList<>(Arrays.asList("Set", "Get", "Give", "Reload", "CheckUpdate", "SetFuel", "GetFuel", "GiveFuel"));

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp() || !sender.hasPermission(PERM_STRING + "admin") || !sender.hasPermission(PERM_STRING + args[0]))
            return null;

        List<String> completions = new ArrayList<>();
        if (args.length == 1)
            StringUtil.copyPartialMatches(args[0], ListCommand, completions);

        if (args[0].equalsIgnoreCase(ListCommand.get(1)) || args[0].equalsIgnoreCase(ListCommand.get(2))) {
            if (args.length == 2) {
                List<String> cmds = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    cmds.add(player.getName());
                    String displayName = player.getDisplayName();
                    if (!displayName.equalsIgnoreCase(player.getName()) && displayName.length() < 1)
                        cmds.add(displayName);
                }
                completions = JetpackList(completions, args[1], cmds);
            }

            if (args.length == 3 && JetpackManager.jetpacksLoaded.get(args[1]) == null)
                completions = JetpackList(completions, args[2], null);
        }

        if (args[0].equalsIgnoreCase(ListCommand.get(0)) && args.length == 2)
            completions = JetpackList(completions, args[1], null);

        if (args[0].equalsIgnoreCase(ListCommand.get(6)) || args[0].equalsIgnoreCase(ListCommand.get(7))) {
            if (args.length == 2) {
                List<String> cmds = new ArrayList<>(JetpackManager.customFuelsLoaded.keySet());
                for (Player player : Bukkit.getOnlinePlayers()) {
                    cmds.add(player.getName());
                    String displayName = player.getDisplayName();
                    if (!displayName.equalsIgnoreCase(player.getName()) && displayName.length() < 1)
                        cmds.add(displayName);
                }
                StringUtil.copyPartialMatches(args[1], cmds, completions);
            }

            if (args.length == 3 && JetpackManager.customFuelsLoaded.get(args[1]) == null)
                StringUtil.copyPartialMatches(args[2], new ArrayList<>(JetpackManager.customFuelsLoaded.keySet()), completions);

        }

        Collections.sort(completions);

        if ((args[0].equalsIgnoreCase(ListCommand.get(0)) && args.length == 3) ||
                ((((args.length == 3 && JetpackManager.jetpacksLoaded.get(args[1]) != null ) || (args.length == 4 && JetpackManager.jetpacksLoaded.get(args[2]) != null)) && (args[0].equalsIgnoreCase(ListCommand.get(1)) || args[0].equalsIgnoreCase(ListCommand.get(2))) ||
                        ((args[0].equalsIgnoreCase(ListCommand.get(6)) || args[0].equalsIgnoreCase(ListCommand.get(7))) &&
                                (((args.length == 3 || args.length == 4) && JetpackManager.customFuelsLoaded.get(args[args.length == 4 ? 2 : 1]) != null)))))) {
            List<String> defFuel = new ArrayList<>();
            defFuel.add("32");
            defFuel.add("64");
            defFuel.add("128");
            defFuel.add("256");
            StringUtil.copyPartialMatches(args[args.length == 4 ? 3 : 2], defFuel, completions);
        }

        return completions;
    }

    private @NotNull List<String> JetpackList(@NotNull List<String> completions, @NotNull String token, List<String> cmdSuggestions) {
        if (cmdSuggestions == null) cmdSuggestions = new ArrayList<>();
        cmdSuggestions.addAll(JetpackManager.jetpacksLoaded.keySet());
        return StringUtil.copyPartialMatches(token, cmdSuggestions, completions);
    }
}
