package me.marveldc.kitpvp.players;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Execute {

    public static List<PlayerData> fetchData() {
        final String SQL_QUERY = "select * from kitpvp;";
        List<PlayerData> players = null;
        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);
             ResultSet rs = pst.executeQuery()) {
            PlayerData player;
            while (rs.next()) {
                player = new PlayerData();
                player.setUuid(rs.getString("uuid"));
                player.setKills(rs.getInt("kills"));
                player.setDeaths(rs.getInt("deaths"));
                player.setLastKilled(rs.getString("lastKilled"));
                players.add(player);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[KitPvP] Error while connecting to database to query.");
        }
        System.out.println(players.toString());
        return players;
    }
}
