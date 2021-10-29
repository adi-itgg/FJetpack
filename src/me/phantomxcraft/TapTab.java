package me.phantomxcraft;

import me.phantomxcraft.jetpack.Jetpack;
import me.phantomxcraft.kode.JetpackManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.libs.org.eclipse.sisu.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static me.phantomxcraft.utils.Fungsi.PERM_STRING;

public class TapTab implements TabCompleter {
    public static final List<String> ListCommand = new ArrayList<>(Arrays.asList("Set", "Get", "Give", "Reload", "CheckUpdate"));

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

            if (args.length == 3) {
                Jetpack jetpack = JetpackManager.jetpacksLoaded.get(args[1]);
                if (jetpack == null)
                    completions = JetpackList(completions, args[2], null);
            }
        }

        if (args[0].equalsIgnoreCase(ListCommand.get(0)) && args.length == 2)
            completions = JetpackList(completions, args[1], null);

        Collections.sort(completions);
        return completions;
    }

    private @NotNull List<String> JetpackList(@NotNull List<String> completions, @NotNull String token, @Nullable List<String> cmdSuggestions) {
        if (cmdSuggestions == null) cmdSuggestions = new ArrayList<>();
        cmdSuggestions.addAll(JetpackManager.jetpacksLoaded.keySet());
        return StringUtil.copyPartialMatches(token, cmdSuggestions, completions);
    }
}
