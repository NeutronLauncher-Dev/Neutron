package com.mjtg.neutron.loader;

import android.util.Log;

import com.google.common.io.ByteStreams;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModLoadingProtocolServer extends WebSocketServer {

    //a function that returns a list of mods to load
    private Supplier<List<String>> fetchModList;
    //a function that turns a mod name into a path to it's jar, return null if the mod not exists
    private Function<String, Path> fetchMod;

    public ModLoadingProtocolServer(Supplier<List<String>> fetchModList, Function<String, Path> fetchMod) {
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
        try {
            final JSONObject json = new JSONObject(message);
            switch(json.getString("type")) {
                case "listMods":
                    handleListMods(conn, json);
                case "fetchMod":
                    handleFetchMod(conn, json);
            }
        } catch (JSONException e) {
            Log.w("Neutron-ModServer", "onMessage: received invalid message from client: "+message, e);
            conn.close();
        }
    }

    private void handleFetchMod(WebSocket conn, JSONObject json) throws JSONException {
        final String name = json.getString("modName");
        final Path path = fetchMod.apply(name);
        if(path == null) {
            Log.w("Neutron-ModServer", "onMessage: received invalid message from client: "+json.toString());
            conn.close();
        } else {
            try (InputStream is = new BufferedInputStream(new FileInputStream(path.toString()))){
                final byte[] read = ByteStreams.toByteArray(is);
                conn.send(read);
            } catch (IOException e) {
                Log.w("Neutron-ModServer", "onMessage: unable to read name: "+name, e);
                conn.close();
            }
        }
    }

    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        Log.w("Neutron-ModServer", "onMessage: received invalid message from client: "+message);
        conn.close();
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        if( conn != null ) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    @Override
    public void onStart() {
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    private void handleListMods(WebSocket conn, JSONObject json) {
        conn.send(new JSONArray(fetchModList.get()).toString());
    }

}
