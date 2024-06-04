package com.lx862.splashfox.mixin;

import com.lx862.splashfox.config.Config;
import com.lx862.splashfox.data.CustomResourceTexture;
import com.lx862.splashfox.SplashFox;
import com.lx862.splashfox.render.FoxRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashOverlay.class)
public class SplashOverlayMixin {
	private static final Identifier EMPTY_LOGO = Identifier.of("splashfox", "textures/empty.png");
	@Shadow private long reloadCompleteTime;
	@Shadow private long reloadStartTime;
	@Shadow @Final private boolean reloading;
	@Shadow @Final private MinecraftClient client;
	@Shadow @Final
	static Identifier LOGO;
	private double elapsed;
	private FoxRenderer renderer;

	@Inject(at = @At("HEAD"), method = "init", cancellable = true)
	private static void init(MinecraftClient client, CallbackInfo ci) {
		client.getTextureManager().registerTexture(SplashFox.config.getFoxImageId(), new CustomResourceTexture(SplashFox.config.getFoxImageId()));

		if(SplashFox.config.position.mojangLogoHidden) {
			client.getTextureManager().registerTexture(LOGO, new CustomResourceTexture(EMPTY_LOGO));
			ci.cancel();
		}
	}

	@Inject(at = @At("TAIL"), method = "render")
	private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		ensureTextureRegistered();

		if(renderer == null) renderer = new FoxRenderer();
		elapsed += delta;
		renderer.render(this.client, context, SplashFox.config.position, SplashFox.config, mouseX, mouseY, elapsed, getOverlayAlpha());
	}

	// The init method is only called once on startup, call init again if any settings is mismatched
	private void ensureTextureRegistered() {
		if(Config.needUpdateTexture) {
			SplashOverlay.init(client);
			Config.needUpdateTexture = false;
		}
	}

	private float getOverlayAlpha() {
		long l = Util.getMeasuringTimeMs();
		float f = this.reloadCompleteTime > -1L ? (float)(l - this.reloadCompleteTime) / 1000.0F : -1.0F;
		float g = this.reloadStartTime > -1L ? (float)(l - this.reloadStartTime) / 500.0F : -1.0F;

		if (f >= 1.0F) {
			return 1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);
		} else if (this.reloading) {
			return MathHelper.clamp(g, 0.0F, 1.0F);
		} else {
			return 1.0F;
		}
	}
}