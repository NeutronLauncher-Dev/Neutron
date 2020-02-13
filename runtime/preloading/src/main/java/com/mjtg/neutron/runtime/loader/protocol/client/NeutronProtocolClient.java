package com.mjtg.neutron.runtime.loader.protocol.client;

import com.mjtg.neutron.runtime.loader.protocol.format.Packet;
import com.mjtg.neutron.runtime.loader.protocol.ProtocolException;
import com.mjtg.neutron.runtime.loader.protocol.format.FetchModsPacket;
import com.mjtg.neutron.runtime.loader.protocol.format.FetchModsResponsePacket;
import com.mjtg.neutron.runtime.loader.protocol.format.FetchRuntimePacket;
import com.mjtg.neutron.runtime.loader.protocol.format.FetchRuntimeResponsePacket;

import org.apache.commons.io.FileUtils;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NeutronProtocolClient {

    private static final String SERVER_URL = "ws://localhost:32770";

    public void downloadRuntime(File runtimeDexDir, File runtimeNativeLibDir) {
        NeutronWebSocketClient client = null;
        try {
            client = new NeutronWebSocketClient(new URI(SERVER_URL));
            if(!client.connectBlocking()) {
                throw new WebsocketNotConnectedException();
            }

            //fetch response
            final Future<Packet> respFut = client.sendMessage(new FetchRuntimePacket());
            FetchRuntimeResponsePacket resp;
            try {
                resp = (FetchRuntimeResponsePacket) respFut.get(10, TimeUnit.SECONDS);
            } catch (ClassCastException e) {
                throw new ProtocolException("invalid response type in fetchRuntime: "+respFut.get().getType());
            }

            //prepare directories
            mkdirsIfNotExists(runtimeDexDir);
            mkdirsIfNotExists(runtimeNativeLibDir);

            //extract response
            try {
            for (Map.Entry<String, byte[]> entry : resp.runtimeDexes.entrySet()) {
                File f = new File(runtimeDexDir.toString()+File.separator+entry.getKey());
                    FileUtils.writeByteArrayToFile(f, entry.getValue());
            }
            } catch (IOException e) {
                throw new RuntimeException("error writing dexes", e);
            }

            try {
                for (Map.Entry<String, byte[]> entry : resp.nativeLibraries.entrySet()) {
                    File f = new File(runtimeNativeLibDir.toString()+File.separator+entry.getKey());
                    FileUtils.writeByteArrayToFile(f, entry.getValue());
                }
            } catch (IOException e) {
                throw new RuntimeException("error writing native libraries", e);
            }

        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new ProtocolException("error in waiting response", e);
        } finally {
            if(client!=null) {
                client.close();
            }
        }
    }

    public void downloadMods(File modsDir) {
        NeutronWebSocketClient client = null;
        try {
            client = new NeutronWebSocketClient(new URI(SERVER_URL));
            if(!client.connectBlocking()) {
                throw new WebsocketNotConnectedException();
            }

            //fetch response
            final Future<Packet> respFut = client.sendMessage(new FetchModsPacket());
            FetchModsResponsePacket resp;
            try {
                resp = (FetchModsResponsePacket) respFut.get(10, TimeUnit.SECONDS);
            } catch (ClassCastException e) {
                throw new ProtocolException("invalid response type in fetch mods: "+respFut.get().getType());
            }

            //prepare directories
            mkdirsIfNotExists(modsDir);

            //extract response
            try {
                for (Map.Entry<String, byte[]> entry : resp.mods.entrySet()) {
                    File f = new File(modsDir.toString()+File.separator+entry.getKey());
                    FileUtils.writeByteArrayToFile(f, entry.getValue());
                }
            } catch (IOException e) {
                throw new RuntimeException("error writing mods", e);
            }

        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new ProtocolException("error in waiting response", e);
        } finally {
            if(client!=null) {
                client.close();
            }
        }
    }

    private void mkdirsIfNotExists(File dir) {
        if(!dir.exists()) {
            if(!dir.mkdirs()) {
                throw new RuntimeException("unable to mkdir for runtimeDexDir: "+dir.toString());
            }
        }
    }

}
