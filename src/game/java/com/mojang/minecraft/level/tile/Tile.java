package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.HitResult;
import com.mojang.minecraft.item.Item;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.liquid.Liquid;
import com.mojang.minecraft.model.Vec3;
import com.mojang.minecraft.particle.ParticleEngine;
import com.mojang.minecraft.particle.TerrainParticle;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.renderer.Tesselator;
import java.util.Random;

public class Tile {
	protected static Random random = new Random();
	public static final Tile[] tiles = new Tile[256];
	public static final boolean[] shouldTick = new boolean[256];
	private static boolean[] isSolid = new boolean[256];
	private static boolean[] isOpaque = new boolean[256];
	public static final boolean[] isLiquid = new boolean[256];
	private static int[] tickSpeed = new int[256];
	public static final Tile rock;
	public static final Tile grass;
	public static final Tile dirt;
	public static final Tile stoneBrick;
	public static final Tile wood;
	public static final Tile bush;
	public static final Tile unbreakable;
	public static final Tile water;
	public static final Tile calmWater;
	public static final Tile lava;
	public static final Tile calmLava;
	public static final Tile sand;
	public static final Tile gravel;
	public static final Tile goldOre;
	public static final Tile ironOre;
	public static final Tile coalOre;
	public static final Tile log;
	public static final Tile leaf;
	public static final Tile sponge;
	public static final Tile glass;
	public static final Tile clothRed;
	public static final Tile clothOrange;
	public static final Tile clothYellow;
	public static final Tile clothChartreuse;
	public static final Tile clothGreen;
	public static final Tile clothSpringGreen;
	public static final Tile clothCyan;
	public static final Tile clothCapri;
	public static final Tile clothUltramarine;
	public static final Tile clothViolet;
	public static final Tile clothPurple;
	public static final Tile clothMagenta;
	public static final Tile clothRose;
	public static final Tile clothDarkGray;
	public static final Tile clothGray;
	public static final Tile clothWhite;
	public static final Tile flower;
	public static final Tile rose;
	public static final Tile mushroom1;
	public static final Tile mushroom2;
	public static final Tile gold;
	public static final Tile iron;
	public static final Tile slabFull;
	public static final Tile slabHalf;
	public static final Tile brick;
	public static final Tile tnt;
	public static final Tile bookshelf;
	public static final Tile mossStone;
	public static final Tile obsidian;
	public int tex;
	public final int id;
	public Tile.SoundType soundType;
	private int destroyProgress;
	private boolean explodeable;
	public float xx0;
	public float yy0;
	public float zz0;
	public float xx1;
	public float yy1;
	public float zz1;
	public float particleGravity;

	protected Tile(int var1) {
		this.explodeable = true;
		tiles[var1] = this;
		this.id = var1;
		this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		isSolid[var1] = this.isSolid();
		isOpaque[var1] = this.isOpaque();
		isLiquid[var1] = false;
	}

	public boolean isOpaque() {
		return true;
	}

	protected final Tile setSoundAndGravity(Tile.SoundType var1, float var2, float var3, float var4) {
		this.particleGravity = var3;
		this.soundType = var1;
		this.destroyProgress = (int)(var4 * 20.0F);
		return this;
	}

	protected final void setTicking(boolean var1) {
		shouldTick[this.id] = var1;
	}

	protected final void setShape(float var1, float var2, float var3, float var4, float var5, float var6) {
		this.xx0 = var1;
		this.yy0 = var2;
		this.zz0 = var3;
		this.xx1 = var4;
		this.yy1 = var5;
		this.zz1 = var6;
	}

	protected Tile(int var1, int var2) {
		this(var1);
		this.tex = var2;
	}

	public final void setTickSpeed(int var1) {
		tickSpeed[this.id] = 16;
	}

