package it.mineblock.simplevanilla.listeners;

import it.mineblock.mbcore.MySQL;
import it.mineblock.simplevanilla.Main;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Copyright © 2017 by Lorenzo Magni
 * All rights reserved. No part of this code may be reproduced, distributed, or transmitted in any form or by any means,
 * including photocopying, recording, or other electronic or mechanical methods, without the prior written permission
 * of the creator.
 */
public class OnPlayerPvP implements Listener {

    @EventHandler
    public void onPlayerPvP(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        if((damager instanceof Player) && (damaged instanceof Player)) {
            if(!MySQL.getBoolean(Main.DB_USER, "uuid", damager.getUniqueId().toString(), "pvp") || !MySQL.getBoolean(Main.DB_USER, "uuid", damaged.getUniqueId().toString(), "pvp")) {
                event.setCancelled(true);
            }
        }

        if((damager instanceof Arrow) && (damaged instanceof Player)) {
            Arrow arrow = (Arrow) damager;
            if(arrow.getShooter() instanceof Player) {
                if(!MySQL.getBoolean(Main.DB_USER, "uuid", ((Player) arrow.getShooter()).getUniqueId().toString(), "pvp") || !MySQL.getBoolean(Main.DB_USER, "uuid", damaged.getUniqueId().toString(), "pvp")) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
