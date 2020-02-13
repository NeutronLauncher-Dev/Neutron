package com.mjtg.neutron.runtime.loader.protocol.format;

import org.json.JSONException;
import org.json.JSONObject;

public class FetchRuntimePacket extends Packet {

    public static final String TYPE = "fetchRuntime";


    @Override
    public String getType() {
        return TYPE;
    }

    public JSONObject toJson() {
        try {
            return new JSONObject()
                    .put("type", TYPE)
                    ;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static FetchRuntimePacket fromJson(JSONObject obj) {
        try {
            if(obj.getString("type").equals(TYPE)) {
                return new FetchRuntimePacket();
            } else {
                throw new IllegalArgumentException("incorrect type");
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