	public void render(Tesselator var1) {
		float var2 = 0.5F;
		float var3 = 0.8F;
		float var4 = 0.6F;
		var1.color(var2, var2, var2);
		this.renderFace(var1, -2, 0, 0, 0);
		var1.color(1.0F, 1.0F, 1.0F);
		this.renderFace(var1, -2, 0, 0, 1);
		var1.color(var3, var3, var3);
		this.renderFace(var1, -2, 0, 0, 2);
		var1.color(var3, var3, var3);
		this.renderFace(var1, -2, 0, 0, 3);
		var1.color(var4, var4, var4);
		this.renderFace(var1, -2, 0, 0, 4);
		var1.color(var4, var4, var4);
		this.renderFace(var1, -2, 0, 0, 5);
	}

	protected float getBrightness(Level var1, int var2, int var3, int var4) {
		return var1.getBrightness(var2, var3, var4);
	}

	public boolean shouldRenderFace(Level var1, int var2, int var3, int var4, int var5) {
		return !var1.isSolidTile(var2, var3, var4);
	}

	protected int getTexture(int var1) {
		return this.tex;
	}

	public void renderFace(Tesselator var1, int var2, int var3, int var4, int var5) {
		int var6 = this.getTexture(var5);
		this.renderFaceNoTexture(var1, var2, var3, var4, var5, var6);
	}

	public final void renderFaceNoTexture(Tesselator var1, int var2, int var3, int var4, int var5, int var6) {
		int var7 = var6 % 16 << 4;
		int var8 = var6 / 16 << 4;
		float var9 = (float)var7 / 256.0F;
		float var17 = ((float)var7 + 15.99F) / 256.0F;
		float var10 = (float)var8 / 256.0F;
		float var11 = ((float)var8 + 15.99F) / 256.0F;
		if(var5 >= 2 && var6 < 240) {
			if(this.yy0 >= 0.0F && this.yy1 <= 1.0F) {
				var10 = ((float)var8 + this.yy0 * 15.99F) / 256.0F;
				var11 = ((float)var8 + this.yy1 * 15.99F) / 256.0F;
			} else {
				var10 = (float)var8 / 256.0F;
				var11 = ((float)var8 + 15.99F) / 256.0F;
			}
		}

		float var16 = (float)var2 + this.xx0;
		float var14 = (float)var2 + this.xx1;
		float var18 = (float)var3 + this.yy0;
		float var15 = (float)var3 + this.yy1;
		float var12 = (float)var4 + this.zz0;
		float var13 = (float)var4 + this.zz1;
		if(var5 == 0) {
			var1.vertexUV(var16, var18, var13, var9, var11);
			var1.vertexUV(var16, var18, var12, var9, var10);
			var1.vertexUV(var14, var18, var12, var17, var10);
			var1.vertexUV(var14, var18, var13, var17, var11);
		} else if(var5 == 1) {
			var1.vertexUV(var14, var15, var13, var17, var11);
			var1.vertexUV(var14, var15, var12, var17, var10);
			var1.vertexUV(var16, var15, var12, var9, var10);
			var1.vertexUV(var16, var15, var13, var9, var11);
		} else if(var5 == 2) {
			var1.vertexUV(var16, var15, var12, var17, var10);
			var1.vertexUV(var14, var15, var12, var9, var10);
			var1.vertexUV(var14, var18, var12, var9, var11);
			var1.vertexUV(var16, var18, var12, var17, var11);
		} else if(var5 == 3) {
			var1.vertexUV(var16, var15, var13, var9, var10);
			var1.vertexUV(var16, var18, var13, var9, var11);
			var1.vertexUV(var14, var18, var13, var17, var11);
			var1.vertexUV(var14, var15, var13, var17, var10);
		} else if(var5 == 4) {
			var1.vertexUV(var16, var15, var13, var17, var10);
			var1.vertexUV(var16, var15, var12, var9, var10);
			var1.vertexUV(var16, var18, var12, var9, var11);
			var1.vertexUV(var16, var18, var13, var17, var11);
		} else if(var5 == 5) {
			var1.vertexUV(var14, var18, var13, var9, var11);
			var1.vertexUV(var14, var18, var12, var17, var11);
			var1.vertexUV(var14, var15, var12, var17, var10);
			var1.vertexUV(var14, var15, var13, var9, var10);
		}
	}

