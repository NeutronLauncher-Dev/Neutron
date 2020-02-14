package com.mjtg.neutron.loader.protocol.server;

import android.util.Log;

import com.google.common.io.Files;
import com.mjtg.neutron.loader.protocol.format.FetchModsPacket;
import com.mjtg.neutron.loader.protocol.format.FetchModsResponsePacket;
import com.mjtg.neutron.loader.protocol.format.FetchRuntimePacket;
import com.mjtg.neutron.loader.protocol.format.FetchRuntimeResponsePacket;
import com.mjtg.neutron.loader.protocol.format.Packet;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Map;

public class LoadingWebsocketProtocolServer extends WebSocketServer {

    private static final String TAG = "Neutron-ModServer";

    private ProtocolRequestHandler handler;


    public LoadingWebsocketProtocolServer(int port, ProtocolRequestHandler handler) {
        super( new InetSocketAddress( port ) );
        this.handler = handler;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.d(TAG, "onOpen: received connection from client!");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Log.d(TAG, "onClose: client disconnected!");
    }

    @Override
    public void onMessage( WebSocket conn, String message ) {
        Log.d(TAG, "onMessage: received message from client: "+message);
        try {
            final Packet packet = Packet.fromJson(new JSONObject(message));
            switch (packet.getType()) {
                case FetchModsPacket.TYPE:
                    handleFetchMod(conn, (FetchModsPacket) packet);
                    break;
                case FetchRuntimePacket.TYPE:
                    handleFetchRuntime(conn, (FetchRuntimePacket) packet);
                    break;
                default:
                    throw new IllegalArgumentException("invalid type:"+ packet.getType());
            }
        } catch (IllegalArgumentException | JSONException e) {
            Log.w(TAG, "onMessage: received invalid message from client: "+message, e);
            conn.close();
        }
    }

    private void handleFetchRuntime(WebSocket conn, FetchRuntimePacket packet) {
        final ProtocolRequestHandler.Runtime rt = handler.getRuntime();
        final FetchRuntimeResponsePacket response = new FetchRuntimeResponsePacket();

        try {
            for (Map.Entry<String, File> entry : rt.nativeLibs.entrySet()) {
                response.nativeLibraries.put(entry.getKey(), Files.toByteArray(entry.getValue()));
            }
            for (Map.Entry<String, File> entry : rt.jars.entrySet()) {
                response.runtimeJars.put(entry.getKey(), Files.toByteArray(entry.getValue()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        conn.send(response.toJson().toString());
    }

    private void handleFetchMod(WebSocket conn, FetchModsPacket packet) {
        final Map<String, File> mods = handler.getMods();
        final FetchModsResponsePacket response = new FetchModsResponsePacket();

        try {
            for (Map.Entry<String, File> entry : mods.entrySet()) {
                response.mods.put(entry.getKey(), Files.toByteArray(entry.getValue()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        conn.send(response.toJson().toString());
    }

    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        Log.w(TAG, "onMessage: received invalid message from client: "+message);
        conn.close();
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        Log.w(TAG, "onError: error with connection!", ex);
        if( conn != null ) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    @Override
    public void onStart() {
        Log.d(TAG, "server is now up and running!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

}
