package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.renderer.Tesselator;
import com.mojang.util.Mth;

import java.util.Random;

public class Flower extends Tile {
	protected Flower(int var1, int var2) {
		super(var1);
		this.tex = var2;
		this.setTicking(true);
		float var3 = 0.2F;
		this.setShape(0.5F - var3, 0.0F, 0.5F - var3, var3 + 0.5F, var3 * 3.0F, var3 + 0.5F);
	}

	public void tick(Level var1, int var2, int var3, int var4, Random var5) {
		if(!var1.growTrees) {
			int var6 = var1.getTile(var2, var3 - 1, var4);
			if(!var1.isLit(var2, var3, var4) || var6 != Tile.dirt.id && var6 != Tile.grass.id) {
				var1.setTile(var2, var3, var4, 0);
			}

		}
	}

	private void renderFlower(Tesselator var1, float var2, float var3, float var4) {
		int var15 = this.getTexture(15);
		int var5 = var15 % 16 << 4;
		int var7 = var15 / 16 << 4;
		float var16 = (float)var5 / 256.0F;
		float var17 = ((float)var5 + 15.99F) / 256.0F;
		float var6 = (float)var7 / 256.0F;
		float var18 = ((float)var7 + 15.99F) / 256.0F;

		for(int var8 = 0; var8 < 2; ++var8) {
			float var9 = (float)((double)Mth.sin((float)var8 * (float)Math.PI / 2.0F + (float)Math.PI * 0.25F) * 0.5D);
			float var10 = (float)((double)Mth.cos((float)var8 * (float)Math.PI / 2.0F + (float)Math.PI * 0.25F) * 0.5D);
			float var11 = var2 + 0.5F - var9;
			var9 += var2 + 0.5F;
			float var13 = var3 + 1.0F;
			float var14 = var4 + 0.5F - var10;
			var10 += var4 + 0.5F;
			var1.vertexUV(var11, var13, var14, var17, var6);
			var1.vertexUV(var9, var13, var10, var16, var6);
			var1.vertexUV(var9, var3, var10, var16, var18);
			var1.vertexUV(var11, var3, var14, var17, var18);
			var1.vertexUV(var9, var13, var10, var17, var6);
			var1.vertexUV(var11, var13, var14, var16, var6);
			var1.vertexUV(var11, var3, var14, var16, var18);
			var1.vertexUV(var9, var3, var10, var17, var18);
		}

	}

	public final AABB getTileAABB(int var1, int var2, int var3) {
		return null;
	}

	public final boolean blocksLight() {
		return false;
	}

	public final boolean isSolid() {
		return false;
	}

	public final void renderGuiTile(Tesselator var1) {
		var1.setNormal(0.0F, 1.0F, 0.0F);
		var1.begin();
		this.renderFlower(var1, 0.0F, 0.4F, -0.3F);
		var1.end();
	}

	public final boolean isOpaque() {
		return false;
	}

	public final boolean render(Level var1, int var2, int var3, int var4, Tesselator var5) {
		float var6 = var1.getBrightness(var2, var3, var4);
		var5.color(var6, var6, var6);
		this.renderFlower(var5, (float)var2, (float)var3, (float)var4);
		return true;
	}

	public final void render(Tesselator var1) {
		var1.color(1.0F, 1.0F, 1.0F);
		this.renderFlower(var1, (float)-2, 0.0F, 0.0F);
	}
}
