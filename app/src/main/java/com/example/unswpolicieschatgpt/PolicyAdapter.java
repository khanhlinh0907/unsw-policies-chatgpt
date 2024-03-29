package com.example.unswpolicieschatgpt;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unswpolicieschatgpt.database.Policy;

import java.net.URL;
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
    public PolicyAdapter(List<Policy> policies, PolicyRecyclerViewInterface policyInterface) {
        this.mPolicies = policies;
        this.mPoliciesFiltered = policies;
        this.mInterface = policyInterface;
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
        System.out.println("Position: " + position);
        holder.mTitle.setText(policy.getTitle());
        holder.mDescription.setText(policy.getResponsible_officer().trim());
        holder.itemView.setTag(policy.getPdf_url());
        Log.d("TAG", "Url for item " + position + " is " + policy.getPdf_url());
        Log.d("TAG", "Title for item " + position + " is " + policy.getTitle());

    }

    /**
     Return number of items in RecyclerView
     */
    @Override
    public int getItemCount() {
        if (mPoliciesFiltered == null) {
            return 0;
        }
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

                String query = charSequence != null ? charSequence.toString() : "";
                System.out.println("Search query: " + query);

                //Check user query
                if(query.isEmpty()) {
                    //If empty, show the exact same list
                    mPoliciesFiltered = mPolicies;
                } else {
                    //Create a new ArrayList to add filtered policies
                    ArrayList<Policy> filteredList = new ArrayList<>();
                    for(Policy policy : mPolicies) {
                        String policyTitle = policy.getTitle().toLowerCase();
                        String policyPurpose = policy.getPurpose().toLowerCase();
                        if(policyTitle.contains(query.toLowerCase()) || policyPurpose.contains(query.toLowerCase())) {
                            filteredList.add(policy);
                            System.out.println("Added to mPoliciesFiltered");
                        }
                    }
                    mPoliciesFiltered = filteredList;
                    System.out.println("PolicyFiltered size: " + mPoliciesFiltered.size());
                    System.out.println("mPolicies size: " + mPolicies.size());
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
        private TextView mTitle, mDescription;

        public MyViewHolder(@NonNull View itemView, PolicyRecyclerViewInterface mInterface) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.tvTitle);
            mDescription = itemView.findViewById(R.id.tvDescription);
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
                        return o1.getTitle().toLowerCase().trim().compareTo(o2.getTitle().toLowerCase().trim());
                    } else if (sortMethod == SORT_METHOD_NAME_REVERSE) {
                        return o2.getTitle().toLowerCase().trim().compareTo(o1.getTitle().toLowerCase().trim());
                    }
                    // By default sort the list in ascending order
                    return o1.getTitle().toLowerCase().trim().compareTo(o2.getTitle().toLowerCase().trim());
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

