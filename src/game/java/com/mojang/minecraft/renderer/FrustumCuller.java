package com.mojang.minecraft.renderer;

import org.lwjgl.opengl.GL11;

import net.lax1dude.eaglercraft.internal.buffer.FloatBuffer;
import com.mojang.util.GLAllocation;
import com.mojang.util.Mth;

public final class FrustumCuller extends Frustum {
	private static FrustumCuller frustum = new FrustumCuller();
	private FloatBuffer _proj = GLAllocation.createFloatBuffer(16);
	private FloatBuffer _modl = GLAllocation.createFloatBuffer(16);
	private FloatBuffer _clip = GLAllocation.createFloatBuffer(16);

	public static Frustum calculateFrustum() {
		FrustumCuller var0 = frustum;
		var0._proj.clear();
		var0._modl.clear();
		var0._clip.clear();
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, var0._proj);
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, var0._modl);
		var0._proj.flip().limit(16);
		var0._proj.get(var0.proj);
		var0._modl.flip().limit(16);
		var0._modl.get(var0.modl);
		var0.clip[0] = var0.modl[0] * var0.proj[0] + var0.modl[1] * var0.proj[4] + var0.modl[2] * var0.proj[8] + var0.modl[3] * var0.proj[12];
		var0.clip[1] = var0.modl[0] * var0.proj[1] + var0.modl[1] * var0.proj[5] + var0.modl[2] * var0.proj[9] + var0.modl[3] * var0.proj[13];
		var0.clip[2] = var0.modl[0] * var0.proj[2] + var0.modl[1] * var0.proj[6] + var0.modl[2] * var0.proj[10] + var0.modl[3] * var0.proj[14];
		var0.clip[3] = var0.modl[0] * var0.proj[3] + var0.modl[1] * var0.proj[7] + var0.modl[2] * var0.proj[11] + var0.modl[3] * var0.proj[15];
		var0.clip[4] = var0.modl[4] * var0.proj[0] + var0.modl[5] * var0.proj[4] + var0.modl[6] * var0.proj[8] + var0.modl[7] * var0.proj[12];
		var0.clip[5] = var0.modl[4] * var0.proj[1] + var0.modl[5] * var0.proj[5] + var0.modl[6] * var0.proj[9] + var0.modl[7] * var0.proj[13];
		var0.clip[6] = var0.modl[4] * var0.proj[2] + var0.modl[5] * var0.proj[6] + var0.modl[6] * var0.proj[10] + var0.modl[7] * var0.proj[14];
		var0.clip[7] = var0.modl[4] * var0.proj[3] + var0.modl[5] * var0.proj[7] + var0.modl[6] * var0.proj[11] + var0.modl[7] * var0.proj[15];
		var0.clip[8] = var0.modl[8] * var0.proj[0] + var0.modl[9] * var0.proj[4] + var0.modl[10] * var0.proj[8] + var0.modl[11] * var0.proj[12];
		var0.clip[9] = var0.modl[8] * var0.proj[1] + var0.modl[9] * var0.proj[5] + var0.modl[10] * var0.proj[9] + var0.modl[11] * var0.proj[13];
		var0.clip[10] = var0.modl[8] * var0.proj[2] + var0.modl[9] * var0.proj[6] + var0.modl[10] * var0.proj[10] + var0.modl[11] * var0.proj[14];
		var0.clip[11] = var0.modl[8] * var0.proj[3] + var0.modl[9] * var0.proj[7] + var0.modl[10] * var0.proj[11] + var0.modl[11] * var0.proj[15];
		var0.clip[12] = var0.modl[12] * var0.proj[0] + var0.modl[13] * var0.proj[4] + var0.modl[14] * var0.proj[8] + var0.modl[15] * var0.proj[12];
		var0.clip[13] = var0.modl[12] * var0.proj[1] + var0.modl[13] * var0.proj[5] + var0.modl[14] * var0.proj[9] + var0.modl[15] * var0.proj[13];
		var0.clip[14] = var0.modl[12] * var0.proj[2] + var0.modl[13] * var0.proj[6] + var0.modl[14] * var0.proj[10] + var0.modl[15] * var0.proj[14];
		var0.clip[15] = var0.modl[12] * var0.proj[3] + var0.modl[13] * var0.proj[7] + var0.modl[14] * var0.proj[11] + var0.modl[15] * var0.proj[15];
		var0.m_Frustum[0][0] = var0.clip[3] - var0.clip[0];
		var0.m_Frustum[0][1] = var0.clip[7] - var0.clip[4];
		var0.m_Frustum[0][2] = var0.clip[11] - var0.clip[8];
		var0.m_Frustum[0][3] = var0.clip[15] - var0.clip[12];
		normalizePlane(var0.m_Frustum, 0);
		var0.m_Frustum[1][0] = var0.clip[3] + var0.clip[0];
		var0.m_Frustum[1][1] = var0.clip[7] + var0.clip[4];
		var0.m_Frustum[1][2] = var0.clip[11] + var0.clip[8];
		var0.m_Frustum[1][3] = var0.clip[15] + var0.clip[12];
		normalizePlane(var0.m_Frustum, 1);
		var0.m_Frustum[2][0] = var0.clip[3] + var0.clip[1];
		var0.m_Frustum[2][1] = var0.clip[7] + var0.clip[5];
		var0.m_Frustum[2][2] = var0.clip[11] + var0.clip[9];
		var0.m_Frustum[2][3] = var0.clip[15] + var0.clip[13];
		normalizePlane(var0.m_Frustum, 2);
		var0.m_Frustum[3][0] = var0.clip[3] - var0.clip[1];
		var0.m_Frustum[3][1] = var0.clip[7] - var0.clip[5];
		var0.m_Frustum[3][2] = var0.clip[11] - var0.clip[9];
		var0.m_Frustum[3][3] = var0.clip[15] - var0.clip[13];
		normalizePlane(var0.m_Frustum, 3);
		var0.m_Frustum[4][0] = var0.clip[3] - var0.clip[2];
		var0.m_Frustum[4][1] = var0.clip[7] - var0.clip[6];
		var0.m_Frustum[4][2] = var0.clip[11] - var0.clip[10];
		var0.m_Frustum[4][3] = var0.clip[15] - var0.clip[14];
		normalizePlane(var0.m_Frustum, 4);
		var0.m_Frustum[5][0] = var0.clip[3] + var0.clip[2];
		var0.m_Frustum[5][1] = var0.clip[7] + var0.clip[6];
		var0.m_Frustum[5][2] = var0.clip[11] + var0.clip[10];
		var0.m_Frustum[5][3] = var0.clip[15] + var0.clip[14];
		normalizePlane(var0.m_Frustum, 5);
		return frustum;
	}

	private static void normalizePlane(float[][] var0, int var1) {
		float var2 = Mth.sqrt_float(var0[var1][0] * var0[var1][0] + var0[var1][1] * var0[var1][1] + var0[var1][2] * var0[var1][2]);
		var0[var1][0] /= var2;
		var0[var1][1] /= var2;
		var0[var1][2] /= var2;
		var0[var1][3] /= var2;
	}
}
