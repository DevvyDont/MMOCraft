package me.devvy.mmocraft.admin;

import me.devvy.mmocraft.MMOCraft;
import me.devvy.mmocraft.entity.EntityManager;
import me.devvy.mmocraft.entity.MMOEntity;
import me.devvy.mmocraft.player.MMOPlayer;
import me.devvy.mmocraft.stats.BaseStatistic;
import me.devvy.mmocraft.stats.StatisticPool;
import me.devvy.mmocraft.stats.StatisticType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatSetter implements CommandExecutor, Listener {

    // These are pools of stats that admins can modify and apply to who they want whenever
    private Map<UUID, StatisticPool> poolsInProgress = new HashMap<>();
    // These are the people who have punch to apply stats mode on
    private Map<UUID, Boolean> punchToApplyPlayers = new HashMap<>();
    // These are the people who want to sneak punch to copy stats
    private Map<UUID, Boolean> sneakPunchToCopy = new HashMap<>();

    public StatSetter() {
        MMOCraft.getInstance().getServer().getPluginManager().registerEvents(this, MMOCraft.getInstance());
    }

    private void printStatPool(Player player) {
        if (poolsInProgress.containsKey(player.getUniqueId())) {
            player.sendMessage("<STAT>: BASE (+BONUS) = TOTAL");
            player.sendMessage("---------------");
            for (BaseStatistic stat : poolsInProgress.get(player.getUniqueId()).getAllStatistics())
                player.sendMessage(String.format("%s: %s (+%s) = %s", stat.getType(), stat.getBaseValue(), stat.getBonusValue(), stat.getTotalValue()));
            player.sendMessage("---------------");
        }
    }

    private StatisticType getStatTypeFromString(String name) {
        try {
            return StatisticType.valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private void applyStatPool(MMOEntity entity, StatisticPool poolToApply) {

        // Loop through all the stats in the pool to apply
        for (BaseStatistic sessionStat : poolToApply.getAllStatistics())
            // Apply it to the entity
            entity.getStatPool().addStatistic(sessionStat);

        // Make the changes take effect
        entity.applyStats(true);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // Only players can use this
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;

        // First ensure that a pool exists
        if (!poolsInProgress.containsKey(player.getUniqueId()))
            poolsInProgress.put(player.getUniqueId(), new StatisticPool());

        StatisticPool statPoolToEdit = poolsInProgress.get(player.getUniqueId());

        // If no args,
        if (args.length <= 0) {
            player.sendMessage("You provided no args, here is your current edit stat session");
            player.sendMessage("Add and remove stats with /editstats <set/remove/clear> <statname> <amount> [bonus reason, leave blank for base]");
            player.sendMessage("When you are ready to apply, do /editstats apply");
            player.sendMessage("To apply to another entity, do /editstats toggle -- this allows you to punch entities to apply the stat pool");
            if (statPoolToEdit.getAllStatistics().size() > 0)
                printStatPool(player);
            return true;
        }

        String action = args[0].toLowerCase();

        if (action.equals("apply")) {
            MMOPlayer mmop = EntityManager.getInstance().getMMOPlayer(player);
            applyStatPool(mmop, statPoolToEdit);
            player.sendMessage("Changed your stats!");
            return true;
        }

        if (action.equals("toggle")) {

            if (!punchToApplyPlayers.containsKey(player.getUniqueId()))
                punchToApplyPlayers.put(player.getUniqueId(), Boolean.FALSE);

            Boolean old = punchToApplyPlayers.get(player.getUniqueId());
            punchToApplyPlayers.put(player.getUniqueId(), !old);

            player.sendMessage( !old ? "Punch entities to apply the stat pool!" : "Turned off punch apply stat mode");
            return true;
        }

        if (action.equals("copypunch")) {

            if (!sneakPunchToCopy.containsKey(player.getUniqueId()))
                sneakPunchToCopy.put(player.getUniqueId(), Boolean.FALSE);

            Boolean old = sneakPunchToCopy.get(player.getUniqueId());
            sneakPunchToCopy.put(player.getUniqueId(), !old);

            player.sendMessage( !old ? "Punch entities while sneaking to copy their stat pool!" : "Turned off punch copy stats mode");
            return true;
        }

        if (action.equals("copy")) {
            MMOPlayer mmop = EntityManager.getInstance().getMMOPlayer(player);
            poolsInProgress.put(player.getUniqueId(), mmop.getStatPool().copy());
            player.sendMessage("Copied your stats to your session!");
            return true;
        }

        if (action.equals("clear")){
            poolsInProgress.put(player.getUniqueId(), new StatisticPool());
            player.sendMessage("Cleared your stat pool session");
            return true;
        }

        if (action.equals("set") || action.equals("remove")){

            if (args.length < 2) {
                player.sendMessage("Please provide a statistic type");
                return true;
            }

            StatisticType typewanted = getStatTypeFromString(args[1]);
            if (typewanted == null) {
                player.sendMessage(String.format("Please provide a valid statistic type: %s", Arrays.toString(StatisticType.values())));
                return true;
            }

            // Where the logic branches off, if we want to remove a stat
            if (action.equals("remove")) {
                statPoolToEdit.removeStatistic(typewanted);
                player.sendMessage(String.format("Successfully removed the %s statistic", typewanted));
                return true;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException | IndexOutOfBoundsException ignored) {
                player.sendMessage("Please provide an amount to set this stat to. Additionally, you can add a reason after the amount, and make it a bonus effect rather than a base value");
                return true;
            }

            String reason = "default";
            try {
                reason = args[3];
            } catch (IndexOutOfBoundsException ignored) {}


            BaseStatistic stat = statPoolToEdit.getStatistic(typewanted);
            if (reason.equals("default")) {
                stat.setBaseValue(amount);
                player.sendMessage(String.format("Set base value of stat %s to %s", typewanted, amount));
                return true;
            }

            stat.addBonus(reason, amount);

            player.sendMessage(String.format("Added bonus value of stat %s to %s with unique identifier %s", typewanted, amount, reason));
            return true;
        }

        player.sendMessage("Unknown action, please use /editstats <set / remove / clear / apply / toggle / copy / punchcopy>");

        return true;


    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPunchApplyCopyStat(EntityDamageByEntityEvent e) {

        if (!(e.getDamager() instanceof Player))
            return;

        Player puncher = (Player) e.getDamager();
        StatisticPool pool = poolsInProgress.get(e.getDamager().getUniqueId());
        MMOEntity punched = EntityManager.getInstance().getMMOEntity(e.getEntity().getUniqueId());

        // Special case to copy stats
        if (puncher.isSneaking() && sneakPunchToCopy.containsKey(e.getDamager().getUniqueId())) {
            // Copy the entities stats to the session
            poolsInProgress.put(puncher.getUniqueId(), punched.getStatPool().copy());
            puncher.sendMessage("Copied stats of " + punched.getEntity().getName());
            return;
        }

        if (!punchToApplyPlayers.containsKey(e.getDamager().getUniqueId()))
            return;

        if (!punchToApplyPlayers.get(e.getDamager().getUniqueId()))
            return;

        applyStatPool(punched, pool);
        e.getDamager().sendMessage("Applied your stat pool to " + punched.getEntity().getName() + ", right click them to see their stats");
    }
}
