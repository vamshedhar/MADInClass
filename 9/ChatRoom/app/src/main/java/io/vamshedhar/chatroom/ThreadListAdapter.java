package io.vamshedhar.chatroom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 11/6/17 9:06 PM.
 * vchinta1@uncc.edu
 */

public class ThreadListAdapter extends RecyclerView.Adapter<ThreadListAdapter.ViewHolder> {

    ArrayList<ChatThread> threads;
    Context context;
    User currentUser;

    ThreadListInterface IData;

    public interface ThreadListInterface {
        void onDeleteThreadClicked(String id);
        void onThreadClicked(ChatThread thread);
    }

    public ThreadListAdapter(ArrayList<ChatThread> threads, Context context, User currentUser, ThreadListInterface IData) {
        this.threads = threads;
        this.context = context;
        this.currentUser = currentUser;
        this.IData = IData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thread_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatThread thread = threads.get(position);

        holder.threadName.setText(thread.getTitle());

        holder.deleteThread.setVisibility(View.INVISIBLE);

        holder.threadName.setTag(position);
        holder.deleteThread.setTag(position);

        if (thread.getUser_id().equals(currentUser.getUser_id())){
            holder.deleteThread.setVisibility(View.VISIBLE);
        }

        holder.threadName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView title = (TextView) view;
                int index = (int) title.getTag();

                ChatThread selectedThread = threads.get(index);
                IData.onThreadClicked(selectedThread);
            }
        });

        holder.deleteThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView deleteImage = (ImageView) view;
                int index = (int) deleteImage.getTag();

                ChatThread selectedThread = threads.get(index);
                IData.onDeleteThreadClicked(selectedThread.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return threads.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView threadName;
        ImageView deleteThread;

        public ViewHolder(View itemView) {
            super(itemView);
            threadName  = itemView.findViewById(R.id.threadTitle);
            deleteThread = itemView.findViewById(R.id.deleteThread);
        }
    }
}
