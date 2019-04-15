package me.marveldc.kitpvp;

import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static me.marveldc.kitpvp.Kitpvp.instance;

class Database {

    private ResultSet toReturn;

    public Database(String type, String query) {
        executeDb(type, query);
    }

    private void executeDb(String type, String query) {
        if (type.equalsIgnoreCase("noreturn")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    PreparedStatement ps = null;

                    try (Connection connection = Kitpvp.getConnection()) {
                        ps = connection.prepareStatement(query);
                        ps.executeQuery();
                        System.out.println("[kitpvp] Added new row.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("[kitpvp] Transaction failed.");
                    } finally {
                        System.out.println("[kitpvp] Closing resources.");
                        closePrepared(ps);
                    }
                }
            }.runTaskAsynchronously(instance);
        } else if (type.equalsIgnoreCase("return")) {
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
                        setRs(rs);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("[kitpvp] Transaction failed.");
                    } finally {
                        closePrepared(ps);
                    }
                }
            }.runTaskAsynchronously(instance);
        }
    }

    private void setRs(ResultSet resultSet) {
        this.toReturn = resultSet;
    }

    public ResultSet getRs() {
        return toReturn;
    }

    private void closePrepared(PreparedStatement ps) {
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
