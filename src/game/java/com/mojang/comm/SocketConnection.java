package com.mojang.comm;

import com.mojang.minecraft.net.Client;
import com.mojang.minecraft.net.Packet;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import net.lax1dude.eaglercraft.internal.IWebSocketClient;
import net.lax1dude.eaglercraft.internal.IWebSocketFrame;

public final class SocketConnection {
	public ByteBuffer readBuffer = ByteBuffer.allocate(1048576);
	public ByteBuffer writeBuffer = ByteBuffer.allocate(1048576);
	public Client client;
	private byte[] stringPacket = new byte[64];
	public IWebSocketClient webSocket;

	public SocketConnection(Client client) throws IOException {
		this.client = client;
		this.readBuffer.clear();
		this.writeBuffer.clear();
	}

	public final void disconnect() {
		if (this.webSocket != null) {
			this.webSocket.close();
			this.webSocket = null;
		}
	}


	public final void sendPacket(Packet var1, Object... var2) {
		this.writeBuffer.put(var1.id);

		for(int var3 = 0; var3 < var2.length; ++var3) {
			Class var10001 = var1.fields[var3];
			Object var6 = var2[var3];
			Class var5 = var10001;
			SocketConnection var4 = this;
			try {
				if(var5 == Long.TYPE) {
					var4.writeBuffer.putLong(((Long)var6).longValue());
				} else if(var5 == Integer.TYPE) {
					var4.writeBuffer.putInt(((Number)var6).intValue());
				} else if(var5 == Short.TYPE) {
					var4.writeBuffer.putShort(((Number)var6).shortValue());
				} else if(var5 == Byte.TYPE) {
					var4.writeBuffer.put(((Number)var6).byteValue());
				} else if(var5 == Double.TYPE) {
					var4.writeBuffer.putDouble(((Double)var6).doubleValue());
				} else if(var5 == Float.TYPE) {
					var4.writeBuffer.putFloat(((Float)var6).floatValue());
				} else {
					byte[] var8;
					if(var5 != String.class) {
						if(var5 == byte[].class) {
							var8 = (byte[])((byte[])var6);
							if(var8.length < 1024) {
								var8 = Arrays.copyOf(var8, 1024);
							}

							var4.writeBuffer.put(var8);
						}
					} else {
						var8 = ((String)var6).getBytes("UTF-8");
						Arrays.fill(var4.stringPacket, (byte)32);

						int var9;
						for(var9 = 0; var9 < 64 && var9 < var8.length; ++var9) {
							var4.stringPacket[var9] = var8[var9];
						}

						for(var9 = var8.length; var9 < 64; ++var9) {
							var4.stringPacket[var9] = 32;
						}

						var4.writeBuffer.put(var4.stringPacket);
					}
				}
			} catch (Exception var7) {
				this.client.handleException(var7);
			}
		}
		flush();
	}

	public Object read(Class var1) {
		try {
			if(var1 == Long.TYPE) {
				return Long.valueOf(this.readBuffer.getLong());
			} else if(var1 == Integer.TYPE) {
				return Integer.valueOf(this.readBuffer.getInt());
			} else if(var1 == Short.TYPE) {
				return Short.valueOf(this.readBuffer.getShort());
			} else if(var1 == Byte.TYPE) {
				return Byte.valueOf(this.readBuffer.get());
			} else if(var1 == Double.TYPE) {
				return Double.valueOf(this.readBuffer.getDouble());
			} else if(var1 == Float.TYPE) {
				return Float.valueOf(this.readBuffer.getFloat());
			} else if(var1 == String.class) {
				this.readBuffer.get(this.stringPacket);
				return (new String(this.stringPacket, "UTF-8")).trim();
			} else if(var1 == byte[].class) {
				byte[] var3 = new byte[1024];
				this.readBuffer.get(var3);
				return var3;
			} else {
				return null;
			}
		} catch (Exception var2) {
			this.client.handleException(var2);
			return null;
		}
	}

	public void flush() {
		if (webSocket == null || !webSocket.isOpen()) {
			return;
		}
		int len = writeBuffer.position();
		if (len > 0) {
			writeBuffer.flip();
			byte[] data = new byte[len];
			writeBuffer.get(data);
			this.webSocket.send(data);
			writeBuffer.clear();
		}
	}
}
