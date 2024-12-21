package com.lx862.splashfox.data;

import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureContents;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;

public class BuiltinResourceTexture extends ResourceTexture {
    public BuiltinResourceTexture(Identifier location) {
        super(location);
    }

    @Override
    public TextureContents loadContents(ResourceManager resourceManager) {
        final Identifier textureId = getId();

        try {
            InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/" + textureId.getNamespace() + "/" + textureId.getPath());
            TextureContents texture = null;

            if(input != null) {
                try {
                    texture = new TextureContents(NativeImage.read(input), new TextureResourceMetadata(true, true));
                } finally {
                    input.close();
                }
            }

            return texture;
        } catch (IOException exception) {
            return TextureContents.createMissing();
        }
    }
}
