package com.example.restapiretrofitdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<Data> dataList;
    private Context context;
    private DatabaseHelper databaseHelper = new DatabaseHelper(context);

    public DataAdapter(ArrayList<Data> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_data_itemview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Data currentData = dataList.get(position);

        holder.data1.setText(currentData.getUserId());
        holder.data2.setText(currentData.getName());
        holder.data4.setText(currentData.getTitle());
        holder.data3.setText(currentData.getBody());
        holder.data5.setText(currentData.getStatus());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseHelper.deleteSingleData(currentData.getUserId());
                        dataList.remove(position);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView data1,data2,data3,data4,data5;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            data1 = itemView.findViewById(R.id.data1TV);
            data2 = itemView.findViewById(R.id.data2TV);
            data3 = itemView.findViewById(R.id.data3TV);
            data4 = itemView.findViewById(R.id.data4TV);
            data5 = itemView.findViewById(R.id.data5TV);
        }
    }
}
