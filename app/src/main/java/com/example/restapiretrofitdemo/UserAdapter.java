package com.example.restapiretrofitdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.StringRequest;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<User> users;
    private Context context;
    private DatabaseHelper databaseHelper = new DatabaseHelper(context);

    public UserAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_user,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = users.get(position);

        holder.userId.setText(String.valueOf(user.getUserId()));
        holder.name.setText(String.valueOf(user.getName()));
        holder.title.setText(user.getTitle());
        holder.body.setText(user.getBody());
        holder.listStatusTV.setText(user.getStatus());

        String listStatus = user.getStatus();

        if(listStatus.equals("Synced")){
            holder.listStatusIV.setImageResource(R.drawable.ic_check_circle_black_24dp);
            holder.listStatusIV.setColorFilter(R.color.colorPrimary);
        }
        else {
            holder.listStatusIV.setImageResource(R.drawable.ic_block_black_24dp);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false);
                builder.setTitle("Update");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userId = user.getUserId();
                        String name = user.getName();
                        String title = user.getTitle();
                        String body = user.getBody();
                        String status = user.getStatus();

                        Intent intent = new Intent(context,UpdateDataActivity.class);
                        intent.putExtra("userId",userId);
                        intent.putExtra("name",name);
                        intent.putExtra("title",title);
                        intent.putExtra("body",body);
                        intent.putExtra("status",status);
                        context.startActivity(intent);
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
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userId, name, title, body, listStatusTV;
        private ImageView listStatusIV;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userId = itemView.findViewById(R.id.userIdTV);
            name = itemView.findViewById(R.id.nameTV);
            title = itemView.findViewById(R.id.titleTV);
            body = itemView.findViewById(R.id.bodyTV);
            listStatusTV = itemView.findViewById(R.id.listStatusTV);
            listStatusIV = itemView.findViewById(R.id.listStatusIV);
        }
    }
}