	public final void renderBackFace(Tesselator var1, int var2, int var3, int var4, int var5) {
		int var6 = this.getTexture(var5);
		float var7 = (float)(var6 % 16) / 16.0F;
		float var8 = var7 + 0.999F / 16.0F;
		float var16 = (float)(var6 / 16) / 16.0F;
		float var9 = var16 + 0.999F / 16.0F;
		float var10 = (float)var2 + this.xx0;
		float var14 = (float)var2 + this.xx1;
		float var11 = (float)var3 + this.yy0;
		float var15 = (float)var3 + this.yy1;
		float var12 = (float)var4 + this.zz0;
		float var13 = (float)var4 + this.zz1;
		if(var5 == 0) {
			var1.vertexUV(var14, var11, var13, var8, var9);
			var1.vertexUV(var14, var11, var12, var8, var16);
			var1.vertexUV(var10, var11, var12, var7, var16);
			var1.vertexUV(var10, var11, var13, var7, var9);
		}

		if(var5 == 1) {
			var1.vertexUV(var10, var15, var13, var7, var9);
			var1.vertexUV(var10, var15, var12, var7, var16);
			var1.vertexUV(var14, var15, var12, var8, var16);
			var1.vertexUV(var14, var15, var13, var8, var9);
		}

		if(var5 == 2) {
			var1.vertexUV(var10, var11, var12, var8, var9);
			var1.vertexUV(var14, var11, var12, var7, var9);
			var1.vertexUV(var14, var15, var12, var7, var16);
			var1.vertexUV(var10, var15, var12, var8, var16);
		}

		if(var5 == 3) {
			var1.vertexUV(var14, var15, var13, var8, var16);
			var1.vertexUV(var14, var11, var13, var8, var9);
			var1.vertexUV(var10, var11, var13, var7, var9);
			var1.vertexUV(var10, var15, var13, var7, var16);
		}

		if(var5 == 4) {
			var1.vertexUV(var10, var11, var13, var8, var9);
			var1.vertexUV(var10, var11, var12, var7, var9);
			var1.vertexUV(var10, var15, var12, var7, var16);
			var1.vertexUV(var10, var15, var13, var8, var16);
		}

		if(var5 == 5) {
			var1.vertexUV(var14, var15, var13, var7, var16);
			var1.vertexUV(var14, var15, var12, var8, var16);
			var1.vertexUV(var14, var11, var12, var8, var9);
			var1.vertexUV(var14, var11, var13, var7, var9);
		}

	}

	public final AABB getAABB(int var1, int var2, int var3) {
		return new AABB((float)var1 + this.xx0, (float)var2 + this.yy0, (float)var3 + this.zz0, (float)var1 + this.xx1, (float)var2 + this.yy1, (float)var3 + this.zz1);
	}

	public AABB getTileAABB(int var1, int var2, int var3) {
		return new AABB((float)var1 + this.xx0, (float)var2 + this.yy0, (float)var3 + this.zz0, (float)var1 + this.xx1, (float)var2 + this.yy1, (float)var3 + this.zz1);
	}

	public boolean blocksLight() {
		return true;
	}

	public boolean isSolid() {
		return true;
	}

	public void tick(Level var1, int var2, int var3, int var4, Random var5) {
	}

	public void destroy(Level var1, int var2, int var3, int var4, ParticleEngine var5) {
		for(int var6 = 0; var6 < 4; ++var6) {
			for(int var7 = 0; var7 < 4; ++var7) {
				for(int var8 = 0; var8 < 4; ++var8) {
					float var9 = (float)var2 + ((float)var6 + 0.5F) / (float)4;
					float var10 = (float)var3 + ((float)var7 + 0.5F) / (float)4;
					float var11 = (float)var4 + ((float)var8 + 0.5F) / (float)4;
					var5.addParticle(new TerrainParticle(var1, var9, var10, var11, var9 - (float)var2 - 0.5F, var10 - (float)var3 - 0.5F, var11 - (float)var4 - 0.5F, this));
				}
			}
		}

	}

