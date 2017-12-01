package io.vamshedhar.inclass10;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText firstName, lastName, email, password, repeatPassword;

    Button signUpBtn, cancelBtn;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserReference;

    public boolean isConnectedOnline(){

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if(info != null && info.isConnected()){
            return true;
        }

        return false;
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public boolean isValidData(){
        if (firstName.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter valid first name!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (lastName.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter valid last name!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidEmail(email.getText().toString().trim())){
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

        signUpBtn= (Button) findViewById(R.id.registerBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserReference = mDatabase.child("users");

        mAuth = FirebaseAuth.getInstance();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnectedOnline()){
                    if (isValidData()){
                        String emailValue = email.getText().toString().trim();
                        String passwordValue = password.getText().toString().trim();

                        mAuth.createUserWithEmailAndPassword(emailValue, passwordValue).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String userId = user.getUid();
                                    mUserReference.child(userId).child("first_name").setValue(firstName.getText().toString().trim());
                                    mUserReference.child(userId).child("last_name").setValue(lastName.getText().toString().trim());
                                    Toast.makeText(SignUpActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignUpActivity.this, ContactListActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    Log.d(MainActivity.TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                } else{
                    Toast.makeText(SignUpActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
                
            }
        });


    }
}
