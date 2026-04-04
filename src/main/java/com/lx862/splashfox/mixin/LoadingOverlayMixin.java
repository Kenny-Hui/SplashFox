package com.lx862.splashfox.mixin;

import com.lx862.splashfox.config.Config;
import com.lx862.splashfox.data.BuiltinResourceTexture;
import com.lx862.splashfox.SplashFox;
import com.lx862.splashfox.data.FileSystemResourceTexture;
import com.lx862.splashfox.render.FoxRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingOverlay.class)
public class LoadingOverlayMixin {
	@Unique private static final Identifier EMPTY_LOGO = Identifier.fromNamespaceAndPath("splashfox", "textures/empty.png");

	@Shadow @Final private boolean fadeIn;
	@Shadow @Final private Minecraft minecraft;
	@Shadow @Final public static Identifier MOJANG_STUDIOS_LOGO_LOCATION;
	@Shadow private long fadeOutStart;
	@Shadow private long fadeInStart;
	@Unique private double elapsed;
	@Unique private FoxRenderer renderer;

	@Inject(at = @At("HEAD"), method = "registerTextures", cancellable = true)
	private static void splashfox$registerTextures(TextureManager textureManager, CallbackInfo ci) {
        Identifier imageId = SplashFox.config.getImageId();
		if(SplashFox.config.usesCustomImage()) {
			textureManager.registerAndLoad(imageId, new FileSystemResourceTexture(SplashFox.config.customPath, imageId));
		} else {
			textureManager.registerAndLoad(imageId, new BuiltinResourceTexture(imageId));
		}

		if(SplashFox.config.position.mojangLogoHidden) {
			textureManager.registerAndLoad(MOJANG_STUDIOS_LOGO_LOCATION, new BuiltinResourceTexture(EMPTY_LOGO));
			ci.cancel();
		}
	}

	@Inject(at = @At("TAIL"), method = "extractRenderState")
	private void splashfox$render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		splashfox$ensureTextureRegistered();

		if(renderer == null) renderer = new FoxRenderer();
		elapsed += delta;
		renderer.render(this.minecraft, guiGraphics, SplashFox.config.position, SplashFox.config, mouseX, mouseY, elapsed, splashfox$getOverlayAlpha());
	}

	// The init method is only called once on startup, call init again if any settings is mismatched
	@Unique
	private void splashfox$ensureTextureRegistered() {
		if(Config.needUpdateTexture) {
			LoadingOverlay.registerTextures(this.minecraft.getTextureManager());
			Config.needUpdateTexture = false;
		}
	}

	@Unique
	private float splashfox$getOverlayAlpha() {
		long timeNow = Util.getMillis();
		float f = this.fadeOutStart > -1L ? (float)(timeNow - this.fadeOutStart) / 1000.0F : -1.0F;
		float g = this.fadeInStart > -1L ? (float)(timeNow - this.fadeInStart) / 500.0F : -1.0F;

		if (f >= 1.0F) {
			return 1.0F - Mth.clamp(f - 1.0F, 0.0F, 1.0F);
		} else if (this.fadeIn) {
			return Mth.clamp(g, 0.0F, 1.0F);
		} else {
			return 1.0F;
		}
	}
}