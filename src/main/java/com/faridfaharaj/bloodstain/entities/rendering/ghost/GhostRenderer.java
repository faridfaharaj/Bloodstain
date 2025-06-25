package com.faridfaharaj.bloodstain.entities.rendering.ghost;

import com.faridfaharaj.bloodstain.entities.entity.Ghost;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class GhostRenderer extends RenderLiving<Ghost> {
    private final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/steve.png");

    public GhostRenderer(RenderManager renderManager) {
        super(renderManager, new ModelPlayer(0.0f, false), 0.5F);
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerBipedArmor(this));
    }


    @Override
    public void doRender(Ghost entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ModelPlayer modelPlayer = (ModelPlayer) this.mainModel;
        modelPlayer.isSneak = entity.isSneaking();

        if(entity.isHandActive()){
            switch (entity.getActiveItemStack().getItemUseAction()){
                case BOW:
                    if (entity.getActiveHand() == EnumHand.MAIN_HAND) {
                        modelPlayer.rightArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
                    } else {
                        modelPlayer.leftArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
                    }
                    break;
                case BLOCK:
                    if (entity.getActiveHand() == EnumHand.MAIN_HAND) {
                        modelPlayer.rightArmPose = ModelBiped.ArmPose.BLOCK;
                    } else {
                        modelPlayer.leftArmPose = ModelBiped.ArmPose.BLOCK;
                    }
                    break;
                default:
                    modelPlayer.rightArmPose = entity.getHeldItemMainhand().isEmpty()?ModelBiped.ArmPose.EMPTY:ModelBiped.ArmPose.ITEM;
                    modelPlayer.leftArmPose = entity.getHeldItemOffhand().isEmpty()?ModelBiped.ArmPose.EMPTY:ModelBiped.ArmPose.ITEM;
                    break;
            }
        }else {
            modelPlayer.rightArmPose = entity.getHeldItemMainhand().isEmpty()?ModelBiped.ArmPose.EMPTY:ModelBiped.ArmPose.ITEM;
            modelPlayer.leftArmPose = entity.getHeldItemOffhand().isEmpty()?ModelBiped.ArmPose.EMPTY:ModelBiped.ArmPose.ITEM;
        }

        float pastswing = entity.pastSwing;
        float swing = entity.getSwing();
        swing = swing - pastswing;
        if (swing < 0.0F) {
            ++swing;
        }
        swing = pastswing + swing * partialTicks;
        modelPlayer.swingProgress = swing;
        boolean renderRiding = entity.getRiding();
        modelPlayer.isRiding = renderRiding;


        // GL
        GlStateManager.pushMatrix();

        GlStateManager.pushAttrib();

        int brightness = 0xF000F0;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
                (float) (brightness & 0xFFFF),
                (float) (brightness >> 16));

        GlStateManager.enableCull();

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.enableDepth();

        GlStateManager.depthMask(true);

        GlStateManager.color(1F, 1F, 1F, 0.6F);

        {

            if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Pre<>(entity, this, partialTicks, x, y, z))) return;
            GlStateManager.pushMatrix();
            GlStateManager.enableCull();
            this.mainModel.swingProgress = swing;
            this.mainModel.isRiding = renderRiding;
            this.mainModel.isChild = entity.isChild();

            try
            {
                float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
                float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
                float f2 = f1 - f;

                if (renderRiding)
                {
                    f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
                    f2 = f1 - f;
                    float f3 = MathHelper.wrapDegrees(f2);

                    if (f3 < -85.0F)
                    {
                        f3 = -85.0F;
                    }

                    if (f3 >= 85.0F)
                    {
                        f3 = 85.0F;
                    }

                    f = f1 - f3;

                    if (f3 * f3 > 2500.0F)
                    {
                        f += f3 * 0.2F;
                    }

                    f2 = f1 - f;
                }

                float f7 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
                this.renderLivingAt(entity, x, y, z);
                float f8 = this.handleRotationFloat(entity, partialTicks);
                this.applyRotations(entity, f8, f, partialTicks);
                float f4 = this.prepareScale(entity, partialTicks);
                float f5 = 0.0F;
                float f6 = 0.0F;

                if (!renderRiding)
                {
                    f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
                    f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);

                    if (entity.isChild())
                    {
                        f6 *= 3.0F;
                    }

                    if (f5 > 1.0F)
                    {
                        f5 = 1.0F;
                    }
                    f2 = f1 - f; // Forge: Fix MC-1207
                }

                GlStateManager.enableAlpha();
                this.mainModel.setLivingAnimations(entity, f6, f5, partialTicks);
                this.mainModel.setRotationAngles(f6, f5, f8, f2, f7, f4, entity);

                if (this.renderOutlines)
                {
                    boolean flag1 = this.setScoreTeamColor(entity);
                    GlStateManager.enableColorMaterial();
                    GlStateManager.enableOutlineMode(this.getTeamColor(entity));

                    if (!this.renderMarker)
                    {
                        this.renderModel(entity, f6, f5, f8, f2, f7, f4);
                    }

                    this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);

                    GlStateManager.disableOutlineMode();
                    GlStateManager.disableColorMaterial();

                    if (flag1)
                    {
                        this.unsetScoreTeamColor();
                    }
                }
                else
                {
                    boolean flag = this.setDoRenderBrightness(entity, partialTicks);
                    this.renderModel(entity, f6, f5, f8, f2, f7, f4);

                    if (flag)
                    {
                        this.unsetBrightness();
                    }

                    GlStateManager.depthMask(true);

                    this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
                }

                GlStateManager.disableRescaleNormal();
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }

            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.enableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.popMatrix();
            {
                if (!this.renderOutlines)
                {
                    this.renderName(entity, x, y, z);
                }
            }
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Post<>(entity, this, partialTicks, x, y, z));

        }
        {
            if (!this.renderOutlines)
            {
                this.renderName(entity, x, y, z);
            }
        }

        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.popAttrib();

        GlStateManager.popMatrix();
    }



    @Override
    protected ResourceLocation getEntityTexture(Ghost entity) {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(Ghost entity, float partialTickTime) {
        float scale = 0.94F;
        GlStateManager.scale(scale, scale, scale);
    }


}
