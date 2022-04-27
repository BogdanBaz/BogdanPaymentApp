package com.example.bogdanpaymentapp;

public enum ActionType {
    TR_UNKNOWN(0),
    TR_PURCHASE(1),
    TR_PREAUTH(4),
    TR_COMPLETION(5),
    TR_RETURN(6),
    TR_CANCEL(10),
    TR_RECON(31),
    TR_PARAMS(40),
    TR_PREAUTH_CANCEL(82),
    TR_INCR_AUTH(86);
    private final int id;

    ActionType(int id) {
        this.id = id;
    }

    public static ActionType fromId(int id) {
        for (ActionType value : ActionType.values()) {
            if (value.getId() == id) {
                return value;
            }
        }
        return null;
    }

    private int getId() {
        return id;
    }
    public static int getIdForName(String name) {
        return ActionType.valueOf(name).getId();
    }

}