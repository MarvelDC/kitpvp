package me.marveldc.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static me.marveldc.kitpvp.Kitpvp.instance;
import static me.marveldc.kitpvp.Kitpvp.prefix;
import static me.marveldc.kitpvp.Util.randomNum;
import static me.marveldc.kitpvp.Util.tl;
import static org.bukkit.Bukkit.getPlayer;

public class Players {

    private String name;
    private UUID uuid;
    private int kills;
    private int deaths;
    private Player player;
    private int coins;
    private String tasks;
    private String lastKilled;
    private String killer;

    public Players(UUID uuid, int killIncrease, int deathIncrease, int coinChange, String tasks, String killer) {
        this.player = getPlayer(uuid);
        this.name = this.player.getName();
        this.uuid = uuid;
        this.kills = killIncrease;
        this.deaths = deathIncrease;
        this.coins = coinChange;
        this.tasks = tasks;
        this.lastKilled = "";
        this.killer = killer;

        try {
            if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getDisplayName().equals(" ")) {
                player.setScoreboard(Util.setScoreboard());
            }
        } catch (NullPointerException e) {
            player.setScoreboard(Util.setScoreboard());
        }

        if (this.tasks.contains("get")) getData();
    }

    public void updateData() {
        String query1 = "insert into kitpvp (uuid, name) values ('{uuid}', '{name}') on conflict (uuid) do update set name = '{name}', kills = {kills}, deaths = {deaths}, coins = {coins}, \"lastKilled\" = '{lastKilled}';"
                .replace("{uuid}", String.valueOf(this.uuid))
                .replace("{name}", getName())
                .replace("{kills}", Integer.toString(getKills()))
                .replace("{deaths}", Integer.toString(getDeaths()))
                .replace("{coins}", Integer.toString(getCoins()));
        try {
            query1 = query1.replace("{lastKilled}", getLastKilled());
        } catch (NullPointerException e) {
            query1 = "insert into kitpvp (uuid, name) values ('{uuid}', '{name}') on conflict (uuid) do update set name = '{name}', kills = {kills}, deaths = {deaths}, coins = {coins};"
                    .replace("{uuid}", String.valueOf(this.uuid))
                    .replace("{name}", getName())
                    .replace("{kills}", Integer.toString(getKills()))
                    .replace("{deaths}", Integer.toString(getDeaths()))
                    .replace("{coins}", Integer.toString(getCoins()));
        }
        final String query = query1;
        new BukkitRunnable() {
            @Override
            public void run() {
                PreparedStatement ps = null;

                try (Connection connection = Kitpvp.getConnection()) {
                    ps = connection.prepareStatement(query);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("[kitpvp] Query execution failed.");
                } finally {
                    if (ps != null) {
                        try {
                            ps.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println("[kitpvp] Failed to close prepared statement.");
                        }
                        //updateScoreboard();
                    }
                }
            }
        }.runTaskAsynchronously(instance);
    }

    public void getData() {
        final String query = "insert into kitpvp (uuid, name) values ('{uuid}', '{name}') on conflict (uuid) do update set name = '{name}', kills = kitpvp.kills, deaths = kitpvp.deaths returning kills, deaths, coins, \"lastKilled\";"
                .replace("{uuid}", String.valueOf(this.uuid))
                .replace("{name}", getName());
        new BukkitRunnable() {
            @SuppressWarnings("Duplicates")
            @Override
            public void run() {
                PreparedStatement ps = null;

                try (Connection connection = Kitpvp.getConnection()) {
                    ps = connection.prepareStatement(query);
                    ResultSet rs;
                    rs = ps.executeQuery();

                    if (rs == null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println("[kitpvp] Failed to close result set.");
                        }
                    }
                    setResult(rs);
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("[kitpvp] Transaction failed.");
                } finally {
                    if (ps != null) {
                        try {
                            ps.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println("[kitpvp] Failed to close prepared statement.");
                        }
                    }
                }
            }
        }.runTaskAsynchronously(instance);
    }

    public void updateScoreboard() {
        Scoreboard board = player.getScoreboard();
        board.getTeam("killCounter").setSuffix(ChatColor.AQUA + Integer.toString(getKills()));
        board.getTeam("deathCounter").setSuffix(ChatColor.AQUA + Integer.toString(getDeaths()));
        board.getTeam("coinCounter").setSuffix(ChatColor.BLUE + Integer.toString(getCoins()));

        try {
            board.getTeam("kd").setSuffix(ChatColor.AQUA + new DecimalFormat(".##")
                    .format(Double.valueOf(String.valueOf(getKills())) / Double.valueOf(String.valueOf(getDeaths()))));
        } catch (ArithmeticException e) {
            board.getTeam("kd").setSuffix(ChatColor.AQUA + "0.00");
        }

    }

    private void setResult(ResultSet rs) {
        try {
            if (rs.next()) {
                if (rs.getObject("lastKilled") == null) {
                    setLastKilled(null);
                } else {
                    setLastKilled(rs.getString("lastKilled"));
                }
                if (!this.killer.equals("")) {
                    if (getLastKilled() == null) {
                        int coins = randomNum(23, 11);

                        setCoins(rs.getInt("coins") + coins);
                        player.sendMessage(tl(prefix + "&7You &6gained &a" + coins + " &7coins for killing &c" + this.killer + "&7!"));

                        setLastKilled(getKiller());
                    } else {
                        String[] temp = getLastKilled().split(",");
                        ArrayList<String> lastKilled = new ArrayList<>();
                        int sum = 0;

                        for (String player : temp) {
                            if (player.equalsIgnoreCase(this.killer)) sum++;
                            lastKilled.add(player);
                        }
                        int coins = randomNum(23, 11);
                        if (sum >= 3) {
                            coins = 0;
                            this.kills = 0;
                            player.sendMessage(tl(prefix + "&cRefrain from targeting players - this kill will not count."));
                        }
                        else if (sum == 2) coins -= 10;
                        else if (sum == 1) coins -= 5;
                        else coins += 10;

                        setCoins(rs.getInt("coins") + coins);
                        player.sendMessage(tl(prefix + "&7You &6gained &a" + coins + " &7coins for killing &c" + this.killer + "&7!"));

                        lastKilled.add(0, this.killer);

                        if (lastKilled.size() >= 4) {
                            lastKilled.remove(lastKilled.size() - 1);
                        }

                        setLastKilled(lastKilled.toString()
                                .replace("[", "")
                                .replace(" ", "")
                                .replace("]", ""));
                    }
                } else {
                    if (this.kills == 0 && this.deaths == 0) {
                        int sum = rs.getInt("coins") + this.coins;
                        if (sum <= 0) {
                            setCoins(0);
                        } else {
                            setCoins(sum);
                        }
                    } else {
                        int sum = rs.getInt("coins") + this.coins;
                        if (sum <= 0) {
                            player.sendMessage(tl(prefix + "&7You would of lost &a" + (this.coins < 0 ? -this.coins : this.coins) + " &7coins but you must have more than &a0 coins&7 always."));
                            setCoins(0);
                        } else {
                            player.sendMessage(tl(prefix + "&7You lost &a" + this.coins + " &7coins."));
                            setCoins(sum);
                        }
                    }
                }

                setKills(rs.getInt("kills") + this.kills);
                setDeaths(rs.getInt("deaths") + this.deaths);

                updateScoreboard();

                if (this.tasks.contains("update")) updateData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public String getLastKilled() { return lastKilled; }

    public String getKiller() {
        return killer;
    }

    public void setLastKilled(String lastKilled) {
        this.lastKilled = lastKilled;
    }
}
