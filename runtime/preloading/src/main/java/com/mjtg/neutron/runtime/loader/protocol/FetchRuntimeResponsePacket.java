package com.mjtg.neutron.runtime.loader.protocol;

import android.util.Base64;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FetchRuntimeResponsePacket extends Packet {

    public static final String TYPE = "fetchRuntimeResponse";

    //map of libname-binary value
    public Map<String, byte[]> nativeLibraries = new HashMap<>();

    //map of dexname-binary value
    public Map<String, byte[]> runtimeDexes = new HashMap<>();


    @Override
    public String getType() {
        return TYPE;
    }

    public JSONObject toJson() {
        try {
            return new JSONObject()
                    .put("type", TYPE)
                    .put("nativeLibs", JSONUtils.encodeAsBase64Object(nativeLibraries))
                    .put("dexes", JSONUtils.encodeAsBase64Object(runtimeDexes))
                    ;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static FetchRuntimeResponsePacket fromJson(JSONObject obj) {
        try {
            if(obj.getString("type").equals(TYPE)) {
                final FetchRuntimeResponsePacket result = new FetchRuntimeResponsePacket();
                result.nativeLibraries = JSONUtils.decodeAsBase64Object(obj.getJSONObject("nativeLibs"));
                result.runtimeDexes = JSONUtils.decodeAsBase64Object(obj.getJSONObject("dexes"));
                return result;
            } else {
                throw new IllegalArgumentException("incorrect type");
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
