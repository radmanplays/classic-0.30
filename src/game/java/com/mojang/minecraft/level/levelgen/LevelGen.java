package com.mojang.minecraft.level.levelgen;

import com.mojang.minecraft.LevelLoaderListener;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.levelgen.synth.Distort;
import com.mojang.minecraft.level.levelgen.synth.PerlinNoise;
import com.mojang.minecraft.level.tile.Tile;
import com.mojang.util.Mth;

import java.util.ArrayList;
import java.util.Random;

public final class LevelGen {
	private LevelLoaderListener levelLoaderListener;
	private int width;
	private int height;
	private int depth;
	private Random random = new Random();
	private byte[] blocks;
	private int waterLevel;
	private int[] coords = new int[1048576];

	public LevelGen(LevelLoaderListener var1) {
		this.levelLoaderListener = var1;
	}

	public final Level generateLevel(String var1, int var2, int var3, int var4) {
		this.levelLoaderListener.beginLevelLoading("Generating level");
		this.width = var2;
		this.height = var3;
		this.depth = 64;
		this.waterLevel = 32;
		this.blocks = new byte[var2 * var3 << 6];
		this.levelLoaderListener.levelLoadUpdate("Raising..");
		LevelGen var6 = this;
		Distort var7 = new Distort(new PerlinNoise(this.random, 8), new PerlinNoise(this.random, 8));
		Distort var8 = new Distort(new PerlinNoise(this.random, 8), new PerlinNoise(this.random, 8));
		PerlinNoise var9 = new PerlinNoise(this.random, 6);
		int[] var10 = new int[this.width * this.height];
		float var11 = 1.3F;

		int var12;
		int var14;
		for(var14 = 0; var14 < var6.width; ++var14) {
			var6.setNextPhase(var14 * 100 / (var6.width - 1));

			for(var12 = 0; var12 < var6.height; ++var12) {
				double var16 = var7.getValue((double)((float)var14 * var11), (double)((float)var12 * var11)) / 6.0D + (double)-4;
				double var18 = var8.getValue((double)((float)var14 * var11), (double)((float)var12 * var11)) / 5.0D + 10.0D + (double)-4;
				double var20 = var9.getValue((double)var14, (double)var12) / 8.0D;
				if(var20 > 0.0D) {
					var18 = var16;
				}

				double var22 = Math.max(var16, var18) / 2.0D;
				if(var22 < 0.0D) {
					var22 *= 0.8D;
				}

				var10[var14 + var12 * var6.width] = (int)var22;
			}
		}

		this.levelLoaderListener.levelLoadUpdate("Eroding..");
		int[] var33 = var10;
		var6 = this;
		var8 = new Distort(new PerlinNoise(this.random, 8), new PerlinNoise(this.random, 8));
		Distort var38 = new Distort(new PerlinNoise(this.random, 8), new PerlinNoise(this.random, 8));

		int var42;
		int var44;
		int var49;
		for(var42 = 0; var42 < var6.width; ++var42) {
			var6.setNextPhase(var42 * 100 / (var6.width - 1));

			for(var44 = 0; var44 < var6.height; ++var44) {
				double var13 = var8.getValue((double)(var42 << 1), (double)(var44 << 1)) / 8.0D;
				var12 = var38.getValue((double)(var42 << 1), (double)(var44 << 1)) > 0.0D ? 1 : 0;
				if(var13 > 2.0D) {
					var49 = var33[var42 + var44 * var6.width];
					var49 = ((var49 - var12) / 2 << 1) + var12;
					var33[var42 + var44 * var6.width] = var49;
				}
			}
		}

		this.levelLoaderListener.levelLoadUpdate("Soiling..");
		var33 = var10;
		var6 = this;
		int var36 = this.width;
		int var40 = this.height;
		var42 = this.depth;
		PerlinNoise var45 = new PerlinNoise(this.random, 8);

		int var17;
		int var19;
		int var46;
		int var54;
		int var56;
		for(var46 = 0; var46 < var36; ++var46) {
			var6.setNextPhase(var46 * 100 / (var6.width - 1));

			for(var14 = 0; var14 < var40; ++var14) {
				var12 = (int)(var45.getValue((double)var46, (double)var14) / 24.0D) - 4;
				var49 = var33[var46 + var14 * var36] + var6.waterLevel;
				var17 = var49 + var12;
				var33[var46 + var14 * var36] = Math.max(var49, var17);
				if(var33[var46 + var14 * var36] > var42 - 2) {
					var33[var46 + var14 * var36] = var42 - 2;
				}

				if(var33[var46 + var14 * var36] < 1) {
					var33[var46 + var14 * var36] = 1;
				}

				for(var54 = 0; var54 < var42; ++var54) {
					var19 = (var54 * var6.height + var14) * var6.width + var46;
					var56 = 0;
					if(var54 <= var49) {
						var56 = Tile.dirt.id;
					}

					if(var54 <= var17) {
						var56 = Tile.rock.id;
					}

					if(var54 == 0) {
						var56 = Tile.lava.id;
					}

					var6.blocks[var19] = (byte)var56;
				}
			}
		}

		this.levelLoaderListener.levelLoadUpdate("Carving..");
		boolean var37 = true;
		boolean var34 = false;
		var6 = this;
		var40 = this.width;
		var42 = this.height;
		var44 = this.depth;
		var46 = var40 * var42 * var44 / 256 / 64 << 1;

		int var5;
		for(var14 = 0; var14 < var46; ++var14) {
			var6.setNextPhase(var14 * 100 / (var46 - 1) / 4);
			float var47 = var6.random.nextFloat() * (float)var40;
			float var50 = var6.random.nextFloat() * (float)var44;
			float var51 = var6.random.nextFloat() * (float)var42;
			var54 = (int)((var6.random.nextFloat() + var6.random.nextFloat()) * 200.0F);
			float var55 = var6.random.nextFloat() * (float)Math.PI * 2.0F;
			float var57 = 0.0F;
			float var21 = var6.random.nextFloat() * (float)Math.PI * 2.0F;
			float var59 = 0.0F;
			float var23 = var6.random.nextFloat() * var6.random.nextFloat();

			for(var5 = 0; var5 < var54; ++var5) {
				var47 += Mth.sin(var55) * Mth.cos(var21);
				var51 += Mth.cos(var55) * Mth.cos(var21);
				var50 += Mth.sin(var21);
				var55 += var57 * 0.2F;
				var57 *= 0.9F;
				var57 += var6.random.nextFloat() - var6.random.nextFloat();
				var21 += var59 * 0.5F;
				var21 *= 0.5F;
				var59 *= 12.0F / 16.0F;
				var59 += var6.random.nextFloat() - var6.random.nextFloat();
				if(var6.random.nextFloat() >= 0.25F) {
					float var35 = var47 + (var6.random.nextFloat() * 4.0F - 2.0F) * 0.2F;
					float var39 = var50 + (var6.random.nextFloat() * 4.0F - 2.0F) * 0.2F;
					float var15 = var51 + (var6.random.nextFloat() * 4.0F - 2.0F) * 0.2F;
					float var24 = ((float)var6.depth - var39) / (float)var6.depth;
					var24 = 1.2F + (var24 * 3.5F + 1.0F) * var23;
					var24 = Mth.sin((float)var5 * (float)Math.PI / (float)var54) * var24;

					for(int var25 = (int)(var35 - var24); var25 <= (int)(var35 + var24); ++var25) {
						for(int var26 = (int)(var39 - var24); var26 <= (int)(var39 + var24); ++var26) {
							for(int var27 = (int)(var15 - var24); var27 <= (int)(var15 + var24); ++var27) {
								float var28 = (float)var25 - var35;
								float var29 = (float)var26 - var39;
								float var30 = (float)var27 - var15;
								var28 = var28 * var28 + var29 * var29 * 2.0F + var30 * var30;
								if(var28 < var24 * var24 && var25 >= 1 && var26 >= 1 && var27 >= 1 && var25 < var6.width - 1 && var26 < var6.depth - 1 && var27 < var6.height - 1) {
									int var64 = (var26 * var6.height + var27) * var6.width + var25;
									if(var6.blocks[var64] == Tile.rock.id) {
										var6.blocks[var64] = 0;
									}
								}
							}
						}
					}
				}
			}
		}

		this.addOre(Tile.coalOre.id, 90, 1, 4);
		this.addOre(Tile.ironOre.id, 70, 2, 4);
		this.addOre(Tile.goldOre.id, 50, 3, 4);
		this.levelLoaderListener.levelLoadUpdate("Watering..");
		var6 = this;
		var42 = Tile.calmWater.id;
		this.setNextPhase(0);

		for(var44 = 0; var44 < var6.width; ++var44) {
			var6.floodFillWithLiquid(var44, var6.depth / 2 - 1, 0, 0, var42);
			var6.floodFillWithLiquid(var44, var6.depth / 2 - 1, var6.height - 1, 0, var42);
		}

		for(var44 = 0; var44 < var6.height; ++var44) {
			var6.floodFillWithLiquid(0, var6.depth / 2 - 1, var44, 0, var42);
			var6.floodFillWithLiquid(var6.width - 1, var6.depth / 2 - 1, var44, 0, var42);
		}

		var44 = var6.width * var6.height / 8000;

		for(var46 = 0; var46 < var44; ++var46) {
			if(var46 % 100 == 0) {
				var6.setNextPhase(var46 * 100 / (var44 - 1));
			}

			var14 = var6.random.nextInt(var6.width);
			var12 = var6.waterLevel - 1 - var6.random.nextInt(2);
			var49 = var6.random.nextInt(var6.height);
			if(var6.blocks[(var12 * var6.height + var49) * var6.width + var14] == 0) {
				var6.floodFillWithLiquid(var14, var12, var49, 0, var42);
			}
		}

		var6.setNextPhase(100);
		this.levelLoaderListener.levelLoadUpdate("Melting..");
		var6 = this;
		var36 = this.width * this.height * this.depth / 20000;

		for(var40 = 0; var40 < var36; ++var40) {
			if(var40 % 100 == 0) {
				var6.setNextPhase(var40 * 100 / (var36 - 1));
			}

			var42 = var6.random.nextInt(var6.width);
			var44 = (int)(var6.random.nextFloat() * var6.random.nextFloat() * (float)(var6.waterLevel - 3));
			var46 = var6.random.nextInt(var6.height);
			if(var6.blocks[(var44 * var6.height + var46) * var6.width + var42] == 0) {
				var6.floodFillWithLiquid(var42, var44, var46, 0, Tile.calmLava.id);
			}
		}

		var6.setNextPhase(100);
		this.levelLoaderListener.levelLoadUpdate("Growing..");
		var33 = var10;
		var6 = this;
		var36 = this.width;
		var40 = this.height;
		var42 = this.depth;
		var45 = new PerlinNoise(this.random, 8);
		PerlinNoise var48 = new PerlinNoise(this.random, 8);

		int var58;
		for(var14 = 0; var14 < var36; ++var14) {
			var6.setNextPhase(var14 * 100 / (var6.width - 1));

			for(var12 = 0; var12 < var40; ++var12) {
				boolean var53 = var45.getValue((double)var14, (double)var12) > 8.0D;
				boolean var52 = var48.getValue((double)var14, (double)var12) > 12.0D;
				var54 = var33[var14 + var12 * var36];
				var19 = (var54 * var6.height + var12) * var6.width + var14;
				var56 = var6.blocks[((var54 + 1) * var6.height + var12) * var6.width + var14] & 255;
				if((var56 == Tile.water.id || var56 == Tile.calmWater.id) && var54 <= var42 / 2 - 1 && var52) {
					var6.blocks[var19] = (byte)Tile.gravel.id;
				}

				if(var56 == 0) {
					var58 = Tile.grass.id;
					if(var54 <= var42 / 2 - 1 && var53) {
						var58 = Tile.sand.id;
					}

					var6.blocks[var19] = (byte)var58;
				}
			}
		}

		this.levelLoaderListener.levelLoadUpdate("Planting..");
		var33 = var10;
		var6 = this;
		var36 = this.width;
		var40 = this.width * this.height / 3000;

		for(var42 = 0; var42 < var40; ++var42) {
			var44 = var6.random.nextInt(2);
			var6.setNextPhase(var42 * 50 / (var40 - 1));
			var46 = var6.random.nextInt(var6.width);
			var14 = var6.random.nextInt(var6.height);

			for(var12 = 0; var12 < 10; ++var12) {
				var49 = var46;
				var17 = var14;

				for(var54 = 0; var54 < 5; ++var54) {
					var49 += var6.random.nextInt(6) - var6.random.nextInt(6);
					var17 += var6.random.nextInt(6) - var6.random.nextInt(6);
					if((var44 < 2 || var6.random.nextInt(4) == 0) && var49 >= 0 && var17 >= 0 && var49 < var6.width && var17 < var6.height) {
						var19 = var33[var49 + var17 * var36] + 1;
						boolean var60 = (var6.blocks[(var19 * var6.height + var17) * var6.width + var49] & 255) == 0;
						if(var60) {
							var58 = (var19 * var6.height + var17) * var6.width + var49;
							int var62 = var6.blocks[((var19 - 1) * var6.height + var17) * var6.width + var49] & 255;
							if(var62 == Tile.grass.id) {
								if(var44 == 0) {
									var6.blocks[var58] = (byte)Tile.flower.id;
								} else if(var44 == 1) {
									var6.blocks[var58] = (byte)Tile.rose.id;
								}
							}
						}
					}
				}
			}
		}

		var33 = var10;
		var6 = this;
		var36 = this.width;
		var42 = this.width * this.height * this.depth / 2000;

		for(var44 = 0; var44 < var42; ++var44) {
			var46 = var6.random.nextInt(2);
			var6.setNextPhase(var44 * 50 / (var42 - 1) + 50);
			var14 = var6.random.nextInt(var6.width);
			var12 = var6.random.nextInt(var6.depth);
			var49 = var6.random.nextInt(var6.height);

			for(var17 = 0; var17 < 20; ++var17) {
				var54 = var14;
				var19 = var12;
				var56 = var49;

				for(var58 = 0; var58 < 5; ++var58) {
					var54 += var6.random.nextInt(6) - var6.random.nextInt(6);
					var19 += var6.random.nextInt(2) - var6.random.nextInt(2);
					var56 += var6.random.nextInt(6) - var6.random.nextInt(6);
					if((var46 < 2 || var6.random.nextInt(4) == 0) && var54 >= 0 && var56 >= 0 && var19 >= 1 && var54 < var6.width && var56 < var6.height && var19 < var33[var54 + var56 * var36] - 1) {
						boolean var63 = (var6.blocks[(var19 * var6.height + var56) * var6.width + var54] & 255) == 0;
						if(var63) {
							int var61 = (var19 * var6.height + var56) * var6.width + var54;
							var5 = var6.blocks[((var19 - 1) * var6.height + var56) * var6.width + var54] & 255;
							if(var5 == Tile.rock.id) {
								if(var46 == 0) {
									var6.blocks[var61] = (byte)Tile.mushroom1.id;
								} else if(var46 == 1) {
									var6.blocks[var61] = (byte)Tile.mushroom2.id;
								}
							}
						}
					}
				}
			}
		}

		Level var32 = new Level();
		var32.waterLevel = this.waterLevel;
		var32.setData(var2, 64, var3, this.blocks);
		var32.createTime = System.currentTimeMillis();
		var32.creator = var1;
		var32.name = "A Nice World";
		int[] var43 = var10;
		Level var41 = var32;
		var6 = this;
		var40 = this.width;
		var42 = this.width * this.height / 4000;

		for(var44 = 0; var44 < var42; ++var44) {
			var6.setNextPhase(var44 * 50 / (var42 - 1) + 50);
			var46 = var6.random.nextInt(var6.width);
			var14 = var6.random.nextInt(var6.height);

			for(var12 = 0; var12 < 20; ++var12) {
				var49 = var46;
				var17 = var14;

				for(var54 = 0; var54 < 20; ++var54) {
					var49 += var6.random.nextInt(6) - var6.random.nextInt(6);
					var17 += var6.random.nextInt(6) - var6.random.nextInt(6);
					if(var49 >= 0 && var17 >= 0 && var49 < var6.width && var17 < var6.height) {
						var19 = var43[var49 + var17 * var40] + 1;
						if(var6.random.nextInt(4) == 0) {
							var41.maybeGrowTree(var49, var19, var17);
						}
					}
				}
			}
		}

		return var32;
	}

