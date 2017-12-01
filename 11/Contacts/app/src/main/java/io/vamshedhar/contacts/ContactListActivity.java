package io.vamshedhar.contacts;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ContactListActivity extends MenuActionBar implements ContactListAdapter.ContactListInterface {

    Button createContactBtn;

    public static final String EDIT_CONTACT = "EDIT_CONTACT";
    public static final String EDIT_USER = "EDIT_USER";
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private DatabaseReference mUsersRef;

    String userId;

    User currentUser;

    ImageView editUser;

    ArrayList<Contact> contacts;

    TextView userName;

    private RecyclerView contactList;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private DatabaseReference mContactsReference;

    ValueEventListener listListner;

    DatabaseReference userContactsRef;

    public void showMainScreen(){
        Intent intent = new Intent(ContactListActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void loadData(){
        Log.d(MainActivity.TAG, contacts.toString());
        adapter = new ContactListAdapter(contacts, this, this);
        contactList.setAdapter(adapter);

        if (contacts.size() == 0){
            Toast.makeText(this, "No contacts to load!", Toast.LENGTH_LONG).show();
        }
    }

    public void loadUserData(){
        userName.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
    }

    public void loadUser(){
        mUsersRef = mDatabase.child("users");
        DatabaseReference mCurrentUserRef = mUsersRef.child(userId);

        mCurrentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                loadUserData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        createContactBtn = (Button) findViewById(R.id.createContactBtn);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mContactsReference = mDatabase.child("contacts");

        mAuth = FirebaseAuth.getInstance();

        userName = findViewById(R.id.username);

        editUser = findViewById(R.id.editBtn);

        editUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactListActivity.this, SignUpActivity.class);
                intent.putExtra(EDIT_USER, currentUser);
                startActivity(intent);
            }
        });

        contactList = (RecyclerView) findViewById(R.id.contactList);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        contactList.setLayoutManager(mLayoutManager);

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null){
            userId = user.getUid();
            loadUser();
        } else {
            showMainScreen();
        }

        contacts = new ArrayList<>();

        userContactsRef = mContactsReference.child(userId);


        listListner = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contacts = new ArrayList<>();
                for (DataSnapshot contactSnap : dataSnapshot.getChildren()){
                    Contact contact = contactSnap.getValue(Contact.class);
                    contacts.add(contact);
                }
                loadData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ContactListActivity.this, "Failed to load contacts!" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        userContactsRef.addValueEventListener(listListner);


        createContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactListActivity.this, CreateContactActivity.class);
                startActivity(intent);
            }
        });
    }

    public void deleteContact(String id){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        userContactsRef.child(id).removeValue();
        StorageReference desertRef = storageRef.child("contacts/" + id);
        desertRef.delete();
    }

    @Override
    public void onContactClicked(final String id) {
        Log.d(MainActivity.TAG, id);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Are you sure you want to delete the contact?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteContact(id);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        final AlertDialog deleteAlert = builder.create();

        deleteAlert.show();
    }

    @Override
    public void onEditClicked(Contact contact) {
        Intent intent = new Intent(this, CreateContactActivity.class);
        intent.putExtra(EDIT_CONTACT, contact);
        startActivity(intent);
    }
}
