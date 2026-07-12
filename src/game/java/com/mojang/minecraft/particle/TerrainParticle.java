package com.mojang.minecraft.particle;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Tile;
import com.mojang.minecraft.renderer.Tesselator;

public class TerrainParticle extends Particle {
	private static final long serialVersionUID = 1L;

	public TerrainParticle(Level var1, float var2, float var3, float var4, float var5, float var6, float var7, Tile var8) {
		super(var1, var2, var3, var4, var5, var6, var7);
		this.tex = var8.tex;
		this.gravity = var8.particleGravity;
		this.rCol = this.gCol = this.bCol = 0.6F;
	}

	public int getParticleTexture() {
		return 1;
	}

	public void render(Tesselator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		float var8 = ((float)(this.tex % 16) + this.uo / 4.0F) / 16.0F;
		float var9 = var8 + 0.999F / 64.0F;
		float var10 = ((float)(this.tex / 16) + this.vo / 4.0F) / 16.0F;
		float var11 = var10 + 0.999F / 64.0F;
		float var12 = 0.1F * this.size;
		float var13 = this.xo + (this.x - this.xo) * var2;
		float var14 = this.yo + (this.y - this.yo) * var2;
		float var15 = this.zo + (this.z - this.zo) * var2;
		var2 = this.getBrightness(var2);
		var1.color(var2 * this.rCol, var2 * this.gCol, var2 * this.bCol);
		var1.vertexUV(var13 - var3 * var12 - var6 * var12, var14 - var4 * var12, var15 - var5 * var12 - var7 * var12, var8, var11);
		var1.vertexUV(var13 - var3 * var12 + var6 * var12, var14 + var4 * var12, var15 - var5 * var12 + var7 * var12, var8, var10);
		var1.vertexUV(var13 + var3 * var12 + var6 * var12, var14 + var4 * var12, var15 + var5 * var12 + var7 * var12, var9, var10);
		var1.vertexUV(var13 + var3 * var12 - var6 * var12, var14 - var4 * var12, var15 + var5 * var12 - var7 * var12, var9, var11);
	}
}