	private void addOre(int var1, int var2, int var3, int var4) {
		byte var25 = (byte)var1;
		var4 = this.width;
		int var5 = this.height;
		int var6 = this.depth;
		int var7 = var4 * var5 * var6 / 256 / 64 * var2 / 100;

		for(int var8 = 0; var8 < var7; ++var8) {
			this.setNextPhase(var8 * 100 / (var7 - 1) / 4 + var3 * 100 / 4);
			float var9 = this.random.nextFloat() * (float)var4;
			float var10 = this.random.nextFloat() * (float)var6;
			float var11 = this.random.nextFloat() * (float)var5;
			int var12 = (int)((this.random.nextFloat() + this.random.nextFloat()) * 75.0F * (float)var2 / 100.0F);
			float var13 = this.random.nextFloat() * (float)Math.PI * 2.0F;
			float var14 = 0.0F;
			float var15 = this.random.nextFloat() * (float)Math.PI * 2.0F;
			float var16 = 0.0F;

			for(int var17 = 0; var17 < var12; ++var17) {
				var9 += Mth.sin(var13) * Mth.cos(var15);
				var11 += Mth.cos(var13) * Mth.cos(var15);
				var10 += Mth.sin(var15);
				var13 += var14 * 0.2F;
				var14 *= 0.9F;
				var14 += this.random.nextFloat() - this.random.nextFloat();
				var15 += var16 * 0.5F;
				var15 *= 0.5F;
				var16 *= 0.9F;
				var16 += this.random.nextFloat() - this.random.nextFloat();
				float var18 = Mth.sin((float)var17 * (float)Math.PI / (float)var12) * (float)var2 / 100.0F + 1.0F;

				for(int var19 = (int)(var9 - var18); var19 <= (int)(var9 + var18); ++var19) {
					for(int var20 = (int)(var10 - var18); var20 <= (int)(var10 + var18); ++var20) {
						for(int var21 = (int)(var11 - var18); var21 <= (int)(var11 + var18); ++var21) {
							float var22 = (float)var19 - var9;
							float var23 = (float)var20 - var10;
							float var24 = (float)var21 - var11;
							var22 = var22 * var22 + var23 * var23 * 2.0F + var24 * var24;
							if(var22 < var18 * var18 && var19 >= 1 && var20 >= 1 && var21 >= 1 && var19 < this.width - 1 && var20 < this.depth - 1 && var21 < this.height - 1) {
								int var26 = (var20 * this.height + var21) * this.width + var19;
								if(this.blocks[var26] == Tile.rock.id) {
									this.blocks[var26] = var25;
								}
							}
						}
					}
				}
			}
		}

	}