	public final void destroy(Level var1, int var2, int var3, int var4, int var5, ParticleEngine var6) {
		float var7 = 0.1F;
		float var8 = (float)var2 + random.nextFloat() * (this.xx1 - this.xx0 - var7 * 2.0F) + var7 + this.xx0;
		float var9 = (float)var3 + random.nextFloat() * (this.yy1 - this.yy0 - var7 * 2.0F) + var7 + this.yy0;
		float var10 = (float)var4 + random.nextFloat() * (this.zz1 - this.zz0 - var7 * 2.0F) + var7 + this.zz0;
		if(var5 == 0) {
			var9 = (float)var3 + this.yy0 - var7;
		}

		if(var5 == 1) {
			var9 = (float)var3 + this.yy1 + var7;
		}

		if(var5 == 2) {
			var10 = (float)var4 + this.zz0 - var7;
		}

		if(var5 == 3) {
			var10 = (float)var4 + this.zz1 + var7;
		}

		if(var5 == 4) {
			var8 = (float)var2 + this.xx0 - var7;
		}

		if(var5 == 5) {
			var8 = (float)var2 + this.xx1 + var7;
		}

		var6.addParticle((new TerrainParticle(var1, var8, var9, var10, 0.0F, 0.0F, 0.0F, this)).setPower(0.2F).scale(0.6F));
	}

	public Liquid getLiquidType() {
		return Liquid.none;
	}

	public void neighborChanged(Level var1, int var2, int var3, int var4, int var5) {
	}

	public void onPlace(Level var1, int var2, int var3, int var4) {
	}

	public int getTickDelay() {
		return 0;
	}

	public void onTileAdded(Level var1, int var2, int var3, int var4) {
	}

	public void onTileRemoved(Level var1, int var2, int var3, int var4) {
	}

	public int resourceCount() {
		return 1;
	}

	public int getId() {
		return this.id;
	}

	public final int getDestroyProgress() {
		return this.destroyProgress;
	}

	public void spawnResources(Level var1, int var2, int var3, int var4) {
		this.spawnResources(var1, var2, var3, var4, 1.0F);
	}

	public void spawnResources(Level var1, int var2, int var3, int var4, float var5) {
		if(!var1.creativeMode) {
			int var6 = this.resourceCount();

			for(int var7 = 0; var7 < var6; ++var7) {
				if(random.nextFloat() <= var5) {
					float var8 = 0.7F;
					float var9 = random.nextFloat() * var8 + (1.0F - var8) * 0.5F;
					float var10 = random.nextFloat() * var8 + (1.0F - var8) * 0.5F;
					var8 = random.nextFloat() * var8 + (1.0F - var8) * 0.5F;
					var1.addEntity(new Item(var1, (float)var2 + var9, (float)var3 + var10, (float)var4 + var8, this.getId()));
				}
			}

		}
	}

	public void renderGuiTile(Tesselator var1) {
		var1.begin();

		for(int var2 = 0; var2 < 6; ++var2) {
			if(var2 == 0) {
				var1.setNormal(0.0F, 1.0F, 0.0F);
			}

			if(var2 == 1) {
				var1.setNormal(0.0F, -1.0F, 0.0F);
			}

			if(var2 == 2) {
				var1.setNormal(0.0F, 0.0F, 1.0F);
			}

			if(var2 == 3) {
				var1.setNormal(0.0F, 0.0F, -1.0F);
			}

			if(var2 == 4) {
				var1.setNormal(1.0F, 0.0F, 0.0F);
			}

			if(var2 == 5) {
				var1.setNormal(-1.0F, 0.0F, 0.0F);
			}

			this.renderFace(var1, 0, 0, 0, var2);
		}

		var1.end();
	}

