package com.mojang.minecraft.model;

import com.mojang.util.Mth;

public final class Vec3 {
	public float x;
	public float y;
	public float z;

	public Vec3(float var1, float var2, float var3) {
		this.x = var1;
		this.y = var2;
		this.z = var3;
	}

	public final Vec3 subtract(Vec3 var1) {
		return new Vec3(this.x - var1.x, this.y - var1.y, this.z - var1.z);
	}

	public final Vec3 normalize() {
		float var1 = Mth.sqrt_float(this.x * this.x + this.y * this.y + this.z * this.z);
		return new Vec3(this.x / var1, this.y / var1, this.z / var1);
	}

	public final Vec3 add(float var1, float var2, float var3) {
		return new Vec3(this.x + var1, this.y + var2, this.z + var3);
	}

	public final float distanceTo(Vec3 var1) {
		float var2 = var1.x - this.x;
		float var3 = var1.y - this.y;
		float var4 = var1.z - this.z;
		return Mth.sqrt_float(var2 * var2 + var3 * var3 + var4 * var4);
	}

	public final float distanceToSqr(Vec3 var1) {
		float var2 = var1.x - this.x;
		float var3 = var1.y - this.y;
		float var4 = var1.z - this.z;
		return var2 * var2 + var3 * var3 + var4 * var4;
	}

	public final Vec3 clipX(Vec3 var1, float var2) {
		float var3 = var1.x - this.x;
		float var4 = var1.y - this.y;
		float var5 = var1.z - this.z;
		if(var3 * var3 < 1.0E-7F) {
			return null;
		} else {
			var2 = (var2 - this.x) / var3;
			return var2 >= 0.0F && var2 <= 1.0F ? new Vec3(this.x + var3 * var2, this.y + var4 * var2, this.z + var5 * var2) : null;
		}
	}

	public final Vec3 clipY(Vec3 var1, float var2) {
		float var3 = var1.x - this.x;
		float var4 = var1.y - this.y;
		float var5 = var1.z - this.z;
		if(var4 * var4 < 1.0E-7F) {
			return null;
		} else {
			var2 = (var2 - this.y) / var4;
			return var2 >= 0.0F && var2 <= 1.0F ? new Vec3(this.x + var3 * var2, this.y + var4 * var2, this.z + var5 * var2) : null;
		}
	}

	public final Vec3 clipZ(Vec3 var1, float var2) {
		float var3 = var1.x - this.x;
		float var4 = var1.y - this.y;
		float var5 = var1.z - this.z;
		if(var5 * var5 < 1.0E-7F) {
			return null;
		} else {
			var2 = (var2 - this.z) / var5;
			return var2 >= 0.0F && var2 <= 1.0F ? new Vec3(this.x + var3 * var2, this.y + var4 * var2, this.z + var5 * var2) : null;
		}
	}

	public final String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}
}
