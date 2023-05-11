package com.lx.splashfox.screen;

import com.lx.splashfox.SplashFox;
import com.lx.splashfox.screen.widget.TexturedButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChooseImageScreen extends Screen {
    private static final double SCREEN_WIDTH_FACTOR = 0.75;
    private static final int PADDING = 20;
    private static final int BUTTON_SIZE = 40;
    private final Consumer<Identifier> callback;
    private final List<TexturedButton> iconButtons;
    private final Screen parentScreen;
    private final int scrollMultiplier = 8;
    private Identifier selectedPath;
    private double scrolledOffset = 0;
    private int totalHeight = 0;
    private final ButtonWidget cancelButton;

    public ChooseImageScreen(Screen parentScreen, Identifier initialPath, Consumer<Identifier> callback) {
        super(Text.literal("Choose an image"));
        this.selectedPath = initialPath;
        this.callback = callback;
        this.parentScreen = parentScreen;
        cancelButton = new ButtonWidget.Builder(Text.translatable("splashfox.gui.cancel"), (btn) -> {
            close();
        }).build();
        iconButtons = new ArrayList<>();
    }

    @Override
    protected void init() {
        super.init();
        try {
            iconButtons.clear();
            URI localResource = SplashFox.class.getResource("/assets/splashfox/textures/gui/").toURI();
            Path localResourcePath = Path.of(localResource);
            Files.list(localResourcePath).forEach(filePath -> {
                String fileName = filePath.getFileName().toString();
                Identifier textureID = new Identifier("splashfox:textures/gui/" + fileName);
                TexturedButton texturedButton = new TexturedButton(0, 0, BUTTON_SIZE, BUTTON_SIZE, 0, 0, selectedPath.equals(textureID), textureID, BUTTON_SIZE, BUTTON_SIZE, e -> {
                    selectedPath = textureID;
                    close();
                }, Text.literal(filePath.getFileName().toString()));
                iconButtons.add(texturedButton);
            });
        } catch (Exception ex) {
        }

        double fullScreenWidth = client.getWindow().getScaledWidth() * SCREEN_WIDTH_FACTOR;
        int startX = (int)(client.getWindow().getScaledWidth() - fullScreenWidth) / 2;
        int x = 0;
        int y = 0;
        for(int i = 0; i < iconButtons.size(); i++) {
            TexturedButton button = iconButtons.get(i);

            int nextX = x + button.getWidth();
            if(nextX > fullScreenWidth) {
                x = 0;
                y += button.getHeight() + PADDING;
            }
            button.setX(startX + x);
            button.setY(y);
            x += button.getWidth() + PADDING;
            addDrawableChild(button);
        }
        cancelButton.setWidth(50);
        cancelButton.setX(client.getWindow().getScaledWidth() - cancelButton.getWidth());
        addDrawableChild(cancelButton);
        totalHeight = y + BUTTON_SIZE;
    }

    private void positionButtonOffset(double offset) {
        for(TexturedButton button : iconButtons) {
            button.setYOffset((int)-offset);
        }
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        renderBackground(drawContext);
        super.render(drawContext, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        scrollButtonWidgets(scrollAmount);
        return super.mouseScrolled(mouseX, mouseY, scrollAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == GLFW.GLFW_KEY_DOWN) {
            scrollButtonWidgets(-1);
        }
        if(keyCode == GLFW.GLFW_KEY_UP) {
            scrollButtonWidgets(1);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        this.client.setScreen(parentScreen);
        callback.accept(selectedPath);
    }

    private void scrollButtonWidgets(double amount) {
        int screenHeight = client.getWindow().getScaledHeight();
        scrolledOffset = Math.min(Math.max(0, scrolledOffset - (amount * scrollMultiplier)), totalHeight - screenHeight);
        positionButtonOffset(scrolledOffset);
    }
}
