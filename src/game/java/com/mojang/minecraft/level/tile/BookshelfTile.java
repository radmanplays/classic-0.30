package com.mojang.minecraft.level.tile;

public final class BookshelfTile extends Tile {
	public BookshelfTile(int var1, int var2) {
		super(47, 35);
	}

	protected final int getTexture(int var1) {
		return var1 <= 1 ? 4 : this.tex;
	}

	public final int resourceCount() {
		return 0;
	}
}
