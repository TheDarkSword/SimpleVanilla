package it.mineblock.simplevanilla.listeners;

import it.mineblock.simplevanilla.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Copyright © 2017 by Lorenzo Magni
 * All rights reserved. No part of this code may be reproduced, distributed, or transmitted in any form or by any means,
 * including photocopying, recording, or other electronic or mechanical methods, without the prior written permission
 * of the creator.
 */
public class OnPlayerLeave implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(Main.staffTicket.containsKey(player)) {
            Main.staffTicket.remove(player);
        }
        if(Main.locationBackup.containsKey(player)) {
            Location location = Main.locationBackup.get(player);
            Main.locationBackup.remove(player);
            player.setInvulnerable(false);
            player.setCanPickupItems(true);
            player.teleport(location);
        }
    }
}