	public final boolean isExplodeable() {
		return this.explodeable;
	}

	public final HitResult clip(int var1, int var2, int var3, Vec3 var4, Vec3 var5) {
		var4 = var4.add((float)(-var1), (float)(-var2), (float)(-var3));
		var5 = var5.add((float)(-var1), (float)(-var2), (float)(-var3));
		Vec3 var6 = var4.clipX(var5, this.xx0);
		Vec3 var7 = var4.clipX(var5, this.xx1);
		Vec3 var8 = var4.clipY(var5, this.yy0);
		Vec3 var9 = var4.clipY(var5, this.yy1);
		Vec3 var10 = var4.clipZ(var5, this.zz0);
		var5 = var4.clipZ(var5, this.zz1);
		if(!this.containsX(var6)) {
			var6 = null;
		}

		if(!this.containsX(var7)) {
			var7 = null;
		}

		if(!this.containsY(var8)) {
			var8 = null;
		}

		if(!this.containsY(var9)) {
			var9 = null;
		}

		if(!this.containsZ(var10)) {
			var10 = null;
		}

		if(!this.containsZ(var5)) {
			var5 = null;
		}

		Vec3 var11 = null;
		if(var6 != null) {
			var11 = var6;
		}

		if(var7 != null && (var11 == null || var4.distanceTo(var7) < var4.distanceTo(var11))) {
			var11 = var7;
		}

		if(var8 != null && (var11 == null || var4.distanceTo(var8) < var4.distanceTo(var11))) {
			var11 = var8;
		}

		if(var9 != null && (var11 == null || var4.distanceTo(var9) < var4.distanceTo(var11))) {
			var11 = var9;
		}

		if(var10 != null && (var11 == null || var4.distanceTo(var10) < var4.distanceTo(var11))) {
			var11 = var10;
		}

		if(var5 != null && (var11 == null || var4.distanceTo(var5) < var4.distanceTo(var11))) {
			var11 = var5;
		}

		if(var11 == null) {
			return null;
		} else {
			byte var12 = -1;
			if(var11 == var6) {
				var12 = 4;
			}

			if(var11 == var7) {
				var12 = 5;
			}

			if(var11 == var8) {
				var12 = 0;
			}

			if(var11 == var9) {
				var12 = 1;
			}

			if(var11 == var10) {
				var12 = 2;
			}

			if(var11 == var5) {
				var12 = 3;
			}

			return new HitResult(var1, var2, var3, var12, var11.add((float)var1, (float)var2, (float)var3));
		}
	}

	private boolean containsX(Vec3 var1) {
		return var1 == null ? false : var1.y >= this.yy0 && var1.y <= this.yy1 && var1.z >= this.zz0 && var1.z <= this.zz1;
	}

	private boolean containsY(Vec3 var1) {
		return var1 == null ? false : var1.x >= this.xx0 && var1.x <= this.xx1 && var1.z >= this.zz0 && var1.z <= this.zz1;
	}

	private boolean containsZ(Vec3 var1) {
		return var1 == null ? false : var1.x >= this.xx0 && var1.x <= this.xx1 && var1.y >= this.yy0 && var1.y <= this.yy1;
	}

	public void wasExploded(Level var1, int var2, int var3, int var4) {
	}

