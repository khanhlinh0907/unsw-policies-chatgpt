package com.example.unswpolicieschatgpt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatBotAdapter extends RecyclerView.Adapter<ChatBotAdapter.MyViewHolder> {
    private List<ChatBotMessage> mMessages;

    public ChatBotAdapter(List<ChatBotMessage> messages) {
        mMessages = messages;
    }

    @NonNull
    @Override
    public ChatBotAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatbot_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatBotAdapter.MyViewHolder holder, int position) {
        ChatBotMessage message = mMessages.get(position);
        holder.mTopic.setText(message.getTopic());
        holder.mLastMsg.setText(message.getLastMsg());
        holder.mDate.setText(message.getDate());
        holder.mTime.setText(message.getTime());
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTopic, mLastMsg, mDate, mTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTopic = itemView.findViewById(R.id.tvTopic);
            mLastMsg = itemView.findViewById(R.id.tvLastMessage);
            mDate = itemView.findViewById(R.id.tvDate);
            mTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
