package com.mojang.minecraft.renderer;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Tile;
import com.mojang.minecraft.player.Player;
import com.mojang.util.Mth;

import org.lwjgl.opengl.GL11;

public final class Chunk {
	private Level level;
	private int lists = -1;
	private static Tesselator t = Tesselator.instance;
	public static int updates = 0;
	private int x0;
	private int y0;
	private int z0;
	private int x1;
	private int y1;
	private int z1;
	public boolean isInFrustum = false;
	private boolean[] skipRenderPass = new boolean[2];
	public boolean dirty;

	public Chunk(Level var1, int var2, int var3, int var4, int var5, int var6) {
		this.level = var1;
		this.x0 = var2;
		this.y0 = var3;
		this.z0 = var4;
		this.x1 = this.y1 = this.z1 = 16;
		Mth.sqrt_float((float)(this.x1 * this.x1 + this.y1 * this.y1 + this.z1 * this.z1));
		this.lists = var6;
		this.reset();
	}

	public final void rebuild() {
		++updates;
		int var1 = this.x0;
		int var2 = this.y0;
		int var3 = this.z0;
		int var4 = this.x0 + this.x1;
		int var5 = this.y0 + this.y1;
		int var6 = this.z0 + this.z1;

		int var7;
		for(var7 = 0; var7 < 2; ++var7) {
			this.skipRenderPass[var7] = true;
		}

		for(var7 = 0; var7 < 2; ++var7) {
			boolean var8 = false;
			boolean var9 = false;
			GL11.glNewList(this.lists + var7, GL11.GL_COMPILE);
			t.begin();

			for(int var10 = var1; var10 < var4; ++var10) {
				for(int var11 = var2; var11 < var5; ++var11) {
					for(int var12 = var3; var12 < var6; ++var12) {
						int var13 = this.level.getTile(var10, var11, var12);
						if(var13 > 0) {
							Tile var14 = Tile.tiles[var13];
							if(var14.getRenderLayer() != var7) {
								var8 = true;
							} else {
								var9 |= var14.render(this.level, var10, var11, var12, t);
							}
						}
					}
				}
			}

			t.end();
			GL11.glEndList();
			if(var9) {
				this.skipRenderPass[var7] = false;
			}

			if(!var8) {
				break;
			}
		}

	}

	public final float compare(Player var1) {
		float var2 = var1.x - (float)this.x0;
		float var3 = var1.y - (float)this.y0;
		float var4 = var1.z - (float)this.z0;
		return var2 * var2 + var3 * var3 + var4 * var4;
	}

	private void reset() {
		for(int var1 = 0; var1 < 2; ++var1) {
			this.skipRenderPass[var1] = true;
		}

	}

	public final void clear() {
		this.reset();
		this.level = null;
	}

	public final int render(int[] var1, int var2, int var3) {
		if(!this.isInFrustum) {
			return var2;
		} else {
			if(!this.skipRenderPass[var3]) {
				var1[var2++] = this.lists + var3;
			}

			return var2;
		}
	}

	public final void isInFrustum(Frustum var1) {
		this.isInFrustum = var1.cubeInFrustum((float)this.x0, (float)this.y0, (float)this.z0, (float)(this.x0 + this.x1), (float)(this.y0 + this.y1), (float)(this.z0 + this.z1));
	}
}
