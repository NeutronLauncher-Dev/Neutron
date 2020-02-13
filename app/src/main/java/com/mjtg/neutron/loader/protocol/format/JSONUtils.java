package com.mjtg.neutron.loader.protocol.format;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JSONUtils {

    public static JSONObject encodeAsBase64Object(Map<String, byte[]> map) throws JSONException {
        JSONObject object = new JSONObject();
        for (Map.Entry<String, byte[]> entry : map.entrySet()) {
            object.put(
                    entry.getKey(),
                    Base64.encodeToString(entry.getValue(), Base64.DEFAULT)
            );
        }
        return object;
    }

    public static Map<String, byte[]> decodeAsBase64Object(JSONObject obj) throws JSONException {
        Map<String, byte[]> map = new HashMap<>();
        final Iterator<String> iter = obj.keys();
        while (iter.hasNext()) {
            final String key = iter.next();
            map.put(
                    key,
                    Base64.decode(obj.getString(key), Base64.DEFAULT)
            );
        }
        return map;
    }

}
