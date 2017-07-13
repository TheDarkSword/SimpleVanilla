package it.mineblock.simplevanilla;

/**
 * Copyright © 2017 by Lorenzo Magni
 * All rights reserved. No part of this code may be reproduced, distributed, or transmitted in any form or by any means,
 * including photocopying, recording, or other electronic or mechanical methods, without the prior written permission
 * of the creator.
 */
public enum Message {
    INCORRECT_PARAM("incorrect-param"),
    INCORRECT_USAGE("incorrect-usage"),
    INSUFFICIENT_PREMISSION("insufficient-perm"),
    NOT_PLAYER("not-player"),
    PLAYER_NOT_FOUND("player-not-found"),
    PROTECTION_ADDED("protection-added"),
    PROTECTION_ADDED_FRIEND("protection-added-friend"),
    PROTECTION_IMPOSSIBLE("protection-impossible"),
    PROTECTION_ERROR("protection-error"),
    PROTECTION_EXISTENT("protection-existent"),
    PROTECTION_INEXISTENT("protection-inexistent"),
    PROTECTION_NOTHING("protection-nothing"),
    PROTECTION_REMOVED("protection-removed"),
    PROTECTION_REMOVED_FRIEND("protection-removed-friend"),
    PVP_ACTIVED("pvp-actived"),
    PVP_DEACTIVED("pvp-deactived"),
    TICKET_LIST("ticket-list"),
    TICKET_NOTHING("ticket-nothing"),
    TICKET_NOTIFY("ticket-notify"),
    TICKET_OPEN("ticket-open"),
    TICKET_SOLVE("ticket-solve"),
    TICKET_SOLVED("ticket-solved"),
    TICKET_SOLVING("ticket-solving"),
    TICKET_SOLVING_NOTIFY("ticket-solving-notify"),
    TICKET_UNSOLVE("ticket-unsolve"),
    TICKET_UNSOLVED("ticket-unsolved");

    private String msg;

    Message(String msg) {
        this.msg = msg;
    }

    public String get() {
        return Main.config.getString("message." + this.msg);
    }

    public String getReplaced(String target, String replacement) {
        return Main.config.getString("message." + this.msg).replace(target, replacement);
    }
}