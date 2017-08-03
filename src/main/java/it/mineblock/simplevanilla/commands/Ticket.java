package it.mineblock.simplevanilla.commands;

import it.mineblock.mbcore.Chat;
import it.mineblock.mbcore.MySQL;
import it.mineblock.simplevanilla.Main;
import it.mineblock.simplevanilla.Message;
import it.mineblock.simplevanilla.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
public class Ticket implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) {
            Chat.send(Message.NOT_PLAYER.get(), sender);
            return true;
        }

        Player player = (Player) sender;

        if(player.hasPermission(Permissions.TICKET_STAFF.get())) {
            if(args.length < 1 || args.length > 2) {
                Chat.send(Message.INCORRECT_USAGE.getReplaced("{command}", "/ticket help"), player);
                return true;
            }

            String command = args[0];

            int id;

            switch(command) {
                case "help":
                    if(args.length != 1) {
                        Chat.send(Message.INCORRECT_USAGE.getReplaced("{command}", "/ticket help"), player);
                        return true;
                    }

                    Chat.send(Chat.getTranslated("&eUtilizzo del comando &c/ticket:"), player);
                    Chat.send(Chat.getTranslated("&e/ticket help &c- &7Mostra questa guida."), player);
                    Chat.send(Chat.getTranslated("&e/ticket list &c- &7Mostra una lista dei ticket attualmente aperti."), player);
                    Chat.send(Chat.getTranslated("&e/ticket solve <numero> &c- &7Porta al luogo in cui è stato aperto il ticket per risolverlo, (da invulnerabilità)."), player);
                    Chat.send(Chat.getTranslated("&e/ticket close [numero] &c- &7Chiude il ticket e lo rimuove dalla lista, (riporta al punto di partenza)."), player);
                    Chat.send(Chat.getTranslated("&e/ticket unsolve [numero] &c- &7Riporta al punto di partenza senza chiudere il ticket."), player);
                    Chat.send(Chat.getTranslated("&cI parametri tra queste parentesi,<>, sono obbligatori, quelli tra queste parentesi,[], sono facoltativi"), player);
                    break;
                case "list":
                    if(args.length != 1) {
                        Chat.send(Message.INCORRECT_USAGE.getReplaced("{command}", "/ticket help"), player);
                        return true;
                    }

                    ArrayList tickets = MySQL.getArrayList(Main.DB_TICKET, "active", "1", "id");

                    Chat.send(Chat.getTranslated("&9-------- &f&lTicket List &9--------"), player);

                    for(int i = 0; i < 16; i++) {
                        if(!MySQL.rowExists(Main.DB_TICKET, "id", String.valueOf(tickets.get(i)))) {
                            break;
                        }
                        String username = MySQL.getString(Main.DB_TICKET, "id", String.valueOf(tickets.get(i)), "username");
                        long x = MySQL.getLong(Main.DB_TICKET, "id", String.valueOf(tickets.get(i)), "x");
                        long y = MySQL.getLong(Main.DB_TICKET, "id", String.valueOf(tickets.get(i)), "y");
                        long z = MySQL.getLong(Main.DB_TICKET, "id", String.valueOf(tickets.get(i)), "z");
                        String message = MySQL.getString(Main.DB_TICKET, "id", String.valueOf(tickets.get(i)), "message");

                        String output = Message.TICKET_LIST.get();
                        output = output.replace("{code}", String.valueOf(tickets.get(i)));
                        output = output.replace("{player}", username);
                        output = output.replace("{x}", String.valueOf(x));
                        output = output.replace("{y}", String.valueOf(y));
                        output = output.replace("{z}", String.valueOf(z));
                        output = output.replace("{message}", String.valueOf(message));

                        Chat.send(output, player);
                    }

                    Chat.send(Chat.getTranslated("&9--------             &9--------"), player);

                    break;
                case "solve":
                    if(args.length != 2) {
                        Chat.send(Message.INCORRECT_USAGE.getReplaced("{command}", "/ticket help"), player);
                        return true;
                    }

                    if(Main.staffTicket.containsKey(player)) {
                        Chat.send(Message.TICKET_SOLVING.get(), player);
                        return true;
                    }

                    try {
                        id = Integer.parseInt(args[1]);
                    } catch (Exception e) {
                        Chat.send(Message.INCORRECT_PARAM.get(), player);
                        return true;
                    }

                    long x = MySQL.getLong(Main.DB_TICKET, "id", String.valueOf(id), "x");
                    long y = MySQL.getLong(Main.DB_TICKET, "id", String.valueOf(id), "y");
                    long z = MySQL.getLong(Main.DB_TICKET, "id", String.valueOf(id), "z");
                    Location staffLocation = player.getLocation();
                    Location ticketLocation = new Location(player.getWorld(), (double) x, (double) y, (double) z);
                    String username = MySQL.getString(Main.DB_TICKET, "id", String.valueOf(id), "username");
                    String message = MySQL.getString(Main.DB_TICKET, "id", String.valueOf(id), "message");

                    String output = Message.TICKET_SOLVE.get();
                    output = output.replace("{code}", String.valueOf(id));
                    output = output.replace("{player}", username);
                    output = output.replace("{message}", message);
                    String notify = Message.TICKET_SOLVING_NOTIFY.get();
                    notify = notify.replace("{player}", player.getName());
                    notify = notify.replace("{code}", String.valueOf(id));

                    Main.staffTicket.put(player, id);
                    Main.locationBackup.put(player, staffLocation);

                    player.setInvulnerable(true);
                    player.setCanPickupItems(false);
                    player.teleport(ticketLocation);
                    Chat.send(output, player);

                    for(Player receiver : Bukkit.getOnlinePlayers()) {
                        if(receiver.hasPermission(Permissions.TICKET_STAFF.get()) && receiver.getUniqueId() != player.getUniqueId()) {
                            Chat.send(notify, receiver);
                        }
                    }

                    break;
                case "close":
                    if(args.length == 1) {
                        if(!Main.staffTicket.containsKey(player)) {
                            Chat.send(Message.TICKET_NOTHING.get(), player);
                            return true;
                        }

                        id = Main.staffTicket.get(player);
                        Location location = Main.locationBackup.get(player);

                        Main.staffTicket.remove(player);
                        Main.locationBackup.remove(player);
                        player.setInvulnerable(false);
                        player.setCanPickupItems(true);
                        player.teleport(location);
                    } else {
                        try {
                            id = Integer.parseInt(args[1]);
                        } catch (Exception e) {
                            Chat.send(Message.INCORRECT_PARAM.get(), player);
                            return true;
                        }

                        if(Main.staffTicket.containsKey(player)) {
                            Location location = Main.locationBackup.get(player);

                            Main.staffTicket.remove(player);
                            Main.locationBackup.remove(player);
                            player.setInvulnerable(false);
                            player.setCanPickupItems(true);
                            player.teleport(location);
                        }
                    }

                    MySQL.setBoolean(Main.DB_TICKET, "active", false, "id", id);

                    Chat.send(Message.TICKET_SOLVED.getReplaced("{code}", String.valueOf(id)), player);
                    break;
                case "unsolve":
                    if(!Main.staffTicket.containsKey(player)) {
                        Chat.send(Message.TICKET_NOTHING.get(), player);
                        return true;
                    }

                    if(args.length == 1) {
                        id = Main.staffTicket.get(player);
                    } else {
                        try {
                            id = Integer.parseInt(args[1]);
                        } catch (Exception e) {
                            Chat.send(Message.INCORRECT_PARAM.get(), player);
                            return true;
                        }

                        if(id != Main.staffTicket.get(player)) {
                            Chat.send(Message.TICKET_UNSOLVE.get(), player);
                            return true;
                        }
                    }

                    Location location = Main.locationBackup.get(player);

                    Main.staffTicket.remove(player);
                    Main.locationBackup.remove(player);
                    player.setInvulnerable(false);
                    player.setCanPickupItems(true);
                    player.teleport(location);

                    Chat.send(Message.TICKET_UNSOLVED.getReplaced("{code}", String.valueOf(id)), player);
                    break;
                default:
                    Chat.send(Message.INCORRECT_USAGE.getReplaced("{command}", "/ticket help"), player);
                    break;
            }

        } else {
            if(args.length < 1) {
                Chat.send(Message.INCORRECT_USAGE.getReplaced("{command}", "/ticket <messaggio>"), player);
                return true;
            }

            String message = Chat.builder(args);
            Location location = player.getLocation();
            long jid = (long) Math.random() * 1000000;
            ArrayList values = new ArrayList();
            values.add(player.getName());
            values.add(player.getUniqueId());
            values.add((long) location.getX());
            values.add((long) location.getY());
            values.add((long) location.getZ());
            values.add(message);
            values.add(jid);

            MySQL.insertLine(Main.DB_TICKET, new String[] {
                    "username",
                    "uuid",
                    "x",
                    "y",
                    "z",
                    "message",
                    "jid"
            }, values);

            int id = MySQL.getInt(Main.DB_TICKET, "jid", String.valueOf(jid), "id");
            MySQL.setInt(Main.DB_TICKET, "jid", 0, "id", id);

            String outputStaff = Message.TICKET_NOTIFY.get();
            outputStaff = outputStaff.replace("{code}", String.valueOf(id));
            outputStaff = outputStaff.replace("{player}", player.getName());
            outputStaff = outputStaff.replace("{message}", message);

            for(Player receiver : Bukkit.getOnlinePlayers()) {
                if(receiver.hasPermission(Permissions.TICKET_STAFF.get())) {
                    Chat.send(outputStaff, receiver);
                }
            }

            Chat.send(Message.TICKET_OPEN.get().replace("{code}", String.valueOf(id)), player);
        }

        return true;
    }
}