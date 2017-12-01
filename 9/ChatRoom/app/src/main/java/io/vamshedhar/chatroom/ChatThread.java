package io.vamshedhar.chatroom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 11/6/17 8:53 PM.
 * vchinta1@uncc.edu
 */

public class ChatThread implements Serializable {
    String user_fname, user_lname, user_id, id, title, created;

    public String getUser_fname() {
        return user_fname;
    }

    public void setUser_fname(String user_fname) {
        this.user_fname = user_fname;
    }

    public String getUser_lname() {
        return user_lname;
    }

    public void setUser_lname(String user_lname) {
        this.user_lname = user_lname;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public static ArrayList<ChatThread> parseThreadsJson(String data) throws JSONException {
        ArrayList<ChatThread> threads = new ArrayList<>();

        JSONObject root = new JSONObject(data);
        JSONArray threadsData = root.getJSONArray("threads");

        for (int i = 0; i < threadsData.length(); i++){
            JSONObject threadData = threadsData.getJSONObject(i);

            ChatThread thread = new ChatThread();
            thread.setId(threadData.getString("id"));
            thread.setUser_fname(threadData.getString("user_fname"));
            thread.setUser_lname(threadData.getString("user_lname"));
            thread.setUser_id(threadData.getString("user_id"));
            thread.setTitle(threadData.getString("title"));
            thread.setCreated(threadData.getString("created_at"));

            threads.add(thread);
        }
        return threads;
    }
}
