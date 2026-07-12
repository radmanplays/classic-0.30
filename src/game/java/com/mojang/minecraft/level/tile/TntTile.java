package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.item.PrimedTnt;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.particle.ParticleEngine;

public final class TntTile extends Tile {
	public TntTile(int var1, int var2) {
		super(46, 8);
	}

	protected final int getTexture(int var1) {
		return var1 == 0 ? this.tex + 2 : (var1 == 1 ? this.tex + 1 : this.tex);
	}

	public final int resourceCount() {
		return 0;
	}

	public final void wasExploded(Level var1, int var2, int var3, int var4) {
		if(!var1.creativeMode) {
			PrimedTnt var5 = new PrimedTnt(var1, (float)var2 + 0.5F, (float)var3 + 0.5F, (float)var4 + 0.5F);
			var5.life = random.nextInt(var5.life / 4) + var5.life / 8;
			var1.addEntity(var5);
		}

	}

	public final void destroy(Level var1, int var2, int var3, int var4, ParticleEngine var5) {
		if(!var1.creativeMode) {
			var1.addEntity(new PrimedTnt(var1, (float)var2 + 0.5F, (float)var3 + 0.5F, (float)var4 + 0.5F));
		} else {
			super.destroy(var1, var2, var3, var4, var5);
		}
	}
}
