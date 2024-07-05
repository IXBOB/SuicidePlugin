package com.ixbob.suicideplugin.event;

import com.ixbob.suicideplugin.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class SuicideCommand implements CommandExecutor {
    private FileConfiguration config;
    private ArrayList<ArrayList<Object>> historyMainList;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (config == null) {
            config = Main.getInstance().getConfig();
        }
        historyMainList = (ArrayList<ArrayList<Object>>) Objects.requireNonNull(config.getList("suicide_latest_history"));

        if (commandSender instanceof Player player) {

            String playerName = player.getName();
            String playerUUIDStr = player.getUniqueId().toString();
            ArrayList<Object> playerHistoryList = getHistoryList(playerUUIDStr);
            //playerHistoryList: index 0 → String uuid; index 1 → long time

            if (playerHistoryList != null && System.currentTimeMillis() - (long)playerHistoryList.get(1) <= 120 * 1000 ) {
                player.sendMessage(Component.text("§c你结束自己生命的频率太高了！"));
                return true;
            }

            suicideWithoutShowingDeathVanillaDeathMessage(player);


            if (playerHistoryList == null) {
                createHistory(playerUUIDStr, System.currentTimeMillis());
            } else {
                updateHistory(playerHistoryList, System.currentTimeMillis());
            }

            Bukkit.getServer().broadcast(Component.text("§c" + playerName + " 结束了自己的生命"));
        }
        return true;
    }

    private void createHistory(String uuid, long time) {
        historyMainList.add(new ArrayList<>(Arrays.asList(uuid, time)));
        Main.getInstance().saveConfig();
    }

    private void updateHistory(ArrayList<Object> innerList, long time) {
        innerList.set(1, time);
        Main.getInstance().saveConfig();
    }

    private ArrayList<Object> getHistoryList(String uuidStr) {
        for (ArrayList<Object> innerList : historyMainList) {
            if (innerList.contains(uuidStr)) {
                return innerList;
            }
        }
        return null;
    }

    private void suicideWithoutShowingDeathVanillaDeathMessage(Player suicidePlayer) {

        ArrayList<World> worlds = new ArrayList<>();

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (!worlds.contains(player.getWorld())) {
                worlds.add(player.getWorld());
            }
        }

        for (World world : worlds) {
            boolean isSendDeathMessage = world.getGameRuleValue(GameRule.SHOW_DEATH_MESSAGES);
            if (isSendDeathMessage) {
                world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
            }
        }

        suicidePlayer.setHealth(0);

        for (World world : worlds) {
            boolean isSendDeathMessage = world.getGameRuleValue(GameRule.SHOW_DEATH_MESSAGES);
            if (isSendDeathMessage) {
                world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, true);
            }
        }
    }
}
