package me.modify.townyboost.commands;

import com.modify.fundamentum.text.ColorUtil;
import me.modify.townyboost.TownyBoost;
import me.modify.townyboost.data.ConfigHelper;
import me.modify.townyboost.data.UserDataHelper;
import me.modify.townyboost.events.BoostActivateEvent;
import me.modify.townyboost.objects.Boost;
import me.modify.townyboost.objects.BoostToken;
import me.modify.townyboost.objects.BoostType;
import me.modify.townyboost.queue.BoosterManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Represents the /TownyBoostAdmin command and the actions performed upon running it in-game.
 */
public class TownyBoostAdminCommand extends BukkitCommand {

    public TownyBoostAdminCommand() {
        super("townyboostadmin");
        setAliases(Arrays.asList("tboostadmin", "tba", "tbadmin", "boostadmin", "boosteradmin"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {

        int length = args.length;

        /*
          Check that the length of the command matches a valid syntax length.
          Syntax length of 3 suggests use of the "give" command.
          Syntax length of 4 suggests use of the "queue" command.
         */
        if (length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("townyboost.reload")) {
                    sender.sendMessage(ColorUtil.format("&4&l(!) &r&cInsufficient permissions."));
                    return true;
                }

                TownyBoost.getInstance().getDataManager().reloadConfigurations();
                sender.sendMessage(ColorUtil.format("&2&l(✓) &aConfiguration files reloaded."));
            } else {
                sendSyntaxMessage(sender);
            }
        } else if (length == 3) {

            if (args[0].equalsIgnoreCase("queue")) {

                if (args[1].equalsIgnoreCase("clear")) {
                    if (!sender.hasPermission("townyboost.queue.clear")) {
                        sender.sendMessage(ColorUtil.format("&4&l(!) &r&cInsufficient permissions."));
                        return true;
                    }

                    if (!args[2].equalsIgnoreCase("mcmmo") && !args[2].equalsIgnoreCase("jobs")) {
                        sender.sendMessage(ColorUtil.format("&4&l(!) &r&cBooster type " + args[2] + " to clear not found. Valid booster types: jobs/mcmmo"));
                        return true;
                    }

                    BoostType type = BoostType.valueOf(args[2].toUpperCase());

                    // // Clears and returns true if any boosters were present in this queue.
                    boolean wasCleared = TownyBoost.getInstance().getBoosterManager().clearQueue(type);

                    if (wasCleared) {
                        sender.sendMessage(ColorUtil.format("&2&l(✓) &aAll queued " + type + " boosters have been cleared."));
                    } else {
                        sender.sendMessage(ColorUtil.format("&4&l(!) &r&cNo boosters in this queue to clear."));
                    }
                    return true;
                }

                if (args[1].equalsIgnoreCase("clear-active")) {
                    if (!sender.hasPermission("townyboost.queue.clear-active")) {
                        sender.sendMessage(ColorUtil.format("&4&l(!) &r&cInsufficient permissions."));
                        return true;
                    }

                    if (!args[2].equalsIgnoreCase("mcmmo") && !args[2].equalsIgnoreCase("jobs")) {
                        sender.sendMessage(ColorUtil.format("&4&l(!) &r&cBooster type " + args[2] + " to clear not found. Valid booster types: jobs/mcmmo"));
                        return true;
                    }

                    BoostType type = BoostType.valueOf(args[2].toUpperCase());

                    // Clears and returns true if an active booster was present in this queue.
                    boolean wasCleared = TownyBoost.getInstance().getBoosterManager().clearActive(type);

                    if (wasCleared) {
                        sender.sendMessage(ColorUtil.format("&2&l(✓) &aActive " + type.name() + " booster has been cleared."));
                    } else {
                        sender.sendMessage(ColorUtil.format("&4&l(!) &r&cNo active booster to clear."));
                    }
                    return true;
                }

                if (!sender.hasPermission("townyboost.queue")) {
                    sender.sendMessage(ColorUtil.format("&4&l(!) &r&cInsufficient permissions."));
                    return true;
                }

                // Check that the command sender is a player (not console sender).
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ColorUtil.format("You must be a player to execute this command."));
                    return true;
                }

                Player player = (Player) sender;
                UUID playerId = player.getUniqueId();

                // Check that the boost type entered is valid.
                if (!args[1].equalsIgnoreCase("mcmmo") && !args[1].equalsIgnoreCase("jobs")) {
                    player.sendMessage(ColorUtil.format("&4&l(!) &r&cBooster type " + args[1] + " not found. Valid booster types: jobs/mcmmo"));
                    return true;
                }

                BoostType type = BoostType.valueOf(args[1].toUpperCase());

                // Check that the boost multiplier entered is an integer.
                int multiplier = -1;
                try {
                    multiplier = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ColorUtil.format("&4&l(!) &r&cMultiplier must be an integer."));
                    return true;
                }

                UUID boostId = UUID.randomUUID();
                Boost boost = new Boost(boostId, playerId, multiplier, ConfigHelper.getBoostDuration());

                BoosterManager manager = TownyBoost.getInstance().getBoosterManager();
                if (type == BoostType.MCMMO) {
                    if (manager.getMcmmoBoostQueue().isAtCapacity()) {
                        player.sendMessage(ColorUtil.format("&4&l(!) &r&cMCMMO booster queue is currently at capacity. Try again later."));
                        return true;
                    }

                    if (manager.getMcmmoBoostQueue().isBoosterActive()) {
                        player.sendMessage(ColorUtil.format("&2&l(✓) &aBooster successfully added to the MCMMO queue."));
                    } else {
                        Bukkit.getPluginManager().callEvent(new BoostActivateEvent(boost, type));
                    }

                    manager.getMcmmoBoostQueue().add(boost);
                } else if (type == BoostType.JOBS) {
                    if (manager.getJobsBoostQueue().isAtCapacity()) {
                        player.sendMessage(ColorUtil.format("&4&l(!) &r&cJobs booster queue is currently at capacity. Try again later."));
                        return true;
                    }

                    if (manager.getJobsBoostQueue().isBoosterActive()) {
                        player.sendMessage(ColorUtil.format("&2&l(✓) &aBooster successfully added to the Jobs queue."));
                    } else {
                        Bukkit.getPluginManager().callEvent(new BoostActivateEvent(boost, type));
                    }

                    manager.getJobsBoostQueue().add(boost);
                }
            } else {
                sendSyntaxMessage(sender);
            }

        } else if (length == 4 || length == 5) {

            if (args[0].equalsIgnoreCase("give")) {

                if (!sender.hasPermission("townyboost.give")) {
                    sender.sendMessage(ColorUtil.format("&4&l(!) &r&cInsufficient permissions."));
                    return true;
                }

                UUID targetId = UserDataHelper.getPlayerId(args[1]);

                // If the target id is null, it means that a user with the entered ign has not been found.
                if (targetId == null) {
                    sender.sendMessage(ColorUtil.format("&4&l(!) &r&cPlayer '" + args[1] + "' not found. "));
                    return true;
                }

                // Check that the boost type entered is valid.
                if (!args[2].equalsIgnoreCase("mcmmo") && !args[2].equalsIgnoreCase("jobs")) {
                    sender.sendMessage(ColorUtil.format("&4&l(!) &r&cBooster type not found. Valid booster types: jobs/mcmmo"));
                    return true;
                }

                BoostType type = BoostType.valueOf(args[2].toUpperCase());

                // Check that the boost multiplier entered is an integer.
                int multiplier = -1;
                try {
                    multiplier = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ColorUtil.format("&4&l(!) &r&cMultiplier must be an integer."));
                    return true;
                }

                if (length == 5) {
                    int quantity = 1;
                    try {
                        quantity = Integer.parseInt(args[4]);
                    } catch(NumberFormatException e) {
                        sender.sendMessage(ColorUtil.format("&4&l(!) &r&cQuantity must be an integer."));
                        return true;
                    }

                    List<BoostToken> tokenList = new ArrayList<>();
                    for (int i = 0; i < quantity; i++) {
                        UUID boostId = UUID.randomUUID();
                        BoostToken token = new BoostToken(boostId, type, multiplier);
                        tokenList.add(token);
                    }
                    sender.sendMessage(ColorUtil.format("&2&l(✓) &aBooster tokens (quantity " + quantity + ") successfully given to the target player."));
                    UserDataHelper.asyncAddBoosterTokenMultiple(targetId, tokenList);
                    return true;
                }

                UUID boostId = UUID.randomUUID();
                BoostToken token = new BoostToken(boostId, type, multiplier);

                sender.sendMessage(ColorUtil.format("&2&l(✓) &aBooster token successfully given to the target player."));
                // Adds a virtual booster token to the user's data file.
                UserDataHelper.asyncAddBoosterToken(targetId, token);
            } else {
                sendSyntaxMessage(sender);
                return true;
            }
        } else {
            sendSyntaxMessage(sender);
            return true;
        }

        return false;
    }

    private void sendSyntaxMessage(CommandSender sender) {
        sender.sendMessage(ColorUtil.format("&4&l(!) Invalid usage. Valid syntax:"));
        sender.sendMessage(ColorUtil.format("&c/townyboostadmin give <player> <type> <multiplier>"));
        sender.sendMessage(ColorUtil.format("&c/townyboostadmin give <player> <type> <multiplier> <quantity>"));
        sender.sendMessage(ColorUtil.format("&c/townyboostadmin queue <type> <multiplier>"));
        sender.sendMessage(ColorUtil.format("&c/townyboostadmin queue clear <type>"));
        sender.sendMessage(ColorUtil.format("&c/townyboostadmin queue clear-active <type>"));
        sender.sendMessage(ColorUtil.format("&c/townyboostadmin reload"));
    }
}

