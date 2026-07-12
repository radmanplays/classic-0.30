package com.mojang.minecraft.model;

import org.lwjgl.opengl.GL11;

import com.mojang.util.Mth;

public class QuadrupedModel extends BaseModel {
	public Cube head = new Cube(0, 0);
	public Cube body;
	public Cube leg1;
	public Cube leg2;
	public Cube leg3;
	public Cube leg4;

	public QuadrupedModel(int var1, float var2) {
		this.head.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, 0.0F);
		this.head.setPos(0.0F, (float)(18 - var1), -6.0F);
		this.body = new Cube(28, 8);
		this.body.addBox(-5.0F, -10.0F, -7.0F, 10, 16, 8, 0.0F);
		this.body.setPos(0.0F, (float)(17 - var1), 2.0F);
		this.leg1 = new Cube(0, 16);
		this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, var1, 4, 0.0F);
		this.leg1.setPos(-3.0F, (float)(24 - var1), 7.0F);
		this.leg2 = new Cube(0, 16);
		this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4, var1, 4, 0.0F);
		this.leg2.setPos(3.0F, (float)(24 - var1), 7.0F);
		this.leg3 = new Cube(0, 16);
		this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4, var1, 4, 0.0F);
		this.leg3.setPos(-3.0F, (float)(24 - var1), -5.0F);
		this.leg4 = new Cube(0, 16);
		this.leg4.addBox(-2.0F, 0.0F, -2.0F, 4, var1, 4, 0.0F);
		this.leg4.setPos(3.0F, (float)(24 - var1), -5.0F);
	}

	public final void render(float var1, float var2, float var3, float var4, float var5, float var6) {
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		this.head.yRot = var4 / (180.0F / (float)Math.PI);
		this.head.xRot = var5 / (180.0F / (float)Math.PI);
		this.body.xRot = (float)Math.PI * 0.5F;
		this.leg1.xRot = Mth.cos(var1 * 0.6662F) * 1.4F * var2;
		this.leg2.xRot = Mth.cos(var1 * 0.6662F + (float)Math.PI) * 1.4F * var2;
		this.leg3.xRot = Mth.cos(var1 * 0.6662F + (float)Math.PI) * 1.4F * var2;
		this.leg4.xRot = Mth.cos(var1 * 0.6662F) * 1.4F * var2;
		this.head.render(var6);
		this.body.render(var6);
		this.leg1.render(var6);
		this.leg2.render(var6);
		this.leg3.render(var6);
		this.leg4.render(var6);
	}
}
