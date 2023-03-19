package com.example.unswpolicieschatgpt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unswpolicieschatgpt.database.Policy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PolicyAdapter extends RecyclerView.Adapter<PolicyAdapter.MyViewHolder> implements Filterable {
    private List<Policy> mPolicies, mPoliciesFiltered;
    private PolicyRecyclerViewInterface mInterface;

    public static final int SORT_METHOD_NAME = 1;
    public static final int SORT_METHOD_NAME_REVERSE = 2;

    //PolicyAdapter constructor method
    public PolicyAdapter(ArrayList<Policy> policies, PolicyRecyclerViewInterface policyInterface) {
        mPolicies = policies;
        /*if (mPolicies == null) {
            mPolicies = new ArrayList<>();
        }*/
        mPoliciesFiltered = policies;
        mInterface = policyInterface;
    }

    @NonNull
    @Override
    public PolicyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view, mInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Assign value to each row in RecyclerView based on position of RecyclerView item
        Policy policy = mPoliciesFiltered.get(position);
        holder.mTitle.setText(policy.getTitle());
    }

    /**
     Return number of items in RecyclerView
     */
    @Override
    public int getItemCount() {
        /*if (mPoliciesFiltered == null) {
            return 0;
        }*/
        return mPoliciesFiltered.size();
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
                String query = charSequence.toString();
                //String query = charSequence != null ? charSequence.toString() : "";

                //Check user query
                if(query.isEmpty()) {
                    //If empty, show the exact same list
                    mPoliciesFiltered = mPolicies;
                } else {
                    //Create a new ArrayList to add filtered policies
                    ArrayList<Policy> filteredList = new ArrayList<>();
                    for(Policy policy : mPolicies) {
                        if(policy.getTitle().toLowerCase().contains(query.toLowerCase())) {
                            filteredList.add(policy);
                        }
                    }
                    mPoliciesFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mPoliciesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mPoliciesFiltered = (ArrayList<Policy>) filterResults.values;
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
        private TextView mTitle;

        public MyViewHolder(@NonNull View itemView, PolicyRecyclerViewInterface mInterface) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.tvTitle);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mInterface != null) {
                        mInterface.onPolicyClick((String) itemView.getTag());
                    }

                }
            });
        }
    }

    /**
     * Sort policy list by name
     * Use the Java Collections.sort() and Comparator methods
     * @param sortMethod
     */
    public void sort(final int sortMethod) {
        if (mPoliciesFiltered.size() > 0) {
            Collections.sort(mPoliciesFiltered, new Comparator<Policy>() {
                @Override
                public int compare(Policy o1, Policy o2) {
                    if (sortMethod == SORT_METHOD_NAME) {
                        return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
                    } else if (sortMethod == SORT_METHOD_NAME_REVERSE) {
                        return o2.getTitle().toLowerCase().compareTo(o1.getTitle().toLowerCase());
                    }
                    // By default sort the list in ascending order
                    return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
                }
            });
        }
        notifyDataSetChanged();
    }

    //Add data to the adapter
    public void setData(ArrayList<Policy> data) {
        mPolicies.clear();
        mPolicies.addAll(data);
        notifyDataSetChanged();
    }
}

