package com.example.restapiretrofitdemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<User> users;

    public UserAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_user,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User user = users.get(position);

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


        /*}
        else if(listStatus == "Synced"){
            holder.userId.setText(String.valueOf(user.getUserId()));
            holder.name.setText(String.valueOf(user.getName()));
            holder.title.setText(String.valueOf(user.getTitle()));
            holder.body.setText(String.valueOf(user.getBody()));
            holder.listStatusTV.setText("Synced");
            holder.listStatusIV.setImageResource(R.drawable.ic_check_circle_black_24dp);
        }*/
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
