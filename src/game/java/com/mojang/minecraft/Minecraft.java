package com.mojang.minecraft;

import com.mojang.comm.SocketConnection;
import com.mojang.minecraft.gamemode.CreativeGameMode;
import com.mojang.minecraft.gamemode.GameMode;
import com.mojang.minecraft.gamemode.SurvivalGameMode;
import com.mojang.minecraft.gui.ChatScreen;
import com.mojang.minecraft.gui.DeathScreen;
import com.mojang.minecraft.gui.ErrorScreen;
import com.mojang.minecraft.gui.Font;
import com.mojang.minecraft.gui.Gui;
import com.mojang.minecraft.gui.PauseScreen;
import com.mojang.minecraft.gui.Screen;
import com.mojang.minecraft.item.Arrow;
import com.mojang.minecraft.item.Item;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.LevelIO;
import com.mojang.minecraft.level.levelgen.LevelGen;
import com.mojang.minecraft.level.liquid.Liquid;
import com.mojang.minecraft.level.tile.Tile;
import com.mojang.minecraft.mob.Mob;
import com.mojang.minecraft.model.Cube;
import com.mojang.minecraft.model.HumanoidModel;
import com.mojang.minecraft.model.ModelCache;
import com.mojang.minecraft.model.Vec3;
import com.mojang.minecraft.net.Client;
import com.mojang.minecraft.net.NetworkPlayer;
import com.mojang.minecraft.net.Packet;
import com.mojang.minecraft.particle.Particle;
import com.mojang.minecraft.particle.ParticleEngine;
import com.mojang.minecraft.particle.WaterDropParticle;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.player.Inventory;
import com.mojang.minecraft.player.KeyboardInput;
import com.mojang.minecraft.player.Player;
import com.mojang.minecraft.renderer.Chunk;
import com.mojang.minecraft.renderer.DirtyChunkSorter;
import com.mojang.minecraft.renderer.Frustum;
import com.mojang.minecraft.renderer.FrustumCuller;
import com.mojang.minecraft.renderer.GameRenderer;
import com.mojang.minecraft.renderer.LevelRenderer;
import com.mojang.minecraft.renderer.Tesselator;
import com.mojang.minecraft.renderer.Textures;
import com.mojang.minecraft.renderer.TileRenderer;
import com.mojang.minecraft.renderer.texture.DynamicTexture;
import com.mojang.minecraft.renderer.texture.LavaTexture;
import com.mojang.minecraft.renderer.texture.WaterTexture;
import com.mojang.minecraft.sound.SoundManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import com.mojang.util.GLAllocation;
import com.mojang.util.Mth;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.EnumPlatformType;
import net.lax1dude.eaglercraft.internal.IWebSocketFrame;
import net.lax1dude.eaglercraft.internal.buffer.ByteBuffer;
import net.lax1dude.eaglercraft.internal.buffer.FloatBuffer;
import net.lax1dude.eaglercraft.internal.buffer.IntBuffer;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;

public final class Minecraft implements Runnable {
	public GameMode gamemode = new SurvivalGameMode(this);
	private boolean fullscreen = false;
	public int width;
	public int height;
	private Timer timer = new Timer(20.0F);
	public Level level;
	public LevelRenderer levelRenderer;
	public Player player;
	public ParticleEngine particleEngine;
	public User user = null;
	public String host;
	public volatile boolean pause = false;
	public Textures textures;
	public Font font;
	public Screen screen = null;
	public LevelLoaderListener loadingScreen = new LevelLoaderListener(this);
	public GameRenderer gameRenderer = new GameRenderer(this);
	public LevelIO levelIo = new LevelIO(this.loadingScreen);
	public SoundManager soundManager = new SoundManager(this);
	private int frames = 0;
	private int clickCounter = 0;
	public String loadMapUser = null;
	public int loadMapId = 0;
	public Gui gui;
	public boolean hideScreen = false;
	public Client networkClient;
	public HitResult hitResult;
	public Options options;
	String server = null;
	int port = 0;
	volatile boolean running;
	public String fpsString;
	public boolean mouseGrabbed;
	public int oFrames;
	public boolean raining;

	public Minecraft(int var3, int var4, boolean var5) {
		new HumanoidModel(0.0F);
		this.hitResult = null;
		this.server = null;
		this.port = 0;
		this.running = false;
		this.fpsString = "";
		this.mouseGrabbed = false;
		this.oFrames = 0;
		this.width = var3;
		this.height = var4;
		this.fullscreen = false;
		this.raining = false;
	}
	
	public final void setServer(String var1) {
		server = var1;
	}

	public final void setScreen(Screen var1) {
		if(!(this.screen instanceof ErrorScreen)) {
			if(this.screen != null) {
				this.screen.removed();
			}

			if(var1 == null && this.player.health <= 0) {
				var1 = new DeathScreen();
			}

			this.screen = (Screen)var1;
			if(var1 != null) {
				if(this.mouseGrabbed) {
					this.player.releaseAllKeys();
					this.mouseGrabbed = false;
					Mouse.setGrabbed(false);
				}

				int var2 = this.width * 240 / this.height;
				int var3 = this.height * 240 / this.height;
				((Screen)var1).init(this, var2, var3);
				this.hideScreen = false;
			} else {
				this.grabMouse();
			}
		}
	}

	private static void checkGlError(String var0) {
		int var1 = GL11.glGetError();
		if(var1 != 0) {
			String var2 = GLU.gluErrorString(var1);
			System.out.println("########## GL ERROR ##########");
			System.out.println("@ " + var0);
			System.out.println(var1 + ": " + var2);
			throw new RuntimeException(var1 + ": " + var2);
		}

	}

	public final void destroy() {

		Minecraft var5 = this;
		try {
			if(this.networkClient == null && var5.level != null) {
				LevelIO.save(var5.level, new VFile2("level.dat"));
			}
		} catch (Exception var2) {
			var2.printStackTrace();
		}

		if(this.networkClient != null) {
			networkClient.serverConnection.disconnect();
		}
		EagRuntime.destroy();
	}

