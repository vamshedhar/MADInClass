package io.vamshedhar.inclass10;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.solver.ArrayLinkedVariables;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity implements ContactListAdapter.ContactListInterface {

    Button createContactBtn, signoutBtn;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    String userId;

    ArrayList<Contact> contacts;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        createContactBtn = (Button) findViewById(R.id.createContactBtn);
        signoutBtn = (Button) findViewById(R.id.logoutBtn);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mContactsReference = mDatabase.child("contacts");

        mAuth = FirebaseAuth.getInstance();

        contactList = (RecyclerView) findViewById(R.id.contactList);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        contactList.setLayoutManager(mLayoutManager);

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null){
            userId = user.getUid();
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

        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userContactsRef.removeEventListener(listListner);
                FirebaseAuth.getInstance().signOut();
                showMainScreen();
            }
        });

        createContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactListActivity.this, CreateContactActivity.class);
                startActivity(intent);
            }
        });
    }

    public void deleteContact(String id){
        userContactsRef.child(id).removeValue();
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
}
