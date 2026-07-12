package com.mojang.minecraft.model;

import org.lwjgl.opengl.GL11;

import com.mojang.util.Mth;

public final class SpiderModel extends BaseModel {
	private Cube head = new Cube(32, 4);
	private Cube neck;
	private Cube body;
	private Cube leg1;
	private Cube leg2;
	private Cube leg3;
	private Cube leg4;
	private Cube leg5;
	private Cube leg6;
	private Cube leg7;
	private Cube leg8;

	public SpiderModel() {
		this.head.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, 0.0F);
		this.head.setPos(0.0F, 0.0F, -3.0F);
		this.neck = new Cube(0, 0);
		this.neck.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F);
		this.body = new Cube(0, 12);
		this.body.addBox(-5.0F, -4.0F, -6.0F, 10, 8, 12, 0.0F);
		this.body.setPos(0.0F, 0.0F, 9.0F);
		this.leg1 = new Cube(18, 0);
		this.leg1.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.leg1.setPos(-4.0F, 0.0F, 2.0F);
		this.leg2 = new Cube(18, 0);
		this.leg2.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.leg2.setPos(4.0F, 0.0F, 2.0F);
		this.leg3 = new Cube(18, 0);
		this.leg3.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.leg3.setPos(-4.0F, 0.0F, 1.0F);
		this.leg4 = new Cube(18, 0);
		this.leg4.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.leg4.setPos(4.0F, 0.0F, 1.0F);
		this.leg5 = new Cube(18, 0);
		this.leg5.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.leg5.setPos(-4.0F, 0.0F, 0.0F);
		this.leg6 = new Cube(18, 0);
		this.leg6.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.leg6.setPos(4.0F, 0.0F, 0.0F);
		this.leg7 = new Cube(18, 0);
		this.leg7.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.leg7.setPos(-4.0F, 0.0F, -1.0F);
		this.leg8 = new Cube(18, 0);
		this.leg8.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.leg8.setPos(4.0F, 0.0F, -1.0F);
	}

	public final void render(float var1, float var2, float var3, float var4, float var5, float var6) {
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		this.head.yRot = var4 / (180.0F / (float)Math.PI);
		this.head.xRot = var5 / (180.0F / (float)Math.PI);
		var4 = (float)Math.PI * 0.25F;
		this.leg1.zRot = -var4;
		this.leg2.zRot = var4;
		this.leg3.zRot = -var4 * 0.74F;
		this.leg4.zRot = var4 * 0.74F;
		this.leg5.zRot = -var4 * 0.74F;
		this.leg6.zRot = var4 * 0.74F;
		this.leg7.zRot = -var4;
		this.leg8.zRot = var4;
		var4 = (float)Math.PI * 0.125F;
		this.leg1.yRot = var4 * 2.0F;
		this.leg2.yRot = -var4 * 2.0F;
		this.leg3.yRot = var4;
		this.leg4.yRot = -var4;
		this.leg5.yRot = -var4;
		this.leg6.yRot = var4;
		this.leg7.yRot = -var4 * 2.0F;
		this.leg8.yRot = var4 * 2.0F;
		var4 = -(Mth.cos(var1 * 0.6662F * 2.0F) * 0.4F) * var2;
		var5 = -(Mth.cos(var1 * 0.6662F * 2.0F + (float)Math.PI) * 0.4F) * var2;
		float var7 = -(Mth.cos(var1 * 0.6662F * 2.0F + (float)Math.PI * 0.5F) * 0.4F) * var2;
		float var8 = -(Mth.cos(var1 * 0.6662F * 2.0F + (float)Math.PI * 3.0F / 2.0F) * 0.4F) * var2;
		float var9 = Math.abs(Mth.sin(var1 * 0.6662F) * 0.4F) * var2;
		float var10 = Math.abs(Mth.sin(var1 * 0.6662F + (float)Math.PI) * 0.4F) * var2;
		float var11 = Math.abs(Mth.sin(var1 * 0.6662F + (float)Math.PI * 0.5F) * 0.4F) * var2;
		var2 = Math.abs(Mth.sin(var1 * 0.6662F + (float)Math.PI * 3.0F / 2.0F) * 0.4F) * var2;
		this.leg1.yRot += var4;
		this.leg2.yRot -= var4;
		this.leg3.yRot += var5;
		this.leg4.yRot -= var5;
		this.leg5.yRot += var7;
		this.leg6.yRot -= var7;
		this.leg7.yRot += var8;
		this.leg8.yRot -= var8;
		this.leg1.zRot += var9;
		this.leg2.zRot -= var9;
		this.leg3.zRot += var10;
		this.leg4.zRot -= var10;
		this.leg5.zRot += var11;
		this.leg6.zRot -= var11;
		this.leg7.zRot += var2;
		this.leg8.zRot -= var2;
		this.head.render(var6);
		this.neck.render(var6);
		this.body.render(var6);
		this.leg1.render(var6);
		this.leg2.render(var6);
		this.leg3.render(var6);
		this.leg4.render(var6);
		this.leg5.render(var6);
		this.leg6.render(var6);
		this.leg7.render(var6);
		this.leg8.render(var6);
	}
}
