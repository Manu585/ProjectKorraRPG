package com.projectkorra.rpg.storage;

import com.projectkorra.projectkorra.storage.DBConnection;
import com.projectkorra.projectkorra.storage.MySQL;

public class TableCreator extends DBConnection {

    public static final String RPG_PLAYER_TABLE = "pkrpg_players";
    public static final String RPG_SCHEDULE_TABLE = "pkrpg_schedule";
    public static final String RPG_AVATAR_TABLE = "pkrpg_avatars";
    public static final String RPG_PASTLIVES_TABLE = "pkrpg_pastlives";

    public TableCreator() {
        this.createRpgPlayerTable();
        this.createScheduleTable();
        this.createRpgAvatarTable();
        this.createRpgPastLivesTable();
    }

    private void createRpgPlayerTable() {
        if (sql instanceof MySQL) {
            if (!sql.tableExists(RPG_PLAYER_TABLE)) {
                final String query = "CREATE TABLE `" + RPG_PLAYER_TABLE + "` ("
                        + "`uuid` varchar(36) NOT NULL,"
                        + "`xp` INT NOT NULL DEFAULT 0,"
                        + "`level` INT NOT NULL DEFAULT 1,"
                        + "PRIMARY KEY (`uuid`),"
                        + "FOREIGN KEY (`uuid`) REFERENCES `pk_players`(`uuid`) ON DELETE CASCADE"
                        + ");";
                sql.modifyQuery(query, false);
            }
        } else {
            if (!sql.tableExists(RPG_PLAYER_TABLE)) {
                final String query = "CREATE TABLE " + RPG_PLAYER_TABLE + "("
                        + "uuid TEXT NOT NULL, "
                        + "xp INTEGER NOT NULL DEFAULT 0, "
                        + "level INTEGER NOT NULL DEFAULT 1, "
                        + "PRIMARY KEY (uuid), "
                        + "FOREIGN KEY (uuid) REFERENCES pk_players(uuid) ON DELETE CASCADE"
                        + ");";
                sql.modifyQuery(query, false);
            }
        }
    }

    private void createScheduleTable() {
        if (sql instanceof MySQL) {
            if (!sql.tableExists(RPG_SCHEDULE_TABLE)) {
                final String query = "CREATE TABLE `" + RPG_SCHEDULE_TABLE + "` ("
                        + "`id` INT NOT NULL AUTO_INCREMENT,"
                        + "`worldevent` TEXT NOT NULL,"
                        + "`last_triggered` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                        + "PRIMARY KEY (`id`))";
                sql.modifyQuery(query, false);
            }
        } else {
            if (!sql.tableExists(RPG_SCHEDULE_TABLE)) {
                final String query = "CREATE TABLE " + RPG_SCHEDULE_TABLE + " ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "worldevent TEXT NOT NULL,"
                        + "last_triggered TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                        + ")";
                sql.modifyQuery(query, false);
            }
        }
    }

    private void createRpgAvatarTable() {
        if (sql instanceof MySQL) {
            if (!sql.tableExists(RPG_AVATAR_TABLE)) {
                final String query = "CREATE TABLE `" + RPG_AVATAR_TABLE + "` ("
                        + "`uuid` varchar(36) NOT NULL,"
                        + "`player` varchar(255) NOT NULL,"
                        + "`startTime` DATETIME NOT NULL,"
                        + "`elements` TEXT NOT NULL,"
                        + "PRIMARY KEY (`uuid`)"
                        + ");";
                sql.modifyQuery(query, false);
            }
        } else {
            if (!sql.tableExists(RPG_AVATAR_TABLE)) {
                final String query = "CREATE TABLE " + RPG_AVATAR_TABLE + " ("
                        + "uuid TEXT NOT NULL, "
                        + "player TEXT NOT NULL, "
                        + "startTime TIMESTAMP NOT NULL, "
                        + "elements TEXT NOT NULL, "
                        + "PRIMARY KEY (uuid)"
                        + ");";
                sql.modifyQuery(query, false);
            }
        }
    }

    private void createRpgPastLivesTable() {
        if (sql instanceof MySQL) {
            if (!sql.tableExists(RPG_PASTLIVES_TABLE)) {
                final String query = "CREATE TABLE `" + RPG_PASTLIVES_TABLE + "` ("
                        + "`uuid` varchar(36) NOT NULL,"
                        + "`player` varchar(255) NOT NULL,"
                        + "`startTime` DATETIME NOT NULL,"
                        + "`endTime` DATETIME DEFAULT NULL,"
                        + "`elements` TEXT NOT NULL,"
                        + "`endReason` varchar(255) DEFAULT NULL"
                        + ");";
                sql.modifyQuery(query, false);
            }
        } else {
            if (!sql.tableExists(RPG_PASTLIVES_TABLE)) {
                final String query = "CREATE TABLE " + RPG_PASTLIVES_TABLE + " ("
                        + "uuid TEXT NOT NULL, "
                        + "player TEXT NOT NULL, "
                        + "startTime TIMESTAMP NOT NULL, "
                        + "endTime TIMESTAMP DEFAULT NULL, "
                        + "elements TEXT NOT NULL, "
                        + "endReason TEXT DEFAULT NULL"
                        + ");";
                sql.modifyQuery(query, false);
            }
        }
    }

}
