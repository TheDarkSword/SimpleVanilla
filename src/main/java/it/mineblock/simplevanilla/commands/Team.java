package it.mineblock.simplevanilla.commands;

import it.mineblock.mbcore.Chat;
import it.mineblock.mbcore.MySQL;
import it.mineblock.simplevanilla.Main;
import it.mineblock.simplevanilla.Message;
import it.mineblock.simplevanilla.Permissions;
import org.bukkit.Bukkit;
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
public class Team implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) {
            Chat.send(Message.NOT_PLAYER.get(), sender);
            return true;
        }

        Player player = (Player) sender;

        if(args.length == 0 || args.length > 4) {
            Chat.send(Message.INCORRECT_USAGE.getReplaced("{command}", "/team help"), player);
            return true;
        }

        String command = args[0];
        String team = null;
        String username = null;
        Player receiver = null;

        switch(command) {
            case "help":
                if(args.length != 1) {
                    Chat.send(Message.INCORRECT_USAGE.getReplaced("{command}", "/team help"), player);
                    return true;
                }

                Chat.send(Chat.getTranslated("&eUtilizzo del comando &c/team:"), player);
                Chat.send(Chat.getTranslated("&e/team create <nome> &c- &7Permette la creazione di un team."), player);
                Chat.send(Chat.getTranslated("&e/team disband [team] &c- &7Permette la cancellazione di un team. Il parametro team tra le parentesi quadrate è ad uso dello staff."), player);
                Chat.send(Chat.getTranslated("&e/team add <player> [team] [force] &c- &7Permette di aggiungere un player al team. I parametri tra le parentesi quadrate sono ad uso dello staff."), player);
                Chat.send(Chat.getTranslated("&e/team accept &c- &7Accetta una richiesta di team."), player);
                Chat.send(Chat.getTranslated("&e/team remove <player> [team] &c- &7Permette di rimuovere un player dal team. Il parametro tra le parentesi quadrate è ad uso dello staff."), player);
                Chat.send(Chat.getTranslated("&cI parametri tra queste parentesi,<>, sono obbligatori, quelli tra queste parentesi,[], sono facoltativi"), player);
                break;
            case "accept":
                if(args.length != 1) {
                    Chat.send(Message.INCORRECT_USAGE.getReplaced("{command}", "/team help"), player);
                    return true;
                }

                team = MySQL.getString(Main.DB_USER, "uuid", player.getUniqueId().toString(), "teamPending");

                if(team == null || team.isEmpty()) {
                    Chat.send(Message.TEAM_PENDING_NONE.get(), player);
                    return true;
                }

                if(!MySQL.rowExists(Main.DB_TEAM, "name", team)) {
                    Chat.send(Message.TEAM_INEXISTENT.get(), player);
                    return true;
                }

                MySQL.setString(Main.DB_USER, "team", team, "uuid", player.getUniqueId().toString());
                MySQL.setString(Main.DB_USER, "teamPending", "", "uuid", player.getUniqueId().toString());

                Chat.send(Message.TEAM_JOIN.getReplaced("{name}", team), player);

                receiver = Bukkit.getPlayer(MySQL.getString(Main.DB_TEAM, "name", team, "leader"));
                if(receiver != null) {
                    Chat.send(Message.TEAM_JOINED.getReplaced("{player}", player.getName()), receiver);
                }
                break;
            case "create":
                if(args.length != 2) {
                    Chat.send(Message.INCORRECT_USAGE.getReplaced("{command}", "/team help"), player);
                    return true;
                }

                team = args[1];

                if(MySQL.rowExists(Main.DB_TEAM, "name", team)) {
                    Chat.send(Message.TEAM_EXISTS.get(), player);
                    return true;
                }

                ArrayList<String> values = new ArrayList<>();
                values.add(team);
                values.add(player.getName());

                MySQL.insertLine(Main.DB_TEAM, new String[] {"name", "leader"}, values);
                MySQL.setString(Main.DB_USER, "team", team, "uuid", player.getUniqueId().toString());
                Chat.send(Message.TEAM_CREATED.getReplaced("{name}", team), player);
                break;
            case "disband":
                if(args.length > 2) {
                    Chat.send(Message.INCORRECT_USAGE.getReplaced("{command}", "/team help"), player);
                    return true;
                }

                if(args.length == 1) {

                    team = MySQL.getString(Main.DB_USER, "username", player.getName(), "team");

                    if(team == null || team.isEmpty()) {
                        Chat.send(Message.TEAM_NOTHING.get(), player);
                        return true;
                    }

                    if (!player.hasPermission(Permissions.TEAM_STAFF.get())) {
                        if (!MySQL.getString(Main.DB_TEAM, "name", team, "leader").equalsIgnoreCase(player.getName())) {
                            Chat.send(Message.TEAM_DENIED_PERM.get(), player);
                            return true;
                        }
                    }

                    MySQL.removeRow(Main.DB_TEAM, "name", team);
                    MySQL.setString(Main.DB_USER, "team", "", "team", team);
                } else {
                    team = args[1];

                    if(!player.hasPermission(Permissions.TEAM_STAFF.get())) {
                        Chat.send(Message.INSUFFICIENT_PREMISSION.get(), player);
                        return true;
                    }

                    if (!MySQL.rowExists(Main.DB_TEAM, "name", team)) {
                        Chat.send(Message.TEAM_INEXISTENT.get(), player);
                        return true;
                    }

                    MySQL.removeRow(Main.DB_TEAM, "name", team);
                    MySQL.setString(Main.DB_USER, "team", "", "team", team);
                }
                Chat.send(Message.TEAM_DISBANDED.get(), player);
                break;
            case "add":
                boolean force = false;
                if(args.length == 2) {
                    username = args[1];

                    if (!MySQL.rowExists(Main.DB_USER, "username", username)) {
                        Chat.send(Message.PLAYER_NOT_FOUND.get(), player);
                        return true;
                    }

                    team = MySQL.getString(Main.DB_USER, "uuid", player.getUniqueId().toString(), "team");

                    if (team == null || team.isEmpty()) {
                        Chat.send(Message.TEAM_NOTHING.get(), player);
                        return true;
                    }

                    if (!MySQL.getString(Main.DB_TEAM, "name", team, "leader").equalsIgnoreCase(player.getName())) {
                        Chat.send(Message.TEAM_DENIED_PERM.get(), player);
                        return true;
                    }

                    if (!(MySQL.getString(Main.DB_USER, "username", username, "team") == null) && !MySQL.getString(Main.DB_USER, "username", username, "team").isEmpty()) {
                        Chat.send(Message.TEAM_ALREADY.get(), player);
                        return true;
                    }

                    MySQL.setString(Main.DB_USER, "teamPending", team, "username", username);
                    Chat.send(Message.TEAM_INVITE_SENT.getReplaced("{player}", username), player);

                    receiver = Bukkit.getPlayer(username);
                    if (receiver != null) {
                        Chat.send(Message.TEAM_INVITE.getReplaced("{name}", team), receiver);
                    }
                }

                if(args.length >= 3) {
                    username = args[1];
                    team = args[2];
                    if(args.length == 4) {
                        if (args[3].equalsIgnoreCase("true")) {
                            force = true;
                        } else {
                            force = false;
                        }
                    }

                    if (!MySQL.rowExists(Main.DB_USER, "username", username)) {
                        Chat.send(Message.PLAYER_NOT_FOUND.get(), player);
                        return true;
                    }

                    if(!MySQL.rowExists(Main.DB_TEAM, "name", team)) {
                        Chat.send(Message.TEAM_INEXISTENT.get(), player);
                        return true;
                    }

                    if(force) {
                        MySQL.setString(Main.DB_USER, "team", team, "uuid", player.getUniqueId().toString());
                        MySQL.setString(Main.DB_USER, "teamPending", "", "uuid", player.getUniqueId().toString());

                        Player receiver1 = Bukkit.getPlayer(username);
                        if(receiver1 != null) {
                            Chat.send(Message.TEAM_JOIN.getReplaced("{name}", team), receiver1);
                        }

                        receiver = Bukkit.getPlayer(MySQL.getString(Main.DB_TEAM, "name", team, "leader"));
                        if(receiver != null) {
                            Chat.send(Message.TEAM_JOINED.getReplaced("{player}", player.getName()), receiver);
                        }
                    } else {
                        MySQL.setString(Main.DB_USER, "teamPending", team, "username", username);
                        Chat.send(Message.TEAM_INVITE_SENT.getReplaced("{player}", username), player);

                        receiver = Bukkit.getPlayer(username);
                        if (receiver != null) {
                            Chat.send(Message.TEAM_INVITE.getReplaced("{name}", team), receiver);
                        }
                    }
                }
                break;
            case "remove":
                if(player.hasPermission(Permissions.TEAM_STAFF.get())) {
                    team = args[2];
                } else {
                    team = MySQL.getString(Main.DB_USER, "uuid", player.getUniqueId().toString(), "team");
                }

                if(args.length == 2 || args.length == 3) {
                    Chat.send(Message.INCORRECT_USAGE.getReplaced("{command}", "/team help"), player);
                    return true;
                }

                username = args[1];

                if(!MySQL.rowExists(Main.DB_USER, "username", username)) {
                    Chat.send(Message.PLAYER_NOT_FOUND.get(), player);
                    return true;
                }

                if(team == null || team.isEmpty()) {
                    Chat.send(Message.TEAM_NOTHING.get(), player);
                    return true;
                }

                if(!MySQL.getString(Main.DB_TEAM, "name", team, "leader").equalsIgnoreCase(player.getName())) {
                    Chat.send(Message.TEAM_DENIED_PERM.get(), player);
                    return true;
                }

                if((!(MySQL.getString(Main.DB_USER, "username", username, "team") == null) && !MySQL.getString(Main.DB_USER, "username", username, "team").isEmpty()) || !MySQL.getString(Main.DB_USER, "username", username, "team").equalsIgnoreCase(team)) {
                    Chat.send(Message.TEAM_NOT_YOUR.get(), player);
                    return true;
                }

                MySQL.setString(Main.DB_USER, "team", "", "username", username);
                Chat.send(Message.TEAM_REMOVE.getReplaced("{player}", username), player);

                receiver = Bukkit.getPlayer(username);
                if(receiver != null) {
                    Chat.send(Message.TEAM_REMOVED.getReplaced("{name}", team), receiver);
                }
                break;
            default:
                Chat.send(Message.INCORRECT_USAGE.getReplaced("{command}", "/team help"), player);
                break;
        }

        return true;
    }
}
