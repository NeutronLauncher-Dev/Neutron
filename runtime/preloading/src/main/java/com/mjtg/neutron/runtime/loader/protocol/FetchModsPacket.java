package com.mjtg.neutron.runtime.loader.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class FetchModsPacket extends Packet {

    public static final String TYPE = "fetchMods";


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

    public static FetchModsPacket fromJson(JSONObject obj) {
        try {
            if(obj.getString("type").equals(TYPE)) {
                return new FetchModsPacket();
            } else {
                throw new IllegalArgumentException("incorrect type");
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
