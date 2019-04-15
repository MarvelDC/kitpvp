package me.marveldc.kitpvp;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.*;

import java.util.Random;

import static org.bukkit.Bukkit.getScoreboardManager;

public class Util {
    public static String tl(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static int randomNum(int max, int min) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    public static Scoreboard setScoreboard() {
        Scoreboard board = getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("kitpvp", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(tl("&b-- &c&lKitPvP &b--"));

        Score blank = obj.getScore(" ");
        blank.setScore(6);

        Team killCounter = board.registerNewTeam("killCounter");
        killCounter.addEntry(ChatColor.RED + "" + ChatColor.WHITE);
        killCounter.setPrefix(ChatColor.GOLD + "Kills: ");
        killCounter.setSuffix(" ");
        obj.getScore(ChatColor.RED + "" + ChatColor.WHITE).setScore(5);

        Team deathCounter = board.registerNewTeam("deathCounter");
        deathCounter.addEntry(ChatColor.WHITE + "" + ChatColor.RED);
        deathCounter.setPrefix(ChatColor.GOLD + "Deaths: ");
        deathCounter.setSuffix(" ");
        obj.getScore(ChatColor.WHITE + "" + ChatColor.RED).setScore(4);

        Team kd = board.registerNewTeam("kd");
        kd.addEntry(ChatColor.GRAY + "" + ChatColor.AQUA);
        kd.setPrefix(ChatColor.GOLD + "K/D: ");
        kd.setSuffix(" ");
        obj.getScore(ChatColor.GRAY + "" + ChatColor.AQUA).setScore(3);

        Score blank1 = obj.getScore("  ");
        blank1.setScore(2);

        Team coinCounter = board.registerNewTeam("coinCounter");
        coinCounter.addEntry(ChatColor.GRAY + "" + ChatColor.BLUE);
        coinCounter.setPrefix(ChatColor.GREEN + "Coins: ");
        coinCounter.setSuffix(" ");
        obj.getScore(ChatColor.GRAY + "" + ChatColor.BLUE).setScore(1);

        Score blank2 = obj.getScore("   ");
        blank2.setScore(0);

        return board;
    }
}
