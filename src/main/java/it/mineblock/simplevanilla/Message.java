package it.mineblock.simplevanilla;

/**
 * Copyright © 2017 by Lorenzo Magni
 * All rights reserved. No part of this code may be reproduced, distributed, or transmitted in any form or by any means,
 * including photocopying, recording, or other electronic or mechanical methods, without the prior written permission
 * of the creator.
 */
public enum Message {
    OPEN("open"),
    DESTROY("destroy"),
    CONTROL("control"),
    INCORRECT_PARAM("incorrect-param"),
    INCORRECT_USAGE("incorrect-usage"),
    INSUFFICIENT_PREMISSION("insufficient-perm"),
    NOT_PLAYER("not-player"),
    PLAYER_NOT_FOUND("player-not-found"),
    PROTECT_ADDED("protect-added"),
    PROTECT_ALLOWED_ADDED("protect-allowed-added"),
    PROTECT_ALLOWED_ALREADY("protect-allowed-already"),
    PROTECT_ALLOWED_NONE("protect-allowed-none"),
    PROTECT_ALLOWED_OVERRIDE("protect-allowed-override"),
    PROTECT_ALLOWED_REMOVED("protect-allowed-removed"),
    PROTECT_ALREADY("protect-already"),
    PROTECT_INCOMPATIBLE("protect-incompatible"),
    PROTECT_INEXISTENT("protect-inexistent"),
    PROTECT_PERM_DENIED("protect-perm-denied"),
    PROTECT_POINTER("protect-pointer"),
    PROTECT_REMOVED("protect-removed"),
    PROTECT_TEAM_ALLOWED("protect-team-allowed"),
    PROTECT_TEAM_DENIED("protect-team-denied"),
    PROTECT_TEAM_SETTING_ALLOWED("protect-team-setting-allowed"),
    PROTECT_TEAM_SETTING_DENIED("protect-team-setting-denied"),
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