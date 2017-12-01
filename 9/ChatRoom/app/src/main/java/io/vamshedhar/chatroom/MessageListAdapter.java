package io.vamshedhar.chatroom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 11/7/17 1:20 AM.
 * vchinta1@uncc.edu
 */

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {

    ArrayList<ChatMessage> messages;
    Context context;
    User currentUser;

    MessageListInterface IData;

    public interface MessageListInterface {
        void onDeleteMessageClicked(String id);
    }

    public MessageListAdapter(ArrayList<ChatMessage> messages, Context context, User currentUser, MessageListInterface IData) {
        this.messages = messages;
        this.context = context;
        this.currentUser = currentUser;
        this.IData = IData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        holder.messageText.setText(message.getMessage());
        holder.messageUser.setText(message.getUserFullName());
        holder.messageTime.setText(message.getCreated_at());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss z");
        try {
            PrettyTime p = new PrettyTime();
            Date createDate = dateFormat.parse(message.getCreated_at() + " GMT");
            holder.messageTime.setText(p.format(createDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 2017-11-07 05:40:48
        // yyyy-MM-dd hh:mm:ss z

        holder.deleteMessage.setVisibility(View.INVISIBLE);

        holder.deleteMessage.setTag(position);

        if (message.getUser_id().equals(currentUser.getUser_id())){
            holder.deleteMessage.setVisibility(View.VISIBLE);
        }

        holder.deleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView deleteIV = (ImageView) view;

                int index = (int) deleteIV.getTag();

                ChatMessage selectedMessage = messages.get(index);

                IData.onDeleteMessageClicked(selectedMessage.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, messageUser, messageTime;
        ImageView deleteMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            messageText  = itemView.findViewById(R.id.messageText);
            messageUser = itemView.findViewById(R.id.messageUser);
            messageTime = itemView.findViewById(R.id.messageTime);
            deleteMessage = itemView.findViewById(R.id.deleteMessage);
        }
    }
}
