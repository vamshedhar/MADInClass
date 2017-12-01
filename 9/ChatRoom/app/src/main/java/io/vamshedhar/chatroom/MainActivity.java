package io.vamshedhar.chatroom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "ChatRoom";
    public static final String CURRENT_USER = "CURRENT_USER";
    public static final String BASE_URL = "http://ec2-54-164-74-55.compute-1.amazonaws.com/";

    EditText useremail, password;
    Button signUpbtn, loginBtn;

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public boolean isValidData(){
        if (!isValidEmail(useremail.getText().toString().trim())){
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter valid password!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        useremail = (EditText) findViewById(R.id.loginEmail);
        password = (EditText) findViewById(R.id.loginPassword);

        useremail.setText("vamshedhar@gmail.com");
        password.setText("qwerty");

        SharedPreferences pref = getApplicationContext().getSharedPreferences(MainActivity.TAG, MODE_PRIVATE);


        Gson gson = new Gson();
        String json = pref.getString(MainActivity.CURRENT_USER, null);
        User user = gson.fromJson(json, User.class);
        if (user != null){
            Intent intent = new Intent(MainActivity.this, ThreadsActivity.class);
            intent.putExtra(CURRENT_USER, user);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        signUpbtn = (Button) findViewById(R.id.signupBtn);
        loginBtn = (Button) findViewById(R.id.loginBrn);

        signUpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (isValidData()){
                String username = useremail.getText().toString();
                String passwordValue = password.getText().toString();

                final OkHttpClient client = new OkHttpClient();
                final Moshi moshi = new Moshi.Builder().build();
                final JsonAdapter<User> userJsonAdapter = moshi.adapter(User.class);

                RequestBody formBody = new FormBody.Builder()
                        .add("email", username)
                        .add("password", passwordValue)
                        .build();

                Request request = new Request.Builder()
                        .url(MainActivity.BASE_URL + "api/login")
                        .post(formBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        Log.d(MainActivity.TAG, "error");
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, Response response) throws IOException {
                        try (ResponseBody responseBody = response.body()) {
                            if (!response.isSuccessful()) {
                                String error = responseBody.string();
                                JSONObject root = new JSONObject(error);
                                final String message = root.getString("message");
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else{
                                User user = userJsonAdapter.fromJson(responseBody.source());

                                SharedPreferences pref = getApplicationContext().getSharedPreferences(MainActivity.TAG, MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                Gson storeGson = new Gson();
                                String storeJson = storeGson.toJson(user);
                                editor.putString(MainActivity.CURRENT_USER, storeJson);
                                editor.commit();

                                Intent intent = new Intent(MainActivity.this, ThreadsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });
            }
            }
        });

    }
}
