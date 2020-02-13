package com.mjtg.neutron.runtime.loader.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Packet {

    public abstract String getType();

    public abstract JSONObject toJson();

    public static Packet fromJson(JSONObject obj) {
        try {
            switch(obj.getString("type")) {
                case FetchModsPacket.TYPE:
                    return FetchModsPacket.fromJson(obj);
                case FetchModsResponsePacket.TYPE:
                    return FetchModsResponsePacket.fromJson(obj);
                case FetchRuntimePacket.TYPE:
                    return FetchRuntimePacket.fromJson(obj);
                case FetchRuntimeResponsePacket.TYPE:
                    return FetchRuntimeResponsePacket.fromJson(obj);
                default:
                    throw new IllegalArgumentException("invalid type!");
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
