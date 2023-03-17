package com.example.unswpolicieschatgpt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PolicyAdapter extends RecyclerView.Adapter<PolicyAdapter.MyViewHolder> implements Filterable {
    private List<Policy> mPolicies, mPoliciesFiltered;
    private PolicyRecyclerViewInterface mInterface;

    public static final int SORT_METHOD_NAME = 1;
    public static final int SORT_METHOD_NAME_REVERSE = 2;

    public PolicyAdapter(List<Policy> policies, PolicyRecyclerViewInterface policyInterface) {
        mPolicies = policies;
        if (mPolicies == null) {
            mPolicies = new ArrayList<>();
        }
        mPoliciesFiltered = new ArrayList<>(policies);
        mInterface = policyInterface;
    }

    @NonNull
    @Override
    public PolicyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view, mInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull PolicyAdapter.MyViewHolder holder, int position) {
        Policy policy = mPoliciesFiltered.get(position);
        holder.mName.setText(policy.getName());
    }

    @Override
    public int getItemCount() {
        if (mPoliciesFiltered == null) {
            return 0;
        }
        return mPoliciesFiltered.size();
    }


    // getFilter method for search function
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                //String query = charSequence.toString();
                String query = charSequence != null ? charSequence.toString() : "";
                if(query.isEmpty()) {
                    mPoliciesFiltered = mPolicies;
                } else {
                    ArrayList<Policy> filteredList = new ArrayList<>();
                    for(Policy policy : mPolicies) {
                        String name = policy.getName();
                        if(name != null && name.contains(query)) {
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


    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;

        public MyViewHolder(@NonNull View itemView, PolicyRecyclerViewInterface mInterface) {
            super(itemView);
            mName = itemView.findViewById(R.id.tvName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mInterface.onPolicyClick(getAdapterPosition());
                }
            });
        }
    }

    // Use the Java Collections.sort() and Comparator methods to sort the list
    public void sort(final int sortMethod) {
        if (mPoliciesFiltered.size() > 0) {
            Collections.sort(mPoliciesFiltered, new Comparator<Policy>() {
                @Override
                public int compare(Policy o1, Policy o2) {
                    if (sortMethod == SORT_METHOD_NAME) {
                        return o1.getName().compareTo(o2.getName());
                    } else if (sortMethod == SORT_METHOD_NAME_REVERSE) {
                        return o2.getName().compareTo(o1.getName());
                    }
                    // By default sort the list by coin name
                    return o1.getName().compareTo(o2.getName());
                }
            });
        }
        notifyDataSetChanged();
    }
}