	private void setNextPhase(int var1) {
		this.levelLoaderListener.setLoadingProgress(var1);
	}

	private long floodFillWithLiquid(int var1, int var2, int var3, int var4, int var5) {
		byte var20 = (byte)var5;
		ArrayList var21 = new ArrayList();
		byte var6 = 0;
		int var7 = 1;

		int var8;
		for(var8 = 1; 1 << var7 < this.width; ++var7) {
		}

		while(1 << var8 < this.height) {
			++var8;
		}

		int var9 = this.height - 1;
		int var10 = this.width - 1;
		int var22 = var6 + 1;
		this.coords[0] = ((var2 << var8) + var3 << var7) + var1;
		long var13 = 0L;
		var1 = this.width * this.height;

		while(var22 > 0) {
			--var22;
			var2 = this.coords[var22];
			if(var22 == 0 && var21.size() > 0) {
				this.coords = (int[])var21.remove(var21.size() - 1);
				var22 = this.coords.length;
			}

			var3 = var2 >> var7 & var9;
			int var11 = var2 >> var7 + var8;
			int var12 = var2 & var10;

			int var15;
			for(var15 = var12; var12 > 0 && this.blocks[var2 - 1] == 0; --var2) {
				--var12;
			}

			while(var15 < this.width && this.blocks[var2 + var15 - var12] == 0) {
				++var15;
			}

			int var16 = var2 >> var7 & var9;
			int var17 = var2 >> var7 + var8;
			if(var16 != var3 || var17 != var11) {
				System.out.println("Diagonal flood!?");
			}

			boolean var23 = false;
			boolean var24 = false;
			boolean var18 = false;
			var13 += (long)(var15 - var12);

			for(var12 = var12; var12 < var15; ++var12) {
				this.blocks[var2] = var20;
				boolean var19;
				if(var3 > 0) {
					var19 = this.blocks[var2 - this.width] == 0;
					if(var19 && !var23) {
						if(var22 == this.coords.length) {
							var21.add(this.coords);
							this.coords = new int[1048576];
							var22 = 0;
						}

						this.coords[var22++] = var2 - this.width;
					}

					var23 = var19;
				}

				if(var3 < this.height - 1) {
					var19 = this.blocks[var2 + this.width] == 0;
					if(var19 && !var24) {
						if(var22 == this.coords.length) {
							var21.add(this.coords);
							this.coords = new int[1048576];
							var22 = 0;
						}

						this.coords[var22++] = var2 + this.width;
					}

					var24 = var19;
				}

				if(var11 > 0) {
					byte var25 = this.blocks[var2 - var1];
					if((var20 == Tile.lava.id || var20 == Tile.calmLava.id) && (var25 == Tile.water.id || var25 == Tile.calmWater.id)) {
						this.blocks[var2 - var1] = (byte)Tile.rock.id;
					}

					var19 = var25 == 0;
					if(var19 && !var18) {
						if(var22 == this.coords.length) {
							var21.add(this.coords);
							this.coords = new int[1048576];
							var22 = 0;
						}

						this.coords[var22++] = var2 - var1;
					}

					var18 = var19;
				}

				++var2;
			}
		}

		return var13;
	}
}
