package me.marveldc.kitpvp.players;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

import static me.marveldc.kitpvp.Kitpvp.instance;

public class DataSource {

    private static HikariDataSource ds;

    public DataSource() {
        HikariConfig config = new HikariConfig(instance.getDataFolder() + "database.properties");
        ds = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

}
