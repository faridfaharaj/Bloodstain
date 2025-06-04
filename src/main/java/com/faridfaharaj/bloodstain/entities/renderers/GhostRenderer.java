package com.faridfaharaj.bloodstain.entities.renderers;

import com.faridfaharaj.bloodstain.entities.entity.Ghost;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class GhostRenderer extends RenderLiving<Ghost> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/steve.png");

    public GhostRenderer(RenderManager renderManager) {
        super(renderManager, new ModelPlayer(0.0f, false), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Ghost entity) {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(Ghost entity, float partialTickTime) {
        float scale = 0.95F;
        GlStateManager.scale(scale, scale, scale);
    }


}
