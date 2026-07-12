package com.mojang.minecraft.renderer;

public class Frustum {
	public float[][] m_Frustum = new float[16][16];
	public float[] proj = new float[16];
	public float[] modl = new float[16];
	public float[] clip = new float[16];

	public final boolean cubeInFrustum(float var1, float var2, float var3, float var4, float var5, float var6) {
		for(int var7 = 0; var7 < 6; ++var7) {
			if(this.m_Frustum[var7][0] * var1 + this.m_Frustum[var7][1] * var2 + this.m_Frustum[var7][2] * var3 + this.m_Frustum[var7][3] <= 0.0F && this.m_Frustum[var7][0] * var4 + this.m_Frustum[var7][1] * var2 + this.m_Frustum[var7][2] * var3 + this.m_Frustum[var7][3] <= 0.0F && this.m_Frustum[var7][0] * var1 + this.m_Frustum[var7][1] * var5 + this.m_Frustum[var7][2] * var3 + this.m_Frustum[var7][3] <= 0.0F && this.m_Frustum[var7][0] * var4 + this.m_Frustum[var7][1] * var5 + this.m_Frustum[var7][2] * var3 + this.m_Frustum[var7][3] <= 0.0F && this.m_Frustum[var7][0] * var1 + this.m_Frustum[var7][1] * var2 + this.m_Frustum[var7][2] * var6 + this.m_Frustum[var7][3] <= 0.0F && this.m_Frustum[var7][0] * var4 + this.m_Frustum[var7][1] * var2 + this.m_Frustum[var7][2] * var6 + this.m_Frustum[var7][3] <= 0.0F && this.m_Frustum[var7][0] * var1 + this.m_Frustum[var7][1] * var5 + this.m_Frustum[var7][2] * var6 + this.m_Frustum[var7][3] <= 0.0F && this.m_Frustum[var7][0] * var4 + this.m_Frustum[var7][1] * var5 + this.m_Frustum[var7][2] * var6 + this.m_Frustum[var7][3] <= 0.0F) {
				return false;
			}
		}

		return true;
	}
}
