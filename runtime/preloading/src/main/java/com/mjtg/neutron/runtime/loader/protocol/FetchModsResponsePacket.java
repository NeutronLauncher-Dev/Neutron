package com.mjtg.neutron.runtime.loader.protocol;

import android.util.Base64;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FetchModsResponsePacket extends Packet {

    public static final String TYPE = "fetchModsResponsePacket";

    public Map<String, byte[]> mods = new HashMap<>();


    @Override
    public String getType() {
        return TYPE;
    }

    public JSONObject toJson() {
        try {
            return new JSONObject()
                    .put("type", TYPE)
                    .put("mods", JSONUtils.encodeAsBase64Object(mods))
                    ;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static FetchModsResponsePacket fromJson(JSONObject obj) {
        try {
            if(obj.getString("type").equals(TYPE)) {
                final FetchModsResponsePacket result = new FetchModsResponsePacket();
                result.mods = JSONUtils.decodeAsBase64Object(obj.getJSONObject("mods"));
                return result;
            } else {
                throw new IllegalArgumentException("incorrect type");
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
