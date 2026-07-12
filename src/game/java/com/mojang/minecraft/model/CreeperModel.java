package com.mojang.minecraft.model;

import org.lwjgl.opengl.GL11;

import com.mojang.util.Mth;

public final class CreeperModel extends BaseModel {
	private Cube head = new Cube(0, 0);
	private Cube hair;
	private Cube body;
	private Cube leg1;
	private Cube leg2;
	private Cube leg3;
	private Cube leg4;

	public CreeperModel() {
		this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		this.hair = new Cube(32, 0);
		this.hair.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F + 0.5F);
		this.body = new Cube(16, 16);
		this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
		this.leg1 = new Cube(0, 16);
		this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F);
		this.leg1.setPos(-2.0F, 12.0F, 4.0F);
		this.leg2 = new Cube(0, 16);
		this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F);
		this.leg2.setPos(2.0F, 12.0F, 4.0F);
		this.leg3 = new Cube(0, 16);
		this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F);
		this.leg3.setPos(-2.0F, 12.0F, -4.0F);
		this.leg4 = new Cube(0, 16);
		this.leg4.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F);
		this.leg4.setPos(2.0F, 12.0F, -4.0F);
	}

	public final void render(float var1, float var2, float var3, float var4, float var5, float var6) {
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		this.head.yRot = var4 / (180.0F / (float)Math.PI);
		this.head.xRot = var5 / (180.0F / (float)Math.PI);
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
