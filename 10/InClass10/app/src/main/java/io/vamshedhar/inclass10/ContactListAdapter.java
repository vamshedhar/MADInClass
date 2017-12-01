package io.vamshedhar.inclass10;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    HashMap<String, Integer> imageIdMap = new HashMap();

    public interface ContactListInterface {
        void onContactClicked(String id);
    }

    public ContactListAdapter(ArrayList<Contact> contacts, Context context, ContactListInterface IData) {
        this.contacts = contacts;
        this.context = context;
        this.IData = IData;

        imageIdMap.put("f_1", R.drawable.avatar_f_1);
        imageIdMap.put("f_2", R.drawable.avatar_f_2);
        imageIdMap.put("f_3", R.drawable.avatar_f_3);
        imageIdMap.put("m_1", R.drawable.avatar_m_1);
        imageIdMap.put("m_2", R.drawable.avatar_m_2);
        imageIdMap.put("m_3", R.drawable.avatar_m_3);

        imageIdMap.put("no_avatar", R.drawable.select_avatar);
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
        holder.contactDepartment.setText(contact.getDepartment());
        holder.contactNo.setText(contact.getPhoneNo());

        holder.contactImage.setImageResource(imageIdMap.get(contact.getProfilePic()));

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView contactName, contactEmail, contactNo, contactDepartment;
        ImageView contactImage;

        Contact contact;
        ContactListInterface IData;

        public ViewHolder(View itemView) {
            super(itemView);
            contactName  = itemView.findViewById(R.id.contactName);
            contactEmail = itemView.findViewById(R.id.contactEmail);
            contactNo = itemView.findViewById(R.id.contactNo);
            contactDepartment = itemView.findViewById(R.id.contactDepartment);
            contactImage = itemView.findViewById(R.id.contactImage);

            contact = null;

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    IData.onContactClicked(contact.getId());
                    return true;
                }
            });
        }
    }
}
