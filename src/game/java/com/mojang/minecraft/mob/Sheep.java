package com.mojang.minecraft.mob;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.item.Item;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Tile;
import com.mojang.minecraft.mob.ai.BasicAI;
import com.mojang.minecraft.model.QuadrupedModel;
import com.mojang.minecraft.player.Player;
import com.mojang.minecraft.renderer.Textures;
import com.mojang.util.Mth;

import org.lwjgl.opengl.GL11;

public class Sheep extends QuadrupedMob {
	public static final long serialVersionUID = 0L;
	public boolean hasFur = true;
	public boolean grazing = false;
	public int grazingTime = 0;
	public float graze;
	public float grazeO;

	public Sheep(Level var1, float var2, float var3, float var4) {
		super(var1, var2, var3, var4);
		this.setSize(1.4F, 1.72F);
		this.setPos(var2, var3, var4);
		this.heightOffset = 1.72F;
		this.modelName = "sheep";
		this.textureName = "/mob/sheep.png";
		this.ai = new BasicAI() {
			private static final long serialVersionUID = 1L;

			public final void update() {
				float var1 = Mth.sin(Sheep.this.yRot * (float)Math.PI / 180.0F);
				float var2 = Mth.cos(Sheep.this.yRot * (float)Math.PI / 180.0F);
				var1 = -0.7F * var1;
				var2 = 0.7F * var2;
				int var4 = (int)(this.mob.x + var1);
				int var3 = (int)(this.mob.y - 2.0F);
				int var5 = (int)(this.mob.z + var2);
				if(Sheep.this.grazing) {
					if(this.level.getTile(var4, var3, var5) != Tile.grass.id) {
						Sheep.this.grazing = false;
					} else {
						if(++Sheep.this.grazingTime == 60) {
							this.level.setTile(var4, var3, var5, Tile.dirt.id);
							if(this.random.nextInt(5) == 0) {
								Sheep.this.hasFur = true;
							}
						}

						this.xxa = 0.0F;
						this.yya = 0.0F;
						this.mob.xRot = (float)(40 + Sheep.this.grazingTime / 2 % 2 * 10);
					}
				} else {
					if(this.level.getTile(var4, var3, var5) == Tile.grass.id) {
						Sheep.this.grazing = true;
						Sheep.this.grazingTime = 0;
					}

					super.update();
				}
			}
		};
	}

	public void aiStep() {
		super.aiStep();
		this.grazeO = this.graze;
		if(this.grazing) {
			this.graze += 0.2F;
		} else {
			this.graze -= 0.2F;
		}

		if(this.graze < 0.0F) {
			this.graze = 0.0F;
		}

		if(this.graze > 1.0F) {
			this.graze = 1.0F;
		}

	}

	public void die(Entity var1) {
		if(var1 != null) {
			var1.awardKillScore(this, 10);
		}

		int var2 = (int)(Math.random() + Math.random() + 1.0D);

		for(int var3 = 0; var3 < var2; ++var3) {
			this.level.addEntity(new Item(this.level, this.x, this.y, this.z, Tile.mushroom1.id));
		}

		super.die(var1);
	}

	public void hurt(Entity var1, int var2) {
		if(this.hasFur && var1 instanceof Player) {
			this.hasFur = false;
			int var3 = (int)(Math.random() * 3.0D + 1.0D);

			for(var2 = 0; var2 < var3; ++var2) {
				this.level.addEntity(new Item(this.level, this.x, this.y, this.z, Tile.clothWhite.id));
			}

		} else {
			super.hurt(var1, var2);
		}
	}

	public void renderModel(Textures var1, float var2, float var3, float var4, float var5, float var6, float var7) {
		QuadrupedModel var8 = (QuadrupedModel)modelCache.getModel(this.modelName);
		float var9 = var8.head.y;
		float var10 = var8.head.z;
		var8.head.y += (this.grazeO + (this.graze - this.grazeO) * var3) * 8.0F;
		var8.head.z -= this.grazeO + (this.graze - this.grazeO) * var3;
		super.renderModel(var1, var2, var3, var4, var5, var6, var7);
		if(this.hasFur) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, var1.loadTexture("/mob/sheep_fur.png"));
			GL11.glDisable(GL11.GL_CULL_FACE);
			QuadrupedModel var11 = (QuadrupedModel)modelCache.getModel("sheep.fur");
			var11.head.yRot = var8.head.yRot;
			var11.head.xRot = var8.head.xRot;
			var11.head.y = var8.head.y;
			var11.head.x = var8.head.x;
			var11.body.yRot = var8.body.yRot;
			var11.body.xRot = var8.body.xRot;
			var11.leg1.xRot = var8.leg1.xRot;
			var11.leg2.xRot = var8.leg2.xRot;
			var11.leg3.xRot = var8.leg3.xRot;
			var11.leg4.xRot = var8.leg4.xRot;
			var11.head.render(var7);
			var11.body.render(var7);
			var11.leg1.render(var7);
			var11.leg2.render(var7);
			var11.leg3.render(var7);
			var11.leg4.render(var7);
		}

		var8.head.y = var9;
		var8.head.z = var10;
	}
}
