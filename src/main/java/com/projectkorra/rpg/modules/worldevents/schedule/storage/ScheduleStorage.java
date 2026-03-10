package com.projectkorra.rpg.modules.worldevents.schedule.storage;

import com.projectkorra.projectkorra.storage.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;
import static com.projectkorra.rpg.storage.TableCreator.RPG_SCHEDULE_TABLE;

// TODO: Add cache (Sync DB Calls)
public class ScheduleStorage extends DBConnection {

    private final Plugin plugin;

    public ScheduleStorage(Plugin plugin) {
        this.plugin = plugin;
    }

    public Optional<Instant> getLastTriggeredTime(String worldEventKey) throws SQLException {
        try {
            Connection conn = sql.getConnection();

            if (conn == null) {
                throw new SQLException("Could not get database connection");
            }

            final String query = "SELECT last_triggered FROM " + RPG_SCHEDULE_TABLE + " WHERE worldevent = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, worldEventKey);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("last_triggered");
                return Optional.of(timestamp.toInstant());
            }

            return Optional.empty();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get last triggered time for event: " + worldEventKey, e);
            throw e;
        }
    }

    public void updateLastTriggeredTime(String worldEventKey, Instant triggerTime) throws SQLException {
        Instant timeToRecord = (triggerTime != null) ? triggerTime : Instant.now();

        try {
            Connection conn = sql.getConnection();

            if (conn == null) {
                throw new SQLException("Could not get database connection");
            }

            PreparedStatement checkPs = conn.prepareStatement("SELECT id FROM " + RPG_SCHEDULE_TABLE + " WHERE worldevent = ?");
            checkPs.setString(1, worldEventKey);
            ResultSet rs = checkPs.executeQuery();

            boolean recordExists = rs.next();
            rs.close();
            checkPs.close();

            PreparedStatement updatePs;
            if (recordExists) {
                updatePs = conn.prepareStatement("UPDATE " + RPG_SCHEDULE_TABLE + " SET last_triggered = ? WHERE worldevent = ?");
                updatePs.setTimestamp(1, Timestamp.from(timeToRecord));
                updatePs.setString(2, worldEventKey);
            } else {
                updatePs = conn.prepareStatement("INSERT INTO " + RPG_SCHEDULE_TABLE + " (worldevent, last_triggered) VALUES (?, ?)");
                updatePs.setString(1, worldEventKey);
                updatePs.setTimestamp(2, Timestamp.from(timeToRecord));
            }

            updatePs.executeUpdate();
            updatePs.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to update last triggered time for event: " + worldEventKey, e);
            throw e;
        }
    }

}
