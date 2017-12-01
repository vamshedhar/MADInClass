package io.vamshedhar.contacts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SignUpActivity extends AppCompatActivity {

    EditText firstName, lastName, email, password, repeatPassword;

    public static final int REQUEST_IMAGE_CAPTURE = 104;
    public static final int PICK_IMAGE_REQUEST = 105;

    Button signUpBtn, cancelBtn;

    ImageView userProfilePic;

    ProgressBar createProgress;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserReference;

    FirebaseStorage storage;
    StorageReference storageRef;

    String imageAbsolutePath;

    User oldUser;

    public boolean isConnectedOnline(){

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if(info != null && info.isConnected()){
            return true;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 5002){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                takePhoto();
            } else {
                Toast.makeText(SignUpActivity.this, "Please Grant required permissions!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        imageAbsolutePath = image.getAbsolutePath();
        return image;
    }

    public void takePhoto(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if(photoFile != null) {
                Uri imageUri = FileProvider.getUriForFile(getApplicationContext(),
                        "io.vamshedhar.contacts.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
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

        if (oldUser != null){
            return true;
        }

        if (!isValidEmail(email.getText().toString().trim())){
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.getText().toString().trim().isEmpty() || repeatPassword.getText().toString().trim().isEmpty() ){
            Toast.makeText(this, "Please enter valid password!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.getText().toString().trim().length() < 8){
            Toast.makeText(this, "Passwords must be minimum 8 characters!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.getText().toString().trim().equals(repeatPassword.getText().toString().trim())){
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    public void cameraIntent(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 5002);
        } else {
            takePhoto();
        }

    }

    public void galleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void showDiag(){
        final CharSequence[] items = { "Take Photo", "Choose from Gallery",  "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    cameraIntent();

                } else if (items[item].equals("Choose from Gallery")) {
                    galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void saveUser(String userId, String profilePicUrl){
        signUpBtn.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);
        createProgress.setVisibility(View.INVISIBLE);

        User user = new User(firstName.getText().toString().trim(), lastName.getText().toString().trim(), profilePicUrl);
        mUserReference.child(userId).setValue(user);
        if (oldUser == null){
            Toast.makeText(SignUpActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignUpActivity.this, ContactListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            finish();
        }

    }

    public void uploadImage(final String userId){

        userProfilePic.setDrawingCacheEnabled(true);
        userProfilePic.buildDrawingCache();
        Bitmap bitmap = userProfilePic.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference contactsImageRef = storageRef.child(userId);

        UploadTask uploadTask = contactsImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(SignUpActivity.this, "Image Upload Failed!", Toast.LENGTH_SHORT).show();
                signUpBtn.setVisibility(View.VISIBLE);
                cancelBtn.setVisibility(View.VISIBLE);
                createProgress.setVisibility(View.INVISIBLE);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                saveUser(userId, downloadUrl.toString());
                Log.d(MainActivity.TAG, "Success : " + downloadUrl.toString());
            }
        });
    }

    public void loginWithCredentials(){


        String emailValue = email.getText().toString().trim();
        String passwordValue = password.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(emailValue, passwordValue).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userId = user.getUid();
                    if (imageAbsolutePath != null && !imageAbsolutePath.equals("")){
                        uploadImage(userId);
                    } else {
                        saveUser(userId, "");
                    }


                } else {
                    Log.d(MainActivity.TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void loadUserData(){
        firstName.setText(oldUser.getFirstName());
        lastName.setText(oldUser.getLastName());

        email.setVisibility(View.INVISIBLE);
        password.setVisibility(View.INVISIBLE);
        repeatPassword.setVisibility(View.INVISIBLE);

        if (!oldUser.getProfilePic().equals("")){
            Picasso.with(this)
                    .load(oldUser.getProfilePic())
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.no_image)
                    .into(userProfilePic);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.choosePassword);
        repeatPassword = findViewById(R.id.repeatPassword);

        signUpBtn= findViewById(R.id.registerBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        userProfilePic = findViewById(R.id.userProfilePic);

        createProgress = findViewById(R.id.createProgress);

        createProgress.setVisibility(View.INVISIBLE);

        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDiag();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserReference = mDatabase.child("users");

        storage = FirebaseStorage.getInstance();

        storageRef = storage.getReference().child("users");

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(ContactListActivity.EDIT_USER)){
            oldUser = (User) getIntent().getSerializableExtra(ContactListActivity.EDIT_USER);
            loadUserData();

        } else {
            oldUser = null;
        }

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
                        signUpBtn.setVisibility(View.INVISIBLE);
                        cancelBtn.setVisibility(View.INVISIBLE);
                        createProgress.setVisibility(View.VISIBLE);
                        if (oldUser == null){
                            loginWithCredentials();
                        } else{

                            String userId = mAuth.getCurrentUser().getUid();
                            if (imageAbsolutePath != null && !imageAbsolutePath.equals("")){
                                uploadImage(userId);
                            } else {
                                saveUser(userId, oldUser.getProfilePic());
                            }
                        }

                    }
                } else{
                    Toast.makeText(SignUpActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
                
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE){
            if(resultCode == RESULT_OK){

                try {

                    Bitmap bmImg = BitmapFactory.decodeFile(imageAbsolutePath);
                    userProfilePic.setImageBitmap(bmImg);

                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                    Log.e("Camera", e.toString());
                }

            }
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            imageAbsolutePath = uri.getPath();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                userProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
