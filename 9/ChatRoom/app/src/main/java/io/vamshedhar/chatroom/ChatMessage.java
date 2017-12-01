package io.vamshedhar.chatroom;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;


/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 11/7/17 1:07 AM.
 * vchinta1@uncc.edu
 */

public class ChatMessage implements Serializable, Comparable<ChatMessage> {
    String user_fname, user_lname, user_id, id, message, created_at;

    public String getUserFullName() {
        return user_fname + " " + user_lname;
    }

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public static ArrayList<ChatMessage> parseMessagesJson(String data) throws JSONException {
        ArrayList<ChatMessage> messages = new ArrayList<>();

        JSONObject root = new JSONObject(data);
        JSONArray messagesData = root.getJSONArray("messages");

        for (int i = 0; i < messagesData.length(); i++){
            JSONObject messageData = messagesData.getJSONObject(i);

            ChatMessage message = new ChatMessage();
            message.setId(messageData.getString("id"));
            message.setUser_fname(messageData.getString("user_fname"));
            message.setUser_lname(messageData.getString("user_lname"));
            message.setUser_id(messageData.getString("user_id"));
            message.setMessage(messageData.getString("message"));
            message.setCreated_at(messageData.getString("created_at"));

            messages.add(message);
        }
        return messages;
    }

    @Override
    public int compareTo(@NonNull ChatMessage chatMessage) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        try {
            Date thisDate = dateFormat.parse(this.getCreated_at());
            Date thatDate = dateFormat.parse(chatMessage.getCreated_at());
            return thisDate.compareTo(thatDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return -1 * this.getCreated_at().compareTo(chatMessage.getCreated_at());
    }
}
