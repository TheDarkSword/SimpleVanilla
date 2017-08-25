package it.mineblock.simplevanilla;

import it.mineblock.simplevanilla.commands.*;
import it.mineblock.simplevanilla.listeners.*;
import it.mineblock.mbcore.Chat;
import it.mineblock.mbcore.Database;
import it.mineblock.mbcore.MySQL;
import it.mineblock.mbcore.spigot.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Copyright © 2017 by Lorenzo Magni
 * All rights reserved. No part of this code may be reproduced, distributed, or transmitted in any form or by any means,
 * including photocopying, recording, or other electronic or mechanical methods, without the prior written permission
 * of the creator.
 */
public class Main extends JavaPlugin {

    public static final SimpleDateFormat TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Database DB_USER;
    public static Database DB_TICKET;
    public static Database DB_PROTECTION;
    public static Database DB_TEAM;
    public static FileConfiguration config;
    public static HashMap<Player, Location> locationBackup = new HashMap<>();
    public static HashMap<Player, Integer> staffTicket = new HashMap<>();

    private static final String USER = "user";
    private static final String TICKET = "ticket";
    private static final String PROTECTION = "protection";
    private static final String TEAM = "team";
    private static String PREFIX;
    private static final String CONFIG = "config.yml";
    private static Statement statement;
    private static Config configuration = Config.getInstance();

    public void onEnable() {
        config = configuration.autoloadConfig(this, CONFIG, new File(getDataFolder(), CONFIG));

        PREFIX = config.getString("mysql.prefix");

        dbInitialization();
        dbTableCreation();
        dbTableUpdate();

        registerListeners();
        registerCommands();

        Chat.getLogger("&6&lSimpleVanilla &2enabled!", "info");
    }

    private void dbInitialization() {
        String host = config.getString("mysql.host");
        int port = config.getInt("mysql.port");
        String database = config.getString("mysql.database");
        String username = config.getString("mysql.username");
        String password = config.getString("mysql.password");

        statement = MySQL.connect(statement, host, port, database,username, password);
        DB_USER = new Database(statement, PREFIX, USER);
        DB_TICKET = new Database(statement, PREFIX, TICKET);
        DB_PROTECTION = new Database(statement, PREFIX, PROTECTION);
        DB_TEAM = new Database(statement, PREFIX, TEAM);
    }

    private void dbTableCreation() {
        MySQL.createTable(DB_USER, new String[] {
                "id INT(11) NOT NULL AUTO_INCREMENT",
                "username VARCHAR(200) NOT NULL",
                "uuid VARCHAR(200) NOT NULL",
                "spawnpoint VARCHAR(400) NULL",
                "team VARCHAR(200) NULL",
                "teamPending VARCHAR(200) NULL",
                "pvp TINYINT DEFAULT 0",
                "allowedPlayer VARCHAR(200) NULL",
                "allowTeam TINYINT DEFAULT 0",
                "first_login TIMESTAMP NULL",
                "PRIMARY KEY (id)"
        });

        MySQL.createTable(DB_TICKET, new String[] {
                "id INT(11) NOT NULL AUTO_INCREMENT",
                "username VARCHAR(200) NOT NULL",
                "uuid VARCHAR(200) NOT NULL",
                "x BIGINT NULL",
                "y BIGINT NULL",
                "z BIGINT NULL",
                "message VARCHAR(400)",
                "jid BIGINT NOT NULL",
                "active TINYINT DEFAULT 1",
                "PRIMARY KEY (id)"
        });

        MySQL.createTable(DB_PROTECTION, new String[] {
                "id INT(11) NOT NULL AUTO_INCREMENT",
                "username VARCHAR(200) NOT NULL",
                "uuid VARCHAR(200) NOT NULL",
                "block VARCHAR(64) NOT NULL",
                "x BIGINT NOT NULL",
                "y BIGINT NOT NULL",
                "z BIGINT NOT NULL",
                "private TINYINT DEFAULT 0",
                "PRIMARY KEY (id)"
        });

        MySQL.createTable(DB_TEAM, new String[] {
                "id INT(11) NOT NULL AUTO_INCREMENT",
                "name VARCHAR(200) NOT NULL",
                "leader VARCHAR(200) NOT NULL",
                "PRIMARY KEY (id)"
        });
    }

    private void dbTableUpdate() {

    }

    private void registerListeners() {
        Bukkit.getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OnPlayerLeave(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OnPlayerPvP(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OnPlayerPlaceBlock(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OnPlayerBreakBlock(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OnPlayerInteract(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OnPlayerUseLimited(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OnEntityExplode(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OnInventoryMove(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OnPlayerSleep(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OnPlayerRespawn(), this);
    }

    private void registerCommands() {
        getCommand("ticket").setExecutor(new Ticket());
        getCommand("pvp").setExecutor(new PvP());
        getCommand("protect").setExecutor(new Protect());
        getCommand("team").setExecutor(new Team());
    }

    /**
     *
     * @TODO Add support for fire arrow
     * @TFO Try to add protection to armor stands
     */
}
