package io.vamshedhar.inclass10;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Contacts";

    EditText usernameET, passwordET;
    TextView signUpBtn;
    Button loginBtn;

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

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
        if (!isValidEmail(usernameET.getText().toString().trim())){
            Toast.makeText(this, "Please enter valid username!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (passwordET.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter valid password!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    CheckBox showPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameET = (EditText) findViewById(R.id.userEmail);
        passwordET = (EditText) findViewById(R.id.userPassword);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        signUpBtn = (TextView) findViewById(R.id.signupBtn);

        showPassword = (CheckBox) findViewById(R.id.showPasswordCB);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserReference = mDatabase.child("users");

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null){
            Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            if (!showPassword.isChecked()){
                passwordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else{
                passwordET.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            passwordET.setSelection(passwordET.getText().length());
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnectedOnline()){
                    if (isValidData()){
                        String emailValue = usernameET.getText().toString().trim();
                        String passwordValue = passwordET.getText().toString().trim();

                        mAuth.signInWithEmailAndPassword(emailValue, passwordValue).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(MainActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    Log.d(TAG, "createUserWithEmail:failure" + task.getException().getMessage());
                                    Toast.makeText(MainActivity.this, "Authentication failed. " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                } else{
                    Toast.makeText(MainActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
