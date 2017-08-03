package it.mineblock.simplevanilla.listeners;

import it.mineblock.simplevanilla.Utilities;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Copyright © 2017 by Lorenzo Magni
 * All rights reserved. No part of this code may be reproduced, distributed, or transmitted in any form or by any means,
 * including photocopying, recording, or other electronic or mechanical methods, without the prior written permission
 * of the creator.
 */
public class OnPlayerUseLimited implements Listener {

    @EventHandler
    public void onPlayerPlaceLava(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();

        if(Utilities.isNewbie(player)) {
            if(event.getBucket() != null && event.getBucket().equals(Material.LAVA_BUCKET)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerPlaceTNT(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Material material = event.getBlockPlaced().getType();

        if(Utilities.isNewbie(player)) {
            if(material.equals(Material.TNT) || material.equals(Material.EXPLOSIVE_MINECART)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerUseFlintAndSteel(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(Utilities.isNewbie(player)) {
            if(event.getItem() != null && event.getItem().getType().equals(Material.FLINT_AND_STEEL)) {
                event.setCancelled(true);
            }
        }
    }
}
