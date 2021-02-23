package com.example.customcalendarevent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URI;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    Context context;
    ArrayList<Event> list;
    DBOpenHelper dbOpenHelper;

    public RecyclerAdapter(Context context, ArrayList<Event> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_row_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final Event events = list.get(position);
        holder.Event.setText(events.getEVENT());
        holder.DateTxt.setText(events.getDATE());
        holder.Time.setText(events.getTIME());
        //holder.imageView.setImageURI(Uri.parse(events.getIMAGE()));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCalendarEvent(events.getEVENT(),events.getDATE(),events.getTIME());
                list.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView DateTxt, Event, Time;
        Button delete;
        ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Event = itemView.findViewById(R.id.eventname);
            DateTxt = itemView.findViewById(R.id.eventdate);
            Time = itemView.findViewById(R.id.eventtime);
            imageView=itemView.findViewById(R.id.picture);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    private void deleteCalendarEvent(String event, String date, String time){
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.deleteEvent(event,date,time,database);
        dbOpenHelper.close();
    }
}
