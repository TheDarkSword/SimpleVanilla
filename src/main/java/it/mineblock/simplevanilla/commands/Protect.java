package it.mineblock.simplevanilla.commands;

import it.mineblock.simplevanilla.Main;
import it.mineblock.simplevanilla.Message;
import it.mineblock.simplevanilla.Utilities;
import it.mineblock.mbcore.Chat;
import it.mineblock.mbcore.MySQL;
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

        if(args.length > 3) {
            Chat.send(Message.INCORRECT_USAGE.getReplaced("{command}", "/protect help"), player);
            return true;
        }

        if(args.length == 0) {
            Block block = Utilities.getPlayerTargetBlock(player);
            if(!Utilities.isBlockCompatible(block)) {
                Chat.send(Message.PROTECTION_IMPOSSIBLE.get(), player);
                return true;
            }

            if(!Utilities.canBeProtected(block)) {
                Chat.send(Message.PROTECTION_IMPOSSIBLE.get(), player);
                return true;
            }

            if(Utilities.isProtected(block)) {
                Chat.send(Message.PROTECTION_EXISTENT.get(), player);
                return true;
            }

            long x = block.getX();
            long y = block.getY();
            long z = block.getZ();
            ArrayList values = new ArrayList();
            values.add(player.getName());
            values.add(player.getUniqueId());
            values.add(x);
            values.add(y);
            values.add(z);

            MySQL.insertLine(Main.DB_PROTECTION, new String[] {"username", "uuid", "x", "y", "z"}, values);
            Chat.send(Message.PROTECTION_ADDED.getReplaced("{block}", block.getType().name()), player);
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
                Chat.send(Chat.getTranslated("&e/protect team <open/destroy/control> <allow/deny> &c- &7Permette di modificare le impostazioni di protezione dei tuoi blocchi."), player);
                Chat.send(Chat.getTranslated("&cI parametri tra queste parentesi,<>, sono obbligatori, quelli tra queste parentesi,[], sono facoltativi"), player);
                Chat.send(Chat.getTranslated("&cPer creare un team usa il comando &e/team&c."), player);
                break;
            case "remove":
                if(args.length == 1) {
                    Block block = Utilities.getPlayerTargetBlock(player);
                    if(!Utilities.isBlockCompatible(block)) {

                        Chat.send(Message.PROTECTION_IMPOSSIBLE.get(), player);
                        return true;
                    }

                    if(!Utilities.canBeUnprotected(block, player)) {
                        /*@TODO Message*/
                        Chat.send(Message.PROTECTION_IMPOSSIBLE.get(), player);
                        return true;
                    }

                    if(!Utilities.isProtected(block)) {
                        /*@TODO Message*/
                        Chat.send(Message.PROTECTION_INEXISTENT.get(), player);
                        return true;
                    }

                    long x = block.getX();
                    long y = block.getY();
                    long z = block.getZ();

                    MySQL.removeRow(Main.DB_PROTECTION, new String[] {"x", "y", "z"}, new String[] {String.valueOf(x), String.valueOf(y), String.valueOf(z)});
                    Chat.send(Message.PROTECTION_ADDED.getReplaced("{block}", block.getType().name()), player);
                }
                else if(args.length == 2) {
                    String username = args[1];

                    if(!MySQL.rowExists(Main.DB_USER, "username", username)) {
                        Chat.send(Message.PLAYER_NOT_FOUND.get(), player);
                        return true;
                    }

                    String allowedPlayer = MySQL.getString(Main.DB_USER, "uuid", player.getUniqueId().toString(), "allowedPlayer");

                    if(allowedPlayer == null || !allowedPlayer.equalsIgnoreCase(username)) {
                        /*@TODO Message*/
                        Chat.send(Message.PROTECTION_NOTHING.get(), player);
                        return true;
                    }

                    MySQL.setString(Main.DB_USER, "allowedPlayer", "", "uuid", player.getUniqueId().toString());
                    /*@TODO Message*/
                    Chat.send(Message.PROTECTION_REMOVED_FRIEND.get(), player);
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

                if(!MySQL.rowExists(Main.DB_USER, "username", username)) {
                    Chat.send(Message.PLAYER_NOT_FOUND.get(), player);
                    return true;
                }

                String allowedPlayer = MySQL.getString(Main.DB_USER, "uuid", player.getUniqueId().toString(), "allowedPlayer");

                if(allowedPlayer == null) {
                    MySQL.setString(Main.DB_USER, "allowedPlayer", username, "uuid", player.getUniqueId().toString());
                    Chat.send(Message.PROTECTION_ADDED_FRIEND.get(), player);
                }
                else if(allowedPlayer.equalsIgnoreCase(username)) {
                    /*@TODO Message nothing to do*/
                    return true;
                }
                else if(!allowedPlayer.equalsIgnoreCase(username)) {
                    /*@TODO Send error message before proceiding*/
                    MySQL.setString(Main.DB_USER, "allowedPlayer", username, "uuid", player.getUniqueId().toString());
                    Chat.send(Message.PROTECTION_ADDED_FRIEND.get(), player);
                }
                break;
            case "team":
                if(args.length == 2) {
                    String param = args[1];
                    switch(param) {
                        case "allow":
                            MySQL.setBoolean(Main.DB_USER, "allowTeam", true, "uuid", player.getUniqueId().toString());
                            /*@TODO send message*/
                            break;
                        case "deny":
                            MySQL.setBoolean(Main.DB_USER, "allowTeam", false, "uuid", player.getUniqueId().toString());
                            /*@TODO send message*/
                            break;
                        default:
                            Chat.send(Message.INCORRECT_PARAM.get(), player);
                            break;
                    }
                }
                else if(args.length == 3) {
                    String param = args[1];
                    String value = args[2];
                    switch(param) {
                        case "open":
                            if(value.equalsIgnoreCase("allow")) {
                                MySQL.setString(Main.DB_USER, "teamSettings", Utilities.codeBuilder(player, "open", "a"), "uuid", player.getUniqueId().toString());
                                /*@TODO send message*/
                            }
                            else if(value.equalsIgnoreCase("deny")) {
                                MySQL.setString(Main.DB_USER, "teamSettings", Utilities.codeBuilder(player, "open", "d"), "uuid", player.getUniqueId().toString());
                                /*@TODO send message*/
                            } else {
                                Chat.send(Message.INCORRECT_PARAM.get(), player);
                                return true;
                            }
                            break;
                        case "destroy":
                            if(value.equalsIgnoreCase("allow")) {
                                MySQL.setString(Main.DB_USER, "teamSettings", Utilities.codeBuilder(player, "destroy", "a"), "uuid", player.getUniqueId().toString());
                                /*@TODO send message*/
                            }
                            else if(value.equalsIgnoreCase("deny")) {
                                MySQL.setString(Main.DB_USER, "teamSettings", Utilities.codeBuilder(player, "destroy", "d"), "uuid", player.getUniqueId().toString());
                                /*@TODO send message*/
                            } else {
                                Chat.send(Message.INCORRECT_PARAM.get(), player);
                                return true;
                            }
                            break;
                        case "control":
                            if(value.equalsIgnoreCase("allow")) {
                                MySQL.setString(Main.DB_USER, "teamSettings", Utilities.codeBuilder(player, "control", "a"), "uuid", player.getUniqueId().toString());
                                /*@TODO send message*/
                            }
                            else if(value.equalsIgnoreCase("deny")) {
                                MySQL.setString(Main.DB_USER, "teamSettings", Utilities.codeBuilder(player, "control", "d"), "uuid", player.getUniqueId().toString());
                                /*@TODO send message*/
                            } else {
                                Chat.send(Message.INCORRECT_PARAM.get(), player);
                                return true;
                            }
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
                if(!Utilities.isBlockCompatible(privateBlock)) {

                    Chat.send(Message.PROTECTION_IMPOSSIBLE.get(), player);
                    return true;
                }

                if(!Utilities.canBeUnprotected(privateBlock)) {
                        /*@TODO Message*/
                    Chat.send(Message.PROTECTION_IMPOSSIBLE.get(), player);
                    return true;
                }

                if(!Utilities.isProtected(privateBlock)) {
                        /*@TODO Message*/
                    Chat.send(Message.PROTECTION_INEXISTENT.get(), player);
                    return true;
                }

                long xPr = privateBlock.getX();
                long yPr = privateBlock.getY();
                long zPr = privateBlock.getZ();

                MySQL.setBoolean(Main.DB_PROTECTION, "private", true, new String[] {"x", "y", "z"}, new String[] {String.valueOf(xPr), String.valueOf(yPr), String.valueOf(zPr)});
                /*@TODO Message*/
                break;
            case "public":
                Block publicBlock = Utilities.getPlayerTargetBlock(player);
                if(!Utilities.isBlockCompatible(publicBlock)) {

                    Chat.send(Message.PROTECTION_IMPOSSIBLE.get(), player);
                    return true;
                }

                if(!Utilities.canBeUnprotected(publicBlock)) {
                        /*@TODO Message*/
                    Chat.send(Message.PROTECTION_IMPOSSIBLE.get(), player);
                    return true;
                }

                if(!Utilities.isProtected(publicBlock)) {
                        /*@TODO Message*/
                    Chat.send(Message.PROTECTION_INEXISTENT.get(), player);
                    return true;
                }

                long xPu = publicBlock.getX();
                long yPu = publicBlock.getY();
                long zPu = publicBlock.getZ();

                MySQL.setBoolean(Main.DB_PROTECTION, "private", true, new String[] {"x", "y", "z"}, new String[] {String.valueOf(xPu), String.valueOf(yPu), String.valueOf(zPu)});
                /*@TODO Message*/
                break;
            default:
                Chat.send(Message.INCORRECT_USAGE.getReplaced("{command}", "/protect help"), player);
                break;
        }

        return true;
    }
}