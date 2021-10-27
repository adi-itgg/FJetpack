package me.phantomxcraft;

import me.phantomxcraft.jetpack.Jetpack;
import me.phantomxcraft.kode.JetpackManager;
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

import static me.phantomxcraft.utils.Fungsi.STRING_EMPTY;

public class TapTab implements TabCompleter {
    public static final List<String> ListCommand = new ArrayList<>(Arrays.asList("Get", "Give", "Reload", "CheckUpdate", "Set"));

    public List<String> onTabComplete(CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp() || sender.hasPermission("fjetpack.admin") || sender.hasPermission("fjetpack." + args[0])) {
            List<String> completions = new ArrayList<>();
            if (args.length == 1) {
                String partialCommand = args[0];
                StringUtil.copyPartialMatches(partialCommand, ListCommand, completions);
            }

            if (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("give")) {
                if (args.length == 2) {
                    String partialPlayer = args[1];

                    StringBuilder sB = new StringBuilder();
                    for(Player player : Bukkit.getOnlinePlayers()){
                        sB.append(player.getName()).append(", ");
                        String displayName = player.getDisplayName();
                        if (!displayName.equalsIgnoreCase(player.getName()) && displayName.length() < 1)
                            sB.append(displayName).append(", ");
                    }
                    completions = JetpackList(completions, partialPlayer, sB);
                }

                if (args.length == 3) {

                    Jetpack jetpack = JetpackManager.jetpacksLoaded.get(args[1]);

                    if (jetpack == null) {
                        String partialJp = args[2];
                        StringBuilder sB = new StringBuilder();
                        completions = JetpackList(completions, partialJp, sB);
                    }
                }
            }

            if (args[0].equalsIgnoreCase("set") && args.length == 2) {
                StringBuilder sB = new StringBuilder();
                completions = JetpackList(completions, args[1], sB);
            }

            Collections.sort(completions);
            return completions;
        } else {
            return null;
        }
    }

    public static List<String> JetpackList(List<String> completions, String partialPlayer, StringBuilder sB) {
        for (String jetpackName : JetpackManager.jetpacksLoaded.keySet())
            sB.append(sB.length() < 1 ? STRING_EMPTY : ", ").append(jetpackName);


        List<String> players = new ArrayList<>(Arrays.asList(sB.toString().split(", ")));
        return StringUtil.copyPartialMatches(partialPlayer, players, completions);
    }
}
