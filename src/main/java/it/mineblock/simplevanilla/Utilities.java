package it.mineblock.simplevanilla;

import it.mineblock.mbcore.MySQL;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Copyright © 2017 by Lorenzo Magni
 * All rights reserved. No part of this code may be reproduced, distributed, or transmitted in any form or by any means,
 * including photocopying, recording, or other electronic or mechanical methods, without the prior written permission
 * of the creator.
 */
public class Utilities {

    public static boolean isBlockCompatible(Block block) {
        List<String> protectedBlocks = Main.config.getStringList("protected-blocks");

        return protectedBlocks.contains(block.getType().name());
    }

    public static boolean canBeProtected(Block block) {
        return false;
    }

    public static boolean canBeUnprotected(Block block) {
        return false;
    }

    public static boolean isProtected(Block block) {
        long x = block.getX();
        long y = block.getY();
        long z = block.getZ();

        if(MySQL.rowExists(Main.DB_PROTECTION, new String[] {"x", "y", "z"}, new String[] {String.valueOf(x), String.valueOf(y), String.valueOf(z)})) {
            return true;
        }
        return false;
    }

    public static Block getPlayerTargetBlock(Player player) {
        return player.getTargetBlock((Set) null, 5);
    }

    public static boolean isNewbie(Player player) {
        Date date = new Date();
        Date firstLogin = new Date(MySQL.getTimestamp(Main.DB_USER, "uuid", player.getUniqueId().toString(), "login_first").getTime());
        return date.getTime() - firstLogin.getTime() < 172800000L;
    }

    public static void codeBuilder(Player player, int position, String value) {
        String[] values = MySQL.getString(Main.DB_USER, "uuid", player.getUniqueId().toString(), "teamSettings").split("-");

    }
}