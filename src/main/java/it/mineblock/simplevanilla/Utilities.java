package it.mineblock.simplevanilla;

import it.mineblock.mbcore.Chat;
import it.mineblock.mbcore.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Copyright © 2017 by Lorenzo Magni
 * All rights reserved. No part of this code may be reproduced, distributed, or transmitted in any form or by any means,
 * including photocopying, recording, or other electronic or mechanical methods, without the prior written permission
 * of the creator.
 */
public class Utilities {

    public static boolean isBlockCompatible(Block block) {

        if(block == null) {
            return false;
        }

        for(String id : Main.config.getStringList("protected-blocks")) {
            if(block.getType().equals(Material.getMaterial(id))) {
                return true;
            }
        }
        return false;
    }

    public static boolean canBeProtected(Block block, Player player) {
        isBlockCompatible(block);

        if(!isBlockCompatible(block)) {
            Chat.send(Message.PROTECT_INCOMPATIBLE.get(), player);
            return false;
        }

        if(isProtected(block)) {
            Chat.send(Message.PROTECT_ALREADY.get(), player);
            return false;
        }
        return true;
    }

    public static boolean canBeControlled(Block block, Player player) {
        if (!isProtected(block)) {
            Chat.send(Message.PROTECT_INEXISTENT.get(), player);
            return false;
        }

        if (player.hasPermission(Permissions.PROTECT_BYPASS.get())) {
            return true;
        }

        long x = (long) block.getX();
        long y = (long) block.getY();
        long z = (long) block.getZ();
        int id = MySQL.getInt(Main.DB_PROTECTION, new String[]{"x", "y", "z"}, new String[]{String.valueOf(x), String.valueOf(y), String.valueOf(z)}, "id");

        if (!MySQL.getString(Main.DB_PROTECTION, "id", String.valueOf(id), "username").equalsIgnoreCase(player.getName())) {
            String owner = MySQL.getString(Main.DB_PROTECTION, "id", String.valueOf(id), "username");

            if (MySQL.getBoolean(Main.DB_PROTECTION, "id", String.valueOf(id), "private")) {
                Chat.send(Message.PROTECT_PRIVATE_WARN.get(), player);
                return false;
            }

            //if team is not allowed
            if (!MySQL.getBoolean(Main.DB_USER, "username", owner, "allowTeam")) {
                //if player is not the owner's allowedPlayer
                String allowedPlayer = MySQL.getString(Main.DB_USER, "username", owner, "allowedPlayer");

                if(allowedPlayer == null) {
                    Chat.send(Message.PROTECT_PERM_DENIED.get(), player);
                    return false;
                }

                if(allowedPlayer.equalsIgnoreCase(player.getName())) {
                    return true;
                } else {
                    Chat.send(Message.PROTECT_PERM_DENIED.get(), player);
                    return false;
                }
            } else { //if team is allowed
                //if player is not in owner team
                if (!MySQL.getString(Main.DB_USER, "username", owner, "team").equalsIgnoreCase(MySQL.getString(Main.DB_USER, "username", player.getName(), "team"))) {
                    Chat.send(Message.PROTECT_PERM_DENIED.get(), player);
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isProtected(Block block) {
        if(block == null) {
            return false;
        }

        long x = (long) block.getX();
        long y = (long) block.getY();
        long z = (long) block.getZ();

        if(MySQL.rowExists(Main.DB_PROTECTION, new String[] {"x", "y", "z"}, new String[] {String.valueOf(x), String.valueOf(y), String.valueOf(z)})) {
            return true;
        }
        return false;
    }

    public static Block getPlayerTargetBlock(Player player) {
        return player.getTargetBlock((Set <Material>) null, 5);
    }

    public static boolean isNewbie(Player player) {
        Date date = new Date();
        Date firstLogin = new Date(MySQL.getTimestamp(Main.DB_USER, "uuid", player.getUniqueId().toString(), "first_login").getTime());
        return date.getTime() - firstLogin.getTime() < 172800000L;
    }
}