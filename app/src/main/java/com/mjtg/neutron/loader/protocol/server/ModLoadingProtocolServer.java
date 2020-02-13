package com.mjtg.neutron.loader.protocol.server;

import android.util.Log;

import com.google.common.io.ByteStreams;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModLoadingProtocolServer extends WebSocketServer {

    //a function that returns a list of mods to load
    private Supplier<List<String>> fetchModList;
    //a function that turns a mod name into a path to it's jar, return null if the mod not exists
    private Function<String, InputStream> fetchMod;

    public ModLoadingProtocolServer(Supplier<List<String>> fetchModList, Function<String, InputStream> fetchMod) {
        super( new InetSocketAddress( 32770 ) );
        this.fetchModList = fetchModList;
        this.fetchMod = fetchMod;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {}

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {}

    @Override
    public void onMessage( WebSocket conn, String message ) {
        Log.d("Neutron-ModServer", "onMessage: received message from client: "+message);
        try {
            final JSONObject json = new JSONObject(message);
            switch(json.getString("type")) {
                case "listMods":
                    handleListMods(conn, json);
                    break;
                case "fetchMod":
                    handleFetchMod(conn, json);
                    break;
            }
        } catch (JSONException e) {
            Log.w("Neutron-ModServer", "onMessage: received invalid message from client: "+message, e);
            conn.close();
        }
    }

    private void handleFetchMod(WebSocket conn, JSONObject json) throws JSONException {
        final String name = json.getString("modName");
        try(InputStream path = fetchMod.apply(name)) {
            final byte[] read = ByteStreams.toByteArray(path);
            conn.send(read);
        } catch (IOException e) {
            Log.w("Neutron-ModServer", "onMessage: unable to read name: "+name, e);
            conn.close();
        }
    }

    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        Log.w("Neutron-ModServer", "onMessage: received invalid message from client: "+message);
        conn.close();
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        Log.w("Neutron-ModServer", "onError: error trying to start Mod Server!", ex);
        if( conn != null ) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    @Override
    public void onStart() {
        Log.d("Neutron-ModServer", "server is now up and running!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    private void handleListMods(WebSocket conn, JSONObject json) {
        conn.send(new JSONArray(fetchModList.get()).toString());
    }

}
