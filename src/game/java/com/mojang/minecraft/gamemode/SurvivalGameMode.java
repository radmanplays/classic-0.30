package com.mojang.minecraft.gamemode;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.LevelLoaderListener;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.MobSpwaner;
import com.mojang.minecraft.level.tile.Tile;
import com.mojang.minecraft.mob.Mob;
import com.mojang.minecraft.player.Player;

public final class SurvivalGameMode extends GameMode {
	private int x;
	private int y;
	private int z;
	private int oDestroyProgress;
	private int destroyProgress;
	private int delay;
	private MobSpwaner mobSpawner;

	public SurvivalGameMode(Minecraft var1) {
		super(var1);
	}

	public final void initPlayer(Player var1) {
		var1.inventory.slots[8] = Tile.tnt.id;
		var1.inventory.count[8] = 10;
	}

	public final void destroyBlock(int var1, int var2, int var3) {
		int var4 = this.minecraft.level.getTile(var1, var2, var3);
		Tile.tiles[var4].spawnResources(this.minecraft.level, var1, var2, var3);
		super.destroyBlock(var1, var2, var3);
	}

	public final boolean removeResource(int var1) {
		return this.minecraft.player.inventory.removeResource(var1);
	}

	public final void startDestroyBlock(int var1, int var2, int var3) {
		int var4 = this.minecraft.level.getTile(var1, var2, var3);
		if(var4 > 0 && Tile.tiles[var4].getDestroyProgress() == 0) {
			this.destroyBlock(var1, var2, var3);
		}

	}

	public final void stopDestroyBlock() {
		this.oDestroyProgress = 0;
		this.delay = 0;
	}

	public final void continueDestroyBlock(int var1, int var2, int var3, int var4) {
		if(this.delay > 0) {
			--this.delay;
		} else if(var1 == this.x && var2 == this.y && var3 == this.z) {
			int var5 = this.minecraft.level.getTile(var1, var2, var3);
			if(var5 != 0) {
				Tile var6 = Tile.tiles[var5];
				this.destroyProgress = var6.getDestroyProgress();
				var6.destroy(this.minecraft.level, var1, var2, var3, var4, this.minecraft.particleEngine);
				++this.oDestroyProgress;
				if(this.oDestroyProgress == this.destroyProgress + 1) {
					this.destroyBlock(var1, var2, var3);
					this.oDestroyProgress = 0;
					this.delay = 5;
				}

			}
		} else {
			this.oDestroyProgress = 0;
			this.x = var1;
			this.y = var2;
			this.z = var3;
		}
	}

	public final void render(float var1) {
		if(this.oDestroyProgress <= 0) {
			this.minecraft.levelRenderer.hurtTime = 0.0F;
		} else {
			this.minecraft.levelRenderer.hurtTime = ((float)this.oDestroyProgress + var1 - 1.0F) / (float)this.destroyProgress;
		}
	}

	public final float getPickRange() {
		return 4.0F;
	}

	public final boolean removeResource(Player var1, int var2) {
		Tile var3 = Tile.tiles[var2];
		if(var3 == Tile.mushroom2 && this.minecraft.player.inventory.removeResource(var2)) {
			var1.hurt((Entity)null, 3);
			return true;
		} else if(var3 == Tile.mushroom1 && this.minecraft.player.inventory.removeResource(var2)) {
			var1.heal(5);
			return true;
		} else {
			return false;
		}
	}

	public final void initLevel(Level var1) {
		super.initLevel(var1);
		this.mobSpawner = new MobSpwaner(var1);
	}

	public final void tick() {
		MobSpwaner var3 = this.mobSpawner;
		int var1 = var3.level.width * var3.level.height * var3.level.depth / 64 / 64 / 64;
		if(var3.level.random.nextInt(100) < var1) {
			int var2 = var3.level.countInstanceOf(Mob.class);
			if(var2 < var1 * 20) {
				var3.spawnMobs(var1, var3.level.player, (LevelLoaderListener)null);
			}
		}

	}

	public final void createPlayer(Level var1) {
		this.mobSpawner = new MobSpwaner(var1);
		this.minecraft.loadingScreen.levelLoadUpdate("Spawning..");
		int var2 = var1.width * var1.height * var1.depth / 800;
		this.mobSpawner.spawnMobs(var2, (Entity)null, this.minecraft.loadingScreen);
	}
}
