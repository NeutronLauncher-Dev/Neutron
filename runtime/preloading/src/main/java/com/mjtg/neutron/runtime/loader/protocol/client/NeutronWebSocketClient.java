package com.mjtg.neutron.runtime.loader.protocol.client;


import android.util.Base64;
import android.util.Log;

import com.mjtg.neutron.runtime.loader.protocol.format.Packet;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class NeutronWebSocketClient extends WebSocketClient {

    private static final String TAG = "Neutron-Client";
    
    private boolean inProgress;

    private Consumer<String> stringMessageConsumer;


    public NeutronWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d(TAG, "connected to server: "+ getRemoteSocketAddress().toString());
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, "onMessage: received text message from server: "+message);
        if(stringMessageConsumer!=null) {
            stringMessageConsumer.accept(message);
        }
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        Log.d(TAG, "onMessage: received byte message from server: "+ Base64.encodeToString(bytes.array(), Base64.DEFAULT));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "connection closed");
    }

    @Override
    public void onError(Exception ex) {
        Log.e(TAG, "error while communicating with server", ex);
    }

    public Future<Packet> sendMessage(Packet request) {
        if(inProgress) {
            throw new IllegalStateException("the client does not support concurrent usage!");
        }

        final CompletableFuture<Packet> fut = new CompletableFuture<>();

        //handle response
        inProgress = true;
        stringMessageConsumer = new Consumer<String>() {
            @Override
            public void accept(String str) {
                inProgress = false;
                stringMessageConsumer = null;
                try {
                    final Packet packet = Packet.fromJson(new JSONObject(str));
                    fut.complete(packet);
                } catch (Exception e) {
                    fut.completeExceptionally(e);
                    NeutronWebSocketClient.this.close();
                }
            }
        };

        //send request
        send(request.toJson().toString());

        return fut;
    }

}
