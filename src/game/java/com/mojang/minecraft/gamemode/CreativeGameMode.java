package com.mojang.minecraft.gamemode;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.User;
import com.mojang.minecraft.gui.BlockSelectionScreen;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Tile;
import com.mojang.minecraft.player.Player;

public final class CreativeGameMode extends GameMode {
	public CreativeGameMode(Minecraft var1) {
		super(var1);
		this.mode = true;
	}

	public final void handleOpenInventory() {
		this.minecraft.setScreen(new BlockSelectionScreen());
	}

	public final void initLevel(Level var1) {
		super.initLevel(var1);
		var1.removeAllNonCreativeModeEntities();
		var1.creativeMode = true;
		var1.growTrees = false;
	}

	public final void adjustPlayer(Player var1) {
		for(int var2 = 0; var2 < 9; ++var2) {
			var1.inventory.count[var2] = 1;
			if(var1.inventory.slots[var2] <= 0) {
				var1.inventory.slots[var2] = ((Tile)User.creativeTiles.get(var2)).id;
			}
		}

	}

	public final boolean canHurtPlayer() {
		return false;
	}
}
