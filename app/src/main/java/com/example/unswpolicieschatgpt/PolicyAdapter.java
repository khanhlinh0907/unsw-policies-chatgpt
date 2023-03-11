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
import java.util.List;

public class PolicyAdapter extends RecyclerView.Adapter<PolicyAdapter.MyViewHolder> implements Filterable {
    private List<Policy> mPolicies, mPoliciesFiltered;
    private PolicyRecyclerViewInterface mInterface;

    public PolicyAdapter(List<Policy> policies, PolicyRecyclerViewInterface policyInterface) {
        mPolicies = policies;
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
        return mPoliciesFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();
                if(query.isEmpty()) {
                    mPoliciesFiltered = mPolicies;
                } else {
                    ArrayList<Policy> filteredList = new ArrayList<>();
                    for(Policy policy : mPolicies) {
                        if(policy.getName().contains(query)) {
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
}

