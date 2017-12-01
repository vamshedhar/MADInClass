package io.vamshedhar.chatroom;

import android.content.Intent;
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
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ThreadsActivity extends AppCompatActivity implements ThreadListAdapter.ThreadListInterface {

    public static final String THREAD = "THREAD";

    TextView userFullName;
    ImageView logout, addThread;

    EditText newThreadTitle;

    User currentUser;

    ArrayList<ChatThread> threads;

    private RecyclerView threadList;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public void loadData(){
        adapter = new ThreadListAdapter(threads, ThreadsActivity.this, currentUser, this);
        threadList.setAdapter(adapter);
    }

    public void getDataFromAPI(){
        final OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(MainActivity.BASE_URL + "api/thread")
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
                        threads = ChatThread.parseThreadsJson(responseBody.string());
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

    @Override
    public void onDeleteThreadClicked(String id) {
        final OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(MainActivity.BASE_URL + "api/thread/delete/" + id)
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
                                newThreadTitle.setText("");
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
    public void onThreadClicked(ChatThread thread) {
        Intent intent = new Intent(ThreadsActivity.this, ChatRoomActivity.class);
        intent.putExtra(THREAD, thread);
        startActivity(intent);
    }

    public void createThread(String name){
        final OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("title", name)
                .build();

        Request request = new Request.Builder()
                .url(MainActivity.BASE_URL + "api/thread/add")
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
                                newThreadTitle.setText("");
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
        setContentView(R.layout.activity_threads);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(MainActivity.CURRENT_USER)){
            currentUser = (User) getIntent().getSerializableExtra(MainActivity.CURRENT_USER);
        } else{
            SharedPreferences pref = getApplicationContext().getSharedPreferences(MainActivity.TAG, MODE_PRIVATE);
            Gson gson = new Gson();
            String json = pref.getString(MainActivity.CURRENT_USER, null);
            currentUser = gson.fromJson(json, User.class);
        }

        userFullName = (TextView) findViewById(R.id.userFullName);
        newThreadTitle = (EditText) findViewById(R.id.newThreadName);
        addThread = (ImageView) findViewById(R.id.addThread);
        logout = (ImageView) findViewById(R.id.logout);

        threadList = (RecyclerView) findViewById(R.id.threadsList);

        userFullName.setText(currentUser.getFullName());

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        threadList.setLayoutManager(mLayoutManager);

        getDataFromAPI();

        addThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String threadName = newThreadTitle.getText().toString().trim();

                if (threadName.equals("")){
                    Toast.makeText(ThreadsActivity.this, "Please enter a valid thread name!", Toast.LENGTH_SHORT).show();
                } else {
                    createThread(threadName);
                }
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences(MainActivity.TAG, MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(MainActivity.CURRENT_USER, null);
                editor.commit();

                Intent intent = new Intent(ThreadsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

}
