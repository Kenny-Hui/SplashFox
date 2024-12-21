package com.lx862.splashfox.screen;

import com.lx862.splashfox.config.Config;
import com.lx862.splashfox.data.ImagePosition;
import com.lx862.splashfox.data.ScreenAlignment;
import com.lx862.splashfox.SplashFox;
import com.lx862.splashfox.render.FoxRenderer;
import com.lx862.splashfox.screen.widget.Slider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigScreen extends Screen {
    private final Slider dropHeightSlider;
    private final Slider foxSizeSlider;
    private final Slider speedSlider;
    private final CheckboxWidget flippedCheckbox;
    private final CheckboxWidget wobblyCheckbox;
    private final ButtonWidget chooseImageButton;
    private final ButtonWidget positionButton;
    private final ButtonWidget discardButton;
    private final ButtonWidget saveButton;
    private final List<Pair<String, Integer>> labels;
    private static final int SCREEN_WIDTH = 320;
    private double elapsed;
    private final Config tmpConfigInstance;
    private final FoxRenderer foxRenderer;
    private final Screen parentScreen;

    public ConfigScreen(Screen parent) {
        super(Text.translatable("splashfox.gui.config_title"));
        this.parentScreen = parent;
        // Make another instance of config so changes only apply if the user click save
        tmpConfigInstance = Config.readConfig();
        foxRenderer = new FoxRenderer();
        labels = new ArrayList<>();

        int curY = 40;

        chooseImageButton = new ButtonWidget.Builder(Text.translatable("splashfox.gui.choose"), (d) -> {
            ChooseImageScreen chooseImageScreen = new ChooseImageScreen(this, tmpConfigInstance);
            client.setScreen(chooseImageScreen);
        }).build();
        chooseImageButton.setY(curY);
        labels.add(new Pair<>("splashfox.gui.choose_img", curY));

        curY += 20;

        speedSlider = new Slider(0, curY, 100, 20, Text.literal(String.valueOf(tmpConfigInstance.speed)), tmpConfigInstance.speed, 2, (slider) -> {
            tmpConfigInstance.speed = slider.getValue();
        });
        labels.add(new Pair<>("splashfox.gui.speed", curY));

        curY += 20;

        dropHeightSlider = new Slider(0, curY, 100, 20, Text.literal(String.valueOf(tmpConfigInstance.dropHeight)), tmpConfigInstance.dropHeight, 3, (slider) -> {
            double val = slider.getValue();
            tmpConfigInstance.dropHeight = val;
        });
        labels.add(new Pair<>("splashfox.gui.drop_height", curY));

        curY += 20;

        foxSizeSlider = new Slider(0, curY, 100, 20, Text.literal(String.valueOf(tmpConfigInstance.foxSize)), tmpConfigInstance.foxSize, 2, (slider) -> {
            tmpConfigInstance.foxSize = slider.getValue();
        });
        labels.add(new Pair<>("splashfox.gui.blobfox_size", curY));

        curY += 20;

        flippedCheckbox = CheckboxWidget.builder(Text.literal(""), MinecraftClient.getInstance().textRenderer)
                .checked(tmpConfigInstance.flipped)
                .pos(0, curY)
                .callback((btn, checked) -> {
                    tmpConfigInstance.flipped = checked;
                }).build();
        labels.add(new Pair<>("splashfox.gui.flipped", curY));

        curY += 20;

        wobblyCheckbox = CheckboxWidget.builder(Text.literal(""), MinecraftClient.getInstance().textRenderer)
                .checked(tmpConfigInstance.wobbly)
                .pos(0, curY)
                .callback((btn, checked) -> {
                    tmpConfigInstance.wobbly = checked;
                }).build();
        labels.add(new Pair<>("splashfox.gui.wobbly", curY));

        curY += 20;

        positionButton = new ButtonWidget.Builder(Text.translatable("splashfox.gui.position." + tmpConfigInstance.position.toString()), (d) -> {
            int index = tmpConfigInstance.position.ordinal();
            ImagePosition[] imagePositions = Arrays.stream(ImagePosition.values()).filter(e -> e.selectable).toArray(ImagePosition[]::new);
            tmpConfigInstance.position = imagePositions[(index + 1) % imagePositions.length];
            d.setMessage(Text.translatable("splashfox.gui.position." + tmpConfigInstance.position.toString()));
        }).build();

        positionButton.setY(curY);
        labels.add(new Pair<>("splashfox.gui.position", curY));

        curY += 20;

        discardButton = new ButtonWidget.Builder(Text.translatable("splashfox.gui.discard_config"), (d) -> {
            close();
        }).build();

        saveButton = new ButtonWidget.Builder(Text.translatable("splashfox.gui.save_config"), (d) -> {
            Config.needUpdateTexture = true;
            Config.writeConfig(tmpConfigInstance);
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
        discardButton.setY(client.getWindow().getScaledHeight() - saveButton.getHeight() - 10);

        saveButton.setWidth(SCREEN_WIDTH / 2);
        saveButton.setX(getX(saveButton, ScreenAlignment.RIGHT));
        saveButton.setY(client.getWindow().getScaledHeight() - saveButton.getHeight() - 10);

        addDrawableChild(chooseImageButton);
        addDrawableChild(speedSlider);
        addDrawableChild(dropHeightSlider);
        addDrawableChild(foxSizeSlider);
        addDrawableChild(flippedCheckbox);
        addDrawableChild(wobblyCheckbox);
        addDrawableChild(positionButton);
        addDrawableChild(discardButton);
        addDrawableChild(saveButton);

        for(Pair<String, Integer> label : labels) {
            TextWidget tw = new TextWidget(Text.translatable(label.getLeft()), MinecraftClient.getInstance().textRenderer);
            tw.setX(getStartX());
            tw.setY(label.getRight() + textRenderer.fontHeight);
            addDrawableChild(tw);
        }
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if(client == null) return;
        super.render(drawContext, mouseX, mouseY, delta);
        elapsed += delta;

        // Render fox preview :D
        foxRenderer.render(client, drawContext, ImagePosition.GUI_PREVIEW, tmpConfigInstance, mouseX, mouseY, elapsed, 1.0f);

        drawContext.drawCenteredTextWithShadow(textRenderer, title, this.width / 2, 12, 0xFFFFFF);
        drawContext.drawTexture(RenderLayer::getGuiTextured, Screen.HEADER_SEPARATOR_TEXTURE, 0, 30, 0.0F, 0.0F, this.width, 2, 32, 2);
        drawContext.drawTexture(RenderLayer::getGuiTextured, Screen.FOOTER_SEPARATOR_TEXTURE, 0, this.height - 40, 0.0F, 0.0F, this.width, 2, 32, 2);
    }

    @Override
    public void close() {
        this.client.setScreen(parentScreen);
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
