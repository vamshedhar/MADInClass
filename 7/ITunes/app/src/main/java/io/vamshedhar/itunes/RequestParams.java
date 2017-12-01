package io.vamshedhar.itunes;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by Vamshedhar Reddy on 27-09-2017.
 */

public class RequestParams {
    String method, baseURL;
    HashMap<String,String> params = new HashMap<String, String>();

    public RequestParams(String method, String baseURL) {
        this.method = method;
        this.baseURL = baseURL;
    }

    public void addParam(String key, String value) {
        params.put(key, value);
    }

    public String getEncodedParams() throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();

        for(String key: params.keySet())
        {
            String value = URLEncoder.encode(params.get(key),"UTF-8");
            if(sb.length() > 0)
                sb.append('&');
            sb.append(key+"="+value);

        }
        return sb.toString();
    }

    public String getEncodedUrl() throws UnsupportedEncodingException {

        return this.baseURL+"?"+getEncodedParams();
    }

    public HttpURLConnection setupConnection() throws IOException
    {
        Log.d("demo","ins setupConnection");
        HttpURLConnection con = null;
        if(method.equals("GET")) {
            Log.d(MainActivity.TAG, getEncodedUrl().toString());
            URL url = new URL(getEncodedUrl());
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            return con;
        } else {
            Log.d(MainActivity.TAG, getEncodedUrl().toString());
            URL url = new URL(this.baseURL);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(getEncodedParams());
            writer.flush();
            return con;
        }
    }

}