	public boolean render(Level var1, int var2, int var3, int var4, Tesselator var5) {
		boolean var6 = false;
		float var7 = 0.5F;
		float var8 = 0.8F;
		float var9 = 0.6F;
		float var10;
		if(this.shouldRenderFace(var1, var2, var3 - 1, var4, 0)) {
			var10 = this.getBrightness(var1, var2, var3 - 1, var4);
			var5.color(var7 * var10, var7 * var10, var7 * var10);
			this.renderFace(var5, var2, var3, var4, 0);
			var6 = true;
		}

		if(this.shouldRenderFace(var1, var2, var3 + 1, var4, 1)) {
			var10 = this.getBrightness(var1, var2, var3 + 1, var4);
			var5.color(var10 * 1.0F, var10 * 1.0F, var10 * 1.0F);
			this.renderFace(var5, var2, var3, var4, 1);
			var6 = true;
		}

		if(this.shouldRenderFace(var1, var2, var3, var4 - 1, 2)) {
			var10 = this.getBrightness(var1, var2, var3, var4 - 1);
			var5.color(var8 * var10, var8 * var10, var8 * var10);
			this.renderFace(var5, var2, var3, var4, 2);
			var6 = true;
		}

		if(this.shouldRenderFace(var1, var2, var3, var4 + 1, 3)) {
			var10 = this.getBrightness(var1, var2, var3, var4 + 1);
			var5.color(var8 * var10, var8 * var10, var8 * var10);
			this.renderFace(var5, var2, var3, var4, 3);
			var6 = true;
		}

		if(this.shouldRenderFace(var1, var2 - 1, var3, var4, 4)) {
			var10 = this.getBrightness(var1, var2 - 1, var3, var4);
			var5.color(var9 * var10, var9 * var10, var9 * var10);
			this.renderFace(var5, var2, var3, var4, 4);
			var6 = true;
		}

		if(this.shouldRenderFace(var1, var2 + 1, var3, var4, 5)) {
			var10 = this.getBrightness(var1, var2 + 1, var3, var4);
			var5.color(var9 * var10, var9 * var10, var9 * var10);
			this.renderFace(var5, var2, var3, var4, 5);
			var6 = true;
		}

		return var6;
	}

	public int getRenderLayer() {
		return 0;
	}

