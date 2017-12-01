package io.vamshedhar.chatroom;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ChatRoomActivity extends AppCompatActivity implements MessageListAdapter.MessageListInterface {

    ImageView homeBtn, sendBtn;
    EditText newMessage;
    TextView chatroomTitle;

    ChatThread thread;
    ArrayList<ChatMessage> messages;

    User currentUser;

    private RecyclerView messageList;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public void loadData(){
        adapter = new MessageListAdapter(messages, this, currentUser, this);
        messageList.setAdapter(adapter);
    }

    public void getDataFromAPI(){
        String threadId = thread.getId();
        final OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(MainActivity.BASE_URL + "api/messages/" + threadId)
                .header("Authorization", "BEARER " + currentUser.getToken())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d(MainActivity.TAG, "error");
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                Log.d(MainActivity.TAG, "Response received");
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        String error = responseBody.string();
                        JSONObject root = new JSONObject(error);
                        final String message = root.getString("message");
                        Log.d(MainActivity.TAG, message);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else{
                        messages = ChatMessage.parseMessagesJson(responseBody.string());
                        Collections.sort(messages);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadData();
                            }
                        });
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });

    }

    public void sendMessage(String message){
        final OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("message", message)
                .add("thread_id", thread.getId())
                .build();

        Request request = new Request.Builder()
                .url(MainActivity.BASE_URL + "api/message/add")
                .header("Authorization", "BEARER " + currentUser.getToken())
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d(MainActivity.TAG, "error");
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                Log.d(MainActivity.TAG, "Response received");
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        String error = responseBody.string();
                        JSONObject root = new JSONObject(error);
                        final String message = root.getString("message");
                        Log.d(MainActivity.TAG, message);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                newMessage.setText("");
                                getDataFromAPI();
                            }
                        });
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    @Override
    public void onDeleteMessageClicked(String id) {
        final OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(MainActivity.BASE_URL + "api/message/delete/" + id)
                .header("Authorization", "BEARER " + currentUser.getToken())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d(MainActivity.TAG, "error");
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                Log.d(MainActivity.TAG, "Response received");
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        String error = responseBody.string();
                        JSONObject root = new JSONObject(error);
                        final String message = root.getString("message");
                        Log.d(MainActivity.TAG, message);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getDataFromAPI();
                            }
                        });
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        chatroomTitle = (TextView) findViewById(R.id.chatRoomName);
        newMessage = (EditText) findViewById(R.id.newMessage);

        homeBtn = (ImageView) findViewById(R.id.homeBtn);
        sendBtn = (ImageView) findViewById(R.id.sendMessage);

        messageList = (RecyclerView) findViewById(R.id.messagesList);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        messageList.setLayoutManager(mLayoutManager);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(MainActivity.TAG, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString(MainActivity.CURRENT_USER, null);
        currentUser = gson.fromJson(json, User.class);

        Log.d(MainActivity.TAG + "D", currentUser.getUser_id());

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = newMessage.getText().toString().trim();
                if (message.equals("")){
                    Toast.makeText(ChatRoomActivity.this, "Please enter a message to send!", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(message);
                }
            }
        });

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(ThreadsActivity.THREAD)){
            thread = (ChatThread) getIntent().getSerializableExtra(ThreadsActivity.THREAD);

            chatroomTitle.setText(thread.getTitle());

            getDataFromAPI();


        } else {
            Toast.makeText(this, "Invalid Thread ID!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


}
