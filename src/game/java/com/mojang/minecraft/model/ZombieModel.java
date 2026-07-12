package com.mojang.minecraft.model;

import com.mojang.util.Mth;

public class ZombieModel extends HumanoidModel {
	public final void setupAnim(float var1, float var2, float var3, float var4, float var5, float var6) {
		super.setupAnim(var1, var2, var3, var4, var5, var6);
		var1 = Mth.sin(this.rot * (float)Math.PI);
		var2 = Mth.sin((1.0F - (1.0F - this.rot) * (1.0F - this.rot)) * (float)Math.PI);
		this.rightArm.zRot = 0.0F;
		this.leftArm.zRot = 0.0F;
		this.rightArm.yRot = -(0.1F - var1 * 0.6F);
		this.leftArm.yRot = 0.1F - var1 * 0.6F;
		this.rightArm.xRot = (float)Math.PI * -0.5F;
		this.leftArm.xRot = (float)Math.PI * -0.5F;
		this.rightArm.xRot -= var1 * 1.2F - var2 * 0.4F;
		this.leftArm.xRot -= var1 * 1.2F - var2 * 0.4F;
		this.rightArm.zRot += Mth.cos(var3 * 0.09F) * 0.05F + 0.05F;
		this.leftArm.zRot -= Mth.cos(var3 * 0.09F) * 0.05F + 0.05F;
		this.rightArm.xRot += Mth.sin(var3 * 0.067F) * 0.05F;
		this.leftArm.xRot -= Mth.sin(var3 * 0.067F) * 0.05F;
	}
}
