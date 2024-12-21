package com.lx862.splashfox.data;

public enum ImagePosition {
    LEFT_TO_MOJANG(false, true),
    RIGHT_TO_MOJANG(false, true),
    ABOVE_MOJANG(false, true),
    REPLACE_MOJANG(true, true),
    FOLLOW_MOUSE(false, true),
    GUI_PREVIEW(false, false);

    public final boolean mojangLogoHidden;
    public final boolean selectable;

    ImagePosition(boolean hideMojangLogo, boolean selectable) {
        this.mojangLogoHidden = hideMojangLogo;
        this.selectable = selectable;
    }
}

