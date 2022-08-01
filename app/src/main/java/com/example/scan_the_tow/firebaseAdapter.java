package com.example.scan_the_tow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class firebaseAdapter extends FirebaseRecyclerAdapter<Towing_Data,firebaseAdapter.myviewholder> {

    public firebaseAdapter(@NonNull FirebaseRecyclerOptions<Towing_Data> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull Towing_Data model) {
        holder.noplate.setText("Number Plate : "+model.getNoplate());
        holder.area.setText("Area : "+model.getArea());
        holder.date.setText("Date : "+model.getDate());
        holder.time.setText("Time : "+model.getTime());
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewlayout,parent,false);
        return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder{

        TextView noplate,area,date,time;
        public myviewholder(@NonNull View itemView) {
            super(itemView);

            noplate = itemView.findViewById(R.id.recyclenoplate);
            date = itemView.findViewById(R.id.recycledate);
            area = itemView.findViewById(R.id.recyclearea);
            time = itemView.findViewById(R.id.recycletime);
        }
    }
}