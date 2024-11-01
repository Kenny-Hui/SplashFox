package com.lx862.splashfox.data;

public enum ImagePosition {
    LEFT_TO_MOJANG(false),
    RIGHT_TO_MOJANG(false),
    ABOVE_MOJANG(false),
    REPLACE_MOJANG(true),
    FOLLOW_MOUSE(false);

    public final boolean mojangLogoHidden;

    ImagePosition(boolean hideMojangLogo) {
        this.mojangLogoHidden = hideMojangLogo;
    }
}

