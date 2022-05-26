package me.modify.townyboost.commands;

import com.modify.fundamentum.text.ColorUtil;
import me.modify.townyboost.menu.BoostMainMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Represents the /TownyBoost command and the actions performed upon running it in-game.
 */
public class TownyBoostCommand extends BukkitCommand {
    public TownyBoostCommand() {
        super("townyboost");
        setAliases(Arrays.asList("tboost", "townyb", "boosters", "boost", "booster"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {

        // Check that the command sender is a player (not console sender).
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to execute this command.");
            return true;
        }

        Player player = (Player) sender;
        int length = args.length;

        BoostMainMenu boostMainMenu = new BoostMainMenu(player);
        boostMainMenu.open();
        return false;
    }
}
