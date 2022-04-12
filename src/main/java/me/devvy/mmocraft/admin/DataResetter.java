package me.devvy.mmocraft.admin;

import me.devvy.mmocraft.experience.PersistentExperienceContainer;
import me.devvy.mmocraft.stats.PersistentBaseStatisticContainer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class DataResetter implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        for (World w : Bukkit.getWorlds()) {
            for (Entity e : w.getEntities()){
                // Remove all instances of persistent data on all entities
                e.getPersistentDataContainer().remove(PersistentBaseStatisticContainer.BASE_STATISTIC_CONTAINER_KEY);
                e.getPersistentDataContainer().remove(PersistentExperienceContainer.EXPERIENCE_CONTAINER_KEY);
            }
        }

        sender.sendMessage(ChatColor.RED + "Successfully cleared all persistent data from loaded entities");
        return true;
    }


}
