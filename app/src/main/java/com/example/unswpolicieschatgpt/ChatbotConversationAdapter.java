package com.example.unswpolicieschatgpt;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unswpolicieschatgpt.database.Policy;

import java.util.ArrayList;
import java.util.List;

public class ChatbotConversationAdapter extends RecyclerView.Adapter<ChatbotConversationAdapter.MyViewHolder> implements Filterable {
    private List<ConversationMessage> mConversation, mConversationFiltered;

    public static final int SORT_METHOD_NAME = 1;
    public static final int SORT_METHOD_NAME_REVERSE = 2;

    //PolicyAdapter constructor method
    public ChatbotConversationAdapter(List<ConversationMessage> conversation) {
        if (mConversation == null) {
            mConversation = new ArrayList<>();
        }
        mConversationFiltered = conversation;

    }

    @NonNull
    @Override
    public ChatbotConversationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_chatbot_conversation_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatbotConversationAdapter.MyViewHolder holder, int position) {
        //Assign value to each row in RecyclerView based on position of RecyclerView item
        ConversationMessage conversationMessage = mConversationFiltered.get(position);


        if (conversationMessage.getMessageType().equals("BOT")) {

            //Makes all user related UI disappear.
            holder.mUserLayout.setVisibility(View.GONE);
            holder.mBotLayout.setVisibility(View.VISIBLE);

            //Set the text of the bot to match the contents of the message.
            holder.mBotChat.setText(conversationMessage.getMessageContents());


        } else {
            //Handling if the message is a user text.
            //Make bot related UI disappear.
            holder.mBotLayout.setVisibility(View.GONE);
            holder.mUserLayout.setVisibility(View.VISIBLE);

            holder.mUserChat.setText(conversationMessage.getMessageContents());
        }

    }

    /**
     Return number of items in RecyclerView
     */
    @Override
    public int getItemCount() {
        if (mConversationFiltered == null) {
            return 0;
        }
        return mConversationFiltered.size();
    }

    /**
     * getFilter() method for search function
     * @return
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                //Convert user input into string

                String query = charSequence != null ? charSequence.toString() : "";

                //Check user query
                if(query.isEmpty()) {
                    //If empty, show the exact same list
                    mConversationFiltered = mConversation;
                } else {
                    //Create a new ArrayList to add filtered policies
                    ArrayList<ConversationMessage> filteredList = new ArrayList<>();
                    for(ConversationMessage message : mConversation) {
                        if(message.getMessageContents().toLowerCase().contains(query.toLowerCase())) {
                            filteredList.add(message);
                        }
                    }
                    mConversationFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mConversationFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mConversationFiltered = (ArrayList<ConversationMessage>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    //Updated getFilter method for Spinner (includes search getFilter code)
//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence charSequence) {
//                String query = charSequence.toString();
//                String selectedCategory = mSelectedCategory;
//                List<Policy> filteredList = new ArrayList<>();
//                if (selectedCategory.equalsIgnoreCase("All Categories")) {
//                    // If "All Categories" is selected, show all policies that match the search query
//                    for (Policy policy : mPolicies) {
//                        if (policy.getName().toLowerCase().contains(query.toLowerCase())) {
//                            filteredList.add(policy);
//                        }
//                    }
//                } else {
//                    // Otherwise, show policies that match both the search query and the selected category
//                    for (Policy policy : mPolicies) {
//                        if (policy.getName().toLowerCase().contains(query.toLowerCase())
//                                && policy.getCategory().equalsIgnoreCase(selectedCategory)) {
//                            filteredList.add(policy);
//                        }
//                    }
//                }
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = filteredList;
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//                mPoliciesFiltered = (ArrayList<Policy>) filterResults.values;
//                notifyDataSetChanged();
//            }
//        };
//    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mBotChat, mUserChat;
        private ConstraintLayout mUserLayout, mBotLayout;
        private ImageView mBotImage;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mBotLayout = itemView.findViewById(R.id.botLayout);
            mUserLayout = itemView.findViewById(R.id.userLayout);
            mBotChat = itemView.findViewById(R.id.botText);
            mUserChat = itemView.findViewById(R.id.userText);
            mBotImage = itemView.findViewById(R.id.botImage);


        }
    }

//    /**
//     * Sort policy list by name
//     * Use the Java Collections.sort() and Comparator methods
//     * @param sortMethod
//     */
//    public void sort(final int sortMethod) {
//        if (mConversationFiltered.size() > 0) {
//            Collections.sort(mConversationFiltered, new Comparator<ConversationMessage>() {
//                @Override
//                public int compare(Policy o1, Policy o2) {
//                    if (sortMethod == SORT_METHOD_NAME) {
//                        return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
//                    } else if (sortMethod == SORT_METHOD_NAME_REVERSE) {
//                        return o2.getTitle().toLowerCase().compareTo(o1.getTitle().toLowerCase());
//                    }
//                    // By default sort the list in ascending order
//                    return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
//                }
//            });
//        }
//        notifyDataSetChanged();
//    }

    //Add data to the adapter
    public void setData(ArrayList<ConversationMessage> data) {
        mConversation.clear();
        mConversation.addAll(data);
        notifyDataSetChanged();
    }
}

