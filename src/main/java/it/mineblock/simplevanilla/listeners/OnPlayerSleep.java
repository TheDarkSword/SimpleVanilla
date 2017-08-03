package it.mineblock.simplevanilla.listeners;

import it.mineblock.mbcore.MySQL;
import it.mineblock.simplevanilla.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

/**
 * Copyright © 2017 by Lorenzo Magni
 * All rights reserved. No part of this code may be reproduced, distributed, or transmitted in any form or by any means,
 * including photocopying, recording, or other electronic or mechanical methods, without the prior written permission
 * of the creator.
 */
public class OnPlayerSleep implements Listener {

    @EventHandler
    public void onPlayerSleep(PlayerBedEnterEvent event) {
        long x = event.getBed().getX();
        long y = event.getBed().getY();
        long z = event.getBed().getZ();

        String spawnpoint = x + "," + y + "," + z;

        MySQL.setString(Main.DB_USER, "spawnpoint", spawnpoint, "uuid", event.getPlayer().getUniqueId().toString());
    }
}
