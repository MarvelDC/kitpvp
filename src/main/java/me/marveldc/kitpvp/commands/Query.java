package me.marveldc.kitpvp.commands;

import me.marveldc.kitpvp.Kitpvp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import static me.marveldc.kitpvp.Kitpvp.instance;
import static me.marveldc.kitpvp.Util.tl;

public class Query implements CommandExecutor {

    public Query(Kitpvp plugin) {
        plugin.getCommand("query").setExecutor(this);
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
        String prefix = Kitpvp.prefix;
        if (args.length == 0) {
            sender.sendMessage(tl(prefix + instance.getMessages().getString("usageQuery")));
            return true;
        }

        StringBuilder msg1 = new StringBuilder();
        for (String arg : Arrays.copyOfRange(args, 0, args.length)) {
            msg1.append(arg);
            msg1.append(" ");
        }
        String msg = msg1.toString();

        if (msg.toLowerCase().contains("insert")) {

            sender.sendMessage(tl(prefix + "&6Starting an asynchronous task."));

            new BukkitRunnable() {
                @Override
                public void run() {
                    sender.sendMessage(tl(prefix + "&aAsynchronous task started."));
                    sender.sendMessage(tl(prefix + "&6Starting database transaction."));
                    PreparedStatement preparedStatement = null;

                    try (Connection connection = Kitpvp.getConnection()){
                        preparedStatement = connection.prepareStatement(msg);
                        int rows = preparedStatement.executeUpdate();
                        sender.sendMessage(tl(prefix + "&aTransaction succeeded. Inserted: &7" + rows + "&a."));
                    } catch (SQLException e) {
                        e.printStackTrace();
                        sender.sendMessage(tl(prefix + "&4Transaction failed."));
                    } finally {
                        sender.sendMessage(tl(prefix + "&cClosing resources."));

                        closePrepared(preparedStatement, sender, prefix);
                    }
                }
            }.runTaskAsynchronously(instance);
        } else if (msg.toLowerCase().contains("select")) {
            sender.sendMessage(tl(prefix + "&6Starting an asynchronous task."));

            new BukkitRunnable() {
                @Override
                public void run() {
                    sender.sendMessage(tl(prefix + "&aAsynchronous task started."));
                    sender.sendMessage(tl(prefix + "&6Starting database transaction."));
                    PreparedStatement preparedStatement = null;
                    ResultSet resultSet;

                    try (Connection connection = Kitpvp.getConnection()) {
                        preparedStatement = connection.prepareStatement(msg);
                        resultSet = preparedStatement.executeQuery();

                        if (resultSet == null) {
                            try {
                                resultSet.close();
                                sender.sendMessage(tl(prefix + "&cClosed result set."));
                            } catch (SQLException e) {
                                e.printStackTrace();
                                sender.sendMessage(tl(prefix + "&4Error while closing result set."));
                            }
                        }

                        if (!resultSet.next()) {
                            sender.sendMessage(tl(prefix + "&cNo more values."));
                        } else {
                            do {
                                sender.sendMessage(tl(prefix + "&aValue: &7" + resultSet.getObject("kills") + "&a."));
                            } while (resultSet.next());
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                        sender.sendMessage(tl(prefix + "&4Transaction failed."));
                    } finally {
                        sender.sendMessage(tl(prefix + "&cClosing resources."));
                        closePrepared(preparedStatement, sender, prefix);
                    }
                }
            }.runTaskAsynchronously(instance);
        } else {
            sender.sendMessage(tl(prefix + "&cArguments &7" + msg + " is not valid."));
        }
        return true;
    }

    private void closePrepared(PreparedStatement preparedStatement, CommandSender sender, String prefix) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
                sender.sendMessage(tl(prefix + "&aClosed statement."));
            } catch (SQLException e) {
                e.printStackTrace();
                sender.sendMessage(tl(prefix + "&4Error while closing statement."));
            }
        }
    }
}
