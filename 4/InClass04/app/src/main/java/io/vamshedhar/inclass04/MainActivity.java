package io.vamshedhar.inclass04;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *  MainActivity.java
 *  @author Vamshedhar Reddy Chintala
 *  @author Anjani Nalla
 */

public class MainActivity extends AppCompatActivity {

    EditText name, dept, age, zipcode;
    TextView selectedPassword;
    ProgressDialog pg;

    Handler handler;

    ArrayList<String> threadPasswords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (EditText) findViewById(R.id.nameET);
        dept = (EditText) findViewById(R.id.deptET);
        age = (EditText) findViewById(R.id.ageET);
        zipcode = (EditText) findViewById(R.id.zipET);
        selectedPassword = (TextView) findViewById(R.id.selectedPassword);

        threadPasswords = new ArrayList<String>();

        pg = new ProgressDialog(MainActivity.this);
        pg.setMessage("Generating Passwords");
        pg.setMax(100);
        pg.setCancelable(false);
        pg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        handler = new Handler(new Handler.Callback(){

            @Override
            public boolean handleMessage(Message message) {
                if(message.getData().containsKey("newPassword")){
                    String newPassword = message.getData().getString("newPassword");
                    threadPasswords.add(newPassword);
                    Log.d("Passwords", newPassword);

                    pg.setProgress(threadPasswords.size() * 20);

                    if (threadPasswords.size() == 5){
                        pg.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder
                                .setTitle("Choose a Password")
                                .setCancelable(false)
                                .setItems(threadPasswords.toArray(new String[threadPasswords.size()]), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        selectedPassword.setText(threadPasswords.get(i));
                                    }
                                });

                        AlertDialog passwordSelector = builder.create();

                        passwordSelector.show();
                    }
                }

                return true;
            }
        });
    }

    public void close(View view){
        finish();
    }

    public void clear(View view){
        name.setText("");
        dept.setText("");
        age.setText("");
        zipcode.setText("");

        selectedPassword.setText("");

        threadPasswords = new ArrayList<>();
    }

    public boolean validate(){

        String nameValue = name.getText().toString();
        String deptValue = dept.getText().toString();

        if (nameValue.trim().equals("")){
            Toast.makeText(MainActivity.this, "Please enter a Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if(nameValue.matches(".*\\d+.*")){
            Toast.makeText(MainActivity.this, "Name should not contain numbers", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(deptValue.trim().equals("")){
            Toast.makeText(MainActivity.this, "Please enter a Department Name", Toast.LENGTH_SHORT).show();
            return false;
        }

        try{
            int ageValue = Integer.parseInt(age.getText().toString());

            if(ageValue <= 0){
                Toast.makeText(MainActivity.this, "Age must be a positive Integer", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e){
            Toast.makeText(MainActivity.this, "Please enter a valid Age", Toast.LENGTH_SHORT).show();
            return false;
        }

        String zipcodeValue = zipcode.getText().toString();

        if(zipcodeValue.length() != 5){
            Toast.makeText(MainActivity.this, "Zip code must contain 5 digits", Toast.LENGTH_SHORT).show();
            return false;
        } else{

            try{
                Integer.parseInt(zipcodeValue);
            } catch (NumberFormatException e){
                Toast.makeText(MainActivity.this, "Please enter a valid Zip code", Toast.LENGTH_SHORT).show();
                return false;
            }

        }

        return true;
    }

    public class GenerateThreadPasswords implements Runnable{

        String name;
        String dept;
        int age;
        int zipcode;

        public GenerateThreadPasswords(String name, String dept, int age, int zipcode) {
            this.name = name;
            this.dept = dept;
            this.age = age;
            this.zipcode = zipcode;
        }

        public void sendMsg(String password) {
            Bundle bundle = new Bundle();
            bundle.putString("newPassword", password);
            Message message = new Message();
            message.setData(bundle);
            handler.sendMessage(message);
        }

        @Override
        public void run() {
            String pass = Util.getPassword(this.name, this.dept, this.age, this.zipcode);
            sendMsg(pass);
        }
    }

    public void onThreadClick(View view){
        if(!validate()){
            return;
        }

        threadPasswords = new ArrayList<>();
        pg.show();
        pg.setProgress(0);
        ExecutorService taskPool = Executors.newFixedThreadPool(2);

        int ageValue = Integer.parseInt(age.getText().toString());
        int zipValue = Integer.parseInt(zipcode.getText().toString());

        for (int i = 0; i < 5; i++){
            taskPool.execute(new GenerateThreadPasswords(name.getText().toString(), dept.getText().toString(), ageValue, zipValue));
        }
    }

    private class GenerateAsyncPasswords extends AsyncTask<String, Integer, String[]>{
        @Override
        protected String[] doInBackground(String... params) {
            String nameValue = params[0];
            String deptValue = params[1];
            int ageValue = Integer.parseInt(params[2]);
            int zipValue = Integer.parseInt(params[3]);

            String[] passwords = new String[5];


            for (int i = 0; i < 5; i++){
                String pass = Util.getPassword(nameValue, deptValue, ageValue, zipValue);
                passwords[i] = pass;
                publishProgress(i + 1);
            }

            return passwords;

        }

        @Override
        protected void onPreExecute() {
            pg.show();
            pg.setProgress(0);
        }

        @Override
        protected void onPostExecute(final String[] passwords) {
            Log.d("Passwords", passwords.toString());
            pg.dismiss();

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder
                    .setTitle("Choose a Password")
                    .setCancelable(false)
                    .setItems(passwords, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            selectedPassword.setText(passwords[i]);
                        }
                    });

            AlertDialog passwordSelector = builder.create();

            passwordSelector.show();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            pg.setProgress(values[0] * 20);
        }
    }

    public void onAsyncClick(View view){
        if(!validate()){
            return;
        }

        new GenerateAsyncPasswords().execute(name.getText().toString(), dept.getText().toString(), age.getText().toString(), zipcode.getText().toString());
    }
}
