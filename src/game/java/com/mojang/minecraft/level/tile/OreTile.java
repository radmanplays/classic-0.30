package com.mojang.minecraft.level.tile;

public final class OreTile extends Tile {
	public OreTile(int var1, int var2) {
		super(var1, var2);
	}

	public final int getId() {
		return this == Tile.coalOre ? Tile.slabHalf.id : (this == Tile.goldOre ? Tile.gold.id : (this == Tile.ironOre ? Tile.iron.id : this.id));
	}

	public final int resourceCount() {
		return random.nextInt(3) + 1;
	}
}
