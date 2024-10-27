package wtf.n1zamu.presents.database.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.n1zamu.presents.NPresents;
import wtf.n1zamu.presents.database.IDataBase;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQLiteDataBase implements IDataBase {
    private Statement statement;
    private final JavaPlugin plugin;

    public SQLiteDataBase() {
        this.plugin = NPresents.getInstance();
        File dataFile = new File(plugin.getDataFolder(), "presents.db");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:plugins/NPresents/presents.db");
            String sql = "CREATE TABLE IF NOT EXISTS presents " +
                    "(worldName VARCHAR(20) NOT NULL, " +
                    "x BIGINT NOT NULL, " +
                    "y BIGINT NOT NULL, " +
                    "z BIGINT NOT NULL, " +
                    "playerName VARCHAR(20), " +
                    "command VARCHAR(150))";
            statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (Exception e) {
            Bukkit.getLogger().info(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        try {
            statement.getConnection().close();
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public void clear() {
        try {
            statement.executeUpdate("DELETE FROM presents");
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public int getTotalPresents() {
        int totalPresents = 0;
        String query = "SELECT DISTINCT worldName, x, y, z FROM presents";
        try {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                totalPresents++;
            }
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getClass().getName() + ": " + e.getMessage());
        }
        return totalPresents;
    }

    @Override
    public int getPlayerPresents(Player player) {
        int playerPresents = 0;
        String query = "SELECT * FROM presents WHERE playerName = ?";
        try (PreparedStatement preparedStatement = statement.getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, player.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                playerPresents++;
            }
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getClass().getName() + ": " + e.getMessage());
        }
        return playerPresents;
    }

    @Override
    public String getCommand(Location location) {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM presents WHERE worldName = '" + location.getWorld().getName()
                    + "' AND x = " + location.getBlock().getX()
                    + " AND y = " + location.getBlock().getY()
                    + " AND z = " + location.getBlock().getZ());

            if (resultSet.next()) {
                return resultSet.getString("command");
            }
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getClass().getName() + ": " + e.getMessage());
        }
        return "";
    }

    @Override
    public void addCommand(Location location, String command) {
        String sql = "INSERT INTO presents(worldName, x, y, z, playerName, command) VALUES(?, ?, ?, ?, NULL, ?)";
        try (PreparedStatement preparedStatement = statement.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, location.getWorld().getName());
            preparedStatement.setInt(2, location.getBlock().getX());
            preparedStatement.setInt(3, location.getBlock().getY());
            preparedStatement.setInt(4, location.getBlock().getZ());
            preparedStatement.setString(5, command);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public void addPresent(Location location, Player player) {
        if (isPresentCollected(location, player)) return;

        String sql = "INSERT INTO presents(worldName, x, y, z, playerName, command) VALUES(?, ?, ?, ?, ?, NULL)";
        try (PreparedStatement preparedStatement = statement.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, location.getWorld().getName());
            preparedStatement.setInt(2, location.getBlock().getX());
            preparedStatement.setInt(3, location.getBlock().getY());
            preparedStatement.setInt(4, location.getBlock().getZ());
            preparedStatement.setString(5, player.getName());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public void removePresent(Location location) {
        try {
            statement.executeUpdate("DELETE FROM presents WHERE worldName = '" + location.getWorld().getName()
                    + "' AND x = " + location.getBlock().getX()
                    + " AND y = " + location.getBlock().getY()
                    + " AND z = " + location.getBlock().getZ());
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public boolean isPresentCollected(Location location, Player player) {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM presents WHERE worldName = '" + location.getWorld().getName()
                    + "' AND x = " + location.getBlock().getX()
                    + " AND y = " + location.getBlock().getY()
                    + " AND z = " + location.getBlock().getZ()
                    + " AND playerName = '" + player.getName() + "'");

            if (resultSet.next()) return true;
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }
}
