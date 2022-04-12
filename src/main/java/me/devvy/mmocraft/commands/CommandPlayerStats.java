package me.devvy.mmocraft.commands;

import me.devvy.mmocraft.entity.EntityManager;
import me.devvy.mmocraft.player.MMOPlayer;
import me.devvy.mmocraft.stats.BaseStatistic;
import me.devvy.mmocraft.stats.StatisticPool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandPlayerStats implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player))
            return false;

        Player p = (Player) sender;

        MMOPlayer mmop = EntityManager.getInstance().getMMOPlayer(p);
        StatisticPool stats = mmop.getStatPool();

        p.sendMessage("------------------------------");
        p.sendMessage("Stats for " + p.getName());
        p.sendMessage("");
        p.sendMessage("Level: " + mmop.getLevelExperience().getLevel());
        int xp = mmop.getLevelExperience().getXp();
        int xpNeed = mmop.getLevelExperience().getTotalXpNeeded();
        int need = mmop.getLevelExperience().getXpNeededToLevel();
        float ratio = (float) xp / (float) xpNeed;
        int iratio = (int)(Math.floor(ratio*100));
        p.sendMessage("XP Progress: " + xp + "/" + xpNeed + " - " + need + "XP to go (" + iratio + "%)");
        p.sendMessage("");
        for (BaseStatistic stat : stats.getAllStatistics())
            p.sendMessage(stat.getType().toString() + ": " + stat.getTotalValue());
        p.sendMessage("------------------------------");
        return true;
    }
}
