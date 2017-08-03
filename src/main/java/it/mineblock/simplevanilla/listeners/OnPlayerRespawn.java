package it.mineblock.simplevanilla.listeners;

import it.mineblock.mbcore.MySQL;
import it.mineblock.simplevanilla.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Copyright © 2017 by Lorenzo Magni
 * All rights reserved. No part of this code may be reproduced, distributed, or transmitted in any form or by any means,
 * including photocopying, recording, or other electronic or mechanical methods, without the prior written permission
 * of the creator.
 */
public class OnPlayerRespawn implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        String spawnpoint = MySQL.getString(Main.DB_USER, "uuid", player.getUniqueId().toString(), "spawnpoint");
        if(spawnpoint != null && !spawnpoint.isEmpty()) {
            String[] coords = spawnpoint.split(",");
            double x = Long.parseLong(coords[0]);
            double y = Long.parseLong(coords[1]) + 0.5;
            double z = Long.parseLong(coords[2]);
            Location location = new Location(player.getWorld(), x, y, z);

            event.setRespawnLocation(location);
        }
    }
}
