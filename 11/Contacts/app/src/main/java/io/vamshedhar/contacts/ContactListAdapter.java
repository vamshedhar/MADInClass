package io.vamshedhar.contacts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 11/13/17 8:18 PM.
 * vchinta1@uncc.edu
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    ArrayList<Contact> contacts;
    Context context;

    ContactListInterface IData;


    public interface ContactListInterface {
        void onContactClicked(String id);
        void onEditClicked(Contact contact);
    }

    public ContactListAdapter(ArrayList<Contact> contacts, Context context, ContactListInterface IData) {
        this.contacts = contacts;
        this.context = context;
        this.IData = IData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = contacts.get(position);



        holder.contact = contact;
        holder.IData = IData;

        holder.contactName.setText(contact.getName());
        holder.contactEmail.setText(contact.getEmail());
        holder.contactNo.setText(contact.getPhoneNo());

        holder.deleteImage.setTag(contact.getId());
        holder.editImage.setTag(position);

        if (!contact.getProfilePic().equals("")){
            Picasso.with(context)
                    .load(contact.getProfilePic())
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.no_image)
                    .into(holder.contactImage);
        }

        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView deleteImageView = (ImageView) view;
                String id = deleteImageView.getTag().toString();
                IData.onContactClicked(id);
            }
        });

        holder.editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView deleteImageView = (ImageView) view;
                int index = (int) deleteImageView.getTag();
                IData.onEditClicked(contacts.get(index));
            }
        });

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView contactName, contactEmail, contactNo;
        ImageView contactImage, editImage, deleteImage;

        Contact contact;
        ContactListInterface IData;

        public ViewHolder(View itemView) {
            super(itemView);
            contactName  = itemView.findViewById(R.id.contactName);
            contactEmail = itemView.findViewById(R.id.contactEmail);
            contactNo = itemView.findViewById(R.id.contactNo);
            contactImage = itemView.findViewById(R.id.contactImage);
            editImage = itemView.findViewById(R.id.editImage);
            deleteImage = itemView.findViewById(R.id.deleteImage);
            contact = null;
        }
    }
}
