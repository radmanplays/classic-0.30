package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.level.Level;

public final class SlabTile extends Tile {
	private boolean half;

	public SlabTile(int var1, boolean var2) {
		super(var1, 6);
		this.half = var2;
		if(!var2) {
			this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		}

	}

	protected final int getTexture(int var1) {
		return var1 <= 1 ? 6 : 5;
	}

	public final boolean isSolid() {
		return this.half;
	}

	public final void neighborChanged(Level var1, int var2, int var3, int var4, int var5) {
		if(this == Tile.slabHalf) {
		}
	}

	public final void onTileAdded(Level var1, int var2, int var3, int var4) {
		if(this != Tile.slabHalf) {
			super.onTileAdded(var1, var2, var3, var4);
		}

		int var5 = var1.getTile(var2, var3 - 1, var4);
		if(var5 == slabHalf.id) {
			var1.setTile(var2, var3, var4, 0);
			var1.setTile(var2, var3 - 1, var4, Tile.slabFull.id);
		}

	}

	public final int getId() {
		return Tile.slabHalf.id;
	}

	public final boolean isOpaque() {
		return this.half;
	}

	public final boolean shouldRenderFace(Level var1, int var2, int var3, int var4, int var5) {
		if(this != Tile.slabHalf) {
			super.shouldRenderFace(var1, var2, var3, var4, var5);
		}

		return var5 == 1 ? true : (!super.shouldRenderFace(var1, var2, var3, var4, var5) ? false : (var5 == 0 ? true : var1.getTile(var2, var3, var4) != this.id));
	}
}
