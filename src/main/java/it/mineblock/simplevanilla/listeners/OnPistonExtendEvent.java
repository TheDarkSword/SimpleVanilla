package it.mineblock.simplevanilla.listeners;

import it.mineblock.simplevanilla.Utilities;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;

import java.util.List;

/**
 * Copyright © 2017 by Lorenzo Magni
 * All rights reserved. No part of this code may be reproduced, distributed, or transmitted in any form or by any means,
 * including photocopying, recording, or other electronic or mechanical methods, without the prior written permission
 * of the creator.
 */
public class OnPistonExtendEvent implements Listener {

    @EventHandler
    public void onPistonExtendEvent(BlockPistonExtendEvent event) {
        List<Block> blocks = event.getBlocks();

        for(Block block : blocks) {
            if(Utilities.isProtected(block)) {
                event.setCancelled(true);
            }
        }
    }
}
