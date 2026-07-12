package com.mojang.minecraft.net;

import com.mojang.comm.SocketConnection;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gui.ErrorScreen;

import net.lax1dude.eaglercraft.internal.EnumEaglerConnectionState;
import net.lax1dude.eaglercraft.internal.PlatformNetworking;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public final class Client {
	public ByteArrayOutputStream levelBuffer;
	public SocketConnection serverConnection;
	public Minecraft minecraft;
	public boolean processData = false;
	public HashMap players = new HashMap();
	private boolean loginSent = false;

	public Client(Minecraft var1, String var2, String var4) throws IOException{
		this.serverConnection = new SocketConnection(this);
		this.serverConnection.client = this;
		this.processData = true;
		this.minecraft = var1;
		this.serverConnection.webSocket = PlatformNetworking.openWebSocket(var2);

		if (this.serverConnection.webSocket == null) {
			this.minecraft.setScreen(new ErrorScreen("Failed to connect", "You failed to connect to the server. It\'s probably down!"));
			throw new IOException("Failed to open websocket to: " + var2);
		}
	}

	public final void sendTileUpdated(int var1, int var2, int var3, int var4, int var5) {
		this.serverConnection.sendPacket(Packet.PLACE_OR_REMOVE_TILE, new Object[]{Integer.valueOf(var1), Integer.valueOf(var2), Integer.valueOf(var3), Integer.valueOf(var4), Integer.valueOf(var5)});
	}

	public final void handleException(Exception var1) {
		this.serverConnection.disconnect();
		this.minecraft.setScreen(new ErrorScreen("Disconnected!", var1.getMessage()));
		var1.printStackTrace();
	}

	public final boolean isConnected() {
		if(this.serverConnection != null) {
			SocketConnection var1 = this.serverConnection;
			if(var1.webSocket != null) {
				if(var1.webSocket.getState() == EnumEaglerConnectionState.CONNECTED) {
					return true;
				}
			}
		}
		
		return false;
	}

	public final List getUsernames() {
		ArrayList var1 = new ArrayList();
		var1.add(this.minecraft.user.name);
		Iterator var3 = this.players.values().iterator();

		while(var3.hasNext()) {
			NetworkPlayer var2 = (NetworkPlayer)var3.next();
			var1.add(var2.name);
		}

		return var1;
	}
	
    public void tick() {
        if (this.serverConnection.webSocket != null
                && this.serverConnection.webSocket.getState() == EnumEaglerConnectionState.CONNECTED
                && !loginSent) {
        	this.serverConnection.sendPacket(Packet.LOGIN, new Object[]{Byte.valueOf((byte)6), this.minecraft.user.name, "", Integer.valueOf(0)});
        	loginSent = true;
        }
    }
}
