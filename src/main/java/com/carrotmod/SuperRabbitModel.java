package com.carrotmod;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** Simple blocky rabbit model (texture sheet 128x64). */
public class SuperRabbitModel extends EntityModel<SuperRabbit> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(CarrotMod.MOD_ID, "super_rabbit"), "main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart hindLeft;
    private final ModelPart hindRight;
    private final ModelPart frontLeft;
    private final ModelPart frontRight;

    public SuperRabbitModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.hindLeft = root.getChild("hind_left");
        this.hindRight = root.getChild("hind_right");
        this.frontLeft = root.getChild("front_left");
        this.frontRight = root.getChild("front_right");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, 0.0F, -11.0F, 14.0F, 12.0F, 22.0F),
                PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 36).addBox(-4.5F, -7.0F, -9.0F, 9.0F, 8.0F, 9.0F),
                PartPose.offset(0.0F, 9.0F, -8.0F));
        head.addOrReplaceChild("ear_left",
                CubeListBuilder.create().texOffs(40, 36).addBox(-1.5F, -10.0F, -0.5F, 3.0F, 10.0F, 1.0F),
                PartPose.offsetAndRotation(2.5F, -7.0F, -4.0F, -0.1F, 0.0F, 0.1F));
        head.addOrReplaceChild("ear_right",
                CubeListBuilder.create().texOffs(40, 36).mirror().addBox(-1.5F, -10.0F, -0.5F, 3.0F, 10.0F, 1.0F),
                PartPose.offsetAndRotation(-2.5F, -7.0F, -4.0F, -0.1F, 0.0F, -0.1F));

        root.addOrReplaceChild("hind_left",
                CubeListBuilder.create().texOffs(76, 0).addBox(-2.0F, 0.0F, -3.0F, 4.0F, 6.0F, 6.0F),
                PartPose.offset(5.0F, 18.0F, 7.0F));
        root.addOrReplaceChild("hind_right",
                CubeListBuilder.create().texOffs(76, 0).mirror().addBox(-2.0F, 0.0F, -3.0F, 4.0F, 6.0F, 6.0F),
                PartPose.offset(-5.0F, 18.0F, 7.0F));
        root.addOrReplaceChild("front_left",
                CubeListBuilder.create().texOffs(76, 14).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F),
                PartPose.offset(4.5F, 18.0F, -8.0F));
        root.addOrReplaceChild("front_right",
                CubeListBuilder.create().texOffs(76, 14).mirror().addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F),
                PartPose.offset(-4.5F, 18.0F, -8.0F));
        root.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(76, 26).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 3.0F),
                PartPose.offset(0.0F, 9.0F, 11.0F));

        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void setupAnim(SuperRabbit entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.xRot = headPitch * ((float) Math.PI / 180.0F);
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180.0F);
        float swing = Mth.cos(limbSwing * 0.6662F) * 1.2F * limbSwingAmount;
        this.hindLeft.xRot = swing;
        this.hindRight.xRot = swing;
        this.frontLeft.xRot = -swing * 0.8F;
        this.frontRight.xRot = -swing * 0.8F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                               int packedOverlay, int color) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
