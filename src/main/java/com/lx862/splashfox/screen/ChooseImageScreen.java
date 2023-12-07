package com.lx862.splashfox.screen;

import com.lx862.splashfox.SplashFox;
import com.lx862.splashfox.screen.widget.SelectFoxButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
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
    private final List<SelectFoxButton> iconButtons;
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

        cancelButton.setWidth(50);
        cancelButton.setX(client.getWindow().getScaledWidth() - cancelButton.getWidth());
        addDrawableChild(cancelButton);

        try {
            iconButtons.clear();
            URI localResource = SplashFox.class.getResource("/assets/splashfox/textures/gui/").toURI();
            Path localResourcePath = Path.of(localResource);
            Files.list(localResourcePath).forEach(filePath -> {
                String fileName = filePath.getFileName().toString();
                Identifier textureID = new Identifier("splashfox", "textures/gui/" + fileName);
                MinecraftClient.getInstance().getTextureManager().getTexture(textureID).bindTexture();
                SelectFoxButton selectFoxButton = new SelectFoxButton(0, 0, BUTTON_SIZE, BUTTON_SIZE, selectedPath.equals(textureID), textureID, e -> {
                    selectedPath = textureID;
                    close();
                }, Text.literal(filePath.getFileName().toString()));
                iconButtons.add(selectFoxButton);
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        double fullScreenWidth = client.getWindow().getScaledWidth() * SCREEN_WIDTH_FACTOR;
        int startX = (int)(client.getWindow().getScaledWidth() - fullScreenWidth) / 2;
        int x = 0;
        int y = 0;

        for (SelectFoxButton button : iconButtons) {
            int nextX = x + button.getWidth();
            if (nextX > fullScreenWidth) {
                x = 0;
                y += button.getHeight() + PADDING;
            }
            button.setX(startX + x);
            button.setY(y);
            x += button.getWidth() + PADDING;
            addDrawableChild(button);
        }

        totalHeight = y + BUTTON_SIZE;
    }

    private void positionButtonOffset(double offset) {
        for(SelectFoxButton button : iconButtons) {
            button.setYOffset((int)-offset);
        }
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double scrollAmount) {
        scrollButtonWidgets(scrollAmount);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, scrollAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == GLFW.GLFW_KEY_DOWN) {
            scrollButtonWidgets(-8);
        }
        if(keyCode == GLFW.GLFW_KEY_UP) {
            scrollButtonWidgets(8);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        this.client.setScreen(parentScreen);
        callback.accept(selectedPath);
    }

    private void scrollButtonWidgets(double amount) {
        scrolledOffset = Math.min(Math.max(0, scrolledOffset - (amount * scrollMultiplier)), totalHeight - height);
        positionButtonOffset(scrolledOffset);
    }
}
