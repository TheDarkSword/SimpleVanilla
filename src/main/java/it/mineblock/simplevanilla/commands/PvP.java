package it.mineblock.simplevanilla.commands;

import it.mineblock.mbcore.Chat;
import it.mineblock.mbcore.MySQL;
import it.mineblock.simplevanilla.Main;
import it.mineblock.simplevanilla.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Copyright © 2017 by Lorenzo Magni
 * All rights reserved. No part of this code may be reproduced, distributed, or transmitted in any form or by any means,
 * including photocopying, recording, or other electronic or mechanical methods, without the prior written permission
 * of the creator.
 */
public class PvP implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            Chat.send(Message.NOT_PLAYER.get(), sender);
            return true;
        }

        Player player = (Player) sender;

        if(args.length != 0) {
            Chat.send(Message.INCORRECT_USAGE.getReplaced("{command}", "/pvp"), player);
            return true;
        }

        UUID uuid = player.getUniqueId();

        if(MySQL.getBoolean(Main.DB_USER, "uuid", uuid.toString(), "pvp")) {
            MySQL.setBoolean(Main.DB_USER, "pvp", false, "uuid", uuid.toString());
            Chat.send(Message.PVP_DEACTIVED.get(), player);
        } else {
            MySQL.setBoolean(Main.DB_USER, "pvp", true, "uuid", uuid.toString());
            Chat.send(Message.PVP_ACTIVED.get(), player);
        }

        return true;
    }
}