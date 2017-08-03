package it.mineblock.simplevanilla.listeners;

import it.mineblock.mbcore.Chat;
import it.mineblock.mbcore.MySQL;
import it.mineblock.simplevanilla.Main;
import it.mineblock.simplevanilla.Message;
import it.mineblock.simplevanilla.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;

/**
 * Copyright © 2017 by Lorenzo Magni
 * All rights reserved. No part of this code may be reproduced, distributed, or transmitted in any form or by any means,
 * including photocopying, recording, or other electronic or mechanical methods, without the prior written permission
 * of the creator.
 */
public class OnPlayerPlaceBlock implements Listener {

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        Player player = event.getPlayer();
        long x = (long) block.getX();
        long y = (long) block.getY();
        long z = (long) block.getZ();


        if(block.getType().equals(Material.HOPPER) || block.getType().equals(Material.HOPPER_MINECART)) {
            Block blockT = block.getRelative(BlockFace.UP);

            if(Utilities.isProtected(blockT)) {
                if(!Utilities.canBeControlled(blockT, player)) {
                    event.setCancelled(true);
                }
            }
        }

        if(block.getType().equals(Material.CHEST) || block.getType().equals(Material.TRAPPED_CHEST)) {
            Block blockN = block.getRelative(BlockFace.NORTH);
            Block blockS = block.getRelative(BlockFace.SOUTH);
            Block blockE = block.getRelative(BlockFace.EAST);
            Block blockW = block.getRelative(BlockFace.WEST);

            if(Utilities.isProtected(blockN)) {
                if(!Utilities.canBeControlled(blockN, player)){
                    event.setCancelled(true);
                    return;
                }
            }

            if(Utilities.isProtected(blockS)) {
                if(!Utilities.canBeControlled(blockS, player)){
                    event.setCancelled(true);
                    return;
                }
            }

            if(Utilities.isProtected(blockE)) {
                if(!Utilities.canBeControlled(blockE, player)){
                    event.setCancelled(true);
                    return;
                }
            }

            if(Utilities.isProtected(blockW)) {
                if(!Utilities.canBeControlled(blockW, player)){
                    event.setCancelled(true);
                    return;
                }
            }
        }

        for(String id : Main.config.getStringList("auto-protected-blocks")) {
            if(block.getType().equals(Material.getMaterial(id))) {
                ArrayList values = new ArrayList();
                values.add(player.getName());
                values.add(player.getUniqueId());
                values.add(block.getType().name());
                values.add(x);
                values.add(y);
                values.add(z);

                MySQL.insertLine(Main.DB_PROTECTION, new String[] {
                        "username",
                        "uuid",
                        "block",
                        "x",
                        "y",
                        "z"
                }, values);
                Chat.send(Message.PROTECT_ADDED.getReplaced("{block}", block.getType().name()), player);
            }
        }
    }
}
