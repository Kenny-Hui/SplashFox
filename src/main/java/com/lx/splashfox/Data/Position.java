package com.lx.splashfox.Data;

public enum Position {
    LEFT_TO_MOJANG(false),
    RIGHT_TO_MOJANG(false),
    ABOVE_MOJANG(false),
    REPLACE_MOJANG(true),
    FOLLOW_MOUSE(false);

    public final boolean mojangLogoHidden;

    Position(boolean mojangLogoHidden) {
        this.mojangLogoHidden = mojangLogoHidden;
    }
}

