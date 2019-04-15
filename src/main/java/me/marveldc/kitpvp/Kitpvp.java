package me.marveldc.kitpvp;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.marveldc.kitpvp.commands.Query;
import me.marveldc.kitpvp.events.Pvp;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static me.marveldc.kitpvp.Util.tl;

public class Kitpvp extends JavaPlugin {
    private FileConfiguration messages;
    private File messagesFile;

    private static HikariDataSource ds;

    public static String prefix;
    public static Kitpvp instance;

    public Kitpvp() {
        instance = this;
    }

    public FileConfiguration getMessages() {
        return this.messages;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Failed to load.");
        }

        createMessagesFile();
        prefix = tl(getMessages().getString("prefix"));

        new Query(this);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new Pvp(), this);

        HikariConfig config = new HikariConfig(getDataFolder() + "\\database.properties");
        ds = new HikariDataSource(config);

        //fetchData();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    private void saveFile(int type) {
        try {
            if (type == 1) getMessages().save(messagesFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[KitPvP] Failed to save files.");
        }
    }

    private void createMessagesFile() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        if (!messagesFile.exists()) {
            if (!messagesFile.getParentFile().mkdirs()) System.out.println("[KitPvP] Failed to create messages.yml directories.");
            try {
                if (!messagesFile.createNewFile()) System.out.println("[KitPvP] Failed to create messages.yml");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("[KitPvP] Function createMessagesFile() failed.");
            }
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);
        try {
            messages.load(messagesFile);
            HashMap<String, Object> defaults = new HashMap<>();
            defaults.put("prefix", "&c&lKitPvP &6&l>> &7");
            defaults.put("noPermission", "&cYou lack permission to execute this command.");
            defaults.put("usageQuery", "&cLacking a query.");

            setDefaultValues(messages, defaults);
            prefix = tl(messages.getString("prefix"));
            saveFile(1);
            messages = YamlConfiguration.loadConfiguration(messagesFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            System.out.println("[KitPvP] Failed to load default values into messages.yml.");
        }
    }

    private void setDefaultValues(FileConfiguration messages, Map<String, Object> defaults) {
        if (messages == null) return;
        for (final Map.Entry<String, Object> e : defaults.entrySet()) {
            if (!messages.contains(e.getKey())) {
                messages.set(e.getKey(), e.getValue());
            }
        }
    }
}
