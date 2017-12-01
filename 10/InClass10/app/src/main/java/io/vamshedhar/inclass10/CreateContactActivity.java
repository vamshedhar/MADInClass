package io.vamshedhar.inclass10;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CreateContactActivity extends AppCompatActivity {

    public static final String PROFILE_PIC_KEY = "SELECTED_PROFILE_PIC";
    public static final int PROFILE_PIC_CODE = 10001;

    EditText nameET, emailET, phoneNumberET;
    RadioGroup departmentRadioGroup;
    ImageView profilePicture;

    Button submitBtn;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserReference;
    private DatabaseReference mContactsReference;

    int profileImage;

    String userId;

    HashMap<Integer, String> imageIdMap = new HashMap();

    public void showMainScreen(){
        Intent intent = new Intent(CreateContactActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public boolean isValidPhoneNumber(String number){
        return number.length() >= 10 && number.length() <= 13 && PhoneNumberUtils.isGlobalPhoneNumber(number);
    }

    public boolean isValidData(){
        if (nameET.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please enter valid name!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidEmail(emailET.getText().toString().trim())){
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidPhoneNumber(phoneNumberET.getText().toString().trim())){
            Toast.makeText(this, "Please enter valid Phone Number!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (departmentRadioGroup.getCheckedRadioButtonId() == -1){
            Toast.makeText(this, "Please enter valid Department!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void reset(){
        nameET.setText("");
        emailET.setText("");
        phoneNumberET.setText("");
        profileImage = R.drawable.select_avatar;
        departmentRadioGroup.check(R.id.sisButton);
        profilePicture.setImageResource(profileImage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);

        nameET = (EditText) findViewById(R.id.nameTextBox);
        emailET = (EditText) findViewById(R.id.emailTextBox);
        phoneNumberET = (EditText) findViewById(R.id.phoneNumberTB);

        submitBtn = (Button) findViewById(R.id.submitButton);
        
        departmentRadioGroup = (RadioGroup) findViewById(R.id.departmentRadioGroup);
        
        profilePicture = (ImageView) findViewById(R.id.profilePicture);
        profileImage = R.drawable.select_avatar;

        imageIdMap.put(R.drawable.avatar_f_1, "f_1");
        imageIdMap.put(R.drawable.avatar_f_2, "f_2");
        imageIdMap.put(R.drawable.avatar_f_3, "f_3");
        imageIdMap.put(R.drawable.avatar_m_1, "m_1");
        imageIdMap.put(R.drawable.avatar_m_2, "m_2");
        imageIdMap.put(R.drawable.avatar_m_3, "m_3");

        imageIdMap.put(R.drawable.select_avatar, "no_avatar");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserReference = mDatabase.child("users");
        mContactsReference = mDatabase.child("contacts");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null){
            userId = user.getUid();
        } else {
            showMainScreen();
        }

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateContactActivity.this, SelectAvatar.class);
                startActivityForResult(intent, PROFILE_PIC_CODE);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidData()){
                    String id = mContactsReference.child(userId).push().getKey();
                    RadioButton department = (RadioButton) findViewById(departmentRadioGroup.getCheckedRadioButtonId());
                    Contact contact = new Contact();
                    contact.setId(id);
                    contact.setName(nameET.getText().toString().trim());
                    contact.setEmail(emailET.getText().toString().trim());
                    contact.setPhoneNo(phoneNumberET.getText().toString().trim());
                    contact.setDepartment(department.getText().toString());
                    contact.setProfilePic(imageIdMap.get(profileImage));

                    mContactsReference.child(userId).child(id).setValue(contact);

                    Toast.makeText(CreateContactActivity.this, "Contact added successfully!", Toast.LENGTH_LONG).show();

                    reset();

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PROFILE_PIC_CODE){
            if(resultCode == RESULT_OK){

                profileImage = data.getExtras().getInt(PROFILE_PIC_KEY);
                profilePicture.setImageResource(profileImage);
            }
        }
    }
}
