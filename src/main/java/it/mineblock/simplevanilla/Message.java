package it.mineblock.simplevanilla;

import it.mineblock.mbcore.Chat;

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
    PROTECT_PRIVATE("protect-private"),
    PROTECT_PRIVATE_WARN("protect-private-warn"),
    PROTECT_PUBLIC("protect-public"),
    PROTECT_REMOVED("protect-removed"),
    PROTECT_TEAM_ALLOWED("protect-team-allowed"),
    PROTECT_TEAM_DENIED("protect-team-denied"),
    PVP_ACTIVED("pvp-actived"),
    PVP_DEACTIVED("pvp-deactived"),
    TEAM_ALREADY("team-already"),
    TEAM_CREATED("team-created"),
    TEAM_DENIED_PERM("team-denied-perm"),
    TEAM_DISBANDED("team-disbanded"),
    TEAM_EXISTS("team-exists"),
    TEAM_INEXISTENT("team-inexistent"),
    TEAM_INVITE("team-invite"),
    TEAM_INVITE_SENT("team-invite-sent"),
    TEAM_JOIN("team-join"),
    TEAM_JOINED("team-joined"),
    TEAM_NOTHING("team-nothing"),
    TEAM_NOT_YOUR("team-not-your"),
    TEAM_PENDING_NONE("team-pending-none"),
    TEAM_REMOVE("team-remove"),
    TEAM_REMOVED("team-removed"),
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
        return Chat.getTranslated(Main.config.getString("message." + this.msg));
    }

    public String getReplaced(String target, String replacement) {
        return Chat.getTranslated(Main.config.getString("message." + this.msg).replace(target, replacement));
    }
}