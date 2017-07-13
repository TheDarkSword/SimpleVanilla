package it.mineblock.simplevanilla;

/**
 * Copyright © 2017 by Lorenzo Magni
 * All rights reserved. No part of this code may be reproduced, distributed, or transmitted in any form or by any means,
 * including photocopying, recording, or other electronic or mechanical methods, without the prior written permission
 * of the creator.
 */
public enum Permissions {

    TICKET_STAFF("ticket.staff"),
    PROTECT_BYPASS("protect.bypass");

    private String perm;

    Permissions(String perm) {
        this.perm = perm;
    }

    public String get() {
        return this.perm;
    }
}