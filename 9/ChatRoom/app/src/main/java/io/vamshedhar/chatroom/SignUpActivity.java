package io.vamshedhar.chatroom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SignUpActivity extends AppCompatActivity {

    EditText firstName, lastName, email, password, repeatPassword;
    Button cancelBtn, registerBtn;

    public boolean isValidData(){
        if (firstName.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter valid first name!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (lastName.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter valid last name!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!MainActivity.isValidEmail(email.getText().toString().trim())){
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.getText().toString().trim().isEmpty() || repeatPassword.getText().toString().trim().isEmpty() ){
            Toast.makeText(this, "Please enter valid password!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.getText().toString().trim().equals(repeatPassword.getText().toString().trim())){
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.choosePassword);
        repeatPassword = (EditText) findViewById(R.id.repeatPassword);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        registerBtn = (Button) findViewById(R.id.registerBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidData()){
                    String username = email.getText().toString();
                    String passwordValue = password.getText().toString();
                    String fname = firstName.getText().toString();
                    String lname = lastName.getText().toString();

                    final OkHttpClient client = new OkHttpClient();
                    final Moshi moshi = new Moshi.Builder().build();
                    final JsonAdapter<User> userJsonAdapter = moshi.adapter(User.class);


                    RequestBody formBody = new FormBody.Builder()
                            .add("email", username)
                            .add("password", passwordValue)
                            .add("fname", fname)
                            .add("lname", lname)
                            .build();

                    Request request = new Request.Builder()
                            .url(MainActivity.BASE_URL + "/api/signup")
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

                                } else {
                                    User user = userJsonAdapter.fromJson(responseBody.source());

                                    SharedPreferences pref = getApplicationContext().getSharedPreferences(MainActivity.TAG, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();

                                    Gson storeGson = new Gson();
                                    String storeJson = storeGson.toJson(user);
                                    editor.putString(MainActivity.CURRENT_USER, storeJson);
                                    editor.commit();

                                    Toast.makeText(SignUpActivity.this, "User created successfully!", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(SignUpActivity.this, ThreadsActivity.class);
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
