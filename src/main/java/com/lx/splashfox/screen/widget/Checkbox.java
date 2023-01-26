package com.lx.splashfox.screen.widget;

import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class Checkbox extends CheckboxWidget {
    private final Consumer<Boolean> onChecked;

    public Checkbox(int x, int y, int width, int height, Text message, boolean checked, Consumer<Boolean> onChecked) {
        super(x, y, width, height, message, checked);
        this.onChecked = onChecked;
    }

    @Override
    public void onPress() {
        super.onPress();
        onChecked.accept(isChecked());
    }
}
