package it.mineblock.simplevanilla.listeners;

import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Copyright © 2017 by Lorenzo Magni
 * All rights reserved. No part of this code may be reproduced, distributed, or transmitted in any form or by any means,
 * including photocopying, recording, or other electronic or mechanical methods, without the prior written permission
 * of the creator.
 */
public class OnInventoryMove implements Listener {

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {
        Inventory destination = event.getDestination();
        InventoryHolder holder = destination.getHolder();
        if(holder instanceof HopperMinecart) {
            event.setCancelled(true);
        }
    }
}
