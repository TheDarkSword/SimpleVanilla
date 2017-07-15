package it.mineblock.simplevanilla;

import it.mineblock.mbcore.Chat;
import it.mineblock.mbcore.MySQL;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

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
        List<String> protectedBlocks = Main.config.getStringList("protected-blocks");

        return protectedBlocks.contains(block.getType().name());
    }

    public static boolean canBeProtected(Block block, Player player) {
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

    public static boolean canBeUnprotected(Block block, Player player) {
        if(!isProtected(block)) {
            Chat.send(Message.PROTECT_INEXISTENT.get(), player);
            return false;
        }

        if(player.hasPermission(Permissions.PROTECT_BYPASS.get())) {
            return true;
        }

        long x = block.getX();
        long y = block.getY();
        long z = block.getZ();
        int id = MySQL.getInt(Main.DB_PROTECTION, new String[] {"x", "y", "z"}, new String[] {String.valueOf(x), String.valueOf(y), String.valueOf(z)}, "id");

        if(!MySQL.getString(Main.DB_PROTECTION, "id", String.valueOf(id), "username").equalsIgnoreCase(player.getName())) {
            String owner = MySQL.getString(Main.DB_PROTECTION, "id", String.valueOf(id), "username");

            //if team is not allowed
            if (!MySQL.getBoolean(Main.DB_USER, "username", owner, "allowTeam")) {
                //if player is not the owner's allowedPlayer
                if (MySQL.getString(Main.DB_USER, "username", owner, "allowedPlayer").equalsIgnoreCase(player.getName())) {
                    Chat.send(Message.PROTECT_PERM_DENIED.get(), player);
                    return false;
                }
            } else { //if team is allowed
                //if player is not in owner team
                if (!MySQL.getString(Main.DB_USER, "username", owner, "team").equalsIgnoreCase(MySQL.getString(Main.DB_USER, "username", player.getName(), "team"))) {
                    Chat.send(Message.PROTECT_PERM_DENIED.get(), player);
                    return false;
                } else { //if player is in owner team
                    //if team setting "control" is false
                    if (!codeParser(owner).get(2)) {
                        Chat.send(Message.PROTECT_PERM_DENIED.get(), player);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean canBeOpened(Block block, Player player) {
        if(!isProtected(block)) {
            return true;
        }

        if(player.hasPermission(Permissions.PROTECT_BYPASS.get())) {
            return true;
        }

        long x = block.getX();
        long y = block.getY();
        long z = block.getZ();
        int id = MySQL.getInt(Main.DB_PROTECTION, new String[] {"x", "y", "z"}, new String[] {String.valueOf(x), String.valueOf(y), String.valueOf(z)}, "id");

        if(!MySQL.getString(Main.DB_PROTECTION, "id", String.valueOf(id), "username").equalsIgnoreCase(player.getName())) {
            String owner = MySQL.getString(Main.DB_PROTECTION, "id", String.valueOf(id), "username");

            //if team is not allowed
            if (!MySQL.getBoolean(Main.DB_USER, "username", owner, "allowTeam")) {
                //if player is not the owner's allowedPlayer
                if (MySQL.getString(Main.DB_USER, "username", owner, "allowedPlayer").equalsIgnoreCase(player.getName())) {
                    Chat.send(Message.PROTECT_PERM_DENIED.get(), player);
                    return false;
                }
            } else { //if team is allowed
                //if player is not in owner team
                if (!MySQL.getString(Main.DB_USER, "username", owner, "team").equalsIgnoreCase(MySQL.getString(Main.DB_USER, "username", player.getName(), "team"))) {
                    Chat.send(Message.PROTECT_PERM_DENIED.get(), player);
                    return false;
                } else { //if player is in owner team
                    //if team setting "open" is false
                    if (!codeParser(owner).get(0)) {
                        Chat.send(Message.PROTECT_PERM_DENIED.get(), player);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean canBeBroken(Block block, Player player) {
        if(!isProtected(block)) {
            return true;
        }

        if(player.hasPermission(Permissions.PROTECT_BYPASS.get())) {
            return true;
        }

        long x = block.getX();
        long y = block.getY();
        long z = block.getZ();
        int id = MySQL.getInt(Main.DB_PROTECTION, new String[] {"x", "y", "z"}, new String[] {String.valueOf(x), String.valueOf(y), String.valueOf(z)}, "id");

        if(!MySQL.getString(Main.DB_PROTECTION, "id", String.valueOf(id), "username").equalsIgnoreCase(player.getName())) {
            String owner = MySQL.getString(Main.DB_PROTECTION, "id", String.valueOf(id), "username");

            //if team is not allowed
            if (!MySQL.getBoolean(Main.DB_USER, "username", owner, "allowTeam")) {
                //if player is not the owner's allowedPlayer
                if (MySQL.getString(Main.DB_USER, "username", owner, "allowedPlayer").equalsIgnoreCase(player.getName())) {
                    Chat.send(Message.PROTECT_PERM_DENIED.get(), player);
                    return false;
                }
            } else { //if team is allowed
                //if player is not in owner team
                if (!MySQL.getString(Main.DB_USER, "username", owner, "team").equalsIgnoreCase(MySQL.getString(Main.DB_USER, "username", player.getName(), "team"))) {
                    Chat.send(Message.PROTECT_PERM_DENIED.get(), player);
                    return false;
                } else { //if player is in owner team
                    //if team setting "open" is false
                    if (!codeParser(owner).get(1)) {
                        Chat.send(Message.PROTECT_PERM_DENIED.get(), player);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean isProtected(Block block) {
        long x = block.getX();
        long y = block.getY();
        long z = block.getZ();

        if(MySQL.rowExists(Main.DB_PROTECTION, new String[] {"x", "y", "z"}, new String[] {String.valueOf(x), String.valueOf(y), String.valueOf(z)})) {
            return true;
        }
        return false;
    }

    public static Block getPlayerTargetBlock(Player player) {
        return player.getTargetBlock((Set) null, 5);
    }

    public static boolean isNewbie(Player player) {
        Date date = new Date();
        Date firstLogin = new Date(MySQL.getTimestamp(Main.DB_USER, "uuid", player.getUniqueId().toString(), "login_first").getTime());
        return date.getTime() - firstLogin.getTime() < 172800000L;
    }

    public static String codeBuilder(Player player, String position, String value) {
        String[] values = MySQL.getString(Main.DB_USER, "uuid", player.getUniqueId().toString(), "teamSettings").split("-");
        String open = values[0];
        String destroy = values[1];
        String control = values[2];

        switch(position) {
            case "open":
                open = value;
                break;
            case "destroy":
                destroy = value;
                break;
            case "control":
                control = value;
                break;
            default:
                break;
        }

        String code = open + "-" + destroy + "-" + control;
        return code;
    }

    public static ArrayList<Boolean> codeParser(String player) {
        String[] values = MySQL.getString(Main.DB_USER, "username", player, "teamSettings").split("-");
        ArrayList<Boolean> output = new ArrayList<>();

        for(String str : values) {
            if(str.equalsIgnoreCase("a")) {
                output.add(true);
            }
            else if(str.equalsIgnoreCase("d")) {
                output.add(false);
            }
        }
        return output;
    }
}