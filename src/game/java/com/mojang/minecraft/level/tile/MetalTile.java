package com.mojang.minecraft.level.tile;

public final class MetalTile extends Tile {
	public MetalTile(int var1, int var2) {
		super(var1);
		this.tex = var2;
	}

	protected final int getTexture(int var1) {
		return var1 == 1 ? this.tex - 16 : (var1 == 0 ? this.tex + 16 : this.tex);
	}
}
