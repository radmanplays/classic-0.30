package com.mojang.minecraft.model;

import org.lwjgl.opengl.GL11;

import com.mojang.util.Mth;

public class HumanoidModel extends BaseModel {
	public Cube head;
	public Cube hair;
	public Cube body;
	public Cube rightArm;
	public Cube leftArm;
	public Cube rightLeg;
	public Cube leftLeg;

	public HumanoidModel() {
		this(0.0F);
	}

	public HumanoidModel(float var1) {
		this.head = new Cube(0, 0);
		this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, var1);
		this.hair = new Cube(32, 0);
		this.hair.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, var1 + 0.5F);
		this.body = new Cube(16, 16);
		this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, var1);
		this.rightArm = new Cube(40, 16);
		this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, var1);
		this.rightArm.setPos(-5.0F, 2.0F, 0.0F);
		this.leftArm = new Cube(40, 16);
		this.leftArm.mirror = true;
		this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, var1);
		this.leftArm.setPos(5.0F, 2.0F, 0.0F);
		this.rightLeg = new Cube(0, 16);
		this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
		this.rightLeg.setPos(-2.0F, 12.0F, 0.0F);
		this.leftLeg = new Cube(0, 16);
		this.leftLeg.mirror = true;
		this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
		this.leftLeg.setPos(2.0F, 12.0F, 0.0F);
	}

	public final void render(float var1, float var2, float var3, float var4, float var5, float var6) {
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		this.setupAnim(var1, var2, var3, var4, var5, var6);
		this.head.render(var6);
		this.body.render(var6);
		this.rightArm.render(var6);
		this.leftArm.render(var6);
		this.rightLeg.render(var6);
		this.leftLeg.render(var6);
	}

	public void setupAnim(float var1, float var2, float var3, float var4, float var5, float var6) {
		this.head.yRot = var4 / (180.0F / (float)Math.PI);
		this.head.xRot = var5 / (180.0F / (float)Math.PI);
		this.rightArm.xRot = Mth.cos(var1 * 0.6662F + (float)Math.PI) * 2.0F * var2;
		this.rightArm.zRot = (Mth.cos(var1 * 0.2312F) + 1.0F) * var2;
		this.leftArm.xRot = Mth.cos(var1 * 0.6662F) * 2.0F * var2;
		this.leftArm.zRot = (Mth.cos(var1 * 0.2812F) - 1.0F) * var2;
		this.rightLeg.xRot = Mth.cos(var1 * 0.6662F) * 1.4F * var2;
		this.leftLeg.xRot = Mth.cos(var1 * 0.6662F + (float)Math.PI) * 1.4F * var2;
		this.rightArm.zRot += Mth.cos(var3 * 0.09F) * 0.05F + 0.05F;
		this.leftArm.zRot -= Mth.cos(var3 * 0.09F) * 0.05F + 0.05F;
		this.rightArm.xRot += Mth.sin(var3 * 0.067F) * 0.05F;
		this.leftArm.xRot -= Mth.sin(var3 * 0.067F) * 0.05F;
	}
}
