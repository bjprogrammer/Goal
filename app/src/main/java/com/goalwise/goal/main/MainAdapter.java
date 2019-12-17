package com.goalwise.goal.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.goalwise.goal.R;
import com.goalwise.goal.model.ListResponse;
import androidx.databinding.library.baseAdapters.BR;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ListResponse.Data> data;
    private final int ITEM = 0;

    private LayoutInflater inflater;

    MainAdapter() {
        data = new ArrayList<>();
    }

    public List<ListResponse.Data> getData() {
        return data;
    }

    public void setData(List<ListResponse.Data> dataList) {
        this.data = dataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        RecyclerView.ViewHolder viewHolder = null;
        inflater = LayoutInflater.from(viewGroup.getContext());

        switch (i) {
            case ITEM:
                viewHolder = new ViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_data, viewGroup, false));
                break;
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int i) {

        switch (getItemViewType(i)) {
            case ITEM:
                MainAdapter.ViewHolder viewHolder = (MainAdapter.ViewHolder) holder;
                viewHolder.bind(data.get(i));
                break;

        }
    }


    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    @Override
    public int getItemViewType(int position) {
        return ITEM;
    }


    private class ViewHolder extends RecyclerView.ViewHolder{
        private ViewDataBinding binding;

        private ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }


        private void bind(Object obj) {
            binding.getRoot().findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.getRoot().findViewById(R.id.add).setVisibility(View.GONE);
                    binding.getRoot().findViewById(R.id.add_fund).setVisibility(View.VISIBLE);
                    binding.getRoot().findViewById(R.id.edit_fund).setVisibility(View.VISIBLE);
                }
            });


            binding.getRoot().findViewById(R.id.add_fund).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText editFunds=binding.getRoot().findViewById(R.id.edit_fund);
                    if(!editFunds.getText().toString().isEmpty()){
                        if(Integer.parseInt(editFunds.getText().toString())< ((ListResponse.Data)obj).getMinimumAmount())
                        {
                            editFunds.setError("Value less than Rs "+ ((ListResponse.Data)obj).getMinimumAmount() + " not allowed");
                        }else if( ((ListResponse.Data)obj).getMinimumAmount() % Integer.parseInt(editFunds.getText().toString()) !=0){
                            editFunds.setError("Only Value in multiple of Rs"+ ((ListResponse.Data)obj).getMultiple() + " is allowed");
                        }else {
//                            listener.onClick(((ListResponse.Data)obj).getMinimumAmount(), ((ListResponse.Data)obj).getMultiple(), Integer.parseInt(editFunds.getText().toString()));

                            AlertDialog.Builder alert = new AlertDialog.Builder(binding.getRoot().getContext());
                            View dialogView = inflater.inflate(R.layout.alert_dialog, null);
                            alert.setView(dialogView);

                            TextView submit = dialogView.findViewById(R.id.ok);
                            alert.setCancelable(false);
                            AlertDialog dialog = alert.create();
                            dialog.show();

                            submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    binding.getRoot().findViewById(R.id.add).setVisibility(View.VISIBLE);
                                    binding.getRoot().findViewById(R.id.add_fund).setVisibility(View.GONE);
                                    binding.getRoot().findViewById(R.id.edit_fund).setVisibility(View.GONE);
                                    dialog.dismiss();

                                    InputMethodManager imm = (InputMethodManager) binding.getRoot().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(editFunds.getWindowToken(), 0);
                                }
                            });
                        }
                    }else
                    {
                        editFunds.setError("Cannot be empty");
                    }
                }
            });

            binding.setVariable(BR.fund,obj);
            binding.executePendingBindings();
        }
    }

    private void add(ListResponse.Data image) {
        data.add(image);
        notifyItemInserted(data.size() - 1);
    }



    void addAll(ListResponse mcList) {
        for (ListResponse.Data response: mcList.getData()) {
            add(response);
        }
    }

    private void remove(ListResponse.Data fund) {
        int position = data.indexOf(fund);
        if (position > -1) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }



    private ListResponse.Data getItem(int position) {
        return data.get(position);
    }
}
