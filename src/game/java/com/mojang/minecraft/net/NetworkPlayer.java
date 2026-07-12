package com.mojang.minecraft.net;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gui.Font;
import com.mojang.minecraft.mob.HumanoidMob;
import com.mojang.minecraft.renderer.Textures;
import java.util.LinkedList;
import java.util.List;
import org.lwjgl.opengl.GL11;

public class NetworkPlayer extends HumanoidMob {
	public static final long serialVersionUID = 77479605454997290L;
	private List moveQueue = new LinkedList();
	private Minecraft minecraft;
	private int xp;
	private int yp;
	private int zp;
	private transient int texture = -1;
	public String name;
	public String displayName;
	int tickCount = 0;
	private Textures textures;

	public NetworkPlayer(Minecraft var1, int var2, String var3, int var4, int var5, int var6, float var7, float var8) {
		super(var1.level, (float)var4, (float)var5, (float)var6);
		this.minecraft = var1;
		this.displayName = var3;
		var3 = Font.removeColorCodes(var3);
		this.name = var3;
		this.xp = var4;
		this.yp = var5;
		this.zp = var6;
		this.heightOffset = 0.0F;
		this.pushthrough = 0.8F;
		this.setPos((float)var4 / 32.0F, (float)var5 / 32.0F, (float)var6 / 32.0F);
		this.xRot = var8;
		this.yRot = var7;
		this.armor = this.helmet = false;
		this.renderOffset = 11.0F / 16.0F;
		this.allowAlpha = false;
	}

	public void aiStep() {
		int var1 = 5;

		do {
			if(this.moveQueue.size() > 0) {
				this.setPos((EntityPos)this.moveQueue.remove(0));
			}
		} while(var1-- > 0 && this.moveQueue.size() > 10);

		this.onGround = true;
	}

	public void bindTexture(Textures var1) {
		this.textures = var1;
		
		if(this.texture < 0) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, var1.loadTexture("/char.png"));
		} else {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texture);
		}
	}

	public void renderHover(Textures var1, float var2) {
		Font var3 = this.minecraft.font;
		GL11.glPushMatrix();
		GL11.glTranslatef(this.xo + (this.x - this.xo) * var2, this.yo + (this.y - this.yo) * var2 + 0.8F + this.renderOffset, this.zo + (this.z - this.zo) * var2);
		GL11.glRotatef(-this.minecraft.player.yRot, 0.0F, 1.0F, 0.0F);
		var2 = 0.05F;
		GL11.glScalef(var2, -var2, var2);
		GL11.glTranslatef((float)(-var3.width(this.displayName)) / 2.0F, 0.0F, 0.0F);
		GL11.glNormal3f(1.0F, -1.0F, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_LIGHT0);
		if(this.name.equalsIgnoreCase("Notch")) {
			var3.draw(this.displayName, 0, 0, 16776960);
		} else {
			var3.draw(this.displayName, 0, 0, 16777215);
		}

		GL11.glDepthFunc(GL11.GL_GREATER);
		GL11.glDepthMask(false);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		var3.draw(this.displayName, 0, 0, 16777215);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glTranslatef(1.0F, 1.0F, -0.05F);
		var3.draw(this.name, 0, 0, 5263440);
		GL11.glEnable(GL11.GL_LIGHT0);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	public void queue(byte var1, byte var2, byte var3, float var4, float var5) {
		float var6 = var4 - this.yRot;

		float var7;
		for(var7 = var5 - this.xRot; var6 >= 180.0F; var6 -= 360.0F) {
		}

		while(var6 < -180.0F) {
			var6 += 360.0F;
		}

		while(var7 >= 180.0F) {
			var7 -= 360.0F;
		}

		while(var7 < -180.0F) {
			var7 += 360.0F;
		}

		var6 = this.yRot + var6 * 0.5F;
		var7 = this.xRot + var7 * 0.5F;
		this.moveQueue.add(new EntityPos(((float)this.xp + (float)var1 / 2.0F) / 32.0F, ((float)this.yp + (float)var2 / 2.0F) / 32.0F, ((float)this.zp + (float)var3 / 2.0F) / 32.0F, var6, var7));
		this.xp += var1;
		this.yp += var2;
		this.zp += var3;
		this.moveQueue.add(new EntityPos((float)this.xp / 32.0F, (float)this.yp / 32.0F, (float)this.zp / 32.0F, var4, var5));
	}

	public void teleport(short var1, short var2, short var3, float var4, float var5) {
		float var6 = var4 - this.yRot;

		float var7;
		for(var7 = var5 - this.xRot; var6 >= 180.0F; var6 -= 360.0F) {
		}

		while(var6 < -180.0F) {
			var6 += 360.0F;
		}

		while(var7 >= 180.0F) {
			var7 -= 360.0F;
		}

		while(var7 < -180.0F) {
			var7 += 360.0F;
		}

		var6 = this.yRot + var6 * 0.5F;
		var7 = this.xRot + var7 * 0.5F;
		this.moveQueue.add(new EntityPos((float)(this.xp + var1) / 64.0F, (float)(this.yp + var2) / 64.0F, (float)(this.zp + var3) / 64.0F, var6, var7));
		this.xp = var1;
		this.yp = var2;
		this.zp = var3;
		this.moveQueue.add(new EntityPos((float)this.xp / 32.0F, (float)this.yp / 32.0F, (float)this.zp / 32.0F, var4, var5));
	}

	public void queue(byte var1, byte var2, byte var3) {
		this.moveQueue.add(new EntityPos(((float)this.xp + (float)var1 / 2.0F) / 32.0F, ((float)this.yp + (float)var2 / 2.0F) / 32.0F, ((float)this.zp + (float)var3 / 2.0F) / 32.0F));
		this.xp += var1;
		this.yp += var2;
		this.zp += var3;
		this.moveQueue.add(new EntityPos((float)this.xp / 32.0F, (float)this.yp / 32.0F, (float)this.zp / 32.0F));
	}

	public void queue(float var1, float var2) {
		float var3 = var1 - this.yRot;

		float var4;
		for(var4 = var2 - this.xRot; var3 >= 180.0F; var3 -= 360.0F) {
		}

		while(var3 < -180.0F) {
			var3 += 360.0F;
		}

		while(var4 >= 180.0F) {
			var4 -= 360.0F;
		}

		while(var4 < -180.0F) {
			var4 += 360.0F;
		}

		var3 = this.yRot + var3 * 0.5F;
		var4 = this.xRot + var4 * 0.5F;
		this.moveQueue.add(new EntityPos(var3, var4));
		this.moveQueue.add(new EntityPos(var1, var2));
	}

	public void clear() {
		if(this.texture >= 0 && this.textures != null) {
			int var1 = this.texture;
			Textures var2 = this.textures;
			var2.pixelsMap.remove(Integer.valueOf(var1));
			var2.ib.clear();
			var2.ib.put(var1);
			var2.ib.flip();
			GL11.glDeleteTextures(var2.ib);
		}

	}
}
