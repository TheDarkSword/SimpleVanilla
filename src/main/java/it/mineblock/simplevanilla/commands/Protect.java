package it.mineblock.simplevanilla.commands;

import it.mineblock.simplevanilla.Main;
import it.mineblock.simplevanilla.Message;
import it.mineblock.simplevanilla.Utilities;
import it.mineblock.mbcore.Chat;
import it.mineblock.mbcore.MySQL;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Copyright © 2017 by Lorenzo Magni
 * All rights reserved. No part of this code may be reproduced, distributed, or transmitted in any form or by any means,
 * including photocopying, recording, or other electronic or mechanical methods, without the prior written permission
 * of the creator.
 */
public class Protect implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) {
            Chat.send(Message.NOT_PLAYER.get(), sender);
            return true;
        }

        Player player = (Player) sender;

        if(args.length > 2) {
            Chat.send(Message.INCORRECT_USAGE.getReplaced("{command}", "/protect help"), player);
            return true;
        }

        if(args.length == 0) {
            Block block = Utilities.getPlayerTargetBlock(player);
            
            if(block == null || block.getType().equals(Material.AIR)) {
                Chat.send(Message.PROTECT_POINTER.get(), player);
                return true;
            }

            if(!Utilities.canBeProtected(block, player)) {
                return true;
            }

            long x = (long) block.getX();
            long y = (long) block.getY();
            long z = (long) block.getZ();
            ArrayList values = new ArrayList();
            values.add(player.getName());
            values.add(player.getUniqueId());
            values.add(block.getType().name());
            values.add(x);
            values.add(y);
            values.add(z);

            MySQL.insertLine(Main.DB_PROTECTION, new String[] {"username", "uuid", "block", "x", "y", "z"}, values);
            Chat.send(Message.PROTECT_ADDED.getReplaced("{block}", block.getType().name()), player);
            return true;
        }

        String command = args[0];

        switch(command) {
            case "help":
                if(args.length != 1) {
                    Chat.send(Message.INCORRECT_USAGE.getReplaced("{command}", "/protect help"), player);
                    return true;
                }

                Chat.send(Chat.getTranslated("&eUtilizzo del comando &c/protect:"), player);
                Chat.send(Chat.getTranslated("&e/protect &c- &7Utilizzato puntando un blocco, protegge quel blocco."), player);
                Chat.send(Chat.getTranslated("&e/protect remove &c- &7Utilizzato puntando un blocco, rimuove la protezione al blocco."), player);
                Chat.send(Chat.getTranslated("&e/protect help &c- &7Mostra questa guida."), player);
                Chat.send(Chat.getTranslated("&e/protect private &c- &7Utilizzato puntando un blocco, imposta la protezione del blocco affinché possa essere aperto/rotto solo da te."), player);
                Chat.send(Chat.getTranslated("&e/protect public &c- &7Ripristina le tue impostazioni di protezione normali."), player);
                Chat.send(Chat.getTranslated("&e/protect add <player>  &c- &7Da il permesso ad un player di controllare il blocco protetto."), player);
                Chat.send(Chat.getTranslated("&e/protect remove <player> &c- &7Toglie il permesso ad un player di controllare il blocco protetto."), player);
                Chat.send(Chat.getTranslated("&e/protect team <allow/deny> &c- &7Da/toglie il permesso di accedre ai blocchi protetti al team"), player);
                Chat.send(Chat.getTranslated("&cI parametri tra queste parentesi,<>, sono obbligatori, quelli tra queste parentesi,[], sono facoltativi"), player);
                Chat.send(Chat.getTranslated("&cPer creare un team usa il comando &e/team help&c."), player);
                break;
            case "remove":
                if(args.length == 1) {
                    Block block = Utilities.getPlayerTargetBlock(player);
            
                    if(block == null || block.getType().equals(Material.AIR)) {
                        Chat.send(Message.PROTECT_POINTER.get(), player);
                        return true;
                    }

                    if(!Utilities.canBeControlled(block, player)) {
                        return true;
                    }

                    long x = (long) block.getX();
                    long y = (long) block.getY();
                    long z = (long) block.getZ();

                    MySQL.removeRow(Main.DB_PROTECTION, new String[] {"x", "y", "z"}, new String[] {String.valueOf(x), String.valueOf(y), String.valueOf(z)});
                    Chat.send(Message.PROTECT_REMOVED.getReplaced("{block}", block.getType().name()), player);
                }
                else if(args.length == 2) {
                    String username = args[1];

                    if(!MySQL.rowExists(Main.DB_USER, "username", username)) {
                        Chat.send(Message.PLAYER_NOT_FOUND.get(), player);
                        return true;
                    }

                    String allowedPlayer = MySQL.getString(Main.DB_USER, "uuid", player.getUniqueId().toString(), "allowedPlayer");

                    if(allowedPlayer == null || !allowedPlayer.equalsIgnoreCase(username)) {
                        Chat.send(Message.PROTECT_ALLOWED_NONE.get(), player);
                        return true;
                    }

                    MySQL.setString(Main.DB_USER, "allowedPlayer", "", "uuid", player.getUniqueId().toString());
                    Chat.send(Message.PROTECT_ALLOWED_REMOVED.get(), player);
                } else {
                    Chat.send(Message.INCORRECT_USAGE.getReplaced("{command}", "/protect help"), player);
                }
                break;
            case "add":
                if(args.length != 2) {
                    Chat.send(Message.INCORRECT_USAGE.getReplaced("{command}", "/protect help"), player);
                    return true;
                }

                String username = args[1];

                if(username.equalsIgnoreCase(player.getName())) {
                    Chat.send(Message.PROTECT_ALLOWED_ALREADY.get(), player);
                    return true;
                }

                if(!MySQL.rowExists(Main.DB_USER, "username", username)) {
                    Chat.send(Message.PLAYER_NOT_FOUND.get(), player);
                    return true;
                }

                String allowedPlayer = MySQL.getString(Main.DB_USER, "uuid", player.getUniqueId().toString(), "allowedPlayer");

                if(allowedPlayer == null) {
                    MySQL.setString(Main.DB_USER, "allowedPlayer", username, "uuid", player.getUniqueId().toString());
                    Chat.send(Message.PROTECT_ALLOWED_ADDED.get(), player);
                }
                else if(allowedPlayer.equalsIgnoreCase(username)) {
                    Chat.send(Message.PROTECT_ALLOWED_ALREADY.get(), player);
                    return true;
                }
                else if(!allowedPlayer.equalsIgnoreCase(username)) {
                    MySQL.setString(Main.DB_USER, "allowedPlayer", username, "uuid", player.getUniqueId().toString());
                    Chat.send(Message.PROTECT_ALLOWED_ADDED.get(), player);
                    Chat.send(Message.PROTECT_ALLOWED_OVERRIDE.getReplaced("{player}", allowedPlayer), player);
                }
                break;
            case "team":
                if(args.length == 2) {
                    String param = args[1];
                    switch(param) {
                        case "allow":
                            MySQL.setBoolean(Main.DB_USER, "allowTeam", true, "uuid", player.getUniqueId().toString());
                            Chat.send(Message.PROTECT_TEAM_ALLOWED.get(), player);
                            break;
                        case "deny":
                            MySQL.setBoolean(Main.DB_USER, "allowTeam", false, "uuid", player.getUniqueId().toString());
                            Chat.send(Message.PROTECT_TEAM_DENIED.get(), player);
                            break;
                        default:
                            Chat.send(Message.INCORRECT_PARAM.get(), player);
                            break;
                    }
                } else {
                    Chat.send(Message.INCORRECT_USAGE.getReplaced("{command}", "/protect help"), player);
                }
                break;
            case "private":
                Block privateBlock = Utilities.getPlayerTargetBlock(player);

                if(privateBlock == null || privateBlock.getType().equals(Material.AIR)) {
                    Chat.send(Message.PROTECT_POINTER.get(), player);
                    return true;
                }

                if(!Utilities.canBeControlled(privateBlock, player)) {
                    return true;
                }

                long xPr = (long) privateBlock.getX();
                long yPr = (long) privateBlock.getY();
                long zPr = (long) privateBlock.getZ();

                MySQL.setBoolean(Main.DB_PROTECTION, "private", true, new String[] {"x", "y", "z"}, new String[] {String.valueOf(xPr), String.valueOf(yPr), String.valueOf(zPr)});
                Chat.send(Message.PROTECT_PRIVATE.getReplaced("{block}", privateBlock.getType().name()), player);
                break;
            case "public":
                Block publicBlock = Utilities.getPlayerTargetBlock(player);

                if(publicBlock == null || publicBlock.getType().equals(Material.AIR)) {
                    Chat.send(Message.PROTECT_POINTER.get(), player);
                    return true;
                }

                if(!Utilities.canBeControlled(publicBlock, player)) {
                    return true;
                }

                long xPu = (long) publicBlock.getX();
                long yPu = (long) publicBlock.getY();
                long zPu = (long) publicBlock.getZ();

                MySQL.setBoolean(Main.DB_PROTECTION, "private", false, new String[] {"x", "y", "z"}, new String[] {String.valueOf(xPu), String.valueOf(yPu), String.valueOf(zPu)});
                Chat.send(Message.PROTECT_PUBLIC.getReplaced("{block}", publicBlock.getType().name()), player);
                break;
            default:
                Chat.send(Message.INCORRECT_USAGE.getReplaced("{command}", "/protect help"), player);
                break;
        }

        return true;
    }
}