	static {
		Tile var10000 = (new StoneTile(1, 1)).setSoundAndGravity(Tile.SoundType.stone, 1.0F, 1.0F, 1.0F);
		boolean var0 = false;
		Tile var1 = var10000;
		var1.explodeable = false;
		rock = var1;
		grass = (new GrassTile(2)).setSoundAndGravity(Tile.SoundType.grass, 0.9F, 1.0F, 0.6F);
		dirt = (new DirtTile(3, 2)).setSoundAndGravity(Tile.SoundType.grass, 0.8F, 1.0F, 0.5F);
		var10000 = (new Tile(4, 16)).setSoundAndGravity(Tile.SoundType.stone, 1.0F, 1.0F, 1.5F);
		var0 = false;
		var1 = var10000;
		var1.explodeable = false;
		stoneBrick = var1;
		wood = (new Tile(5, 4)).setSoundAndGravity(Tile.SoundType.wood, 1.0F, 1.0F, 1.5F);
		bush = (new Bush(6, 15)).setSoundAndGravity(Tile.SoundType.none, 0.7F, 1.0F, 0.0F);
		var10000 = (new Tile(7, 17)).setSoundAndGravity(Tile.SoundType.stone, 1.0F, 1.0F, 999.0F);
		var0 = false;
		var1 = var10000;
		var1.explodeable = false;
		unbreakable = var1;
		water = (new LiquidTile(8, Liquid.water)).setSoundAndGravity(Tile.SoundType.none, 1.0F, 1.0F, 100.0F);
		calmWater = (new CalmLiquidTile(9, Liquid.water)).setSoundAndGravity(Tile.SoundType.none, 1.0F, 1.0F, 100.0F);
		lava = (new LiquidTile(10, Liquid.lava)).setSoundAndGravity(Tile.SoundType.none, 1.0F, 1.0F, 100.0F);
		calmLava = (new CalmLiquidTile(11, Liquid.lava)).setSoundAndGravity(Tile.SoundType.none, 1.0F, 1.0F, 100.0F);
		sand = (new FallingTile(12, 18)).setSoundAndGravity(Tile.SoundType.gravel, 0.8F, 1.0F, 0.5F);
		gravel = (new FallingTile(13, 19)).setSoundAndGravity(Tile.SoundType.gravel, 0.8F, 1.0F, 0.6F);
		var10000 = (new OreTile(14, 32)).setSoundAndGravity(Tile.SoundType.stone, 1.0F, 1.0F, 3.0F);
		var0 = false;
		var1 = var10000;
		var1.explodeable = false;
		goldOre = var1;
		var10000 = (new OreTile(15, 33)).setSoundAndGravity(Tile.SoundType.stone, 1.0F, 1.0F, 3.0F);
		var0 = false;
		var1 = var10000;
		var1.explodeable = false;
		ironOre = var1;
		var10000 = (new OreTile(16, 34)).setSoundAndGravity(Tile.SoundType.stone, 1.0F, 1.0F, 3.0F);
		var0 = false;
		var1 = var10000;
		var1.explodeable = false;
		coalOre = var1;
		log = (new LogTile(17)).setSoundAndGravity(Tile.SoundType.wood, 1.0F, 1.0F, 2.5F);
		leaf = (new LeafTile(18, 22)).setSoundAndGravity(Tile.SoundType.grass, 1.0F, 0.4F, 0.2F);
		sponge = (new SpongeTile(19)).setSoundAndGravity(Tile.SoundType.cloth, 1.0F, 0.9F, 0.6F);
		glass = (new GlassTile(20, 49, false)).setSoundAndGravity(Tile.SoundType.metal, 1.0F, 1.0F, 0.3F);
		clothRed = (new Tile(21, 64)).setSoundAndGravity(Tile.SoundType.cloth, 1.0F, 1.0F, 0.8F);
		clothOrange = (new Tile(22, 65)).setSoundAndGravity(Tile.SoundType.cloth, 1.0F, 1.0F, 0.8F);
		clothYellow = (new Tile(23, 66)).setSoundAndGravity(Tile.SoundType.cloth, 1.0F, 1.0F, 0.8F);
		clothChartreuse = (new Tile(24, 67)).setSoundAndGravity(Tile.SoundType.cloth, 1.0F, 1.0F, 0.8F);
		clothGreen = (new Tile(25, 68)).setSoundAndGravity(Tile.SoundType.cloth, 1.0F, 1.0F, 0.8F);
		clothSpringGreen = (new Tile(26, 69)).setSoundAndGravity(Tile.SoundType.cloth, 1.0F, 1.0F, 0.8F);
		clothCyan = (new Tile(27, 70)).setSoundAndGravity(Tile.SoundType.cloth, 1.0F, 1.0F, 0.8F);
		clothCapri = (new Tile(28, 71)).setSoundAndGravity(Tile.SoundType.cloth, 1.0F, 1.0F, 0.8F);
		clothUltramarine = (new Tile(29, 72)).setSoundAndGravity(Tile.SoundType.cloth, 1.0F, 1.0F, 0.8F);
		clothViolet = (new Tile(30, 73)).setSoundAndGravity(Tile.SoundType.cloth, 1.0F, 1.0F, 0.8F);
		clothPurple = (new Tile(31, 74)).setSoundAndGravity(Tile.SoundType.cloth, 1.0F, 1.0F, 0.8F);
		clothMagenta = (new Tile(32, 75)).setSoundAndGravity(Tile.SoundType.cloth, 1.0F, 1.0F, 0.8F);
		clothRose = (new Tile(33, 76)).setSoundAndGravity(Tile.SoundType.cloth, 1.0F, 1.0F, 0.8F);
		clothDarkGray = (new Tile(34, 77)).setSoundAndGravity(Tile.SoundType.cloth, 1.0F, 1.0F, 0.8F);
		clothGray = (new Tile(35, 78)).setSoundAndGravity(Tile.SoundType.cloth, 1.0F, 1.0F, 0.8F);
		clothWhite = (new Tile(36, 79)).setSoundAndGravity(Tile.SoundType.cloth, 1.0F, 1.0F, 0.8F);
		flower = (new Flower(37, 13)).setSoundAndGravity(Tile.SoundType.none, 0.7F, 1.0F, 0.0F);
		rose = (new Flower(38, 12)).setSoundAndGravity(Tile.SoundType.none, 0.7F, 1.0F, 0.0F);
		mushroom1 = (new Mushroom(39, 29)).setSoundAndGravity(Tile.SoundType.none, 0.7F, 1.0F, 0.0F);
		mushroom2 = (new Mushroom(40, 28)).setSoundAndGravity(Tile.SoundType.none, 0.7F, 1.0F, 0.0F);
		var10000 = (new MetalTile(41, 40)).setSoundAndGravity(Tile.SoundType.metal, 0.7F, 1.0F, 3.0F);
		var0 = false;
		var1 = var10000;
		var1.explodeable = false;
		gold = var1;
		var10000 = (new MetalTile(42, 39)).setSoundAndGravity(Tile.SoundType.metal, 0.7F, 1.0F, 5.0F);
		var0 = false;
		var1 = var10000;
		var1.explodeable = false;
		iron = var1;
		var10000 = (new SlabTile(43, true)).setSoundAndGravity(Tile.SoundType.stone, 1.0F, 1.0F, 2.0F);
		var0 = false;
		var1 = var10000;
		var1.explodeable = false;
		slabFull = var1;
		var10000 = (new SlabTile(44, false)).setSoundAndGravity(Tile.SoundType.stone, 1.0F, 1.0F, 2.0F);
		var0 = false;
		var1 = var10000;
		var1.explodeable = false;
		slabHalf = var1;
		var10000 = (new Tile(45, 7)).setSoundAndGravity(Tile.SoundType.stone, 1.0F, 1.0F, 2.0F);
		var0 = false;
		var1 = var10000;
		var1.explodeable = false;
		brick = var1;
		tnt = (new TntTile(46, 8)).setSoundAndGravity(Tile.SoundType.cloth, 1.0F, 1.0F, 0.0F);
		bookshelf = (new BookshelfTile(47, 35)).setSoundAndGravity(Tile.SoundType.wood, 1.0F, 1.0F, 1.5F);
		var10000 = (new Tile(48, 36)).setSoundAndGravity(Tile.SoundType.stone, 1.0F, 1.0F, 1.0F);
		var0 = false;
		var1 = var10000;
		var1.explodeable = false;
		mossStone = var1;
		var10000 = (new StoneTile(49, 37)).setSoundAndGravity(Tile.SoundType.stone, 1.0F, 1.0F, 10.0F);
		var0 = false;
		var1 = var10000;
		var1.explodeable = false;
		obsidian = var1;
	}

