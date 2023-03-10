package com.lx.splashfox.screen;

import com.lx.splashfox.Config.Config;
import com.lx.splashfox.Data.Position;
import com.lx.splashfox.Data.ScreenAlignment;
import com.lx.splashfox.SplashFox;
import com.lx.splashfox.render.FoxRenderer;
import com.lx.splashfox.screen.widget.Checkbox;
import com.lx.splashfox.screen.widget.Slider;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {
    private final Slider dropHeightSlider;
    private final Slider foxSizeSlider;
    private final Slider speedSlider;
    private final Checkbox flippedCheckbox;
    private final Checkbox wobblyCheckbox;
    private final ButtonWidget chooseImageButton;
    private final ButtonWidget positionButton;
    private final ButtonWidget discardButton;
    private final ButtonWidget saveButton;
    private final List<Pair<String, Integer>> labels;
    private static final int SCREEN_WIDTH = 320;
    private double elapsed;
    private final Config sessionInstance;
    private final FoxRenderer foxRenderer;

    public ConfigScreen() {
        super(Text.translatable("splashfox.gui.config_title"));
        // Make another instance of config so changes will apply to this config screen but not globally if we don't want to save
        sessionInstance = Config.readConfig();
        foxRenderer = new FoxRenderer();
        labels = new ArrayList<>();

        int i = 0;

        labels.add(new Pair<>("splashfox.gui.config_title", i));

        i += 40;

        chooseImageButton = new ButtonWidget.Builder(Text.translatable("splashfox.gui.choose"), (d) -> {
            Identifier currentImageId = new Identifier(sessionInstance.imagePath);
            ChooseImageScreen chooseImageScreen = new ChooseImageScreen(this, currentImageId, (id) -> {
                sessionInstance.imagePath = id.toString();
            });

            client.setScreen(chooseImageScreen);
        }).build();
        chooseImageButton.setY(i);
        labels.add(new Pair<>("splashfox.gui.choose_img", i));

        i += 20;

        speedSlider = new Slider(0, i, 100, 20, Text.literal(String.valueOf(sessionInstance.speed)), sessionInstance.speed, 2, (slider) -> {
            double val = slider.getValue();
            sessionInstance.speed = val;
        });
        labels.add(new Pair<>("splashfox.gui.speed", i));

        i += 20;

        dropHeightSlider = new Slider(0, i, 100, 20, Text.literal(String.valueOf(sessionInstance.dropHeight)), sessionInstance.dropHeight, 3, (slider) -> {
            double val = slider.getValue();
            sessionInstance.dropHeight = val;
        });
        labels.add(new Pair<>("splashfox.gui.drop_height", i));

        i += 20;

        foxSizeSlider = new Slider(0, i, 100, 20, Text.literal(String.valueOf(sessionInstance.foxSize)), sessionInstance.foxSize, 2, (slider) -> {
            double val = slider.getValue();
            sessionInstance.foxSize = val;
        });
        labels.add(new Pair<>("splashfox.gui.blobfox_size", i));

        i += 20;

        flippedCheckbox = new Checkbox(0, i, 20, 20, Text.literal(""), sessionInstance.flipped, (checked) -> {
            sessionInstance.flipped = checked;
        });
        labels.add(new Pair<>("splashfox.gui.flipped", i));

        i += 20;

        wobblyCheckbox = new Checkbox(0, i, 20, 20, Text.literal(""), sessionInstance.wobbly, (checked) -> {
            sessionInstance.wobbly = checked;
        });
        labels.add(new Pair<>("splashfox.gui.wobbly", i));

        i += 20;

        positionButton = new ButtonWidget.Builder(Text.translatable("splashfox.gui.position." + sessionInstance.position.toString()), (d) -> {
            int index = sessionInstance.position.ordinal();
            sessionInstance.position = Position.values()[(index + 1) % Position.values().length];
            d.setMessage(Text.translatable("splashfox.gui.position." + sessionInstance.position.toString()));
        }).build();

        positionButton.setY(i);
        labels.add(new Pair<>("splashfox.gui.position", i));

        i += 20;

        discardButton = new ButtonWidget.Builder(Text.translatable("splashfox.gui.discard_config"), (d) -> {
            close();
        }).build();

        saveButton = new ButtonWidget.Builder(Text.translatable("splashfox.gui.save_config"), (d) -> {
            Config.writeConfig(sessionInstance);
            SplashFox.config = Config.readConfig();
            close();
        }).build();
    }

    @Override
    protected void init() {
        super.init();

        chooseImageButton.setWidth(100);
        chooseImageButton.setX(getX(chooseImageButton, ScreenAlignment.RIGHT));

        speedSlider.setX(getX(speedSlider, ScreenAlignment.RIGHT));
        dropHeightSlider.setX(getX(dropHeightSlider, ScreenAlignment.RIGHT));
        foxSizeSlider.setX(getX(foxSizeSlider, ScreenAlignment.RIGHT));
        flippedCheckbox.setX(getX(flippedCheckbox, ScreenAlignment.RIGHT));
        wobblyCheckbox.setX(getX(wobblyCheckbox, ScreenAlignment.RIGHT));

        positionButton.setWidth(120);
        positionButton.setX(getX(positionButton, ScreenAlignment.RIGHT));

        discardButton.setWidth(SCREEN_WIDTH / 2);
        discardButton.setX(getX(saveButton, ScreenAlignment.LEFT));
        discardButton.setY(client.getWindow().getScaledHeight() - saveButton.getHeight());

        saveButton.setWidth(SCREEN_WIDTH / 2);
        saveButton.setX(getX(saveButton, ScreenAlignment.RIGHT));
        saveButton.setY(client.getWindow().getScaledHeight() - saveButton.getHeight());

        addDrawableChild(chooseImageButton);
        addDrawableChild(speedSlider);
        addDrawableChild(dropHeightSlider);
        addDrawableChild(foxSizeSlider);
        addDrawableChild(flippedCheckbox);
        addDrawableChild(wobblyCheckbox);
        addDrawableChild(positionButton);
        addDrawableChild(discardButton);
        addDrawableChild(saveButton);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        elapsed += delta;
        renderBackground(matrices);
        if(client == null) return;

        TextRenderer textRenderer = client.textRenderer;

        // Render text label
        for(Pair<String, Integer> label : labels) {
            int y = label.getRight() + textRenderer.fontHeight;
            textRenderer.drawWithShadow(matrices, Text.translatable(label.getLeft()), getStartX(), y, 0xFFFFFF);
        }

        // Render fox preview :D
        try {
            foxRenderer.render(client, matrices, null, sessionInstance, mouseX, mouseY, elapsed, 1.0f);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        super.close();
    }

    private int getX(int width, ScreenAlignment type) {
        if(client != null) {
            int scaledWidth = client.getWindow().getScaledWidth();
            int startX = (scaledWidth - SCREEN_WIDTH) / 2;

            if(type == ScreenAlignment.LEFT) {
                return startX;
            }

            if(type == ScreenAlignment.CENTERED) {
                return (scaledWidth / 2) - (width / 2);
            }

            if(type == ScreenAlignment.RIGHT) {
                return scaledWidth - startX - width;
            }
        }

        return 0;
    }

    private int getX(ClickableWidget widget, ScreenAlignment type) {
        return getX(widget.getWidth(), type);
    }

    private int getStartX() {
        return getX(0, ScreenAlignment.LEFT);
    }
}
