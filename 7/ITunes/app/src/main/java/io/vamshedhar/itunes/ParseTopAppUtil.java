package io.vamshedhar.itunes;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 9/27/17 11:19 PM.
 * vchinta1@uncc.edu
 */

public class ParseTopAppUtil {

    public static ArrayList<TopApp> ParseTopApps(String data) throws JSONException, InterruptedException {
        ArrayList<TopApp> tracksApps = new ArrayList<>();



        JSONObject root = new JSONObject(data);

        JSONObject feed = root.getJSONObject("feed");
        JSONArray entries = feed.getJSONArray("entry");

        for (int i = 0; i < entries.length(); i++) {
            JSONObject topApp = entries.getJSONObject(i);

            JSONObject nameObject = topApp.getJSONObject("im:name");
            String name = nameObject.getString("label");

            JSONArray imageArray = topApp.getJSONArray("im:image");
            JSONObject imageObject = imageArray.getJSONObject(imageArray.length() - 1);
            String image = imageObject.getString("label");

            JSONObject thumbObject = imageArray.getJSONObject(0);
            String thumb = imageObject.getString("label");

            JSONObject priceObject = topApp.getJSONObject("im:price");
            JSONObject priceAttr = priceObject.getJSONObject("attributes");
            String price = priceAttr.getString("amount");

            JSONObject idObject = topApp.getJSONObject("id");
            JSONObject idAttr = idObject.getJSONObject("attributes");
            String id = idAttr.getString("im:id");

            TopApp TopAppObject = new TopApp(id, name, price, image, thumb);
            tracksApps.add(TopAppObject);

        }

        return tracksApps;
    }
}
