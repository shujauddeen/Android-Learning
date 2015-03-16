package com.komli.adcatalog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 3/3/15.
 */
public class ProductStore {

    private static ProductStore obj = null;
    HashMap<String,HashMap<String,String>> data;

    private ProductStore() {
        data = new HashMap<String,HashMap<String,String>>();
    }

    public static ProductStore getInstance(){
            if(obj == null)
                obj = new ProductStore();
        return obj;
    }

    public void set(String key, JSONObject v){
        try{
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id",v.getString("id"));
            map.put("availability",v.getString("availability"));
            map.put("availability",v.getString("availability"));
            map.put("condition",v.getString("condition"));
            map.put("description",v.getString("description"));
            map.put("image_link",v.getString("image_link"));
            map.put("link",v.getString("link"));
            map.put("title",v.getString("title"));
            map.put("price",v.getString("price"));
            map.put("brand",v.getString("brand"));
            data.put(key,map);
        }catch (Exception e){

        }
    }

    public Map<String,String> get(String key){

        return data.get(key);
    }
}
