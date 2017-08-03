package it.mineblock.simplevanilla.listeners;

import it.mineblock.mbcore.Chat;
import it.mineblock.mbcore.MySQL;
import it.mineblock.simplevanilla.Main;
import it.mineblock.simplevanilla.Message;
import it.mineblock.simplevanilla.Utilities;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Copyright © 2017 by Lorenzo Magni
 * All rights reserved. No part of this code may be reproduced, distributed, or transmitted in any form or by any means,
 * including photocopying, recording, or other electronic or mechanical methods, without the prior written permission
 * of the creator.
 */
public class OnPlayerBreakBlock implements Listener {

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        long x = (long) block.getX();
        long y = (long) block.getY();
        long z = (long) block.getZ();

        if(Utilities.isProtected(block)) {
            if(!Utilities.canBeControlled(block, player)) {
                event.setCancelled(true);
            } else {
                MySQL.removeRow(Main.DB_PROTECTION, new String[] {"x", "y", "z"}, new String[] {String.valueOf(x), String.valueOf(y), String.valueOf(z)});
                Chat.send(Message.PROTECT_REMOVED.getReplaced("{block}", block.getType().name()), player);
            }
        }
    }
}
