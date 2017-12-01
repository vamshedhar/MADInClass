package io.vamshedhar.contacts;

import android.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class CreateContactActivity extends MenuActionBar {

    public static final String PROFILE_PIC_KEY = "SELECTED_PROFILE_PIC";
    public static final int PROFILE_PIC_CODE = 10001;
    public static final int REQUEST_IMAGE_CAPTURE = 102;
    public static final int PICK_IMAGE_REQUEST = 103;

    EditText nameET, emailET, phoneNumberET;
    ProgressBar imageUplaodPB;
    ImageView profilePicture;

    Button submitBtn;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserReference;
    private DatabaseReference mContactsReference;
    FirebaseStorage storage;
    StorageReference storageRef;

    int profileImage;

    String imageAbsolutePath;

    String userId;

    Contact oldContact;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 5002){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                takePhoto();
            } else {
                Toast.makeText(CreateContactActivity.this, "Please Grant required permissions!", Toast.LENGTH_SHORT).show();
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

        return true;
    }

    public void reset(){
        nameET.setText("");
        emailET.setText("");
        phoneNumberET.setText("");
        profilePicture.setImageResource(R.drawable.add_photo);

        imageUplaodPB.setVisibility(View.INVISIBLE);

        submitBtn.setVisibility(View.VISIBLE);
        imageUplaodPB.setProgress(0);
    }

    public void cameraIntent(){
        if (ContextCompat.checkSelfPermission(CreateContactActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(CreateContactActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(CreateContactActivity.this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 5002);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(CreateContactActivity.this);
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

    public void uploadImage(final String contactId){

        submitBtn.setVisibility(View.INVISIBLE);

        imageUplaodPB.setVisibility(View.VISIBLE);

        profilePicture.setDrawingCacheEnabled(true);
        profilePicture.buildDrawingCache();
        Bitmap bitmap = profilePicture.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference contactsImageRef = storageRef.child(contactId);

        UploadTask uploadTask = contactsImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(CreateContactActivity.this, "Image Upload Failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                saveContact(contactId, downloadUrl.toString());
                Log.d(MainActivity.TAG, "Success : " + downloadUrl.toString());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                imageUplaodPB.setProgress((int) progress);
                Log.d(MainActivity.TAG, "Upload is " + progress + "% done");
            }
        });
    }

    public void saveContact(String id, String imageUrl){

        Contact contact = new Contact();
        contact.setId(id);
        contact.setName(nameET.getText().toString().trim());
        contact.setEmail(emailET.getText().toString().trim());
        contact.setPhoneNo(phoneNumberET.getText().toString().trim());
        contact.setProfilePic(imageUrl);

        mContactsReference.child(userId).child(id).setValue(contact);

        Toast.makeText(CreateContactActivity.this, "Contact added successfully!", Toast.LENGTH_LONG).show();

        reset();

    }

    public void loadOldContact(){
        nameET.setText(oldContact.getName());
        emailET.setText(oldContact.getEmail());
        phoneNumberET.setText(oldContact.getPhoneNo());

        if (!oldContact.getProfilePic().equals("")){
            Picasso.with(this)
                    .load(oldContact.getProfilePic())
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.no_image)
                    .into(profilePicture);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);

        storage = FirebaseStorage.getInstance();

        storageRef = storage.getReference().child("contacts");

        nameET = findViewById(R.id.nameTextBox);
        emailET = findViewById(R.id.emailTextBox);
        phoneNumberET = findViewById(R.id.phoneNumberTB);

        submitBtn = findViewById(R.id.submitButton);
        
        profilePicture = findViewById(R.id.profilePicture);
        profileImage = R.drawable.select_avatar;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserReference = mDatabase.child("users");
        mContactsReference = mDatabase.child("contacts");

        imageUplaodPB = findViewById(R.id.imageUploadProgress);

        imageUplaodPB.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(ContactListActivity.EDIT_CONTACT)){
            oldContact = (Contact) getIntent().getSerializableExtra(ContactListActivity.EDIT_CONTACT);
            loadOldContact();
        } else{
            oldContact = null;
        }

        if (user != null){
            userId = user.getUid();
        } else {
            showMainScreen();
        }

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDiag();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if (isValidData()){
                String id = "";

                if (oldContact != null){
                    id = oldContact.getId();
                } else{
                    id = mContactsReference.child(userId).push().getKey();
                }

                String imageURL = "";

                if (oldContact != null){
                    imageURL = oldContact.getProfilePic();
                }

                if (imageAbsolutePath != null && !imageAbsolutePath.equals("")){
                    uploadImage(id);
                } else {
                    saveContact(id, imageURL);
                }

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
                    profilePicture.setImageBitmap(bmImg);

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

                profilePicture.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