	public final void run() {
		this.running = true;

		try {
			Minecraft var4 = this;
			if(this.fullscreen) {
				Display.toggleFullscreen();
				this.width = Display.getWidth();
				this.height = Display.getHeight();
			} else {
				this.width = Display.getWidth();
				this.height = Display.getHeight();
			}

			Display.setTitle("Minecraft 0.30");

			Display.create();
			Keyboard.create();
			Mouse.create();

			checkGlError("Pre startup");
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			GL11.glClearDepth(1.0D);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDepthFunc(GL11.GL_LEQUAL);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
			GL11.glCullFace(GL11.GL_BACK);
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			checkGlError("Startup");
			this.options = new Options(this);
			this.textures = new Textures(this.options);
			this.textures.addDynamicTexture(new LavaTexture());
			this.textures.addDynamicTexture(new WaterTexture());
			this.font = new Font(this.options, "/default.png", this.textures);
			IntBuffer var8 = GLAllocation.createIntBuffer(256);
			var8.clear().limit(256);
			this.levelRenderer = new LevelRenderer(this, this.textures);
			Item.initModels();
			Mob.modelCache = new ModelCache();
			GL11.glViewport(0, 0, this.width, this.height);
			if(this.server != null && this.user != null) {
				Level var62 = new Level();
				var62.setData(8, 8, 8, new byte[512]);
				this.loadLegacy(var62);
			} else {
				boolean var9 = false;

				try {
					Level var10 = null;
					var10 = var4.levelIo.load(new VFile2("level.dat"));
					var9 = var10 != null;
					if(var9) {
						var4.loadLegacy(var10);
					}
				} catch (Exception var41) {
					var41.printStackTrace();
				}

				if(this.level == null) {
					this.generateLevel(1);
				}
			}

			this.particleEngine = new ParticleEngine(this.level, this.textures);

			checkGlError("Post startup");
			this.gui = new Gui(this, this.width, this.height);
			if(this.server != null && this.user != null) {
				this.networkClient = new Client(this, this.server, this.user.name);
			}
		} catch (Exception var49) {
			var49.printStackTrace();
			System.out.println("Failed to start Minecraft");
			return;
		}

		long var1 = System.currentTimeMillis();
		int var3 = 0;

		try {
			while(this.running) {
				if(Display.isCloseRequested()) {
					if(this.networkClient != null) {
						networkClient.serverConnection.disconnect();
					}
					this.running = false;
				}

					try {
						Timer var52 = this.timer;
						long var59 = System.currentTimeMillis();
						long var63 = var59 - var52.msPerTick;
						long var70 = System.nanoTime() / 1000000L;
						double var87;
						if(var63 > 1000L) {
							long var82 = var70 - var52.passedTime;
							var87 = (double)var63 / (double)var82;
							var52.averageFrameTime += (var87 - var52.averageFrameTime) * (double)0.2F;
							var52.msPerTick = var59;
							var52.passedTime = var70;
						}

						if(var63 < 0L) {
							var52.msPerTick = var59;
							var52.passedTime = var70;
						}

						double var83 = (double)var70 / 1000.0D;
						var87 = (var83 - var52.lastTime) * var52.averageFrameTime;
						var52.lastTime = var83;
						if(var87 < 0.0D) {
							var87 = 0.0D;
						}

						if(var87 > 1.0D) {
							var87 = 1.0D;
						}

						var52.ticks = (float)((double)var52.ticks + var87 * (double)var52.fps * (double)var52.ticksPerSecond);
						var52.frames = (int)var52.ticks;
						if(var52.frames > 100) {
							var52.frames = 100;
						}

						var52.ticks -= (float)var52.frames;
						var52.alpha = var52.ticks;

						for(int var53 = 0; var53 < this.timer.frames; ++var53) {
							++this.frames;
							this.tick();
						}

						checkGlError("Pre render");
						GL11.glEnable(GL11.GL_TEXTURE_2D);
						if(!this.hideScreen) {
							if (Display.wasResized()) {
								if(Display.getHeight() != 0) {
									this.width = Display.getWidth();
									this.height = Display.getHeight();
									if(this.gui !=null) {
										this.gui = new Gui(this, this.width, this.height);
									}
									
									if(this.screen != null) {
										Screen sc = this.screen;
										this.setScreen((Screen)null);
										this.setScreen(sc);
									}
								}
							}
							this.gamemode.render(this.timer.alpha);
							float var60 = this.timer.alpha;
							GameRenderer var54 = this.gameRenderer;
							if(var54.displayActive && !Display.isActive()) {
								var54.minecraft.pauseScreen();
							}

							var54.displayActive = Display.isActive();
							int var61;
							int var64;
							int var71;
							int var76;
							if(var54.minecraft.mouseGrabbed) {
								var61 = 0;
								var64 = 0;
								var61 = Mouse.getDX();
								var64 = Mouse.getDY();

								byte var68 = 1;
								if(var54.minecraft.options.invertYMouse) {
									var68 = -1;
								}

								var54.minecraft.player.turn((float)var61, (float)(var64 * var68));
							}

							if(!var54.minecraft.hideScreen) {
								var61 = var54.minecraft.width * 240 / var54.minecraft.height;
								var64 = var54.minecraft.height * 240 / var54.minecraft.height;
								int var72 = Mouse.getX() * var61 / var54.minecraft.width;
								var71 = var64 - Mouse.getY() * var64 / var54.minecraft.height - 1;
								if(var54.minecraft.level != null) {
									float var93 = var60;
									GameRenderer var89 = var54;
									GameRenderer var21 = var54;
									Player var23 = var54.minecraft.player;
									float var24 = var23.xRotO + (var23.xRot - var23.xRotO) * var60;
									float var25 = var23.yRotO + (var23.yRot - var23.yRotO) * var60;
									Vec3 var26 = var54.getPlayerRotVec(var60);
									float var27 = Mth.cos(-var25 * ((float)Math.PI / 180.0F) - (float)Math.PI);
									float var78 = Mth.sin(-var25 * ((float)Math.PI / 180.0F) - (float)Math.PI);
									float var86 = Mth.cos(-var24 * ((float)Math.PI / 180.0F));
									float var17 = Mth.sin(-var24 * ((float)Math.PI / 180.0F));
									float var18 = var78 * var86;
									float var73 = var27 * var86;
									float var19 = var54.minecraft.gamemode.getPickRange();
									Vec3 var81 = var26.add(var18 * var19, var17 * var19, var73 * var19);
									var54.minecraft.hitResult = var54.minecraft.level.clip(var26, var81);
									var86 = var19;
									if(var54.minecraft.hitResult != null) {
										var86 = var54.minecraft.hitResult.vec.distanceTo(var54.getPlayerRotVec(var60));
									}

									var26 = var54.getPlayerRotVec(var60);
									if(var54.minecraft.gamemode instanceof CreativeGameMode) {
										var19 = 32.0F;
									} else {
										var19 = var86;
									}

									var81 = var26.add(var18 * var19, var17 * var19, var73 * var19);
									var54.entity = null;
									List var5 = var54.minecraft.level.blockMap.getEntities(var23, var23.bb.expand(var18 * var19, var17 * var19, var73 * var19));
									float var6 = 0.0F;

									for(var61 = 0; var61 < var5.size(); ++var61) {
										Entity var75 = (Entity)var5.get(var61);
										if(var75.isPickable()) {
											var86 = 0.1F;
											AABB var91 = var75.bb.grow(var86, var86, var86);
											HitResult var92 = var91.clip(var26, var81);
											if(var92 != null) {
												var86 = var26.distanceTo(var92.vec);
												if(var86 < var6 || var6 == 0.0F) {
													var21.entity = var75;
													var6 = var86;
												}
											}
										}
									}

									if(var21.entity != null && !(var21.minecraft.gamemode instanceof CreativeGameMode)) {
										var21.minecraft.hitResult = new HitResult(var21.entity);
									}

									int var90 = 0;

									while(true) {
										if(var90 >= 2) {
											GL11.glColorMask(true, true, true, false);
											break;
										}

										if(var89.minecraft.options.anaglyph3d) {
											if(var90 == 0) {
												GL11.glColorMask(false, true, true, false);
											} else {
												GL11.glColorMask(true, false, false, false);
											}
										}

										Player var55 = var89.minecraft.player;
										Level var57 = var89.minecraft.level;
										LevelRenderer var66 = var89.minecraft.levelRenderer;
										ParticleEngine var77 = var89.minecraft.particleEngine;
										GL11.glViewport(0, 0, var89.minecraft.width, var89.minecraft.height);
										Level var22 = var89.minecraft.level;
										var23 = var89.minecraft.player;
										var24 = 1.0F / (float)(4 - var89.minecraft.options.viewDistance);
										var24 = 1.0F - (float)Math.pow((double)var24, 0.25D);
										var25 = (float)(var22.skyColor >> 16 & 255) / 255.0F;
										float var114 = (float)(var22.skyColor >> 8 & 255) / 255.0F;
										var27 = (float)(var22.skyColor & 255) / 255.0F;
										var89.fogRed = (float)(var22.fogColor >> 16 & 255) / 255.0F;
										var89.fogGreen = (float)(var22.fogColor >> 8 & 255) / 255.0F;
										var89.fogBlue = (float)(var22.fogColor & 255) / 255.0F;
										var89.fogRed += (var25 - var89.fogRed) * var24;
										var89.fogGreen += (var114 - var89.fogGreen) * var24;
										var89.fogBlue += (var27 - var89.fogBlue) * var24;
										var89.fogRed *= var89.fogColorMultiplier;
										var89.fogGreen *= var89.fogColorMultiplier;
										var89.fogBlue *= var89.fogColorMultiplier;
										Tile var84 = Tile.tiles[var22.getTile((int)var23.x, (int)(var23.y + 0.12F), (int)var23.z)];
										if(var84 != null && var84.getLiquidType() != Liquid.none) {
											Liquid var94 = var84.getLiquidType();
											if(var94 == Liquid.water) {
												var89.fogRed = 0.02F;
												var89.fogGreen = 0.02F;
												var89.fogBlue = 0.2F;
											} else if(var94 == Liquid.lava) {
												var89.fogRed = 0.6F;
												var89.fogGreen = 0.1F;
												var89.fogBlue = 0.0F;
											}
										}

										if(var89.minecraft.options.anaglyph3d) {
											var86 = (var89.fogRed * 30.0F + var89.fogGreen * 59.0F + var89.fogBlue * 11.0F) / 100.0F;
											var17 = (var89.fogRed * 30.0F + var89.fogGreen * 70.0F) / 100.0F;
											var18 = (var89.fogRed * 30.0F + var89.fogBlue * 70.0F) / 100.0F;
											var89.fogRed = var86;
											var89.fogGreen = var17;
											var89.fogBlue = var18;
										}

										GL11.glClearColor(var89.fogRed, var89.fogGreen, var89.fogBlue, 0.0F);
										GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
										var89.fogColorMultiplier = 1.0F;
										GL11.glEnable(GL11.GL_CULL_FACE);
										var89.renderDistance = (float)(512 >> (var89.minecraft.options.viewDistance << 1));
										GL11.glMatrixMode(GL11.GL_PROJECTION);
										GL11.glLoadIdentity();
										var24 = 0.07F;
										if(var89.minecraft.options.anaglyph3d) {
											GL11.glTranslatef((float)(-((var90 << 1) - 1)) * var24, 0.0F, 0.0F);
										}

										Player var117 = var89.minecraft.player;
										var78 = 70.0F;
										if(var117.health <= 0) {
											var86 = (float)var117.deathTime + var93;
											var78 /= (1.0F - 500.0F / (var86 + 500.0F)) * 2.0F + 1.0F;
										}

										GLU.gluPerspective(var78, (float)var89.minecraft.width / (float)var89.minecraft.height, 0.05F, var89.renderDistance);
										GL11.glMatrixMode(GL11.GL_MODELVIEW);
										GL11.glLoadIdentity();
										if(var89.minecraft.options.anaglyph3d) {
											GL11.glTranslatef((float)((var90 << 1) - 1) * 0.1F, 0.0F, 0.0F);
										}

										var89.renderHurtFrames(var93);
										if(var89.minecraft.options.bobView) {
											var89.cameraBob(var93);
										}

										var117 = var89.minecraft.player;
										GL11.glTranslatef(0.0F, 0.0F, -0.1F);
										GL11.glRotatef(var117.xRotO + (var117.xRot - var117.xRotO) * var93, 1.0F, 0.0F, 0.0F);
										GL11.glRotatef(var117.yRotO + (var117.yRot - var117.yRotO) * var93, 0.0F, 1.0F, 0.0F);
										var78 = var117.xo + (var117.x - var117.xo) * var93;
										var86 = var117.yo + (var117.y - var117.yo) * var93;
										var17 = var117.zo + (var117.z - var117.zo) * var93;
										GL11.glTranslatef(-var78, -var86, -var17);
										Frustum var88 = FrustumCuller.calculateFrustum();
										Frustum var104 = var88;
										LevelRenderer var101 = var89.minecraft.levelRenderer;

										int var108;
										for(var108 = 0; var108 < var101.chunks.length; ++var108) {
											var101.chunks[var108].isInFrustum(var104);
										}

										var101 = var89.minecraft.levelRenderer;
										Collections.sort(var101.allDirtyChunks, new DirtyChunkSorter(var55));
										var108 = var101.allDirtyChunks.size() - 1;
										int var111 = var101.allDirtyChunks.size();
										if(var111 > 3) {
											var111 = 3;
										}

										int var112;
										for(var112 = 0; var112 < var111; ++var112) {
											Chunk var115 = (Chunk)var101.allDirtyChunks.remove(var108 - var112);
											var115.rebuild();
											var115.dirty = false;
										}

										var89.setupFog();
										GL11.glEnable(GL11.GL_FOG);
										var66.render(var55, 0);
										int var58;
										int var95;
										int var96;
										int var97;
										int var100;
										Tesselator var116;
										int var119;
										if(var57.isSolid(var55.x, var55.y, var55.z, 0.1F)) {
											var58 = (int)var55.x;
											var96 = (int)var55.y;
											var95 = (int)var55.z;

											for(var97 = var58 - 1; var97 <= var58 + 1; ++var97) {
												for(var100 = var96 - 1; var100 <= var96 + 1; ++var100) {
													for(int var20 = var95 - 1; var20 <= var95 + 1; ++var20) {
														var111 = var20;
														var108 = var100;
														int var105 = var97;
														var112 = var66.level.getTile(var97, var100, var20);
														if(var112 != 0 && Tile.tiles[var112].isSolid()) {
															GL11.glColor4f(0.2F, 0.2F, 0.2F, 1.0F);
															GL11.glDepthFunc(GL11.GL_LESS);
															var116 = Tesselator.instance;
															var116.begin();

															for(var119 = 0; var119 < 6; ++var119) {
																Tile.tiles[var112].renderFace(var116, var105, var108, var111, var119);
															}

															var116.end();
															GL11.glCullFace(GL11.GL_FRONT);
															var116.begin();

															for(var119 = 0; var119 < 6; ++var119) {
																Tile.tiles[var112].renderFace(var116, var105, var108, var111, var119);
															}

															var116.end();
															GL11.glCullFace(GL11.GL_BACK);
															GL11.glDepthFunc(GL11.GL_LEQUAL);
														}
													}
												}
											}
										}

//										var89.toggleLight(true);
										Vec3 var106 = var89.getPlayerRotVec(var93);
										var66.level.blockMap.render(var106, var88, var66.textures, var93);
//										var89.toggleLight(false);
										var89.setupFog();
										float var110 = var93;
										ParticleEngine var103 = var77;
										var24 = -Mth.cos(var55.yRot * (float)Math.PI / 180.0F);
										var25 = -Mth.sin(var55.yRot * (float)Math.PI / 180.0F);
										var114 = -var25 * Mth.sin(var55.xRot * (float)Math.PI / 180.0F);
										var27 = var24 * Mth.sin(var55.xRot * (float)Math.PI / 180.0F);
										var78 = Mth.cos(var55.xRot * (float)Math.PI / 180.0F);

										for(var96 = 0; var96 < 2; ++var96) {
											if(var103.particles[var96].size() != 0) {
												var95 = 0;
												if(var96 == 0) {
													var95 = var103.textures.loadTexture("/particles.png");
												}

												if(var96 == 1) {
													var95 = var103.textures.loadTexture("/terrain.png");
												}

												GL11.glBindTexture(GL11.GL_TEXTURE_2D, var95);
												Tesselator var99 = Tesselator.instance;
												var99.begin();

												for(var58 = 0; var58 < var103.particles[var96].size(); ++var58) {
													Particle var79 = (Particle)var103.particles[var96].get(var58);
													var79.render(var99, var110, var24, var78, var25, var114, var27);
												}

												var99.end();
											}
										}

										var66.renderSurroundingGround();
										var89.setupFog();
										var101 = var66;
										GL11.glBindTexture(GL11.GL_TEXTURE_2D, var66.textures.loadTexture("/clouds.png"));
										GL11.glEnable(GL11.GL_ALPHA_TEST);
										GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
										GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
										var110 = (float)(var66.level.cloudColor >> 16 & 255) / 255.0F;
										var24 = (float)(var66.level.cloudColor >> 8 & 255) / 255.0F;
										var25 = (float)(var66.level.cloudColor & 255) / 255.0F;
										if(var66.minecraft.options.anaglyph3d) {
											var114 = (var110 * 30.0F + var24 * 59.0F + var25 * 11.0F) / 100.0F;
											var27 = (var110 * 30.0F + var24 * 70.0F) / 100.0F;
											var78 = (var110 * 30.0F + var25 * 70.0F) / 100.0F;
											var110 = var114;
											var24 = var27;
											var25 = var78;
										}

										var116 = Tesselator.instance;
										var86 = 0.0F;
										var17 = 0.5F / 1024.0F;
										var86 = (float)(var66.level.depth + 2);
										var18 = ((float)var66.cloudTickCounter + var93) * var17 * 0.03F;
										var6 = 0.0F;
										var116.begin();
										var116.color(var110, var24, var25);

										for(var64 = -2048; var64 < var101.level.width + 2048; var64 += 512) {
											for(var100 = -2048; var100 < var101.level.height + 2048; var100 += 512) {
												var116.vertexUV((float)var64, var86, (float)(var100 + 512), (float)var64 * var17 + var18, (float)(var100 + 512) * var17);
												var116.vertexUV((float)(var64 + 512), var86, (float)(var100 + 512), (float)(var64 + 512) * var17 + var18, (float)(var100 + 512) * var17);
												var116.vertexUV((float)(var64 + 512), var86, (float)var100, (float)(var64 + 512) * var17 + var18, (float)var100 * var17);
												var116.vertexUV((float)var64, var86, (float)var100, (float)var64 * var17 + var18, (float)var100 * var17);
												var116.vertexUV((float)var64, var86, (float)var100, (float)var64 * var17 + var18, (float)var100 * var17);
												var116.vertexUV((float)(var64 + 512), var86, (float)var100, (float)(var64 + 512) * var17 + var18, (float)var100 * var17);
												var116.vertexUV((float)(var64 + 512), var86, (float)(var100 + 512), (float)(var64 + 512) * var17 + var18, (float)(var100 + 512) * var17);
												var116.vertexUV((float)var64, var86, (float)(var100 + 512), (float)var64 * var17 + var18, (float)(var100 + 512) * var17);
											}
										}

										var116.end();
										GL11.glDisable(GL11.GL_TEXTURE_2D);
										var116.begin();
										var18 = (float)(var101.level.skyColor >> 16 & 255) / 255.0F;
										var6 = (float)(var101.level.skyColor >> 8 & 255) / 255.0F;
										var73 = (float)(var101.level.skyColor & 255) / 255.0F;
										if(var101.minecraft.options.anaglyph3d) {
											var19 = (var18 * 30.0F + var6 * 59.0F + var73 * 11.0F) / 100.0F;
											var78 = (var18 * 30.0F + var6 * 70.0F) / 100.0F;
											var86 = (var18 * 30.0F + var73 * 70.0F) / 100.0F;
											var18 = var19;
											var6 = var78;
											var73 = var86;
										}

										var116.color(var18, var6, var73);
										var86 = (float)(var101.level.depth + 10);

										for(var100 = -2048; var100 < var101.level.width + 2048; var100 += 512) {
											for(var76 = -2048; var76 < var101.level.height + 2048; var76 += 512) {
												var116.vertex((float)var100, var86, (float)var76);
												var116.vertex((float)(var100 + 512), var86, (float)var76);
												var116.vertex((float)(var100 + 512), var86, (float)(var76 + 512));
												var116.vertex((float)var100, var86, (float)(var76 + 512));
											}
										}

										var116.end();
										GL11.glEnable(GL11.GL_TEXTURE_2D);
										var89.setupFog();
										int var120;
										if(var89.minecraft.hitResult != null) {
											GL11.glDisable(GL11.GL_ALPHA_TEST);
											HitResult var10001 = var89.minecraft.hitResult;
											var111 = var55.inventory.getSelected();
											boolean var113 = false;
											HitResult var107 = var10001;
											var101 = var66;
											Tesselator var118 = Tesselator.instance;
											GL11.glEnable(GL11.GL_BLEND);
											GL11.glEnable(GL11.GL_ALPHA_TEST);
											GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
											GL11.glColor4f(1.0F, 1.0F, 1.0F, (Mth.sin((float)System.currentTimeMillis() / 100.0F) * 0.2F + 0.4F) * 0.5F);
											if(var66.hurtTime > 0.0F) {
												GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR);
												var120 = var66.textures.loadTexture("/terrain.png");
												GL11.glBindTexture(GL11.GL_TEXTURE_2D, var120);
												GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
												GL11.glPushMatrix();
												var119 = var66.level.getTile(var107.x, var107.y, var107.z);
												var84 = var119 > 0 ? Tile.tiles[var119] : null;
												var86 = (var84.xx0 + var84.xx1) / 2.0F;
												var17 = (var84.yy0 + var84.yy1) / 2.0F;
												var18 = (var84.zz0 + var84.zz1) / 2.0F;
												GL11.glTranslatef((float)var107.x + var86, (float)var107.y + var17, (float)var107.z + var18);
												var6 = 1.01F;
												GL11.glScalef(var6, var6, var6);
												GL11.glTranslatef(-((float)var107.x + var86), -((float)var107.y + var17), -((float)var107.z + var18));
												var118.begin();
												var118.noColor();
												GL11.glDepthMask(false);
												if(var84 == null) {
													var84 = Tile.rock;
												}

												for(var64 = 0; var64 < 6; ++var64) {
													var84.renderFaceNoTexture(var118, var107.x, var107.y, var107.z, var64, 240 + (int)(var101.hurtTime * 10.0F));
												}

												var118.end();
												GL11.glDepthMask(true);
												GL11.glPopMatrix();
											}

											GL11.glDisable(GL11.GL_BLEND);
											GL11.glDisable(GL11.GL_ALPHA_TEST);
											var10001 = var89.minecraft.hitResult;
											var55.inventory.getSelected();
											var113 = false;
											var107 = var10001;
											GL11.glEnable(GL11.GL_BLEND);
											GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
											GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
											GL11.glLineWidth(2.0F);
											GL11.glDisable(GL11.GL_TEXTURE_2D);
											GL11.glDepthMask(false);
											var24 = 0.002F;
											var112 = var66.level.getTile(var107.x, var107.y, var107.z);
											if(var112 > 0) {
												AABB var122 = Tile.tiles[var112].getAABB(var107.x, var107.y, var107.z).grow(var24, var24, var24);
												GL11.glBegin(GL11.GL_LINE_STRIP);
												GL11.glVertex3f(var122.x0, var122.y0, var122.z0);
												GL11.glVertex3f(var122.x1, var122.y0, var122.z0);
												GL11.glVertex3f(var122.x1, var122.y0, var122.z1);
												GL11.glVertex3f(var122.x0, var122.y0, var122.z1);
												GL11.glVertex3f(var122.x0, var122.y0, var122.z0);
												GL11.glEnd();
												GL11.glBegin(GL11.GL_LINE_STRIP);
												GL11.glVertex3f(var122.x0, var122.y1, var122.z0);
												GL11.glVertex3f(var122.x1, var122.y1, var122.z0);
												GL11.glVertex3f(var122.x1, var122.y1, var122.z1);
												GL11.glVertex3f(var122.x0, var122.y1, var122.z1);
												GL11.glVertex3f(var122.x0, var122.y1, var122.z0);
												GL11.glEnd();
												GL11.glBegin(GL11.GL_LINES);
												GL11.glVertex3f(var122.x0, var122.y0, var122.z0);
												GL11.glVertex3f(var122.x0, var122.y1, var122.z0);
												GL11.glVertex3f(var122.x1, var122.y0, var122.z0);
												GL11.glVertex3f(var122.x1, var122.y1, var122.z0);
												GL11.glVertex3f(var122.x1, var122.y0, var122.z1);
												GL11.glVertex3f(var122.x1, var122.y1, var122.z1);
												GL11.glVertex3f(var122.x0, var122.y0, var122.z1);
												GL11.glVertex3f(var122.x0, var122.y1, var122.z1);
												GL11.glEnd();
											}

											GL11.glDepthMask(true);
											GL11.glEnable(GL11.GL_TEXTURE_2D);
											GL11.glDisable(GL11.GL_BLEND);
											GL11.glEnable(GL11.GL_ALPHA_TEST);
										}

										GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
										var89.setupFog();
										GL11.glEnable(GL11.GL_TEXTURE_2D);
										GL11.glEnable(GL11.GL_BLEND);
//										GL11.glBindTexture(GL11.GL_TEXTURE_2D, var66.textures.loadTexture("/water.png"));
//										GL11.glCallList(var66.surroundLists + 1);
										GL11.glDisable(GL11.GL_BLEND);
										GL11.glEnable(GL11.GL_BLEND);
//										GL11.glColorMask(false, false, false, false);
										var58 = var66.render(var55, 1);
//										GL11.glColorMask(true, true, true, true);
										if(var89.minecraft.options.anaglyph3d) {
											if(var90 == 0) {
												GL11.glColorMask(false, true, true, false);
											} else {
												GL11.glColorMask(true, false, false, false);
											}
										}

										if(var58 > 0) {
											GL11.glBindTexture(GL11.GL_TEXTURE_2D, var66.textures.loadTexture("/terrain.png"));
											GL11.glCallLists(var66.ib);
										}

										GL11.glDepthMask(true);
										GL11.glDisable(GL11.GL_BLEND);
										GL11.glDisable(GL11.GL_FOG);
										if(var89.minecraft.raining) {
											float var109 = var93;
											var21 = var89;
											var23 = var89.minecraft.player;
											Level var121 = var89.minecraft.level;
											var112 = (int)var23.x;
											var120 = (int)var23.y;
											var119 = (int)var23.z;
											Tesselator var98 = Tesselator.instance;
											GL11.glDisable(GL11.GL_CULL_FACE);
											GL11.glNormal3f(0.0F, 1.0F, 0.0F);
											GL11.glEnable(GL11.GL_BLEND);
											GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
											GL11.glEnable(GL11.GL_ALPHA_TEST);
											GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
											GL11.glEnable(GL11.GL_TEXTURE_2D);
											GL11.glBindTexture(GL11.GL_TEXTURE_2D, var89.minecraft.textures.loadTexture("/rain.png"));
											var95 = var112 - 5;

											while(true) {
												if(var95 > var112 + 5) {
													GL11.glEnable(GL11.GL_CULL_FACE);
													GL11.glDisable(GL11.GL_BLEND);
													break;
												}

												for(var97 = var119 - 5; var97 <= var119 + 5; ++var97) {
													var58 = var121.getHighestTile(var95, var97);
													var64 = var120 - 5;
													var100 = var120 + 5;
													if(var64 < var58) {
														var64 = var58;
													}

													if(var100 < var58) {
														var100 = var58;
													}

													if(var64 != var100) {
														var86 = ((float)((var21.rainTicks + var95 * 3121 + var97 * 418711) % 32) + var109) / 32.0F;
														float var56 = (float)var95 + 0.5F - var23.x;
														var6 = (float)var97 + 0.5F - var23.z;
														float var69 = Mth.sqrt_float(var56 * var56 + var6 * var6) / (float)5;
														GL11.glColor4f(1.0F, 1.0F, 1.0F, (1.0F - var69 * var69) * 0.7F);
														var98.begin();
														var98.vertexUV((float)var95, (float)var64, (float)var97, 0.0F, (float)var64 * 2.0F / 8.0F + var86 * 2.0F);
														var98.vertexUV((float)(var95 + 1), (float)var64, (float)(var97 + 1), 2.0F, (float)var64 * 2.0F / 8.0F + var86 * 2.0F);
														var98.vertexUV((float)(var95 + 1), (float)var100, (float)(var97 + 1), 2.0F, (float)var100 * 2.0F / 8.0F + var86 * 2.0F);
														var98.vertexUV((float)var95, (float)var100, (float)var97, 0.0F, (float)var100 * 2.0F / 8.0F + var86 * 2.0F);
														var98.vertexUV((float)var95, (float)var64, (float)(var97 + 1), 0.0F, (float)var64 * 2.0F / 8.0F + var86 * 2.0F);
														var98.vertexUV((float)(var95 + 1), (float)var64, (float)var97, 2.0F, (float)var64 * 2.0F / 8.0F + var86 * 2.0F);
														var98.vertexUV((float)(var95 + 1), (float)var100, (float)var97, 2.0F, (float)var100 * 2.0F / 8.0F + var86 * 2.0F);
														var98.vertexUV((float)var95, (float)var100, (float)(var97 + 1), 0.0F, (float)var100 * 2.0F / 8.0F + var86 * 2.0F);
														var98.end();
													}
												}

												++var95;
											}
										}

										if(var89.entity != null) {
											var89.entity.renderHover(var89.minecraft.textures, var93);
										}

										GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
										GL11.glLoadIdentity();
										if(var89.minecraft.options.anaglyph3d) {
											GL11.glTranslatef((float)((var90 << 1) - 1) * 0.1F, 0.0F, 0.0F);
										}

										var89.renderHurtFrames(var93);
										if(var89.minecraft.options.bobView) {
											var89.cameraBob(var93);
										}

										TileRenderer var123 = var89.tileRenderer;
										var114 = var123.oProgress + (var123.progress - var123.oProgress) * var93;
										var117 = var123.minecraft.player;
										GL11.glPushMatrix();
										GL11.glRotatef(var117.xRotO + (var117.xRot - var117.xRotO) * var93, 1.0F, 0.0F, 0.0F);
										GL11.glRotatef(var117.yRotO + (var117.yRot - var117.yRotO) * var93, 0.0F, 1.0F, 0.0F);
										var123.minecraft.gameRenderer.toggleLight(true);
										GL11.glPopMatrix();
										GL11.glPushMatrix();
										var78 = 0.8F;
										if(var123.move) {
											var86 = ((float)var123.rot + var93) / 7.0F;
											var17 = Mth.sin(var86 * (float)Math.PI);
											var18 = Mth.sin(Mth.sqrt_float(var86) * (float)Math.PI);
											GL11.glTranslatef(-var18 * 0.4F, Mth.sin(Mth.sqrt_float(var86) * (float)Math.PI * 2.0F) * 0.2F, -var17 * 0.2F);
										}

										GL11.glTranslatef(0.7F * var78, -0.65F * var78 - (1.0F - var114) * 0.6F, -0.9F * var78);
										GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
										GL11.glEnable(GL11.GL_NORMALIZE);
										if(var123.move) {
											var86 = ((float)var123.rot + var93) / 7.0F;
											var17 = Mth.sin(var86 * var86 * (float)Math.PI);
											var18 = Mth.sin(Mth.sqrt_float(var86) * (float)Math.PI);
											GL11.glRotatef(var18 * 80.0F, 0.0F, 1.0F, 0.0F);
											GL11.glRotatef(-var17 * 20.0F, 1.0F, 0.0F, 0.0F);
										}

										var86 = var123.minecraft.level.getBrightness((int)var117.x, (int)var117.y, (int)var117.z);
										GL11.glColor4f(var86, var86, var86, 1.0F);
										GL11.glEnable(GL11.GL_TEXTURE_2D);
										GL11.glEnable(GL11.GL_ALPHA_TEST);
										GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);;
										Tesselator var102 = Tesselator.instance;
										if(var123.tile != null) {
											var18 = 0.4F;
											GL11.glScalef(var18, var18, var18);
											GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
											GL11.glBindTexture(GL11.GL_TEXTURE_2D, var123.minecraft.textures.loadTexture("/terrain.png"));
											var123.tile.renderGuiTile(var102);
										} else {
											var117.bindTexture(var123.minecraft.textures);
											GL11.glScalef(1.0F, -1.0F, -1.0F);
											GL11.glTranslatef(0.0F, 0.2F, 0.0F);
											GL11.glRotatef(-120.0F, 0.0F, 0.0F, 1.0F);
											GL11.glScalef(1.0F, 1.0F, 1.0F);
											var18 = 1.0F / 16.0F;
											Cube var67 = var123.minecraft.player.getModel().leftArm;
											if(!var67.compiled) {
												var67.translateTo(var18);
											}

											GL11.glCallList(var67.list);
										}

										GL11.glDisable(GL11.GL_NORMALIZE);
										GL11.glPopMatrix();
										var123.minecraft.gameRenderer.toggleLight(false);
										if(!var89.minecraft.options.anaglyph3d) {
											break;
										}

										++var90;
									}

									var54.minecraft.gui.render(var60, var54.minecraft.screen != null, var72, var71);
								} else {
									GL11.glViewport(0, 0, var54.minecraft.width, var54.minecraft.height);
									GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
									GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
									GL11.glMatrixMode(GL11.GL_PROJECTION);
									GL11.glLoadIdentity();
									GL11.glMatrixMode(GL11.GL_MODELVIEW);
									GL11.glLoadIdentity();
									var54.render();
								}

								if(var54.minecraft.screen != null) {
									var54.minecraft.screen.render(var72, var71);
								}

								Thread.yield();
								Display.update();
							}
						}

						if(this.options.limitFramerate) {
							Thread.sleep(5L);
						}

						checkGlError("Post render");
						++var3;
					} catch (Exception var47) {
						this.setScreen(new ErrorScreen("Client error", "The game broke! [" + var47 + "]"));
						var47.printStackTrace();
					}

					while(System.currentTimeMillis() >= var1 + 1000L) {
						this.fpsString = var3 + " fps, " + Chunk.updates + " chunk updates";
						Chunk.updates = 0;
						var1 += 1000L;
						var3 = 0;
					}
				}

