package it.mineblock.simplevanilla.listeners;

import it.mineblock.mbcore.MySQL;
import it.mineblock.simplevanilla.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Copyright © 2017 by Lorenzo Magni
 * All rights reserved. No part of this code may be reproduced, distributed, or transmitted in any form or by any means,
 * including photocopying, recording, or other electronic or mechanical methods, without the prior written permission
 * of the creator.
 */
public class OnPlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Date date = new Date();
        ArrayList values = new ArrayList();

        values.add(player.getName());
        values.add(uuid.toString());
        values.add(Main.TIMESTAMP.format(date));

        if(!MySQL.rowExists(Main.DB_USER, "uuid", uuid.toString())) {
            MySQL.insertLine(Main.DB_USER, new String[] {"username", "uuid", "first_login"}, values);
        }
    }
}