	public static enum SoundType {
		none("-", 0, 0.0F, 0.0F),
		grass("grass", 4 , 0.6F, 1.0F),
		cloth("grass", 4 , 0.7F, 1.2F),
		gravel("gravel", 4 , 1.0F, 1.0F),
		stone("stone", 4 , 1.0F, 1.0F),
		metal("stone", 4 , 1.0F, 2.0F),
		wood("wood", 4 , 1.0F, 1.0F);

		public final String name;
		private final float volume;
		private final float pitch;
		public int num;

		private SoundType(String var3, int var2, float var4, float var5) {
			this.num = var2;
			this.name = var3;
			this.volume = var4;
			this.pitch = var5;
		}

		public final float getVolume() {
			return this.volume / (Tile.random.nextFloat() * 0.4F + 1.0F) * 0.5F;
		}

		public final float getPitch() {
			return this.pitch / (Tile.random.nextFloat() * 0.2F + 0.9F);
		}
		
		public static SoundType getSoundType(String sound) {
			switch (sound) {
			case "step.grass":
				return SoundType.grass;
			case "step.cloth":
				return SoundType.cloth;
			case "step.gravel":
				return SoundType.gravel;
			case "step.stone":
				return SoundType.stone;
			case "step.metal":
				return SoundType.metal;
			case "step.wood":
				return SoundType.wood;
			default:
				return SoundType.none;
			}
		}
	}
}