			return;
		} catch (StopGameException var46) {
			return;
		} catch (Exception var47) {
			var47.printStackTrace();
		} finally {
			this.destroy();
		}

	}

	public final void grabMouse() {
		if(!this.mouseGrabbed) {
			this.mouseGrabbed = true;
				Mouse.setGrabbed(true);

			this.setScreen((Screen)null);
			this.oFrames = this.frames + 10000;
		}
	}

	public final void pauseScreen() {
		if(this.screen == null && (!(this.screen instanceof PauseScreen))) {
			this.setScreen(new PauseScreen());
			LevelIO.save(this.level, new VFile2("level.dat"));
		}
	}
	
	private int saveCountdown = 600;

	private void levelSave() {
	    if (level == null) return;

	    saveCountdown--;
	    if (saveCountdown <= 0) {
	    	LevelIO.save(this.level, new VFile2("level.dat"));
	        saveCountdown = 600;
	    }
	}

	private void clickMouse(int var1) {
		if(var1 != 0 || this.clickCounter <= 0) {
			TileRenderer var6;
			if(var1 == 0) {
				var6 = this.gameRenderer.tileRenderer;
				var6.rot = -1;
				var6.move = true;
			}

			int var2;
			if(var1 == 1) {
				var2 = this.player.inventory.getSelected();
				if(var2 > 0 && this.gamemode.removeResource(this.player, var2)) {
					var6 = this.gameRenderer.tileRenderer;
					var6.progress = 0.0F;
					return;
				}
			}

			if(this.hitResult == null) {
				if(var1 == 0 && !(this.gamemode instanceof CreativeGameMode)) {
					this.clickCounter = 10;
				}

			} else {
				if(this.hitResult.type == 1) {
					if(var1 == 0) {
						this.hitResult.entity.hurt(this.player, 4);
						return;
					}
				} else if(this.hitResult.type == 0) {
					var2 = this.hitResult.x;
					int var3 = this.hitResult.y;
					int var4 = this.hitResult.z;
					if(var1 != 0) {
						if(this.hitResult.f == 0) {
							--var3;
						}

						if(this.hitResult.f == 1) {
							++var3;
						}

						if(this.hitResult.f == 2) {
							--var4;
						}

						if(this.hitResult.f == 3) {
							++var4;
						}

						if(this.hitResult.f == 4) {
							--var2;
						}

						if(this.hitResult.f == 5) {
							++var2;
						}
					}

					Tile var5 = Tile.tiles[this.level.getTile(var2, var3, var4)];
					if(var1 == 0) {
						if(var5 != Tile.unbreakable || this.player.userType >= 100) {
							this.gamemode.startDestroyBlock(var2, var3, var4);
							return;
						}
					} else {
						int var8 = this.player.inventory.getSelected();
						if(var8 <= 0) {
							return;
						}

						Tile var9 = Tile.tiles[this.level.getTile(var2, var3, var4)];
						if(var9 == null || var9 == Tile.water || var9 == Tile.calmWater || var9 == Tile.lava || var9 == Tile.calmLava) {
							AABB var10 = Tile.tiles[var8].getTileAABB(var2, var3, var4);
							if(var10 == null || (this.player.bb.intersects(var10) ? false : this.level.isFree(var10))) {
								if(!this.gamemode.removeResource(var8)) {
									return;
								}

								if(this.isOnlineClient()) {
									this.networkClient.sendTileUpdated(var2, var3, var4, var1, var8);
								}

								this.level.netSetTile(var2, var3, var4, var8);
								var6 = this.gameRenderer.tileRenderer;
								var6.progress = 0.0F;
								Tile.tiles[var8].onPlace(this.level, var2, var3, var4);
							}
						}
					}
				}

			}
		}
	}

	private void tick() {
		SoundManager var1 = this.soundManager;
		if(System.currentTimeMillis() > var1.lastMusic && var1.playMusic()) {
			var1.lastMusic = System.currentTimeMillis() + (long)var1.random.nextInt(900000) + 300000L;
		}


		this.gamemode.tick();
		Gui var16 = this.gui;
		++var16.tickCounter;

		int var19;
		for(var19 = 0; var19 < var16.messages.size(); ++var19) {
			++((GuiMessage)var16.messages.get(var19)).counter;
		}

		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textures.loadTexture("/terrain.png"));
		Textures var17 = this.textures;

		for(var19 = 0; var19 < var17.textureList.size(); ++var19) {
			DynamicTexture var3 = (DynamicTexture)var17.textureList.get(var19);
			var3.anaglyph = var17.options.anaglyph3d;
			var3.tick();
			var17.pixels.clear();
			var17.pixels.put(var3.pixels);
			var17.pixels.position(0).limit(var3.pixels.length);
			GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, var3.tex % 16 << 4, var3.tex / 16 << 4, 16, 16, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)var17.pixels);
		}

		int var4;
		int var8;
		int var38;
		int var46;
		int var47;
		if(this.networkClient != null && !(this.screen instanceof ErrorScreen)) {
			if(!this.networkClient.isConnected()) {
				this.loadingScreen.beginLevelLoading("Connecting..");
				this.loadingScreen.setLoadingProgress(0);
			} else {
				Client var18 = this.networkClient;
				if(var18.processData) {
					SocketConnection var22 = var18.serverConnection;
					if(var22.client.isConnected()) {
						try {
							SocketConnection var21 = var18.serverConnection;
							IWebSocketFrame packet = var21.webSocket.getNextBinaryFrame();
							byte[] packetData = packet == null ? null : packet.getByteArray();

							if (packetData != null && packetData.length > 0) {
								var21.readBuffer.put(packetData);
							}
							var4 = 0;

							while(var21.readBuffer.position() > 0 && var4++ != 100) {
								var21.readBuffer.flip();
								byte var5 = var21.readBuffer.get(0);
								Packet var6 = Packet.PACKETS[var5];
								if(var6 == null) {
									throw new IOException("Bad command: " + var5);
								}

								if(var21.readBuffer.remaining() < var6.size + 1) {
									var21.readBuffer.compact();
									break;
								}

								var21.readBuffer.get();
								Object[] var7 = new Object[var6.fields.length];

								for(var8 = 0; var8 < var7.length; ++var8) {
									var7[var8] = var21.read(var6.fields[var8]);
								}

								Client var43 = var21.client;
								if(var43.processData) {
									if(var6 == Packet.LOGIN) {
										var43.minecraft.loadingScreen.beginLevelLoading(var7[1].toString());
										var43.minecraft.loadingScreen.levelLoadUpdate(var7[2].toString());
										var43.minecraft.player.userType = ((Byte)var7[3]).byteValue();
									} else if(var6 == Packet.LEVEL_INITIALIZE) {
										var43.minecraft.loadLegacy((Level)null);
										var43.levelBuffer = new ByteArrayOutputStream();
									} else if(var6 == Packet.LEVEL_DATA_CHUNK) {
										short var11 = ((Short)var7[0]).shortValue();
										byte[] var12 = (byte[])((byte[])var7[1]);
										byte var13 = ((Byte)var7[2]).byteValue();
										var43.minecraft.loadingScreen.setLoadingProgress(var13);
										var43.levelBuffer.write(var12, 0, var11);
									} else if(var6 == Packet.LEVEL_FINALIZE) {
										try {
											var43.levelBuffer.close();
										} catch (IOException var14) {
											var14.printStackTrace();
										}

										byte[] var57 = LevelIO.loadBlocks(new ByteArrayInputStream(var43.levelBuffer.toByteArray()));
										var43.levelBuffer = null;
										short var60 = ((Short)var7[0]).shortValue();
										short var63 = ((Short)var7[1]).shortValue();
										short var27 = ((Short)var7[2]).shortValue();
										Level var30 = new Level();
										var30.setNetworkMode(true);
										var30.setData(var60, var63, var27, var57);
										var43.minecraft.loadLegacy(var30);
										var43.minecraft.hideScreen = false;
									} else if(var6 == Packet.SET_TILE) {
										if(var43.minecraft.level != null) {
											var43.minecraft.level.netSetTile(((Short)var7[0]).shortValue(), ((Short)var7[1]).shortValue(), ((Short)var7[2]).shortValue(), ((Byte)var7[3]).byteValue());
										}
									} else {
										byte var10;
										byte var10001;
										short var10003;
										short var10004;
										String var33;
										NetworkPlayer var34;
										short var36;
										short var45;
										if(var6 == Packet.PLAYER_JOIN) {
											var10001 = ((Byte)var7[0]).byteValue();
											String var10002 = (String)var7[1];
											var10003 = ((Short)var7[2]).shortValue();
											var10004 = ((Short)var7[3]).shortValue();
											short var10005 = ((Short)var7[4]).shortValue();
											byte var10006 = ((Byte)var7[5]).byteValue();
											byte var58 = ((Byte)var7[6]).byteValue();
											var10 = var10006;
											short var9 = var10005;
											var45 = var10004;
											var36 = var10003;
											var33 = var10002;
											var5 = var10001;
											if(var5 >= 0) {
												var10 = (byte)(var10 + 128);
												var45 = (short)(var45 - 22);
												var34 = new NetworkPlayer(var43.minecraft, var5, var33, var36, var45, var9, (float)(var10 * 360) / 256.0F, (float)(var58 * 360) / 256.0F);
												var43.players.put(Byte.valueOf(var5), var34);
												var43.minecraft.level.addEntity(var34);
											} else {
												var43.minecraft.level.setSpawnPos(var36 / 32, var45 / 32, var9 / 32, (float)(var10 * 320 / 256));
												var43.minecraft.player.moveTo((float)var36 / 32.0F, (float)var45 / 32.0F, (float)var9 / 32.0F, (float)(var10 * 360) / 256.0F, (float)(var58 * 360) / 256.0F);
											}
										} else {
											byte var48;
											NetworkPlayer var59;
											byte var70;
											if(var6 == Packet.PLAYER_TELEPORT) {
												var10001 = ((Byte)var7[0]).byteValue();
												short var66 = ((Short)var7[1]).shortValue();
												var10003 = ((Short)var7[2]).shortValue();
												var10004 = ((Short)var7[3]).shortValue();
												var70 = ((Byte)var7[4]).byteValue();
												var10 = ((Byte)var7[5]).byteValue();
												var48 = var70;
												var45 = var10004;
												var36 = var10003;
												short var35 = var66;
												var5 = var10001;
												if(var5 < 0) {
													var43.minecraft.player.moveTo((float)var35 / 32.0F, (float)var36 / 32.0F, (float)var45 / 32.0F, (float)(var48 * 360) / 256.0F, (float)(var10 * 360) / 256.0F);
												} else {
													var48 = (byte)(var48 + 128);
													var36 = (short)(var36 - 22);
													var59 = (NetworkPlayer)var43.players.get(Byte.valueOf(var5));
													if(var59 != null) {
														var59.teleport(var35, var36, var45, (float)(var48 * 360) / 256.0F, (float)(var10 * 360) / 256.0F);
													}
												}
											} else {
												byte var37;
												byte var40;
												byte var51;
												byte var67;
												byte var68;
												if(var6 == Packet.PLAYER_MOVE_AND_ROTATE) {
													var10001 = ((Byte)var7[0]).byteValue();
													var67 = ((Byte)var7[1]).byteValue();
													var68 = ((Byte)var7[2]).byteValue();
													byte var69 = ((Byte)var7[3]).byteValue();
													var70 = ((Byte)var7[4]).byteValue();
													var10 = ((Byte)var7[5]).byteValue();
													var48 = var70;
													var51 = var69;
													var40 = var68;
													var37 = var67;
													var5 = var10001;
													if(var5 >= 0) {
														var48 = (byte)(var48 + 128);
														var59 = (NetworkPlayer)var43.players.get(Byte.valueOf(var5));
														if(var59 != null) {
															var59.queue(var37, var40, var51, (float)(var48 * 360) / 256.0F, (float)(var10 * 360) / 256.0F);
														}
													}
												} else if(var6 == Packet.PLAYER_ROTATE) {
													var10001 = ((Byte)var7[0]).byteValue();
													var67 = ((Byte)var7[1]).byteValue();
													var40 = ((Byte)var7[2]).byteValue();
													var37 = var67;
													var5 = var10001;
													if(var5 >= 0) {
														var37 = (byte)(var37 + 128);
														NetworkPlayer var52 = (NetworkPlayer)var43.players.get(Byte.valueOf(var5));
														if(var52 != null) {
															var52.queue((float)(var37 * 360) / 256.0F, (float)(var40 * 360) / 256.0F);
														}
													}
												} else if(var6 == Packet.PLAYER_MOVE) {
													var10001 = ((Byte)var7[0]).byteValue();
													var67 = ((Byte)var7[1]).byteValue();
													var68 = ((Byte)var7[2]).byteValue();
													var51 = ((Byte)var7[3]).byteValue();
													var40 = var68;
													var37 = var67;
													var5 = var10001;
													if(var5 >= 0) {
														NetworkPlayer var53 = (NetworkPlayer)var43.players.get(Byte.valueOf(var5));
														if(var53 != null) {
															var53.queue(var37, var40, var51);
														}
													}
												} else if(var6 == Packet.PLAYER_DISCONNECT) {
													var5 = ((Byte)var7[0]).byteValue();
													if(var5 >= 0) {
														var34 = (NetworkPlayer)var43.players.remove(Byte.valueOf(var5));
														if(var34 != null) {
															var34.clear();
															var43.minecraft.level.removeEntity(var34);
														}
													}
												} else if(var6 == Packet.CHAT_MESSAGE) {
													var10001 = ((Byte)var7[0]).byteValue();
													var33 = (String)var7[1];
													var5 = var10001;
													if(var5 < 0) {
														var43.minecraft.gui.addMessage("&e" + var33);
													} else {
														var43.players.get(Byte.valueOf(var5));
														var43.minecraft.gui.addMessage(var33);
													}
												} else if(var6 == Packet.KICK_PLAYER) {
													var43.serverConnection.disconnect();
													var43.minecraft.setScreen(new ErrorScreen("Connection lost", (String)var7[0]));
												} else if(var6 == Packet.USER_TYPE) {
													var43.minecraft.player.userType = ((Byte)var7[0]).byteValue();
												}
											}
										}
									}
								}

								if(!this.networkClient.isConnected()) {
									break;
								}

								var21.readBuffer.compact();
							}

							var21.flush();
						} catch (Exception var15) {
							var18.minecraft.setScreen(new ErrorScreen("Disconnected!", "You\'ve lost connection to the server"));
							var18.minecraft.hideScreen = false;
							var15.printStackTrace();
							var18.serverConnection.disconnect();
							var18.minecraft.networkClient = null;
						}
					}
				}

				Player var31 = this.player;
				var18 = this.networkClient;
				if(this.networkClient.isConnected()) {
					int var23 = (int)(var31.x * 32.0F);
					var4 = (int)(var31.y * 32.0F);
					var38 = (int)(var31.z * 32.0F);
					var46 = (int)(var31.yRot * 256.0F / 360.0F) & 255;
					var47 = (int)(var31.xRot * 256.0F / 360.0F) & 255;
					var18.serverConnection.sendPacket(Packet.PLAYER_TELEPORT, new Object[]{Integer.valueOf(-1), Integer.valueOf(var23), Integer.valueOf(var4), Integer.valueOf(var38), Integer.valueOf(var46), Integer.valueOf(var47)});
				}
			}
		}

		if(this.screen == null && this.player != null && this.player.health <= 0) {
			this.setScreen((Screen)null);
		}

		
		if(this.screen == null) {
			if(Mouse.isMouseGrabbed() || Mouse.isActuallyGrabbed()) {
				this.mouseGrabbed = true;
			}
			int var20;
			while(Mouse.next()) {
				var20 = Mouse.getEventDWheel();
				if(var20 != 0) {
					this.player.inventory.swapPaint(var20);
				}

				if(this.screen == null) {
					if(!this.mouseGrabbed && Mouse.getEventButtonState()) {
						this.grabMouse();
					} else {
						if(Mouse.getEventButton() == 0 && Mouse.getEventButtonState()) {
							this.clickMouse(0);
							this.oFrames = this.frames;
						}

						if(Mouse.getEventButton() == 1 && Mouse.getEventButtonState()) {
							this.clickMouse(1);
							this.oFrames = this.frames;
						}

						if(Mouse.getEventButton() == 2 && Mouse.getEventButtonState() && this.hitResult != null) {
							var19 = this.level.getTile(this.hitResult.x, this.hitResult.y, this.hitResult.z);
							if(var19 == Tile.grass.id) {
								var19 = Tile.dirt.id;
							}

							if(var19 == Tile.slabFull.id) {
								var19 = Tile.slabHalf.id;
							}

							if(var19 == Tile.unbreakable.id) {
								var19 = Tile.rock.id;
							}

							this.player.inventory.grabTexture(var19, this.gamemode instanceof CreativeGameMode);
						}
					}
				}

				if(this.screen != null) {
					this.screen.mouseEvent();
				}
			}

			if(this.clickCounter > 0) {
				--this.clickCounter;
			}

			label380:
			while(true) {
				do {
					do {
						if(!Keyboard.next()) {
							if(this.screen == null) {
								if(Mouse.isButtonDown(0) && (float)(this.frames - this.oFrames) >= this.timer.ticksPerSecond / 4.0F && this.mouseGrabbed) {
									this.clickMouse(0);
									this.oFrames = this.frames;
								}

								if(Mouse.isButtonDown(1) && (float)(this.frames - this.oFrames) >= this.timer.ticksPerSecond / 4.0F && this.mouseGrabbed) {
									this.clickMouse(1);
									this.oFrames = this.frames;
								}
							}

							boolean var25 = this.screen == null && Mouse.isButtonDown(0) && this.mouseGrabbed;
							boolean var39 = false;
							if(!this.gamemode.mode && this.clickCounter <= 0) {
								if(var25 && this.hitResult != null && this.hitResult.type == 0) {
									var4 = this.hitResult.x;
									var38 = this.hitResult.y;
									var46 = this.hitResult.z;
									this.gamemode.continueDestroyBlock(var4, var38, var46, this.hitResult.f);
								} else {
									this.gamemode.stopDestroyBlock();
								}
							}
							break label380;
						}

						this.player.setKey(Keyboard.getEventKey(), Keyboard.getEventKeyState());
					} while(!Keyboard.getEventKeyState());

					if(this.screen != null) {
						this.screen.keyboardEvent();
					}

					if(this.screen == null) {
						if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
							this.pauseScreen();
						}

						if(this.gamemode instanceof CreativeGameMode) {
							if(Keyboard.getEventKey() == this.options.load.key) {
								this.player.resetPos();
							}

							if(Keyboard.getEventKey() == this.options.save.key) {
								this.level.setSpawnPos((int)this.player.x, (int)this.player.y, (int)this.player.z, this.player.yRot);
								this.player.resetPos();
							}
						}

						Keyboard.getEventKey();
						if(Keyboard.getEventKey() == Keyboard.KEY_F5) {
							this.raining = !this.raining;
						}

						if(Keyboard.getEventKey() == Keyboard.KEY_TAB && this.gamemode instanceof SurvivalGameMode && this.player.arrows > 0) {
							this.level.addEntity(new Arrow(this.level, this.player, this.player.x, this.player.y, this.player.z, this.player.yRot, this.player.xRot, 1.2F));
							--this.player.arrows;
						}

						if(Keyboard.getEventKey() == this.options.build.key) {
							this.gamemode.handleOpenInventory();
						}

						if(Keyboard.getEventKey() == this.options.chat.key && this.networkClient != null && this.networkClient.isConnected()) {
							this.player.releaseAllKeys();
							this.setScreen(new ChatScreen());
						}
					}

					for(var20 = 0; var20 < 9; ++var20) {
						if(Keyboard.getEventKey() == var20 + 2) {
							this.player.inventory.selected = var20;
						}
					}
				} while(Keyboard.getEventKey() != this.options.toggleFog.key);

				this.options.setOption(4, !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) ? 1 : -1);
			}
		}

		if(this.screen != null) {
			this.oFrames = this.frames + 10000;
		}

		if(this.screen != null) {
			this.screen.updateEvents();
			if(this.screen != null) {
				this.screen.tick();
			}
		}

		if(this.level != null) {
			GameRenderer var24 = this.gameRenderer;
			++var24.rainTicks;
			TileRenderer var41 = var24.tileRenderer;
			var41.oProgress = var41.progress;
			if(var41.move) {
				++var41.rot;
				if(var41.rot == 7) {
					var41.rot = 0;
					var41.move = false;
				}
			}

			Player var28 = var41.minecraft.player;
			var4 = var28.inventory.getSelected();
			Tile var42 = null;
			if(var4 > 0) {
				var42 = Tile.tiles[var4];
			}

			float var50 = 0.4F;
			float var49 = var42 == var41.tile ? 1.0F : 0.0F;
			float var54 = var49 - var41.progress;
			if(var54 < -var50) {
				var54 = -var50;
			}

			if(var54 > var50) {
				var54 = var50;
			}

			var41.progress += var54;
			if(var41.progress < 0.1F) {
				var41.tile = var42;
			}

			if(var24.minecraft.raining) {
				GameRenderer var44 = var24;
				var28 = var24.minecraft.player;
				Level var32 = var24.minecraft.level;
				var38 = (int)var28.x;
				var46 = (int)var28.y;
				var47 = (int)var28.z;

				for(var8 = 0; var8 < 50; ++var8) {
					int var55 = var38 + var44.random.nextInt(9) - 4;
					int var56 = var47 + var44.random.nextInt(9) - 4;
					int var61 = var32.getHighestTile(var55, var56);
					if(var61 <= var46 + 4 && var61 >= var46 - 4) {
						float var62 = var44.random.nextFloat();
						float var64 = var44.random.nextFloat();
						var44.minecraft.particleEngine.addParticle(new WaterDropParticle(var32, (float)var55 + var62, (float)var61 + 0.1F, (float)var56 + var64));
					}
				}
			}

			if(this.networkClient != null) {
				this.networkClient.tick();
			}
			LevelRenderer var26 = this.levelRenderer;
			++var26.cloudTickCounter;
			this.level.tickEntities();
			if(!this.isOnlineClient()) {
				this.level.tick();
			}

			this.particleEngine.tick();
			if(this.networkClient == null) {
				levelSave();
			}
		}

	}

	public final boolean isOnlineClient() {
		return this.networkClient != null;
	}

	public final void generateLevel(int var1) {
		String var2 = this.user != null ? this.user.name : "anonymous";
		LevelGen var3 = new LevelGen(this.loadingScreen);
		Level var4 = var3.generateLevel(var2, 128 << var1, 128 << var1, 64);
		this.gamemode.createPlayer(var4);
		this.loadLegacy(var4);
	}

	public final void loadLegacy(Level var1) {
//		if(!this.applet.getDocumentBase().getHost().toLowerCase().endsWith("minecraft.net")) {
//			var1 = null;
//		}

		this.level = var1;
		if(var1 != null) {
			var1.initTransient();
			this.gamemode.initLevel(var1);
			var1.font = this.font;
			var1.rendererContext = this;
			if(!this.isOnlineClient()) {
				this.player = (Player)var1.findSubclassOf(Player.class);
			} else if(this.player != null) {
				this.player.resetPos();
				this.gamemode.initPlayer(this.player);
				if(var1 != null) {
					var1.player = this.player;
					var1.addEntity(this.player);
				}
			}
		}
		this.player = (Player) var1.getPlayer();
		if(this.player == null) {
			this.player = new Player(var1);
			this.player.resetPos();
			this.gamemode.initPlayer(this.player);
			if(var1 != null) {
				var1.player = this.player;
			}
		}

		if(this.player != null) {
			this.player.input = new KeyboardInput(this.options);
			this.gamemode.adjustPlayer(this.player);
		}

		if(this.levelRenderer != null) {
			LevelRenderer var2 = this.levelRenderer;
			if(var2.level != null) {
				var2.level.removeListener(var2);
			}

			var2.level = var1;
			if(var1 != null) {
				var1.addListener(var2);
				var2.compileSurroundingGround();
			}
		}

		if(this.particleEngine != null) {
			ParticleEngine var5 = this.particleEngine;
			if(var1 != null) {
				var1.particleEngine = var5;
			}

			for(int var4 = 0; var4 < 2; ++var4) {
				var5.particles[var4].clear();
			}
		}
		System.gc();
	}

	static enum OS {
		linux,
		solaris,
		windows,
		macos,
		unknown;
	}
}
