package com.carrotmod;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SuperRabbitRenderer extends MobRenderer<SuperRabbit, SuperRabbitModel> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[5];

    static {
        for (int i = 0; i < 5; i++) {
            TEXTURES[i] = ResourceLocation.fromNamespaceAndPath(CarrotMod.MOD_ID,
                    "textures/entity/super_rabbit_" + (i + 1) + ".png");
        }
    }

    public SuperRabbitRenderer(EntityRendererProvider.Context context) {
        super(context, new SuperRabbitModel(context.bakeLayer(SuperRabbitModel.LAYER)), 0.8F);
    }

    @Override
    public ResourceLocation getTextureLocation(SuperRabbit entity) {
        return TEXTURES[entity.getTier() - 1];
    }
}
